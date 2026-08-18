---
unit: 13
unit_title: "Curso do Meta SDK (DAT)"
section: "13.6"
section_title: "Microfone e alto-falantes via Bluetooth"
source_file: "Un13_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN13_Curso_do_Meta_SDK_DAT.md"
source_pages: [48, 49, 50, 51, 52, 53, 54, 55]
language: "pt-BR"
---

## 13.6 Microfone e alto-falantes via Bluetooth

### 13.6.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar por que o microfone e os alto-falantes dos óculos são acessados pelos perfis de áudio Bluetooth do Android, e não pela API de câmera do Device Access Toolkit (DAT).

- Comparar os perfis A2DP e HFP e escolher o perfil adequado para cada cenário (só reprodução vs. voz bidirecional).

- Implementar o roteamento de áudio para os óculos usando AudioManager e setCommunicationDevice().

- Implementar a captura da voz do usuário com AudioRecord e a reprodução da resposta pelos alto-falantes open-ear.

- Antecipar as limitações de qualidade do áudio HFP (8 kHz, mono, beamforming) no design da sua solução de hackathon.

### 13.6.2 Pré-requisitos

- Tópico 1.4 (Permissões em runtime) — você vai precisar solicitar RECORD_AUDIO (e BLUETOOTH_CONNECT, já usada pelo DAT) em runtime.

- Tópico 1.8 (STT e TTS) — o áudio capturado aqui é a entrada do seu pipeline de voz; a saída do TTS é o que você vai reproduzir nos óculos.

- Tópicos 2.3 e 2.4 — app já configurado com o toolkit, registrado no app Meta AI e com os óculos pareados.

- Ferramentas: Android Studio com o projeto do tópico 2.3; óculos Ray-Ban Meta pareados via app Meta AI. As APIs de roteamento usadas aqui (availableCommunicationDevices / setCommunicationDevice) exigem Android 12 (API 31) ou superior no smartphone.

### 13.6.3 Conceito / fundamentação

#### 13.6.3.1 Duas vias de acesso: câmera pelo toolkit, áudio pelo Bluetooth

O DAT, na versão atual do developer preview, expõe as capacidades de câmera dos óculos: streaming de vídeo e captura de foto, como visto no 2.5. O áudio é outra via: microfone e alto-falantes dos óculos se comportam como qualquer headset Bluetooth e são acessados pelas APIs de áudio padrão do Android (AudioManager, AudioRecord, AudioTrack) por meio dos perfis de áudio Bluetooth.

Atenção! Não procure um "session.audioStream" no toolkit — ele não existe. A regra é: câmera = DAT; áudio = perfis Bluetooth do Android. Esse é o erro conceitual mais comum ao começar com o SDK: tentar obter o áudio do microfone pela sessão do DAT e concluir, errado, que "os óculos não têm API de áudio". Têm — mas é a API de áudio do próprio Android.

A boa notícia dessa separação: todo o conhecimento de áudio Android que já existe (documentação, exemplos, bibliotecas de STT/TTS do 1.8) funciona sem adaptação, porque para o Android os óculos são "só" um headset Bluetooth.

#### 13.6.3.2 Os dois perfis de áudio Bluetooth: A2DP e HFP

Segundo a documentação oficial, o áudio do dispositivo usa dois perfis Bluetooth (Tabela 24):

**Tabela 24 – Perfis de áudio utilizados pelo dispositivo Perfil Nome completo Direção Qualidade Uso típico**

A2DP Advanced Audio Distribution Profile Somente saída (telefone → óculos) Alta (mídia) Música, podcasts, áudio de mídia

HFP Hands-Free Profile Bidirecional (voz nos dois sentidos) Voz, 8 kHz mono Chamadas, assistentes de voz

Fonte: autoria própria.

A escolha é ditada pela direção do fluxo: se você só precisa reproduzir áudio de mídia com boa qualidade, o A2DP resolve. Se precisa capturar a voz do usuário (com ou sem resposta de volta), o caminho é o HFP — é ele que abre o canal do microfone.

Uma analogia: o A2DP é uma caixa de som de alta fidelidade — o som só vai numa direção, mas vai bem. O HFP é um walkie-talkie — fala e escuta ao mesmo tempo, mas com qualidade de voz de telefone, não de estúdio.

#### 13.6.3.3 O que esperar da qualidade: 8 kHz, mono e beamforming

Dois fatos da documentação oficial moldam o design da sua solução:

