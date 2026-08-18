---
unit: 13
unit_title: "Curso do Meta SDK (DAT)"
section: "13.1"
section_title: "Apresentação do hardware e do SDK (DAT)"
source_file: "Un13_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN13_Curso_do_Meta_SDK_DAT.md"
source_pages: [2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
language: "pt-BR"
---

## 13.1 Apresentação do hardware e do SDK (DAT)

### 13.1.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Descrever o hardware dos óculos Ray-Ban Meta (câmera, microfones, alto-falantes) e suas limitações, em especial a ausência de display.

- Explicar as implicações de design de um dispositivo sem tela para as soluções do hackathon (interação por visão, voz e áudio).

- Explicar o que é o Meta Wearables Device Access Toolkit (DAT), o que ele permite e o que ele não permite, e o que significa ele estar em developer preview.

- Localizar as fontes oficiais de estudo — documentação, API reference, repositório no GitHub, sample apps e fórum — e montar seu próprio roteiro de consulta.

### 13.1.2 Pré-requisitos

- Módulo 1 concluído, em especial 1.3 (Fundamentos de Android) e 1.4 (Permissões em runtime) — o DAT é um SDK Android e seu modelo de permissões vai parecer familiar.

- Noções de STT/TTS (tópico 1.8), úteis para entender por que a interação com os óculos é centrada em voz e áudio.

- Navegador e conta no GitHub para explorar o repositório oficial. O Android Studio só será necessário no tópico 2.3 (Setup).

### 13.1.3 Conceito / fundamentação

#### 13.1.3.1 O hardware: Ray-Ban Meta

Os óculos usados em todo o programa são os Ray-Ban Meta — smart glasses com a estética clássica dos Ray-Ban (a armação Wayfarer é a mais conhecida), desenvolvidos pela Meta em parceria com a EssilorLuxottica. À primeira vista parecem óculos comuns; a diferença está nos sensores embutidos na armação.

FOTO: óculZSos Ray-Ban Meta com anotações apontando a câmera na haste esquerda, o LED de captura, os microfones e os alto-falantes nas hastes]

As características de cada componente do Ray-Ban Meta estão descritas na

**Tabela 18.**

**Tabela 18 – Características técnicas dos componentes dos óculos Ray-Ban Meta Componente Especificação O que significa para você**

Câmera Ultra-wide de 12 MP

Fotos e streaming de vídeo em primeira pessoa: o app "vê" o que o usuário vê

Microfones Array de 5 microfones

Captura de voz de qualidade mesmo em ambiente externo; entrada natural do usuário

Alto-falantes Open-ear (abertos, direcionados ao ouvido)

Única saída de informação para o usuário; não bloqueiam o som ambiente

Display Não possui Nenhuma saída visual — toda resposta ao usuário é por áudio

Conectividade Bluetooth, pareado ao smartphone via app Meta AI Os óculos não funcionam sozinhos: dependem do telefone

Fonte: autoria própria.

Nota 21: existem outros modelos na família de AI glasses da Meta com suporte no SDK (Ray-Ban Meta Gen 1 e Gen 2, Oakley Meta HSTN, Oakley Meta Vanguard e Meta Ray-Ban Display — este último com display). O hardware do programa é o Ray-Ban Meta sem display, e é para ele que você deve projetar.

#### 13.1.3.2 Limitações que definem o jogo

Três limitações do hardware moldam qualquer solução que você vá construir:

1. Sem display — saída só por áudio. Não há tela, ícone, notificação visual ou realidade aumentada. Se o seu app precisa comunicar algo ao usuário, isso sai pelos alto-falantes: fala sintetizada (TTS, visto no 1.8), sons de confirmação ou música. 2. Dependência de pareamento. Os óculos são um periférico do smartphone: pareiam via Bluetooth através do app Meta AI, e é o app companion Android — rodando no telefone — que executa a lógica e os modelos de IA. Sem telefone por perto, sem app companion, não há solução. 3. Bateria e banda limitadas. Óculos são um dispositivo pequeno: bateria modesta e um link Bluetooth com largura de banda restrita. Isso explica decisões do SDK, como o áudio de microfone chegar ao app como mono a 8 kHz pelo perfil Bluetooth HFP (detalhes no tópico 2.6), e reforça o que você viu no 1.5 sobre consumo de energia.

