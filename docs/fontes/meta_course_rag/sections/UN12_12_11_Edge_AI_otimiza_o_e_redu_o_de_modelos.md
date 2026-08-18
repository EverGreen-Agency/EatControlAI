---
unit: 12
unit_title: "Curso de Kotlin/Android"
section: "12.11"
section_title: "Edge AI: otimização e redução de modelos"
source_file: "Un12_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN12_Curso_de_Kotlin_Android.md"
source_pages: [52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66]
language: "pt-BR"
---

## 12.11 Edge AI: otimização e redução de modelos

### 12.11.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar o que é Edge AI e ponderar seus benefícios (latência, privacidade, offline, custo) contra seus limites (memória, calor, energia).

- Comparar as técnicas de redução de modelos — quantização (post-training × quantization-aware), destilação, pruning e LoRA/adaptadores — e identificar quando cada uma compensa.

- Diferenciar os formatos .tflite/.litertlm, GGUF e ONNX, associando cada um ao runtime e ao hardware onde roda melhor.

- Distinguir CPU, GPU, NPU e TPU e escolher o alvo de execução adequado para celular × notebook.

- Montar a combinação otimização + formato + hardware que encaixa um modelo no hardware disponível no hackathon, priorizando latência.

### 12.11.2 Pré-requisitos

- Tópico 1.3 (Fundamentos de Android) — noção de app, processo e recursos compartilhados do aparelho.

- Tópico 1.5 (Background e bateria) — entender por que trabalho computacional contínuo custa energia e gera calor.

- Ferramentas: nenhuma é obrigatória para acompanhar este tópico (ele é conceitual). Para reproduzir os exemplos da seção 5: Android Studio (versão estável mais recente) e um celular físico Android.

### 12.11.3 Conceito / fundamentação

#### 12.11.3.1 O que é Edge AI

Edge AI é rodar modelos de IA no próprio dispositivo — celular, notebook, óculos — em vez de enviar os dados para um servidor na nuvem e esperar a resposta. "Edge" (borda) é o nome dado a esses dispositivos na ponta da rede, perto de quem gera os dados.

Uma analogia: nuvem é pedir comida por delivery — a cozinha é enorme e faz qualquer prato, mas você paga o frete (rede) e espera a entrega (latência). Edge AI é cozinhar em casa — a cozinha é menor e o cardápio mais limitado, mas o prato sai na hora, ninguém vê o que você come e funciona mesmo sem "entregador" disponível.

Os benefícios, um a um:

- Latência: sem viagem de ida e volta pela internet; a resposta depende só do hardware local. Para experiências em tempo real, isso é decisivo.

- Privacidade: os dados (imagens, áudio) não saem do aparelho. Importante quando a câmera está apontada para o mundo ao redor do usuário.

- Offline: funciona sem conexão — em um evento lotado com Wi-Fi saturado, isso deixa de ser detalhe.

- Custo: sem servidor para pagar e sem cobrança por chamada de API. E os limites, que são o motivo de este tópico existir:

- Memória: a RAM do celular é compartilhada com o sistema e os outros apps; um modelo de bilhões de parâmetros em precisão cheia simplesmente não cabe.

- Calor: inferência contínua esquenta o SoC; o sistema reage com thermal throttling (reduz a frequência dos chips), e a latência piora justamente quando o app mais roda.

- Energia: cada inferência consome bateria — e, como visto no 1.5, o Android pune apps que drenam energia em segundo plano.

Na Figura 9, veja uma comparação do custo da viagem pela rede em nuvem e Edge AI.

**Figura 9 – Nuvem × Edge AI: o custo da viagem pela rede**

Fonte: autoria própria.

Na prática: na arquitetura do programa, os óculos Ray-Ban Meta não executam modelos — eles capturam (câmera de 12 MP, 5 microfones) e reproduzem (alto-falantes open-ear), conversando por Bluetooth com o app companion Android. Toda a IA roda no celular. O usuário fala ou aponta o olhar, e espera a resposta em áudio: cada milissegundo de inferência entra na conta da experiência. Edge AI não é uma opção estética no hackathon — é a arquitetura obrigatória, e otimizar modelos é o que a torna viável.

