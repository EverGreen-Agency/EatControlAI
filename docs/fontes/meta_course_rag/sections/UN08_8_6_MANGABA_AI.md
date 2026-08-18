---
unit: 8
unit_title: "Ecossistema de ferramentas - visão geral"
section: "8.6"
section_title: "MANGABA AI®"
source_file: "Un8_curso_meta(1).pdf"
source_markdown: "units/UN08_Ecossistema_de_ferramentas_vis_o_geral.md"
source_pages: [14, 15, 16, 17]
language: "pt-BR"
---

## 8.6 MANGABA AI®

Fechando nossa visão geral, destacamos o MANGABA AI®, um framework brasileiro de código aberto que também foca em equipes de agentes autônomos e fluxos de trabalho inteligentes. O MANGABA AI® se descreve como um framework Python para criar times de agentes que colaboram de forma inteligente e eficiente para resolver tarefas complexas (Mangaba AI, 2025). Ele traz alguns conceitos semelhantes aos já discutidos, mas com suas particularidades e ênfase no desenvolvimento orientado a workflows de tarefa. Características marcantes do MANGABA AI®:

- 
Arquitetura multiagente colaborativa: assim como o CrewAI®, o MANGABA AI®
permite montar equipes de agentes especializados trabalhando juntos (Mangaba AI,
2025). A ideia central é que cada agente recebe um papel (role) e todos contribuem
para uma meta comum. Por exemplo, poderíamos configurar um time com: um agente
“Pesquisador” (busca informações relevantes), um “Analista” (sintetiza os dados) e um
“Redator” (gera um relatório final). Esses agentes compartilham informações e atuam
como um time coordenado. O framework fornece a classe Team (equipe) para agrupar
agentes e gerenciar a colaboração entre eles.

- Fluxos de trabalho orientados a tarefas: uma funcionalidade importante é que podemos definir uma tarefa complexa única e passar para a equipe de agentes resolver. Com um único comando, por exemplo, team.solve(“descrição da tarefa”), o MANGABA AI® irá orquestrar automaticamente o fluxo de trabalho quebrando a tarefa entre os agentes, respeitando dependências e prioridades (Mangaba AI, 2025). Isso significa que o desenvolvedor não precisa manualmente chamar cada agente; ele confia ao framework a gestão das etapas até a conclusão. Internamente, o MANGABA AI® gerencia um grafo de dependências de subtarefas e atribui quem faz o quê, alinhado com os papéis definidos.

- Memória individual e compartilhada: o MANGABA AI® destaca suporte à memória contextual tanto individual de cada agente quanto compartilhada entre eles (Mangaba AI, 2025). Assim, agentes podem ter histórico próprio de interações e também um histórico comum da equipe. Isso facilita colaboração, pois um agente pode aproveitar descobertas feitas por outro anteriormente. Por exemplo, se o agente Pesquisador já trouxe certa informação, o Analista verá isso na memória compartilhada e não precisará refazer a busca. Essa persistência de estado entre agentes promove continuidade nas tarefas de longa duração.

- Integração com modelos avançados (Gemini® etc.): o framework vem com integração nativa a modelos de ponta, como os modelos Gemini® do Google®, além de OpenAI® e outros. Ou seja, está preparado para usar diferentes fornecedores de LLMs, permitindo aproveitar capacidades cognitivas mais avançadas se disponíveis. Isso é um diferencial, pois muitos frameworks focam somente em OpenAI®; o MANGABA AI®, sendo brasileiro e open source, busca uma abordagem mais agnóstica e extensível em relação a modelos (Mangaba AI, 2025).

- Ferramentas externas e APIs: os agentes MANGABA AI® podem facilmente usar ferramentas externas. Já vem com integração para coisas, como busca no Google®, e permite adicionar novas APIs, conforme o necessário. Assim, um agente pode consultar a web em tempo real, acessar bancos de dados ou outros serviços durante o fluxo de trabalho. Essa capacidade é crucial quando se quer que o agente tome ações no mundo ou busque dados atualizados para completar a tarefa, por exemplo, extrair cotações financeiras atuais, se essa for parte da atividade (Mangaba AI, 2025).

