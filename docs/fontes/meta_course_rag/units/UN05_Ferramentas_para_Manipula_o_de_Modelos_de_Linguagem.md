---
corpus: "CEIA e Meta - AI Glasses Brasil"
unit: 5
title: "Ferramentas para Manipulação de Modelos de Linguagem"
source_file: "Un5_curso_meta(1).pdf"
source_pages: 20
source_sha256: "ed52e646266bf7c8a91e36282979f9851cb1a42a7da54979bab8005e3c24e2b8"
language: "pt-BR"
normalization: "text layer normalized; page provenance preserved; visual-only supplements transcribed from source pages when identified"
---

# Unidade 5: Ferramentas para Manipulação de Modelos de Linguagem

> Fonte única: `Un5_curso_meta(1).pdf`. O conteúdo abaixo foi normalizado para busca/RAG, sem complementação por fontes externas.

<!-- source_page: 1 -->

# Unidade III - Ferramentas para Manipulação de Modelos de Linguagem

<!-- source_page: 2 -->

# Unidade V - Ferramentas para Manipulação de Modelos de Linguagem

## 5.1 Ferramentas Populares para Manipulação de Modelos de Linguagem

Nas Unidades anteriores, exploramos os fundamentos da geração de texto, aprendemos a elaborar prompts eficazes e descobrimos como usar plataformas online para interagir com modelos de linguagem. Agora, vamos dar um passo adiante e conhecer ferramentas mais avançadas que permitam a manipulação e personalização desses modelos. Prepare-se para assumir o controle e turbinar suas aplicações de IA Generativa!

### 5.1.1 Por que Manipular Modelos de Linguagem?

Imagine que você tem um modelo de linguagem poderoso, mas ele não se adapta perfeitamente à sua tarefa específica. As ferramentas de manipulação permitem:

- Ajuste fino11 (fine-tuning): adaptar o modelo a um domínio específico, como linguagem médica ou jurídica, melhorando sua performance em tarefas especializadas.

- Personalização: criar modelos personalizados que refletem seu estilo de escrita, tom de voz ou preferências específicas.

- Otimização: melhorar a eficiência do modelo, reduzindo o tempo de resposta e o consumo de recursos.

### 5.1.2 Ferramentas Essenciais

#### 5.1.2.1 Hugging Face Transformers®

A Hugging Face® é uma plataforma que oferece uma ampla biblioteca de modelos de linguagem pré-treinados e ferramentas para sua manipulação. A biblioteca Transformers® em Python permite carregar, treinar e utilizar modelos como BERT®, GPT-2®, GPT-3® e outros (Código 1).

11 Processo de personalização de modelos de linguagem pré-treinados em grandes conjuntos de dados para tarefas específicas, utilizando dados especializados.

<!-- source_page: 3 -->

Características:

- Suporte a múltiplos modelos e arquiteturas.

- Fácil integração com outras bibliotecas, como PyTorch® e TensorFlow®.

- Comunidade ativa e ampla documentação.

**Código 1 - Exemplo de uso: importando o pipeline da biblioteca Transformers**

```text
# Importa o pipeline da biblioteca transformers, que permite usar modelos pré-
treinados para várias tarefas de NLP (Processamento de Linguagem Natural)
from transformers import pipeline
# Cria um pipeline de geração de texto utilizando o modelo pré-treinado GPT-2
gerador_texto = pipeline('text-generation', model='gpt2')
# Define o prompt inicial que será usado como entrada para o modelo
prompt = "A inteligência artificial está transformando"
```

```text
# Gera texto com base no prompt fornecido, limitando o resultado a 50 tokens e
gerando apenas 1 sequência
resultado = gerador_texto(prompt, max_length=50, num_return_sequences=1)
```

```text
# Imprime o texto gerado pelo modelo, acessando o campo 'generated_text' da
primeira sequência retornada
print(resultado[0]['generated_text'])
```

Nota: linguagem Python. Fonte: autoria própria. Aplicações:

- Geração de texto contínuo a partir de um prompt.

- Resumo automático de textos.

- Tradução automática.