1. HFP transmite áudio a 8 kHz em mono. É qualidade de "ligação telefônica". Para fala isso costuma bastar (motores de STT são treinados com voz telefônica), mas não espere capturar música ou sons ricos do ambiente com fidelidade.

2. O array de microfones usa beamforming para isolar e clarear a voz de quem está usando os óculos, reduzindo significativamente o volume de sons ambientes e de outras pessoas falando. A documentação descreve isso como uma limitação esperada — mas, para o caso de uso de assistente de voz, é uma vantagem: menos ruído chegando ao seu STT.

Nota 35: beamforming é a técnica de combinar os sinais dos 5 microfones para "apontar" a escuta na direção da boca do usuário, como um zoom acústico. Na prática: isso define o que é viável no hackathon. Ideias do tipo "assistente que responde ao que eu falo" funcionam bem — a voz do usuário chega limpa ao pipeline STT → modelo → TTS. Ideias do tipo "transcrever a conversa das pessoas ao meu redor" ou "identificar a música tocando no ambiente" tendem a falhar, porque o beamforming atenua exatamente esses sons e os 8 kHz cortam o espectro que apps como identificadores de música precisam.

#### 13.6.3.4 Coexistência: áudio HFP e streaming de vídeo do DAT

A documentação oficial destaca dois pontos sobre usar áudio e câmera ao mesmo tempo (Figura 24):

- As sessões do DAT compartilham o acesso a microfone e alto-falantes com a pilha Bluetooth do sistema nos óculos — o toolkit não "toma posse" do áudio.

- Ao planejar usar HFP e streaming simultaneamente, configure o HFP completamente antes de iniciar a sessão de streaming que depende de áudio. Inverter a ordem é fonte de comportamento intermitente.

Lembre também que o streaming de vídeo do 2.5 já disputa a banda do Bluetooth; somar o canal de voz HFP é mais um consumidor do mesmo link.

**Figura 24 – Duas vias de acesso: câmera pelo DAT, áudio pelos perfis Bluetooth®**

Fonte: autoria própria.

### 13.6.4 Na prática

Vamos montar o ciclo completo de voz: rotear o áudio para os óculos → capturar a fala → (processar com STT/TTS do 1.8) → reproduzir a resposta. O código abaixo segue o exemplo oficial da documentação do DAT, adaptado e comentado.

Nota 36: todos os blocos desta seção dependem de um smartphone físico com os óculos (ou um headset Bluetooth com HFP) pareados — o emulador e o Mock Device Kit não têm Bluetooth real.

#### 13.6.4.1 Passo 0 — Permissões

Como visto no 1.4, solicite em runtime:

- RECORD_AUDIO — obrigatória para qualquer captura com AudioRecord, mesmo com microfone externo.

- BLUETOOTH_CONNECT — já deve estar no seu manifest desde o setup do toolkit no 2.3.

#### 13.6.4.2 Passo 1 — Rotear o áudio para os óculos

Os óculos aparecem para o Android como um dispositivo de comunicação Bluetooth do tipo SCO (o transporte do HFP). O exemplo oficial da documentação faz o roteamento assim:

▶ Código 2.6-01 – Requer API 31+ (availableCommunicationDevices / setCommunicationDevice) — código completo no notebook companion (seção 2.6.4.2).

Verificar: o exemplo oficial do DAT usa AudioManager.MODE_NORMAL; a documentação do Android Developers costuma associar setCommunicationDevice() a MODE_IN_COMMUNICATION em casos de uso de voz. Se a captura não iniciar no seu dispositivo, teste com MODE_IN_COMMUNICATION e confira a doc oficial das duas fontes. Atenção! O comando setCommunicationDevice() pode retornar false (roteamento recusado) e availableCommunicationDevices pode simplesmente não listar os óculos — trate os dois casos com mensagem clara ao usuário ("verifique se os óculos estão conectados no app Meta AI") em vez de falhar silenciosamente.

#### 13.6.4.3 Passo 2 — Capturar a voz do array de microfones

Com o roteamento ativo, AudioRecord captura normalmente — o Android entrega o áudio vindo do microfone dos óculos:

▶ Código 2.6-02 – código completo no notebook companion (seção 2.6.4.3).

E o loop de leitura como um Flow (padrão do 1.2), pronto para alimentar o STT do 1.8:

▶ Código 2.6-03 – código completo no notebook companion (seção 2.6.4.3).

Nota 37: gravar a 16 kHz é o padrão dos motores de STT, mas o sinal que chega via HFP nasce a 8 kHz — subir a taxa de amostragem não recupera qualidade que o link não transmitiu.

