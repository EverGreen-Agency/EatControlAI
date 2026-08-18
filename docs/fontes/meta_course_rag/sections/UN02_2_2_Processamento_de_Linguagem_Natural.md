---
unit: 2
unit_title: "Principais abordagens e aplicações"
section: "2.2"
section_title: "Processamento de Linguagem Natural"
source_file: "Un2_curso_meta.pdf"
source_markdown: "units/UN02_Principais_abordagens_e_aplica_es.md"
source_pages: [29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49]
language: "pt-BR"
---

## 2.2 Processamento de Linguagem Natural

Durante toda a história da humanidade, a linguagem foi a principal ferramenta para nos comunicarmos, transmitirmos ideias e organizarmos sociedades. Por meio da fala e da escrita, desenvolvemos culturas, ciência, literatura e sistemas políticos. A linguagem humana, no entanto, é extremamente complexa: envolve regras gramaticais, contextos sociais, ambiguidade, ironia, intenção e emoção. Quando a inteligência artificial se propõe a compreender a linguagem humana, ela está tentando decifrar um dos sistemas mais sofisticados já criados – e é exatamente essa a missão do campo chamado Processamento de Linguagem Natural (NLP - Natural Language Processing). O NLP busca construir sistemas capazes de compreender e gerar linguagem humana com sentido. Isso não significa apenas traduzir palavras para comandos, mas sim interpretar significados, considerar contextos e reagir de forma coerente. O grande desafio é que a linguagem não é uma sequência fixa de códigos: é viva, adaptável, diversa e até contraditória. Ao tentar automatizar o entendimento de linguagem, o NLP enfrenta problemas como ambiguidade semântica (uma mesma palavra pode

ter vários significados), variações linguísticas (diferentes formas de dizer a mesma coisa) e o papel do contexto (uma frase pode mudar totalmente de sentido dependendo da situação). Portanto, o NLP não é apenas uma aplicação da IA em interfaces tecnológicas: é uma tentativa de ensinar as máquinas a lidarem com a complexidade da comunicação humana, abrindo portas para avanços em educação, ciência, saúde, acessibilidade, entre muitas outras áreas.

Notebook Colab

a) Objetivos de Aprendizagem

Apresentar, na prática, conceitos básicos de Processamento de Linguagem Natural (PLN), incluindo classificação de texto com Bag of Words e construção de um chatbot com modelo de linguagem.

b) 📘 Introdução ao Processamento de Linguagem Natural

O Processamento de Linguagem Natural é uma área da Inteligência Artificial que capacita máquinas a entender, interpretar, gerar e interagir com a linguagem humana. Este notebook é uma introdução prática ao PLN (ou NLP, da sigla em inglês) com Python, dividida em duas partes:

1. Classificação de Texto com Bag of Words: Representamos textos numericamente e treinamos um classificador simples usando Naive Bayes.

2. Uso de Modelos de Linguagem para Construção de Chatbots: mostramos como utilizar um modelo de linguagem para gerar respostas às perguntas do usuário.

Vamos começar!

c) 🧠 Parte 1: Representação e Classificação de Texto

Nesta primeira parte, vamos entender como o computador “lê” texto e como podemos ensinar um modelo a dizer se uma frase é educacional ou não.

d) ⚙️ Instalando as bibliotecas necessárias

Antes de usar qualquer ferramenta em Python, precisamos instalar os pacotes que não fazem parte da instalação padrão. Isso é comum em notebooks como o Google Colab. 📌 Objetivo: Instalar o NLTK (para processamento de linguagem) e scikit-learn (para classificação).

```text
# Esta célula instala pacotes externos que serão usados no notebook.
# Ela é necessária apenas uma vez no início.
# "nltk" é uma biblioteca para processamento de texto.
# "scikit-learn" é usada para machine learning (classificação de texto).
```

!pip install nltk scikit-learn --quiet

f) 📚 Importando bibliotecas e baixando recursos

Agora que instalamos as bibliotecas, vamos carregá-las no notebook e preparar o que for necessário. 📌 Objetivo: importar ferramentas de NLP e machine learning e baixar os recursos de tokenização do NLTK.

