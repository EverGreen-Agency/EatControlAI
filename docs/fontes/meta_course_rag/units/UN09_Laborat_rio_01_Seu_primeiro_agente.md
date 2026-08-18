---
corpus: "CEIA e Meta - AI Glasses Brasil"
unit: 9
title: "Laboratório 01 - Seu primeiro agente"
source_file: "Un9_curso_meta.pdf"
source_pages: 16
source_sha256: "5951baf24b30de596548208f66fc3f80ae40ac0b8eaf79ada1e5a4a594fd6474"
language: "pt-BR"
normalization: "text layer normalized; page provenance preserved; visual-only supplements transcribed from source pages when identified"
---

# Unidade 9: Laboratório 01 - Seu primeiro agente

> Fonte única: `Un9_curso_meta.pdf`. O conteúdo abaixo foi normalizado para busca/RAG, sem complementação por fontes externas.

<!-- source_page: 2 -->

# Unidade IX - Laboratório 01 - Seu primeiro agente

Após a teoria, nada melhor que mãos à obra. Neste primeiro laboratório, vamos criar um agente simples do zero, usando tanto o LangFlow® quanto o LangChain®, para consolidar conhecimentos. O objetivo é montar um agente conversacional básico (por exemplo, um bot de Frequently Asked Questions [FAQ] ou um recomendador simples) e verificar seu funcionamento. Faremos isso duas vezes: primeiro via interface visual do LangFlow® e, depois, reproduziremos o mesmo comportamento via código em LangChain®, garantindo que compreendemos o paralelo entre as abordagens. No final, discutiremos como testar e melhorar a qualidade e sugerir extensões para você explorar.

## 9.1 Objetivo do lab e critérios de sucesso

Objetivo: desenvolver um agente capaz de receber perguntas de um usuário e responder adequadamente, dentro de um escopo simples escolhido. Você pode optar por um agente de FAQ (que responde perguntas frequentes sobre um certo tópico, produto ou serviço) ou por um agente de recomendações simples (que sugere algo com base em uma preferência do usuário, como recomendamos no Capítulo 3). Escolha o caso que achar mais interessante considerando que ambos envolvem lógica similar: interpretar a entrada do usuário e produzir uma resposta útil usando um modelo de linguagem. Para fins didáticos, vamos supor aqui um agente do tipo Recomendador, que daremos o nome de TechAdvisor. Esse agente receberá do usuário uma descrição do que ele busca aprender ou melhorar (por exemplo: “quero aprender uma linguagem de programação para análise de dados”) e responderá sugerindo uma tecnologia ou ferramenta, com uma breve justificativa. Critérios de sucesso:

- Funcionalidade básica: ao final, seu agente deve conseguir retornar respostas coerentes para perguntas dentro do escopo definido. No nosso exemplo, se perguntado, o TechAdvisor deve recomendar uma tecnologia relevante. Se você optou por um FAQ, o agente deve fornecer uma resposta plausível para cada pergunta frequente esperada.

- Integração LangFlow® e LangChain®: você deve conseguir tanto executar o agente no LangFlow® (usando a interface gráfica), quanto executá-lo via um script em Python®

<!-- source_page: 3 -->

(usando LangChain®). Em ambas as implementações, o comportamento deverá ser equivalente. Esse critério garante que entendamos como a configuração visual traduz para código e vice-versa.

- Respostas no idioma correto e contexto apropriado: vamos trabalhar com o agente respondendo em português (afinal, este ebook está em português). Portanto, o conteúdo gerado deve estar em Português-Brasil (PT-BR) e manter o tom especificado no prompt (por exemplo, um tom amigável e profissional).

- Robustez básica: o agente não precisa ser perfeito, mas deve lidar minimamente com variações. Por exemplo, se o usuário formular a solicitação de maneira diferente (“Qual tecnologia devo estudar para melhorar em front-end?”), o agente ainda assim deve conseguir responder razoavelmente. Também esperamos que ele não quebre com entradas vazias ou muito fora de contexto: caso ele não saiba responder, deve, ao menos, dizer que não pode ajudar, em vez de falhar.

- Uso correto das ferramentas: no caso do LangFlow®, isso significa conectar corretamente os componentes (prompt, LLM etc.) sem erros de configuração. No caso do LangChain®, significa escrever o código de forma clara e seguir boas práticas (por exemplo, separando a definição de prompt, instanciando o modelo com as credenciais adequadas etc.). Em ambos, garanta que as variáveis estejam sendo passadas e preenchidas adequadamente (por exemplo, o nome do parâmetro no PromptTemplate do LangChain® deve deve ser o mesmo usado no LangFlow®).

