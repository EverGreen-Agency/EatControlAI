---
unit: 9
unit_title: "Laboratório 01 - Seu primeiro agente"
section: "9.5"
section_title: "Extensões sugeridas"
source_file: "Un9_curso_meta.pdf"
source_markdown: "units/UN09_Laborat_rio_01_Seu_primeiro_agente.md"
source_pages: [13, 14, 15]
language: "pt-BR"
---

## 9.5 Extensões sugeridas

Seu primeiro agente está funcional – parabéns! A partir daqui, existem inúmeras maneiras de tornar o agente mais poderoso, seguro e útil. Listamos algumas extensões sugeridas para você experimentar. Essas não serão resolvidas passo a passo como acima (faz parte do aprendizado você pesquisar e implementar), mas daremos direções:

- Filtro de linguagem e conteúdo: Para evitar que seu agente produza ou aceite conteúdo inadequado, você pode implementar um filtro. Por exemplo, restringir que ele só responda em português, independentemente da língua da pergunta, ou que se recuse a discutir certos tópicos (violência, hacking etc.). Como fazer isso? Uma abordagem simples é, antes de passar a pergunta ao agente, verificar com palavraschave ou usar uma pequena lista de termos banidos. Se detectar algo, o agente pode responder com uma mensagem padrão (“Desculpe, não posso ajudar com isso.”). Outra abordagem é integrar uma ferramenta moderadora, por exemplo, OpenAI® tem uma API de moderação que sinaliza conteúdo impróprio. Você poderia chamar essa API automaticamente antes de prosseguir para a resposta do LLM principal. No LangFlow®, isso poderia ser um nó extra de checagem antes do LLM; em LangChain®, seria um trecho de código if antes de chain.run.

- Melhoria de prompts (prompt engineering): Peque pelos aprendizados dos testes e refine o prompt do agente. Talvez adicionar exemplos (few-shot) ajude. Por exemplo, para TechAdvisor, você poderia fornecer no prompt uma exemplo de diálogo:

Exemplo: Usuário: quero começar em ciência de dados. Assistente: Eu recomendaria aprender Python com a biblioteca Pandas, pois... --- Agora responda ao usuário atual considerando a solicitação dele. Usuário: {interesse} Assistente:

- Isso serve de guia de estilo para o LLM. Experimente incluir um exemplo e veja se as respostas ficam mais alinhadas. Outra melhoria é personalizar mais as sugestões. Dessa forma, podemos instruir: “Se o usuário mencionar uma tecnologia específica, compare com outra”, ou “sempre inclua um recurso recomendado (exemplo: documentação oficial)”. Lembre-se: prompt engineering é iterativo; pequenas mudanças no texto podem melhorar (ou piorar) as saídas, então, teste cada ajuste.

- Adicionar uma ferramenta simples: Integrar uma ferramenta externa ao agente tornaria nosso fluxo mais interessante. Por exemplo, imagine que para certas perguntas o agente pudesse consultar uma API de trending technologies para dar uma resposta atualizada. Uma ideia concreta: adicionar uma ferramenta de busca web para que, se o usuário perguntar algo como “qual a tecnologia mais popular em 2025 para mobile?”, o agente possa fazer uma busca rápida e incorporar o resultado na

resposta. No LangChain®, você poderia usar o SerpAPIWrapper (ferramenta de busca do Google®) e criar um agente do tipo zero-shot-react com essa ferramenta disponível. No LangFlow®, há componentes de agentes que você pode configurar para usar ferramentas (a seção “Utilizando Agentes e Ferramentas” do tutorial LangFlow® indica suporte a isso) (WENTING, 2025). Como exercício, tente permitir ao seu agente usar pelo menos uma ferramenta, que pode ser um simples calculadora, para fazer contas se seu agente atender FAQ de finanças, ou um buscador como citado. Avalie o ganho de complexidade: agentes com ferramentas precisam de um prompt de agente (um pouco diferente do prompt normal) e têm overhead de decidirem quando usar a ferramenta. Mas é um passo importantíssimo rumo a agentes mais autônomos e capazes.

- Memória conversacional (extensão para múltiplas interações): Se você quer que seu agente lembre do que foi dito anteriormente em uma conversa longa, será preciso adicionar memória. No LangChain®, seria usar classes como ConversationBufferMemory acopladas à chain ou ao agente. No LangFlow®, ao importar um exemplo de “cadeia conversacional”, como mencionado no tutorial (WENTING, 2025), você já viu componentes de memória integrados. Tente habilitar memória para que, por exemplo, o usuário possa perguntar duas coisas sequencialmente e o agente considere o contexto. Por exemplo, o usuário solicita: “Quero aprender algo para front-end.” (agente responde). Usuário: “Esse que você recomendou é difícil de aprender?” – o agente com memória deveria entender que “esse” refere-se à tecnologia recomendada antes. Implementar isso exigiria incluir o histórico no prompt (LangChain® faz isso automaticamente com Memory). Fica como desafio para quando se sentir confortável.

- Aprimorar o front-end/interação: Embora não seja foco deste livro técnico, lembrese que um agente, muitas vezes, será integrado a uma interface (um chat web, um bot do Telegram® etc.). Pensar na experiência do usuário pode revelar extensões úteis, por exemplo, formatar a resposta com Markdown (LangChain® permite usar MarkdownFormatter ou simplesmente incluir no prompt instruções de formatação). Ou dividir a resposta em passos numerados, se for um FAQ tipo tutorial. Essas melhorias de apresentação podem ser consideradas parte da engenharia de prompt e pósprocessamento.

Atividade proposta: Escolha uma das extensões acima (ou mais de uma!) e tente implementála no seu agente. Por exemplo, você pode tentar adicionar a filtragem de linguagem ou integrar uma ferramenta de busca. Documente o processo e teste novamente o agente com situações, nas quais essa extensão faça a diferença. Sem olhar soluções prontas, use a documentação do LangChain® e LangFlow®, além de buscas online para superar os desafios, essa pesquisa ativa consolida muito o aprendizado.

Com isso, encerramos o Laboratório 01. Você construiu seu primeiro agente inteligente e o fez funcionar em dois ambientes diferentes. Mais importante, aprendeu a pensar de forma modular: prompt, modelo, chain, ferramenta, memória – são blocos que você pode combinar de diversas formas para criar agentes cada vez mais sofisticados. Nos próximos capítulos, exploraremos agentes mais avançados, integraremos fontes de dados externas e abordaremos novos padrões de projeto. Continue experimentando e bom desenvolvimento!

Saiba mais…

- Guia prático para FastAPI aplicado a agentes: https://fastapi.tiangolo.com/

- Boas práticas iniciais de logging em aplicações Python: https://realpython.com/pythonlogging/

- Introdução a testes automatizados para APIs (essenciais para fornecer serviços para os agente): https://testdriven.io/blog/fastapi-testing/

Para relembrar…

- O laboratório introduz a construção prática de um agente simples.

- LangFlow® permite criar agentes visualmente, sem código inicial.

- LangChain® mostra como replicar o mesmo agente via programação.

- Testes básicos e logging são essenciais para validar comportamento e qualidade.