```text
# Importação das bibliotecas principais
import pandas as pd # Manipulação de dados em formato de tabela (DataFrames)
from nltk.tokenize import word_tokenize, sent_tokenize # Funções de
tokenização
from sklearn.feature_extraction.text import CountVectorizer # Vetorização
de texto (Bag-of-Words)
from sklearn.naive_bayes import MultinomialNB # Classificador Naive Bayes
from sklearn.metrics import accuracy_score # Métrica de acurácia para
avaliação
```

```text
import nltk
# Download the necessary punkt_tab resource
nltk.download('punkt_tab")
nltk.download('punkt") #
```

[nltk_data] Downloading package punkt_tab to /root/nltk_data... [nltk_data] Package punkt_tab is already up-to-date! [nltk_data] Downloading package punkt to /root/nltk_data... [nltk_data] Package punkt is already up-to-date! True

g) ✂️ Tokenização: Frases e Palavras

Antes de podermos ensinar o computador a entender um texto, precisamos quebrá-lo em partes menores que ele possa processar. Esse processo se chama tokenização. 🔹 O que são tokens? Tokens são os fragmentos básicos do texto, como frases, palavras ou partes de palavras. Por exemplo:

- Um texto pode ser dividido em frases, usando pontuação como ponto final, interrogação ou exclamação.

- Cada frase, por sua vez, pode ser dividida em palavras. 🔹 Por que isso é importante? O computador não entende o texto como um todo. Ele precisa que a informação seja dividida para que possa:

- Contar palavras,

- Medir frequência de termos,

- Construir vetores numéricos,

- Alimentar modelos de aprendizado de máquina. 👉 Em resumo: tokenizar é o primeiro passo essencial para qualquer tarefa de Processamento de Linguagem Natural.

texto = "Hoje é um ótimo dia para aprender NLP! Vamos começar agora mesmo?"

```text
# Separar em frases
frases = sent_tokenize(texto)
print("Frases:", frases)
```

```text
# Separar em palavras
for i, frase in enumerate(frases):
print(f"Tokens da frase {i+1}:", word_tokenize(frase))
```

Frases: ['Hoje é um ótimo dia para aprender NLP!", 'Vamos começar agora mesmo?"] Tokens da frase 1: ['Hoje", 'é", 'um", 'ótimo", 'dia", 'para", 'aprender", 'NLP", '!"] Tokens da frase 2: ['Vamos", 'começar", 'agora", 'mesmo", '?"]

h) 🧮 Vetorização com Bag of Words

Modelos de machine learning não conseguem interpretar texto diretamente — eles trabalham apenas com números. Por isso, antes de usarmos um modelo, precisamos transformar o texto em uma representação numérica. 🔹 O que é a técnica Bag of Words? Uma forma simples e eficaz de fazer isso é usando o método Bag of Words (BoW). Essa técnica:

- Cria um vocabulário com todas as palavras que aparecem no nosso conjunto de textos;

- Conta quantas vezes cada palavra aparece em cada frase;

- Ignora a ordem das palavras, focando apenas na frequência. Por exemplo, se tivermos as frases:

- "Eu gosto de NLP"

- "NLP é incrível" O BoW criaria um vetor para cada frase, com base na contagem das palavras "eu", "gosto", "de", "nlp", "é", "incrível". 🔹 Para que serve isso? Com esses vetores numéricos, conseguimos:

- Alimentar modelos de classificação ou agrupamento,

- Medir similaridade entre frases,

- Criar representações úteis para visualização e análise. 👉 Agora que entendemos o conceito, vamos aplicar o Bag of Words na prática!

```text
# Nosso conjunto de frases com temas educacionais e não educacionais
corpus = [
```

"Estou estudando processamento de linguagem natural.", "Machine learning é uma subárea da IA.", "Hoje assisti uma palestra sobre inteligência artificial.", "O time ganhou a final do campeonato.", "Assisti um filme muito bom ontem.", "Vamos ao show da banda neste fim de semana." ]

# Rótulos correspondentes: o que cada frase representa continua continua

rotulos = [ "educacional", "educacional", "educacional", "não educacional", "não educacional", "não educacional" ]

```text
# Criamos um vetor que transforma texto em números com base na contagem de
palavras
vectorizer = CountVectorizer()
```

```text
# Transformamos o corpus em uma matriz de contagem
X = vectorizer.fit_transform(corpus)
```

```text
# Visualizamos o vocabulário aprendido (as palavras únicas)
print("Vocabulário:", vectorizer.get_feature_names_out())
```

