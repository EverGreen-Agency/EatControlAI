---
unit: 12
unit_title: "Curso de Kotlin/Android"
section: "12.3"
section_title: "Conceito / fundamentação"
source_file: "Un12_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN12_Curso_de_Kotlin_Android.md"
source_pages: [4, 5, 6, 7, 8, 9]
language: "pt-BR"
---

## 12.3 Conceito / fundamentação

Este é o coração do tópico: entender o que é o Kotlin e por que todo o programa é construído nele. A parte prática (instalar e rodar código) vem na próxima seção.

### 12.3.1 O que é Kotlin

Kotlin é uma linguagem de programação moderna, de propósito geral, criada pela JetBrains — a mesma empresa por trás de ferramentas de desenvolvimento amplamente usadas no mercado. Ela foi desenhada para ser concisa, segura e prática, resolvendo dores que os desenvolvedores sentiam em outras linguagens.

Se você nunca programou, pense na linguagem de programação como o idioma que usamos para dar instruções ao computador. Assim como existem idiomas humanos com regras diferentes, existem linguagens de programação com estilos diferentes. Kotlin é uma dessas linguagens — e é a que usaremos do começo ao fim deste programa.

### 12.3.2 Uma breve história: de 2011 a hoje

A trajetória do Kotlin ajuda a entender por que ele é levado a sério hoje:

- 2011 — a JetBrains anuncia publicamente o projeto Kotlin.

- 2016 — sai o Kotlin 1.0, a primeira versão estável, considerada pronta para uso em produção.

- 2017 — no evento Google I/O, o Google anuncia suporte oficial ao Kotlin para o desenvolvimento Android.

- 2019 — o Google torna o Kotlin a linguagem preferida (a estratégia "Kotlin-first"): novas APIs, exemplos e documentação passam a priorizar Kotlin.

- Hoje — a linguagem continua evoluindo, em ciclos previsíveis. A linha estável atual é a 2.4, aberta pelo Kotlin 2.4.0 em junho de 2026; na data de escrita, a versão pontual mais recente dessa linha é a 2.4.10 (julho de 2026). Em julho de 2026, o Kotlin completou 15 anos (Figura 1).

**Figura 1 – Linha do tempo do Kotlin: de projeto da JetBrains a linguagem preferida do Android (2011–2026)**

Fonte: autoria própria.

### 12.3.3 Para que serve e por que existe

Kotlin roda na JVM (Java Virtual Machine), a mesma máquina virtual que executa programas Java. Uma máquina virtual, aqui, é um "motor" instalado no dispositivo que sabe rodar o código compilado. Rodar na JVM traz três consequências importantes:

- Interoperável com Java: código Kotlin e código Java podem conviver no mesmo projeto, e o Kotlin consegue usar bibliotecas Java já existentes. Isso é

decisivo, porque o Android nasceu em Java — todo o ecossistema já pronto continua disponível.

- Moderno e conciso: Kotlin costuma expressar a mesma ideia com menos código que Java, o que reduz repetição e chance de erro.

- Seguro: o destaque é o null safety (segurança contra nulos), explicado a seguir.

### 12.3.4 Por que Kotlin, e não Java, para Android hoje

Java segue funcionando no Android, mas o Kotlin virou a escolha padrão por alguns motivos concretos:

- Null safety. Um dos erros mais comuns em Java é o NullPointerException: o programa tenta usar um valor que, na verdade, está "vazio" (null) e quebra em tempo de execução. Kotlin trata isso no sistema de tipos: um tipo como String não aceita null, e você precisa dizer explicitamente String? quando um valor pode faltar. Muitos desses erros passam a ser pegos já na compilação, antes de o app chegar ao usuário.

- Concisão. Menos código repetitivo (boilerplate) para tarefas comuns.

- Coroutines. Um mecanismo moderno para lidar com tarefas que acontecem "ao mesmo tempo" (assíncronas) de forma legível — fundamental para streaming de dados. Você verá coroutines em detalhe no tópico 1.2.

- Suporte oficial do Google. Sendo a linguagem preferida, Kotlin recebe primeiro as novidades, exemplos e ferramentas.

