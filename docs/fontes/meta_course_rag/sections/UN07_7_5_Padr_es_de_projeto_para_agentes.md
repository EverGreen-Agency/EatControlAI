---
unit: 7
unit_title: "Arquiteturas de agentes"
section: "7.5"
section_title: "Padrões de projeto para agentes"
source_file: "Un7_curso_meta.pdf"
source_markdown: "units/UN07_Arquiteturas_de_agentes.md"
source_pages: [9, 10, 11]
language: "pt-BR"
---

## 7.5 Padrões de projeto para agentes

Além das arquiteturas gerais, existem padrões de projeto recorrentes na construção de agentes e sistemas multiagentes. Esses padrões são como “formas de organizar agentes” para resolver problemas comuns de design, distribuindo responsabilidades entre módulos ou facilitando a cooperação. A seguir, destacamos alguns padrões úteis no contexto de agentes inteligentes:

- Planejador-Executor (Planner-Executor): esse padrão separa as funções de planejar e executar em entidades distintas. Pode ser implementado com dois componentes dentro de um mesmo agente ou até com dois agentes diferentes trabalhando em conjunto. A ideia é que o módulo planejador receba um objetivo ou tarefa complexa e produza um plano (uma sequência de ações ou subtarefas) para alcançar aquele objetivo. Em seguida, o módulo executor toma esse plano e o coloca em prática, realizando passo a passo as ações concretas no ambiente ou chamando ferramentas necessárias. Esse padrão imita a dinâmica de “pensar antes de agir”: primeiro elabora-se a estratégia, depois a executa. Na prática, isso traz melhor controle e organização, pois o planejamento pode considerar o quadro geral enquanto o executor foca na implementação detalhada de cada passo. Por exemplo, em um agente de IA que resolve problemas matemáticos complexos, um sub-agente planejador poderia decompô-los em etapas (exemplo: quebrar um problema em partes menores) e um executor (ou outro agente) realiza os cálculos em cada parte na ordem correta. Em sistemas modernos de LLM, vemos ecos desse padrão quando um agente LLM cria uma lista de passos (plano) e depois itera executando cada passo sequencialmente, possivelmente validando cada resultado antes de seguir. Esse isolamento ajuda também na verificação de qualidade, ou seja, o planejador pode simular ou checar o plano e o executor pode ter salvaguardas durante a execução, aumentando a confiabilidade do sistema.

- Supervisor-Trabalhador (Supervisor-Worker): padrão inspirado em modelos de coordenação de tarefas, onde um agente supervisor (ou gerente) distribui e coordena o trabalho de um ou mais agentes trabalhadores (ou operários). O supervisor é responsável por decompor um problema em subtarefas, atribuir essas subtarefas aos trabalhadores, monitorar o progresso e integrar os resultados. Os trabalhadores, por sua vez, são agentes mais especializados ou simplificados que executam as tarefas específicas que lhes foram designadas, reportando de volta ao supervisor. Esse padrão é especialmente útil em cenários de multiagentes, onde diferentes agentes podem ter habilidades especializadas. Por exemplo, imagine um sistema multiagente de análise financeira (antecipando nosso laboratório futuro): um agente supervisor recebe a solicitação de gerar um relatório financeiro. Ele, então, delega partes do trabalho a trabalhadores: um trabalhador para coletar dados de mercado, outro para calcular indicadores, outro para gerar visualizações. Conforme cada trabalhador conclui sua parte, o supervisor agrega os resultados e produz o output final. O benefício desse design é que ele espelha estruturas hierárquicas bem compreendidas (como um gerente e sua equipe), provendo escalabilidade e paralelismo (múltiplos trabalhadores podem operar em paralelo) e facilitando controle central (o supervisor aplica políticas e verifica qualidade). Uma variação desse padrão inclui múltiplos níveis de supervisão (supervisores de alto nível coordenando subsupervisores). Mas, a ideia básica é garantir que haja um orquestrador central mantendo o sistema alinhado aos objetivos gerais enquanto trabalhadores tratam das minúcias.

- Agente com Ferramentas/Skills: esse padrão foca em ampliar as capacidades de um agente por meio da integração com ferramentas externas ou módulos de habilidades especializadas (muitas vezes chamados de skills). Em vez de tentar programar todo o conhecimento ou funcionalidade dentro do agente central, a abordagem de ferramentas permite que o agente chame serviços ou módulos auxiliares para realizar subtarefas que estão fora de sua competência central. No contexto de agentes impulsionados por LLM, isso se popularizou como o conceito de “usar ferramentas”: por exemplo, um agente de conversação que, quando necessário, realiza uma busca na web, consulta uma calculadora, acessa um banco de dados ou invoca uma API externa para obter informações atualizadas. Cada uma dessas capacidades é uma ferramenta/skill que o agente sabe quando e como usar. A arquitetura típica aqui envolve um mecanismo de decisão dentro do agente para selecionar a ferramenta adequada para cada problema que ele não resolve sozinho. Esse padrão promove modularidade e reuso, pois você pode desenvolver novas skills de forma independente e adicioná-las à “caixa de ferramentas” do agente. Também torna o agente mais potente e versátil sem precisar treinar modelos enormes para cada habilidade – em vez disso, combina-se inteligência do agente com competências especializadas de outros sistemas. Por exemplo, no framework LangChain® (que exploraremos adiante), agentes são construídos com a habilidade de invocar tools como buscadores, sistemas de cálculo, serviços de mapas etc., sempre que a tarefa exige conhecimento externo ou operações específicas. Projetar agentes com um leque de ferramentas requer definir protocolos claros de interação (como o agente formata pedidos para a ferramenta e como incorpora as respostas), mas os resultados podem ser impressionantes. Um mesmo agente consegue responder perguntas de conhecimento geral, calcular equações matemáticas e controlar dispositivos, alternando entre skills conforme necessário.

Em resumo, os padrões de projeto, como Planejador-Executor, Supervisor-Trabalhador e uso de Ferramentas/Skills, servem como blueprints para estruturar sistemas de agentes de forma eficaz e manutenível. Eles não são mutuamente exclusivos; de fato, em um sistema complexo pode-se ver uma combinação deles. Por exemplo, um sistema multiagente pode ter um supervisor que implemente lógica planejadora, delegando a execução para trabalhadores, os quais, por sua vez, usem ferramentas especializadas. À medida que avançarmos nesse bootcamp, veremos esses conceitos se materializando em frameworks e casos práticos, ajudando-nos a projetar agentes inteligentes do conceito à produção de maneira organizada e escalável.

Saiba mais…

- Análise detalhada de arquiteturas deliberativas e reativas: https://www.sciencedirect. com/topics/computer-science/agent-architecture

- Discussão sobre arquiteturas híbridas em sistemas inteligentes: https://ieeexplore.ieee. org/document/826096

- Padrões de projeto aplicados a agentes de software: https://www.oreilly.com/library/ view/multiagent-systems/9780470519462/

Para relembrar…

- Arquiteturas de agentes definem como percepção, decisão e ação são organizadas internamente.

- Arquiteturas reflexivas respondem diretamente a estímulos, enquanto arquiteturas baseadas em modelo mantêm estado interno.

- Arquiteturas híbridas combinam deliberação e reação para maior flexibilidade.

- A escolha da arquitetura depende da complexidade do problema e dos requisitos do ambiente.
