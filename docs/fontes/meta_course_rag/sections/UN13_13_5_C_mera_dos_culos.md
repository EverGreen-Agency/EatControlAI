---
unit: 13
unit_title: "Curso do Meta SDK (DAT)"
section: "13.5"
section_title: "Câmera dos óculos"
source_file: "Un13_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN13_Curso_do_Meta_SDK_DAT.md"
source_pages: [38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48]
language: "pt-BR"
---

## 13.5 Câmera dos óculos

### 13.5.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar a relação entre DeviceSession e Stream no Meta Wearables Device Access Toolkit (DAT) e o papel de cada um no acesso à câmera.

- Implementar a abertura de um stream de vídeo dos óculos no app companion, coletando frames e estados via Flow.

- Implementar a captura de foto durante um streaming ativo e tratar os formatos de retorno (PhotoData.HEIC e PhotoData.Bitmap).

- Configurar resolução (VideoQuality) e frame rate no StreamConfiguration, escolhendo valores adequados ao caso de uso.

- Comparar os trade-offs de qualidade impostos pela banda do Bluetooth Classic e antecipar o comportamento do rebaixamento automático de qualidade.

### 13.5.2 Pré-requisitos

- Tópico 2.3 (Setup): projeto Android com os artefatos do DAT configurados no Gradle (mwdat-core, mwdat-camera) e manifest preparado.

- Tópico 2.4 (Registro e sessão): app registrado no Meta AI, permissão Permission.CAMERA do wearable concedida e conceitos de estado de sessão.

- Tópico 1.2 (Coroutines e Flow): Flow, StateFlow e collect são usados em todo o código deste tópico.

- Android Studio com o SDK do DAT versão 0.8.0 (Verificar: o toolkit está em developer preview e a versão muda com frequência; confira a mais recente em GitHub Packages do repositório oficial).

### 13.5.3 Conceito / fundamentação

#### 13.5.3.1 Como o vídeo chega ao seu app

A câmera ultra-wide dos óculos não é acessada como uma câmera do Android (nada de CameraX/Camera2 aqui). Quem fala com o hardware é o app Meta AI, e o seu app recebe os dados por meio do DAT: os óculos capturam o vídeo, enviam pelo Bluetooth Classic ao celular e o toolkit entrega os frames ao seu código como um Flow. Pense no DAT como um "encanamento" já pronto: você não controla o sensor diretamente — você abre uma torneira (o stream) e decide o que fazer com o que sai dela (Figura 22).

**Figura 22 – Como o vídeo dos óculos chega ao seu app**

Fonte: autoria própria.

#### 13.5.3.2 Session e Stream: quem faz o quê

O acesso à câmera é organizado em duas camadas:

- DeviceSession: representa o acesso do seu app a um dispositivo. Você a cria com Wearables.createSession(...), passando um seletor de dispositivo — AutoDeviceSelector (o SDK escolhe pelo usuário) ou

SpecificDeviceSelector (quando seu app oferece UI de seleção). A criação e os estados da sessão foram vistos no tópico 2.4.

- Stream: é a capability de mídia anexada à sessão, criada com session.addStream(config). É ele quem entrega vídeo (videoStream), estado (state), erros (errorStream) e captura de foto (capturePhoto()).

Dois detalhes importantes dessa hierarquia:

1. addStream() apenas registra a capability; é o stream.start() que ativa a câmera nos óculos e começa a entrega de frames.

2. O stop é em cascata: quando a sessão-pai para, todos os streams dela param automaticamente. Você também pode parar um stream individualmente com stream.stop() (ou removê-lo com Session.removeStream). Após stop(), o stream é invalidado e não pode ser reutilizado — crie outro se precisar.

Nota 32: Stream implementa Closeable, e close() delega para stop(). Isso permite usar o stream com use { ... } do Kotlin quando fizer sentido.

#### 13.5.3.3 O ciclo de vida do stream

O estado do stream é exposto em stream.state: StateFlow<StreamState> e transita assim:

- STOPPED → STARTING → STARTED → STREAMING — quando o dispositivo conecta e o streaming começa;

- STREAMING → STOPPING → STOPPED — em desconexão ou erro;

- qualquer estado → STOPPING → STOPPED → CLOSED — em stop/close.

Os frames de vídeo só são entregues no estado STREAMING. Se a sessão for pausada pelo sistema (estado PAUSED, visto no 2.4 — por exemplo, quando outro app assume o dispositivo), os streams param de entregar dados até a sessão voltar a RUNNING (Figura 23).

**Figura 23 – Ciclo de vida do Stream**

Fonte: autoria própria.

Atenção! Fechar as hastes dos óculos derruba o Bluetooth, para os streams ativos e leva a sessão a STOPPED. Reabrir as hastes restaura o Bluetooth, mas não reinicia a sessão — seu app precisa criar uma nova quando o dispositivo voltar a ficar disponível.