**Figura 17 – Arquitetura Padrão do programa**

Fonte: autoria própria.

#### 13.1.3.3 Implicações de design: pensar em visão, voz e áudio

A ausência de display não é um detalhe — é a decisão de design mais importante do hackathon. Uma analogia: projetar para os Ray-Ban Meta é como projetar um assistente por telefone que enxerga. O usuário não olha para uma tela; ele mostra (câmera), fala (microfones) e ouve (alto-falantes).

Na prática, isso significa que toda solução do programa segue o mesmo padrão de interação (Figura 18):

- Entrada por visão: a câmera captura o que o usuário está vendo (uma foto ou um stream de vídeo), e a visão computacional (tópico 1.7) roda no app companion.

- Entrada por voz: o usuário fala; o áudio chega ao telefone e passa por STT (tópico 1.8).

- Saída por áudio: o resultado volta como fala (TTS) ou sinais sonoros. Respostas precisam ser curtas e objetivas — ninguém quer ouvir três parágrafos.

- Sem confirmação visual: estados como "estou processando" ou "não entendi" precisam de feedback sonoro explícito, senão o usuário fica falando com o vazio.

Na prática: ao avaliar sua ideia de hackathon, faça o teste dos três canais: dá para resolver com o que a câmera vê, o que o usuário fala e o que ele ouve de volta? Se a ideia exige mostrar um mapa, um gráfico ou um texto longo, ela não serve para este hardware — redesenhe a saída como áudio ou descarte.

**Figura 18 – Diferentes tipos de entrada e saída para interação usuário-óculos**

Fonte: autoria própria.

#### 13.1.3.4 O SDK: Meta Wearables Device Access Toolkit (DAT)

O Meta Wearables Device Access Toolkit (DAT) é o SDK que permite que um app mobile (Android ou iOS; no programa, Android) acesse os sensores dos óculos. Nas palavras da documentação oficial: ele permite "conectar de forma confiável aos AI glasses da Meta e aproveitar capacidades como streaming de vídeo e captura de foto" para construir experiências hands-free dentro do seu próprio aplicativo.

O DAT foi lançado em developer preview — anunciado no Meta Connect de setembro de 2025. Developer preview significa, segundo o FAQ oficial: você pode baixar o SDK, construir e testar (com hardware em Developer Mode ou sem hardware, via Mock Device Kit — tópico 2.7), e compartilhar com usuários de teste por release channels; mas não pode publicar para usuários finais ainda.

Atenção! Por estar em preview, capacidades, APIs, versões e condições de acesso podem mudar sem aviso. Tudo neste módulo deve ser conferido contra a documentação oficial antes de usar — os links estão na seção de Referências. Além disso, o acesso completo ao toolkit (incluindo o Wearables Developer Center) está disponível apenas nos países com suporte oficial aos AI glasses; consulte o FAQ para a lista atual.

#### 13.1.3.5 O que o DAT permite — e o que não permite

Veja, na Tabela 19, as funções permitidas pelo DAT.

**Tabela 19 – Funcionalidades e limitações do Device Access Toolkit ✅ Permite ❌ Não permite**

"Hey Meta" e Meta AI: o assistente da Meta e seu wake word não fazem parte do toolkit

Streaming de vídeo da câmera dos óculos para o seu app (com seleção de resolução e frame rate)

Captura de foto durante o stream Rodar código nos óculos: seu app roda no telefone; os óculos são sensores e alto-falantes

Publicar para usuários finais durante o preview

Registro do app junto ao dispositivo do usuário e gestão de permissões (via deeplink para o app Meta AI)

Sessões com o dispositivo: o usuário pausa, retoma ou encerra tocando nos óculos, tirando-os ou fechando as hastes Mais de uma sessão simultânea no mesmo dispositivo; e alguns recursos nativos dos óculos ficam indisponíveis durante a sessão

Testes sem hardware, com o Mock Device Kit —

Fonte: autoria própria.

Um detalhe importante do desenho do SDK: microfones e alto-falantes não são acessados pelo DAT, e sim pelos perfis Bluetooth padrão do Android (HFP para captura de voz, por exemplo). O toolkit cuida de câmera, registro, permissões e sessão; o áudio passa pelo caminho Bluetooth que qualquer headset usa. Você verá isso em detalhe no tópico 2.6.

