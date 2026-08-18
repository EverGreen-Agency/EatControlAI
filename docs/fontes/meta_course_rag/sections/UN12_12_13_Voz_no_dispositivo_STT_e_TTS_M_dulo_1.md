---
unit: 12
unit_title: "Curso de Kotlin/Android"
section: "12.13"
section_title: "Voz no dispositivo: STT e TTS - Módulo: 1"
source_file: "Un12_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN12_Curso_de_Kotlin_Android.md"
source_pages: [80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93]
language: "pt-BR"
---

## 12.13 Voz no dispositivo: STT e TTS - Módulo: 1

Kotlin/Android · CEIA e Meta - AI Glasses Brasil

### 12.13.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar o que são STT e TTS e onde cada um entra no assistente de voz dos óculos Ray-Ban Meta.

- Descrever os pipelines de STT e de TTS etapa por etapa, diferenciando pipelines clássicos (módulos separados) de modelos end-to-end.

- Comparar modelos de STT (Whisper, Wav2Vec 2.0, Conformer/RNN-T, Vosk) e de TTS (Tacotron 2, FastSpeech 2, VITS, Piper) quanto à viabilidade on-device.

- Aplicar técnicas de processamento de voz — VAD, diarização e palavra de ativação — e escolher entre estratégias como streaming × lote e push-to-talk × sempre-escutando.

- Implementar STT e TTS no Android com as APIs nativas (SpeechRecognizer / TextToSpeech) e conhecer o caminho para rodar modelos on-device (Whisper tiny, Piper).

### 12.13.2 Pré-requisitos

- Tópico 1.2 (Coroutines e Flow) — a inferência de voz roda fora da main thread; usaremos withContext e Dispatchers.

- Tópico 1.4 (Permissões em runtime) — STT exige a permissão RECORD_AUDIO concedida em tempo de execução.

- Tópico 1.6 (Edge AI) — quantização, formatos de modelo e trade-offs de rodar IA no aparelho; não reexplicaremos isso aqui.

- Ferramentas — Android Studio atualizado e um dispositivo físico com microfone (o emulador tem limitações de captura de áudio).

### 12.13.3 Conceito / fundamentação

O assistente dos óculos conversa: o usuário fala um comando e o assistente responde falando (os óculos não têm display — a voz é a interface principal de saída). Este tópico cobre as duas metades dessa conversa: STT, que entende a fala, e TTS, que produz a resposta falada. É o "menu de técnicas" de voz, no mesmo espírito do que o tópico 1.7 fez para visão.

#### 12.13.3.1 STT: transformar fala em texto

STT (Speech-to-Text), também chamado de ASR (Automatic Speech Recognition), é a tarefa de converter um sinal de áudio contendo fala em texto. No nosso contexto, é a porta de entrada do assistente: o usuário fala perto dos óculos,

o áudio chega ao celular via Bluetooth e o STT o transcreve; o texto resultante é o que o restante do app consegue processar (interpretar o comando, alimentar um modelo de linguagem etc.).

Uma analogia útil: o STT é o "ouvido alfabetizado" do sistema — ele não entende o significado do que foi dito (isso é papel de outra camada), apenas registra fielmente o que foi dito.

#### 12.13.3.2 O pipeline de STT

Um sistema de STT funciona em etapas (Figura 14):

1. Captura de áudio — o microfone produz amostras PCM (tipicamente 16 kHz, mono, 16 bits para fala). 2. Pré-processamento / extração de características — o áudio bruto é convertido em uma representação mais compacta e informativa, quase sempre um mel-spectrogram: uma "imagem" que mostra quanta energia existe em cada faixa de frequência ao longo do tempo, usando a escala mel, que aproxima a percepção humana de graves e agudos. Pense nele como a "partitura" do áudio. 3. Modelo acústico + decodificador — uma rede neural mapeia as características em unidades de fala (caracteres, fonemas ou sub-palavras) e um decodificador monta a sequência final de texto, possivelmente com ajuda de um modelo de língua. 4. Texto — a transcrição, muitas vezes acompanhada de hipóteses alternativas e scores de confiança. Clássico × end-to-end. Nos pipelines clássicos, as etapas são módulos separados e treinados individualmente: modelo acústico + léxico de pronúncia + modelo de língua, combinados na decodificação. Nos modelos end-to-end, uma única rede neural é treinada para mapear áudio → texto diretamente (com técnicas como CTC, atenção encoder-decoder ou RNN-T). End-to-end simplifica o sistema e domina o estado da arte; o clássico ainda vence em cenários de recursos muito limitados e vocabulário controlado.