- Execução paralela (assíncrona): o MANGABA AI® suporta rodar sub-tarefas em paralelo, quando possível, graças a um design assíncrono. Isso melhora a eficiência e velocidade, ou seja, agentes não precisam sempre esperar um ao outro, se suas tarefas são independentes. Por exemplo, numa análise de documentos, um agente pode estar extraindo texto do documento 1 enquanto outro agente já analisa o documento 2, simultaneamente. O framework cuida da sincronização final e junção dos resultados. Esse paralelismo controlado é a chave para lidar com cargas maiores de trabalho sem multiplicar linearmente o tempo de execução (Mangaba AI, 2025).

Exemplo de uso e sintaxe: a sintaxe do MANGABA AI® é desenhada para ser simples e intuitiva (Mangaba AI, 2025). Pelo exemplo fornecido na documentação, criar um time de agentes e resolver uma tarefa pode ser feito em poucas linhas:

```text
from mangaba import Team, Agent
# Criar uma equipe de agentes
team = Team(“Equipe de Pesquisa”)
# Adicionar agentes especializados com papéis
pesquisador = Agent(“Pesquisador”, role=”Buscar informações relevantes”)
analista = Agent(“Analista”, role=”Analisar e sintetizar dados”)
redator = Agent(“Redator”, role=”Criar relatórios finais”)
team.add_agents([pesquisador, analista, redator])
# Definir uma tarefa complexa para a equipe resolver
resultado = team.solve(“Pesquise os avanços recentes em IA generativa, analise
as tendências e crie um relatório de 3 páginas.”)
print(resultado.output)
```

Nesse código hipotético (baseado no exemplo real do MANGABA AI®), vemos como é direta a criação dos agentes e a delegação da tarefa. O Team gerencia a interação: o agente Pesquisador possivelmente faz buscas iniciais, passa informações para o Analista resumir, e o Redator compila no formato pedido. O desenvolvedor não orquestra manualmente cada passo – o framework infere o fluxo necessário a partir dos roles e da natureza da tarefa. O resultado (resultado.output) seria o relatório de três páginas produzido colaborativamente. Quando considerar MANGABA AI®? Se você deseja uma solução end-to-end para automação de processos com IA, onde consegue descrever o problema de forma declarativa (como no team.solve() acima), o MANGABA AI® é muito atraente. Ele foi concebido para automatizar fluxos de trabalho que antes exigiam intervenção humana em múltiplas etapas. Setores como análise de documentos, geração de relatórios personalizados, pesquisa e desenvolvimento, automação de atendimento e outros podem tirar proveito desse modelo (Mangaba AI, 2025). Um detalhe importante: sendo um projeto relativamente jovem, convém validar a maturidade para casos de uso críticos. Porém, de acordo com os depoimentos de usuários, o MANGABA AI® já demonstrou resultados notáveis, por exemplo, empresas relatam reduções drásticas de tempo de processamento ao automatizar tarefas usando ele (Mangaba AI, 2025). Em suma, o MANGABA AI® representa a combinação das ideias de multiagentes e fluxos de trabalho automáticos. Ele incorpora memória compartilhada, uso de ferramentas, modelos avançados e paralelismo para atingir um objetivo: resolver tarefas complexas de maneira inteligente e eficiente. Para desenvolvedores, oferece uma API clara para modelar equipes e problemas, abstraindo grande parte da coordenação interna. Vale acompanhar sua evolução e considerá-lo ao projetar soluções nas quais vários agentes possam brilhar juntos.

Saiba mais…

- Visão geral prática do ecossistema moderno de LLM tools: https://www.deeplearning. ai/resources/llm-tools/

- Comparação entre frameworks de agentes baseados em LLMs: https://medium.com/@ mjbahmani/llm-agent-frameworks-comparison-2024-9d1f8c1c3c0e

- Discussão sobre abstrações para agentes inteligentes: https://arxiv.org/abs/2308.08155

Para relembrar…

- O ecossistema de ferramentas facilita o desenvolvimento de agentes baseados em LLMs.

- Ferramentas como LangFlow® e LangChain® aceleram a prototipação e integração com modelos.

- Frameworks multiagentes permitem dividir responsabilidades entre agentes especializados.

- A escolha da ferramenta impacta controle, escalabilidade e observabilidade do sistema.
