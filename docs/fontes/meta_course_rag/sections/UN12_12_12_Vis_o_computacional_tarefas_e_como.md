---
unit: 12
unit_title: "Curso de Kotlin/Android"
section: "12.12"
section_title: "Visão computacional: tarefas e como"
source_file: "Un12_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN12_Curso_de_Kotlin_Android.md"
source_pages: [66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80]
language: "pt-BR"
---

## 12.12 Visão computacional: tarefas e como

levá-las ao edge

### 12.12.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar as principais tarefas de visão computacional (classificação, detecção, segmentação, OCR, landmarks, captioning e rastreamento) e dar exemplos de uso com a câmera em primeira pessoa dos óculos.

- Comparar o custo computacional dessas tarefas e avaliar quais são viáveis para rodar on-device.

- Explicar o que são transfer learning e fine-tuning e como aplicá-los a um modelo YOLO com o dataset da sua equipe.

- Implementar o caminho completo de uma detecção para o edge: fine-tuning → quantização/conversão → execução no celular em CPU ou GPU.

- Escolher entre ML Kit, MediaPipe e LiteRT como caminho de implementação para uma tarefa de visão.

### 12.12.2 Pré-requisitos

- Tópico 1.6 (Edge AI): quantização, otimização e formatos de modelo — a teoria de lá é usada aqui sem reexplicação.

- Tópico 1.2 (Coroutines e Flow) e 1.3 (Fundamentos de Android), para os trechos de código.

- Android Studio atualizado e um celular Android físico para testar inferência real (o emulador não reflete o desempenho de CPU/GPU).

- Python 3.10+ no computador (o fine-tuning acontece fora do Android). Recomendamos gerenciar o ambiente com uv.

### 12.12.3 Conceito / fundamentação

Visão computacional é o conjunto de técnicas que permite a um programa extrair informação de imagens e vídeo: o que aparece, onde aparece, que texto está escrito, que gesto uma mão faz. No contexto do programa, a fonte de imagens é a câmera ultra-wide de 12 MP dos óculos — uma câmera em primeira pessoa, que vê o que o usuário vê. E, como os óculos não têm display, o resultado de qualquer tarefa de visão precisa virar outra coisa: áudio nos alto-falantes open-ear, uma ação no app, um registro. Essa combinação — entrada visual em primeira pessoa, saída por voz — é o que torna a escolha da tarefa certa tão importante para a sua ideia de hackathon.

Esta seção funciona como um catálogo: para cada tarefa, o que ela resolve, um exemplo nos óculos e o peso esperado ao rodar on-device (no celular pareado, como definido na arquitetura do programa).

#### 12.12.3.1 Classificação de imagem

O que resolve: atribui um ou mais rótulos à imagem inteira ("é um prato de comida", "é uma planta", "é um documento"). Não diz onde o objeto está — só o que domina a cena.

Exemplo nos óculos: o usuário olha para um prato e o app identifica o tipo de refeição; ou detecta que a cena é "ambiente de obra" para ativar um modo específico do app.

Peso on-device: leve. Modelos como MobileNet foram desenhados exatamente para isso e rodam em poucos milissegundos por frame em CPU de celular.

#### 12.12.3.2 Detecção de objetos

O que resolve: encontra o que há na imagem e onde: para cada objeto, uma caixa delimitadora (bounding box), uma classe e uma confiança. É a tarefa mais versátil do catálogo — muita ideia de hackathon se reduz a "detectar X no campo de visão".

Exemplo nos óculos: identificar produtos numa prateleira, detectar se um trabalhador está sem capacete, avisar (por áudio) sobre um obstáculo à frente para uma pessoa com deficiência visual.

Peso on-device: médio, dependendo do modelo. Variantes compactas (como as YOLO nano, detalhadas adiante) rodam em tempo real no celular; variantes grandes já pedem GPU.

#### 12.12.3.3 Segmentação

O que resolve: classifica a imagem pixel a pixel, gerando uma máscara. Na segmentação semântica, cada pixel recebe uma classe ("chão", "pessoa", "céu"); na segmentação de instâncias, cada objeto ganha a própria máscara. É a resposta mais rica espacialmente — e a mais cara.

