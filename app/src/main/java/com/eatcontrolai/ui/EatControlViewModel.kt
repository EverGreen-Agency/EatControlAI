package com.eatcontrolai.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eatcontrolai.AppContainer
import com.eatcontrolai.benchmark.ProviderBenchmark
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity
import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.LabelClaim
import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.RestrictionSeverity
import com.eatcontrolai.core.model.UncertaintyPolicy
import com.eatcontrolai.domain.voice.VoiceIntentParser
import com.eatcontrolai.glasses.CaptureSource
import com.eatcontrolai.glasses.GlassesStatus
import com.eatcontrolai.glasses.MockScene
import com.eatcontrolai.glasses.MockScenes
import com.eatcontrolai.glasses.SceneKind
import com.eatcontrolai.orchestration.AnalysisTrack
import com.eatcontrolai.orchestration.InteractionResult
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Modos de entrada de `contexto-gpt.md` §12.4.
 *
 * [track] nulo significa que a trilha ainda não existe: a UI mostra o modo mas não deixa analisar,
 * em vez de fingir que funciona.
 */
enum class AnalyzeMode(
    val label: String,
    val glyph: String,
    val prompt: String,
    val track: AnalysisTrack?,
    val sceneKind: SceneKind?
) {
    LABEL("Rótulo", "Aa", "\"Tem algo aqui que conflita com meu plano?\"", AnalysisTrack.LABEL, SceneKind.LABEL),
    BARCODE("Código de barras", "▦", "\"Posso incluir esse produto?\"", AnalysisTrack.BARCODE, SceneKind.BARCODE),
    PLATE("Prato", "◉", "\"Como está esse prato para mim?\"", null, null),
    MENU("Cardápio", "≡", "\"Qual opção parece mais alinhada comigo?\"", null, null);

    val ready: Boolean get() = track != null
}

data class VoiceState(
    val listening: Boolean = false,
    val transcript: String = "",
    val onDevice: Boolean = false,
    val available: Boolean = true,
    val permissionDenied: Boolean = false
)

data class LabState(
    val running: Boolean = false,
    val results: List<ProviderBenchmark.Result> = emptyList(),
    val error: String? = null
)

data class AnalyzeState(
    val mode: AnalyzeMode = AnalyzeMode.LABEL,
    val source: CaptureSource = CaptureSource.MOCK_GLASSES,
    val selectedSceneId: String = MockScenes.default.id,
    val isAnalyzing: Boolean = false,
    val result: InteractionResult? = null,
    val resultRecordId: String? = null,
    val showResult: Boolean = false,
    val error: String? = null
) {
    val scenes: List<MockScene>
        get() = mode.sceneKind?.let { MockScenes.forKind(it) } ?: emptyList()

    val selectedScene: MockScene?
        get() = scenes.firstOrNull { it.id == selectedSceneId } ?: scenes.firstOrNull()

    /** O seletor de cena só faz sentido quando a fonte é simulada. */
    val showsSceneSelector: Boolean get() = source == CaptureSource.MOCK_GLASSES && scenes.isNotEmpty()
}

class EatControlViewModel(private val container: AppContainer) : ViewModel() {

    val profile = container.profiles.profile
    val privacy = container.privacy.settings
    val history = container.history.records

    private val _analyze = MutableStateFlow(AnalyzeState())
    val analyze: StateFlow<AnalyzeState> = _analyze.asStateFlow()

    private val _glasses = MutableStateFlow(container.glasses.status)
    val glasses: StateFlow<GlassesStatus> = _glasses.asStateFlow()

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
            val scenes = mode.sceneKind?.let { kind -> MockScenes.forKind(kind) }.orEmpty()
            it.copy(
                mode = mode,
                selectedSceneId = scenes.firstOrNull()?.id ?: it.selectedSceneId,
                result = null,
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
        _analyze.update { it.copy(selectedSceneId = sceneId, result = null, error = null) }
    }

    fun selectSource(source: CaptureSource) {
        container.glasses.select(source)
        _analyze.update { it.copy(source = source, result = null, error = null) }
        refreshGlasses()

        // Os óculos reais precisam abrir sessão antes de qualquer captura.
        if (source == CaptureSource.DAT_GLASSES) connectDatGlasses()
    }

    /**
     * Abre o fluxo de autorização do DAT no app Meta AI.
     *
     * O controle volta para cá pelo deep link declarado no manifesto. Precisa de uma Activity porque
     * é o Meta AI que apresenta a tela de consentimento.
     */
    fun registerDatGlasses(activity: Activity) {
        container.datGlasses.initialize()
            .onSuccess { container.datGlasses.startRegistration(activity) }
            .onFailure { showToast("Não consegui iniciar o DAT: ${it.message}") }
    }

    private fun connectDatGlasses() {
        viewModelScope.launch {
            runCatching { container.datGlasses.connect() }
                .onSuccess {
                    refreshGlasses()
                    showToast("Sessão aberta com os óculos.")
                }
                .onFailure {
                    refreshGlasses()
                    _analyze.update { state -> state.copy(error = it.message) }
                }
        }
    }