**Figura 14 – Comparação pipelines de STT por etapas x end-to-end**

Fonte: autoria própria.

#### 12.13.3.3 Modelos de STT e viabilidade on-device

Na Tabela 15, a seguir, você encontra a ideia central de cada modelo de STT e sua viabilidade on-device.

**Tabela 15 – Modelos de STT e sua viabilidade on-device Modelo / família Ideia central On-device?**

Whisper (OpenAI)

tiny/base rodam em celular, especialmente quantizadas (ver 1.6). Processa em lote (janelas de áudio) — não nasceu para streaming.

Encoder-decoder Transformer, end-to-end, multilíngue (inclui pt-BR), robusto a ruído. Variantes de tiny (~39 M parâmetros) a large; existem versões destiladas (Distil-Whisper) menores e mais rápidas.

Wav2Vec 2.0 (Meta AI)

Possível com variantes pequenas quantizadas, mas é mais comum como base de pesquisa/fine-tuning do que como solução mobile pronta.

Pré-treinamento autossupervisionado em áudio sem transcrição; fine-tuning com pouca supervisão, decodificação via CTC.

Conformer / RNN-T

Sim — é a família por trás de ASR on-device comercial (ditado de teclado, legendas ao vivo em celulares).

Encoder Conformer (convolução + Transformer) com decodificador RNN-T, projetado para streaming com baixa latência.

Vosk / Kaldi

Toolkit clássico leve e offline; o Vosk empacota modelos Kaldi para mobile com API simples.

Sim — modelos pt-BR compactos (dezenas de MB), streaming nativo, latência baixa; qualidade inferior ao Whisper em fala espontânea.

Fonte: autoria própria.

Na prática: para o hackathon, a escolha típica é Whisper tiny ou base quantizado para transcrever comandos curtos em lote (o usuário fala, o áudio é fechado, o modelo transcreve), ou Vosk se você precisar de transcrição contínua em streaming com pouquíssimo recurso. Whisper large e afins ficam fora do alcance do celular.

#### 12.13.3.4 TTS: transformar texto em fala

TTS (Text-to-Speech) é a tarefa inversa: converter texto em áudio de fala natural. No assistente dos óculos, é a porta de saída: a resposta gerada pelo app vira voz e é reproduzida nos alto-falantes open-ear. Sem display, o TTS não é um "extra" — é o único canal rico de resposta ao usuário.

#### 12.13.3.5 O pipeline de TTS

O pipeline clássico de síntese também funciona em etapas (Figura 15):

1. Normalização de texto — expandir números, siglas e símbolos para a forma falada: "R$ 10" → "dez reais", "Dr." → "doutor". 2. Análise linguística / fonemas — converter grafemas em fonemas (G2P, grapheme-to-phoneme) e extrair marcações de prosódia (pausas, ênfase). 3. Modelo acústico — uma rede neural converte a sequência de fonemas em um mel-spectrogram (o mesmo tipo de representação usado no STT, agora como saída intermediária). 4. Vocoder — uma segunda rede converte o mel-spectrogram em forma de onda (as amostras de áudio finais). End-to-end. Modelos como o VITS unificam tudo: uma única rede vai do texto direto à forma de onda, treinada de ponta a ponta — menos componentes para integrar e menos fontes de erro acumulado.

**Figura 15 – Comparação pipelines de TTS por etapas x end-to-end**

Fonte: autoria própria.

#### 12.13.3.6 Modelos de TTS e viabilidade on-device