Nota 22: no Android, o SDK é distribuído como artefatos Maven (via GitHub Packages) — os principais são mwdat-core (registro, dispositivos, permissões, sessão), mwdat-camera (câmera) e mwdat-mockdevice (dispositivo simulado). Há também um componente de display (MWDATDisplay), que só se aplica ao Meta Ray-Ban Display — fora do escopo do programa. A arquitetura desses componentes é o assunto do tópico 2.2, e a instalação, do 2.3. Nota: a versão-alvo do material é a 0.8.0 (resolve do GitHub Packages). Como o SDK está em developer preview e a versão muda com frequência, confira sempre a versão vigente no GitHub Packages e na API reference antes de fixar dependências.

### 13.1.4 Na prática

Este tópico não tem setup — o objetivo prático aqui é montar seu mapa de estudo e aprender a se orientar nas fontes oficiais. Reserve ~40 minutos e siga o roteiro.

#### 13.1.4.1 Passo 1 — Explore o Wearables Developer Center

Abra <https://wearables.developer.meta.com/>. Este é o hub oficial: dele saem a documentação, a API reference e o cadastro de conta/organização. Repare no fluxo de 4 passos que a própria Meta propõe (conta → Developer Mode → registro de projeto → release channels) — ele é o esqueleto dos tópicos 2.3 e 2.4. Não precisa criar conta agora; isso será feito no 2.3.

#### 13.1.4.2 Passo 2 — Faça um tour guiado pela documentação

Em <https://wearables.developer.meta.com/docs>, siga esta ordem de leitura (só leitura — nada de codar ainda):

1. Setup (docs/develop/dat/getting-started-toolkit) — requisitos de sistema (Android 10+, Android Studio) e como habilitar o Developer Mode no app Meta AI.

2. Integration overview (docs/develop/dat/build-overview) — a página mais importante do módulo: descreve o ciclo registro → permissões → sessão e os componentes do SDK.

3. Session lifecycle, Permissions and registration, Microphones and speakers — bata o olho nos títulos; são os assuntos dos tópicos 2.4 e 2.6.

4. Mock Device Kit basics — assunto do 2.7.

Nota 23: cada página da documentação tem um link para sua versão em Markdown puro (.../docs-markdown/...) — útil para colar em ferramentas de IA ou ler offline.

#### 13.1.4.3 Passo 3 — Explore o repositório oficial no GitHub

Abra <https://github.com/facebook/meta-wearables-dat-android> e localize:

- README — visão geral e instruções de dependência Gradle (você as usará no 2.3).

- samples/ — os sample apps oficiais, o melhor código de referência disponível durante o preview.

- CHANGELOG.md — em um SDK em preview, ler o changelog a cada versão não é opcional: é onde as quebras de API aparecem.

- Discussions — o fórum oficial da comunidade. Durante o preview, é o principal lugar onde "exemplos já existentes na internet" e soluções de problemas se concentram; procure lá antes de abrir issue. Só para reconhecer o formato (a instalação completa é assunto do 2.3), a declaração dos artefatos no catálogo de versões (libs.versions.toml) e no build.gradle.kts — em Kotlin DSL, como visto no 1.1 — tem esta cara:

