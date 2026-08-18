---
unit: 6
unit_title: "Fundamentos de sistemas de agentes"
section: "6.3"
section_title: "Ambientes de agentes inteligentes"
source_file: "Un6_curso_meta.pdf"
source_markdown: "units/UN06_Fundamentos_de_sistemas_de_agentes.md"
source_pages: [5, 6, 7, 8]
language: "pt-BR"
---

## 6.3 Ambientes de agentes inteligentes

Todo agente está inserido em algum ambiente, que é o domínio ou contexto dentro do qual ele percebe e age. Entender as características do ambiente é fundamental, pois a natureza do ambiente influencia diretamente o projeto do agente e as técnicas necessárias para tornálo eficaz (Correia, 2025). Por exemplo, projetar um agente para jogar xadrez é bem diferente de projetar um agente para dirigir um carro em uma cidade – os desafios ambientais mudam drasticamente, assim como as abordagens de solução. Algumas dimensões clássicas para classificar ambientes em IA incluem1:

- Observabilidade do ambiente: pode ser totalmente observável ou parcialmente observável.

- Em um ambiente totalmente observável, o agente consegue obter todas as informações relevantes sobre o estado do mundo apenas pelos seus sensores. Não há “segredos” escondidos: a partir da percepção atual, o agente sabe tudo que precisa para decidir sua ação. Nesses casos, não é necessário manter estado interno de memória sobre o mundo, pois nada de importante está oculto (Correia, 2025). Exemplo: um jogo de tabuleiro como xadrez é totalmente observável – o agente (ou jogador) vê todas as peças e suas posições a cada turno.

- Em um ambiente parcialmente observável, por outro lado, o agente só tem acesso a parte das informações do estado atual. Ele pode ter sensores limitados ou o ambiente pode ser inerentemente imprevisível, exigindo que o agente faça inferências ou estimativas. Nesses ambientes, costuma ser necessário que o agente mantenha um estado interno (memória) para lembrar percepções passadas

1 Existe ainda a noção de ambiente estratégico, que é um caso de ambiente determinístico em que o resultado das ações também depende de outras entidades (por exemplo, outros agentes). Jogos de tabuleiro de dois jogadores, como xadrez, são muitas vezes considerados determinísticos do ponto de vista de cada jogador, mas estratégicos porque o “ambiente” inclui um oponente que age racionalmente para frustrar seus planos. Nesse caso, embora não haja aleatoriedade, seu sucesso depende das ações de outro agente.

ou estimar aspectos não diretamente observados (Correia, 2025). Exemplo: para um carro autônomo, o ambiente urbano é parcialmente observável – ele nunca tem certeza absoluta do que está além da próxima curva ou escondido por outros carros, então deve inferir e reagir com base em informações incompletas.

- Determinismo do ambiente: pode ser determinístico ou estocástico (não determinístico).

- 
Um ambiente determinístico é aquele em que o próximo estado é totalmente
previsível a partir do estado atual e da ação executada pelo agente (Correia,
2025). Em outras palavras, não há aleatoriedade envolvida: dadas as condições
atuais e uma ação, o resultado é sempre o mesmo. Exemplo: um quebra-cabeça
de oito peças (8-puzzle) ou um problema matemático são essencialmente
determinísticos, ou seja, as ações levam a resultados conhecidos e fixos.

- Um ambiente estocástico (ou não determinístico) é aquele onde há incerteza nos resultados. A mesma ação em situações aparentemente idênticas pode levar a resultados diferentes devido a elementos aleatórios ou fora do controle do agente (BRAINLY, 2025). Exemplo: dirigir no trânsito real é estocástico – mesmo que você (agente) dirija da mesma forma, fatores externos aleatórios (comportamento de outros motoristas, clima, eventos imprevistos) podem mudar o resultado das suas ações.

- Episodicidade: o ambiente pode ser episódico ou sequencial.

- Em um ambiente episódico, as experiências do agente acontecem em episódios isolados, sem que haja dependência entre eles (Correia, 2025). Cada percepçãoação é como um episódio independente: uma vez que a ação é tomada, o próximo episódio começa do zero, sem memória do anterior. Exemplo: um sistema de análise de imagens que examina fotos uma a uma para dizer se há um objeto presente – cada imagem é tratada como um caso separado (episódio).