Na Tabela 16, a seguir, você encontra a ideia central de cada modelo de TTS e sua viabilidade on-device.

**Tabela 16 – Análise comparativa das arquiteturas de síntese de fala e de sua viabilidade para execução on-device**

Modelo Ideia central On-device?

Tacotron 2 + vocoder

Pesado para celular na forma original; historicamente importante, hoje pouco usado em mobile.

Modelo acústico autorregressivo (gera o mel quadro a quadro) + vocoder. O vocoder original, WaveNet, tem alta qualidade mas é lento; HiFi-GAN gera com qualidade próxima em tempo real na CPU.

FastSpeech 2

Viável com vocoder leve (HiFi-GAN), mas exige montar duas peças.

Não-autorregressivo: gera todos os quadros do mel em paralelo — rápido e estável (não "engole" nem repete palavras). Precisa de vocoder.

VITS End-to-end: texto → forma de onda em um único modelo, com qualidade alta.

Variantes reduzidas são viáveis; é a base do caminho mais prático abaixo.

Piper

Sistema baseado em VITS otimizado para edge (nasceu para Raspberry Pi), modelos em ONNX, dezenas de idiomas — incluindo vozes pt-BR. Sim — é a opção de referência para TTS neural local; se roda em Raspberry Pi, roda com folga num celular moderno.

Fonte: autoria própria.

Nota 19: o par "modelo acústico + vocoder" explica por que você verá o mel-spectrogram nos dois mundos: no STT ele é a entrada do modelo; no TTS clássico, a saída intermediária que o vocoder transforma em som.

#### 12.13.3.7 Técnicas de processamento de voz

Um assistente de voz real não é só "STT + TTS". Entre o microfone e o modelo há um encadeamento de técnicas que economizam bateria, reduzem latência e melhoram a experiência.

##### 12.13.3.7.1 VAD (Voice Activity Detection)

O VAD responde uma pergunta simples: há fala neste trecho de áudio, ou é silêncio/ruído? Com ele, o STT (caro) só roda quando alguém de fato está falando — o resto do tempo o áudio é descartado barato. Isso corta latência (o app sabe quando a fala terminou e pode fechar a janela de transcrição) e poupa bateria (ver

tópico 1.5). Exemplos: Silero VAD (modelo neural minúsculo, na casa de poucos MB) e WebRTC VAD (clássico, extremamente leve).

##### 12.13.3.7.2 Diarização

Diarização responde "quem falou e quando": segmenta um áudio com várias pessoas e atribui cada trecho a um locutor (Locutor A, Locutor B...). É relevante quando o caso de uso envolve mais de uma voz — transcrever uma reunião ou entrevista capturada pelos óculos, por exemplo. Para um assistente de comandos de um único usuário, geralmente é dispensável. É um processamento relativamente pesado, normalmente feito em lote, após a captura.

##### 12.13.3.7.3 Palavra de ativação (wake word)

Uma wake word é uma frase-gatilho ("Ok, assistente") detectada por um modelo minúsculo e sempre-ligado, cujo único trabalho é reconhecer aquela frase específica. Ao detectá-la, ele "acorda" o pipeline completo (VAD → STT → resposta). A lógica é de cascata: o modelo barato roda o tempo todo; o caro, quase nunca. Exemplos: Porcupine e openWakeWord (open source) (Figura 16).

Atenção! O acionamento "Hey Meta" dos óculos não é acessível pelo toolkit de desenvolvimento — ele pertence ao assistente da Meta. Se a sua equipe quiser ativação por voz, precisará construir a própria wake word rodando no celular, sobre o áudio que chega dos óculos (o acesso ao microfone via SDK será visto no tópico 2.6).

**Figura 16 – Lógica de modelo cascata com wake word**

Fonte: autoria própria.

##### 12.13.3.7.4 Estratégias de processamento

- Streaming × em lote. Em streaming, o áudio é transcrito enquanto chega — transcrições parciais aparecem em tempo real (Vosk e Conformer/RNN-T nasceram para isso). Em lote, o app captura a fala inteira e transcreve de uma vez (o modo natural do Whisper). Lote é mais

