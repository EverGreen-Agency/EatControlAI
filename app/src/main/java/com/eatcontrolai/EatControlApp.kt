package com.eatcontrolai

import android.app.Application
import com.eatcontrolai.data.LocalStore
import com.eatcontrolai.data.MealHistoryRepository
import com.eatcontrolai.data.OpenFoodFactsClient
import com.eatcontrolai.domain.barcode.BarcodeRepository
import com.eatcontrolai.data.PersonalRuleRepository
import com.eatcontrolai.data.PrivacyRepository
import com.eatcontrolai.data.ProfileRepository
import com.eatcontrolai.data.SymptomRepository
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import com.eatcontrolai.domain.glp1.Glp1RulePackV1
import com.eatcontrolai.domain.glp1.RulePack
import com.eatcontrolai.glasses.CaptureSourceRouter
import com.eatcontrolai.glasses.DatGlassesGateway
import com.eatcontrolai.glasses.GlassesAudioRouter
import com.eatcontrolai.glasses.MockGlassesGateway
import com.eatcontrolai.glasses.PhoneCameraGateway
import com.eatcontrolai.inference.ModelRegistry
import com.eatcontrolai.inference.ProviderSet
import com.eatcontrolai.inference.androidstt.AndroidSttProvider
import com.eatcontrolai.inference.androidtts.AndroidTtsProvider
import com.eatcontrolai.inference.mlkit.MlKitBarcodeProvider
import com.eatcontrolai.inference.mlkit.MlKitImageLabelingProvider
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

    override fun onCreate() {
        super.onCreate()
        // O DAT exige inicialização uma única vez por processo, na Application. Chamar qualquer
        // API antes disso devolve NOT_INITIALIZED.
        container.datGlasses.initialize()
    }
}

class AppContainer(application: Application) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val tts = AndroidTtsProvider(application)

    /** Roteia voz para os óculos por HFP quando eles estiverem disponíveis (unidade 13.6). */
    val audioRouter = GlassesAudioRouter(application)

    val stt = AndroidSttProvider(application)

    val mockGlasses = MockGlassesGateway(tts)

    val phoneCamera = PhoneCameraGateway(application, tts)

    val datGlasses = DatGlassesGateway(application, tts)

    /** Fonte de captura ativa. Trocar de fonte não recria nada a jusante. */
    val glasses = CaptureSourceRouter(mockGlasses, phoneCamera, datGlasses)

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
            stt = stt,
            detector = MlKitImageLabelingProvider()
        )
    )

    val metrics = InMemoryMetricsRecorder()

    val decisionEngine = FoodDecisionEngine()

    /**
     * Rule pack GLP-1 (`glp1-rules-v1`).
     *
     * `underReview = false` porque o aplicativo **não afirma revisão profissional em lugar nenhum**.
     * A flag existia para divulgar um estado intermediário — "conteúdo revisado, registro formal
     * pendente" — e esse estado não é o nosso: nenhum profissional assinou o rule pack, e nenhum
     * assinaria antes de ver o produto funcionando. Sem afirmação de validação, o aviso de revisão
     * não tem o que qualificar; ele só levantaria uma pergunta que a interface não responde.
     *
     * O que sustenta as regras hoje é o que elas realmente são: limites de rotulagem da Anvisa
     * sobre composição declarada, metas que a própria pessoa configurou e regras que ela criou.
     * Os termos de uso dizem isso, e nada além disso.
     *
     * Quando houver registro formal — nome, CRM ou CRN, data e escopo —, volte para `true` **e**
     * acrescente a atribuição na interface. As duas coisas juntas, nunca uma sem a outra.
     */
    val glp1Rules: RulePack = Glp1RulePackV1(underReview = false)

    /**
     * Catálogo de demonstração na frente, Open Food Facts atrás.
     *
     * É a única consulta de rede do aplicativo, e ela carrega só o código de barras. O caminho de
     * decisão continua local: sem internet, a trilha cai para o OCR do rótulo.
     */
    val products = BarcodeRepository(remote = OpenFoodFactsClient())

    val orchestrator = InteractionOrchestrator(
        glasses = glasses,
        models = models,
        decisionEngine = decisionEngine,
        metrics = metrics,
        barcodeRepository = products,
        rulePack = glp1Rules
    )

    private val store = LocalStore(application)

    val profiles = ProfileRepository(store, scope)

    val privacy = PrivacyRepository(store, scope)

    val history = MealHistoryRepository(store, scope)

    /** Regras pessoais e sintomas relatados: entradas da camada GLP-1, ambas locais. */
    val personalRules = PersonalRuleRepository(store, scope)

    val symptoms = SymptomRepository(store, scope)
}