#### 5.1.2.2 OpenAI API®

A OpenAI® oferece acesso a modelos avançados, como o GPT-3® e GPT-4®, por meio de uma Interface de Programação de Aplicações (API)12 (Código 2). Isso permite que desenvolvedores integrem facilmente recursos de geração de texto em suas aplicações, sem a necessidade de hospedar o modelo localmente.

12 Permite a integração de diferentes sistemas para acessar funcionalidades ou dados de um serviço.

<!-- source_page: 4 -->

Características:

- Acesso a modelos poderosos e atualizados.

- Simplicidade na integração via chamadas de API.

- Controle sobre parâmetros como temperatura, comprimento máximo da resposta e formato.

**Código 2 - Exemplo de uso: importando a biblioteca OpenAI®**

```text
# Importa a biblioteca OpenAI, que fornece acesso à
API de modelos de linguagem, como o GPT
import OpenAI
```

# Define a chave de API necessária para autenticação na plataforma OpenAI OpenAI®.api_key = 'sua-chave-api'

```text
# Define o prompt que será enviado para o modelo, contendo a instrução ou
pergunta
prompt = "Explique a importância da reciclagem para o meio ambiente."
```

```text
# Envia uma solicitação para a API da OpenAI para
gerar uma resposta baseada no prompt
# - engine: Define o modelo que será utilizado (text-davinci-003, neste caso)
# - prompt: O texto de entrada que orienta o modelo
# - max_tokens: Limita o número máximo de tokens na
resposta gerada (100 tokens neste caso)
# - temperature: Controla o nível de criatividade da resposta (valores mais
altos geram respostas mais criativas)
resposta = OpenAI.Completion.create(
engine="text-davinci-003",
prompt=prompt,
max_tokens=100,
temperature=0.7)
```

```text
# Exibe a resposta gerada pelo modelo, acessando o texto da primeira escolha
retornada e removendo espaços extras
print(resposta.choices[0].text.strip())
```

Nota: linguagem Python. Fonte: autoria própria.

<!-- source_page: 5 -->

Aplicações:

- Assistentes virtuais e chatbots avançados.

- Geração de conteúdo personalizado.

- Análise e interpretação de linguagem natural.

#### 5.1.2.3 Google Cloud Natural Language API®

O Google Cloud® oferece serviços para processamento de linguagem natural, incluindo análise de sentimentos, extração de entidades e sintaxe (Código 3). Embora não seja focado na geração de texto, é uma ferramenta poderosa para manipulação e compreensão de linguagem. Características:

- Análise de sentimento em textos.

- Reconhecimento de entidades nomeadas (pessoas, lugares, organizações).

- Análise sintática e categorização de conteúdo.

**Código 3 - Exemplo de uso: importando a biblioteca do cliente para o Google Cloud Natural Language API®**

```text
# Importa a biblioteca do cliente para o Google Cloud Natural Language API
from google.cloud import language_v1
```

```text
# Inicializa o cliente para interagir com a API de análise de linguagem
client = language_v1.LanguageServiceClient()
```

```text
# Define o texto que será analisado para sentimentos
text_content = "A inovação tecnológica é essencial para o progresso da
sociedade."
```

```text
# Cria um objeto de documento que contém o texto e especifica o tipo de
conteúdo como texto simples (plain text)
document = language_v1.Document(content=text_content, type_=language_
v1.Document.Type.PLAIN_TEXT)
```

```text
# Realiza a análise de sentimento no texto fornecido e armazena os resultados
# - request: Dicionário que especifica o documento a ser analisado
# - document_sentiment: Retorna os detalhes do sentimento, como score e
magnitude
sentiment = client.analyze_sentiment(request={'document': document}).document_
sentiment
continua
```

<!-- source_page: 6 -->

```text
# Exibe o score do sentimento, indicando se o texto é positivo (valores
próximos a 1), negativo (valores próximos a -1) ou neutro (próximo a 0)
print("Score de Sentimento:", sentiment.score)
```