```text
# Mostramos os dados em formato de tabela
df_freq = pd.DataFrame(X.toarray(), columns=vectorizer.get_feature_names_out())
df_freq.index = [f"Frase {i+1}" for i in range(len(corpus))]
df_freq
```

Vocabulário: ['ao" 'artificial" 'assisti" 'banda" 'bom" 'campeonato" 'da" 'de" 'do" 'estou" 'estudando" 'filme" 'fim" 'final" 'ganhou" 'hoje" 'ia" 'inteligência" 'learning" 'linguagem" 'machine" 'muito" 'natural" 'neste" 'ontem" 'palestra" 'processamento" 'semana" 'show" 'sobre" 'subárea" 'time" 'um" 'uma" 'vamos"]

i) 🤖 Classificação com Naive Bayes

Agora que conseguimos representar o texto como vetores numéricos (com o Bag of Words), estamos prontos para treinar um modelo de classificação. 🔹 O que vamos fazer? Vamos ensinar o computador a prever se uma frase é do tipo:

- Educacional (relacionada a estudo, ciência, tecnologia etc.), ou

- Não educacional (relacionada a lazer, cotidiano, eventos etc.)

Para isso, vamos usar um modelo chamado Naive Bayes, que é bastante comum e eficaz para tarefas simples de classificação de texto. 🔹 Mas atenção! ⚠️ Este é apenas um exemplo didático, com poucas frases de treino. Na prática, um modelo de machine learning precisa de centenas ou milhares de exemplos para aprender bem e generalizar para novos dados. Nosso objetivo aqui é apenas demonstrar o funcionamento básico de uma classificação automática de texto.

📌 Objetivo desta etapa

- Treinar um modelo Naive Bayes com um conjunto simples de frases e rótulos;

- Usar esse modelo para prever a categoria de frases novas.

```text
# Criamos o modelo de classificação com Naive Bayes
modelo = MultinomialNB()
```

# Treinamos o modelo com as frases e seus rótulos modelo.fit(X, rotulos)

j) 🔍Testando o Modelo com Novas Frases

Vamos agora verificar se o modelo consegue prever corretamente o tipo de frases que ele nunca viu antes.

```text
# Novas frases para testar
novas_frases = [
```

"Quero aprender mais sobre redes neurais.", "O show ontem foi espetacular!", "Estou estudando inteligência artificial.", "Meu time venceu o campeonato!" ]

```text
# Convertendo para o mesmo formato numérico usado no treinamento
X_test = vectorizer.transform(novas_frases)
```

# Fazemos as previsões continua continua

predicoes = modelo.predict(X_test)

```text
# Exibimos os resultados de cada frase
for frase, categoria in zip(novas_frases, predicoes):
print(f""{frase}" => Categoria prevista: {categoria}")
```

'Quero aprender mais sobre redes neurais." => Categoria prevista: educacional 'O show ontem foi espetacular!" => Categoria prevista: não educacional 'Estou estudando inteligência artificial." => Categoria prevista: educacional 'Meu time venceu o campeonato!" => Categoria prevista: não educacional

k) 📈 Avaliando o Modelo

Depois de treinar e testar um modelo, é importante saber se ele está acertando ou errando nas previsões. 🔹 Como avaliamos? Se tivermos os rótulos verdadeiros das frases que usamos no teste (ou seja, sabemos qual deveria ser a resposta correta), podemos comparar com as previsões do modelo. Uma forma simples de avaliação é a acurácia, que mede quantas vezes o modelo acertou, em relação ao total de exemplos. Por exemplo, se testamos 4 frases e o modelo acertou 3 delas, a acurácia será de 75%. 📌 Objetivo desta etapa Verificar a qualidade do modelo comparando suas previsões com as respostas corretas.

```text
# Rótulos reais (verdadeiros) das frases de teste
y_true = ["educacional", "não educacional", "educacional", "não educacional"]
```

```text
# Comparamos com as previsões
acc = accuracy_score(y_true, predicoes)
```

```text
# Mostramos a acurácia (porcentagem de acertos)
print(f"Acurácia do modelo no teste: {acc:.2f}")
```

Acurácia do modelo no teste: 1.00