simples e costuma bastar para comandos curtos; streaming vale quando o usuário precisa de feedback imediato ou fala longamente.

- Push-to-talk × sempre-escutando. Push-to-talk: o usuário aciona explicitamente a escuta (botão no app, gesto). Simples, previsível, zero custo quando ocioso — ótimo primeiro passo no hackathon. Sempre-escutando: exige a cascata wake word + VAD acima e cobra seu preço em bateria (tópico 1.5), mas oferece a experiência mãos-livres que combina com óculos.

- On-device × híbrido. On-device: todo o processamento no celular — privacidade (o áudio da voz do usuário nunca sai do aparelho), funciona offline, latência previsível. Híbrido: partes pesadas na nuvem — mais qualidade, ao custo de rede, latência variável e envio de áudio para terceiros. Os checkpoints do programa valorizam IA local: se usar qualquer serviço externo, deixe explícito.

- Latência e ruído. Latência total = captura + (Bluetooth) + VAD + STT + lógica + TTS + reprodução; cada estágio soma, e o usuário percebe a soma. Quanto ao ruído, há uma boa notícia: o array de 5 microfones dos óculos ajuda a capturar a voz do usuário com mais limpeza do que um microfone único em ambiente barulhento — o sinal que chega ao celular via Bluetooth já é mais favorável ao STT (as características do streaming de áudio via SDK serão vistas no tópico 2.6).

#### 12.13.3.8 Viabilidade on-device: fechando a conta

Tudo que o tópico 1.6 apresentou sobre edge AI vale aqui: modelos de voz viáveis no celular são as variantes pequenas e quantizadas — Whisper tiny/base ou Distil-Whisper para STT, Piper para TTS, Silero VAD e wake words minúsculas como estágios baratos. A conta a fechar é tripla: memória (o modelo precisa caber na RAM junto com o resto do app), latência (o usuário espera resposta em ~1–2 s de conversa natural) e bateria (inferência contínua drena; a cascata de ativação existe exatamente para isso).

Na prática: um pipeline de voz completo e 100% local no celular é perfeitamente alcançável no hackathon: wake word própria (ou push-to-talk) →

Silero VAD → Whisper tiny quantizado → lógica do app → Piper. Nenhum áudio sai do aparelho — argumento forte nos checkpoints de IA local e privacidade.

### 12.13.4 Na prática

Vamos implementar as duas pontas no Android: primeiro com as APIs nativas (rápido de montar, ótimo para prototipar) e depois o caminho com modelos on-device (controle total e privacidade garantida). Em cada abordagem, indicamos explicitamente se o processamento é local ou usa serviço.

#### 12.13.4.1 Passo 0 — Permissão de microfone

STT exige RECORD_AUDIO, uma permissão dangerous que deve ser solicitada em runtime, como visto no tópico 1.4. No AndroidManifest.xml:

<uses-permission android:name="android.permission.RECORD_AUDIO" />

**Código 1.8-01 · também no notebook companion, seção 1.8.4.1 E no app, antes de iniciar qualquer captura, confirme a concessão (fluxo do 1.4). O TextToSpeech não exige permissão.**

#### 12.13.4.2 STT com a API nativa: SpeechRecognizer

O Android oferece reconhecimento de fala pronto via android.speech.SpeechRecognizer, que delega o trabalho a um serviço de reconhecimento instalado no aparelho (em geral, o da Google).

▶ Código 1.8-02 – código completo no notebook companion (seção 1.8.4.2).

On-device ou serviço? Por padrão, o serviço de reconhecimento pode enviar o áudio a servidores — depende do fornecedor do serviço e do aparelho. Para garantir processamento local você tem duas alavancas: EXTRA_PREFER_OFFLINE (API 23+, apenas prefere o modo offline) e, a partir do API 31, SpeechRecognizer.createOnDeviceSpeechRecognizer() combinado com isOnDeviceRecognitionAvailable(), que usa