```text
# Exibe a magnitude do sentimento, que indica a intensidade emocional
do texto, independentemente de ser positiva ou negativa
print("Magnitude do Sentimento:", sentiment.magnitude)
```

Nota: linguagem Python. Fonte: autoria própria. Aplicações:

- Monitoramento de opiniões em redes sociais.

- Análise de feedbacks de clientes.

- Classificação de documentos.

#### 5.1.2.4 IBM Watson Natural Language Understanding®

O IBM Watson NLU® é uma plataforma que fornece serviços de processamento de linguagem natural para análise de texto, incluindo extração de conceitos, categorias, emoções e muito mais (Código 4). Características:

- Análise de emoções e sentimentos.

- Extração de conceitos, palavras-chave e categorias.

- Identificação de relações entre entidades.

**Código 4 - Exemplo de uso: importando o cliente para o IBM Watson Natural Language Understanding®**

```text
# Importa o cliente para o IBM Watson Natural Language Understanding
from ibm_watson import NaturalLanguageUnderstandingV1
```

```text
# Importa as classes necessárias para configurar as features de análise,
incluindo análise de emoções
from ibm_watson.natural_language_understanding_v1 import Features,
EmotionOptions
```

```text
# Inicializa o cliente para o IBM Watson Natural Language
Understanding, configurando a versão da API,
# a chave de API para autenticação e a URL do serviço
```

<!-- source_page: 7 -->

```text
natural_language_understanding = NaturalLanguageUnderstandingV1(
version='2021-08-01',
iam_apikey='sua-chave-api',
url='sua-url'
)
```

```text
# Realiza a análise de emoções no texto fornecido
# - text: Define o texto que será analisado
# - features: Especifica o tipo de análise a ser realizada (emoções no
documento neste caso)
response = natural_language_understanding.analyze(
text="A tecnologia blockchain está revolucionando o setor financeiro.",
features=Features(emotion=EmotionOptions(document=True))
).get_result()
```

```text
# Exibe as emoções detectadas no texto, retornadas como um dicionário com os
níveis de emoção (alegria, tristeza, etc.)
print(response['emotion']['document']['emotion'])
```

Nota: linguagem Python. Fonte: autoria própria. Aplicações:

- Compreensão profunda de textos complexos.

- Desenvolvimento de aplicações de atendimento ao cliente.

- Pesquisa e análise de mercado.

#### 5.1.2.5 Demais Ferramentas

- Bibliotecas de machine learning:

- PyTorch®: um framework flexível e popular para programar modelos de deep learning, com suporte a GPUs e recursos para construir e treinar modelos de linguagem complexos.

- TensorFlow®: framework poderoso para programar modelos de deep learning, desenvolvido pelo Google®, com ampla comunidade e recursos para diversas aplicações de IA.

- Plataformas de desenvolvimento:

- Google Colaboratory® (Colab): um ambiente de desenvolvimento online gratuito que oferece acesso a GPUs, ideal para experimentar modelos de linguagem e executar códigos complexos.

<!-- source_page: 8 -->

- Amazon SageMaker®: Uma plataforma completa para construir, treinar e implantar modelos de machine learning, incluindo modelos de linguagem, com recursos de escalabilidade e integração com outros serviços da AWS®.

- Ferramentas de ajuste fino:

- OpenAI Fine-tuning API®: permite ajustar modelos da OpenAI®, como o GPT-3®, com seus próprios dados, personalizando-os para suas necessidades específicas.

- Hugging Face Trainer®: Uma ferramenta simplificada para ajuste fino de modelos da Hugging Face®, com interface intuitiva e recursos para monitorar o progresso do treinamento.

- Ferramentas de monitoramento e avaliação:

- Weights & Biases®13: uma plataforma para rastrear experimentos de machine learning, visualizar métricas, comparar modelos e otimizar o desempenho.

- TensorBoard®: uma ferramenta de visualização para TensorFlow® que permite monitorar o treinamento de modelos, analisar gráficos e visualizar dados em tempo real.

### 5.1.3 Como Escolher a Ferramenta Adequada?

Ao selecionar uma ferramenta para manipulação de modelos de linguagem para seu projeto específico, considere:

- Objetivo do projeto: defina se precisa de geração de texto, análise de sentimento, tradução etc.

- Facilidade de uso: avalie o nível de conhecimento técnico necessário para implementar a ferramenta.

- Custo: verifique os custos associados, como planos pagos ou limites de uso nas versões gratuitas.

- Escalabilidade: considere se a ferramenta suporta o volume de dados que você pretende processar.

- Suporte e comunidade: verifique a disponibilidade de documentação, tutoriais e suporte da comunidade.

13 Tendências ou preconceitos inerentes aos dados usados para treinar modelos de IA, que podem influenciar negativamente os resultados gerados

<!-- source_page: 9 -->

## 5.2 Ajuste e Personalização de Modelos

Na seção anterior, exploramos as ferramentas que permitem a manipulação de modelos de linguagem. Modelos de linguagem generativa pré-treinados são ferramentas poderosas, mas podem não atender perfeitamente às necessidades específicas de uma aplicação. O ajuste e a personalização permitem adaptar esses modelos para tarefas específicas, melhorando sua eficácia e adequação a contextos particulares. Nesta seção, exploraremos os conceitos de ajuste fino (fine-tuning) e personalização, apresentando exemplos práticos e destacando como implementar essas técnicas.

### 5.2.1 Por que Ajustar e Personalizar um Modelo?

Os modelos pré-treinados são desenvolvidos com base em grandes volumes de dados genéricos, abrangendo uma ampla variedade de tópicos. No entanto, aplicações específicas podem exigir:

- Vocabulário especializado: adaptação para áreas como saúde, direito ou engenharia;

- Tons e estilos: ajuste para linguagem formal, criativa ou voltada para o público infantil;

- Melhor desempenho em tarefas específicas: como tradução, classificação de sentimentos14 ou geração de respostas técnicas.

O ajuste fino e a personalização permitem que os modelos aprendam nuances e peculiaridades específicas, melhorando sua performance em contextos definidos.

### 5.2.2 Ajuste Fino (Fine-tuning)

Imagine que você quer usar um modelo de linguagem para gerar textos médicos, mas ele foi treinado em um conjunto de dados geral, com textos de diversas áreas. O ajuste fino permite "especializar" o modelo, treinando-o com dados específicos da área médica.

- Como funciona?

- Ponto de partida: um modelo de linguagem pré-treinado, como o GPT-3®, serve como modelo base.

14 Técnica de análise de texto que identifica emoções expressas, como positivo, negativo ou neutro.

<!-- source_page: 10 -->

- Dados específicos: você precisa de um conjunto de dados de treinamento com textos da área médica, como artigos científicos, prontuários médicos e bulas de medicamentos.

- Treinamento adicional: o modelo é treinado com esses dados específicos, ajustando seus parâmetros para se adaptar à linguagem e aos conceitos da área médica.

- Resultado: um modelo mais preciso e eficiente para gerar textos médicos, com vocabulário especializado e conhecimento do domínio.

- Exemplos:

- Tradução especializada: ajustar um modelo para traduzir textos jurídicos, com precisão na terminologia e nos termos específicos.

- Análise de sentimentos em um nicho: adaptar um modelo para analisar sentimentos em reviews de produtos de beleza, com foco em termos e expressões comuns nesse nicho.

- Geração de código em uma linguagem específica: ajustar um modelo para gerar código na linguagem Python, com conhecimento das bibliotecas e frameworks mais utilizados.

No Código 5, veja um exemplo de uso de tokenização e treinamento do modelo.

**Código 5 - Exemplo de uso: importando as classes necessárias da biblioteca Transformers para tokenização e treinamento do modelo**

