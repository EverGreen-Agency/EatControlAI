---
unit: 9
unit_title: "Laboratório 01 - Seu primeiro agente"
section: "9.2"
section_title: "LangFlow®: criando um agente básico"
source_file: "Un9_curso_meta.pdf"
source_markdown: "units/UN09_Laborat_rio_01_Seu_primeiro_agente.md"
source_pages: [3, 4, 5, 6]
language: "pt-BR"
---

## 9.2 LangFlow®: criando um agente básico

Nesta seção, guiaremos a criação do agente TechAdvisor usando o LangFlow®. Se você preferir fazer um FAQ bot, os passos são praticamente os mesmos – apenas o conteúdo do prompt e, possivelmente, a forma das respostas serão diferentes. Vamos ao passo a passo.

#### Passo 1: Preparação do ambiente LangFlow®

- Certifique-se de ter o LangFlow® instalado e atualizado. Se ainda não instalou, execute pip install LangFlow®. Em seguida, inicialize a interface com python -m langflow ou simplesmente langflow no terminal. Isso deve abrir o LangFlow® em seu navegador padrão (geralmente, em http://localhost:7860 ou porta semelhante).

- Tenha em mãos sua chave de API do provedor de modelo de linguagem que vai usar (por exemplo, OpenAI®). No LangFlow®, você poderá configurar isso no componente do modelo.

#### Passo 2: Criar um novo fluxo (flow)

- Na interface do LangFlow®, clique para criar um novo fluxo em branco, que será nosso agente. Dê um nome para ele, por exemplo “Agente TechAdvisor”. Você verá uma tela com uma área de trabalho vazia e uma barra lateral com componentes.

#### Passo 3: Adicionar um componente de prompt

- Na barra lateral, procure por algo como “PromptTemplate” ou “Prompt”. Arraste esse componente para a área de trabalho. Ele representa o texto base e a estrutura da mensagem que o agente usará ao chamar o LLM.

- Configure o prompt de acordo com o objetivo do agente. Clique no nó de Prompt para editar suas propriedades. Escreva um Template que inclua instruções para o agente e um espaço para a pergunta do usuário. Por exemplo, um template para TechAdvisor poderia ser [Observe a variável {interesse} – marcaremos ela como um Input Variable no componente (deve haver um campo para listar as variáveis esperadas; adicione “interesse”]. Essa variável será preenchida com a entrada real do usuário na hora da execução):

Você é um assistente que recomenda tecnologias de programação com base no interesse do usuário. Usuário: {interesse} Recomende uma tecnologia apropriada para o usuário aprender em seguida e explique brevemente o porquê.

- Salve/aplique as mudanças no componente de Prompt.

#### Passo 4: Adicionar o componente de LLM

- Na barra lateral, encontre um componente de modelo de linguagem (LLM). O LangFlow® oferece componentes pré-configurados para vários modelos (OpenAI, AI21 etc.). Arraste o componente referente ao modelo que planeja usar. Aqui usaremos por exemplo “OpenAI (GPT-4o-mini)”.

- Clique no componente LLM para configurar. Insira a chave de API no campo apropriado (o LangFlow® geralmente mascara a chave para segurança). Selecione o modelo exato, por exemplo “gpt-3.5-turbo” ou outro disponível. Configure também os parâmetros desejados, como temperatura (exemplo: 0.7 para respostas variadas). Dica: se você não tem uma chave de API, pode usar um modelo open source local se o LangFlow® suportar, mas isso foge do escopo do nosso lab. Vamos presumir o uso de um modelo via API.

#### Passo 5: Conectar Prompt e LLM em uma Chain

- Agora precisamos ligar as peças para formar o fluxo. A maioria dos fluxos em LangFlow® segue: Input do usuário → Prompt → LLM → Output.

- Procure na barra lateral os componentes PromptTemplate e ChatOpenAI.

- Arraste o PromptTemplate para a área de trabalho e configure o campo template com o texto que contém a variável {interesse}.

- Adicione o ChatOpenAI e selecione um modelo atual, como gpt-4o-mini ou gpt-4o-mini.

- Conecte a saída do PromptTemplate diretamente à entrada do ChatOpenAI (não é mais necessário usar “LLMChain”).

- Conecte também a saída do ChatOpenAI a um componente de Output para exibir a resposta.

- A entrada do usuário será solicitada automaticamente na execução, quando o LangFlow identificar a variável {interesse} no prompt.

#### Passo 6: Executar/testar no LangFlow®

- Com tudo conectado, está na hora de testar. Normalmente, o LangFlow® oferece um botão de Run ou você pode clicar no componente final (Chain) e haverá uma opção de executar. Ao rodar, ele vai solicitar o valor para a variável {interesse} (nosso prompt input).

- Insira um exemplo, por exemplo: “desenvolvimento web front-end” (imaginando que o usuário quer dicas para front-end). Execute e veja o resultado gerado. Se tudo deu certo, o output deve aparecer na interface, geralmente destacando a resposta do LLM. Por exemplo, você pode ver algo como: “Recomendo aprender React.js, pois é uma biblioteca JavaScript dominante para front-end e vai ampliar muito suas habilidades em desenvolvimento web.”.

- Teste com outros inputs também: tente algo como “análise de dados em Python” ou “mobile apps, quero evoluir” e veja se as respostas fazem sentido. Lembre-se de que o LLM não tem conhecimento específico do contexto além do prompt que fornecemos, então ele vai basear as recomendações no conhecimento geral. No caso de um FAQ, se você não integrou uma base de dados de fato, o LLM responderia do conhecimento geral, portanto, cuidado com possíveis alucinações. Em labs futuros, poderemos conectar fontes externas.

#### Passo 7: Refinamentos opcionais no LangFlow®

- Se a resposta veio muito curta ou muito longa, você pode ajustar a temperatura ou outros parâmetros do modelo. Temperatura mais baixa (próximo de 0) torna respostas mais diretas/determinísticas; mais alta (~0.8) pode dar respostas mais criativas porém variáveis. Para nosso agente, 0.7 está bom, mas sinta-se livre para experimentar.

- Você também pode enriquecer o Prompt: adicionar detalhes à instrução. Por exemplo, poderíamos instruir o agente a recomendar também um recurso de aprendizagem (um curso, documentação oficial) junto com a tecnologia. Basta editar o texto do prompt template e testar novamente. Lembre-se de manter a formatação consistente (pode usar quebras de linha, bullet points etc., o LLM deve respeitar, em parte).

- Verifique no LangFlow® se é possível salvar/exportar o fluxo. É boa prática salvar seu trabalho; caso algo trave, você pode reabrir sem refazer tudo.

Agora você tem seu primeiro agente criado visualmente! Foi relativamente simples, mas esse é o ponto: começar pequeno para entender o fluxo. Atividade: Antes de seguir para a implementação em código, documente dois exemplos de perguntas e respostas obtidas no LangFlow®. Anote se a resposta do agente foi adequada. Tente também fornecer um input fora do escopo esperado (por exemplo: “Qual sua comida favorita?” para o TechAdvisor) e observe como o agente reage. Isso vai ajudar a pensar em melhorias depois.
