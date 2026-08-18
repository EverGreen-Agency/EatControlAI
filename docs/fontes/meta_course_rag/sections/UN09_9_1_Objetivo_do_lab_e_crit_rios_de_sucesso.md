---
unit: 9
unit_title: "Laboratório 01 - Seu primeiro agente"
section: "9.1"
section_title: "Objetivo do lab e critérios de sucesso"
source_file: "Un9_curso_meta.pdf"
source_markdown: "units/UN09_Laborat_rio_01_Seu_primeiro_agente.md"
source_pages: [2, 3]
language: "pt-BR"
---

## 9.1 Objetivo do lab e critérios de sucesso

Objetivo: desenvolver um agente capaz de receber perguntas de um usuário e responder adequadamente, dentro de um escopo simples escolhido. Você pode optar por um agente de FAQ (que responde perguntas frequentes sobre um certo tópico, produto ou serviço) ou por um agente de recomendações simples (que sugere algo com base em uma preferência do usuário, como recomendamos no Capítulo 3). Escolha o caso que achar mais interessante considerando que ambos envolvem lógica similar: interpretar a entrada do usuário e produzir uma resposta útil usando um modelo de linguagem. Para fins didáticos, vamos supor aqui um agente do tipo Recomendador, que daremos o nome de TechAdvisor. Esse agente receberá do usuário uma descrição do que ele busca aprender ou melhorar (por exemplo: “quero aprender uma linguagem de programação para análise de dados”) e responderá sugerindo uma tecnologia ou ferramenta, com uma breve justificativa. Critérios de sucesso:

- Funcionalidade básica: ao final, seu agente deve conseguir retornar respostas coerentes para perguntas dentro do escopo definido. No nosso exemplo, se perguntado, o TechAdvisor deve recomendar uma tecnologia relevante. Se você optou por um FAQ, o agente deve fornecer uma resposta plausível para cada pergunta frequente esperada.

- Integração LangFlow® e LangChain®: você deve conseguir tanto executar o agente no LangFlow® (usando a interface gráfica), quanto executá-lo via um script em Python®

(usando LangChain®). Em ambas as implementações, o comportamento deverá ser equivalente. Esse critério garante que entendamos como a configuração visual traduz para código e vice-versa.

- Respostas no idioma correto e contexto apropriado: vamos trabalhar com o agente respondendo em português (afinal, este ebook está em português). Portanto, o conteúdo gerado deve estar em Português-Brasil (PT-BR) e manter o tom especificado no prompt (por exemplo, um tom amigável e profissional).

- Robustez básica: o agente não precisa ser perfeito, mas deve lidar minimamente com variações. Por exemplo, se o usuário formular a solicitação de maneira diferente (“Qual tecnologia devo estudar para melhorar em front-end?”), o agente ainda assim deve conseguir responder razoavelmente. Também esperamos que ele não quebre com entradas vazias ou muito fora de contexto: caso ele não saiba responder, deve, ao menos, dizer que não pode ajudar, em vez de falhar.

- Uso correto das ferramentas: no caso do LangFlow®, isso significa conectar corretamente os componentes (prompt, LLM etc.) sem erros de configuração. No caso do LangChain®, significa escrever o código de forma clara e seguir boas práticas (por exemplo, separando a definição de prompt, instanciando o modelo com as credenciais adequadas etc.). Em ambos, garanta que as variáveis estejam sendo passadas e preenchidas adequadamente (por exemplo, o nome do parâmetro no PromptTemplate do LangChain® deve deve ser o mesmo usado no LangFlow®).

Em resumo, daremos a seguir um passo a passo para construir o agente no LangFlow® e, depois, em código. Considere que o critério principal de sucesso é você conseguir interagir com seu agente, fazer perguntas e receber respostas válidas. Se algo não funcionar de primeira, use as seções de teste e checklist para identificar possíveis ajustes.
