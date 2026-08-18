---
corpus: "CEIA e Meta - AI Glasses Brasil"
unit: 4
title: "Engenharia de Prompts para Geração de Texto"
source_file: "Un4_curso_meta(1).pdf"
source_pages: 15
source_sha256: "10f80faac86fff137fbec875b3ef8e7c7b9ded548ce372ab84d6f46e4cf989ad"
language: "pt-BR"
normalization: "text layer normalized; page provenance preserved; visual-only supplements transcribed from source pages when identified"
---

# Unidade 4: Engenharia de Prompts para Geração de Texto

> Fonte única: `Un4_curso_meta(1).pdf`. O conteúdo abaixo foi normalizado para busca/RAG, sem complementação por fontes externas.

<!-- source_page: 1 -->

# Unidade II - Engenharia de Prompts para Geração de Texto

<!-- source_page: 2 -->

# Unidade IV - Engenharia de Prompts para Geração de Texto

## 4.1 Técnicas de Engenharia de Prompts10

Na Unidade I, vimos o poder dos modelos de linguagem e suas diversas aplicações. Agora, vamos mergulhar no mundo da engenharia de prompts, a arte e a ciência de se comunicar de forma eficaz com a IA para obter os melhores resultados. A engenharia de prompts é uma habilidade essencial para quem trabalha com modelos de linguagem generativa. Um prompt é o texto ou instrução fornecida ao modelo para que ele possa realizar uma tarefa específica. A forma como um prompt é estruturado influencia diretamente a qualidade e relevância da resposta gerada pelo modelo. Nesta seção, você aprenderá técnicas fundamentais para criar prompts eficazes, aumentando a precisão e a utilidade das respostas obtidas.

### 4.1.1 O Que É Engenharia de Prompts?

Analogia: Imagine que você está conversando com um gênio da lâmpada. Para que ele realize seus desejos, você precisa expressá-los de forma objetiva e precisa, certo? A engenharia de prompts funciona de forma similar. É a técnica de elaborar instruções (prompts) precisas e detalhadas para guiar os modelos de linguagem na geração de textos que atendam às suas necessidades. Mas, o que é um prompt? Um prompt é a instrução ou entrada inicial fornecida a um modelo de linguagem, descrevendo o que você deseja que ele faça. Ele pode ser uma frase, uma pergunta ou até mesmo um conjunto de parâmetros que orientam o modelo a gerar uma resposta ou realizar uma tarefa específica. A qualidade e a clareza do prompt determinam diretamente a relevância e a precisão do resultado gerado pelo modelo. Na Figura 3, é apresentado um fluxograma detalhando o processo de engenharia de prompts, desde a definição do objetivo até o refinamento e ajuste dos prompts.

10 Técnica de elaboração de prompts precisos e detalhados para guiar modelos de linguagem a gerar respostas alinhadas às necessidades.

<!-- source_page: 3 -->

**Figura 3 - Fluxograma de engenharia de prompts**

Fonte: autoria própria.

### 4.1.2 Dominando as Técnicas

Existem diversas técnicas para criar prompts eficazes. Vamos explorar algumas das principais:

1. Clareza e especificidade:

- Seja preciso e conciso: Evite ambiguidades e linguagem vaga. Quanto mais preciso for o seu prompt, melhor o modelo entenderá o que você deseja. Na Figura 4, é apresentado um exemplo de prompt vago versus um prompt bem estruturado.

- Exemplo: em vez do seguinte prompt "Escreva sobre a História do Brasil", prefira "Gere um texto de 500 palavras sobre a História do Brasil, focando no período colonial e seus impactos na sociedade atual".

- Defina o formato: especifique o tipo de texto desejado (poema, artigo, roteiro etc.), o público-alvo e o tom de voz.

- Exemplo: "Escreva um conto infantil sobre um gatinho que se perde na floresta, com um tom divertido e linguagem simples."

<!-- source_page: 4 -->

**Figura 4 - Exemplo de prompt vago versus prompt bem estruturado**

Fonte: autoria própria. 2. Contexto e informação:

- Forneça contexto relevante: inclua informações importantes para guiar o modelo na direção desejada.

- Exemplo: "Imagine um mundo onde os animais falam. Escreva um diálogo entre um cachorro e um gato discorrendo sobre a importância da amizade."

- Use exemplos: mostre ao modelo o que você espera, fornecendo exemplos de textos similares ao que você deseja.

- Exemplo: "Escreva um poema curto sobre a natureza, no estilo de Carlos Drummond de Andrade: 'No meio do caminho tinha uma pedra...' ".

3. Técnicas avançadas:

- Prompt chaining: divida tarefas complexas em etapas menores, usando a saída de um prompt como entrada para o próximo (Figura 5).

