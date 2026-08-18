---
unit: 7
unit_title: "Arquiteturas de agentes"
section: "7.3"
section_title: "Arquitetura híbrida (deliberação + reação)"
source_file: "Un7_curso_meta.pdf"
source_markdown: "units/UN07_Arquiteturas_de_agentes.md"
source_pages: [5, 6, 7]
language: "pt-BR"
---

## 7.3 Arquitetura híbrida (deliberação + reação)

As arquiteturas híbridas surgiram para combinar o melhor dos dois mundos das abordagens reativas e deliberativas. Um agente híbrido integra tanto componentes reativos (reflexivos) quanto componentes deliberativos (baseados em modelo e planejamento), dentro de um mesmo sistema (Ataide, 2025). O objetivo é aproveitar a velocidade de resposta das reações simples e a inteligência estratégica do planejamento de alto nível. Como funciona? Tipicamente, uma arquitetura híbrida é organizada em camadas ou módulos separados (Ataide, 2025). Por exemplo, uma implementação comum é a arquitetura em camadas:

- Camada reativa: responsável por processar percepções e acionar respostas imediatas a estímulos urgentes. Aqui residem comportamentos de baixo nível, muito rápidos, que garantem que o agente reaja a situações críticas (por exemplo, frear um carro autônomo ao detectar um pedestre inesperadamente).

- Camada deliberativa: responsável pelo raciocínio de alto nível, planejamento de ações e tomada de decisões baseada em objetivos de longo prazo. Essa camada usa um modelo do mundo, considera metas desejadas e calcula planos ou sequência de passos para atingi-las de forma ótima.

- Camada de coordenação: muitos projetos híbridos inserem uma camada (ou mecanismo) de mediação entre as duas acima, para orquestrar a interação. Essa camada decide, por exemplo, quando um comportamento reativo deve ter prioridade máxima ou quando seguir o plano deliberativo. Ela gerencia conflitos entre as recomendações de cada camada e assegura que o agente opere de forma coerente (ATAIDE, 2025).

**Figura 5 - Camadas de uma arquitetura híbrida**

Fonte: autoria própria. Em suma, a deliberação fornece direção estratégica, enquanto a reatividade garante adaptação rápida. Como resultado, agentes híbridos podem tanto planejar quando há tempo e necessidade, quanto reagir prontamente a eventos inesperados. Muitos sistemas de agentes complexos são construídos nesse formato para obter robustez: o agente não fica paralisado “pensando”, quando precisa agir rápido, e não age de forma míope, quando a situação exige reflexão. Exemplos: considere um assistente pessoal inteligente capaz de ajudar em tarefas diárias. Em muitos casos, ele precisa ser reativo. Por exemplo, se o usuário diz “desfaça a última ação”, o assistente imediatamente cancela a última operação sem precisar planejar algo complexo. Ao mesmo tempo, para tarefas mais complicadas (“organize minha agenda deste mês e marque reuniões considerando meus horários livres”), o agente precisará deliberar, acessar dados de calendário, cruzar informações e talvez planejar uma sequência de ações (consultar agenda, encontrar vagas, enviar convites). Uma arquitetura híbrida permitiria ao assistente alternar entre essas capacidades conforme necessário. Já no campo da robótica, imagine um carro autônomo: ele deve reagir em milissegundos a um obstáculo na pista (comportamento reativo de emergência), porém, conta também com um módulo deliberativo de alto nível traçando a rota

ótima até o destino, obedecendo a regras de trânsito e otimizando tempo e segurança. Esse carro autônomo híbrido consegue atravessar distâncias longas de forma segura e eficiente, algo inviável se fosse apenas reativo (ficaria preso localmente) ou apenas deliberativo (seria lento demais para evitar acidentes) (LAB-8, [s.d.]). Desafios: projetar arquiteturas híbridas traz desafios adicionais, pois é necessário definir claramente como as camadas interagem. A coordenação entre comportamentos reativos e deliberativos pode ficar complexa, exigindo estratégias como subsumption (onde comportamentos simples têm precedência) ou supervisão deliberativa (onde o planejador monitora e ajusta reações). Apesar da complexidade de implementação, as arquiteturas híbridas são consideradas estado da arte para muitos sistemas de IA que demandam tanto prontidão quanto planejamento (Ataide, 2025). Tecnologia em foco: a arquitetura Belief-Desire-Intention (BDI), bastante usada em agentes cognitivos, é um exemplo de arquitetura híbrida/deliberativa. Nela, um agente mantém crenças sobre o mundo, desejos (objetivos) e intenções (planos comprometidos), equilibrando reação e deliberação de forma estruturada.
