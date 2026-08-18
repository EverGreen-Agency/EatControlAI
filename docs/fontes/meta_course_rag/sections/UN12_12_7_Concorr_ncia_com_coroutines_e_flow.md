---
unit: 12
unit_title: "Curso de Kotlin/Android"
section: "12.7"
section_title: "Concorrência com coroutines e flow"
source_file: "Un12_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN12_Curso_de_Kotlin_Android.md"
source_pages: [13, 14, 15, 16, 17, 18, 19, 20, 21, 22]
language: "pt-BR"
---

## 12.7 Concorrência com coroutines e flow

### 12.7.1 Objetivos de aprendizagem

- Ao final, você será capaz de explicar por que operações demoradas não podem bloquear a thread principal (UI) de um app Android e como coroutines evitam isso.

- Implementar funções suspend e iniciar coroutines com launch e async dentro de um scope adequado.

- Configurar o dispatcher correto (Main, IO, Default) para cada tipo de trabalho.

- Implementar e consumir um Flow que emite dados contínuos, como leituras de sensor ou frames de câmera.

- Aplicar boas práticas de cancelamento e ciclo de vida para evitar vazamento de coroutines.

### 12.7.2 Pré-requisitos

- Tópico 1.1 — Introdução ao Kotlin: funções, lambdas, classes e null safety.

- Android Studio instalado, com o mesmo ambiente de projeto Kotlin usado no 1.1.

- Dependência org.jetbrains.kotlinx:kotlinx-coroutines-android. Use a

versão estável mais recente — na data de escrita, a 1.11.0 (maio de 2026). Confira o vigente em https://github.com/Kotlin/kotlinx.coroutines/releases.

Atenção! Cuidado ao copiar versões da internet! A própria página de coroutines do Android Developers (developer.android.com/kotlin/coroutines), citada nas referências deste tópico, exibe no snippet de dependência a versão 1.3.9 — de 2020. O texto explicativo dela é excelente e continua válido; o número da versão, não. Regra geral do programa: leia o conceito na documentação, pegue o número no repositório oficial da biblioteca.

### 12.7.3 Conceito / fundamentação

#### 12.7.3.1 O problema: a thread principal não pode esperar

No Android existe uma única thread principal (main thread), responsável por desenhar a interface e responder aos toques do usuário. Para a UI parecer fluida, ela precisa completar cada ciclo de desenho em cerca de 16 ms (60 quadros por segundo). Se você executar nela algo demorado — esperar dados do Bluetooth, ler um arquivo, rodar a inferência de um modelo —, a tela congela. Se o bloqueio durar alguns segundos, o sistema exibe o diálogo de ANR (Application Not Responding), e o usuário provavelmente fecha o app.

Uma analogia: a main thread é o garçom do restaurante. Ele anota o pedido e o entrega à cozinha, mas não fica parado esperando o prato ficar pronto — volta a atender as mesas e só busca o prato quando a cozinha avisa. Coroutines são exatamente esse mecanismo de "avise-me quando estiver pronto" (Figura 3).

Na prática: no app companion dos óculos, tudo que importa chega de forma assíncrona: frames da câmera via Bluetooth, áudio do array de microfones, resultados da inferência dos modelos on-device. Nenhuma dessas operações pode rodar na main thread — ela deve ficar livre apenas para exibir resultados e reagir ao usuário.

**Figura 3 – Main thread bloqueada × coroutine em background**

Fonte: autoria própria.

#### 12.7.3.2 Coroutines: concorrência leve

Uma coroutine é uma tarefa leve gerenciada pelo runtime do Kotlin, não pelo sistema operacional. Criar uma thread é caro (cada uma reserva memória própria de pilha e envolve o sistema operacional); criar uma coroutine custa quase nada — dezenas de milhares podem rodar sobre um punhado de threads. O truque: em vez de bloquear a thread enquanto espera, a coroutine se suspende — libera a thread para outro trabalho e retoma do ponto exato onde parou quando o resultado chega.

#### 12.7.3.3 Funções suspend

Uma função marcada com suspend pode pausar sua execução sem bloquear a thread. É o bloco de construção básico:

▶ Código 1.2-01 — delay() suspende a coroutine; a thread fica livre nesse meio-tempo — código completo no notebook companion (seção 1.2.3.3).

Regra: uma função suspend só pode ser chamada de outra função suspend ou de dentro de uma coroutine. É o compilador que garante que você nunca "espere" fora de um contexto seguro.

Atenção! Thread.sleep(100) e delay(100) parecem iguais, mas o primeiro bloqueia a thread (e congela a UI, se for a main) e o segundo apenas suspende a coroutine. Confundir os dois é um dos erros mais comuns de quem está começando.

#### 12.7.3.4 launch e async: iniciando coroutines

Para entrar no "mundo suspend" a partir de código comum, você inicia uma coroutine com um builder:

- launch — dispara e esquece ("faça isso"); retorna um Job, que permite cancelar.

- async — dispara e devolve um resultado futuro ("calcule isso"); retorna um Deferred<T>, cujo valor você obtém com await().