    /** Chamado pela tela quando a câmera do celular liga ou desliga. */
    fun onCameraBindingChanged() = refreshGlasses()

    fun analyze() {
        val state = _analyze.value
        val track = state.mode.track ?: return
        if (state.isAnalyzing) return

        _analyze.update { it.copy(isAnalyzing = true, error = null) }

        viewModelScope.launch {
            runCatching { container.orchestrator.analyze(track, profile.value) }
                .onSuccess { result ->
                    val record = result.toRecord()
                    container.history.add(record)
                    _analyze.update {
                        it.copy(
                            isAnalyzing = false,
                            result = result,
                            resultRecordId = record.id,
                            showResult = true
                        )
                    }
                }
                .onFailure { throwable ->
                    // NFR-004: falha degrada para mensagem explícita, nunca para afirmação de segurança.
                    _analyze.update {
                        it.copy(
                            isAnalyzing = false,
                            error = throwable.message ?: "Falha ao analisar."
                        )
                    }
                }
        }
    }

    fun dismissResult() = _analyze.update { it.copy(showResult = false) }

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
            container.history.replace(updated.toRecord().copy(id = recordId, userConfirmed = true))
        }

        viewModelScope.launch { container.glasses.playSpeech(decision.shortMessage) }
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
        val provider = container.models.current().stt ?: return

        _voice.update { it.copy(listening = true, transcript = "") }

        viewModelScope.launch {
            runCatching { provider.transcribe() }
                .onSuccess { result ->
                    val intent = VoiceIntentParser.parse(result.text)
                    _voice.update {
                        it.copy(
                            listening = false,
                            transcript = result.text,
                            onDevice = container.stt.usingOnDevice
                        )
                    }

                    if (!intent.recognized) {
                        showToast("Não entendi. Tente de novo mais perto do microfone.")
                        return@onSuccess
                    }

                    val mode = AnalyzeMode.entries.firstOrNull { it.track == intent.track }
                    if (mode != null && mode != _analyze.value.mode) selectMode(mode)
                    analyze()
                }
                .onFailure { throwable ->
                    _voice.update { it.copy(listening = false) }
                    showToast("Reconhecimento de voz falhou: ${throwable.message}")
                }
        }
    }

    // ------------------------------------------------------------- meu plano

    fun toggleRestriction(allergen: Allergen) {
        container.profiles.update { current ->
            val existing = current.restrictionFor(allergen)
            val next = if (existing != null) current.restrictions - existing
            else current.restrictions + Restriction(allergen)
            current.copy(restrictions = next)
        }
        _analyze.update { it.copy(result = null) }
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
        _analyze.update { it.copy(result = null) }
    }

    fun setUncertaintyPolicy(allergen: Allergen, policy: UncertaintyPolicy) {
        container.profiles.update { current ->
            val existing = current.restrictionFor(allergen) ?: return@update current
            current.copy(
                restrictions = current.restrictions - existing + existing.copy(uncertaintyPolicy = policy)
            )
        }
        _analyze.update { it.copy(result = null) }
    }

    // -------------------------------------------------------------- privacidade

    fun toggleSavePhotos() = container.privacy.update { it.copy(savePhotos = !it.savePhotos) }

    fun toggleShareForImprovement() =
        container.privacy.update { it.copy(shareForImprovement = !it.shareForImprovement) }

    fun toggleSyncHistory() = container.privacy.update { it.copy(syncHistory = !it.syncHistory) }

    fun clearHistory() {
        container.history.clear()
        showToast("Histórico apagado do aparelho.")
    }

    fun resetProfile() {
        container.profiles.reset()
        showToast("Perfil restaurado para o de demonstração.")
    }

    // ------------------------------------------------------------------ óculos

    fun testAudio() {
        viewModelScope.launch {
            val meta = container.glasses.playSpeech("Eat Control pronto.")
            showToast("Áudio reproduzido em ${meta.latencyMs} ms até o primeiro som.")
        }
    }

    fun testCamera() {
        viewModelScope.launch {
            runCatching { container.glasses.capturePhoto() }
                .onSuccess { showToast("Frame capturado: ${it.size / 1024} KB.") }
                .onFailure { showToast("Captura falhou: ${it.message}") }
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
        val title = productName
            ?: _analyze.value.selectedScene?.title
            ?: when (track) {
                AnalysisTrack.LABEL -> "Rótulo analisado"
                AnalysisTrack.BARCODE -> "Produto escaneado"
            }
        return MealRecord(
            id = UUID.randomUUID().toString(),
            timestampMillis = System.currentTimeMillis(),
            title = title,
            decisionState = decision.state,
            shortMessage = decision.shortMessage,
            recognizedText = recognizedText,
            evidenceLabels = decision.evidence.map { it.type.label },
            endToEndMs = metrics.firstOrNull { it.stage.key == "end_to_end_ms" }?.latencyMs ?: 0L
        )
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            EatControlViewModel(container) as T
    }
}