Na prática: no nosso programa, o app companion Android é quem recebe o vídeo da câmera e o áudio dos microfones dos óculos via Bluetooth e roda os modelos de IA on-device. Um app que lida com câmera, áudio e conexão sem fio tem muitos pontos onde um valor pode "faltar" (um frame que não chegou, um microfone indisponível). O null safety do Kotlin ajuda a evitar que essas situações virem travamentos no meio de uma demo do hackathon.

### 12.3.5 Kotlin × Swift: linguagens irmãs, plataformas diferentes

É comum confundir os papéis. A forma simples de lembrar (Tabela 1):

**Tabela 1 – Comparação entre Kotlin e Swift**

Papel Kotlin Swift Criador JetBrains (com o Google adotando para Android) Apple

Plataforma principal Android (e multiplataforma via KMP) Ecossistema Apple: iOS, macOS

Filosofia Moderna, concisa, segura Moderna, concisa, segura

Fonte: autoria própria.

Ambas são linguagens modernas e seguras, com ideias parecidas — mas cada uma domina um mundo diferente. KMP (Kotlin Multiplatform) é a tecnologia que permite compartilhar código Kotlin entre plataformas; guarde só o nome por enquanto. Como o hardware que vamos usar é Android, Kotlin é o caminho natural.

### 12.3.6 Por que Android nativo em Kotlin, e não Flutter/React Native

Frameworks cross-platform (como Flutter e React Native) permitem escrever um código e rodá-lo em Android e iOS. Parece atraente, mas para este programa há motivos técnicos decisivos para ficar no nativo.

Primeiro, um esclarecimento: o SDK da Meta não é exclusivo do Android. O Meta Wearables Device Access Toolkit (DAT) é publicado em duas trilhas — um SDK Android (artefatos Gradle, API em Kotlin) e um SDK iOS (Swift Package Manager, Swift 6). O que ele não tem é trilha cross-platform:

O DAT é distribuído apenas como SDK nativo — Android (Kotlin) ou iOS (Swift). Não existe plugin oficial para Flutter, módulo para React Native, nem binding cross-platform.

As consequências práticas de tentar usar um framework cross-platform aqui:

- A ponte seria sua. Você teria que escrever e manter um platform channel (Flutter) ou native module (React Native) expondo o SDK nativo ao seu código Dart/JavaScript — trabalho que não conta como progresso no hackathon.

- A API do DAT é Kotlin idiomático. Estado exposto como StateFlow e SharedFlow, funções suspend, enum para os estados de registro e sessão (você verá tudo isso no 1.2 e no 2.4). Atravessar Flow e suspend por uma ponte é justamente a parte difícil: você reimplementaria à mão a semântica de streaming e de ciclo de sessão.

- O gargalo de dados. O stream de câmera entrega buffers de pixels a até 30 fps (tópico 2.5), e esses frames alimentam a inferência on-device (tópico 1.7). Fazer esses buffers atravessarem uma ponte para o lado Dart/JS e voltarem para o lado nativo do modelo acrescenta cópia e latência no trecho mais sensível do pipeline.

- O SDK está em developer preview. As APIs mudam entre versões — é por isso que ler o CHANGELOG é obrigatório (tópico 2.1). Uma ponte de terceiros, se existisse, andaria atrás.

Escrever em Kotlin/Android nativo dá acesso direto e de primeira classe ao DAT e às APIs de dispositivo (câmera, microfone, Bluetooth). Uma abordagem cross-platform precisaria de uma camada de "ponte" (bridge) para alcançar esse SDK nativo — mais complexidade, mais pontos de falha e, muitas vezes, atraso em relação aos recursos mais novos do SDK. Em um hackathon, onde tempo e estabilidade contam, o acesso direto é a escolha pragmática.

Nota 3: "nativo" quer dizer usar diretamente as ferramentas e linguagens oficiais da plataforma (aqui, Kotlin + SDK Android), sem uma camada intermediária que traduza para outra tecnologia.

Na Figura 2, veja a arquitetura da solução óculos Bluetooth® app ⇔ ⇔ companion.

**Figura 2 – Arquitetura da solução: óculos, Bluetooth® e app companion Android®**

Fonte: autoria própria.