#### 12.11.3.2 Por que modelos precisam "emagrecer": a conta da memória

Um modelo de rede neural é, na essência, um conjunto enorme de números chamados pesos (weights). Por padrão, cada peso é armazenado como um número de ponto flutuante de 32 bits — FP32, 4 bytes. A conta do tamanho mínimo do modelo é direta:

Memória dos pesos ≈ número de parâmetros × bytes por parâmetro

Na Tabela 7, a seguir, você encontra exemplos comparativos de diferentes tamanhos de modelos.

**Tabela 7 – Exemplos comparativos de diferentes tamanhos de modelos Modelo (parâmetros) FP32 (4 B) FP16 (2 B) INT8 (1 B) INT4 (0,5 B)**

1 bilhão (1B) ~4 GB ~2 GB ~1 GB ~0,5 GB

3 bilhões (3B) ~12 GB ~6 GB ~3 GB ~1,5 GB

7 bilhões (7B) ~28 GB ~14 GB ~7 GB ~3,5 GB

Nota: Valores aproximados, apenas para os pesos; a execução ainda exige memória extra para ativações e caches. Fonte: autoria própria.

Repare: um modelo 3B em FP32 exige ~12 GB só de pesos — mais RAM do que muitos celulares têm no total. O mesmo modelo em INT4 cai para ~1,5 GB e passa a ser plausível.

E não é só questão de "caber": em dispositivos móveis, a largura de banda de memória (velocidade com que os pesos são lidos da RAM) costuma ser o gargalo da inferência. Pesos menores = menos bytes trafegando = inferência mais rápida. Reduzir tamanho e ganhar latência andam juntos.

#### 12.11.3.3 Quantização: reduzir a precisão dos pesos

Quantização é representar os pesos (e, em alguns esquemas, também as ativações) com menos bits: FP32 → INT8 → INT4. A analogia clássica é a foto: o arquivo RAW da câmera tem informação de sobra; um JPEG bem comprimido é várias vezes menor e, para quase todos os usos, ninguém nota diferença. A quantização faz o mesmo com os números do modelo — sacrifica precisão numérica que o modelo, na maioria dos casos, nem estava usando de fato.

Há duas famílias de abordagem:

- Post-training quantization (PTQ) — quantiza um modelo já treinado. É o caminho barato: não exige retreinar, roda em minutos com ferramentas prontas e, para INT8, a perda de qualidade costuma ser pequena. Quanto mais agressiva a redução (INT4 ou menos), maior o risco de degradação perceptível.

- Quantization-aware training (QAT) — simula a quantização durante o treinamento (ou em um fine-tuning), para que o modelo aprenda a compensar o erro de arredondamento. Recupera qualidade em precisões baixas, mas exige pipeline de treino, dados e tempo de computação.

O trade-off central — precisão × tamanho × velocidade (Tabela 8):

**Tabela 8 – Comparação entre níveis de precisão quanto ao tamanho, à velocidade, à qualidade e ao custo de obtenção**

Precisão Tamanho relativo Velocidade Qualidade Custo de obter FP32 1× (referência) mais lento máxima nenhum (original) FP16 ~0,5× melhor em GPU quase idêntica conversão trivial

INT8 (PTQ) ~0,25× ganho típico de 2–3× em CPU baixo (sem retreino)

perda geralmente pequena

INT4 ~0,125×

perda pode ser visível

mais rápido ainda em runtimes com suporte baixo (PTQ) ou médio (QAT para recuperar)

Fonte: autoria própria.

Atenção! Quantização não é grátis. Modelos pequenos e tarefas sensíveis (números, nomes próprios, detalhes finos de imagem) tendem a sofrer mais em INT4. Nunca assuma que "continua bom": meça a qualidade depois de quantizar, na sua tarefa, antes de adotar a versão reduzida.

#### 12.11.3.4 Destilação: o professor e o aluno

Destilação (distillation) é treinar um modelo pequeno — o aluno (student) — para imitar as saídas de um modelo grande — o professor (teacher) (Figura 10). Em vez de aprender só com os rótulos "secos" do dataset, o aluno aprende com as distribuições de saída do professor (as probabilidades que ele atribui a cada resposta), que carregam informação mais rica sobre como o professor raciocina.