#### 13.5.3.4 Resolução, frame rate e formato dos frames

Você pede resolução e frame rate no StreamConfiguration:

▶ Código 2.5-01 – código completo no notebook companion (seção 2.5.3.4).

Na Tabela 23, você encontra diferentes resoluções para diferentes tipos de qualidade.

**Tabela 23 – Diferentes resoluções para diferentes tipos de qualidade VideoQuality Resolução (px) HIGH 720 × 1280**

MEDIUM (padrão) 504 × 896 LOW 360 × 640

Fonte: autoria própria.

Os valores válidos de frameRate são 2, 7, 15, 24 ou 30 FPS — não existe valor intermediário nem 60 FPS.

O parâmetro compressVideo define o formato dos frames entregues:

- false (padrão): o SDK decodifica internamente e entrega buffers de pixels YUV — prontos para exibir ou alimentar um modelo de visão.

- true: o SDK pula a decodificação e entrega buffers HEVC comprimidos — útil se você mesmo vai decodificar ou gravar. Nesse modo, frames com isCodecConfig = true carregam a configuração do codec (VPS/SPS/PPS) e precisam ser alimentados ao decoder antes dos frames de vídeo.

Cada frame chega como um VideoFrame(buffer, width, height, presentationTimeUs, isCompressed, isCodecConfig).

Nota 33: as resoluções são "em pé" (largura × altura, ex.: 720 × 1280) — bem abaixo dos 12 MP do sensor. O stream é dimensionado para caber no Bluetooth, não para espelhar a capacidade da câmera.

#### 13.5.3.5 Desempenho: a banda do Bluetooth manda

Resolução e frame rate reais são limitados pela conexão Bluetooth Classic entre celular e óculos. Para lidar com a banda limitada, o toolkit aplica um rebaixamento automático em escada (quality ladder): primeiro reduz a resolução em um degrau (ex.: HIGH → MEDIUM); se ainda faltar banda, reduz o frame rate (ex.: 30 → 24), mas nunca abaixo de 15 FPS.

Além disso, há compressão por frame que se adapta à banda disponível. Consequência contraintuitiva: a imagem pode parecer pior do que a resolução reportada (HIGH/MEDIUM) sugere. Pedir resolução menor, frame rate menor, ou ambos, deixa mais banda por frame e costuma resultar em imagem visualmente melhor, com menos perda de compressão.

Dois pontos finais de desempenho:

O videoStream lida com buffer cheio descartando os frames mais antigos para manter o streaming fluido. Se seu processamento por frame for lento (ex.: inferência pesada), você perderá frames — o que geralmente é o comportamento desejado em tempo real.

- Só pode haver uma captura de foto em andamento por vez: uma segunda chamada a capturePhoto() com outra pendente retorna CaptureError.CaptureInProgress.

Na prática: para o hackathon, resista à tentação de pedir HIGH + 30 FPS "porque é o máximo". Um pipeline de visão computacional como o do tópico 1.7 raramente precisa de mais que 15 FPS em MEDIUM ou LOW — e a imagem menos comprimida tende a melhorar a acurácia do modelo mais do que a resolução extra melhoraria. Menos banda também significa menos consumo de bateria nos óculos e no celular (tópico 1.5).

### 13.5.4 Na prática

Vamos abrir um stream da câmera e capturar uma foto. Os trechos seguem o guia oficial de integração Android e assumem o setup do 2.3 e o registro/permissões do 2.4 já feitos.

#### 13.5.4.1 Passo 0 — Pré-condições

No Gradle, o módulo de câmera precisa estar entre as dependências (feito no 2.3):

▶ Código 2.5-02 – código completo no notebook companion (seção 2.5.4.1).

Antes de streamar, confirme a permissão de câmera do wearable (fluxo completo no 2.4):

▶ Código 2.5-03 – dispare o RequestPermissionContract, como visto no 2.4 — código completo no notebook companion (seção 2.5.4.1).

Atenção! Chamar qualquer API do toolkit antes de Wearables.initialize(context) resulta em WearablesError.NOT_INITIALIZED. A inicialização é uma vez por processo, no startup (tópico 2.3).

#### 13.5.4.2 Passo 1 — Criar e iniciar a sessão

▶ Código 2.5-04 – AutoDeviceSelector deixa o SDK escolher o dispositivo pelo usuário — código completo no notebook companion (seção 2.5.4.2).

#### 13.5.4.3 Passo 2 — Adicionar e iniciar o stream de câmera

▶ Código 2.5-05 – Config explícita: MEDIUM (504x896) a 24 FPS, frames YUV decodificados — código completo no notebook companion (seção 2.5.4.3).

