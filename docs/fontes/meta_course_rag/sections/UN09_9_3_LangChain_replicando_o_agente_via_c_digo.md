---
unit: 9
unit_title: "Laboratório 01 - Seu primeiro agente"
section: "9.3"
section_title: "LangChain®: replicando o agente via código"
source_file: "Un9_curso_meta.pdf"
source_markdown: "units/UN09_Laborat_rio_01_Seu_primeiro_agente.md"
source_pages: [6, 7, 8, 9]
language: "pt-BR"
---

## 9.3 LangChain®: replicando o agente via código

Agora que o agente TechAdvisor funciona no LangFlow®, vamos recriar sua lógica usando o LangChain® em Python®. Assim, confirmamos que entendemos cada componente e ganhamos liberdade para futuras modificações programáticas. A meta aqui é alcançar paridade funcional, isto é, o agente em código deve se comportar essencialmente da mesma forma que no fluxo visual. Configuração do ambiente de código: certifique-se de ter o pacote LangChain® instalado (pip install LangChain® para a versão Python®). Também será necessário o pacote do provedor do LLM (por exemplo, OpenAI®), além de configurar sua chave de API via variável de ambiente ou diretamente no código (não é recomendado escrever a chave em código por questões de segurança; aqui assumiremos que a variável OPENAI_API_KEY já esteja configurada no seu ambiente).

Vamos construir passo a passo, correspondente ao que fizemos no LangFlow®: 1. Importar classes necessárias: No mínimo, precisaremos de PromptTemplate e ChatOpenAI do LangChain®. Essa combinação já cobre o fluxo completo, sem precisar de chains explícitas.

```text
from langchain.prompts import PromptTemplate
from langchain_openai import ChatOpenAI
```

2. Definir o prompt template: esse deve ser exatamente o mesmo texto que você configurou no LangFlow®. A única diferença é que aqui passaremos explicitamente as variáveis. No LangFlow® usamos {interesse}; faremos da mesma forma no código a seguir:

template_text = ( “Você é um assistente que recomenda tecnologias de programação com base no interesse do usuário.\n” “Usuário: {interesse}\n” “Recomende uma tecnologia apropriada para o usuário aprender em seguida e explique brevemente o porquê.” ) prompt = PromptTemplate(input_variables=[“interesse”], template=template_text)

Note que adicionamos quebras de linha \n para formatar como no LangFlow® (cada linha do prompt template). Isso ajuda a organizar o conteúdo. Temos uma primeira linha definindo o papel do agente e depois a estrutura de diálogo. 3. Instanciar o modelo LLM: tal como configuramos o nó de modelo no LangFlow®, aqui criamos um objeto OpenAI, por exemplo:

llm = ChatOpenAI(model=”gpt-4o-mini”, temperature=0.7)

Isso deve usar o modelo GPT-4o-mini. E LangChain® internamente lidará com o prompt (provavelmente, enviando como mensagem do usuário). 4. Criar a chain que une prompt e LLM: agora usamos o operador pipe (|) que conecta os componentes no formato LCEL (LangChain Expression Language):

chain = prompt | llm

Esse objeto chain agora representa exatamente o fluxo: recebe uma variável interesse e retorna uma saída chamando o modelo com o prompt correspondente.

5. Testar a chain com exemplos: vamos simular algumas perguntas do usuário, assim como fizemos no LangFlow®:

```text
# Exemplo 1:
pergunta1 = “quero melhorar minhas habilidades em desenvolvimento web
frontend”
resposta1 = chain.invoke({“interesse”: pergunta1})
print(“Pergunta:”, pergunta1)
print(“Resposta do agente:”, resposta1.content)
# Exemplo 2:
pergunta2 = “interesso em análise de dados, qual tecnologia devo aprender?”
resposta2 = chain.invoke({“interesse”: pergunta2})
print(“\nPergunta:”, pergunta2)
print(“Resposta do agente:”, resposta2.content)
```

Ao rodar esse código, devemos ver impressas duas perguntas e as respostas sugeridas. Compare com o que obteve no LangFlow®: idealmente, as respostas devem ser do mesmo estilo e relevância, já que o prompt e modelo são os mesmos. Talvez haja variações aleatórias (especialmente com temperatura 0.7, as saídas nunca são 100% iguais), mas o conteúdo deve fazer sentido. Por exemplo, possíveis outputs:

- Para a pergunta 1 (front-end): “Eu recomendaria aprender React.js. React é amplamente usado para front-end web, e aprender React vai melhorar muito suas habilidades de construir interfaces de usuário interativas.”

- Para a pergunta 2 (análise de dados): “Uma ótima escolha seria aprender Python® com a biblioteca Pandas®. Python® é muito usado em análise de dados e o Pandas® facilita manipular e extrair informações de conjuntos de dados de forma eficiente.”

Se esses resultados estão de acordo com o esperado, parabéns! Você reproduziu o agente em código que antes existia apenas no LangFlow®. Verificando paridade funcional: garanta que você usou o mesmo prompt e configurações (modelo, temperatura) em ambos os ambientes. Se notar alguma discrepância, volte e ajuste. O importante é percebermos que LangFlow® e LangChain® estão fortemente alinhados: tudo que montamos visualmente era um reflexo dessas classes e chamadas de método. Isso significa que, uma vez confortável com ambos, você pode prototipar rapidamente no LangFlow® e depois extrair um skeletal code para evoluir manualmente, ou vice-versa: “codar” primeiro e usar o LangFlow® como ferramenta de apresentação.

Atividade: Experimente modificar algo no código e observar o efeito. Por exemplo, ajuste a temperatura para 0.3 e veja se as respostas ficam mais “conservadoras” (menos criativas). Ou, então, adicione mais uma linha no prompt, como “Forneça a resposta em, no máximo, duas frases.”, para ver se o agente encurta a sugestão. Esse tipo de experimento rápido ajuda a compreender a sensibilidade do agente a parâmetros e wording do prompt.
