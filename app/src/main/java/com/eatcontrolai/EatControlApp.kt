package com.eatcontrolai

import android.app.Application
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import com.eatcontrolai.glasses.MockGlassesGateway
import com.eatcontrolai.inference.ModelRegistry
import com.eatcontrolai.inference.ProviderSet
import com.eatcontrolai.inference.androidtts.AndroidTtsProvider
import com.eatcontrolai.inference.mlkit.MlKitOcrProvider
import com.eatcontrolai.metrics.InMemoryMetricsRecorder
import com.eatcontrolai.orchestration.InteractionOrchestrator

/**
 * Composição manual das dependências.
 *
 * Sem framework de DI de propósito: são poucos objetos, o grafo é legível de uma vez só, e trocar
 * [MockGlassesGateway] por `DatGlassesGateway` vai ser uma linha quando o ADR-0006 for decidido.
 */
class EatControlApp : Application() {

    val container: AppContainer by lazy { AppContainer(this) }
}

class AppContainer(application: Application) {

    private val tts = AndroidTtsProvider(application)

    val glasses = MockGlassesGateway(tts)

    val models = ModelRegistry(
        ProviderSet(
            ocr = MlKitOcrProvider(),
            tts = tts
        )
    )

    val metrics = InMemoryMetricsRecorder()

    val orchestrator = InteractionOrchestrator(
        glasses = glasses,
        models = models,
        decisionEngine = FoodDecisionEngine(),
        metrics = metrics
    )

    /**
     * Perfil de demonstração — o "João" do `docs/PRD.md`.
     *
     * Local e fictício, como manda `docs/DATA_SOURCES.md`. Nenhum dado real de saúde entra no MVP.
     */
    val demoProfile = UserProfile(
        id = "demo-joao",
        displayName = "João",
        restrictions = setOf(Allergen.MILK),
        goals = setOf("priorizar proteína", "melhorar hidratação")
    )
}