exclusivamente o reconhecedor local do aparelho (exige que o pacote de idioma pt-BR esteja baixado no dispositivo).

Atenção! Erros comuns com SpeechRecognizer:
1) criar ou chamar o recognizer fora da main thread — a API exige a thread
principal;
2) esquecer destroy() e vazar a conexão com o serviço;
3) tratar ERROR_NO_MATCH como falha grave — ele só significa "não entendi
nada", situação normal que merece um simples "pode repetir?";
4) iniciar a escuta sem a permissão RECORD_AUDIO concedida, o que gera
ERROR_INSUFFICIENT_PERMISSIONS.

#### 12.13.4.3 TTS com a API nativa: TextToSpeech

A síntese nativa usa a engine de TTS instalada no aparelho (em geral, a da Google). A inicialização é assíncrona — este é o detalhe que mais derruba iniciantes:

▶ Código 1.8-03 – código completo no notebook companion (seção 1.8.4.3).

On-device ou serviço? A engine da Google sintetiza localmente quando a voz do idioma está baixada no aparelho (configurável pelo usuário em ajustes de "saída de conversão de texto em voz"); algumas vozes de maior qualidade podem usar rede. Para o checkpoint de privacidade, teste em modo avião: se falar, é local.

Atenção! O erro clássico é chamar speak() logo após criar o objeto — o onInit ainda não rodou e a chamada falha silenciosamente. Sempre condicione a fala ao estado ready (ou enfileire os textos até a engine inicializar).

#### 12.13.4.4 STT on-device com Whisper tiny (whisper.cpp)

Para garantir STT 100% local e independente do serviço do aparelho, o caminho mais usado é o whisper.cpp — implementação em C++ do Whisper, com bindings JNI e um exemplo Android oficial no repositório

(examples/whisper.android). O modelo ggml-tiny tem ~75 MB, e versões quantizadas ficam bem menores (técnicas do 1.6).

O fluxo tem três partes — capturar PCM, carregar o modelo, transcrever fora da main thread:

▶ Código 1.8-04 – 1) Captura: PCM 16 kHz, mono, 16 bits — o formato que o Whisper espera — código completo no notebook companion (seção 1.8.4.4). ▶ Código 1.8-05 – 2) e 3) Carga do modelo e transcrição — API do binding do exemplo oficial — código completo no notebook companion (seção 1.8.4.4).

Nota 20: o Whisper trabalha em lote: feche a janela de áudio (fim do push-to-talk ou VAD detectando silêncio) e então transcreva. Para comandos de poucos segundos, a latência do tiny quantizado em celulares recentes geralmente fica dentro do aceitável para conversa.

#### 12.13.4.5 TTS on-device com Piper

O Piper é um sistema em C++/ONNX sem biblioteca Android oficial própria; o caminho prático em Android é o runtime sherpa-onnx (projeto k2-fsa), que executa vozes VITS/Piper localmente e oferece API Kotlin:

▶ Código 1.8-06 – depende da dependência sherpa-onnx e de uma voz pt-BR do Piper — código completo no notebook companion (seção 1.8.4.5).

Todo o processamento é local: o texto nunca sai do aparelho.

#### 12.13.4.6 Resumo: on-device ou serviço?

Veja, na Tabela 17, a seguir, um resumo sobre as abordagens, locais de processamento e as implicações para os checkpoints.

**Tabela 17 – Abordagens de STT e TTS quanto ao local de processamento e à validação técnica Abordagem Onde processa Implicação para os checkpoints**

SpeechRecognizer (padrão) Serviço do aparelho — pode usar rede Declarar como híbrido, salvo se forçar on-device

Local (com pacote de idioma baixado) OK para IA local; documente a configuração

SpeechRecognizer on-device (API 31+) / EXTRA_PREFER_OFFLINE

Local com voz baixada; rede possível Teste em modo avião e documente TextToSpeech (engine Google)

Whisper tiny via whisper.cpp 100% local Atende IA local e privacidade Piper via sherpa-onnx 100% local Atende IA local e privacidade

Fonte: autoria própria.