```text
# Importa as classes necessárias da biblioteca transformers
para tokenização e treinamento do modelo
from transformers import GPT2Tokenizer, GPT2LMHeadModel, Trainer,
TrainingArguments
# Carrega o modelo GPT-2 pré-treinado para geração de texto
model = GPT2LMHeadModel.from_pretrained("gpt2")
# Carrega o tokenizador correspondente ao modelo GPT-2
tokenizer = GPT2Tokenizer.from_pretrained("gpt2")
# Preparar conjunto de dados
texts = ["Contrato de compra e venda...", "Ação de execução de título..."]
# Prepara o conjunto de dados para treinamento
# - texts: Lista de textos que será usada como dados de entrada para o modelo
# - return_tensors: Define o formato de saída como tensores PyTorch
# - max_length: Limita o tamanho máximo de cada sequência
continua
```

<!-- source_page: 11 -->

```text
# - truncation: Trunca textos que excedem o tamanho máximo
# - padding: Adiciona preenchimento para igualar o comprimento das sequências
inputs = tokenizer(texts, return_tensors="pt", max_length=512, truncation=True,
padding=True)
# # Configura os argumentos para o treinamento do modelo
# - output_dir: Diretório onde os resultados do treinamento serão salvos
# - num_train_epochs: Número de épocas para o treinamento
# - per_device_train_batch_size: Tamanho do lote
usado por dispositivo (ex.: GPU)
# - save_steps: Salva o modelo a cada N passos
# - save_total_limit: Número máximo de checkpoints salvos
# - logging_dir: Diretório para salvar os logs do treinamento
training_args = TrainingArguments(
output_dir="./results",
num_train_epochs=3,
per_device_train_batch_size=4,
save_steps=10_000,
save_total_limit=2,
logging_dir="./logs",
)
# Cria o objeto Trainer para realizar o treinamento
# - model: O modelo que será treinado
# - args: Configurações do treinamento
# - train_dataset: O conjunto de dados preparado para o treinamento
trainer = Trainer(
model=model,
args=training_args,
train_dataset=inputs,
)
```

# Inicia o processo de treinamento trainer.train()

Nota: linguagem Python. Fonte: autoria própria.

### 5.2.3 Adaptação por Engenharia de Prompts

Nem sempre é necessário ajustar o modelo completamente. Uma técnica mais leve é criar prompts bem estruturados que orientem o modelo a produzir resultados adequados ao domínio.

<!-- source_page: 12 -->

Exemplo de uso: Para obter descrições criativas de produtos, um prompt pode incluir instruções como: "Escreva uma descrição detalhada e criativa para um smartphone que destaque sua câmera e durabilidade." Essa abordagem não modifica o modelo diretamente, mas guia sua saída por meio de prompts eficazes.

### 5.2.4 Ajuste por Dados de Aprendizado Ativo15

Outra abordagem é fornecer exemplos durante a interação com o modelo, permitindo que ele adapte suas respostas com base em feedback. Isso é útil em sistemas que aprendem continuamente com os usuários. Exemplo de Uso: Um chatbot treinado com feedback dos usuários para ajustar sua linguagem e melhorar sua capacidade de responder perguntas frequentes em um tom amigável.

### 5.2.5 Personalização

Que tal ter um modelo de linguagem que escreva no seu estilo, com seu tom de voz e suas preferências? A personalização permite criar modelos únicos, que refletem sua identidade.

- Como funciona?

- Dados pessoais: utilize seus próprios textos, como e-mails, artigos ou posts de mídia social, para treinar o modelo.

- Estilos e preferências: defina parâmetros para controlar o estilo de escrita, o tom de voz e outras características do texto gerado.

- Feedback e iteração: ajuste o modelo com base no feedback dos seus textos, refinando-o até que ele atenda às suas expectativas.

- Exemplos:

- Escrita criativa: criar um modelo que escreve poemas no seu estilo, com suas rimas e métricas preferidas.

- Redação de e-mails: personalizar um modelo para gerar respostas de e-mail no seu tom de voz, com suas frases e saudações características.

15 Método de aprendizado de máquina em que o modelo solicita ao usuário informações adicionais ou exemplos para melhorar sua performance.

<!-- source_page: 13 -->

- Criação de conteúdo para redes sociais: adaptar um modelo para gerar posts no seu estilo, com a sua linguagem e o seu humor.

## 5.3 Demonstração de Uso: Geração de Texto

