---
unit: 6
unit_title: "Fundamentos de sistemas de agentes"
section: "6.7"
section_title: "Aplicações e caso de uso de agentes inteligentes"
source_file: "Un6_curso_meta.pdf"
source_markdown: "units/UN06_Fundamentos_de_sistemas_de_agentes.md"
source_pages: [14, 15, 16, 17]
language: "pt-BR"
---

## 6.7 Aplicações e caso de uso de agentes inteligentes

Para concluir esse capítulo fundamental, vale a pena revisitar alguns casos de uso onde agentes de IA já estão sendo aplicados ou vislumbrados, consolidando a teoria na prática:

**Figura 4 - Casos de uso de agentes de IA**

**[Conteúdo visual da página]**

**Sistemas autônomos físicos.** Carros autônomos, drones de entrega, robôs industriais e veículos exploratórios (como *rovers* em Marte) são exemplos de agentes que percebem o mundo via sensores (câmeras, lidar, sensores de proximidade) e agem com atuadores (motores, braços, direção). Eles devem lidar com ambientes dinâmicos e parcialmente observáveis, tomar decisões de navegação e operar de forma segura e eficiente. No caso de múltiplos robôs trabalhando juntos (por exemplo, drones realizando entrega em malha urbana), torna-se um sistema multiagente com desafios de coordenação.

**Sistemas de recomendação e personalização.** Como mencionado, plataformas de *streaming*, e-commerce e mídias sociais empregam agentes de aprendizado para personalizar conteúdo para cada usuário. Esses agentes aprendem preferências (percepção do usuário por meio de interações) e agem fornecendo recomendações ou ajustando o conteúdo exibido. São geralmente agentes de aprendizagem com objetivo implícito de maximizar engajamento ou satisfação do usuário.

**Financeiro e comércio eletrônico.** Agentes automatizados participam de mercados financeiros (algoritmos de *trading* atuando como agentes que compram/vendem ativos baseados em objetivos como maximizar retorno, com funções utilidade incluindo risco). Em leilões online e sistemas de anúncios em tempo real, agentes leiloeiros e compradores competem automaticamente. Também vemos agentes auxiliando em negociações comerciais, fazendo *matchmaking* entre oferta e demanda de forma autônoma.

Fonte: autoria própria.

Esses são apenas alguns exemplos, pois a lista de aplicações potenciais cresce rapidamente com os avanços em IA. Uma tendência atual é integrar LLMs como o motor de raciocínio de agentes, aproveitando sua capacidade de compreensão contextual e geração de planos em linguagem natural. Esse movimento tem criado agentes surpreendentemente capazes de lidar com tarefas antes difíceis para IA, como programar por conta própria, usar ferramentas de software, ou colaborar em escrita de textos complexos. Nos próximos capítulos, exploraremos ferramentas modernas (como LangChain®, AutoGen®, Crew® etc.) que abstraem muita da complexidade de construir esses agentes de alto nível, e veremos também os desafios práticos para levar agentes de IA do protótipo à produção, englobando tópicos de deploy em nuvem, orquestração de múltiplos componentes, e operações específicas para gerenciamento de modelos de linguagem em produção (LLMOps).

Com os fundamentos teóricos estabelecidos neste capítulo – entendimento de agentes, ambientes e tipos de arquiteturas –, estamos prontos para nos aprofundar nas ferramentas e técnicas que permitem implementar agentes inteligentes na prática.