▶ Código 1.2-02 — código completo no notebook companion (seção 1.2.3.4).

#### 12.7.3.5 Scopes: toda coroutine tem dono

Coroutines seguem o princípio de structured concurrency (concorrência estruturada): toda coroutine nasce dentro de um CoroutineScope, e quando o scope é cancelado, todas as coroutines dele são canceladas juntas. Isso evita "tarefas fantasma" rodando para sempre.

- No Android, você quase nunca cria scopes na mão — usa os que já vêm amarrados ao ciclo de vida dos componentes: viewModelScope e lifecycleScope (os componentes Android e seus ciclos de vida serão vistos no tópico 1.3).

Atenção! Evite GlobalScope. Coroutines nele vivem enquanto o processo do app viver — se uma delas coleta frames da câmera e a tela é fechada, ela continua coletando, gastando bateria e segurando referências (vazamento de memória)

#### 12.7.3.6 Dispatchers: em que thread o código roda

O dispatcher decide o conjunto de threads em que a coroutine executa (Tabela 2):

**Tabela 2 – Tipos de dispatchers e contextos de execução Dispatcher Para quê Exemplo no projeto dos óculos**

Dispatchers.Main Tocar na UI Exibir a transcrição na tela

Dispatchers.IO Esperas de rede, disco, sockets Ler o stream Bluetooth, salvar áudio

Dispatchers.Default Trabalho pesado de CPU Decodificar frame, pré-processar imagem para o modelo

Fonte: autoria própria.

Para trocar de dispatcher no meio de uma função, use withContext:

▶ Código 1.2-03 — código completo no notebook companion (seção 1.2.3.6).

Na prática: um pipeline típico do hackathon usa os três: IO para receber os bytes do frame pelo Bluetooth, Default para pré-processar e rodar a inferência (tópicos 1.6 e 1.7) e Main apenas para mostrar o resultado.

#### 12.7.3.7 Flow: fluxos contínuos de dados

Uma função suspend devolve um valor e termina. Mas câmera e microfone não entregam um valor: entregam uma sequência que não acaba — dezenas de frames por segundo, blocos contínuos de áudio, resultados parciais de transcrição (tópico 1.8). Para isso existe o Flow<T>: um fluxo assíncrono que emite vários valores ao longo do tempo.

Dois comportamentos definem um Flow:

- Ele é frio (cold): o código produtor só executa quando alguém chama collect. Sem coletor, nada acontece.

- Ele é sequencial e suspenso: cada emit respeita o ritmo do coletor, sem bloquear threads.

Entre produtor e coletor você encadeia operadores, no mesmo espírito das operações de coleção vistas no 1.1: map transforma, filter descarta, flowOn define o dispatcher do trecho produtor (Figura 4).

**Figura 4 – Pipeline de Flow e a fronteira do flowOn**

Fonte: autoria própria.

Nota 7: além do Flow frio, existem variações "quentes", como StateFlow, que guarda o último valor emitido — útil para representar estado de tela. Aqui o foco é o Flow básico; veja as referências para aprofundar.

#### 12.7.3.8 Cancelamento e ciclo de vida: evitando vazamentos

O cancelamento em coroutines é cooperativo e em cascata:

- Cancelar um Job (ou o scope inteiro) cancela todos os filhos.

- As funções suspend da biblioteca (delay, emit, withContext) verificam o cancelamento automaticamente; em loops de cálculo puro, verifique você mesmo com isActive ou ensureActive().

- O cancelamento é sinalizado por uma CancellationException — nunca a "engula" com um catch genérico sem relançá-la.

- Use try/finally (ou awaitClose, em callbackFlow) para liberar recursos: fechar o stream da câmera, parar o microfone.

O vazamento clássico: iniciar a coleta de um Flow em um scope que vive mais que a tela. O usuário sai da tela, mas a coroutine continua coletando frames — desperdiçando CPU, bateria e banda Bluetooth. A regra de ouro: colete fluxos ligados à UI em scopes cientes do ciclo de vida (lifecycleScope + repeatOnLifecycle, detalhados no tópico 1.3).

### 12.7.4 Na prática

Vamos construir, passo a passo, um Flow que simula um sensor emitindo leituras contínuas — o mesmo padrão que você usará com os frames dos óculos.

#### 12.7.4.1 Passo 0 — Dependência

▶ Código 1.2-04 – build.gradle.kts (módulo app) — código completo no notebook companion (seção 1.2.4.1).

#### 12.7.4.2 Passo 1 — O sensor simulado

▶ Código 1.2-05 – código completo no notebook companion (seção 1.2.4.2).

Repare: flow { } cria o Flow, emit publica um valor e delay marca o ritmo sem bloquear. Como o Flow é frio, nada disso executa ainda.

#### 12.7.4.3 Passo 2 — Coletando e transformando

▶ Código 1.2-06 – código completo no notebook companion (seção 1.2.4.3).

Execute como um programa Kotlin comum e você verá as leituras surgindo em tempo real. O take(20) cancela o produtor automaticamente após a 20ª emissão — o while (true) não roda para sempre.