Nesta seção, aplicamos os conceitos apresentados nas seções anteriores em uma demonstração prática. Vamos explorar o uso de um modelo de linguagem generativa prétreinado, desde a inicialização até a geração de texto, com exemplos precisos e objetivos. Essa demonstração pode ser realizada no Google® Colab, permitindo fácil acesso e execução para todos os participantes. Objetivo da demonstração:

- Mostrar como carregar e usar um modelo pré-treinado da biblioteca Hugging Face®;

- Gerar texto a partir de prompts personalizados;

- Explorar como diferentes configurações de parâmetros impactam os resultados;

- Ao final, você será capaz de utilizar modelos de linguagem para tarefas práticas como geração de texto criativo, resumos ou respostas para perguntas específicas.

### 5.3.1 Configuração Inicial no Google Colab®

Antes de começar, abra um notebook no Google® Colab e execute o seguinte comando (Código 6) para instalar a biblioteca Hugging Face Transformers®:

**Código 6 - Instalar a biblioteca Hugging Face Transformers®**

!pip install transformers

Fonte: autoria própria.

O exemplo pode ser acessado por meio do link do código no Google Colab.

Notebook Colab

Atenção: Os códigos apresentados neste ebook foram diagramados em formato PDF apenas para fins didáticos. Por esse motivo, a formatação exibida no arquivo pode não ser idêntica ao código original desenvolvido no Google Colab. Recomendamos sempre consultar e executar os notebooks originais disponíveis no Google Colab, pois neles os códigos estão no formato adequado para reprodução e testes.

<!-- source_page: 14 -->

a) Demonstração de Uso de Ferramentas para Manipulação de Modelos de Linguagem

Nesta seção, aplicamos os conceitos apresentados nas seções anteriores em uma demonstração prática. Vamos explorar o uso de um modelo de linguagem generativa prétreinado, desde a inicialização até a geração de texto, com exemplos claros e objetivos. Objetivo da demonstração:

- Mostrar como carregar e usar um modelo pré-treinado da biblioteca Hugging Face.

- Gerar texto a partir de prompts personalizados.

- Explorar como diferentes configurações de parâmetros impactam os resultados.

- Ao final, você será capaz de utilizar modelos de linguagem para tarefas práticas como geração de texto criativo, resumos ou respostas para perguntas específicas.

b) Instalar a biblioteca Transformers (caso não esteja instalada)

!pip install transformers

c) Carregando o Modelo

```text
# Importa as classes para trabalhar com modelos e tokenizadores
from transformers import AutoModelForCausalLM, AutoTokenizer
```

```text
# Define o nome de um modelo treinado para português
model_name = "pierreguillou/gpt2-small-portuguese" # Modelo GPT-2 ajustado
para português
```

```text
# Carrega o modelo e o tokenizador correspondentes
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForCausalLM.from_pretrained(model_name)
```

d) Gerando Texto a Partir de um Prompt

# Define o prompt inicial que será usado como entrada para o modelo prompt = """ Escreva um parágrafo em português sobre como a inteligência artificial está transformando o setor de saúde. Foque em exemplos como diagnósticos precisos, análise de dados médicos e personalização de tratamentos. """

<!-- source_page: 15 -->

```text
# Tokeniza o prompt, convertendo-o em tensores que o modelo pode processar
# - return_tensors="pt": Retorna os tokens no formato de tensor PyTorch
inputs = tokenizer.encode(prompt, return_tensors="pt")
```

```text
# Gera texto com base no prompt tokenizado
outputs = model.generate(
inputs,
max_length=200, # Limita o comprimento da saída
num_return_sequences=1, # Gera uma sequência
temperature=0.9, # Controla a criatividade do texto
do_sample=True, # Ativa amostragem aleatória
pad_token_id=tokenizer.eos_token_id, # Define o token de preenchimento
top_p=0.95,
eos_token_id=None
)
```

```text
# Decodifica os tokens gerados de volta para texto legível
# - skip_special_tokens=True: Remove tokens especiais, como "<|endoftext|>"
generated_text = tokenizer.decode(outputs[0], skip_special_tokens=True)
```

