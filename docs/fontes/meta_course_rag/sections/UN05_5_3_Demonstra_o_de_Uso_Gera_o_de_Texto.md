---
unit: 5
unit_title: "Ferramentas para Manipulação de Modelos de Linguagem"
section: "5.3"
section_title: "Demonstração de Uso: Geração de Texto"
source_file: "Un5_curso_meta(1).pdf"
source_markdown: "units/UN05_Ferramentas_para_Manipula_o_de_Modelos_de_Linguagem.md"
source_pages: [13, 14, 15, 16, 17, 18, 19]
language: "pt-BR"
---

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