A mensagem Acurácia do modelo no teste: 1.00 indica que o modelo acertou todas as previsões no conjunto de teste. Embora isso pareça excelente, é importante lembrar que usamos poucos exemplos e frases simples. Isso significa que o resultado pode não se repetir com dados reais ou mais variados. ✅Mesmo assim, conseguimos observar na prática como é possível:

- Representar texto como números,

- Treinar um classificador básico,

- E prever automaticamente a categoria de frases novas. Com isso, encerramos a parte de classificação de texto com Bag of Words e Naive Bayes.

l) Parte 2: 🤖 Utilizando Modelo de Linguagem para Construção de um Chatbot Simples

m) 📦 Instalação das bibliotecas necessárias

Instalamos a biblioteca huggingface-hub para acessar modelos hospedados na Hugging Face.

!pip install huggingface-hub --quiet

Instalamos a biblioteca gradio para termos uma interface de chat.

!pip install gradio

n) 🔑 Configuração do token de acesso

Para usar os modelos da Hugging Face — uma plataforma que oferece acesso a modelos de IA pré-treinados — é necessário criar uma conta gratuita e gerar um token de acesso com permissão para usar a API de inferência.

Passos para gerar o token:

1. Acesse huggingface.co e crie uma conta gratuita, se ainda não tiver.

2. Após logar, vá até a página de tokens: https://huggingface.co/settings/tokens

3. Clique em 'New token" (Novo token)

4. Dê um nome para o token, como token-chatbot

5. Selecione a permissão Inference: Make calls to Inference Providers (necessária para gerar respostas com os modelos)

6. Clique em 'Create token" e copie o código gerado

7. Crie uma nova chave secreta na barra lateral à esquerda (colab) com nome "token" e o valor do token da HuggingFace

⚠️ Importante: guarde seu token com segurança e nunca o compartilhe publicamente.

from huggingface_hub import InferenceClient

o) 🔑 Configuração do token de acesso

Aqui você coloca o seu token de acesso (com permissão Access Inference API) para se conectar com os modelos da Hugging Face.

```text
from google.colab import userdata
token = userdata.get('token")
```

client = InferenceClient(token=token)

🤖 Modelo utilizado no chatbot Para garantir uma experiência de uso fluida, com boa qualidade de resposta e compatibilidade total com a API pública gratuita da Hugging Face, utilizaremos o modelo: HuggingFaceH4/zephyr-7b-beta: modelo de 7 bilhões de parâmetros, otimizado para seguir instruções de forma clara, consistente e adaptada a múltiplos idiomas, incluindo o português. Ele apresenta excelente equilíbrio entre velocidade, custo e

qualidade de geração de respostas, sendo ideal para aplicações educacionais e interativas como este chatbot. Escolhemos o zephyr-7b-beta por ser atualmente uma ótima opção gratuita disponível, combinando alta qualidade nas respostas com acesso estável pela API da Hugging Face, sem restrições de tamanho.

p) 🧠 Definindo o comportamento do chatbot

O system prompt é uma instrução especial enviada ao modelo antes de qualquer pergunta do usuário. Ele serve para definir o comportamento, o estilo de linguagem e o papel que o assistente deve desempenhar ao longo da conversa. No nosso caso, estamos configurando o modelo para agir como um professor de biologia simpático, que responde de forma didática, clara e acessível, sempre em português. Isso ajuda o modelo a manter coerência no tom, usar exemplos simples e ajustar o vocabulário para alunos do ensino médio. 📌 O system prompt funciona como uma "personalidade base" para o chatbot — é como se estivéssemos dizendo: "Finja que você é esse personagem e se comunique da forma mais apropriada para esse papel."

system_prompt = ( "Você é um assistente de biologia simpático. Sempre que o usuário fizer uma pergunta relacionada à biologia, responda de forma clara, objetiva e em português. Se a pergunta não estiver relacionada a biologia, responda normalmente de forma simpática." )

q) 💬 Função do chatbot interativo

Esta função inicia um bate-papo com o modelo escolhido. Parâmetros da geração de texto:

- prompt: o texto enviado ao modelo, incluindo o system prompt e a pergunta;

- model: o nome do modelo que será usado (ver opções abaixo);

- max_new_tokens=300: número máximo de tokens na resposta. Respostas mais longas usam valores maiores;