Exemplo nos óculos: separar precisamente um objeto do fundo (para medir área ocupada, por exemplo) ou identificar a região "caminho livre" no chão.

Peso on-device: médio a pesado. Existem modelos móveis de segmentação (o MediaPipe oferece alguns prontos), mas o custo por frame é bem maior que o da detecção. Use quando a caixa da detecção não basta.

#### 12.12.3.4 Reconhecimento de texto (OCR)

O que resolve: localiza e transcreve texto presente na imagem: placas, rótulos, cardápios, documentos.

Exemplo nos óculos: o usuário olha para um cardápio ou para o rótulo de um remédio e o app lê o conteúdo em voz alta (a síntese de voz será vista no tópico 1.8).

Peso on-device: médio. O ML Kit oferece OCR on-device pronto, com bom desempenho em texto impresso; texto manuscrito ou em ângulos extremos degrada o resultado. Com a câmera em primeira pessoa, enquadramento e estabilidade importam: texto pequeno e distante é o principal inimigo.

#### 12.12.3.5 Landmarks (rosto, mãos e pose)

O que resolve: em vez de caixas, retorna pontos-chave: os 468 pontos da malha facial, as juntas dos dedos, as articulações do corpo. Com esses pontos dá para inferir gestos, expressões e posturas.

Exemplo nos óculos: como a câmera é em primeira pessoa, as mãos do próprio usuário aparecem no campo de visão — dá para reconhecer gestos das mãos como comandos. Para pose, o alvo é outra pessoa: um app de treino que observa a postura do aluno e corrige por áudio.

Peso on-device: leve a médio. Os pipelines de landmarks do MediaPipe foram criados para tempo real em celular.

#### 12.12.3.6 Captioning / descrição de imagem

O que resolve: gera uma frase em linguagem natural descrevendo a cena ("uma pessoa segurando uma caneca vermelha ao lado de um notebook"). Diferente das tarefas anteriores, exige um modelo multimodal (visão + linguagem).

Exemplo nos óculos: descrever a cena completa em voz alta para uma pessoa com deficiência visual — o caso de uso mais direto da combinação câmera + alto-falantes.

Peso on-device: pesado — é a tarefa mais cara do catálogo. Modelos multimodais compactos para dispositivo existem e evoluem rápido, mas espere latência de segundos, não milissegundos. Para o hackathon, trate captioning como recurso pontual (descrever sob demanda), não como algo contínuo por frame.

#### 12.12.3.7 Rastreamento (tracking)

O que resolve: mantém a identidade de um objeto ao longo dos frames: "a pessoa da caixa 3 no frame anterior é a mesma da caixa 1 agora". Normalmente é uma camada sobre a detecção, não um modelo separado.

Exemplo nos óculos: seguir um objeto de interesse enquanto o usuário move a cabeça (e a câmera junto), ou contar objetos que cruzam o campo de visão sem contar duas vezes.

Peso on-device: custo adicional moderado sobre a detecção — trackers clássicos são baratos; o desafio nos óculos é que a câmera se move o tempo todo com a cabeça do usuário, o que exige detecção frequente para reancorar o rastreamento.

#### 12.12.3.8 O catálogo em uma tabela

As tarefas estão descritas na Tabela 13.

**Tabela 13 – Catálogo de tarefas de visão computacional, resultados esperados e caminhos de implementação on-device**

Tarefa O que entrega Peso on-device Caminho típico Classificação rótulo(s) da imagem inteira leve ML Kit (image labeling) ou modelo próprio

Detecção de objetos caixas + classes + confiança leve a médio YOLO via LiteRT; ML Kit/MediaPipe

Segmentação máscara pixel a pixel médio a pesado MediaPipe; modelo próprio OCR texto localizado e transcrito médio ML Kit (text recognition)

Landmarks pontos-chave de rosto/mãos/pose leve a médio MediaPipe; ML Kit

Captioning frase descrevendo a cena pesado modelo multimodal Rastreamento identidade entre frames adicional sobre detecção camada sobre o detector

Fonte: autoria própria.

