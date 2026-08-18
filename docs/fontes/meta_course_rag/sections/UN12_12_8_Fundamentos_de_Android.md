---
unit: 12
unit_title: "Curso de Kotlin/Android"
section: "12.8"
section_title: "Fundamentos de Android"
source_file: "Un12_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN12_Curso_de_Kotlin_Android.md"
source_pages: [22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32]
language: "pt-BR"
---

## 12.8 Fundamentos de Android

### 12.8.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar a estrutura de um projeto Android com Gradle (Kotlin DSL), o papel dos módulos e do AndroidManifest.xml.

- Descrever o ciclo de vida de uma Activity e justificar por que ele importa quando o app mantém uma sessão com os óculos e recebe stream de câmera.

- Implementar um ViewModel que expõe o estado da tela via StateFlow, sobrevivendo a mudanças de configuração.

- Construir uma UI mínima em Jetpack Compose com um botão e um texto que reagem a estado.

- Comparar onde cada tipo de estado deve viver (Activity vs. ViewModel) no app companion dos óculos.

### 12.8.2 Pré-requisitos

- Tópico 1.1 — Introdução ao Kotlin: sintaxe, data class, lambdas e referências de função.

- Tópico 1.2 — Coroutines e Flow: viewModelScope, launch, o que é um Flow e como coletá-lo.

- Android Studio instalado, com um emulador ou dispositivo físico Android configurado.

### 12.8.3 Conceito / fundamentação

#### 12.8.3.1 Por que você precisa de um app Android

Os Ray-Ban Meta não têm display nem rodam seus modelos: eles capturam (câmera ultra-wide de 12 MP, array de 5 microfones) e reproduzem áudio (alto-falantes open-ear). Todo o "cérebro" da sua solução — os modelos de IA on-device, a lógica do agente, a UI de configuração — roda no app companion Android, conectado aos óculos via Bluetooth. Este tópico monta a estrutura mínima desse app: um projeto que compila, uma tela que reage a estado e uma base sólida para hospedar o modelo e o agente nos tópicos seguintes.

#### 12.8.3.2 Anatomia de um projeto Android (Gradle + Kotlin DSL)

Um projeto Android é, na essência, um conjunto de módulos orquestrados pelo Gradle, o sistema de build. Cada módulo é uma unidade compilável: o módulo

:app gera o APK; módulos de biblioteca (ex.: :core, :ml) geram código reutilizável. Para o hackathon, um único módulo :app é suficiente — só crie módulos extras se o time crescer e precisar isolar responsabilidades.

Os scripts de build são escritos em Kotlin DSL (arquivos .gradle.kts) — a mesma linguagem do seu código, com autocompletar e checagem de tipos, em vez do Groovy legado (Tabela 4).

**Tabela 4 – Principais arquivos de configuração de um projeto Android Arquivo Papel**

settings.gradle.kts Declara quais módulos existem no projeto e de onde baixar dependências (repositórios).

build.gradle.kts (raiz) Configurações comuns a todos os módulos; declara plugins sem aplicá-los.

app/build.gradle.kts Configura o módulo :app: namespace, minSdk/targetSdk, e o bloco dependencies.

gradle/libs.versions.toml Version catalog: centraliza nomes e versões de bibliotecas em um só lugar.

app/src/main/AndroidManifest.xml

"Certidão de nascimento" do app: componentes, permissões e requisitos de hardware.

Fonte: autoria própria.

Um recorte típico do app/build.gradle.kts:

▶ Código 1.3-01 – código completo no notebook companion (seção 1.3.3.2).

Nota 8: os projetos novos criados pelo Android Studio já vêm com Kotlin DSL e version catalog por padrão — você não precisa migrar nada.

#### 12.8.3.3 AndroidManifest: o contrato com o sistema

O AndroidManifest.xml declara ao Android o que seu app é e do que precisa: quais componentes existem (Activities, Services...), quais permissões o app pode pedir e qual Activity abre quando o usuário toca no ícone. É nele que, no tópico 1.4, você declarará permissões como microfone e Bluetooth.

▶ Código 1.3-02 – código completo no notebook companion (seção 1.3.3.3).

Atenção: Declarar uma permissão no Manifest não a concede — permissões sensíveis (câmera, microfone) exigem pedido em runtime, assunto do tópico 1.4.

#### 12.8.3.4 Ciclo de vida da Activity

Uma Activity é uma tela do app, e o Android cria e destrói Activities conforme o usuário navega, atende uma ligação ou gira o aparelho. O sistema avisa cada transição chamando callbacks que você pode sobrescrever:

- onCreate() — a Activity nasce; configure a UI aqui (setContent no Compose).

- onStart() — a tela ficou visível.

- onResume() — a tela está em primeiro plano, recebendo interação.

- onPause() — perdeu o primeiro plano (ex.: diálogo por cima); pode ainda estar visível.

- onStop() — deixou de ser visível (usuário foi para outro app).

- onDestroy() — a Activity morre: o usuário fechou a tela ou o sistema a recriou (Figura 5).

**Figura 5 – Ciclo de vida da Activity**