```text
# Exibe o texto gerado
print(generated_text)
```

e) Prompt para gerar um resumo

```text
# Prompt para gerar um resumo
prompt = "Resuma o seguinte texto: A reciclagem é importante porque reduz
resíduos, economiza energia e protege o meio ambiente."
```

inputs = tokenizer.encode(prompt, return_tensors="pt")

```text
outputs = model.generate(inputs, max_length=60, num_return_sequences=1,
temperature=0.7)
print(tokenizer.decode(outputs[0], skip_special_tokens=True))
```

### 5.3.2 Carregando o Modelo

Vamos usar o GPT-2® ajustado para português, um modelo amplamente utilizado para geração de texto. No Código 7, a seguir, é carregado o modelo e o tokenizer necessários.

**Código 7 - Carregar o modelo e o tokenizer**

```text
# Importa as classes GPT2LMHeadModel e GPT2Tokenizer da biblioteca transformers
from transformers import AutoModelForCausalLM, AutoTokenizer
# Define o nome de um modelo treinado para português
model_name = "pierreguillou/gpt2-small-portuguese"
continua
```

<!-- source_page: 16 -->

```text
# Carrega o tokenizador pré-treinado correspondente ao modelo GPT-2
# - O tokenizador é responsável por transformar texto em tokens que o modelo
pode processar
tokenizer = AutoTokenizer.from_pretrained(model_name)
# Carrega o modelo pré-treinado GPT-2 para tarefas de geração de texto
# - AutoModelForCausalLM é a arquitetura específica usada para geração de texto
model = AutoModelForCausalLM.from_pretrained(model_name)
```

Fonte: autoria própria.

### 5.3.3 Gerando Texto a Partir de um Prompt

Agora que o modelo está carregado, você pode gerar texto fornecendo um prompt inicial. Vamos usar um exemplo simples no Código 8.

**Código 8 - Exemplo de geração de texto usando o modelo GPT2®**

```text
# Define o prompt inicial que será usado como entrada para o modelo GPT-2
prompt = "Escreva um parágrafo em português sobre como a
inteligência artificial está transformando o setor de saúde.
Foque em exemplos como diagnósticos precisos, análise de
dados médicos e personalização de tratamentos.
"
# Tokeniza o prompt, convertendo-o em tensores que o modelo pode processar
# - return_tensors="pt": Retorna os tokens no formato de tensor PyTorch
inputs = tokenizer.encode(prompt, return_tensors="pt")
# Gera texto com base no prompt tokenizado
# - max_length: Define o número máximo de tokens
no texto gerado (50 neste caso)
# - num_return_sequences: Define o número de sequências
de texto a serem geradas (1 neste caso)
# - temperature: Controla a criatividade do modelo; valores mais altos produzem
textos mais variados
outputs = model.generate(
inputs,
max_length=200,
num_return_sequences=1,
temperature=0.9,
do_sample=True,
pad_token_id=tokenizer.eos_token_id,
top_p=0.95,
eos_token_id=None
)
continua
```

<!-- source_page: 17 -->

```text
# Decodifica os tokens gerados de volta para texto legível
# - skip_special_tokens=True: Remove tokens especiais, como "<|endoftext|>"
generated_text = tokenizer.decode(outputs[0], skip_special_tokens=True)
```

```text
# Exibe o texto gerado pelo modelo
print(generated_text)
```

Fonte: autoria própria.

### 5.3.4 Ajustando Parâmetros

Parâmetros como max_length, temperature16 e num_return_sequences podem ser ajustados para modificar o comportamento do modelo:

- max_length: define o comprimento máximo do texto gerado;

- temperature: controla o nível de criatividade do texto (valores mais baixos geram respostas mais conservadoras, enquanto valores mais altos produzem respostas mais criativas);

- num_return_sequences: define o número de textos gerados.

Experimente: altere o valor de temperature para 0.3 e observe como o texto gerado se torna mais previsível (Código 9).

**Código 9 - Exemplo de uso: alterando temperature para controlar o nível de criatividade do texto**

