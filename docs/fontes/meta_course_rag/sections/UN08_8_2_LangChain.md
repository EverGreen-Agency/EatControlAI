---
unit: 8
unit_title: "Ecossistema de ferramentas - visão geral"
section: "8.2"
section_title: "LangChain®"
source_file: "Un8_curso_meta(1).pdf"
source_markdown: "units/UN08_Ecossistema_de_ferramentas_vis_o_geral.md"
source_pages: [4, 5, 6, 7]
language: "pt-BR"
---

## 8.2 LangChain®

Enquanto o LangFlow® abstrai a programação via UI, o LangChain® é o framework de código que atua nos bastidores. Voltado a desenvolvedores, o LangChain® fornece classes e funções Python para montar cadeias (chains) e agentes envolvendo modelos de linguagem.

Diferente do ambiente visual do LangFlow®, aqui você escreve código para instanciar modelos, definir prompts e conectar ferramentas, o que oferece maior flexibilidade e controle total sobre cada detalhe da implementação. Conceitos principais do LangChain®:

- Prompt: no contexto do LangChain®, prompt se refere geralmente a um template de texto com parâmetros, que será preenchido com entradas do usuário ou dados dinâmicos. A classe PromptTemplate permite definir esses modelos de prompt de forma estruturada (por exemplo: “Traduzir ‘{frase}’ para inglês” em que {frase} é preenchido na hora do uso). Gerenciar bem os prompts é crucial para orientar o comportamento do LLM.

- LLM e chains: o LangChain® simplifica o uso de LLMs fornecendo abstrações como LLMChain, que une um modelo de linguagem e um prompt para formar uma cadeia reutilizável (IBM, 2024b). Uma chain básica recebe uma entrada (por exemplo, uma pergunta), formata um prompt e consulta o LLM, retornando a resposta. LangChain® suporta encadear múltiplas chains ou passos em sequência, permitindo workflows mais complexos em vez de uma simples chamada. Esse encadeamento de prompts e resultados – prompt chaining – permite resolver tarefas passo a passo, nas quais a saída de um passo alimenta o próximo (por exemplo, primeiro extrair dados relevantes de um texto, depois formular uma resposta baseada nesses dados).

- Tools (ferramentas): ferramentas são funções ou APIs externas que um agente pode chamar durante sua execução. O LangChain® permite integrar ferramentas como buscadores web, calculadoras, banco de dados etc.. Cada ferramenta é abstraída com um nome e descrição e o agente pode decidir invocá-las, quando necessário. Por exemplo, podemos registrar uma ferramenta “Calculadora” que, dada uma expressão, retorna o resultado; um agente inteligente pode, então, escolher usar essa ferramenta para perguntas matemáticas. A integração de tools expande consideravelmente o poder dos agentes, pois eles não ficam limitados apenas ao conhecimento estático do modelo de linguagem, podem buscar informações atualizadas, fazer cálculos e interagir com o ambiente externo.

- Agent (agente): é a estrutura que combina um LLM com lógica para decidir qual ação tomar na sequência. Em LangChain®, um agente utiliza um modelo de linguagem não apenas para gerar respostas diretas, mas também para planejar etapas, podendo selecionar ferramentas para usar e até invocar outros agentes. Internamente, um agente segue um ciclo de pensar -> agir -> observar resultado -> pensar de novo, orientado por um prompt especial que define como ele deve tomar decisões (o prompt do agente inclui instruções como “Se a pergunta precisar de cálculo, use a calculadora” etc.). Os agentes permitem criar sistemas dinâmicos: por exemplo, um agente de pergunta e resposta que primeiro faz buscas na web (usando uma ferramenta de busca) e depois sintetiza a resposta baseada nos resultados encontrados.

Para ilustrar esses conceitos, vejamos um “hello world” conceitual em LangChain®, recriando de forma programática o agente simples de recomendação do exemplo anterior. Em código Python, usaríamos o LangChain® assim:

```text
from langchain.prompts import PromptTemplate
from langchain_openai import ChatOpenAI
from langchain.chains import LLMChain # ainda existe, mas o pipe é preferido
# Define o prompt
prompt = PromptTemplate.from_template(
“Você é um assistente especialista em tecnologia. “
“O usuário descreve seu interesse: {interesse}. “
“Sugira uma tecnologia apropriada para ele aprender em seguida, com uma
breve justificativa.”
)
# Modelo mais atual (substitui o antigo ‘OpenAI’)
llm = ChatOpenAI(model=”gpt-4o-mini”, temperature=0.7)
# Forma moderna: composição via pipes
chain = prompt | llm
# Executa a chain
resposta = chain.invoke({“interesse”: “Quero melhorar em desenvolvimento web
frontend, o que devo aprender?”})
print(resposta.content)
```

No código acima, utilizamos os componentes modernos do LangChain®. O PromptTemplate define o prompt parametrizado, que contém o texto-base e os espaços reservados (placeholders) para as variáveis que serão preenchidas em tempo de execução. Em seguida, instanciamos o modelo de linguagem ChatOpenAI (da biblioteca langchain_openai), que representa uma interface direta com os modelos da OpenAI®, como o GPT-4o-mini. A composição entre o prompt e o modelo é feita de forma declarativa usando o operador de pipe (|), que conecta o fluxo de entrada e saída dos componentes — essa é a nova forma recomendada, chamada de Lang Chain Expression Language (LCEL). Ao chamar chain. invoke(...), passando o parâmetro interesse com a dúvida do usuário, o LangChain preenche o template, envia o prompt ao modelo e retorna a resposta como um objeto AIMessage.

O conteúdo textual da resposta pode ser acessado por resposta.content. A saída impressa seria algo assim:

Aprenda **React.js**! React é uma biblioteca JavaScript amplamente utilizada para desenvolvimento frontend. Ela vai te ajudar a criar interfaces de usuário interativas de forma eficiente e é muito demandada no mercado de trabalho.

Nesse “hello world” em LangChain®, é ilustrada a simplicidade de orquestrar um modelo de linguagem via código. Com poucas linhas, conseguimos um agente funcional. A grande vantagem aqui é que podemos estender esse código facilmente. Se quisermos adicionar uma ferramenta calculadora, por exemplo, bastaria definir a função e incluí-la na inicialização de um agente do LangChain®. Se quisermos que o agente se “lembre” das interações (memória), podemos envolver a chain com um objeto de memória que armazena o histórico. Toda a flexibilidade do Python® e das bibliotecas está disponível, permitindo lidar com casos complexos que, em plataformas low-code, poderiam ser desafiadores de implementar. Em resumo, o LangChain® fornece o arcabouço de baixo nível para construir workflows personalizados envolvendo LLMs (IBM, 2024b). Seus conceitos de prompts, chains e tools se combinam para dar ao desenvolvedor controle total sobre agentes. Use LangChain® (hard-code) quando você precisar de personalização máxima, integrar lógica customizada ou escalar o agente de forma mais controlada. A contrapartida é escrever e manter o código. No entanto, para programadores experientes, isso significa poder aplicar todas as boas práticas de engenharia de software (versão de código, testes, logging detalhado etc.) no desenvolvimento de agentes inteligentes.