Fonte: autoria própria.

O detalhe que mais derruba iniciantes: uma mudança de configuração (girar a tela, trocar idioma, redimensionar em multi-window) destrói e recria a Activity. Qualquer estado guardado em propriedades da Activity — um contador, um texto, uma conexão — se perde.

Na prática: no app companion, o ciclo de vida dita o consumo de recursos caros. O preview da câmera dos óculos chegando por Bluetooth (tópico 2.5) não deve continuar sendo renderizado com o app invisível: pause-o em onStop() e retome em onStart(). Ignorar isso drena a bateria dos óculos e do celular — e bateria é um dos recursos mais escassos do hackathon. O que pode (e o que não pode) continuar em segundo plano é assunto do tópico 1.5.

#### 12.8.3.5 ViewModel: estado que sobrevive à recriação

Se a Activity morre na rotação, onde guardar o estado da tela? No ViewModel, componente do Jetpack criado exatamente para isso: ele é retido pelo sistema durante mudanças de configuração e só é destruído quando a tela sai

definitivamente da navegação. A Activity recriada se reconecta ao mesmo ViewModel e encontra o estado intacto.

O ViewModel também é o dono do viewModelScope (visto no 1.2): coroutines lançadas nele são canceladas automaticamente quando o ViewModel morre — sem vazamento.

Pense na divisão assim: a Activity é a moldura (nasce e morre ao sabor do sistema); o ViewModel é o quadro (o conteúdo que persiste enquanto a tela existir logicamente).

#### 12.8.3.6 StateFlow: a fonte única do estado da tela

O ViewModel guarda o estado, mas a UI precisa reagir quando ele muda. A ponte é o StateFlow (visto no 1.2): um Flow que sempre tem um valor atual e emite cada novo valor aos coletores. O padrão consagrado:

1. O estado da tela é uma data class imutável (UiState). 2. O ViewModel mantém um MutableStateFlow privado — só ele escreve. 3. A UI recebe uma versão somente leitura (StateFlow) e apenas observa. 4. As interações do usuário viram chamadas de função no ViewModel, nunca escrita direta no estado. Esse desenho é o unidirectional data flow (UDF, fluxo de dados unidirecional): estado desce, eventos sobem (Figura 6).

**Figura 6 – Fluxo de dados unidirecional (UDF)**

Fonte: autoria própria.

#### 12.8.3.7 Jetpack Compose: UI declarativa mínima

Jetpack Compose é o toolkit moderno de UI do Android. Em vez de manipular views imperativamente ("pegue o TextView e troque o texto"), você descreve a tela como função do estado: UI = f(state). Quando o estado muda, o Compose reexecuta as funções afetadas — a recomposição — e a tela se atualiza sozinha.

Funções de UI são marcadas com @Composable e montadas por composição: Column, Text, Button etc. Para o app companion, isso basta: a experiência do usuário final está na voz e no áudio dos óculos; a tela do celular é só painel de controle — conectar, ver status, ajustar.

Na prática: resista à tentação de investir tempo de hackathon em UI elaborada. Os jurados verão a demo pelos óculos (voz e áudio), não pelo celular. Uma tela com status da conexão e um botão já cumpre o papel.

### 12.8.4 Na prática

Vamos construir o esqueleto do app companion: uma tela com um texto de status e um botão "Conectar" que simula o handshake com os óculos. A conexão real via Meta SDK será vista no tópico 2.4 — aqui o foco é a estrutura.

#### 12.8.4.1 Passo 1 — Criar o projeto

No Android Studio: New Project → Empty Activity. Apesar do nome genérico, este é o template Compose — ele já vem com as dependências do Compose e o Material Design configurados, e a documentação oficial o indica como ponto de partida recomendado para qualquer projeto novo. Nomeie Companion, pacote com.exemplo.companion, linguagem Kotlin (é a única opção para Compose). Em Minimum SDK, escolha API 29 (Android 10) — é o piso exigido pelo SDK da Meta, que você vai adicionar no tópico 2.3; escolher menos agora significa refazer depois. O wizard gera a estrutura Gradle Kotlin DSL + version catalog da seção anterior.

Nota 9: se você encontrar tutoriais mandando escolher "Empty Compose Activity", é o nome antigo do mesmo template — foi renomeado para "Empty Activity". Os templates com "Views" no nome (ex.: "Empty Views Activity") são os de UI em XML, o caminho legado, que não usaremos. Atenção, usuários de macOS! A documentação oficial recomenda salvar o projeto em /Users/seu_usuario/AndroidStudioProjects. Pastas como Documentos, Desktop e Downloads são protegidas pelo mecanismo TCC do macOS, e os arquivos do projeto podem não aparecer corretamente no Android Studio sem conceder permissão especial ao IDE.

#### 12.8.4.2 Passo 2 — Conferir dependências

O template já inclui Compose e Activity. Adicione (via version catalog) o suporte a ViewModel no Compose e à coleta lifecycle-aware:

▶ Código 1.3-03 – app/build.gradle.kts — bloco dependencies — código completo no notebook companion (seção 1.3.4.2).