**Figura 10 – Destilação: o professor e o aluno**

Fonte: autoria própria.

Diferença essencial para a quantização: a quantização mantém a arquitetura e encolhe os números; a destilação cria um modelo de arquitetura menor por meio de treino. As duas se combinam — é comum destilar primeiro e quantizar o aluno depois.

Quando a destilação compensa:

- Quando mesmo o modelo quantizado ainda é grande ou lento demais para o alvo — você precisa de menos parâmetros, não só de parâmetros menores.

- Quando você tem acesso a dados representativos da tarefa e a computação para treinar.

- Quando o uso é um domínio restrito: o aluno não precisa saber tudo o que o professor sabe, só o recorte relevante — e aí um modelo muito menor mantém qualidade surpreendente. Quando não compensa: para um hackathon com prazo curto, treinar um aluno do zero raramente cabe no cronograma. O movimento realista é usar modelos pequenos já destilados e publicados pela comunidade/fabricantes — colhendo o benefício sem pagar o treino.

#### 12.11.3.5 Outras otimizações: pruning e LoRA/adaptadores

Pruning (poda) parte de uma observação: depois do treino, muitos pesos contribuem quase nada para o resultado. O pruning remove esses pesos (ou neurônios/blocos inteiros, no structured pruning), seguido em geral de um fine-tuning curto para recuperar a qualidade. Ressalva importante: zerar pesos avulsos só vira ganho real de velocidade se o runtime souber explorar essa esparsidade — remoção estruturada (blocos, canais) tende a se traduzir em ganho prático com mais facilidade.

LoRA (Low-Rank Adaptation) e adaptadores atacam um problema diferente: não reduzem o modelo, reduzem o custo de especializá-lo. Em vez de refinar bilhões de parâmetros, o LoRA congela o modelo base e treina apenas pequenas matrizes de baixo posto acopladas a ele — o "adaptador" resultante tem poucos megabytes. No edge, isso permite distribuir um modelo base e vários adaptadores leves, um por tarefa, em vez de vários modelos gigantes.

Nota 15: LoRA não é técnica de compressão — o modelo base continua do mesmo tamanho. Ele entra no cardápio porque resolve o custo de personalização e distribuição, que no edge (onde cada megabyte baixado e armazenado conta) é um gargalo real.

#### 12.11.3.6 Formatos de arquivo: por que o formato não é detalhe

Um modelo treinado precisa ser salvo em algum formato de arquivo — e essa escolha não é burocracia: o formato determina qual runtime consegue executar o modelo e, por consequência, em qual hardware ele roda com eficiência. É como codec de vídeo: o mesmo filme pode existir em vários formatos, mas só alguns têm decodificação acelerada por hardware na sua TV. Modelo em formato errado para o alvo = ou não roda, ou roda devagar.

Os três formatos que importam para o programa:

.tflite / .litertlm (ecossistema LiteRT). LiteRT é o runtime de inferência do Google para mobile e edge — é o novo nome do TensorFlow Lite. O .tflite é o formato clássico para modelos convertidos (visão, áudio, classificação...), compacto e desenhado para o celular, com acesso aos aceleradores do aparelho (GPU/NPU) via delegates — mecanismos que despacham a execução para hardware especializado. O .litertlm é o formato voltado a modelos de linguagem no LiteRT-LM. Cenário ideal: o alvo é o celular/Android — exatamente o caso do app companion.

GGUF. Formato do ecossistema ggml/llama.cpp, voltado a LLMs quantizados rodando em CPU (com offload opcional para GPU) de desktop/notebook. Um arquivo GGUF é autossuficiente: carrega pesos quantizados, tokenizer e metadados juntos — baixou, rodou. Cenário ideal: rodar um modelo de linguagem local no notebook do hackathon, para prototipagem e ferramentas de apoio do time.

ONNX. Formato interoperável: uma representação aberta do grafo do modelo que diversos frameworks exportam e diversos runtimes executam. O ONNX Runtime usa execution providers para despachar a execução em CPUs, GPUs e alguns aceleradores, em desktop, servidor e mobile. Cenário ideal: quando você