Na prática: ao escolher a tarefa para a sua ideia, pense no ciclo completo dos óculos: o frame chega ao celular por streaming Bluetooth (como será visto no tópico 2.5), o modelo roda no celular e o resultado precisa virar áudio útil nos alto-falantes. Uma tarefa leve rodando de forma contínua (detecção) e uma pesada rodando sob demanda (captioning quando o usuário pede) podem conviver no mesmo app — essa é uma composição comum e eficiente. Atenção! "Roda no meu notebook" não significa "roda no celular". A viabilidade on-device se decide pelo tamanho do modelo, pela latência por frame no aparelho-alvo e pelo consumo de bateria (revisite o tópico 1.5). Sempre valide no dispositivo físico o quanto antes.

#### 12.12.3.9 YOLO: a família de referência para detecção em tempo real

YOLO ("You Only Look Once") é uma das famílias de modelos mais famosas e eficientes para detecção de objetos em tempo real. A ideia que dá nome à família: em vez de analisar a imagem em várias etapas, o modelo faz uma única passada pela rede e já produz todas as caixas e classes. Esse desenho o torna rápido — e é por isso que ele se encaixa tão bem no cenário dos óculos, em que frames de uma câmera em primeira pessoa chegam continuamente e precisam de resposta com baixa latência.

A implementação mais usada hoje é a da Ultralytics, que mantém as gerações recentes da família (YOLOv8, YOLO11 e, mais recentemente, YOLO26) com uma API Python unificada de treino e exportação.

##### 12.12.3.9.1 Tamanhos e variantes

Cada geração YOLO vem em variantes de tamanhos diferentes — tipicamente n (nano), s (small), m (medium), l (large) e x (extra). Todas fazem a mesma tarefa; o que muda é o trade-off (Tabela 14):

**Tabela 14 – Variantes do YOLO: precisão, velocidade e adequação ao edge Variante Precisão Velocidade / leveza Candidata a edge?**

n (nano) menor máxima sim — primeira escolha para CPU de celular

s (small) boa alta sim — boa com GPU ou CPU forte m (medium) melhor média só com GPU/acelerador l / x máxima baixa raramente; pensadas para servidor/desktop

Fonte: autoria própria.

A regra prática para o programa: comece pela nano, meça a latência no celular-alvo e só suba de variante se a precisão for insuficiente e houver folga de desempenho (por exemplo, com GPU disponível — ver adiante).

##### 12.12.3.9.2 Transfer learning: não treine do zero

Os modelos YOLO pré-treinados aprenderam com datasets grandes de uso geral (como o COCO, com 80 classes do cotidiano). Nesse processo, as camadas iniciais da rede aprenderam a enxergar coisas universais: bordas, texturas, formas, partes de objetos. Transfer learning é aproveitar todo esse conhecimento como ponto de partida em vez de começar com uma rede "em branco". A analogia: contratar alguém que já sabe ler e escrever e ensiná-lo o vocabulário técnico da sua área — em vez de alfabetizá-lo do zero.

##### 12.12.3.9.3 Fine-tuning: o modelo aprende as SUAS classes

Fine-tuning é a aplicação prática do transfer learning: você pega o modelo pré-treinado e o re-treina com um dataset próprio, contendo as classes que interessam à sua equipe (os objetos da sua ideia de hackathon: um tipo de equipamento, um produto específico, uma sinalização). Como o modelo já "sabe ver", ele precisa de pouco dado e pouco tempo para aprender as classes novas — algumas centenas de imagens anotadas e algumas dezenas de épocas de treino

costumam bastar para um protótipo funcional. Isso é perfeitamente viável dentro do cronograma do programa, inclusive usando GPUs gratuitas de serviços como o Google Colab.

O que a equipe precisa produzir é o dataset: imagens dos objetos-alvo (idealmente capturadas em condições parecidas com as reais — primeira pessoa, distâncias e iluminações variadas) e as anotações de caixa para cada objeto.

Nota 16: o resultado do fine-tuning é um modelo em precisão cheia (float32), no formato de treino (.pt, do PyTorch). Ele ainda não está pronto para o celular: como visto no 1.6, falta o passo de otimização/quantização e a conversão para um formato de execução móvel (como .tflite). Fine-tuning e otimização são etapas distintas e ambas necessárias — a seção "Na prática" percorre as duas.

