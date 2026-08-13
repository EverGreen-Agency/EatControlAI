package com.eatcontrolai

import android.app.Application
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.Guideline
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.RestrictionSeverity
import com.eatcontrolai.core.model.UncertaintyPolicy
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.data.LocalStore
import com.eatcontrolai.data.MealHistoryRepository
import com.eatcontrolai.data.PrivacyRepository
import com.eatcontrolai.data.ProfileRepository
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import com.eatcontrolai.glasses.CaptureSourceRouter
import com.eatcontrolai.glasses.MockGlassesGateway
import com.eatcontrolai.glasses.PhoneCameraGateway
import com.eatcontrolai.inference.ModelRegistry
import com.eatcontrolai.inference.ProviderSet
import com.eatcontrolai.inference.androidstt.AndroidSttProvider
import com.eatcontrolai.inference.androidtts.AndroidTtsProvider
import com.eatcontrolai.inference.mlkit.MlKitBarcodeProvider
import com.eatcontrolai.inference.mlkit.MlKitOcrProvider
import com.eatcontrolai.metrics.InMemoryMetricsRecorder
import com.eatcontrolai.orchestration.InteractionOrchestrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Composição manual das dependências.
 *
 * Sem framework de DI de propósito: são poucos objetos, o grafo é legível de uma vez só, e trocar a
 * fonte de captura por `DatGlassesGateway` vai ser uma linha quando as credenciais do ADR-0006
 * saírem.
 */
class EatControlApp : Application() {

    val container: AppContainer by lazy { AppContainer(this) }
}

class AppContainer(application: Application) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val tts = AndroidTtsProvider(application)

    val stt = AndroidSttProvider(application)

    val mockGlasses = MockGlassesGateway(tts)

    val phoneCamera = PhoneCameraGateway(application, tts)

    /** Fonte de captura ativa. Trocar de fonte não recria nada a jusante. */
    val glasses = CaptureSourceRouter(mockGlasses, phoneCamera)

    /**
     * **Ponto único de troca de modelos.**
     *
     * Trocar de OCR é trocar a implementação abaixo; em runtime, `models.swap(outroConjunto)`.
     * O catálogo de candidatos está em `benchmark/candidates.yaml` e a régua de escolha em
     * `docs/MODEL_BENCHMARK.md`. Nenhuma outra parte do app conhece o ML Kit.
     */
    val models = ModelRegistry(
        ProviderSet(
            ocr = MlKitOcrProvider(),
            tts = tts,
            barcode = MlKitBarcodeProvider(),
            stt = stt
        )
    )

    val metrics = InMemoryMetricsRecorder()

    val decisionEngine = FoodDecisionEngine()

    val orchestrator = InteractionOrchestrator(
        glasses = glasses,
        models = models,
        decisionEngine = decisionEngine,
        metrics = metrics
    )

    private val store = LocalStore(application)

    /**
     * Perfil de demonstração — o "João" do `docs/PRD.md`.
     *
     * Local e fictício, como manda `docs/DATA_SOURCES.md`. Nenhum dado real de saúde entra no MVP.
     * Usado apenas na primeira execução; depois, o que vale é o que está gravado.
     */
    private val demoProfile = UserProfile(
        id = "demo-joao",
        displayName = "João",
        usesGlp1 = true,
        restrictions = setOf(
            Restriction(
                allergen = Allergen.MILK,
                severity = RestrictionSeverity.CRITICAL,
                uncertaintyPolicy = UncertaintyPolicy.ASK_CONFIRMATION
            )
        ),
        goals = setOf("Priorizar proteína", "Melhorar hidratação", "Preservar massa magra"),
        guidelines = listOf(
            Guideline(
                "Priorizar proteína nas refeições principais",
                "Usada como prioridade de composição, não como recomendação clínica universal."
            ),
            Guideline(
                "Evitar refeições excessivamente volumosas",
                "O sistema pode sinalizar porção aparente grande ou perguntar antes de concluir."
            ),
            Guideline(
                "Lembrar hidratação ao longo do dia",
                "O histórico alimenta lembretes, respeitando as preferências do usuário."
            )
        )
    )

    val profiles = ProfileRepository(store, scope, demoProfile)

    val privacy = PrivacyRepository(store, scope)

    val history = MealHistoryRepository(store, scope)
}