- Em um ambiente sequencial, os estados e experiências têm dependências ao longo do tempo (Correia, 2025). A decisão atual do agente pode influenciar situações futuras, e vice-versa – há uma corrente histórica. Exemplo: dirigir um carro ou jogar um jogo de longa duração é sequencial, pois cada ação influencia o estado subsequente do ambiente (a manobra que você fez agora muda as condições para as próximas decisões).

- Dinamicidade: refere-se a se o ambiente é estático ou dinâmico.

- Um ambiente estático permanece inalterado enquanto o agente decide suas ações (Correia, 2025). Isso significa que, se o agente “parar no tempo” para pensar, nada de relevante muda no mundo durante essa deliberação. Exemplo: um problema de cálculo matemático em papel é estático; o mundo não muda enquanto você pensa na solução.

- Um ambiente dinâmico pode mudar continuamente ao longo do tempo, mesmo enquanto o agente está escolhendo ou executando uma ação (BRAINLY, 2025). O agente deve rastrear essas mudanças e talvez reagir a elas em tempo real.

Exemplo: o trânsito em uma cidade é dinâmico – enquanto um carro autônomo calcula a próxima manobra, os outros veículos e semáforos continuam mudando de estado.

- Ambientes semidinâmicos são aqueles onde o ambiente em si não muda com o tempo, mas o desempenho do agente pode se degradar conforme o tempo passa (Correia, 2025). Por exemplo, um jogo de xadrez com limite de tempo: o tabuleiro (ambiente) não muda sozinho, mas o agente (jogador) perde pontos ou oportunidades se demorar demais para agir, introduzindo um fator de tempo.

- Continuidade: refere-se à granularidade das percepções e ações possíveis – um ambiente pode ser discreto ou contínuo.

- Em um ambiente discreto, há um conjunto finito e bem definido de percepções e ações possíveis em cada momento (Correia, 2025). Muitos problemas clássicos de IA são formulados de modo discreto (como no xadrez, movimentos discretos e posições discretas).

- Em um ambiente contínuo, as percepções ou ações variam em um espectro contínuo, potencialmente infinito ou com resolução arbitrária (Correia, 2025). Um robô móvel no mundo real enfrenta continuidade tanto nas percepções (valores de sensores podem ser qualquer número real dentro de um range) quanto nas ações (pode acelerar em qualquer grau, virar em qualquer ângulo etc.).

- Número de agentes: o ambiente pode ser de agente único ou multiagente.

- Em um ambiente de agente único, só existe um agente inteligente relevante operando; quaisquer outros elementos do ambiente são passivos ou parte do cenário. Exemplo: um quebra-cabeça tradicional é agente único, ou seja, apenas o seu programa está tomando decisões.

- Em um ambiente multiagente, há mais de um agente inteligente presente, o que significa que eles podem interagir de várias formas (cooperação, competição, comunicação). Exemplo: um jogo de pôquer com jogadores virtuais é um ambiente multiagente, assim como um mercado eletrônico com diversos agentes negociando entre si. Ambientes multiagentes trazem desafios adicionais, pois cada agente deve considerar as possíveis ações e objetivos dos outros ao decidir o que fazer.

É importante notar que essas classificações não são mutuamente excludentes – um mesmo ambiente se encaixa em múltiplas categorias simultaneamente. Por exemplo, o “mundo real” em que um carro autônomo opera pode ser caracterizado assim: parcialmente observável, multiagente, estocástico, sequencial, dinâmico e contínuo (Correia, 2025). Ou seja, ele representa praticamente o caso mais complexo em cada dimensão, o que explica por que construir agentes para dirigir carros é tão desafiador! Já um ambiente de “palavras cruzadas” num jornal seria: completamente observável, agente único, determinístico, sequencial (ou

episódico se cada palavra for isolada), estático e discreto (Correia, 2025) – um cenário muito mais simples para um agente resolver. Conhecer o tipo de ambiente ajuda a determinar que tipo de agente e técnicas de IA serão mais adequados. Por exemplo, em ambientes parcialmente observáveis ou dinâmicos, o agente provavelmente precisará manter estado interno e atualizar um modelo do mundo. Em ambientes estocásticos, pode ser necessário empregar probabilidades e aprendizado para lidar com a incerteza. Em ambientes multiagentes, questões de comunicação e teoria de jogos podem entrar em cena. Ao projetar um agente, portanto, um dos primeiros passos é especificar o ambiente de tarefa com o máximo de detalhes possível, pois as propriedades ambientais guiarão a arquitetura do agente e as soluções adotadas (Correia, 2025).
