---
corpus: "CEIA e Meta - AI Glasses Brasil"
unit: 1
title: "Como os modelos aprendem?"
source_file: "Un1_curso_meta.pdf"
source_pages: 8
source_sha256: "953ac276ce99faa39928d73e2d69f6873ad1832d70fc0724dd24829be80bc0df"
language: "pt-BR"
normalization: "text layer normalized; page provenance preserved; visual-only supplements transcribed from source pages when identified"
---

# Unidade 1: Como os modelos aprendem?

> Fonte única: `Un1_curso_meta.pdf`. O conteúdo abaixo foi normalizado para busca/RAG, sem complementação por fontes externas.

<!-- source_page: 2 -->

# Unidade I - Como os modelos aprendem?

## 1.1 Como os modelos aprendem?

A IA é uma área fascinante da tecnologia que permite que máquinas executem tarefas de maneira inteligente. Mas como esses sistemas "aprendem"? Nesta unidade, exploraremos os principais conceitos por trás do aprendizado de máquina, desde as primeiras abordagens até as mais avançadas. Você entenderá as diferenças entre aprendizado supervisionado, não supervisionado e por reforço, além de conhecer as redes neurais e suas aplicações. Vamos embarcar nessa jornada para entender como os modelos de IA transformam dados em conhecimento.

## 1.2 IA Simbólica: o início de tudo

A história da IA começa com a chamada IA Simbólica, também conhecida como "boa e velha IA". Essa abordagem baseia-se na ideia de que a inteligência pode ser representada por regras lógicas e manipulação de símbolos. Imagine que você está ensinando um computador a identificar animais. Na IA Simbólica, você precisaria fornecer um conjunto de regras explícitas, como "se um animal voa, então é uma ave" ou "se mia, então é um gato". Essa abordagem foi usada na criação de sistemas especialistas, como o MYCIN®, um software da década de 1970 que ajudava médicos a diagnosticar infecções. No entanto, o grande problema da IA Simbólica é que o mundo real é cheio de exceções e regras complexas. Por exemplo, morcegos voam, mas não são aves, enquanto avestruzes e pinguins são aves, mas não voam. Criar regras para todos os cenários torna-se inviável, o que levou os cientistas a buscar novas abordagens, como o aprendizado de máquina.

<!-- source_page: 3 -->

## 1.3 Aprendizado supervisionado e não supervisionado

O aprendizado de máquina surgiu como uma solução para os desafios da IA Simbólica. Em vez de definir regras fixas, os cientistas desenvolveram métodos que permitem que os sistemas aprendam padrões a partir de dados. Existem duas formas principais de aprendizado: supervisionado e não supervisionado.

- Aprendizado supervisionado: Imagine que você é um professor ensinando uma criança a identificar frutas. Você mostra uma imagem de uma maçã e diz "isto é uma maçã". Depois, mostra um morango e diz "isto é um morango". Com o tempo, a criança aprende a reconhecer cada fruta. Esse é o princípio do aprendizado supervisionado: o modelo aprende com exemplos rotulados. A representação simplificada de seu funcionamento é apresentada na Figura 2.

**Figura 2 - Representação simplificada do funcionamento do aprendizado supervisionado.**

Fonte: Adaptada de Mohri et al. (2018); Parmley et al. (2019); Rad & Rafezi (2020); Jo (2021).

- Aprendizado não supervisionado: Agora imagine que você dá um monte de frutas para a criança, mas não diz quais são. Em vez disso, ela observa os padrões sozinha e agrupa frutas parecidas. O aprendizado não supervisionado funciona assim: o sistema tenta encontrar padrões nos dados sem que tenha respostas prontas. A sua representação simplificada está apresentada na Figura 3.

<!-- source_page: 4 -->

**Figura 3 - Representação simplificada do funcionamento do aprendizado não supervisionado.**

Fonte: Adaptada de Mohri et al. (2018); Parmley et al. (2019); Rad & Rafezi (2020); Jo (2021).

## 1.4 Aprendizado por reforço

Diferente do aprendizado supervisionado e não supervisionado, o aprendizado por reforço é baseado na ideia de "tentativa e erro". Imagine que você quer ensinar um cachorro a pegar um graveto. No início, ele não sabe o que fazer, mas quando pega o graveto e você oferece um petisco, ele percebe que essa ação traz uma recompensa. Com o tempo, ele aprende a repetir esse comportamento para ganhar mais petiscos. Segundo Sutton e Barto (2018), esse tipo de aprendizado busca maximizar as recompensas que um agente recebe ao interagir com um ambiente incerto, aprendendo gradualmente quais ações produzem os melhores resultados. A principal área de aplicação do aprendizado por reforço é a robótica, pois permite que máquinas aprendam a realizar tarefas de forma autônoma em ambientes dinâmicos. Um robô que precisa aprender a caminhar, por exemplo, pode testar diferentes formas de movimento e receber feedback sobre quais estratégias funcionam melhor, adaptando-se progressivamente. Além da robótica, esse tipo de aprendizado também é usado em jogos, otimização de processos e finanças.

## 1.5 Redes neurais