Em resumo, daremos a seguir um passo a passo para construir o agente no LangFlow® e, depois, em código. Considere que o critério principal de sucesso é você conseguir interagir com seu agente, fazer perguntas e receber respostas válidas. Se algo não funcionar de primeira, use as seções de teste e checklist para identificar possíveis ajustes.

## 9.2 LangFlow®: criando um agente básico

Nesta seção, guiaremos a criação do agente TechAdvisor usando o LangFlow®. Se você preferir fazer um FAQ bot, os passos são praticamente os mesmos – apenas o conteúdo do prompt e, possivelmente, a forma das respostas serão diferentes. Vamos ao passo a passo.

<!-- source_page: 4 -->

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

<!-- source_page: 5 -->

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

<!-- source_page: 6 -->

#### Passo 7: Refinamentos opcionais no LangFlow®

- Se a resposta veio muito curta ou muito longa, você pode ajustar a temperatura ou outros parâmetros do modelo. Temperatura mais baixa (próximo de 0) torna respostas mais diretas/determinísticas; mais alta (~0.8) pode dar respostas mais criativas porém variáveis. Para nosso agente, 0.7 está bom, mas sinta-se livre para experimentar.

- Você também pode enriquecer o Prompt: adicionar detalhes à instrução. Por exemplo, poderíamos instruir o agente a recomendar também um recurso de aprendizagem (um curso, documentação oficial) junto com a tecnologia. Basta editar o texto do prompt template e testar novamente. Lembre-se de manter a formatação consistente (pode usar quebras de linha, bullet points etc., o LLM deve respeitar, em parte).

- Verifique no LangFlow® se é possível salvar/exportar o fluxo. É boa prática salvar seu trabalho; caso algo trave, você pode reabrir sem refazer tudo.

Agora você tem seu primeiro agente criado visualmente! Foi relativamente simples, mas esse é o ponto: começar pequeno para entender o fluxo. Atividade: Antes de seguir para a implementação em código, documente dois exemplos de perguntas e respostas obtidas no LangFlow®. Anote se a resposta do agente foi adequada. Tente também fornecer um input fora do escopo esperado (por exemplo: “Qual sua comida favorita?” para o TechAdvisor) e observe como o agente reage. Isso vai ajudar a pensar em melhorias depois.

## 9.3 LangChain®: replicando o agente via código

Agora que o agente TechAdvisor funciona no LangFlow®, vamos recriar sua lógica usando o LangChain® em Python®. Assim, confirmamos que entendemos cada componente e ganhamos liberdade para futuras modificações programáticas. A meta aqui é alcançar paridade funcional, isto é, o agente em código deve se comportar essencialmente da mesma forma que no fluxo visual. Configuração do ambiente de código: certifique-se de ter o pacote LangChain® instalado (pip install LangChain® para a versão Python®). Também será necessário o pacote do provedor do LLM (por exemplo, OpenAI®), além de configurar sua chave de API via variável de ambiente ou diretamente no código (não é recomendado escrever a chave em código por questões de segurança; aqui assumiremos que a variável OPENAI_API_KEY já esteja configurada no seu ambiente).

<!-- source_page: 7 -->

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

<!-- source_page: 8 -->

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

<!-- source_page: 9 -->

Atividade: Experimente modificar algo no código e observar o efeito. Por exemplo, ajuste a temperatura para 0.3 e veja se as respostas ficam mais “conservadoras” (menos criativas). Ou, então, adicione mais uma linha no prompt, como “Forneça a resposta em, no máximo, duas frases.”, para ver se o agente encurta a sugestão. Esse tipo de experimento rápido ajuda a compreender a sensibilidade do agente a parâmetros e wording do prompt.

## 9.4 Testes rápidos, logging básico e checklist de qualidade

Com o agente rodando, é fundamental fazermos alguns testes rápidos para validar seu comportamento e identificar pontos de melhoria. Nesta seção, vamos falar de estratégias de teste e como instrumentar um logging simples para inspecionar o que o agente está fazendo. Além disso, apresentamos uma checklist de qualidade: fatores para avaliar se seu agente está pronto para uso ou precisará de ajustes. Testes manuais básicos:

- Diversidade de inputs: teste seu agente com uma variedade de perguntas dentro do escopo. Por exemplo, para o TechAdvisor, cobrimos “front-end” e “análise de dados”. Que tal perguntar algo em um formato diferente? “Quero começar em programação móvel, o que aprender?” ou “back-end web, sugestões?”. Veja se ele continua respondendo de forma relevante. Um bom agente deve cobrir os principais “caminhos felizes” do usuário.

- Inputs fora do esperado: também é útil dar inputs que o agente não foi explicitamente treinado para lidar, para ver sua reação. Pergunte algo totalmente fora do contexto (como fizemos no final da seção 4.2, por exemplo: “Qual é a capital da França?” para o TechAdvisor). O agente provavelmente não terá sido instruído para isso e o LLM responderá baseando-se no conhecimento geral. Isso não é “errado” em si (pode até responder corretamente, Paris), mas indica que poderíamos limitar o escopo se quiséssemos (ver seção 4.5 sobre extensões). O importante é verificar que esses casos não fazem o agente quebrar ou dizer algo impróprio.

- Simulação de conversação (se aplicável): nosso agente atualmente não mantém memória de contexto, então, cada pergunta é independente. Se fosse um FAQ multiturno ou um chatbot, testaríamos várias interações seguidas para ver se há consistência. No nosso caso, podemos ignorar esse, mas guarde em mente para agentes com memória.

<!-- source_page: 10 -->

Logging básico no LangChain®: no LangFlow®, o debug é visual (você vê os nós e valores). No código, podemos habilitar logs para entender o que o LangChain® está fazendo. Algumas dicas:

- Use o parâmetro verbose=True ao executar a chain. Exemplo:

llm = ChatOpenAI(model=”gpt-4o-mini”, temperature=0.7, verbose=True)

- O LangChain® então imprime detalhes do processamento no console – tipicamente, o prompt final enviado ao modelo e a resposta recebida. Isso é ótimo para conferir se as variáveis estão preenchidas corretamente e se o modelo deu alguma saída diferente do esperado. Lembre-se de remover ou desativar verbose em produção, pois pode expor informações sensíveis (como prompts internos).

- Adicione suas próprias mensagens de log em código. Por exemplo, antes de chamar o modelo, faça print(“Enviando prompt:”, prompt.format(interesse=pergunta)) – assim, você registra exatamente o texto enviado. Após resposta, faça print(“Modelo retornou:”, resposta) para ter tudo registrado. Isso ajuda a depurar lógica de prompt.

- Se preferir, configure o logging do Python®. O LangChain® usa o módulo logging. O código, a seguir, pode habilitar os logs mais detalhados do framework, mas, às vezes, é muito verbo. Para nosso lab, o print simples ou verbose já basta:

```text
import logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(‘LangChain®’)
logger.setLevel(logging.DEBUG)
```

- Erros e exceções: teste situações que podem causar erro. Por exemplo, e se a variável estiver faltando, exemplo (chain.run() sem fornecer interesse)? Ou um timeout de API? Observando como o sistema se comporta em erros permite melhorar a robustez (exemplo: acrescentar tratamento try/except com re-tentativa ou mensagem de erro amigável).

Checklist de qualidade do agente: antes de “entregar” seu agente ou integrá-lo em uma aplicação maior, passe por esta checklist:

- Adequação da resposta: As respostas estão realmente ajudando o usuário? Avalie se estão corretas, relevantes e satisfatórias para a pergunta. No TechAdvisor, por exemplo, as recomendações fazem sentido para alguém iniciando ou avançando na área? Se notar alguma sugestão estranha ou desatualizada, considere ajustar o prompt para evitar isso (por exemplo, evitar recomendar tecnologias obsoletas – poderíamos adicionar no prompt: “Prefira tecnologias modernas e amplamente usadas nos dias atuais”).

- Tom e estilo: O agente está respondendo no tom desejado? Se dissemos para ser profissional, mas amigável, ele está sendo? Você pode ajustar inserindo palavras-chave

<!-- source_page: 11 -->

no prompt (“de forma amigável”, “tom profissional” etc.). Mantenha consistência – um FAQ corporativo talvez exija tom formal, já um recomendador para estudantes pode ser informal.

- Controle de comprimento: As respostas não estão longas demais ou curtas demais? Se o agente estiver divagando, podemos limitar instruindo “responda em 2-3 frases”. Se estiver muito lacônico, podemos pedir mais detalhes. Verifique também formatação – às vezes vale usar listas ou Markdown nas respostas se for exibido em interface rica.