<!-- source_page: 5 -->

**Figura 5 - Técnicas avançadas de engenharia de prompts**

Fonte: autoria própria.

- Exemplo: Primeiro, gere uma lista de tópicos. Depois, use cada tópico como prompt para gerar um parágrafo sobre o tema.

- Uma equipe de marketing precisa criar um relatório detalhado sobre tendências de mercado para um cliente no setor de tecnologia sustentável.

- Prompt 1 - Geração de tópicos: "Liste cinco tendências emergentes em tecnologia sustentável para os próximos cinco anos."

- Saída do modelo:

- Uso de energia solar em dispositivos portáteis.

- Ampliação da mobilidade elétrica.

- Aplicação de IA para eficiência energética.

- Desenvolvimento de baterias recicláveis.

- Expansão da agricultura vertical.

- Prompt 2 - Para cada tópico gerado, use como prompt para criar um parágrafo explicativo: "Explique como a ampliação da mobilidade elétrica impactará as cidades nos próximos anos."

- Saída do modelo:

- "A ampliação da mobilidade elétrica trará impactos positivos para as cidades, incluindo a redução da poluição sonora e atmosférica, além de incentivar o desenvolvimento de infraestrutura de carregamento

<!-- source_page: 6 -->

elétrico. Essa tendência também promoverá inovações no setor de transporte público, tornando as cidades mais sustentáveis e acessíveis."

- Prompt 3 - Combine os parágrafos gerados em um texto final: "Crie uma introdução e uma conclusão para o relatório com base nos parágrafos sobre as tendências emergentes."

- Saída do modelo:

- "Introdução: As tecnologias sustentáveis estão transformando diversos setores, com inovações que prometem redefinir o futuro. Esse relatório explora cinco tendências-chave que moldarão o mercado nos próximos anos."

- "Conclusão: A adoção de tecnologias sustentáveis não é apenas uma escolha estratégica, mas uma necessidade para enfrentar desafios globais. Empresas que investem nessas áreas têm o potencial de liderar o mercado e impulsionar mudanças significativas."

- Prompt engineering com templates - Utilize templates pré-definidos para estruturar seus prompts e garantir consistência na geração de texto: "Crie um template para gerar posts de mídia social, com espaços para preencher o tema, a data e a call to action."

- Definição do template:

- Crie um template base para estruturar os posts, garantindo consistência e foco nos objetivos de cada campanha.

- Template:

- Tema: [defina o assunto principal do post]

- Data: [inclua a data relevante, se aplicável]

- Texto principal: [descreva o produto, evento ou mensagem principal]

- Call to action: [incentive a interação, como "Saiba mais!", "Compre agora!" ou "Participe!"]

- Aplicação do template para gerar um post: Prompt: "Use o template abaixo para criar um post de mídia social sobre o lançamento de um novo smartphone com câmera avançada.

- Tema: lançamento de smartphone

- Data: 20 de novembro

- Texto principal: apresente o novo modelo 'SmartTech 12', com câmera de 108 MP, design elegante e bateria de longa duração.

<!-- source_page: 7 -->

- Call to action: descubra mais no nosso site e aproveite a pré-venda com descontos exclusivos!"

- Saída do modelo: "Prepare-se para o lançamento do ano! No dia 20 de novembro, descubra o novo 'SmartTech 12', o smartphone com câmera de 108 MP, design premium e bateria que dura o dia todo. Não perca a prévenda e aproveite descontos exclusivos. Saiba mais em nosso site!"

4. Iteração e refinamento:

- Experimente e ajuste: a engenharia de prompts é um processo iterativo. Teste diferentes prompts, analise os resultados e ajuste suas instruções para obter o resultado desejado.

- Seja paciente e persistente: nem sempre o primeiro prompt gerará o texto perfeito. Explore diferentes abordagens e refine suas técnicas com a prática.

### 4.1.3 Exercício de Fixação

Imagine que você precisa gerar um texto para um anúncio de um novo aplicativo de viagens. Utilizando as técnicas de engenharia de prompts, elabore um prompt detalhado que especifique:

- O público-alvo (jovens aventureiros);

- O tom de voz (informal e empolgante);

- O objetivo do anúncio (destacar os benefícios do aplicativo); e

- As informações essenciais (nome do aplicativo, funcionalidades principais).

## 4.2 Ferramentas para Criação de Prompts Eficazes

Na seção anterior, exploramos as principais técnicas de engenharia de prompts. Agora, vamos conhecer algumas ferramentas que podem auxiliar na criação de prompts eficazes, otimizando seu tempo e potencializando seus resultados.

### 4.2.1 Plataformas de Modelos de Linguagem

Existem inúmeras plataformas de modelos de linguagem, porém três se destacam (Figura 6):