#### 12.12.3.10 Como levar uma tarefa ao edge: três caminhos

Com a tarefa escolhida, há três caminhos de implementação no Android — do mais pronto ao mais flexível (Figura 12):

1. ML Kit — APIs prontas do Google para tarefas comuns (OCR, detecção genérica de objetos, landmarks de rosto e pose, image labeling, entre outras). Você não treina nem converte nada: chama a API e recebe o resultado. É o caminho mais rápido quando a tarefa padrão resolve o seu problema. Algumas APIs aceitam plugar um modelo customizado, mas o forte do ML Kit é a prateleira pronta. 2. MediaPipe Tasks — pipelines prontos de visão (detecção, segmentação, landmarks de mãos/rosto/pose) com pré e pós-processamento embutidos, permitindo trocar o modelo por um seu quando suportado. Meio-termo entre prateleira e customização. 3. LiteRT — o runtime de inferência do Google AI Edge (evolução do TensorFlow Lite, visto no 1.6). Aqui você roda qualquer modelo seu — como o YOLO com fine-tuning — e cuida do pré e pós-processamento. Máxima flexibilidade, máximo trabalho. A decisão prática: se existe API pronta no ML Kit para a sua tarefa, comece por ela (você economiza dias de trabalho); se a tarefa existe no MediaPipe com possibilidade de modelo próprio, avalie-o; se você precisa de

classes/comportamento que só um modelo seu entrega — o caso do YOLO fine-tunado — o caminho é o LiteRT.

**Figura 12 – Três caminhos para levar uma tarefa de visão ao edge**

Fonte: autoria própria.

#### 12.12.3.11 Onde o modelo roda no celular: CPU, GPU e aceleradores

O celular pareado tem mais de um processador capaz de rodar inferência:

- CPU — o caminho universal: funciona em qualquer aparelho, sem verificação prévia. O LiteRT usa por padrão kernels otimizados (XNNPack) e várias threads. Para modelos pequenos (uma YOLO nano quantizada), a CPU costuma bastar.

- GPU — via delegate (um plugin do runtime que desvia a execução para outro processador). Em aparelhos compatíveis, reduz a latência principalmente de modelos maiores, onde há mais cálculo para paralelizar.

Tem custos: inicialização mais lenta, nem todo aparelho suporta, e nem toda operação do modelo é aceita (o que não for cai de volta na CPU).

- NPU/DSP e outros aceleradores — chips dedicados a IA presentes em muitos aparelhos, acessados por delegates específicos de fabricante. A antiga via genérica (NNAPI) foi descontinuada pelo Android. Para o programa, trate CPU e GPU como os dois alvos realistas. Essa escolha influencia a variante do modelo: uma YOLO nano na CPU e uma YOLO small/medium na GPU podem ter latências parecidas — a segunda com mais precisão. Se o aparelho-alvo da equipe tem GPU compatível, vale testar uma variante acima; se o app precisa rodar em qualquer aparelho, projete para a nano na CPU e trate a GPU como bônus.

Nota 17: GPU não é sinônimo de "sempre mais rápido". Para modelos muito pequenos, o overhead de preparar a execução na GPU pode comer o ganho. A única resposta confiável é medir a latência no aparelho-alvo, nos dois modos.

#### 12.12.3.12 Do frame ao resultado: o pipeline

Independentemente da tarefa e do caminho escolhidos, o fluxo de execução é sempre o mesmo (Figura 13):

**Figura 13 – Do frame ao resultado: o pipeline universal de visão**

Fonte: autoria própria.

1. Frame — uma imagem individual do fluxo da câmera. Aqui tratamos o frame como um Bitmap genérico; a captura real vinda dos óculos será vista no tópico 2.5. 2. Pré-processamento — o modelo espera entrada em tamanho e formato fixos (ex.: 640×640 pixels, valores normalizados). Todo frame precisa ser convertido antes.