▶ Código 2.1-01 – libs.versions.toml — trecho do README oficial (não configure ainda; ver tópico 2 — código completo no notebook companion (seção 2.1.4.3).

▶ Código 2.1-02 – build.gradle.kts (módulo do app) — trecho do README oficial — código completo no notebook companion (seção 2.1.4.3).

#### 13.1.4.4 Passo 4 — Guarde os atalhos de consulta rápida

- API reference (Android): <https://wearables.developer.meta.com/docs/reference/> — a referência é versionada; navegue sempre na versão que seu projeto usa.

- FAQ oficial: <https://developers.meta.com/wearables/faq/> — status do preview, dispositivos suportados e países.

- Recursos para ferramentas de IA: o repositório traz plugins/skills para Claude Code, Cursor, Copilot e afins, um endpoint llms.txt e um servidor MCP de documentação — vale a pena conectar sua ferramenta de IA a eles no hackathon (ver seção AI-Assisted Development na doc e no README).

Atenção! Erro comum: achar que o app "roda nos óculos" e sair procurando como instalar um APK neles, ou procurar no DAT uma API do "Hey Meta". Nenhum dos dois existe: seu código roda no app companion Android, os óculos entram como câmera/microfone/alto-falante, e o acionamento da sua solução é responsabilidade do seu app — não do assistente da Meta. Na prática: monte hoje um documento de time com esses links fixados (docs, API reference na versão certa, repo, Discussions, FAQ). No dia do hackathon, os minutos gastos procurando "onde estava aquela página" fazem falta — e, num SDK em preview, a página de ontem pode ter mudado.

### 13.1.5 Resumo / cheatsheet

- Ray-Ban Meta: câmera ultra-wide 12 MP + array de 5 microfones + alto-falantes open-ear; sem display — toda saída é áudio.

- Os óculos dependem de pareamento Bluetooth com o smartphone (via app Meta AI); a lógica e a IA rodam no app companion Android.

- Design das soluções: entrada por visão (câmera) e voz (mics), saída por áudio (TTS/sons); sempre dar feedback sonoro de estado.

- DAT = SDK que dá ao seu app mobile acesso aos sensores dos óculos: streaming de vídeo, captura de foto, registro, permissões e sessões.

- Microfones e alto-falantes não passam pelo DAT: usam os perfis Bluetooth padrão do Android (detalhes no 2.6).

- "Hey Meta" e Meta AI não fazem parte do toolkit; e seu código não roda nos óculos.

- Developer preview: pode construir, testar (glasses em Developer Mode ou Mock Device Kit) e distribuir a testadores via release channels — mas não publicar; tudo pode mudar, confira sempre a doc oficial.

- Mapa de estudo: Developer Center (hub) → docs (guias) → API reference (versionada) → GitHub facebook/meta-wearables-dat-android (README, samples/, CHANGELOG, Discussions) → FAQ.

### 13.1.6 Referências

- META WEARABLES DEVELOPER CENTER / META. Disponível em: <https://wearables.developer.meta.com/>. Hub oficial: criação de conta/organização e ponto de partida para docs e API reference.

- DOCUMENTAÇÃO DO DEVICE ACCESS TOOLKIT / META. Disponível em: <https://wearables.developer.meta.com/docs>. Guias de setup, integração, sessões, permissões, áudio e Mock Device Kit.

- SETUP — DEVICE ACCESS TOOLKIT / META. Disponível em: <https://wearables.developer.meta.com/docs/develop/dat/getting-started-to olkit/>. Requisitos de sistema, dispositivos suportados e habilitação do Developer Mode.

- INTEGRATION OVERVIEW — DEVICE ACCESS TOOLKIT / META. Disponível em: <https://wearables.developer.meta.com/docs/develop/dat/build-overview/>. Ciclo registro → permissões → sessão e os componentes do SDK (core, camera, display).

- API REFERENCE — WEARABLES / META. Disponível em: <https://wearables.developer.meta.com/docs/reference/>. Referência versionada das APIs Android e iOS do toolkit.

- META WEARABLES DEVICE ACCESS TOOLKIT FOR ANDROID / FACEBOOK (GITHUB). Disponível em: <https://github.com/facebook/meta-wearables-dat-android>. SDK via GitHub Packages, sample apps, CHANGELOG e fórum de Discussions.

- FREQUENTLY ASKED QUESTIONS — WEARABLES / META. Disponível em: <https://developers.meta.com/wearables/faq/>. Significado do developer preview, dispositivos suportados e disponibilidade por país.

#### 13.1.6.1 Para ir além

INTRODUCING THE META WEARABLES DEVICE ACCESS TOOLKIT / META. Disponível em:

<https://developers.meta.com/blog/introducing-meta-wearables-device-access -toolkit/>. Post de lançamento, com a visão da Meta para casos de uso do toolkit.

AI-ASSISTED DEVELOPMENT — DEVICE ACCESS TOOLKIT / META. Disponível em: <https://wearables.developer.meta.com/docs/develop/dat/ai-assisted/>. Como conectar Claude Code, Cursor, Copilot e servidores MCP à documentação do DAT.