quer flexibilidade — treinar em um framework, executar em outro ambiente, ou manter portabilidade entre alvos diferentes.

Ideia-chave: o mesmo modelo pode existir em vários formatos. A pergunta certa nunca é "qual formato é melhor?", e sim "onde este modelo vai rodar (celular × notebook) e qual runtime vai executá-lo?" — a resposta define o formato (Tabela 9).

**Tabela 9 – Formatos de modelos, runtimes típicos e cenários ideais de execução Formato Runtime típico Hardware / cenário ideal**

.tflite LiteRT

Celular/Android (CPU, GPU e NPU via delegates) — modelos de visão, áudio e classificação no app companion

.litertlm LiteRT-LM LLMs compactos no celular/edge

GGUF llama.cpp e derivados CPU (e GPU) do notebook — LLM local para prototipagem no hackathon

ONNX ONNX Runtime

Portátil entre desktop, servidor e alguns aceleradores — quando a flexibilidade importa

Fonte: autoria própria.

Na prática: a divisão natural no hackathon: o modelo que processa os frames dos óculos vai em .tflite dentro do app Android; se o time quiser um LLM auxiliar durante o desenvolvimento (gerar dados de teste, prototipar prompts), um GGUF quantizado rodando no notebook resolve sem custo de API.

#### 12.11.3.7 Onde roda: CPU × GPU × NPU × TPU

O formato define o runtime; o runtime despacha para o hardware. Conheça os quatro tipos de processador (Tabela 10):

**Tabela 10 – Comparação entre CPU, GPU, NPU e TPU quanto às características, funções e cenários de uso**

Processador O que é Para que serve Quando usar

CPU

Qualquer código; inferência de modelos pequenos/quantizados

Processador de propósito geral: poucos núcleos, muito flexíveis Sempre disponível — é o fallback universal; no notebook, é o alvo padrão de LLMs em GGUF

GPU

Milhares de núcleos simples em paralelo Operações matriciais massivas — modelos de visão, redes grandes

Quando o modelo é paralelizável e o ganho compensa o custo de energia; no Android, via delegate de GPU

NPU

Inferência frequente/contínua no celular, quando bateria e calor importam

Acelerador neural dedicado dentro do SoC do celular

Inferência de redes neurais (especialmente quantizadas) com máxima eficiência energética

TPU

ASIC do Google para redes neurais No contexto do hackathon, é o menos relevante — vocês não controlam esse hardware

Treino e inferência em escala (datacenter); variantes edge aparecem em alguns dispositivos Google

Fonte: autoria própria.

A intuição: CPU é o canivete suíço, GPU é a linha de produção, NPU é a máquina construída para uma única tarefa — e por isso a executa gastando muito menos energia por inferência.

Verificar: o acesso à NPU no Android está em transição — a NNAPI (API clássica de aceleração neural do Android) foi descontinuada, e o caminho recomendado passa pelos delegates/aceleradores do próprio runtime (LiteRT) e SDKs de fabricantes. Cheque a documentação oficial do LiteRT sobre aceleração antes de contar com a NPU no aparelho do time.

Celular × notebook — a diferença que muda a estratégia. O celular tem RAM menor e compartilhada, envelope térmico apertado e bateria; favorece modelos pequenos, quantizados, em .tflite, de preferência acelerados por NPU/GPU. O notebook do hackathon tem mais RAM, refrigeração ativa e tomada: aguenta modelos maiores e formatos mais pesados — um LLM 7B quantizado em GGUF roda tranquilamente na CPU. Regra prática: o que o usuário final vê na demo com os óculos precisa rodar no celular; o notebook é o laboratório do time.

#### 12.11.3.8 O menu de alternativas: escolhendo a combinação

Nenhuma técnica deste tópico vive sozinha — o trabalho do time é compor: otimização + formato + hardware, com a latência como critério de desempate (Figura 11). O raciocínio, em ordem:

1. Tarefa e orçamento de latência. O que o modelo faz e em quanto tempo a resposta precisa sair? Resposta por voz ao usuário dos óculos = orçamento apertado. 2. Alvo de hardware. Faz parte da demo com os óculos? → celular. É ferramenta interna do time? → notebook. 3. Runtime e formato decorrem do alvo. Celular → LiteRT → .tflite/.litertlm. Notebook + LLM → llama.cpp → GGUF. Múltiplos ambientes → ONNX. 4. Otimize até caber, do barato ao caro. Comece com PTQ INT8 (custo quase zero). Não coube ou está lento? INT4. Ainda não? Procure uma variante menor/destilada do modelo. Pruning e QAT são os últimos recursos — exigem treino. 5. Meça no alvo real. Latência e qualidade, no celular físico do time, com o app rodando de verdade. 6. Itere. Se sobrou folga, dá para subir a qualidade (modelo maior, mais precisão); se faltou, aperte mais um passo do item 4.

**Figura 11 – Funil de decisão: otimização + formato + hardware**

Fonte: autoria própria.

O tópico 1.7 aplica esse menu às tarefas de visão computacional (detecção, classificação, OCR nos frames da câmera dos óculos) — este tópico dá o vocabulário; o próximo, a aplicação.

### 12.11.4 Na prática

Este tópico é conceitual, então a prática aqui é fazer contas e tomar decisões com números — a habilidade que o time vai usar no hackathon antes de escrever qualquer linha de código de inferência.

#### 12.11.4.1 Passo 1 — Estime a memória antes de baixar

Antes de baixar qualquer modelo, aplique a fórmula da seção 4.2. Suponha um celular com 8 GB de RAM (dos quais o sistema e outros apps já consomem uma boa parte) e três candidatos de LLM compacto (Tabela 11):

**Tabela 11 – Estimativa de memória dos modelos em diferentes níveis de quantização e sua compatibilidade com dispositivos móveis**

Candidato FP16 INT8 INT4 Cabe no celular? 0,5B ~1 GB ~0,5 GB ~0,25 GB Sim, com folga 3B ~6 GB ~3 GB ~1,5 GB Só quantizado (INT8/INT4) 7B ~14 GB ~7 GB ~3,5 GB Arriscado até em INT4 — melhor no notebook

Fonte: autoria própria.

Dez segundos de aritmética eliminam becos sem saída de horas.

#### 12.11.4.2 Passo 2 — O efeito da quantização em um modelo de visão

Números ilustrativos para um classificador de imagens da classe MobileNet rodando no celular (Tabela 12):

**Tabela 12 – Comparação entre as versões FP32 e INT8 de um modelo MobileNet Versão Tamanho Latência por inferência (CPU) Acurácia**

FP32 ~16 MB ~90 ms (ilustrativo) referência INT8 (PTQ) ~4 MB ~30–45 ms (ilustrativo) queda tipicamente pequena

Fonte: autoria própria.

A redução de tamanho para ~¼ é aritmética (4 bytes → 1 byte por peso); o ganho de latência de 2–3× em CPU é a ordem de grandeza tipicamente citada pela documentação de quantização — o número real depende do chip, do modelo e do delegate, e só a medição no aparelho do time responde.

A 30 ms por frame, dá para analisar vários frames por segundo vindos dos óculos; a 90 ms, a experiência já engasga se houver mais etapas no pipeline.

#### 12.11.4.3 Passo 3 — O LLM do notebook em GGUF

O mesmo modelo em GGUF com quantização de ~4 bits cai para a faixa de ~4 GB e roda na CPU via llama.cpp, com velocidade de geração dependente do hardware (avalie na sua máquina).

Na prática: monte o orçamento de latência de ponta a ponta da sua demo: captura nos óculos → transferência Bluetooth → pré-processamento → inferência → síntese de voz → áudio nos alto-falantes. A inferência é só uma fatia do total — e o Bluetooth já consome parte do orçamento antes de o modelo ver o primeiro byte. É por isso que a escolha INT8 + acelerador, que parece "otimização prematura", é na verdade o que sobra de margem para o resto do pipeline.

#### 12.11.4.4 Passo 4 — Um gostinho do "como" no Android

O carregamento de um .tflite no app companion, só para visualizar onde as decisões deste tópico se materializam (o pipeline completo de visão é assunto do tópico 1.7):