Na prática: para a demo do hackathon, comece com push-to-talk + APIs nativas — você terá o ciclo falar → transcrever → responder → ouvir funcionando em uma tarde. Depois, troque as peças nativas por Whisper tiny e Piper para cravar o requisito de IA local, e por fim adicione VAD/wake word se a experiência mãos-livres for o diferencial do seu projeto.

### 12.13.5 Resumo / cheatsheet

- STT = fala → texto (entender o usuário); TTS = texto → fala (responder). Nos óculos sem display, voz é entrada e saída.

- Pipeline STT: áudio PCM → mel-spectrogram → modelo acústico/decodificador → texto; end-to-end faz tudo em uma rede (CTC, atenção, RNN-T).

- Pipeline TTS: texto → normalização → fonemas → modelo acústico (mel) → vocoder (onda); VITS/Piper são end-to-end.

- On-device viáveis: Whisper tiny/base quantizado e Vosk (STT); Piper (TTS). Grandes (Whisper large, Tacotron+WaveNet) não cabem no celular.

- Cascata de ativação: wake word (minúscula, sempre-ligada) → VAD (filtra silêncio) → STT (pesado, sob demanda). Diarização = "quem falou quando", para áudio multi-locutor.

- "Hey Meta" não é acessível pelo toolkit — wake word própria roda no celular, sobre o áudio vindo dos óculos (tópico 2.6).

- SpeechRecognizer padrão pode usar rede; para local: EXTRA_PREFER_OFFLINE / createOnDeviceSpeechRecognizer (API 31+). TextToSpeech inicializa assíncrono — nunca speak() antes do onInit.

- Inferência de voz sempre fora da main thread (coroutines, 1.2); escuta contínua cobra bateria (1.5); quantização e formatos são o 1.6.

### 12.13.6 Referências

- SPEECHRECOGNIZER / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/reference/android/speech/SpeechRecognizer . Referência completa da API de reconhecimento, incluindo createOnDeviceSpeechRecognizer e códigos de erro.

- RECOGNIZERINTENT / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/reference/android/speech/RecognizerIntent. Extras de configuração do reconhecimento (EXTRA_LANGUAGE, EXTRA_PREFER_OFFLINE, EXTRA_PARTIAL_RESULTS).

- TEXTTOSPEECH / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/reference/android/speech/tts/TextToSpeech. Referência da API de síntese: inicialização, filas, UtteranceProgressListener.

- WHISPER / OPENAI (GITHUB). Disponível em: https://github.com/openai/whisper. Modelo, variantes (tiny a large), idiomas suportados e artigos associados.

- WHISPER.CPP / GGERGANOV (GITHUB). Disponível em: https://github.com/ggml-org/whisper.cpp. Implementação C++ do Whisper com modelos ggml quantizados e exemplo Android.

- PIPER / RHASSPY (GITHUB). Disponível em: https://github.com/rhasspy/piper. TTS neural local baseado em VITS, com catálogo de vozes por idioma.

- SILERO VAD / SNAKERS4 (GITHUB). Disponível em: https://github.com/snakers4/silero-vad. Detector neural de atividade de voz, leve e pronto para uso.

- VOSK API / ALPHACEP (GITHUB). Disponível em: https://github.com/alphacep/vosk-api. Toolkit de ASR offline com suporte a Android e modelos compactos por idioma.

#### 12.13.6.1 Para ir além

SHERPA-ONNX / K2-FSA (GITHUB). Disponível em: https://github.com/k2-fsa/sherpa-onnx. Runtime de fala (STT, TTS, VAD) com exemplos Android — o caminho prático para vozes Piper no celular.

OPENWAKEWORD / DSCRIPKA (GITHUB). Disponível em: https://github.com/dscripka/openWakeWord. Framework open source de wake word para construir a frase de ativação da sua equipe.

FAIRSEQ (WAV2VEC 2.0) / META AI (GITHUB). Disponível em: https://github.com/facebookresearch/fairseq. Código e modelos oficiais do Wav2Vec 2.0 para quem quiser explorar fine-tuning.