<!-- source_page: 8 -->

1. OpenAI® Playground: uma interface interativa para experimentar o GPT-3® e outros modelos da OpenAI®. Permite ajustar parâmetros, testar diferentes prompts e visualizar os resultados em tempo real;

2. Google® Colaboratory (Colab): um ambiente de desenvolvimento online que oferece acesso gratuito a Unidades de Processamento Gráfico (GPUs), ideal para experimentar modelos de linguagem e executar códigos complexos;

3. Hugging Face®: uma plataforma colaborativa com uma vasta coleção de modelos de linguagem pré-treinados, ferramentas para criação de prompts e recursos para desenvolvedores.

**Figura 6 - Plataformas e ferramentas de engenharia de prompts**

Fonte: autoria própria.

### 4.2.2 Ferramentas de Engenharia de Prompts

- PromptBase®: um marketplace para comprar e vender prompts de alta qualidade para diferentes modelos de linguagem e tarefas.

<!-- source_page: 9 -->

- LangChain®: uma framework para desenvolver aplicações com modelos de linguagem, oferecendo recursos para gerenciamento e encadeamento de prompts e integração com outras ferramentas.

- PromptHero®: um repositório de prompts com exemplos e templates para diversas aplicações, como geração de imagens, escrita criativa e tradução.

### 4.2.3 Extensões de Navegador

- AIPRM® for ChatGPT®: uma extensão para Google Chrome® que oferece templates de prompts otimizados para diferentes tarefas no ChatGPT®, como escrita de e-mails, geração de conteúdo e tradução.

- Promptheus®: uma extensão que permite salvar e reutilizar seus prompts favoritos, além de gerar variações de um prompt automaticamente.

### 4.2.4 Dicas para Escolher Ferramentas

- Objetivo: defina qual o seu objetivo com a ferramenta. Você precisa de uma plataforma para experimentar modelos, um repositório de prompts prontos ou uma extensão para auxiliar na escrita?

- Modelo de linguagem: verifique se a ferramenta é compatível com o modelo de linguagem que você deseja usar (GPT-3®, LaMDA® etc.).

- Funcionalidades: analise as funcionalidades oferecidas pela ferramenta, como templates de prompts, geração de variações e visualização do fluxo do prompt.

- Usabilidade: escolha uma ferramenta com interface intuitiva e fácil de usar, que se adapte ao seu nível de conhecimento.

### 4.2.5 Exercício de Fixação

Explore a plataforma OpenAI® Playground e experimente gerar diferentes tipos de texto com o GPT-3®. Utilize as ferramentas de edição de parâmetros para ajustar a temperatura, o tamanho máximo do texto e outros aspectos da geração.

<!-- source_page: 10 -->

## 4.3 Exercícios Práticos de Criação de Prompts

Chegou a hora de colocar a mão na massa! Nesta seção, vamos praticar a criação de prompts eficazes para diferentes aplicações, utilizando as técnicas e ferramentas que aprendemos nas seções anteriores. Prepare-se para desafiar sua criatividade e aprimorar suas habilidades em engenharia de prompts. No Quadro 1, apresentamos um exemplo ilustrado de um prompt e o resultado gerado.

Quadro 1 - Exemplo ilustrado do Exercício 1, incluindo prompt utilizado e o resultado gerado

Fonte: autoria própria.

### 4.3.1 Exercício 1: a História Inesperada

- Objetivo: gerar um conto criativo e original com elementos inesperados.

- Prompt: "Escreva um conto curto sobre um(a) [profissão] que descobre um portal mágico para um mundo habitado por [criaturas fantásticas]. O conto deve ter um tom [humorístico/sombrio/reflexivo] e explorar temas como [amizade/coragem/superação]."

- Dicas:

- Preencha as lacunas: escolha uma profissão incomum (ex: bibliotecário, chef de cozinha, astronauta) e criaturas fantásticas originais (ex: sereias falantes, árvores inteligentes, duendes tecnológicos);

<!-- source_page: 11 -->

- Defina o tom e os temas: explore diferentes combinações para criar histórias únicas;

- Experimente: gere diferentes versões do conto, variando os parâmetros do modelo de linguagem (temperatura, tamanho do texto, etc.) e compare os resultados.

### 4.3.2 Exercício 2: a Carta Persuasiva

- Objetivo: gerar uma carta persuasiva para convencer alguém a [ação desejada].

- Prompt: "Escreva uma carta formal e persuasiva para [destinatário], com o objetivo de convencê-lo(a) a [ação desejada]. Utilize argumentos convincentes e linguagem culta. A carta deve ter no máximo [número] palavras."

- Dicas:

- Contexto: forneça contexto relevante sobre o destinatário e a ação desejada;

- Exemplos: inclua exemplos de cartas persuasivas para inspirar o modelo;