#### 13.6.4.4 Passo 3 — Reproduzir a resposta nos alto-falantes open-ear

Se você usa o TextToSpeech do 1.8, a fala sintetizada já sai pelo dispositivo de áudio ativo — os óculos. Para reproduzir PCM bruto (por exemplo, de um TTS on-device que gera samples), use AudioTrack marcando o áudio como comunicação de voz, para que ele siga o mesmo roteamento do Passo 1:

▶ Código 2.6-04 – código completo no notebook companion (seção 2.6.4.4).

#### 13.6.4.5 Passo 4 — Liberar o roteamento

Quando a interação de voz terminar, devolva o áudio ao padrão do sistema — senão todo áudio do telefone continua preso no canal de voz de 8 kHz:

▶ Código 2.6-05 – código completo no notebook companion (seção 2.6.4.5).

#### 13.6.4.6 Erro comum: iniciar o streaming de vídeo antes de configurar o HFP

Se sua solução usa câmera e voz (o caso típico no hackathon), a documentação oficial é explícita: configure o HFP por completo antes de iniciar a StreamSession que depende de áudio. O sintoma de inverter a ordem é captura de voz que "às vezes funciona": o canal SCO não sobe de forma confiável com o streaming já ativo. A correção é sequenciar — rotear o áudio, confirmar que o dispositivo de comunicação está ativo e só então chamar o session.addStream(...) do 2.5.

Na prática: para o hackathon, encapsule os Passos 1–4 numa classe única (ex.: GlassesAudioManager) com iniciar()/liberar(), e teste o

pipeline de voz com um fone Bluetooth comum com HFP antes de ter os óculos em mãos — para o Android, ambos são o mesmo tipo de dispositivo, e você libera os óculos da equipe para quem está testando a câmera. A simulação de dispositivo do toolkit será vista no 2.7.

### 13.6.5 Resumo / cheatsheet

- Câmera = DAT (tópico 2.5); microfone e alto-falantes = perfis de áudio Bluetooth padrão do Android. Não existe API de áudio no toolkit.

- A2DP: somente saída, alta qualidade (mídia). HFP: bidirecional, voz — é o perfil que abre o microfone.

- HFP entrega 8 kHz mono; beamforming do array de 5 microfones isola a voz do usuário e atenua o ambiente.

- Roteamento: audioManager.availableCommunicationDevices → achar TYPE_BLUETOOTH_SCO → setCommunicationDevice() (API 31+).

- Captura: AudioRecord com fonte VOICE_COMMUNICATION, mono, PCM 16-bit, em Dispatchers.IO.

- Reprodução: TTS sai direto pelos óculos; PCM bruto via AudioTrack com USAGE_VOICE_COMMUNICATION.

- Usando voz + vídeo juntos: configure o HFP antes de iniciar a sessão de streaming.

- Sempre chame clearCommunicationDevice() ao terminar; permissões necessárias: RECORD_AUDIO + BLUETOOTH_CONNECT.

### 13.6.6 Referências

- USE DEVICE MICROPHONES AND SPEAKERS — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/microphones-and-speakers/.

Página oficial deste tópico: perfis A2DP/HFP, nota de qualidade (8 kHz/beamforming) e o código de roteamento Android usado aqui.

- AUDIOMANAGER — ANDROID DEVELOPERS. Disponível em: https://developer.android.com/reference/android/media/AudioManager. Referência de availableCommunicationDevices, setCommunicationDevice(), clearCommunicationDevice() e modos de áudio.

- AUDIORECORD — ANDROID DEVELOPERS. Disponível em: https://developer.android.com/reference/android/media/AudioRecord. Referência da API de captura PCM usada no Passo 2.

- META WEARABLES DEVICE ACCESS TOOLKIT FOR ANDROID — GITHUB (FACEBOOK). Disponível em: https://github.com/facebook/meta-wearables-dat-android. Repositório oficial com sample app para comparar sua integração.

#### 13.6.6.1 Para ir além

AUDIODEVICEINFO — ANDROID DEVELOPERS. Disponível em: https://developer.android.com/reference/android/media/Audio DeviceInfo. Todos os tipos de dispositivo de áudio (SCO, A2DP, BLE) e como inspecioná-los.

SESSION LIFECYCLE — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/lifecycle-events/. Como o ciclo de vida da sessão do DAT interage com o restante do app (revisto no 2.4).
