package com.eatcontrolai.domain.glp1

import com.eatcontrolai.core.model.Decision
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.NutritionFacts
import com.eatcontrolai.domain.label.FoodTerms
import com.eatcontrolai.domain.label.TextNormalizer
import com.eatcontrolai.domain.plate.PlateFoodClass

/**
 * Ligação entre uma interação de análise e o rule pack GLP-1.
 *
 * O [Glp1Context] tem duas metades com donos diferentes: a metade da pessoa (metas, restrições,
 * regras pessoais, sintomas) vive na camada de aplicação e muda devagar; a metade da percepção
 * (tabela lida, componentes confirmados, termos observados) nasce a cada análise. Este arquivo é o
 * ponto único onde as duas se encontram, para que o orquestrador e a interface não montem contextos
 * ligeiramente diferentes e produzam respostas diferentes para a mesma refeição.
 */

/** A metade da percepção: o que esta interação específica observou. */
data class Glp1Perception(
    val facts: NutritionFacts = NutritionFacts(),
    /** Texto reconhecido pelo OCR, quando houver. Vira termo observado, nunca composição. */
    val recognizedText: String = "",
    /** Termos já extraídos por um parser próprio, como as opções de cardápio. */
    val knownTerms: Set<String> = emptySet(),
    /** Componentes que a pessoa confirmou na trilha de prato. Candidato não confirmado não entra. */
    val confirmedComponents: Set<PlateFoodClass> = emptySet(),
    /** Porções confirmadas. Nulo enquanto a pessoa não disser quanto comeu. */
    val confirmedPortions: Double? = null,
    /** `true` quando a interação passou por modelo de visão. */
    val hasVisualInference: Boolean = false
)

/**
 * Preenche a metade da percepção sobre um contexto que já traz a metade da pessoa.
 *
 * Os termos observados somam três origens porque elas se complementam: o parser de cardápio entrega
 * termos já estruturados, o texto livre passa pelo vocabulário fixo de [FoodTerms], e as palavras
 * que a própria pessoa cadastrou nas regras dela são procuradas no texto.
 *
 * A terceira origem não é detalhe. [FoodTerms] tem nove termos; sem ela, uma regra "evitar camarão"
 * jamais encontraria camarão em lugar nenhum, e a pessoa veria a regra cadastrada nunca disparar
 * sem entender por quê.
 */
fun Glp1Context.withPerception(perception: Glp1Perception): Glp1Context = copy(
    facts = perception.facts,
    confirmedPortions = perception.confirmedPortions,
    confirmedComponents = perception.confirmedComponents,
    observedTerms = perception.knownTerms +
        FoodTerms.observedIn(perception.recognizedText) +
        personalTermsIn(perception.recognizedText),
    hasVisualInference = perception.hasVisualInference
)

/**
 * Termos das regras da pessoa que aparecem literalmente no texto lido.
 *
 * Correspondência por substring do texto normalizado, não por inferência: se ela escreveu "camarão"
 * e o rótulo diz "camarão", isso é o texto dizendo, não o aplicativo adivinhando. Termos apenas
 * plausíveis entram aqui também — quem decide se viram pergunta em vez de afirmação é a R5.
 */
private fun Glp1Context.personalTermsIn(text: String): Set<String> {
    if (text.isBlank() || personalRules.isEmpty()) return emptySet()
    val haystack = TextNormalizer.normalize(text)
    return personalRules
        .flatMap { it.matchTerms + it.possibleTerms }
        .filter { term ->
            term.isNotBlank() && haystack.contains(TextNormalizer.normalize(term))
        }
        .toSet()
}

/**
 * Se o pack tem alguma coisa a dizer sobre esta refeição.
 *
 * Sem meta cadastrada, sem regra pessoal e com um rótulo dentro dos limites de rotulagem, a resposta
 * honesta do pack é "não avaliei" — e essa frase ao lado de um selo verde de compatibilidade parece
 * contradição, não transparência. Quando não há achado, pergunta nem encaminhamento, a leitura GLP-1
 * simplesmente não aparece.
 */
val Glp1Assessment.hasSomethingToSay: Boolean
    get() = analysisSuppressed ||
        findings.isNotEmpty() ||
        questions.isNotEmpty() ||
        escalation != EscalationLevel.NONE

/**
 * Compõe a frase falada quando as duas réguas do produto respondem juntas.
 *
 * São duas réguas distintas e não intercambiáveis: o motor determinístico responde sobre conflito
 * com o plano de restrições — é a resposta de segurança — e o rule pack responde sobre o recorte
 * GLP-1. A ordem aqui é a ordem de prioridade delas, não uma concatenação por conveniência.
 */
object Glp1SpokenLine {

    fun compose(decision: Decision, assessment: Glp1Assessment?): String {
        if (assessment == null) return decision.shortMessage

        // Encaminhamento médico suprime a análise alimentar inteira. Falar o veredito de
        // compatibilidade junto contradiria a regra validada, que manda parar de orientar.
        if (assessment.analysisSuppressed) return assessment.shortMessage

        // Conflito com o plano e pergunta pendente são a resposta que a pessoa pediu. Empilhar
        // atenção nutricional em cima deixa a fala mais longa e a decisão menos clara.
        val decisionAnswers = decision.state == DecisionState.INCOMPATIBLE ||
            decision.state == DecisionState.NEEDS_CONFIRMATION
        if (decisionAnswers || !deservesAudio(assessment)) return decision.shortMessage

        return "${decision.shortMessage} ${assessment.shortMessage}"
    }

    /**
     * O que vale interromper alguém para dizer.
     *
     * Atenção e pergunta, sim. Achado positivo e saldo de meta ficam na tela: repetir "boa presença
     * de vegetais" em toda análise gastaria o orçamento de fala sem mudar nenhuma decisão.
     */
    private fun deservesAudio(assessment: Glp1Assessment): Boolean {
        val hasAttention = assessment.findings.any { it.kind == FindingKind.ATTENTION }
        val onlyQuestion = assessment.findings.isEmpty() && assessment.questions.isNotEmpty()
        return hasAttention || onlyQuestion
    }
}