3. Inferência — uma passada do modelo sobre a entrada. É o passo cujo custo você negociou ao escolher tarefa, variante e processador. 4. Pós-processamento — a saída crua do modelo vira resultado útil: no caso da detecção, decodificar as caixas, descartar as de baixa confiança (threshold) e remover caixas duplicadas sobre o mesmo objeto (NMS — non-maximum suppression). 5. Resultado → ação — o app decide o que fazer: falar, registrar, alertar. Como a câmera gera frames mais rápido do que o modelo processa, o pipeline processa um frame por vez e descarta os que chegam durante a inferência — estratégia vista com Flow no 1.2 (conflate). Processar todo frame em fila só acumula atraso.

### 12.12.4 Na prática

Vamos percorrer o fio condutor completo do tópico com um exemplo concreto: um app de segurança de obra que detecta capacete e colete no campo de visão. O caminho tem três etapas: fine-tuning (Python, no computador) → quantização/conversão (Python) → execução no celular (Kotlin, CPU ou GPU).

#### 12.12.4.1 Etapa 1 — Fine-tuning do YOLO com o dataset da equipe

No computador (não no Android), crie o ambiente e instale a biblioteca da Ultralytics:

# ambiente isolado com uv uv venv uv pip install ultralytics

**Código 1.7-01 · também no notebook companion, seção 1.7.4.1 Organize o dataset no formato YOLO: imagens + um arquivo .txt de anotações por imagem (classe e coordenadas da caixa), e um data.yaml descrevendo tudo:**

▶ Código 1.7-02 – dataset/data.yaml — código completo no notebook companion (seção 1.7.4.1).

O treino em si é curto — o transfer learning acontece na primeira linha, ao carregar os pesos pré-treinados:

▶ Código 1.7-03 – código completo no notebook companion (seção 1.7.4.1).

O melhor checkpoint fica salvo em runs/detect/train/weights/best.pt. Valide as métricas no conjunto de validação antes de seguir — não adianta otimizar um modelo ruim.

Nota 18: para anotar as imagens (desenhar as caixas), use qualquer ferramenta que exporte no formato YOLO. Capture imagens em primeira pessoa, parecidas com o que a câmera dos óculos verá: mesmas distâncias, ângulos e iluminação. Dataset parecido com a realidade vale mais do que dataset grande.

#### 12.12.4.2 Etapa 2 — Quantização e conversão (amarrando com o 1.6)

O best.pt é float32 e formato PyTorch — inadequado para o celular. Aplicamos agora o que o 1.6 ensinou: quantização INT8 e conversão para .tflite, em uma única chamada de exportação:

▶ Código 1.7-04 – código completo no notebook companion (seção 1.7.4.2).

Atenção! Não pule a validação pós-quantização. Rode o modelo quantizado sobre o conjunto de validação e compare as métricas com o float32 — como visto no 1.6, a quantização pode degradar a precisão, e é melhor descobrir isso agora do que no dispositivo.

#### 12.12.4.3 Etapa 3 — Execução no celular com LiteRT (CPU ou GPU)

No projeto Android, adicione as dependências do LiteRT e copie o .tflite para assets/:

▶ Código 1.7-05 – build.gradle.kts (módulo app) — código completo no notebook companion (seção 1.7.4.3).

Carregue o modelo e crie o interpretador, escolhendo CPU ou GPU conforme a compatibilidade do aparelho:

▶ Código 1.7-06 – Carrega o .tflite de assets/ como buffer mapeado em memória — código completo no notebook companion (seção 1.7.4.3).

É exatamente aqui que a escolha de variante volta à cena: se isDelegateSupportedOnThisDevice for verdadeiro no aparelho-alvo da equipe, você pode embarcar uma variante maior; se o app precisa rodar em qualquer aparelho, embarque a nano e conte só com a CPU.

#### 12.12.4.4 Etapa 4 — Do frame ao resultado

A inferência nunca roda na main thread — é trabalho pesado de CPU (como visto no 1.2, usamos Dispatchers.Default):

▶ Código 1.7-07 – código completo no notebook companion (seção 1.7.4.4).

preprocess e postprocess dependem do formato exato de entrada/saída do seu export: inspecione os tensores com interpreter.getInputTensor(0) e getOutputTensor(0) para confirmar dimensões e tipo. O pós-processamento da saída YOLO em TFLite (decodificação das caixas e NMS) varia conforme as opções de exportação.

