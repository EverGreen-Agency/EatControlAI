package com.eatcontrolai.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eatcontrolai.AppContainer
import com.eatcontrolai.benchmark.ProviderBenchmark
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity
import com.eatcontrolai.core.model.Decision
import com.eatcontrolai.core.model.DecisionReason
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.LabelClaim
import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.NutrientAmount
import com.eatcontrolai.core.model.NutrientProgress
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.RestrictionSeverity
import com.eatcontrolai.core.model.UncertaintyPolicy
import com.eatcontrolai.domain.nutrition.NutritionCalculator
import com.eatcontrolai.domain.plate.PlateFoodClass
import com.eatcontrolai.domain.voice.VoiceIntentParser
import com.eatcontrolai.glasses.CaptureSource
import com.eatcontrolai.glasses.GlassesStatus
import com.eatcontrolai.glasses.MockScene
import com.eatcontrolai.glasses.MockScenes
import com.eatcontrolai.glasses.SceneKind
import com.eatcontrolai.orchestration.AnalysisTrack
import com.eatcontrolai.orchestration.InteractionResult
import java.util.UUID
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Modos de captura disponíveis; cada modo só é habilitado quando possui pipeline ponta a ponta. */
enum class AnalyzeMode(
    val label: String,
    val glyph: String,
    val prompt: String,
    val track: AnalysisTrack?,
    val sceneKind: SceneKind?,
    /** `true` no modo em que a cascata decide a trilha sozinha. */
    val auto: Boolean = false
) {
    /**
     * A cascata decide: barcode primeiro (barato), OCR só se preciso, e o roteador escolhe.
     * É o modo que a palestra do Ideathon defende — um gatilho, ferramentas roteadas por custo.
     */
    AUTO("Automático", "✦", "\"Posso comer isso?\"", AnalysisTrack.LABEL, null, auto = true),
    LABEL("Rótulo", "Aa", "\"Tem algo aqui que conflita com meu plano?\"", AnalysisTrack.LABEL, SceneKind.LABEL),
    BARCODE("Código de barras", "▦", "\"Posso incluir esse produto?\"", AnalysisTrack.BARCODE, SceneKind.BARCODE),
    PLATE("Prato", "◉", "\"Como está esse prato para mim?\"", AnalysisTrack.PLATE, SceneKind.PLATE),
    MENU("Cardápio", "≡", "\"Qual opção parece mais alinhada comigo?\"", AnalysisTrack.MENU, SceneKind.MENU);

    val ready: Boolean get() = track != null
}

data class DatUiState(
    val launchingRegistration: Boolean = false,
    val connecting: Boolean = false,
    val registrationError: String? = null,
    val connectionError: String? = null
)

data class VoiceState(
    val listening: Boolean = false,
    val transcript: String = "",
    val onDevice: Boolean = false,
    val available: Boolean = true,
    val permissionDenied: Boolean = false,
    /** `true` quando a fala foi captada pelos microfones dos óculos, via HFP. */
    val capturedOnGlasses: Boolean = false
)

data class LabState(
    val running: Boolean = false,
    val results: List<ProviderBenchmark.Result> = emptyList(),
    val error: String? = null
)

data class AnalyzeState(
    val mode: AnalyzeMode = AnalyzeMode.AUTO,
    val source: CaptureSource = CaptureSource.MOCK_GLASSES,
    val selectedSceneId: String = MockScenes.default.id,
    val isAnalyzing: Boolean = false,
    val result: InteractionResult? = null,
    val resultRecordId: String? = null,
    val showResult: Boolean = false,
    val error: String? = null,
    /** Porções confirmadas pelo usuário para a refeição atual. */
    val portions: Double = 1.0,
    /** `true` depois de o consumo desta refeição entrar no total do dia. */
    val consumptionLogged: Boolean = false
) {
    /** Consumo desta refeição. Vazio quando a tabela não permite conta honesta. */
    val consumedNutrients: List<NutrientAmount>
        get() = result?.let { NutritionCalculator.consumed(it.nutrition, portions) } ?: emptyList()

    val scenes: List<MockScene>
        get() = when {
            mode.auto -> MockScenes.automatic
            mode.sceneKind != null -> MockScenes.forKind(mode.sceneKind)
            else -> emptyList()
        }

    val selectedScene: MockScene?
        get() = scenes.firstOrNull { it.id == selectedSceneId } ?: scenes.firstOrNull()

    /** O seletor de cena só faz sentido quando a fonte é simulada. */
    val showsSceneSelector: Boolean get() = source == CaptureSource.MOCK_GLASSES && scenes.isNotEmpty()
}

