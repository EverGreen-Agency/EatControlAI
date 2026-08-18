---
unit: 12
unit_title: "Curso de Kotlin/Android"
section: "12.4"
section_title: "Na prática"
source_file: "Un12_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN12_Curso_de_Kotlin_Android.md"
source_pages: [9, 10, 11]
language: "pt-BR"
---

## 12.4 Na prática

Agora vamos sair da teoria: preparar o ambiente e rodar seu primeiro programa. O objetivo aqui é mínimo — só o suficiente para você não travar nos próximos tópicos.

### 12.4.1 Passo 1 — O que instalar

- Android Studio — o ambiente de desenvolvimento (IDE) oficial para Android. É onde você escreve, roda e depura o código. Baixe sempre a versão estável mais recente em developer.android.com/studio; na data de escrita, a linha estável era a Quail (Feature Drop Quail 2 | 2026.1.2). O piso real do programa é bem mais baixo: o SDK da Meta exige Android Studio Flamingo ou mais recente (tópico 2.3), então qualquer versão estável atual atende com folga.

- JDK (Java Development Kit) — necessário porque o Kotlin roda na JVM. Boa notícia para iniciantes: o Android Studio já vem com um JDK embutido (o JetBrains Runtime), que acompanha a versão do IDE — então normalmente você não precisa instalar um JDK separado.

Nota 4: quer só experimentar Kotlin agora, sem instalar nada? Use o Kotlin Playground no navegador, em play.kotlinlang.org. É a forma mais rápida de testar os exemplos abaixo antes de montar o ambiente completo. Nota 5: como o Android Studio é versionado, cada linha do IDE recebe um nome de animal em ordem alfabética (…Otter, Panda, Quail…), e dentro da linha saem Feature Drops numerados — Quail 1, Quail 2, Quail 3 — cada um com sua versão numérica (2026.1.1, 2026.1.2, 2026.1.3) e seus patches. Sempre há três canais em paralelo: Stable (use este), RC (quase pronto) e

Canary (experimental). Ou seja: o nome que você vê nesta página vai mudar durante o programa, e isso é normal — confira o vigente em developer.android.com/studio e o histórico em developer.android.com/studio/releases.

### 12.4.2 Passo 2 — Seu primeiro "Olá, mundo"

Todo programa Kotlin começa a executar por uma função especial chamada main. O exemplo mínimo:

▶ Código 1.1-01 – A função main é o ponto de entrada: é por aqui que o programa começa. — código completo no notebook companion (seção 1.1.4.2).

Ao rodar (no Playground, ou pelo botão de executar do Android Studio), a saída é simplesmente:

Olá, mundo!

### 12.4.3 Passo 3 — val, var e tipos

Programas guardam informação em variáveis. Em Kotlin há duas formas de declarar:

▶ Código 1.1-02 – código completo no notebook companion (seção 1.1.4.3).

Repare que não escrevemos o tipo (String, Int...) acima: o Kotlin usa inferência de tipo, deduzindo o tipo pelo valor. Você também pode declará-lo explicitamente:

▶ Código 1.1-03 – código completo no notebook companion (seção 1.1.4.3).

Os tipos básicos que você mais verá são String (texto), Int (inteiro), Double (decimal) e Boolean (verdadeiro/falso).

E o null safety citado na seção anterior aparece aqui, no tipo:

var texto: String = "ok"

```text
// texto = null
// ERRO: String comum não aceita
nulo
```

var talvez: String? = null // OK: o "?" permite ausência de valor

**Código 1.1-04 · também no notebook companion, seção 1.1.4.3**

Na prática: prefira val por padrão e só use var quando o valor realmente precisar mudar. Em um app que processa fluxo contínuo de frames e áudio dos óculos, quanto menos estado mutável "solto", menos surpresas — é um hábito que paga dividendos quando o código cresce. Nota 6: isso é só o mínimo para começar. Estruturas de controle, funções mais completas, classes e o modelo assíncrono (coroutines e Flow) virão nos próximos tópicos — coroutines e Flow, especificamente, no 1.2.