- Tom e estilo: ajuste o tom e o estilo da carta de acordo com o destinatário e o objetivo da comunicação.

### 4.3.3 Exercício 3: o Roteiro Criativo

- Objetivo: gerar um roteiro para um vídeo curto e criativo sobre [tema].

- Prompt: "Imagine que você é um roteirista de cinema. Crie um roteiro detalhado para um vídeo curto (máximo [duração]) sobre [tema]. O vídeo deve incluir [elementos visuais] e ter um tom [emocional/humorístico/informativo]."

- Dicas:

- Formato: especifique o formato do roteiro (cenas, diálogos, descrições);

- Público-alvo: defina o público-alvo do vídeo para adaptar a linguagem e o estilo;

- Detalhes: quanto mais detalhes você fornecer no prompt, mais completo será o roteiro gerado.

### 4.3.4 Exercício 4: Análise de Sentimentos em Reviews

- Objetivo: utilizar um modelo de linguagem para analisar o sentimento expresso em reviews de um produto/serviço.

<!-- source_page: 12 -->

- Prompt: "Analise o sentimento expresso nos seguintes reviews de [produto/serviço]: [lista de reviews]. Classifique cada review como positivo, negativo ou neutro. Justifique sua classificação com base nas palavras e expressões utilizadas."

- Dicas:

- Formato: organize os reviews em uma lista ou tabela para facilitar a análise do modelo;

- Contexto: forneça contexto sobre o produto/serviço para auxiliar na interpretação dos reviews;

- Exemplos: se possível, inclua exemplos de reviews com diferentes sentimentos para calibrar o modelo.

### 4.3.5 Exercício 5: Geração de Ideias Criativas

- Objetivo: Peça ao modelo para criar uma lista de ideias para um comercial de um novo smartphone.

- Prompts:

- 
1) "Sugira cinco ideias criativas para um comercial de um smartphone moderno."

- 
2) "Gere cinco ideias para um comercial de um smartphone, destacando sua
câmera de alta resolução e bateria de longa duração."

- Análise: Qual prompt gerou ideias mais alinhadas às características do produto?

Agora, com esses conhecimentos, você está preparado/a para criar prompts mais eficazes e explorar todo o potencial das ferramentas de IA Generativa. Na próxima Unidade, avançaremos para ferramentas para manipulação de modelos de linguagem.

CURIOSIDADES…  Sabia que a comunidade de engenharia de prompt está crescendo rapidamente, com fóruns online, grupos de discussão e até competições de prompts?

 A engenharia de prompts está sendo aplicada em áreas como a arte, a música e o design, permitindo que artistas explorem novas formas de criatividade com a IA.

 Existem ferramentas que utilizam algoritmos de aprendizado de máquina para otimizar prompts automaticamente, buscando os melhores resultados para diferentes modelos de linguagem.

<!-- source_page: 13 -->

Saiba mais…

- Artigos:

LIU, P. et al. (2023). Pre-train, prompt, and predict: a systematic survey of prompting methods in natural language processing.

Um estudo abrangente sobre os diferentes métodos de engenharia de prompt em PLN.

PROMPT ENGINEERING (2025). The art of asking the right questions.

Um guia detalhado sobre como estruturar prompts para modelos de linguagem. Inclui exemplos de diferentes tipos de tarefas.

- Livros:

PARLI, V.. Prompt Engineering for ChatGPT®.

Um guia prático com dicas e exemplos para dominar a arte da engenharia de prompts com o ChatGPT®.

GOODSAY, R.. Learning prompting.

Um livro interativo que ensina os fundamentos da engenharia de prompts, utilizando exemplos e exercícios.

- Links:

OpenAI® Cookbook

Um repositório com exemplos de código e prompts para diversas aplicações com os modelos da OpenAI®.

Awesome Prompt Engineering®

Uma lista curada de recursos sobre engenharia de prompts, incluindo artigos, ferramentas e datasets.

<!-- source_page: 14 -->

Para relembrar…

- Engenharia de prompts: é a arte e a ciência de elaborar instruções eficazes para guiar modelos de linguagem na geração de texto.

- Técnicas de engenharia de prompts: objetividade, especificidade, contexto, exemplos, prompt chaining e templates.

- Ferramentas: plataformas de modelos de linguagem, ferramentas de engenharia de prompts, extensões de navegador e ferramentas de visualização.

- Exercícios práticos: criação de prompts para diferentes aplicações, como contos, cartas, roteiros e análise de sentimentos.

- Engenharia de prompts é uma habilidade essencial para quem deseja dominar a IA Generativa de Texto. Ao aprimorar suas técnicas e utilizar as ferramentas certas, você poderá obter resultados incríveis com os modelos de linguagem.