Atenção! Erro comum: criar o Flow e "não acontecer nada". Quase sempre a causa é não ter chamado collect (ou tê-lo chamado fora de uma coroutine). Flow frio sem coletor é só a receita, não a comida.

#### 12.7.4.4 Passo 3 — Tirando o trabalho pesado da main thread

Se o map fizesse algo caro (decodificar imagem, pré-processar para a inferência), você moveria o trecho produtor para Default com flowOn:

▶ Código 1.2-07 – código completo no notebook companion (seção 1.2.4.4).

Regra do flowOn: ele afeta o que está acima dele na cadeia (upstream), nunca o collect.

#### 12.7.4.5 Passo 4 — Coletando com segurança na UI do Android

Em um app real, amarre a coleta ao ciclo de vida da tela para que ela comece e pare automaticamente:

▶ Código 1.2-08 – Dentro de uma Activity (Activity e lifecycle são detalhados no tópico 1.3) — código completo no notebook companion (seção 1.2.4.5).

Quando o usuário sai da tela, a coleta é cancelada; quando volta, recomeça. Zero vazamento.

#### 12.7.4.6 Passo 5 — E quando a fonte usa callbacks?

SDKs de hardware (câmera, microfone) costumam entregar dados por callback, não por Flow. O adaptador padrão é callbackFlow:

▶ Código 1.2-09 – código completo no notebook companion (seção 1.2.4.6).

O awaitClose é obrigatório: é ele que garante que, quando o coletor cancelar (tela fechada), o listener será removido e o hardware liberado.

Na prática: este é exatamente o formato do streaming dos óculos. Os frames da câmera e os blocos de áudio chegarão ao app companion por APIs assíncronas (tópicos 2.5 e 2.6), e você os tratará como Flows — o pipeline map/filter/flowOn que você montou aqui para o sensor simulado é o mesmo que alimentará a inferência on-device.

#### 12.7.4.7 Erros comuns

Os erros mais comuns estão descritos na Tabela 3.

**Tabela 3 – Erros comuns no uso de coroutines e Flows Sintoma Causa provável Correção**

UI congela ou ANR

withContext(Dispatchers.D efault/IO) ou flowOn

Trabalho pesado ou Thread.sleep na main thread

"Suspend function should be called only from a coroutine..." Envolver em scope.launch { } Chamar suspend fora de coroutine

Flow "não emite" Falta de collect (Flow é frio) Coletar dentro de uma coroutine

Coleta contínua com a tela fechada lifecycleScope + repeatOnLifecycle Scope errado (GlobalScope ou scope manual)

Fonte: autoria própria.

### 12.7.5 Resumo / cheatsheet

- A main thread só desenha UI e reage ao usuário; qualquer espera ou cálculo pesado vai para coroutines.

- suspend pausa sem bloquear; delay ≠ Thread.sleep.

- launch = disparar sem resultado (retorna Job); async/await = disparar com resultado (Deferred<T>).

- Toda coroutine vive em um scope; no Android, prefira viewModelScope/lifecycleScope; evite GlobalScope.

- Dispatchers: Main = UI · IO = rede/disco/Bluetooth · Default = CPU pesada.

- Flow é frio: só produz quando coletado; map/filter transformam o fluxo no caminho.

- flowOn muda o dispatcher do trecho acima dele (upstream); o collect roda no contexto do chamador.

- Cancelamento é cooperativo e em cascata: cancele pelo scope e use awaitClose/finally para liberar câmera e microfone.

### 12.7.6 Referências

- COROUTINES / KOTLIN DOCS. Disponível em: https://kotlinlang.org/docs/coroutines-overview.html. Visão geral oficial de coroutines: suspend, builders e structured concurrency.

- ASYNCHRONOUS FLOW / KOTLIN DOCS. Disponível em: https://kotlinlang.org/docs/flow.html. Guia completo de Flow: cold streams, operadores, flowOn e cancelamento.

- KOTLIN COROUTINES ON ANDROID / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/kotlin/coroutines. Coroutines no contexto Android: scopes, dispatchers e integração com o framework.

- KOTLIN FLOWS ON ANDROID / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/kotlin/flow. Flow em apps Android, incluindo coleta segura com o ciclo de vida e StateFlow.

- BEST PRACTICES FOR COROUTINES IN ANDROID / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/kotlin/coroutines/coroutines-best-practices. Regras práticas: injetar dispatchers, evitar GlobalScope e tratar cancelamento.

- KOTLINX.COROUTINES / GITHUB (JETBRAINS). Disponível em: https://github.com/Kotlin/kotlinx.coroutines. Repositório oficial, com changelog e a versão estável atual da biblioteca.

#### 12.7.6.1 Para ir além

USE KOTLIN COROUTINES WITH LIFECYCLE-AWARE COMPONENTS / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/topic/libraries/architecture/coroutines. Detalha lifecycleScope, viewModelScope e repeatOnLifecycle.

COROUTINES GUIDE / KOTLIN DOCS. Disponível em: https://kotlinlang.org/docs/coroutines-guide.html. Índice do guia aprofundado, do básico a channels e tratamento de exceções.