- temperature=0.3: controla a criatividade. Valores baixos = respostas mais objetivas; altos = mais criativas.

```text
# Histórico de conversas
historico = []
```

```text
# Função para formatar o histórico para exibição
def formatar_historico(hist):
texto = ""
for m in hist:
prefixo = "🧑 Usuário:" if m["role"] == "user" else "🤖 Assistente:"
texto += f"{prefixo} {m['content"]}\n\n"
return texto.strip()
```

# Função principal da IA def responder_pergunta(mensagem, reset=True): global historico

if reset: historico = []

```text
# Prompt formatado apenas com a pergunta atual, sem histórico
prompt_formatado = (
```

f"SYSTEM: {system_prompt}\n" f"USER: {mensagem}\nASSISTANT:" )

resposta_raw = client.text_generation(

```text
prompt=prompt_formatado,
model="HuggingFaceH4/zephyr-7b-beta",
max_new_tokens=300,
temperature=0.3
)
```

```text
# Processa o retorno
if isinstance(resposta_raw, str):
resposta = resposta_raw
elif isinstance(resposta_raw, list):
resposta = resposta_raw[0]["generated_text"]
elif isinstance(resposta_raw, dict):
resposta = resposta_raw.get("generated_text", "")
else:
resposta = str(resposta_raw)
```

resposta_limpa = resposta.split("ASSISTANT:")[-1].strip()

historico.append({"role": "user", "content": mensagem}) historico.append({"role": "assistant", "content": resposta_limpa})

return resposta_limpa, formatar_historico(historico)

r) 🧪 Testes diretos com o modelo no notebook

Antes de criarmos uma interface visual, é importante testar se o modelo está funcionando corretamente por meio de entradas e saídas diretas no terminal do notebook. Nesta etapa, vamos digitar perguntas manualmente no terminal do Colab e ver as respostas geradas pelo modelo. Esse teste ajuda a:

- Validar se a conexão com o modelo está funcionando.

- Observar a qualidade das respostas.

- Avaliar o comportamento do system prompt em ação. 💡 Para encerrar o teste, digite sair ou exit no prompt.

# Teste interativo no Colab — digite perguntas diretamente

print("🔬 Chat de Biologia — digite 'sair" para encerrar\n")

while True: pergunta = input("Você: ") if pergunta.strip().lower() in ["sair", "exit", "quit"]:

```text
print("Encerrado.")
break
resposta, _ = responder_pergunta(pergunta)
print(f"Assistente: {resposta}\n")
```

🔬 Chat de Biologia — digite 'sair" para encerrar

Você: O que é DNA? Assistente: DNA (sigla de Deoxyribonucleic acid) é o material genético que carrega as instruções para a construção e funcionamento de todos os seres vivos. É composto por moléculas longas e estreitas, formadas por quatro tipos de bases (adenina, timina, guanina e citosina), que se combinam em pares específicos (adenina com timina e guanina com citosina) para formar as famosas "escalas de gel" características do DNA. O DNA se enrola em estruturas complexas, como o cromossomo, que são responsáveis pela transmissão hereditária de características de um organismo para outro.

Você: sair Encerrado.

s) 💬 Criando uma interface amigável com Gradio

Após testar o modelo manualmente, vamos criar uma interface interativa com Gradio, onde:

- O usuário digita sua pergunta em uma caixa de texto

- O chatbot responde de forma clara e visual

- Todo o histórico da conversa é mostrado abaixo Isso permite que qualquer pessoa interaja com o modelo de forma simples, como em um chat educacional. 🌿 O tema da interface é voltado para a Biologia, com um design amigável e educativo!

```text
# Interface Gradio com visual temático de biologia
import gradio as gr
```

with gr.Blocks(theme=gr.themes.Base()) as demo: gr.Markdown(

""" <h1 style="text-align: center; color: #2E8B57;">💬 Chat de Biologia</h1> <p style="text-align: center; font-size: 16px;">Tire suas dúvidas sobre biologia com um assistente inteligente 🌿</p>

""" )

with gr.Row(): entrada = gr.Textbox(label="Pergunta", placeholder="Ex: O que é fotossíntese?", lines=3, scale=1)

botao = gr.Button("Enviar", scale=0)

gr.Markdown("---") saida = gr.Textbox(label="Resposta do Assistente", lines=5, interactive=False)