Atenção! Erro comum: alimentar um modelo quantizado INT8 com dados float (ou vice-versa). O sintoma clássico é o modelo "rodar sem erro" mas devolver lixo — caixas absurdas, confianças zeradas. Antes de depurar o pipeline inteiro, cheque interpreter.getInputTensor(0).dataType() e garanta que o pré-processamento produz exatamente esse tipo. Na prática: monte um orçamento de latência de ponta a ponta. No hackathon, o frame ainda vai atravessar o Bluetooth antes de chegar ao seu pipeline (streaming visto no tópico 2.5) e o resultado ainda vira fala (TTS, tópico 1.8). Se a inferência sozinha consome 500 ms, a experiência total ficará lenta demais para um aviso de segurança. Meça cada etapa separadamente — e enquanto os óculos não estão disponíveis, o Mock Device Kit (tópico 2.7) permite exercitar esse pipeline com dados simulados.

### 12.12.5 Resumo / cheatsheet

- O catálogo de visão: classificação (o que é), detecção (o que + onde), segmentação (pixel a pixel), OCR (texto), landmarks (pontos-chave), captioning (descrição em frase), rastreamento (identidade entre frames).

- Peso on-device cresce nesta ordem aproximada: classificação < detecção (compacta) ≈ landmarks < OCR < segmentação < captioning.

- YOLO = detecção em tempo real em uma única passada; variantes n/s/m/l/x trocam precisão por velocidade — a nano é a candidata natural para edge.

- Transfer learning: partir de um modelo pré-treinado em dataset geral; fine-tuning: re-treiná-lo com as classes da sua equipe — pouco dado, pouco tempo, viável no programa.

- Fine-tuning não encerra o trabalho: o modelo ainda passa por quantização e conversão de formato (teoria no 1.6) antes de ir para o celular.

- Três caminhos de implementação: ML Kit (tarefas prontas) → MediaPipe (pipelines com modelo trocável) → LiteRT (modelo próprio, máxima flexibilidade).

- CPU roda em qualquer aparelho; GPU (via delegate) acelera modelos maiores em aparelhos compatíveis — a escolha do processador influencia a variante do modelo.

- Pipeline universal: frame → pré-processamento → inferência → pós-processamento (threshold + NMS) → resultado → ação (nos óculos, tipicamente áudio).

### 12.12.6 Referências

- ML KIT / GOOGLE. Disponível em: https://developers.google.com/ml-kit. Catálogo das APIs prontas de visão on-device (OCR, detecção, landmarks, image labeling).

- LITERT / GOOGLE AI EDGE. Disponível em: https://ai.google.dev/edge/litert. Documentação do runtime de inferência: guia Android, formatos e otimização.

- GPU ACCELERATION DELEGATE / GOOGLE AI EDGE. Disponível em: https://ai.google.dev/edge/litert/android/gpu. Como habilitar o delegate GPU com a Interpreter API e as dependências necessárias.

- MEDIAPIPE SOLUTIONS / GOOGLE AI EDGE. Disponível em: https://ai.google.dev/edge/mediapipe/solutions/guide. Pipelines prontos de visão (detecção, segmentação, landmarks de mãos/rosto/pose).

- ULTRALYTICS (repositório oficial). Disponível em: https://github.com/ultralytics/ultralytics. Código e documentação de treino, fine-tuning e exportação dos modelos YOLO.

- EXPORT YOLO TO TFLITE / ULTRALYTICS DOCS. Disponível em: https://docs.ultralytics.com/integrations/tflite. Guia oficial de exportação para TFLite, incluindo quantização INT8 com dados de calibração.

#### 12.12.6.1 Para ir além

MEDIAPIPE MODEL MAKER / GOOGLE AI EDGE. Disponível em: https://ai.google.dev/edge/mediapipe/solutions/model_maker. Transfer learning simplificado para os modelos usados nos pipelines do MediaPipe.

MODEL EXPORT / ULTRALYTICS DOCS. Disponível em: https://docs.ultralytics.com/modes/export. Todos os formatos e parâmetros de exportação disponíveis para os modelos YOLO.