Atenção! O erro mais comum deste tópico é esquecer o stream.start() depois do addStream(). O addStream() retorna sucesso, os collectors ficam registrados, nenhuma exceção acontece — e nenhum frame chega, porque a câmera nunca foi ativada. Se seu videoStream está "mudo", verifique primeiro se start() foi chamado e se state chegou a STREAMING.

#### 13.5.4.4 Passo 3 — Exibir os frames

Com compressVideo = false, cada VideoFrame traz pixels YUV em frame.buffer, com frame.width e frame.height. A conversão YUV → Bitmap para exibição fica a cargo do app — veja a implementação de renderização no sample CameraAccess do repositório meta-wearables-dat-android.

▶ Código 2.5-06 – frame.buffer: ByteBuffer YUV (isCompressed = false) – código completo no notebook companion (seção 2.5.4.4).

#### 13.5.4.5 Passo 4 — Capturar uma foto

Com o streaming ativo, capturePhoto() é uma suspend function que retorna DatResult<PhotoData, CaptureError>:

▶ Código 2.5-07 – código completo no notebook companion (seção 2.5.4.5).

Nota 34: se você for compartilhar a foto com outros apps, o guia oficial pede um res/xml/file_paths.xml para que o FileProvider exponha as imagens em cache.

#### 13.5.4.6 Passo 5 — Encerrar

activeStream?.stop() // invalida o stream; para reuso, crie outro com addStream

**Código 2.5-08 · também no notebook companion, seção 2.5.4.6 Ao parar a sessão (tópico 2.4), todos os streams dela param em cascata — mas parar o stream explicitamente quando a UI sai de cena evita manter a câmera dos óculos ligada à toa.**

Na prática: o "hello world" da câmera no hackathon é ver o stream chegando no app: rode o passo a passo acima com os óculos pareados — ou com o Mock Device Kit usando a câmera do próprio celular como fonte, sem hardware (tópico 2.7) — e observe o state transitar STARTING → STARTED → STREAMING enquanto o ImageView mostra a visão em primeira pessoa do usuário. Esse é o ponto de partida de qualquer solução de visão: a partir daí, basta trocar o displayFrame por um pipeline de inferência como o do tópico 1.7.

### 13.5.5 Resumo / cheatsheet

- DeviceSession dá acesso ao dispositivo; Stream (via session.addStream(config)) é a capability de câmera: vídeo, estado, erros e foto.

- addStream() registra; stream.start() liga a câmera. Sem start(), nenhum frame chega.

- Frames só são entregues no estado STREAMING; ciclo: STOPPED → STARTING → STARTED → STREAMING → STOPPING → STOPPED → CLOSED.

- StreamConfiguration: videoQuality HIGH 720×1280 / MEDIUM 504×896 (padrão) / LOW 360×640; frameRate ∈ {2, 7, 15, 24, 30}, padrão 24.

- compressVideo = false (padrão) entrega YUV decodificado; true entrega HEVC comprimido (frames isCodecConfig alimentam o decoder).

- Banda do Bluetooth Classic limita tudo: o ladder automático reduz primeiro resolução (um degrau), depois FPS (nunca abaixo de 15) — e pedir resolução/FPS menores reduz a compressão por frame, podendo melhorar a qualidade visual.

- capturePhoto() exige streaming ativo, aceita uma captura por vez (CaptureError.CaptureInProgress) e retorna PhotoData.HEIC ou PhotoData.Bitmap.

- Stop em cascata: sessão parou → streams param. Stream parado é invalidado; crie outro para voltar a streamar.

### 13.5.6 Referências

- ANDROID INTEGRATIONS — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/develop/dat/build-integration-an droid/. Guia oficial passo a passo: manifest, Gradle, sessão, stream de câmera e captura de foto.

- STREAM INTERFACE — API REFERENCE ANDROID DAT 0.8, META. Disponível em: https://wearables.developer.meta.com/docs/reference/android/dat/0.6/com_ meta_wearable_dat_camera_stream. Contratos de videoStream, state, errorStream, start/stop e capturePhoto.

- STREAMCONFIGURATION — API REFERENCE ANDROID DAT 0.8, META. Disponível em: https://wearables.developer.meta.com/docs/reference/android/dat/0.6/com_ meta_wearable_dat_camera_types_streamconfiguration. Parâmetros videoQuality, frameRate e compressVideo, com defaults.

- META-WEARABLES-DAT-ANDROID — FACEBOOK/GITHUB. Disponível em: https://github.com/facebook/meta-wearables-dat-android. Setup do SDK via GitHub Packages e sample app completo com streaming e renderização de frames.

#### 13.5.6.1 Para ir além

SESSION LIFECYCLE — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/develop/dat/lifecycle-

events/. Estados de sessão (RUNNING/PAUSED/STOPPED) e efeitos sobre os streams.

ANDROID TESTING COM MOCK DEVICE KIT — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/develop/dat/testingmdk-android/. Como testar o stream de câmera sem óculos físicos (aprofundado no tópico 2.7).