with gr.Accordion("📚 Ver histórico completo da conversa", open=False): historico_area = gr.Textbox(label="", lines=10, interactive=False)

def responder_gradio(texto): resposta, hist = responder_pergunta(texto) return resposta, hist

botao.click(fn=responder_gradio, inputs=entrada, outputs=[saida, historico_ area])

demo.launch() continua continua

It looks like you are running Gradio on a hosted a Jupyter notebook. For the Gradio app to work, sharing must be enabled. Automatically setting `share=True` (you can turn this off by setting `share=False` in `launch()` explicitly).

Colab notebook detected. To show errors in colab notebook, set debug=True in launch() * Running on public URL: https://841f071c3887812580.gradio.live

This share link expires in 1 week. For free permanent hosting and GPU upgrades, run `gradio deploy` from the terminal in the working directory to deploy to Hugging Face Spaces (https://huggingface.co/spaces)

✅Conclusão – Modelos de Linguagem e Chatbots Nesta etapa, utilizamos um modelo de linguagem para construir um chatbot simples, capaz de gerar respostas a perguntas de forma automatizada. Exploramos os seguintes conceitos:

- Como configurar o acesso à API da Hugging Face;

- Como definir um system prompt para orientar o comportamento do assistente;

- Como testar o modelo diretamente no notebook;

- E como criar uma interface amigável com Gradio para interação. Essa abordagem mostra como é possível integrar modelos de linguagem em aplicações práticas, mesmo em cenários educacionais e experimentais. Com isso, finalizamos a parte de uso de modelos de linguagem para construção de um chatbot simples.

t) Referências Bibliográficas

BIRD, Steven; KLEIN, Ewan; LOPER, Edward. Natural Language Processing with Python: Analyzing Text with the Natural Language Toolkit. Sebastopol: O"Reilly Media, 2009. LECUN, Yann; BENGIO, Yoshua; HINTON, Geoffrey. Deep learning. Nature, v. 521, n. 7553, p. 436–444, 2015. DOI: https://doi.org/10.1038/nature14539. VASWANI, Ashish et al. Attention is all you need. In: ADVANCES in Neural Information Processing Systems 30 (NeurIPS 2017). p. 5998–6008, 2017. RUSSELL, Stuart J; NORVIG, Peter. Inteligência artificial: uma abordagem moderna. 4. ed. Rio de Janeiro: GEN LTC, 2022. 1080 p.

### 2.2.1 Conceitos e fundamentos

Ao longo do desenvolvimento do NLP, diferentes estratégias foram criadas para tornar possível que computadores lidem com a linguagem humana de forma eficaz. Três formas principais se destacam nesse processo. A primeira foi baseada em regras gramaticais fixas. Nela, os sistemas eram programados com instruções sobre como a língua funciona, como identificar sujeitos, verbos ou objetos. Esse método funcionava bem com frases simples, mas falhava quando apareciam gírias, metáforas ou variações de linguagem. É como seguir um manual de receitas: funciona quando tudo está dentro do esperado, mas não quando os ingredientes mudam. Com a internet e os textos digitais, surgiu uma segunda forma: o uso de estatísticas. Os computadores passaram a aprender com grandes quantidades de texto, observando quais palavras aparecem juntas com mais frequência. Por exemplo, perceberam que "bom" aparece muito com "dia" ou "filme". Esse tipo de aprendizado permite que o sistema reconheça padrões, mesmo sem entender o significado das palavras, como alguém que aprende uma nova língua apenas ouvindo muito outras pessoas. A abordagem mais recente, e mais avançada, usa redes neurais artificiais, inspiradas no funcionamento do cérebro humano. Nela, as palavras são transformadas em números e organizadas em um espaço matemático, onde palavras parecidas ficam próximas. Por exemplo, "rei" e "rainha" aparecem em contextos semelhantes e, por isso, ficam próximas nesse espaço. Assim, o sistema consegue entender relações de sentido, como "feliz" estar mais perto de "alegre" do que de "triste". Essa forma de aprendizagem permite que a máquina compreenda melhor o contexto e até crie textos novos com base no que aprendeu. Para que isso funcione, o NLP segue algumas etapas. Primeiro, a frase é dividida em partes menores, como palavras – essa etapa se chama tokenização. Depois, cada palavra é analisada: que tipo de palavra é (verbo, substantivo, advérbio, etc.) e qual sua função na frase. Em seguida, o sistema tenta entender o significado das palavras e da frase como um todo. Finalmente, ele interpreta o contexto e a intenção por trás do que foi dito. Por exemplo, na frase "está chovendo canivete", o sistema precisa entender que isso é uma metáfora para uma chuva muito forte, e não algo literal. Além disso, os computadores precisam transformar as palavras em números, já que é assim que eles operam. Cada palavra vira um vetor, uma sequência de valores que indica como ela se relaciona com outras. Palavras usadas em contextos parecidos terão vetores parecidos. Isso permite que o sistema reconheça, por exemplo, que "amor" e "carinho" têm sentidos próximos, mesmo que sejam palavras diferentes.