```text
outputs = model.generate(inputs, max_length=50, num_return_sequences=1,
temperature=0.3)
print(tokenizer.decode(outputs[0], skip_special_tokens=True))
```

Fonte: autoria própria.

### 5.3.5 Exemplo Aplicado: Resumo de Texto

Vamos gerar um resumo simples de um texto fornecido. Embora o GPT-2® não seja otimizado para resumos, ele pode ser ajustado com prompts específicos (Código 10).

16 Parâmetro que ajusta a criatividade do modelo na geração de texto; valores mais baixos produzem respostas mais conservadoras.

<!-- source_page: 18 -->

**Código 10 - Exemplo de uso: prompt para gerar um resumo**

```text
# Prompt para gerar um resumo
prompt = "Resuma o seguinte texto: A inteligência artificial está transformando
o setor de saúde."
inputs = tokenizer.encode(prompt, return_tensors="pt")
outputs = model.generate(inputs, max_length=60, num_return_sequences=1,
temperature=0.7)
print(tokenizer.decode(outputs[0], skip_special_tokens=True))
```

Fonte: autoria própria. 5.3.6 Conclusão

Esta demonstração mostrou como usar um modelo de linguagem pré-treinado para gerar texto e adaptar os resultados com base em diferentes configurações. A prática com ferramentas como Hugging Face® e a experimentação com parâmetros são fundamentais para compreender o funcionamento dos modelos e aproveitar seu potencial em aplicações reais. Com esses aprendizados, você está pronto para aplicar modelos de linguagem em projetos reais, ajustando-os às necessidades específicas de cada contexto. Na próxima Unidade, exploraremos aplicações práticas, como geração de relatórios, de planilhas financeiras e de propostas de negócios.

CURIOSIDADES: Sabia que o ajuste fino de modelos de linguagem pode ser realizado com poucos dados, utilizando técnicas como "few-shot learning"?

A personalização de modelos de linguagem permite criar assistentes virtuais com personalidades distintas, que se adaptam às preferências de cada usuário.

Algumas empresas oferecem serviços de ajuste fino e personalização de modelos de linguagem, permitindo que você crie soluções sob medida para seu negócio.

<!-- source_page: 19 -->

Saiba mais…

- Artigos:

OUYANG et al. (2019). Fine-tuning language models from human preferences.

Um artigo que explora técnicas de ajuste fino de modelos de linguagem com base nas preferências humanas.

GALLEA, Q. (2023). Building a custom GPT: lessons and tips.

O artigo destaca os desafios e soluções no desenvolvimento de um GPT® personalizado, mostrando como pre-prompting e bases de conhecimento especializadas podem criar aplicações eficazes e confiáveis para tarefas específicas.

- Links:

Guia: Hugging Face documentation.

A documentação oficial da Hugging Face® é uma fonte abrangente para aprender sobre o uso de modelos pré-treinados, ajuste fino e personalização.

Tutorial: OpenAI® API Quickstart.

Um guia prático para começar a usar a API da OpenAI®, incluindo exemplos de como personalizar prompts e ajustar parâmetros.

Para relembrar…

- Manipulação de modelos de linguagem: técnicas e ferramentas para ajustar, personalizar e otimizar modelos de linguagem.

- Ferramentas populares para manipulação: aprendemos sobre plataformas como Hugging Face®, OpenAI® API, Google Cloud Natural Language API® e IBM Watson NLU®. Essas ferramentas facilitam o uso de modelos de linguagem em diversas aplicações.

- Ajuste fino (fine-tuning): adaptar um modelo pré-treinado a um domínio específico, treinando-o com dados específicos.

- Personalização: criar modelos únicos que refletem o estilo de escrita, o tom de voz e as preferências do usuário.

- Demonstração de uso: utilizar as ferramentas e técnicas aprendidas para manipular modelos de linguagem em aplicações práticas.

- Manipulação de modelos de linguagem: um leque de possibilidades para a criação de soluções de IA Generativa de Texto inovadoras e personalizadas. Ao dominar as ferramentas e técnicas, você poderá extrair o máximo potencial desses modelos.