class EatControlViewModel(private val container: AppContainer) : ViewModel() {

    val profile = container.profiles.profile
    val profileState = container.profiles.state
    val history = container.history.records
    val datRegistrationState = container.datGlasses.registrationState

    /**
     * Total do dia versus metas configuradas.
     *
     * Deriva do histórico local: só entra o que o usuário confirmou como consumido. Sem meta
     * configurada, cada item aparece como consumo puro, sem julgamento.
     */
    val dailyProgress: StateFlow<List<NutrientProgress>> =
        combine(container.history.records, container.profiles.profile) { records, profile ->
            val startOfDay = startOfToday()
            val consumed = records
                .filter { it.timestampMillis >= startOfDay }
                .flatMap { it.consumedNutrients }
            NutritionCalculator.progress(NutritionCalculator.total(consumed), profile.macroGoals)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _analyze = MutableStateFlow(AnalyzeState())
    val analyze: StateFlow<AnalyzeState> = _analyze.asStateFlow()

    private val _glasses = MutableStateFlow(container.glasses.status)
    val glasses: StateFlow<GlassesStatus> = _glasses.asStateFlow()

    private val _datUi = MutableStateFlow(DatUiState())
    val datUi: StateFlow<DatUiState> = _datUi.asStateFlow()

    private val _voice = MutableStateFlow(VoiceState(available = container.stt.isAvailable()))
    val voice: StateFlow<VoiceState> = _voice.asStateFlow()

    private val _lab = MutableStateFlow(LabState())
    val lab: StateFlow<LabState> = _lab.asStateFlow()

    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast.asStateFlow()

    init {
        viewModelScope.launch {
            container.glasses.connect()
            refreshGlasses()
        }
    }

    fun showToast(message: String) {
        _toast.value = message
    }

    fun clearToast() {
        _toast.value = null
    }

    private fun refreshGlasses() {
        _glasses.value = container.glasses.status
    }

    // ---------------------------------------------------------------- analisar

    fun selectMode(mode: AnalyzeMode) {
        _analyze.update {
            val scenes = when {
                mode.auto -> MockScenes.automatic
                mode.sceneKind != null -> MockScenes.forKind(mode.sceneKind)
                else -> emptyList()
            }
            it.copy(
                mode = mode,
                selectedSceneId = scenes.firstOrNull()?.id ?: it.selectedSceneId,
                result = null,
                resultRecordId = null,
                showResult = false,
                error = null
            )
        }
        _analyze.value.selectedScene?.let { container.mockGlasses.selectScene(it.id) }
        if (!mode.ready) {
            showToast("${mode.label} ainda não tem trilha implementada. Rótulo e código de barras estão prontos.")
        }
    }

    fun selectScene(sceneId: String) {
        container.mockGlasses.selectScene(sceneId)
        _analyze.update {
            it.copy(
                selectedSceneId = sceneId,
                result = null,
                resultRecordId = null,
                showResult = false,
                error = null
            )
        }
    }

    fun selectSource(source: CaptureSource) {
        if (_analyze.value.isAnalyzing) {
            showToast("Aguarde a análise terminar antes de trocar a câmera.")
            return
        }

        val previous = container.glasses.active
        if (previous == source) return

        container.glasses.select(source)
        _analyze.update {
            it.copy(
                source = source,
                result = null,
                resultRecordId = null,
                showResult = false,
                error = null
            )
        }
        refreshGlasses()

        viewModelScope.launch {
            runCatching { container.glasses.gatewayFor(previous).disconnect() }
            if (source != CaptureSource.DAT_GLASSES) {
                runCatching { container.glasses.gatewayFor(source).connect() }
            }
            refreshGlasses()
        }
    }

    /**
     * Abre o fluxo de autorização do DAT no app Meta AI.
     *
     * Após o retorno, [datRegistrationState] muda para REGISTERED e a tela solicita a permissão de
     * câmera do toolkit. A sessão de câmera só será aberta durante a análise.
     */
    fun registerDatGlasses(activity: Activity) {
        if (_datUi.value.launchingRegistration) return
        _datUi.update {
            it.copy(
                launchingRegistration = true,
                registrationError = null,
                connectionError = null
            )
        }

        val result = container.datGlasses.initialize().fold(
            onSuccess = { container.datGlasses.startRegistration(activity) },
            onFailure = { Result.failure(it) }
        )
        result.onSuccess {
            _datUi.update { it.copy(launchingRegistration = false) }
            showToast("Meta AI aberto. Conclua a autorização e volte ao Eat Control.")
        }.onFailure { throwable ->
            val message = throwable.message ?: "O fluxo de autorização não foi aberto."
            _datUi.update {
                it.copy(launchingRegistration = false, registrationError = message)
            }
            showToast("Não consegui iniciar o DAT: $message")
        }
    }

    fun onDatCameraPermissionResult(granted: Boolean) {
        if (granted) {
            _datUi.update { it.copy(registrationError = null, connectionError = null) }
            showToast("Câmera dos óculos autorizada. A captura será aberta sob demanda.")
        } else {
            _datUi.update {
                it.copy(connectionError = "A permissão de câmera DAT foi negada no Meta AI.")
            }
            showToast("Câmera dos óculos não autorizada. Voltando para a fonte simulada.")
            selectSource(CaptureSource.MOCK_GLASSES)
        }
    }

    /** Chamado pela tela quando a câmera do celular liga ou desliga. */
    fun onCameraBindingChanged() = refreshGlasses()

    fun analyze() {
        if (_analyze.value.isAnalyzing) return
        viewModelScope.launch {
            // A mesma rota HFP serve ao feedback e ao TTS da resposta. Só é liberada quando o
            // orquestrador termina de falar.
            container.audioRouter.routeToGlasses()
            try {
                analyzeCurrent()
            } finally {
                container.audioRouter.release()
            }
        }
    }

    /** Executa uma análise e mantém a sessão DAT aberta somente durante esta interação. */
    private suspend fun analyzeCurrent() {
        val state = _analyze.value
        val track = state.mode.track ?: return
        if (state.isAnalyzing) return

        _analyze.update { it.copy(isAnalyzing = true, error = null) }
        val usesDat = state.source == CaptureSource.DAT_GLASSES

        try {
            if (usesDat) {
                _datUi.update { it.copy(connecting = true, connectionError = null) }
                container.datGlasses.connect()
                _datUi.update { it.copy(connecting = false) }
                refreshGlasses()
            }

            val result = if (state.mode.auto) {
                container.orchestrator.analyzeAuto(profile.value)
            } else {
                container.orchestrator.analyze(track, profile.value)
            }
            val record = result
                .takeUnless { it.track == AnalysisTrack.MENU || it.track == AnalysisTrack.PLATE }
                ?.toRecord()
            if (record != null) container.history.add(record)
            _analyze.update {
                it.copy(
                    isAnalyzing = false,
                    result = result,
                    resultRecordId = record?.id,
                    showResult = true,
                    portions = 1.0,
                    consumptionLogged = false
                )
            }
        } catch (throwable: Throwable) {
            val baseMessage = throwable.message ?: "Falha ao analisar."
            if (usesDat) {
                _datUi.update { it.copy(connecting = false, connectionError = baseMessage) }
                container.glasses.select(CaptureSource.MOCK_GLASSES)
                _analyze.update {
                    it.copy(
                        source = CaptureSource.MOCK_GLASSES,
                        isAnalyzing = false,
                        error = "$baseMessage Fonte alterada para os óculos simulados."
                    )
                }
            } else {
                _analyze.update { it.copy(isAnalyzing = false, error = baseMessage) }
            }
        } finally {
            if (usesDat) {
                _datUi.update { it.copy(connecting = false) }
                runCatching { container.datGlasses.disconnect() }
                refreshGlasses()
            }
        }
    }

    /** Fecha a visualização e remove da memória o único objeto que mantém os bytes da foto. */
    fun dismissResult() = _analyze.update {
        it.copy(result = null, resultRecordId = null, showResult = false)
    }

    // ---------------------------------------------------- cardápio e prato

    fun selectMenuOption(optionId: String) {
        val current = _analyze.value.result ?: return
        val menu = current.menuAnalysis ?: return
        if (menu.options.none { it.id == optionId }) return

        val pendingDecision = Decision(
            state = DecisionState.NEEDS_CONFIRMATION,
            shortMessage = "Revise a opção e confirme antes de registrar.",
            evidence = current.decision.evidence.filterNot {
                it.type == EvidenceType.USER_CONFIRMATION
            },
            reasons = listOf(
                DecisionReason(
                    "O cardápio informa texto, não receita completa, porção ou macros.",
                    EvidenceType.OCR_TEXT
                )
            )
        )
        _analyze.update {
            it.copy(
                result = current.copy(
                    decision = pendingDecision,
                    menuAnalysis = menu.copy(
                        selectedOptionId = optionId,
                        registered = false
                    )
                )
            )
        }
    }

    fun registerMenuSelection() {
        val current = _analyze.value.result ?: return
        val menu = current.menuAnalysis ?: return
        val option = menu.selectedOption ?: run {
            showToast("Selecione uma opção do cardápio antes de registrar.")
            return
        }
        val confirmation = Evidence(
            type = EvidenceType.USER_CONFIRMATION,
            value = "opção confirmada: ${option.name}",
            source = "seleção do usuário no cardápio"
        )
        val updated = current.copy(
            decision = Decision(
                state = DecisionState.INSUFFICIENT_INFORMATION,
                shortMessage = "Opção registrada. O cardápio não informa macros completos.",
                evidence = current.decision.evidence
                    .filterNot { it.type == EvidenceType.USER_CONFIRMATION } + confirmation,
                reasons = listOf(
                    DecisionReason(
                        "Você confirmou a opção observada no cardápio.",
                        EvidenceType.USER_CONFIRMATION
                    ),
                    DecisionReason(
                        "Sem receita e quantidade declaradas, macros permanecem desconhecidos.",
                        EvidenceType.OCR_TEXT
                    )
                )
            ),
            menuAnalysis = menu.copy(registered = true)
        )
        persistInteractiveResult(updated)
        showToast("Opção registrada sem inventar composição nutricional.")
    }

    fun togglePlateComponent(component: PlateFoodClass) {
        val current = _analyze.value.result ?: return
        val plate = current.plateAnalysis ?: return
        val selected = if (component in plate.selectedComponents) {
            plate.selectedComponents - component
        } else {
            plate.selectedComponents + component
        }
        val baseEvidence = current.decision.evidence.filterNot {
            it.type == EvidenceType.USER_CONFIRMATION
        }
        _analyze.update {
            it.copy(
                result = current.copy(
                    decision = Decision(
                        state = if (selected.isEmpty()) {
                            DecisionState.INSUFFICIENT_INFORMATION
                        } else {
                            DecisionState.NEEDS_CONFIRMATION
                        },
                        shortMessage = if (selected.isEmpty()) {
                            "Selecione ao menos um componente que você reconhece."
                        } else {
                            "Confirme os componentes; a foto não mede macros."
                        },
                        evidence = baseEvidence,
                        reasons = listOf(
                            DecisionReason(
                                "A seleção ainda não foi confirmada pelo usuário.",
                                EvidenceType.VISUAL_INFERENCE
                            )
                        )
                    ),
                    plateAnalysis = plate.copy(
                        selectedComponents = selected,
                        registered = false
                    )
                )
            )
        }
    }

    fun registerPlateSelection() {
        val current = _analyze.value.result ?: return
        val plate = current.plateAnalysis ?: return
        if (plate.selectedComponents.isEmpty()) {
            showToast("Selecione ao menos um componente do prato.")
            return
        }
        val labels = plate.selectedComponents
            .sortedBy(PlateFoodClass::ordinal)
            .joinToString { it.displayName }
        val confirmation = Evidence(
            type = EvidenceType.USER_CONFIRMATION,
            value = "componentes confirmados: $labels",
            source = "confirmação assistida do usuário"
        )
        val updated = current.copy(
            decision = Decision(
                state = DecisionState.INSUFFICIENT_INFORMATION,
                shortMessage = "Componentes registrados. Quantidade e macros não foram medidos.",
                evidence = current.decision.evidence
                    .filterNot { it.type == EvidenceType.USER_CONFIRMATION } + confirmation,
                reasons = listOf(
                    DecisionReason(
                        "Você confirmou apenas os componentes visíveis selecionados.",
                        EvidenceType.USER_CONFIRMATION
                    ),
                    DecisionReason(
                        "Ingrediente oculto, receita, quantidade e macros continuam desconhecidos.",
                        EvidenceType.VISUAL_INFERENCE
                    )
                )
            ),
            plateAnalysis = plate.copy(registered = true)
        )
        persistInteractiveResult(updated)
        showToast("Prato registrado como identificação assistida, sem macro automático.")
    }

    private fun persistInteractiveResult(result: InteractionResult) {
        val existingId = _analyze.value.resultRecordId
        val existing = existingId?.let { id ->
            container.history.records.value.firstOrNull { it.id == id }
        }
        val generated = result.toRecord()
        val record = if (existing == null) {
            generated
        } else {
            generated.copy(
                id = existing.id,
                timestampMillis = existing.timestampMillis,
                consumedNutrients = existing.consumedNutrients
            )
        }
        if (existing == null) container.history.add(record) else container.history.replace(record)
        _analyze.update { it.copy(result = result, resultRecordId = record.id) }
    }

    // -------------------------------------------------------------- nutrição

    fun setPortions(portions: Double) {
        val state = _analyze.value
        val recordId = state.resultRecordId
        if (recordId != null) {
            container.history.records.value
                .firstOrNull { it.id == recordId && it.consumedNutrients.isNotEmpty() }
                ?.let { current ->
                    container.history.replace(
                        com.eatcontrolai.domain.nutrition.MealRecordUpdater.withConsumption(
                            current,
                            emptyList()
                        )
                    )
                }
        }
        _analyze.update { it.copy(portions = portions, consumptionLogged = false) }
    }

    /**
     * Lança o consumo desta refeição no total do dia.
     *
     * Exige confirmação explícita: sem o usuário dizer quantas porções comeu, não existe consumo.
     * Estimar a quantidade a partir da foto corromperia o total diário com aparência de precisão.
     */
    fun registerConsumption() {
        val state = _analyze.value
        val recordId = state.resultRecordId ?: return

        val consumed = state.consumedNutrients
        if (consumed.isEmpty()) {
            showToast("A tabela lida não permite calcular o consumo com segurança.")
            return
        }

        val current = container.history.records.value.firstOrNull { it.id == recordId } ?: return
        container.history.replace(
            com.eatcontrolai.domain.nutrition.MealRecordUpdater.withConsumption(current, consumed)
        )
        _analyze.update { it.copy(consumptionLogged = true) }
        showToast("Consumo registrado no total de hoje.")
    }

    private fun startOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    /**
     * Fecha o loop de confirmação do `contexto-gpt.md` §56 (Cenário 3).
     *
     * A resposta do usuário entra como [EvidenceType.USER_CONFIRMATION] — rank 4, acima de OCR bruto
     * e de inferência visual — e a decisão é **recalculada pelo mesmo motor**. Não é um texto trocado
     * na tela: a evidência nova realmente muda o resultado.
     */
    fun confirmIngredient(allergen: Allergen, present: Boolean) {
        val current = _analyze.value.result ?: return
        val recordId = _analyze.value.resultRecordId

        val confirmation = Evidence(
            type = EvidenceType.USER_CONFIRMATION,
            value = if (present) "usuário confirmou presença de ${allergen.displayName}"
            else "usuário confirmou ausência de ${allergen.displayName}",
            source = "confirmação do usuário",
            claims = listOf(
                LabelClaim(
                    allergen = allergen,
                    polarity = if (present) ClaimPolarity.CONTAINS else ClaimPolarity.FREE_OF,
                    sourceText = "confirmado por você"
                )
            )
        )

        val evidence = current.decision.evidence + confirmation
        val decision = container.decisionEngine.decide(profile.value, evidence)
        val updated = current.copy(decision = decision)

        _analyze.update { it.copy(result = updated) }
        if (recordId != null) {
            container.history.records.value.firstOrNull { it.id == recordId }?.let { record ->
                container.history.replace(
                    com.eatcontrolai.domain.nutrition.MealRecordUpdater.withDecision(
                        current = record,
                        decision = decision,
                        recognizedText = updated.recognizedText
                    )
                )
            }
        }

        viewModelScope.launch {
            container.audioRouter.routeToGlasses()
            try {
                container.glasses.playSpeech(decision.shortMessage)
            } finally {
                container.audioRouter.release()
            }
        }
    }

    // --------------------------------------------------------------------- voz

    fun onAudioPermissionResult(granted: Boolean) {
        _voice.update { it.copy(permissionDenied = !granted) }
        if (granted) listen()
    }

    /**
     * Escuta a pergunta, interpreta o comando e dispara a análise correspondente.
     *
     * A interpretação é determinística ([VoiceIntentParser]) — um LLM aqui só acrescentaria latência
     * e dependência de rede no caminho crítico.
     */
    fun listen() {
        if (_voice.value.listening) return
        val provider = container.models.current().stt
        if (provider == null || !provider.isAvailable()) {
            _voice.update { it.copy(available = false, listening = false) }
            showToast("Voz offline indisponível. Use o botão Analisar ou instale o pacote pt-BR.")
            return
        }

        _voice.update { it.copy(listening = true, transcript = "", available = true) }

        // HFP sobe antes do STT e permanece até a análise e o TTS terminarem.
        val onGlasses = container.audioRouter.routeToGlasses()

        viewModelScope.launch {
            try {
                val result = provider.transcribe()
                val intent = VoiceIntentParser.parse(result.text)
                _voice.update {
                    it.copy(
                        listening = false,
                        transcript = result.text,
                        onDevice = container.stt.usingOnDevice,
                        capturedOnGlasses = onGlasses
                    )
                }

                if (!intent.recognized) {
                    showToast("Não entendi. Tente de novo mais perto do microfone.")
                    return@launch
                }

                // Voz apenas dispara a interação. A cascata automática decide entre barcode e OCR;
                // seleções manuais permanecem exclusivas do diagnóstico em builds debug.
                if (_analyze.value.mode != AnalyzeMode.AUTO) selectMode(AnalyzeMode.AUTO)
                analyzeCurrent()
            } catch (throwable: Throwable) {
                _voice.update { it.copy(listening = false) }
                showToast("Reconhecimento de voz offline falhou: ${throwable.message}")
            } finally {
                container.audioRouter.release()
            }
        }
    }

    // ------------------------------------------------------------- meu plano

    fun saveProfile(
        displayName: String,
        usesGlp1: Boolean,
        macroGoals: com.eatcontrolai.core.model.MacroGoals
    ) {
        val normalizedName = displayName.trim()
        if (normalizedName.isEmpty()) {
            showToast("Informe como você quer ser chamado.")
            return
        }

        val normalizedGoals = if (macroGoals.isConfigured) {
            macroGoals
        } else {
            com.eatcontrolai.core.model.MacroGoals()
        }
        container.profiles.update { current ->
            current.copy(
                id = current.id.ifBlank { UUID.randomUUID().toString() },
                displayName = normalizedName,
                usesGlp1 = usesGlp1,
                macroGoals = normalizedGoals
            )
        }
        clearAnalysisResult()
        showToast("Perfil e metas salvos neste aparelho.")
    }

    fun toggleRestriction(allergen: Allergen) {
        container.profiles.update { current ->
            val existing = current.restrictionFor(allergen)
            val next = if (existing != null) current.restrictions - existing
            else current.restrictions + Restriction(allergen)
            current.copy(restrictions = next)
        }
        clearAnalysisResult()
    }

    fun cycleSeverity(allergen: Allergen) {
        container.profiles.update { current ->
            val existing = current.restrictionFor(allergen) ?: return@update current
            val order = RestrictionSeverity.entries
            val next = order[(order.indexOf(existing.severity) + 1) % order.size]
            current.copy(
                restrictions = current.restrictions - existing + existing.copy(severity = next)
            )
        }
        clearAnalysisResult()
    }

    fun setUncertaintyPolicy(allergen: Allergen, policy: UncertaintyPolicy) {
        container.profiles.update { current ->
            val existing = current.restrictionFor(allergen) ?: return@update current
            current.copy(
                restrictions = current.restrictions - existing + existing.copy(uncertaintyPolicy = policy)
            )
        }
        clearAnalysisResult()
    }

    // -------------------------------------------------------------- privacidade

    private fun clearAnalysisResult() = _analyze.update {
        it.copy(result = null, resultRecordId = null, showResult = false, error = null)
    }

    fun clearHistory() {
        container.history.clear()
        showToast("Histórico apagado do aparelho.")
    }

    fun resetProfile() {
        container.profiles.reset()
        clearAnalysisResult()
        showToast("Perfil local removido. Configure um novo perfil para continuar.")
    }

    // ------------------------------------------------------------------ óculos

    fun testAudio() {
        viewModelScope.launch {
            container.audioRouter.routeToGlasses()
            try {
                val meta = container.glasses.playSpeech("Eat Control pronto.")
                if (meta.providerId.endsWith("_unavailable")) {
                    showToast("Voz pt-BR offline indisponível neste aparelho.")
                } else {
                    showToast("Áudio offline iniciado em ${meta.latencyMs} ms.")
                }
            } catch (throwable: Throwable) {
                showToast("Teste de áudio falhou: ${throwable.message}")
            } finally {
                container.audioRouter.release()
            }
        }
    }

    fun testCamera() {
        viewModelScope.launch {
            val usesDat = container.glasses.active == CaptureSource.DAT_GLASSES
            try {
                if (usesDat) {
                    _datUi.update { it.copy(connecting = true, connectionError = null) }
                    container.datGlasses.connect()
                    _datUi.update { it.copy(connecting = false) }
                    refreshGlasses()
                }
                val frame = container.glasses.capturePhoto()
                showToast("Frame capturado: ${frame.size / 1024} KB.")
            } catch (throwable: Throwable) {
                val message = throwable.message ?: "Falha desconhecida."
                if (usesDat) {
                    _datUi.update { it.copy(connecting = false, connectionError = message) }
                }
                showToast("Captura falhou: $message")
            } finally {
                if (usesDat) {
                    _datUi.update { it.copy(connecting = false) }
                    runCatching { container.datGlasses.disconnect() }
                    refreshGlasses()
                }
            }
        }
    }

    // ------------------------------------------------------------- laboratório

    /**
     * Roda o mesmo harness do benchmark de terminal, dentro do app.
     *
     * Números iguais aos de `./scripts/benchmark.sh` porque é literalmente o mesmo código
     * ([ProviderBenchmark]).
     */
    fun runBenchmark(repetitions: Int = 3) {
        if (_lab.value.running) return
        _lab.update { it.copy(running = true, results = emptyList(), error = null) }

        viewModelScope.launch {
            runCatching {
                ProviderBenchmark(container.decisionEngine).run(
                    provider = container.models.current().ocr,
                    profile = profile.value,
                    repetitions = repetitions
                )
            }.onSuccess { result ->
                _lab.update { it.copy(running = false, results = listOf(result)) }
            }.onFailure { throwable ->
                _lab.update { it.copy(running = false, error = throwable.message) }
            }
        }
    }

    private fun InteractionResult.toRecord(): MealRecord {
        val menuItem = menuAnalysis?.selectedOption?.takeIf { menuAnalysis.registered }
        val plateItems = plateAnalysis
            ?.takeIf { it.registered }
            ?.selectedComponents
            .orEmpty()
            .sortedBy(PlateFoodClass::ordinal)
            .map(PlateFoodClass::displayName)
        val confirmedItems = menuItem?.let { listOf(it.name) } ?: plateItems
        val title = productName
            ?: menuItem?.name
            ?: plateItems.takeIf { it.isNotEmpty() }
                ?.joinToString(prefix = "Prato · ", limit = 3, truncated = "…")
            ?: _analyze.value.selectedScene?.title
            ?: when (track) {
                AnalysisTrack.LABEL -> "Rótulo analisado"
                AnalysisTrack.BARCODE -> "Produto escaneado"
                AnalysisTrack.MENU -> "Opção de cardápio"
                AnalysisTrack.PLATE -> "Prato assistido"
            }
        return MealRecord(
            id = UUID.randomUUID().toString(),
            timestampMillis = System.currentTimeMillis(),
            title = title,
            decisionState = decision.state,
            shortMessage = decision.shortMessage,
            recognizedText = recognizedText,
            evidenceLabels = decision.evidence.map { it.type.label }.distinct(),
            endToEndMs = metrics.firstOrNull { it.stage.key == "end_to_end_ms" }?.latencyMs ?: 0L,
            userConfirmed = menuAnalysis?.registered == true || plateAnalysis?.registered == true,
            confirmedItems = confirmedItems,
            containsVisualEstimate = plateAnalysis?.candidates?.isNotEmpty() == true
        )
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            EatControlViewModel(container) as T
    }
}