Esses fundamentos mostram como o NLP tenta ensinar as máquinas a compreenderem a linguagem de forma cada vez mais próxima da nossa. Ainda que os computadores não "entendam" como nós, eles conseguem identificar padrões, interpretar frases e produzir textos úteis e coerentes.

#### 2.2.1.1 Como os computadores aprendem a escrever como humanos?

Nos últimos anos, o NLP deu um salto enorme com o surgimento dos Modelos de Linguagem de Grande Escala, conhecidos como LLMs (Large Language Models). Esses modelos são sistemas de inteligência artificial treinados para compreender e gerar linguagem humana com um nível impressionante de naturalidade e coerência. Um dos marcos desse avanço foi a introdução da arquitetura Transformer, proposta por Vaswani et al. (2017). Baseada em um mecanismo de atenção, ela permite que o modelo identifique quais partes do texto são mais relevantes para entender o significado geral. Embora tenha sido criada para tradução automática, a arquitetura se tornou a base dos principais modelos de linguagem atuais e é considerada um dos pilares da inteligência artificial moderna. Mas afinal, o que é um modelo de linguagem? De forma simples, é um tipo de sistema que aprende a prever qual será a próxima palavra de uma frase com base nas palavras anteriores. Essa tarefa, aparentemente simples, exige que o modelo compreenda estruturas linguísticas, relações de significado, contexto e até intenções do falante. Por exemplo, ao ler a frase "Hoje eu vou à...", o modelo pode prever que a próxima palavra será "escola", "praia" ou "igreja", pois são termos que costumam completar esse tipo de construção de maneira coerente. Por outro lado, dificilmente ele sugeriria uma palavra como "geladeira", que quebraria o sentido da frase. Durante o processo de treinamento, o modelo passa por milhões de exemplos desse tipo, ajustando seus parâmetros internos – que podem ser imaginados como pequenos botões que controlam suas decisões – até conseguir gerar textos que façam sentido em diferentes contextos. Quanto maior o modelo, maior sua capacidade de lidar com situações linguísticas complexas, como metáforas, ambiguidade ou perguntas abertas. Esses modelos se tornam ainda mais impressionantes quando ganham uma capacidade chamada "generativa". Um modelo generativo não apenas entende padrões, mas também é capaz de criar novos conteúdos com base nesses padrões. Isso significa que, em vez de simplesmente classificar um texto ou identificar uma informação, ele pode escrever uma redação, responder a uma pergunta aberta, resu-

mir um artigo ou até inventar uma história. Em outras palavras, modelos generativos transformam a IA em uma ferramenta criativa, que consegue produzir novas respostas a partir do que aprendeu. Na Figura 14, tem-se uma representação simplificada do aprendizado de modelos de linguagem.

**Figura 14 - Representação simplificada do aprendizado de modelos de linguagem**

Fonte: Autoria própria.

O funcionamento de um modelo generativo baseado em linguagem pode ser comparado ao de uma pessoa que leu milhares de livros e artigos, e que usa esse conhecimento para produzir um texto novo cada vez que alguém lhe faz uma pergunta. Por exemplo, se o modelo recebe a instrução "Explique o que é um modelo de linguagem para estudantes do ensino médio", ele irá recuperar, entre os padrões que aprendeu, formas comuns de explicar esse conceito, vocabulário apropriado para o público e estruturas típicas de uma explicação. A resposta gerada será construída palavra por palavra, de forma inédita, considerando o conteúdo da pergunta e a forma como as pessoas costumam abordar esse tema. Assim, os LLMs representam uma das mais impressionantes conquistas recentes da inteligência artificial, trazendo o uso da linguagem para o centro das interações entre humanos e máquinas. Sua capacidade de compreender e produzir texto de forma fluente está transformando a maneira como aprendemos, trabalhamos e nos comunicamos com a tecnologia.

