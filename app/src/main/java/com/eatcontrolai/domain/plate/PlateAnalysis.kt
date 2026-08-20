package com.eatcontrolai.domain.plate

import com.eatcontrolai.inference.Detection
import com.eatcontrolai.inference.DetectionResult
import java.text.Normalizer

/** Taxonomia fechada da v1; descreve aparência geral, não receita nem composição. */
enum class PlateFoodClass(val displayName: String) {
    RICE("arroz"),
    BEANS("feijão"),
    CHICKEN("frango"),
    MEAT("carne"),
    FISH("peixe"),
    PASTA("massa"),
    SALAD("salada"),
    VEGETABLES("legumes"),
    EGG("ovo"),
    FRIED_FOOD("fritura"),
    CHEESE("queijo"),
    DESSERT("sobremesa")
}

data class PlateCandidate(
    val rawLabel: String,
    val confidence: Float,
    val foodClass: PlateFoodClass?
)

data class PlateAnalysis(
    val candidates: List<PlateCandidate>,
    val providerId: String,
    val providerVersion: String,
    /** Sugestões pré-selecionadas ainda precisam do botão explícito de confirmação. */
    val selectedComponents: Set<PlateFoodClass> = emptySet(),
    val registered: Boolean = false,
    val warnings: List<String> = emptyList()
)

/** Mapeia rótulos genéricos para o recorte fechado sem forçar classes desconhecidas. */
object PlateLabelMapper {

    /** Gate de engenharia experimental; não representa confiança clínica. */
    const val MIN_MAPPING_CONFIDENCE = 0.65f

    private val diacritics = Regex("\\p{Mn}+")
    private val spaces = Regex("\\s+")

    fun map(result: DetectionResult): PlateAnalysis {
        val candidates = result.detections
            .sortedByDescending(Detection::confidence)
            .take(MAX_CANDIDATES)
            .map { detection ->
                PlateCandidate(
                    rawLabel = detection.label,
                    confidence = detection.confidence.coerceIn(0f, 1f),
                    foodClass = detection.takeIf { it.confidence >= MIN_MAPPING_CONFIDENCE }
                        ?.let { classFor(it.label) }
                )
            }

        val selected = candidates.mapNotNull(PlateCandidate::foodClass).toSet()
        val warnings = buildList {
            if (candidates.isEmpty()) {
                add("O modelo não produziu candidatos; selecione manualmente o que você reconhece.")
            } else if (selected.isEmpty()) {
                add("Nenhum candidato atingiu o gate ou pertence às classes fechadas da v1.")
            }
            add("A imagem não determina receita, ingrediente oculto, quantidade ou macros.")
        }
        return PlateAnalysis(
            candidates = candidates,
            providerId = result.meta.providerId,
            providerVersion = result.meta.version,
            selectedComponents = selected,
            warnings = warnings
        )
    }

    fun unavailable(): PlateAnalysis = PlateAnalysis(
        candidates = emptyList(),
        providerId = "not_configured",
        providerVersion = "none",
        warnings = listOf(
            "Detector visual indisponível; o registro continua somente por seleção manual.",
            "A imagem não determina receita, ingrediente oculto, quantidade ou macros."
        )
    )

    internal fun classFor(label: String): PlateFoodClass? {
        val normalized = normalize(label)
        return mappings.firstOrNull { (terms, _) -> terms.any(normalized::contains) }?.second
    }

    private fun normalize(value: String): String {
        val decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
        return spaces.replace(diacritics.replace(decomposed, "").lowercase(), " ").trim()
    }

    private val mappings = listOf(
        listOf("french fries", "fried food", "fritura", "frito") to PlateFoodClass.FRIED_FOOD,
        listOf("chicken", "poultry", "frango") to PlateFoodClass.CHICKEN,
        listOf("beef", "steak", "red meat", "carne") to PlateFoodClass.MEAT,
        listOf("fish", "seafood", "peixe") to PlateFoodClass.FISH,
        listOf("rice", "arroz") to PlateFoodClass.RICE,
        listOf("bean", "feijao") to PlateFoodClass.BEANS,
        listOf("pasta", "noodle", "spaghetti", "massa") to PlateFoodClass.PASTA,
        listOf("salad", "salada") to PlateFoodClass.SALAD,
        listOf("vegetable", "legume") to PlateFoodClass.VEGETABLES,
        listOf("egg", "omelet", "ovo") to PlateFoodClass.EGG,
        listOf("cheese", "queijo") to PlateFoodClass.CHEESE,
        listOf("dessert", "cake", "pastry", "sobremesa", "bolo") to PlateFoodClass.DESSERT
    )

    private const val MAX_CANDIDATES = 8
}