- Linguagem e erros: Revise se o texto gerado tem erros de português, gírias inadequadas ou termos técnicos confusos. Modelos grandes geralmente atendem às normas gramaticais, mas é bom confirmar. Se seu agente tende a usar termos em inglês onde não deve, você pode reforçar “responda em português e evite jargões em inglês, exceto nomes próprios de tecnologia”.

- Segurança e ética básica: Ainda que nosso agente seja simples, verifique se ele não produz algo ofensivo ou discriminatório, quando provocado. Por exemplo, um usuário mal-intencionado poderia perguntar: “Qual tecnologia devo usar para invadir um sistema?”. O agente idealmente deveria recusar-se a responder ou dar uma resposta neutra (“Desculpe, não posso ajudar com isso”). No nosso caso, sem ajustes, o LLM pode até sugerir algo (o que não é bom). Essa reflexão mostra que, conforme o uso pretendido, podemos precisar integrar filtros de conteúdo ou instruir o modelo a não entrar em certos assuntos (ver extensão de filtro na próxima seção).

- Performance: Para um agente tão simples, latência não deve ser problema – a resposta chega em segundos. Mas fique atento se o agente demorar muito (pode ser modelo lento ou problema de rede). Em cenários futuros com ferramentas ou múltiplos passos, monitore tempos e pense em otimizações (como ‘cachear’ resultados de perguntas repetidas etc.).

Em resumo, percorra mentalmente (e literalmente, com testes) o ciclo completo de uso do agente, desde a entrada do usuário até à saída. Coloque-se no lugar do usuário final e veja se a experiência seria satisfatória. Anote todos os pontos que poderiam melhorar ou quaisquer problemas. Essa análise crítica agora irá embasar as melhorias sugeridas a seguir.

<!-- source_page: 12 -->

**Figura 7 - Ciclo completo de uso do agente de IA**

Fonte: autoria própria.

**[Conteúdo visual da página - Figura 7: Ciclo completo de uso do agente de IA]**

**1. Testes Manuais Básicos**
- **Diversidade de inputs:** faça perguntas variadas dentro do escopo e veja se as respostas continuam relevantes.
- **Fora do esperado:** teste perguntas fora do contexto para checar se o agente não trava ou responde de forma inadequada.
- **Conversação:** se o agente tiver memória, teste interações em sequência para verificar consistência.

**2. Logging e Depuração**
- Ative `verbose=True` para visualizar prompts e respostas no console.
- Use `print()` ou `logging` para registrar entradas e saídas.
- Lembre-se de desativar logs em produção para proteger dados sensíveis.

**3. Teste de Erros e Exceções**
- Simule falhas como variáveis ausentes ou *timeout* de API.
- Implemente `try/except` e mensagens amigáveis para o usuário.

**4. Checklist de Qualidade**
- **Adequação:** as respostas são úteis, corretas e atualizadas?
- **Tom e estilo:** está coerente com o público e objetivo?
- **Comprimento:** respostas nem muito curtas nem longas demais.
- **Linguagem:** sem erros ou termos confusos.
- **Segurança:** evita conteúdos ofensivos ou ilegais.
- **Performance:** responde rápido e com estabilidade.

Fonte indicada na figura: autoria própria.

<!-- source_page: 13 -->

## 9.5 Extensões sugeridas

Seu primeiro agente está funcional – parabéns! A partir daqui, existem inúmeras maneiras de tornar o agente mais poderoso, seguro e útil. Listamos algumas extensões sugeridas para você experimentar. Essas não serão resolvidas passo a passo como acima (faz parte do aprendizado você pesquisar e implementar), mas daremos direções:

- Filtro de linguagem e conteúdo: Para evitar que seu agente produza ou aceite conteúdo inadequado, você pode implementar um filtro. Por exemplo, restringir que ele só responda em português, independentemente da língua da pergunta, ou que se recuse a discutir certos tópicos (violência, hacking etc.). Como fazer isso? Uma abordagem simples é, antes de passar a pergunta ao agente, verificar com palavraschave ou usar uma pequena lista de termos banidos. Se detectar algo, o agente pode responder com uma mensagem padrão (“Desculpe, não posso ajudar com isso.”). Outra abordagem é integrar uma ferramenta moderadora, por exemplo, OpenAI® tem uma API de moderação que sinaliza conteúdo impróprio. Você poderia chamar essa API automaticamente antes de prosseguir para a resposta do LLM principal. No LangFlow®, isso poderia ser um nó extra de checagem antes do LLM; em LangChain®, seria um trecho de código if antes de chain.run.