#### 2.2.1.2 Aplicações de Processamento de Linguagem Natural

As tecnologias baseadas em NLP estão presentes em diversas situações do cotidiano, muitas vezes sem que o usuário perceba. Um exemplo clássico são os assistentes virtuais. Quando alguém pergunta "Vai chover hoje?", o sistema identifica que se trata de uma solicitação relacionada à previsão do tempo, busca a informação adequada e responde de forma clara e compreensível. Outro exemplo são os teclados de smartphones, que sugerem palavras ou completam frases automaticamente. Isso acontece porque o modelo reconhece padrões comuns de escrita. Se alguém começa a digitar "eu te amo...", é provável que o sistema sugira palavras como "muito" ou "demais" com base em seu aprendizado prévio. Os chatbots utilizados em sites de compras ou serviços também utilizam NLP para compreender e responder a perguntas frequentes, como "Qual o valor do frete?" ou "Como faço para trocar um produto?". Além disso, plataformas de redes sociais utilizam NLP para traduzir publicações, filtrar conteúdos ofensivos ou identificar opiniões positivas e negativas em comentários. Outras aplicações incluem sistemas de filtragem de spam, que analisam o conteúdo de e-mails para separar mensagens indesejadas, e ferramentas de correção automática de textos, que ajudam o usuário a escrever de forma mais clara e correta. Tradutores automáticos também se tornaram mais eficazes, conseguindo adaptar frases completas ao contexto e até interpretar expressões idiomáticas corretamente. Um exemplo bastante conhecido do uso de NLP em conjunto com LLMs é o ChatGPT®. Desenvolvido pela empresa OpenAI®, o ChatGPT® é um modelo de linguagem capaz de conversar com usuários por meio de textos. Ele analisa a mensagem recebida, interpreta o que está sendo pedido e gera uma resposta nova, elaborada em tempo real. Por meio dessa ferramenta, é possível tirar dúvidas, pedir explicações, receber sugestões de escrita, resolver problemas de linguagem e até criar histórias ou textos acadêmicos. O ChatGPT® mostra como os avanços no NLP e nos modelos generativos estão sendo aplicados diretamente no cotidiano das pessoas, facilitando a comunicação com a tecnologia. Esses exemplos mostram como o NLP já faz parte da vida cotidiana e como ele contribui para tornar a interação com a tecnologia mais fluida, natural e inteligente.

Agora é sua vez!

Acesse o notebook para colocar em prática o conteúdo adquirido e sintetizar o tópico de Processamento de Linguagem Natural.

Saiba mais...

Para aprofundar os conhecimentos sobre NLP, é possível explorar recursos que apresentam tanto os fundamentos quanto aplicações práticas da área.

Blog do Hugging Face®: O Hugging Face® é uma das principais plataformas de desenvolvimento em NLP atualmente. Seu blog reúne textos atualizados sobre o funcionamento de modelos de linguagem, avanços na área e tutoriais com exemplos reais. É indicado para quem deseja entender como as tecnologias de NLP são desenvolvidas e aplicadas em diferentes contextos. Disponível em: https://huggingface.co/blog.

Documentação do NLTK (Natural Language Toolkit): O NLTK é uma biblioteca de Python voltada ao ensino e experimentação em NLP. Sua documentação oferece explicações de conceitos fundamentais, como tokenização e análise gramatical, além de exemplos práticos e exercícios. É indicada para iniciantes que desejam explorar como os computadores processam textos. A biblioteca é utilizada como base no livro Natural Language Processing with Python (Bird; Klein; Loper, 2009), escrito pelos próprios autores do NLTK. Disponível em: https://www.nltk.org.

ChatGPT®: O ChatGPT® é um exemplo direto da aplicação de modelos de linguagem em interfaces interativas. Por meio da interação com a ferramenta, é possível observar como o sistema interpreta perguntas, gera textos e se adapta a diferentes contextos comunicativos. A experiência permite compreender, na prática, como funcionam os modelos generativos. Disponível em: https://chat.openai.com.