▶ Código 1.6-01 – build.gradle.kts (módulo) — dependência do runtime LiteRT — código completo no notebook companion (seção 1.6.4.4).

Repare que a decisão de quantização já aconteceu antes do app: o arquivo nos assets já é o INT8. O código Android apenas colhe o resultado.

Atenção! Dois erros comuns nesta etapa:
1) Medir latência no emulador — o emulador não tem a NPU/GPU do aparelho
real e distorce tudo; meça sempre em um celular físico.
2) Assumir que INT8 é sempre mais rápido em qualquer processador —
delegates de GPU historicamente preferem FP16/FP32, e um modelo INT8
pode acabar caindo de volta na CPU. Combine a precisão escolhida com o alvo
de execução, e confirme medindo.

### 12.11.5 Resumo / cheatsheet

- Edge AI = inferência no próprio dispositivo: ganha latência, privacidade, offline e custo; paga em memória, calor e energia.

- Memória dos pesos ≈ parâmetros × bytes por parâmetro: FP32 = 4 B, FP16 = 2 B, INT8 = 1 B, INT4 = 0,5 B.

- Quantização: PTQ é barata (sem retreino, boa em INT8); QAT recupera qualidade em bits baixos, mas exige treinar.

- Destilação cria um modelo menor em arquitetura treinado para imitar um maior; combina com quantização; no hackathon, use alunos já publicados.

- Pruning remove pesos pouco importantes (ganho depende do runtime); LoRA especializa com adaptadores de poucos MB sem mexer no modelo base.

- Formato define runtime e hardware: .tflite/.litertlm → LiteRT → celular; GGUF → llama.cpp → notebook; ONNX → portátil entre ambientes.

- CPU = fallback universal; GPU = paralelismo bruto; NPU = eficiência energética no celular; TPU = ASIC do Google, pouco relevante no hackathon.

- Receita: alvo de hardware → formato do runtime → INT8 primeiro → medir latência e qualidade no aparelho real → iterar.

### 12.11.6 Referências

- LiteRT — Google AI Edge. Disponível em: https://ai.google.dev/edge/litert. Visão geral do runtime de inferência para mobile/edge (ex-TensorFlow Lite), conversão de modelos e aceleração por delegates.

- Post-training quantization — Google AI Edge. Disponível em: https://ai.google.dev/edge/litert/models/post_training_quantization. Guia oficial de quantização pós-treinamento: opções (dynamic range, INT8 completo, FP16) e trade-offs.

- LiteRT-LM — repositório oficial (google-ai-edge). Disponível em: https://github.com/google-ai-edge/LiteRT-LM. Runtime e formato para executar modelos de linguagem no ecossistema LiteRT.

- llama.cpp — repositório oficial (ggml-org). Disponível em: https://github.com/ggml-org/llama.cpp. Runtime de LLMs quantizados em CPU/GPU de desktop e notebook; documentação do ecossistema GGUF.

- Especificação GGUF — ggml (ggml-org). Disponível em: https://github.com/ggml-org/ggml/blob/master/docs/gguf.md. Especificação técnica do formato GGUF (estrutura do arquivo, metadados, tipos de quantização).

- ONNX. Disponível em: https://onnx.ai. Especificação do formato aberto e ecossistema de ferramentas de interoperabilidade.

- ONNX Runtime — Microsoft. Disponível em: https://onnxruntime.ai. Runtime multiplataforma para modelos ONNX e seus execution providers (CPU, GPU, aceleradores).

#### 12.11.6.1 Para ir além

TensorFlow Model Optimization Toolkit. Disponível em: https://www.tensorflow.org/model_optimization. Ferramentas oficiais de quantização (PTQ e QAT) e pruning, com tutoriais.

LLM Inference — MediaPipe / Google AI Edge. Disponível em: https://ai.google.dev/edge/mediapipe/solutions/genai/llm_ inference. Execução de LLMs on-device no Android, incluindo suporte a adaptadores LoRA.

AI no Android — Android Developers. Disponível em: https://developer.android.com/ai. Panorama oficial das opções de IA on-device na plataforma Android.
