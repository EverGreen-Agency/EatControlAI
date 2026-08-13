package com.eatcontrolai.ui

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
import com.eatcontrolai.glasses.GlassesStatus
import com.eatcontrolai.glasses.MockScene
import com.eatcontrolai.glasses.MockScenes
import com.eatcontrolai.orchestration.InteractionResult
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Modos de entrada de `contexto-gpt.md` §12.4. Só RÓTULO está implementado ponta a ponta. */
enum class AnalyzeMode(val label: String, val glyph: String, val prompt: String, val ready: Boolean) {
    LABEL("Rótulo", "Aa", "\"Tem algo aqui que conflita com meu plano?\"", true),
    PLATE("Prato", "◉", "\"Como está esse prato para mim?\"", false),
    BARCODE("Código de barras", "▦", "\"Posso incluir esse produto?\"", false),
    MENU("Cardápio", "≡", "\"Qual opção parece mais alinhada comigo?\"", false)
}

data class LabState(
    val running: Boolean = false,
    val results: List<ProviderBenchmark.Result> = emptyList(),
    val error: String? = null
)

data class AnalyzeState(
    val mode: AnalyzeMode = AnalyzeMode.LABEL,
    val scenes: List<MockScene> = MockScenes.all,
    val selectedSceneId: String = MockScenes.default.id,
    val isAnalyzing: Boolean = false,
    val result: InteractionResult? = null,
    val resultRecordId: String? = null,
    val showResult: Boolean = false,
    val error: String? = null
) {
    val selectedScene: MockScene get() = scenes.first { it.id == selectedSceneId }
}

class EatControlViewModel(private val container: AppContainer) : ViewModel() {

    val profile = container.profiles.profile
    val privacy = container.privacy.settings
    val history = container.history.records

    private val _analyze = MutableStateFlow(AnalyzeState())
    val analyze: StateFlow<AnalyzeState> = _analyze.asStateFlow()

    private val _glasses = MutableStateFlow(container.glasses.status)
    val glasses: StateFlow<GlassesStatus> = _glasses.asStateFlow()

    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast.asStateFlow()

    private val _lab = MutableStateFlow(LabState())
    val lab: StateFlow<LabState> = _lab.asStateFlow()

    init {
        viewModelScope.launch {
            container.glasses.connect()
            _glasses.value = container.glasses.status
        }
    }

    fun showToast(message: String) {
        _toast.value = message
    }

    fun clearToast() {
        _toast.value = null
    }

    // ---------------------------------------------------------------- analisar

    fun selectMode(mode: AnalyzeMode) {
        _analyze.update { it.copy(mode = mode, result = null, error = null) }
        if (!mode.ready) {
            showToast("${mode.label} ainda não está implementado. Rótulo é a trilha fechada.")
        }
    }

    fun selectScene(sceneId: String) {
        container.glasses.selectScene(sceneId)
        _analyze.update { it.copy(selectedSceneId = sceneId, result = null, error = null) }
    }

    fun analyze() {
        if (_analyze.value.isAnalyzing) return
        _analyze.update { it.copy(isAnalyzing = true, error = null) }

        viewModelScope.launch {
            runCatching { container.orchestrator.analyzeLabel(profile.value) }
                .onSuccess { result ->
                    val record = result.toRecord(_analyze.value.selectedScene.title)
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
                            error = throwable.message ?: "Falha ao analisar o rótulo."
                        )
                    }
                }
        }
    }

    fun dismissResult() = _analyze.update { it.copy(showResult = false) }

    /**
     * Fecha o loop de confirmação do `contexto-gpt.md` §56 (Cenário 3).
     *
     * A resposta do usuário entra como [EvidenceType.USER_CONFIRMATION] — rank 4, acima de OCR
     * bruto e de inferência visual — e a decisão é **recalculada pelo mesmo motor**. Não é um texto
     * trocado na tela: a evidência nova realmente muda o resultado.
     */
    fun confirmIngredient(allergen: Allergen, present: Boolean) {
        val current = _analyze.value.result ?: return
        val recordId = _analyze.value.resultRecordId

        val confirmation = Evidence(
            type = EvidenceType.USER_CONFIRMATION,
            value = if (present) "usuário confirmou presença de ${allergen.displayName}"
            else "usuário confirmou ausência de ${allergen.displayName}",
            source = "user",
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
            container.history.replace(
                updated.toRecord(_analyze.value.selectedScene.title)
                    .copy(id = recordId, userConfirmed = true)
            )
        }

        viewModelScope.launch { container.glasses.playSpeech(decision.shortMessage) }
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
     * ([ProviderBenchmark]) — a tela existe para conferir no aparelho sem precisar do notebook.
     */
    fun runBenchmark(repetitions: Int = 3) {
        if (_lab.value.running) return
        _lab.update { it.copy(running = true, results = emptyList()) }

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

    private fun InteractionResult.toRecord(title: String) = MealRecord(
        id = UUID.randomUUID().toString(),
        timestampMillis = System.currentTimeMillis(),
        title = title,
        decisionState = decision.state,
        shortMessage = decision.shortMessage,
        recognizedText = recognizedText,
        evidenceLabels = decision.evidence.map { it.type.label },
        endToEndMs = metrics.firstOrNull { it.stage.key == "end_to_end_ms" }?.latencyMs ?: 0L
    )

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            EatControlViewModel(container) as T
    }
}