#### 12.8.4.3 Passo 3 — Modelar o estado da tela

▶ Código 1.3-04 – UiState.kt — o estado da tela em uma data class imutável — código completo no notebook companion (seção 1.3.4.3).

#### 12.8.4.4 Passo 4 — Escrever o ViewModel

▶ Código 1.3-05 – MainViewModel.kt — código completo no notebook companion (seção 1.3.4.4).

Nota 10: update { } aplica a mudança de forma atômica: recebe o estado atual e devolve uma cópia modificada (copy da data class). Nunca mute o objeto de estado — crie um novo.

#### 12.8.4.5 Passo 5 — Construir a UI em Compose

▶ Código 1.3-06 – MainScreen.kt — código completo no notebook companion (seção 1.3.4.5).

#### 12.8.4.6 Passo 6 — Ligar a Activity

▶ Código 1.3-07 – MainActivity.kt — código completo no notebook companion (seção 1.3.4.6).

#### 12.8.4.7 Passo 7 — Rodar e provar o ciclo de vida

Execute no emulador. Toque em "Conectar aos óculos", espere o status virar "Conectado" e gire a tela: a Activity é destruída e recriada, mas o texto continua "Conectado" — o UiState mora no ViewModel, que sobreviveu. Guarde esse teste: é a prova de que seu estado está no lugar certo.

Na prática: esse esqueleto é literalmente a base do seu app do hackathon. Nos tópicos do Módulo 2, o delay simulado dará lugar à sessão real com os óculos (2.4) — e é o ViewModel que vai segurar essa sessão, imune às rotações de tela durante a demo.

#### 12.8.4.8 Erro comum 1 — Expor o MutableStateFlow

val uiState = MutableStateFlow(UiState()) // público e mutável: ruim!

**Código 1.3-08 · também no notebook companion, seção 1.3.4.8 Qualquer Composable poderia escrever no estado, quebrando o UDF e tornando bugs impossíveis de rastrear. Correção: campo private val _uiState + exposição via asStateFlow(), como no Passo 4.**

#### 12.8.4.9 Erro comum 2 — Coletar sem consciência de lifecycle

Usar collectAsState() (sem WithLifecycle) mantém a coleta ativa mesmo com o app em segundo plano. Com um contador de status é inofensivo; com frames de câmera chegando dos óculos, é desperdício de CPU e bateria. Correção: prefira collectAsStateWithLifecycle(), que segue as regras de onStart/onStop automaticamente.

Atenção! Se collectAsStateWithLifecycle não for resolvido, falta a dependência androidx.lifecycle:lifecycle-runtime-compose do Passo 2 — o erro de import confunde porque collectAsState (de outro pacote) continua disponível.

### 12.8.5 Resumo / cheatsheet

- Módulo :app + Gradle Kotlin DSL (.gradle.kts) + version catalog (libs.versions.toml): estrutura padrão de projeto; dependências vivem em app/build.gradle.kts.

- AndroidManifest.xml declara componentes, permissões e a Activity de entrada (MAIN/LAUNCHER) — declarar permissão não é concedê-la (tópico 1.4).

- Ciclo de vida: onCreate → onStart → onResume (subida) e onPause → onStop → onDestroy (descida); rotação destrói e recria a Activity.

- Recursos caros (preview de câmera, renderização) devem pausar em onStop() e retomar em onStart() — bateria é crítica com os óculos.

- ViewModel sobrevive a mudanças de configuração e é o dono do estado da tela e do viewModelScope.

- Padrão de estado: data class UiState imutável + MutableStateFlow privado + StateFlow público + update { it.copy(...) }.

- UDF: estado desce (ViewModel → UI), eventos sobem (UI → funções do ViewModel).

- No Compose, UI = f(state): colete com collectAsStateWithLifecycle() e a recomposição atualiza a tela sozinha.

### 12.8.6 Referências

- ACTIVITY LIFECYCLE / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/guide/components/activities/activity-lifecycle. Diagrama oficial e descrição de cada callback do ciclo de vida.

- VIEWMODEL OVERVIEW / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/topic/libraries/architecture/viewmodel.

Quando usar ViewModel e como ele sobrevive a mudanças de configuração.

- STATEFLOW AND SHAREDFLOW / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/kotlin/flow/stateflow-and-sharedflow. Padrões de exposição de estado com StateFlow, incluindo coleta lifecycle-aware.

- JETPACK COMPOSE / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/jetpack/compose. Documentação central do Compose: composables, estado e recomposição.

- APP MANIFEST OVERVIEW / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/guide/topics/manifest/manifest-intro. Estrutura e elementos do AndroidManifest.xml.

- CONFIGURE YOUR BUILD / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/build. Gradle no Android: módulos, Kotlin DSL e version catalogs.

#### 12.8.6.1 Para ir além

GUIDE TO APP ARCHITECTURE / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/topic/architecture. A arquitetura recomendada completa (UI layer, data layer, UDF) — o "mapa" onde este tópico se encaixa.

STATE IN COMPOSE / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/jetpack/compose/state. Aprofundamento em estado e recomposição, incluindo state hoisting.