Redes neurais artificiais foram inspiradas no funcionamento do cérebro humano. Elas são compostas por "neurônios" artificiais organizados em camadas, onde cada neurônio recebe informações, processa e repassa adiante.

<!-- source_page: 5 -->

Para entender melhor, imagine que você quer ensinar um robô a reconhecer a letra "A". Ele recebe milhares de exemplos da letra, cada um com pequenas variações (diferentes fontes, tamanhos, escritas à mão). Com o tempo, a rede neural aprende os padrões e consegue identificar a letra mesmo em condições diferentes.

## 1.6 Machine Learning versus Deep Learning

O aprendizado de máquina (Machine Learning) é um conjunto de técnicas que permite que computadores aprendam a partir de dados, sem serem explicitamente programados para cada tarefa. Dentro do aprendizado de máquina, existe uma abordagem mais avançada chamada Deep Learning (Aprendizado Profundo), que utiliza redes neurais profundas para modelar padrões complexos.

- Machine Learning (ML): em abordagens tradicionais de aprendizado de máquina, o modelo depende de um especialista para escolher quais características dos dados são mais relevantes para a tarefa. Por exemplo, um sistema de recomendação de filmes pode utilizar informações como gênero, tempo de duração e avaliações dos usuários para fazer previsões.

- Deep Learning (DL): aqui, o próprio modelo descobre automaticamente os padrões nos dados. Segundo LeCun, Bengio e Hinton (2015), esses modelos são compostos por múltiplas camadas de processamento que aprendem representações hierárquicas dos dados, o que permite avanços significativos em tarefas como reconhecimento de imagens, fala e texto. Por exemplo, em uma indústria de fabricação de garrafas, um sistema baseado em Deep Learning pode analisar imagens das garrafas em uma esteira e identificar defeitos automaticamente. Ele aprende, a partir de milhares de exemplos, a distinguir garrafas em perfeitas condições daquelas com rachaduras, bolhas ou desalinhamentos na tampa, sem necessidade de regras manuais prédefinidas.

Em resumo, o DL faz parte do ML, mas se diferencia por sua capacidade de aprender representações mais abstratas e sofisticadas dos dados, tornando-se essencial em aplicações como carros autônomos, assistentes virtuais, diagnósticos médicos baseados em imagens, dentre outras.

<!-- source_page: 6 -->

## 1.7 O que os modelos de IA podem e não podem fazer?

Os modelos de IA estão cada vez mais presentes em nosso cotidiano, automatizando tarefas e tornando processos mais eficientes. No entanto, é fundamental compreender suas capacidades e limitações para um uso responsável. A seguir, apresentamos alguns exemplos do que os modelos de IA podem e não podem fazer atualmente:

**Tabela 1 - Exemplificação das potencialidades e restrições da inteligência artificial**

Fonte: Autoria própria.

Os modelos de IA continuam avançando e se tornando parte essencial do nosso dia a dia, automatizando tarefas e otimizando processos em diversas áreas. No entanto, é importante compreender não apenas seu potencial, mas também suas limitações. A IA não é uma solução para tudo, mas uma ferramenta poderosa que, quando bem utilizada, pode trazer grandes benefícios. Acompanhar sua evolução nos permite entender melhor suas aplicações atuais e como podemos aproveitá-las de forma eficiente e responsável. Na próxima unidade, vamos investigar como as tecnologias de IA, alicerçadas nesses métodos de aprendizado, estão transformando áreas como Ciência de Dados, Processamento de Linguagem Natural (NLP), Visão Computacional e até mesmo Robótica e Automação. Como veremos, essas tecnologias estão cada vez mais presentes em nossas vidas, possibilitando desde a análise de grandes volumes de dados até a automação de tarefas complexas, revolucionando setores como saúde, educação e segurança.

<!-- source_page: 7 -->

Saiba mais…

Curso introdutório gratuito de Machine Learning: "Machine Learning Crash Course" – Google AI®. Ideal para quem quer ter um primeiro contato prático com conceitos como aprendizado supervisionado, redes neurais e avaliação de modelos, utilizando exercícios interativos e exemplos reais. Disponível em: https://developers.google.com/machine-learning/crash-course?hl=pt-br

Livro: Artificial Intelligence: A Modern Approach – Russell; Norvig (2022). Considerado uma das principais referências acadêmicas em IA, o livro aborda desde métodos clássicos, como IA simbólica, até técnicas modernas de aprendizado de máquina e agentes inteligentes.

Curso básico de IA: Elements of AI – iniciativa gratuita da Universidade de Helsinki. Um curso acessível para o público geral que busca introduzir conceitos fundamentais de IA, sem exigir conhecimentos prévios em programação ou matemática avançada. Disponível em: https://www.elementsofai.com/es/

Série de vídeos: Neural Networks – 3Blue1Brown (YouTube). Uma introdução visualmente intuitiva ao funcionamento das redes neurais, começando com o vídeo "But what is a neural network?", excelente para quem quer entender como os modelos aprendem de maneira dinâmica e interativa. Disponível em: https://www.youtube.com/watch?v=aircAruvnKk&list=PLZHQObOWTQD- NU6R1_67000Dx_ZCJB-3pi
