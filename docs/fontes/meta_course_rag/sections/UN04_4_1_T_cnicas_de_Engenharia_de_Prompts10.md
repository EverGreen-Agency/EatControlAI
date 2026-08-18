---
unit: 4
unit_title: "Engenharia de Prompts para Geração de Texto"
section: "4.1"
section_title: "Técnicas de Engenharia de Prompts10"
source_file: "Un4_curso_meta(1).pdf"
source_markdown: "units/UN04_Engenharia_de_Prompts_para_Gera_o_de_Texto.md"
source_pages: [2, 3, 4, 5, 6, 7]
language: "pt-BR"
---

## 4.1 Técnicas de Engenharia de Prompts10

Na Unidade I, vimos o poder dos modelos de linguagem e suas diversas aplicações. Agora, vamos mergulhar no mundo da engenharia de prompts, a arte e a ciência de se comunicar de forma eficaz com a IA para obter os melhores resultados. A engenharia de prompts é uma habilidade essencial para quem trabalha com modelos de linguagem generativa. Um prompt é o texto ou instrução fornecida ao modelo para que ele possa realizar uma tarefa específica. A forma como um prompt é estruturado influencia diretamente a qualidade e relevância da resposta gerada pelo modelo. Nesta seção, você aprenderá técnicas fundamentais para criar prompts eficazes, aumentando a precisão e a utilidade das respostas obtidas.

### 4.1.1 O Que É Engenharia de Prompts?

Analogia: Imagine que você está conversando com um gênio da lâmpada. Para que ele realize seus desejos, você precisa expressá-los de forma objetiva e precisa, certo? A engenharia de prompts funciona de forma similar. É a técnica de elaborar instruções (prompts) precisas e detalhadas para guiar os modelos de linguagem na geração de textos que atendam às suas necessidades. Mas, o que é um prompt? Um prompt é a instrução ou entrada inicial fornecida a um modelo de linguagem, descrevendo o que você deseja que ele faça. Ele pode ser uma frase, uma pergunta ou até mesmo um conjunto de parâmetros que orientam o modelo a gerar uma resposta ou realizar uma tarefa específica. A qualidade e a clareza do prompt determinam diretamente a relevância e a precisão do resultado gerado pelo modelo. Na Figura 3, é apresentado um fluxograma detalhando o processo de engenharia de prompts, desde a definição do objetivo até o refinamento e ajuste dos prompts.

10 Técnica de elaboração de prompts precisos e detalhados para guiar modelos de linguagem a gerar respostas alinhadas às necessidades.

**Figura 3 - Fluxograma de engenharia de prompts**

Fonte: autoria própria.

### 4.1.2 Dominando as Técnicas

Existem diversas técnicas para criar prompts eficazes. Vamos explorar algumas das principais:

1. Clareza e especificidade:

- Seja preciso e conciso: Evite ambiguidades e linguagem vaga. Quanto mais preciso for o seu prompt, melhor o modelo entenderá o que você deseja. Na Figura 4, é apresentado um exemplo de prompt vago versus um prompt bem estruturado.

- Exemplo: em vez do seguinte prompt "Escreva sobre a História do Brasil", prefira "Gere um texto de 500 palavras sobre a História do Brasil, focando no período colonial e seus impactos na sociedade atual".

- Defina o formato: especifique o tipo de texto desejado (poema, artigo, roteiro etc.), o público-alvo e o tom de voz.

- Exemplo: "Escreva um conto infantil sobre um gatinho que se perde na floresta, com um tom divertido e linguagem simples."

**Figura 4 - Exemplo de prompt vago versus prompt bem estruturado**

Fonte: autoria própria. 2. Contexto e informação:

- Forneça contexto relevante: inclua informações importantes para guiar o modelo na direção desejada.

- Exemplo: "Imagine um mundo onde os animais falam. Escreva um diálogo entre um cachorro e um gato discorrendo sobre a importância da amizade."

- Use exemplos: mostre ao modelo o que você espera, fornecendo exemplos de textos similares ao que você deseja.

- Exemplo: "Escreva um poema curto sobre a natureza, no estilo de Carlos Drummond de Andrade: 'No meio do caminho tinha uma pedra...' ".

3. Técnicas avançadas:

- Prompt chaining: divida tarefas complexas em etapas menores, usando a saída de um prompt como entrada para o próximo (Figura 5).

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