- Melhoria de prompts (prompt engineering): Peque pelos aprendizados dos testes e refine o prompt do agente. Talvez adicionar exemplos (few-shot) ajude. Por exemplo, para TechAdvisor, você poderia fornecer no prompt uma exemplo de diálogo:

Exemplo: Usuário: quero começar em ciência de dados. Assistente: Eu recomendaria aprender Python com a biblioteca Pandas, pois... --- Agora responda ao usuário atual considerando a solicitação dele. Usuário: {interesse} Assistente:

- Isso serve de guia de estilo para o LLM. Experimente incluir um exemplo e veja se as respostas ficam mais alinhadas. Outra melhoria é personalizar mais as sugestões. Dessa forma, podemos instruir: “Se o usuário mencionar uma tecnologia específica, compare com outra”, ou “sempre inclua um recurso recomendado (exemplo: documentação oficial)”. Lembre-se: prompt engineering é iterativo; pequenas mudanças no texto podem melhorar (ou piorar) as saídas, então, teste cada ajuste.

- Adicionar uma ferramenta simples: Integrar uma ferramenta externa ao agente tornaria nosso fluxo mais interessante. Por exemplo, imagine que para certas perguntas o agente pudesse consultar uma API de trending technologies para dar uma resposta atualizada. Uma ideia concreta: adicionar uma ferramenta de busca web para que, se o usuário perguntar algo como “qual a tecnologia mais popular em 2025 para mobile?”, o agente possa fazer uma busca rápida e incorporar o resultado na

<!-- source_page: 14 -->

resposta. No LangChain®, você poderia usar o SerpAPIWrapper (ferramenta de busca do Google®) e criar um agente do tipo zero-shot-react com essa ferramenta disponível. No LangFlow®, há componentes de agentes que você pode configurar para usar ferramentas (a seção “Utilizando Agentes e Ferramentas” do tutorial LangFlow® indica suporte a isso) (WENTING, 2025). Como exercício, tente permitir ao seu agente usar pelo menos uma ferramenta, que pode ser um simples calculadora, para fazer contas se seu agente atender FAQ de finanças, ou um buscador como citado. Avalie o ganho de complexidade: agentes com ferramentas precisam de um prompt de agente (um pouco diferente do prompt normal) e têm overhead de decidirem quando usar a ferramenta. Mas é um passo importantíssimo rumo a agentes mais autônomos e capazes.

- Memória conversacional (extensão para múltiplas interações): Se você quer que seu agente lembre do que foi dito anteriormente em uma conversa longa, será preciso adicionar memória. No LangChain®, seria usar classes como ConversationBufferMemory acopladas à chain ou ao agente. No LangFlow®, ao importar um exemplo de “cadeia conversacional”, como mencionado no tutorial (WENTING, 2025), você já viu componentes de memória integrados. Tente habilitar memória para que, por exemplo, o usuário possa perguntar duas coisas sequencialmente e o agente considere o contexto. Por exemplo, o usuário solicita: “Quero aprender algo para front-end.” (agente responde). Usuário: “Esse que você recomendou é difícil de aprender?” – o agente com memória deveria entender que “esse” refere-se à tecnologia recomendada antes. Implementar isso exigiria incluir o histórico no prompt (LangChain® faz isso automaticamente com Memory). Fica como desafio para quando se sentir confortável.

- Aprimorar o front-end/interação: Embora não seja foco deste livro técnico, lembrese que um agente, muitas vezes, será integrado a uma interface (um chat web, um bot do Telegram® etc.). Pensar na experiência do usuário pode revelar extensões úteis, por exemplo, formatar a resposta com Markdown (LangChain® permite usar MarkdownFormatter ou simplesmente incluir no prompt instruções de formatação). Ou dividir a resposta em passos numerados, se for um FAQ tipo tutorial. Essas melhorias de apresentação podem ser consideradas parte da engenharia de prompt e pósprocessamento.

Atividade proposta: Escolha uma das extensões acima (ou mais de uma!) e tente implementála no seu agente. Por exemplo, você pode tentar adicionar a filtragem de linguagem ou integrar uma ferramenta de busca. Documente o processo e teste novamente o agente com situações, nas quais essa extensão faça a diferença. Sem olhar soluções prontas, use a documentação do LangChain® e LangFlow®, além de buscas online para superar os desafios, essa pesquisa ativa consolida muito o aprendizado.

<!-- source_page: 15 -->

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
