---
corpus: "CEIA e Meta - AI Glasses Brasil"
unit: 6
title: "Fundamentos de sistemas de agentes"
source_file: "Un6_curso_meta.pdf"
source_pages: 20
source_sha256: "5a24d7cdf4705e55f8fa8c340e33f2783cb53449e790cfad8c0738a12e36ffef"
language: "pt-BR"
normalization: "text layer normalized; page provenance preserved; visual-only supplements transcribed from source pages when identified"
---

# Unidade 6: Fundamentos de sistemas de agentes

> Fonte única: `Un6_curso_meta.pdf`. O conteúdo abaixo foi normalizado para busca/RAG, sem complementação por fontes externas.

<!-- source_page: 2 -->

# Unidade VI - Fundamentos de sistemas de agentes

Os agentes inteligentes estão revolucionando a forma como interagimos com a tecnologia. Em vez de programas tradicionais passivos, que respondem rigidamente a comandos, agentes de IA atuam de maneira autônoma e proativa para atingir objetivos definidos. Pense em assistentes virtuais capazes de coordenar sua agenda, ou em robôs que navegam por um ambiente dinâmico tomando decisões em tempo real. Esses sistemas são projetados para perceber e interpretar o ambiente, tomar decisões racionais e realizar tarefas sem intervenção constante do usuário. A ideia de “agentes” abrange desde algo simples, como um termostato ajustando a temperatura automaticamente, até sistemas complexos, como carros autônomos ou assistentes de pesquisa na web. Um agente de IA pode ser definido de forma simplificada como um programa ou sistema que atua em nome de um usuário, executando tarefas, tomando decisões e interagindo com o ambiente ao seu redor (Caetano, 2024). Esses agentes usam sensores para perceber o ambiente e atuadores para agir sobre ele, fechando um ciclo de percepção-ação (Caetano, 2024). Eles estão presentes em diversos setores, por exemplo, no atendimento ao cliente (chatbots inteligentes que resolvem dúvidas), na automação industrial (software que controla máquinas) e em aplicações móveis (assistentes pessoais que organizam informações). Em todos os casos, a capacidade desses agentes de tomar decisões baseadas nos dados disponíveis e aprender com experiências os torna ferramentas poderosas e cada vez mais essenciais na indústria moderna (Caetano, 2024).

## 6.1 Definição de Agente Inteligente

Em termos formais, na área de IA um agente inteligente é qualquer entidade capaz de perceber seu ambiente por meio de sensores e agir sobre esse ambiente por meio de atuadores de forma autônoma, com o objetivo de cumprir metas específicas (Wikipédia, 2024). Essa definição, introduzida por autores clássicos de IA, enfatiza dois aspectos fundamentais:

<!-- source_page: 3 -->

**Figura 1 - Aspectos principais de um agente inteligente**

Fonte: autoria própria.

Um agente opera continuamente em um ciclo de percepção-decisão-ação: ele observa o estado do mundo, decide o que fazer em seguida de acordo com seu objetivo e conhecimento e, então, atua, influenciando o ambiente. Esse ciclo se repete indefinidamente enquanto o agente estiver em operação (Correia, 2025). Vale ressaltar que um agente normalmente não precisa de intervenção humana passo-a-passo; ele tem autonomia para escolher e executar ações por conta própria, dentro dos limites de sua programação.

## 6.2 Propriedades dos agentes de inteligência artificial

Para que uma entidade computacional seja considerada um agente inteligente, espera-se que ela apresente algumas propriedades-chave em seu comportamento (Figura 2).

- Autonomia: capacidade de operar sem que um usuário tenha que guiá-la o tempo todo. O agente decide sozinho quais ações realizar para atingir seus objetivos, com base nas percepções e em seu conhecimento. Por exemplo, um robô móvel explora um ambiente e reage a obstáculos sem precisar de comandos manuais a cada instante.

- Reatividade: habilidade de responder prontamente a mudanças no ambiente. O agente reconhece eventos ou condições relevantes e age de forma adequada em tempo hábil. Por exemplo, se um sensor do robô detecta um objeto à frente, ele reage desviando ou parando imediatamente (CAETANO, 2024).

<!-- source_page: 4 -->

- Proatividade: além de reagir, agentes também devem tomar iniciativa quando necessário, antecipando-se a problemas ou oportunidades. Eles planejam ações futuras visando cumprir suas metas, em vez de apenas esperar estímulos externos. Um agente proativo pode, por exemplo, dividir uma tarefa complexa em subtarefas autônomas, ou agendar verificações periódicas para prevenir falhas futuras (ORACLE, 2024).

- Capacidade de aprendizagem (adaptabilidade): agentes inteligentes melhoram seu desempenho com o tempo, aprendendo com experiências passadas. Isso pode envolver técnicas de aprendizado de máquina ou simplesmente atualização de suas regras de decisão baseadas em feedback. Um agente de recomendação, por exemplo, refina suas sugestões conforme aprende as preferências do usuário ao longo das interações (ORACLE, 2024).

- Habilidade social: em muitos casos, um agente opera em um ambiente onde há outros agentes ou humanos. Assim, ele pode necessitar comunicar, cooperar ou competir com outras entidades. A habilidade social refere-se à capacidade do agente de interagir eficazmente – por exemplo, negociando com outros agentes, ou entendendo comandos de um usuário humano em linguagem natural.

**Figura 2 - Propriedades-chave de um agente inteligente**

Fonte: autoria própria. Essas características combinadas diferenciam agentes inteligentes de programas convencionais. Em resumo, um agente de IA busca adotar a melhor ação possível para cada situação que enfrenta, visando maximizar alguma medida de desempenho ou utilidade definida para sua tarefa (Wikipédia, 2025).

<!-- source_page: 5 -->

Essa noção de racionalidade é central em IA: um agente racional escolhe as ações que julga (com base na informação e conhecimento que possui) mais propensas a alcançar seus objetivos com sucesso (Correia, 2025). Importante destacar que racionalidade não implica perfeição – o agente não é onisciente e nem infalível. Ele apenas toma decisões otimamente em vista do que sabe até o momento, podendo ainda assim falhar, caso o ambiente seja imprevisível ou suas percepções sejam limitadas (Correia, 2025). Por exemplo, se um carro autônomo planeja a rota mais rápida com base nas informações de trânsito conhecidas, ele está agindo de forma racional; contudo, se um acidente inesperado ocorrer e bloquear a estrada, o agente terá que se adaptar, pois sua decisão inicial, embora racional, não garantiu o resultado desejado nesse cenário dinâmico (Correia, 2025).

## 6.3 Ambientes de agentes inteligentes

Todo agente está inserido em algum ambiente, que é o domínio ou contexto dentro do qual ele percebe e age. Entender as características do ambiente é fundamental, pois a natureza do ambiente influencia diretamente o projeto do agente e as técnicas necessárias para tornálo eficaz (Correia, 2025). Por exemplo, projetar um agente para jogar xadrez é bem diferente de projetar um agente para dirigir um carro em uma cidade – os desafios ambientais mudam drasticamente, assim como as abordagens de solução. Algumas dimensões clássicas para classificar ambientes em IA incluem1:

- Observabilidade do ambiente: pode ser totalmente observável ou parcialmente observável.

- Em um ambiente totalmente observável, o agente consegue obter todas as informações relevantes sobre o estado do mundo apenas pelos seus sensores. Não há “segredos” escondidos: a partir da percepção atual, o agente sabe tudo que precisa para decidir sua ação. Nesses casos, não é necessário manter estado interno de memória sobre o mundo, pois nada de importante está oculto (Correia, 2025). Exemplo: um jogo de tabuleiro como xadrez é totalmente observável – o agente (ou jogador) vê todas as peças e suas posições a cada turno.

- Em um ambiente parcialmente observável, por outro lado, o agente só tem acesso a parte das informações do estado atual. Ele pode ter sensores limitados ou o ambiente pode ser inerentemente imprevisível, exigindo que o agente faça inferências ou estimativas. Nesses ambientes, costuma ser necessário que o agente mantenha um estado interno (memória) para lembrar percepções passadas

1 Existe ainda a noção de ambiente estratégico, que é um caso de ambiente determinístico em que o resultado das ações também depende de outras entidades (por exemplo, outros agentes). Jogos de tabuleiro de dois jogadores, como xadrez, são muitas vezes considerados determinísticos do ponto de vista de cada jogador, mas estratégicos porque o “ambiente” inclui um oponente que age racionalmente para frustrar seus planos. Nesse caso, embora não haja aleatoriedade, seu sucesso depende das ações de outro agente.

<!-- source_page: 6 -->

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

<!-- source_page: 7 -->

Exemplo: o trânsito em uma cidade é dinâmico – enquanto um carro autônomo calcula a próxima manobra, os outros veículos e semáforos continuam mudando de estado.

- Ambientes semidinâmicos são aqueles onde o ambiente em si não muda com o tempo, mas o desempenho do agente pode se degradar conforme o tempo passa (Correia, 2025). Por exemplo, um jogo de xadrez com limite de tempo: o tabuleiro (ambiente) não muda sozinho, mas o agente (jogador) perde pontos ou oportunidades se demorar demais para agir, introduzindo um fator de tempo.

- Continuidade: refere-se à granularidade das percepções e ações possíveis – um ambiente pode ser discreto ou contínuo.

- Em um ambiente discreto, há um conjunto finito e bem definido de percepções e ações possíveis em cada momento (Correia, 2025). Muitos problemas clássicos de IA são formulados de modo discreto (como no xadrez, movimentos discretos e posições discretas).

- Em um ambiente contínuo, as percepções ou ações variam em um espectro contínuo, potencialmente infinito ou com resolução arbitrária (Correia, 2025). Um robô móvel no mundo real enfrenta continuidade tanto nas percepções (valores de sensores podem ser qualquer número real dentro de um range) quanto nas ações (pode acelerar em qualquer grau, virar em qualquer ângulo etc.).

- Número de agentes: o ambiente pode ser de agente único ou multiagente.

- Em um ambiente de agente único, só existe um agente inteligente relevante operando; quaisquer outros elementos do ambiente são passivos ou parte do cenário. Exemplo: um quebra-cabeça tradicional é agente único, ou seja, apenas o seu programa está tomando decisões.

- Em um ambiente multiagente, há mais de um agente inteligente presente, o que significa que eles podem interagir de várias formas (cooperação, competição, comunicação). Exemplo: um jogo de pôquer com jogadores virtuais é um ambiente multiagente, assim como um mercado eletrônico com diversos agentes negociando entre si. Ambientes multiagentes trazem desafios adicionais, pois cada agente deve considerar as possíveis ações e objetivos dos outros ao decidir o que fazer.

É importante notar que essas classificações não são mutuamente excludentes – um mesmo ambiente se encaixa em múltiplas categorias simultaneamente. Por exemplo, o “mundo real” em que um carro autônomo opera pode ser caracterizado assim: parcialmente observável, multiagente, estocástico, sequencial, dinâmico e contínuo (Correia, 2025). Ou seja, ele representa praticamente o caso mais complexo em cada dimensão, o que explica por que construir agentes para dirigir carros é tão desafiador! Já um ambiente de “palavras cruzadas” num jornal seria: completamente observável, agente único, determinístico, sequencial (ou

<!-- source_page: 8 -->

episódico se cada palavra for isolada), estático e discreto (Correia, 2025) – um cenário muito mais simples para um agente resolver. Conhecer o tipo de ambiente ajuda a determinar que tipo de agente e técnicas de IA serão mais adequados. Por exemplo, em ambientes parcialmente observáveis ou dinâmicos, o agente provavelmente precisará manter estado interno e atualizar um modelo do mundo. Em ambientes estocásticos, pode ser necessário empregar probabilidades e aprendizado para lidar com a incerteza. Em ambientes multiagentes, questões de comunicação e teoria de jogos podem entrar em cena. Ao projetar um agente, portanto, um dos primeiros passos é especificar o ambiente de tarefa com o máximo de detalhes possível, pois as propriedades ambientais guiarão a arquitetura do agente e as soluções adotadas (Correia, 2025).

## 6.4 Tipos de Agentes Inteligentes

Agora que entendemos o que são agentes e ambientes, vamos explorar os principais tipos de agentes de IA e suas arquiteturas de decisão. Diferentes tipos de agentes apresentam diferentes níveis de sofisticação, uso de memória, planejamento e capacidade de aprendizado. Na literatura clássica de IA, geralmente, classificamos agentes em cinco categorias principais, do mais simples ao mais avançado:

1. Agente de reflexo simples: é a forma mais básica de agente. Suas decisões de ação se baseiam apenas na percepção atual, sem considerar histó- rico de percepções anteriores (IBM, 2024a). Ele segue um conjunto predefinido de regras de condição-ação (do tipo “se percepção X, então ação Y”). Em outras palavras, é puramente reativo ao estímulo do momento. Esse agente não mantém memória do que já ocorreu e não tenta prever o futuro; se ele não tiver uma regra programada para alguma situação, simplesmente ficará sem resposta adequada. Vantagens: é rápido e simples, funcionando bem em tarefas bem deli- mitadas e em ambientes totalmente observáveis (onde todas as informações necessárias para decidir estão presentes na percepção do momento) (IBM, 2024a). Limitações: falha em ambientes parciais ou dinâmicos que exigem adaptação, já que não “aprende” nem “pensa além” das regras fixas. Exemplo: um termostato que liga a calefação toda vez que a temperatura cai abaixo de certo nível é um agente reflexo simples – ele “lê” a temperatura (percepção) e age ligando/desligando baseado apenas no valor atual, sem memória de leituras passadas.

2. Agente de reflexo baseado em modelo: esse tipo de agente ainda reage a estímulos, mas é um passo acima em complexidade. Ele mantém um modelo interno do mundo, ou seja, alguma forma de estado interno que representa aspectos do ambiente que não são diretamente observáveis no momento (IBM, 2024a). Esse estado interno é atualizado conforme o agente recebe novas percepções, permitindo que ele lembre informa- ções passadas ou inferidas. Assim, o agente de reflexo baseado em modelo pode lidar melhor com ambientes parcialmente observáveis do que o reflexo simples, pois não depende apenas da percepção instantânea (IBM, 2024a). No entanto, ele ainda funcio-

<!-- source_page: 9 -->

na principalmente por regras condicionais reativas, apenas usando o modelo para complementar as entradas. Exemplo: um robô aspirador de pó autônomo simplificado pode ser visto como um agente baseado em modelo. Ele tem sensores para detectar sujeira e obstáculos e mantém um mapa interno (ou registro) de áreas já limpas e obstáculos encontrados. Com isso, quando o robô retorna a um cômodo, ele “lembra” onde já passou e onde havia um móvel bloqueando, adaptando seu comportamento em vez de agir cegamente toda vez (IBM, 2024a). Ainda assim, esse robô age por regras relativamente simples (se sentir obstáculo, virar; se chão sujo, limpar etc.) e não traça planos de longo prazo – ele apenas melhora a reação imediata graças ao modelo de mundo.

3. Agente baseado em objetivos: esse agente possui, além de um modelo do mundo, a explicitação de um objetivo (ou objetivos) que ele deseja alcançar (IBM, 2024a). Em vez de apenas reagir miopicamente, o agente considera futuros possíveis e planeja sequências de ações para atingir seus objetivos. Ou seja, ele tem alguma forma de deliberação e busca por ações que o levem mais perto da meta. Isso confere mais flexibilidade e inteligência: se uma simples reação não for suficiente para atingir o objetivo, ele pode encadear ações de forma orientada a metas. Exemplo: um sistema Global Position System (GPS) de navegação pode ser visto como agente baseado em objetivos. Dado um destino (objetivo) e conhecendo o mapa (modelo do mundo), ele avalia diferentes rotas e escolhe a sequência de ruas que provavelmente levará ao destino mais rapidamente (IBM, 2024a). Se uma rota estiver congestionada, ele reconsidera e escolhe outra, ou seja, há um processo de busca/planejamento orientado pelo objetivo de minimizar o tempo de viagem. Em contraste com agentes puramente reativos, agentes baseados em objetivos podem atuar em ambientes mais amplos, pois não ficam presos apenas a regras locais: eles têm noção de um propósito a atingir e podem até ignorar reações imediatas em prol de um benefício futuro (por exemplo, entrar voluntariamente em uma rua mais lenta agora para no fim chegar mais rápido).

4. Agente baseado em utilidade: estende o conceito de objetivo introduzindo uma função de utilidade para avaliar quão desejáveis são os diferentes estados ou resultados (IBM, 2024a). Em outras palavras, enquanto um agente baseado em objetivos sabe distinguir estados “de sucesso” (que satisfazem a meta) de estados “não sucesso”, um agente de utilidade consegue atribuir um grau de preferência a diferentes realizações do objetivo. Isso é útil quando há múltiplas maneiras de alcançar a mesma meta, algumas preferíveis a outras. O agente de utilidade escolhe ações que maximizam a utilidade esperada, fazendo trade-offs se necessário. Exemplo: novamente pensando em navegação, suponha um aplicativo de rotas que considere não apenas chegar ao destino, mas também otimizar vários critérios – tempo, custo de pedágio, consumo de combustível, cenários de risco etc.. Ele pode ter uma função de utilidade que combina esses fatores (por exemplo, atribuindo um escore a cada rota possível) e então buscar a rota que maximize esse escore (IBM, 2024a). Dessa forma, mesmo que todas as rotas levem ao objetivo (destino), o agente escolhe a de maior utilidade (talvez uma um pouco mais longa em distância, porém sem pedágios e tráfego, por exemplo). Agentes baseados em utilidade são poderosos em situações complexas, pois permitem decisões

<!-- source_page: 10 -->

ótimas de acordo com preferências definidas – contudo, definir corretamente a função de utilidade pode ser difícil e calcular máximos pode ser computacionalmente custoso, se as possibilidades forem muitas.

5. Agente de aprendizagem: é o tipo mais avançado, incorporando tudo que os anteriores têm (percepção, modelo, objetivos ou utilidade) e adicionando a capacidade de aprender com a experiência (IBM, 2024a). Esse agente pode, ao longo do tempo, aprimorar seu desempenho adaptando suas estratégias com base em feedback ou conhe- cimento adquirido. Em vez de ter apenas comportamento fixo programado, ele evolui. Um agente de aprendizagem geralmente é projetado com componentes específicos para tal:

a. um elemento de aprendizado: responsável por fazer ajustes no agente (por exemplo, ajustando parâmetros das suas regras ou atualizando um modelo preditivo) conforme recebe feedback;

b. um elemento crítico: avalia o desempenho do agente em relação a algum padrão ou objetivo (informando ao elemento de aprendizado se as ações tiveram bom resultado);

c. um elemento de desempenho: é basicamente o agente “em si” escolhendo ações (pode ser baseado em objetivos ou utilidade, por exemplo);

d. um gerador de problemas: sugere novas experiências ou explorações para o agente tentar melhorar (por exemplo, tomar ações diferentes para ver se consegue um resultado melhor) (IBM, 2024a).

Esses quatro módulos formam a arquitetura conceitual de muitos agentes de aprendizagem. Exemplo: serviços de recomendação online (como sistemas de recomendação de filmes ou produtos) operam como agentes de aprendizagem. Inicialmente, podem começar com um modelo simples ou até regras fixas; mas conforme interagem com usuários (percepções) e recebem feedback implícito (usuário clicou ou não clicou, curtiu ou ignorou uma recomendação), eles ajustam seus modelos para recomendar melhor da próxima vez. Com o tempo, o agente fica mais preciso e eficaz para aquele usuário, aprendendo seus gostos. Outro exemplo são agentes de detecção de fraude financeira que melhoram seus classificadores à medida que recebem confirmação de quais sinais realmente indicam fraude ou não; com isso, aprendem novos padrões e se adaptam a golpistas que mudam de estratégia. Esse esquema de cinco tipos é cumulativo em complexidade: cada nível incorpora capacidades dos anteriores. Agentes de aprendizagem, por exemplo, normalmente também têm, no mínimo, objetivos ou utilidades, e certamente mantêm modelo de estado interno. A escolha do tipo de agente a projetar depende muito dos requisitos da tarefa e do ambiente: problemas simples podem ser resolvidos com agentes reativos puros (rápidos e de simples implementação), enquanto desafios complexos exigem agentes com planejamento sofisticado e

<!-- source_page: 11 -->

aprendizado adaptativo. Frequentemente, implementações práticas combinam características, por exemplo, um agente híbrido pode ter componentes reativos rápidos para decisões de baixo nível e componentes deliberativos para planejamento de alto nível.

## 6.5 Exemplo prático: agente de reflexo para o mundo do aspirador

Para ilustrar alguns conceitos vistos na seção anterior, vamos considerar um exemplo clássico de agente e implementar uma versão simplificada em Python®. O cenário é o mundo do aspirador de pó: imagine um ambiente com duas salas (A e B), que podem estar limpas ou sujas, e um aspirador robô que pode se mover entre as salas e limpar sujeira. Esse exemplo é frequentemente usado em IA introdutória para demonstrar agentes reflexivos e ambientes parcialmente observáveis. Suponha um agente de reflexo simples para esse mundo. Ele percebe duas coisas: sua localização atual (A ou B) e o estado daquela sala (“limpo” ou “sujo”). Com base apenas nessa percepção imediata, ele deve decidir entre três ações possíveis: mover para a esquerda, mover para a direita ou aspirar. Uma estratégia simples (e racional, nesse caso) é:

- Se a sala atual estiver suja, a ação escolhida deve ser aspirar (limpar) imediatamente;

- Se a sala atual estiver limpa, então vá para a próxima sala (por exemplo, da A vá para B, ou da B volte para a sala A) para verificar se há sujeira lá.

Podemos codificar esse comportamento em uma função Python bem simples:

```text
def agente_reflexo_simples(local, situacao):
“””Decide a ação com base na localização e situação atual (limpo/sujo).”””
if situacao == ‘sujo’:
return ‘aspirar’
elif local == ‘A’:
return ‘direita’ # sala A está limpa, mover para B
else:
return ‘esquerda’ # sala B está limpa, mover para A
# Exemplos de uso da função:
print(agente_reflexo_simples(‘A’, ‘sujo’)) # Saída esperada: ‘aspirar’ (se em A
e está sujo, limpa)
print(agente_reflexo_simples(‘A’, ‘limpo’)) # Saída esperada: ‘direita’ (se em A
e limpo, vai para B)
print(agente_reflexo_simples(‘B’, ‘limpo’)) # Saída esperada:
‘esquerda’ (se em B e limpo, volta para A)
```

<!-- source_page: 12 -->

Esse agente é “míope” no sentido de que ele não guarda memória do que já ocorreu nem planeja adiante – ele simplesmente reage: limpe se sujo, senão, vá para o outro lado. Ainda assim, essa regra simples é suficiente para eventualmente limpar as duas salas, se o mundo for estático. No entanto, podemos pensar em melhorias: e se ao chegar na sala B o agente descobri-la limpa também? No código acima, ele voltaria para A imediatamente. Ficaria então em um loop A→B→A mesmo que tudo esteja limpo (nosso código não prevê uma condição de parada). Poderíamos introduzir alguma regra adicional, como “se ambas as salas estiverem limpas, então parar”, mas note que para saber que ambas estão limpas o agente já precisaria ter memória (“lembrar” que a outra sala estava limpa quando saiu de lá). Essa exigência o tiraria da categoria de reflexo simples e o colocaria na categoria baseado em modelo (pois precisaria armazenar o fato observado anteriormente). Apesar de simples, o exemplo do aspirador ilustra bem as diferenças de arquiteturas de agentes. Um agente reflexivo simples funciona com regras fixas e sem estado interno, adequado para ambientes pequenos, totalmente observáveis e estáticos. Já um agente com modelo interno poderia, por exemplo, “memorizar” quais salas já foram limpas para evitar trabalho repetido. Um agente baseado em objetivos poderia ter como objetivo “limpar ambas as salas” e então planejar uma sequência de ações que garanta isso, talvez evitando voltar desnecessariamente a uma sala já limpa. Poderíamos até incorporar o aprendizado, fazendo o agente perceber padrões (por exemplo, se uma sala suja tende a ficar suja novamente após certo tempo, talvez por um fator externo, o agente poderia aprender a revisitar periodicamente em vez de parar completamente).

## 6.6 Sistemas multiagentes

Até aqui focamos em um agente individual interagindo com o ambiente. Entretanto, muitos cenários do mundo real envolvem múltiplos agentes atuando simultaneamente, seja cooperando em uma tarefa comum, seja competindo pelos mesmos recursos, ou simplesmente compartilhando o espaço. Chamamos de sistemas multiagentes (Multi-agents Systems [MAS]) os ambientes e problemas em que várias entidades inteligentes autônomas coexistem e possivelmente interagem. Em um MAS, cada agente tem suas percepções e objetivos (que podem ser alinhados ou não com os dos outros agentes). As interações entre agentes podem trazer diversos comportamentos emergentes e desafios adicionais (Figura 3).

<!-- source_page: 13 -->

**Figura 3 - Comportamentos e Desafios de Sistemas Multiagentes**

Fonte: autoria própria. Um exemplo concreto de MAS é o trânsito urbano: imagine carros autônomos (cada carro é um agente) circulando e interagindo. Eles têm um objetivo individual (levar seus passageiros ao destino), mas também devem cooperar implicitamente para evitar acidentes e engarrafamentos, seguindo regras de tráfego. Se cada carro agisse de forma puramente egoísta, o resultado poderia ser ruim para todos (congestionamentos piores, mais acidentes). Pesquisas nessa área exploram como dotar cada agente-veículo de comportamentos que levem a um bem comum (como fluir melhor o trânsito) enquanto atendem aos objetivos individuais. Outro exemplo são robôs de resgate em desastre operando juntos: eles compartilham informações sobre áreas vasculhadas, vítimas encontradas, e dividem zonas de busca, cooperando para cobrir terreno mais rápido do que seria possível individualmente. Há também exemplos em domínios virtuais, como agentes financeiros automatizados em bolsas de valores ou bots autônomos em jogos online interagindo entre si e com humanos. Sistemas multiagentes podem trazer grande robustez e eficiência, pois vários agentes podem trabalhar em paralelo, cobrindo diferentes aspectos de uma tarefa complexa. Contudo, projetá-los exige atenção para evitar comportamentos indesejados emergentes (por exemplo, quando agentes competitivos podem entrar em ciclos destrutivos ou agentes cooperativos podem ficar presos esperando uns pelos outros). Tecnologias modernas, como as que veremos nos próximos capítulos (por exemplo, frameworks como LangChain® e Crew® para agentes baseados em large language models

<!-- source_page: 14 -->

[LLM]), estão facilitando a implementação de times de agentes IA que cooperam entre si para resolver problemas maiores. Veremos adiante estratégias e ferramentas para lidar com comunicação entre agentes, coordenação via serviços (como mensageria RabbitMQ® ou armazenamento de estado compartilhado via Redis®), e outras práticas de engenharia que permitem levar agentes do laboratório para aplicações de produção.

## 6.7 Aplicações e caso de uso de agentes inteligentes

Para concluir esse capítulo fundamental, vale a pena revisitar alguns casos de uso onde agentes de IA já estão sendo aplicados ou vislumbrados, consolidando a teoria na prática:

**Figura 4 - Casos de uso de agentes de IA**

<!-- source_page: 15 -->

**[Conteúdo visual da página]**

**Sistemas autônomos físicos.** Carros autônomos, drones de entrega, robôs industriais e veículos exploratórios (como *rovers* em Marte) são exemplos de agentes que percebem o mundo via sensores (câmeras, lidar, sensores de proximidade) e agem com atuadores (motores, braços, direção). Eles devem lidar com ambientes dinâmicos e parcialmente observáveis, tomar decisões de navegação e operar de forma segura e eficiente. No caso de múltiplos robôs trabalhando juntos (por exemplo, drones realizando entrega em malha urbana), torna-se um sistema multiagente com desafios de coordenação.

**Sistemas de recomendação e personalização.** Como mencionado, plataformas de *streaming*, e-commerce e mídias sociais empregam agentes de aprendizado para personalizar conteúdo para cada usuário. Esses agentes aprendem preferências (percepção do usuário por meio de interações) e agem fornecendo recomendações ou ajustando o conteúdo exibido. São geralmente agentes de aprendizagem com objetivo implícito de maximizar engajamento ou satisfação do usuário.

**Financeiro e comércio eletrônico.** Agentes automatizados participam de mercados financeiros (algoritmos de *trading* atuando como agentes que compram/vendem ativos baseados em objetivos como maximizar retorno, com funções utilidade incluindo risco). Em leilões online e sistemas de anúncios em tempo real, agentes leiloeiros e compradores competem automaticamente. Também vemos agentes auxiliando em negociações comerciais, fazendo *matchmaking* entre oferta e demanda de forma autônoma.

<!-- source_page: 16 -->

Fonte: autoria própria.

Esses são apenas alguns exemplos, pois a lista de aplicações potenciais cresce rapidamente com os avanços em IA. Uma tendência atual é integrar LLMs como o motor de raciocínio de agentes, aproveitando sua capacidade de compreensão contextual e geração de planos em linguagem natural. Esse movimento tem criado agentes surpreendentemente capazes de lidar com tarefas antes difíceis para IA, como programar por conta própria, usar ferramentas de software, ou colaborar em escrita de textos complexos. Nos próximos capítulos, exploraremos ferramentas modernas (como LangChain®, AutoGen®, Crew® etc.) que abstraem muita da complexidade de construir esses agentes de alto nível, e veremos também os desafios práticos para levar agentes de IA do protótipo à produção, englobando tópicos de deploy em nuvem, orquestração de múltiplos componentes, e operações específicas para gerenciamento de modelos de linguagem em produção (LLMOps).

<!-- source_page: 17 -->

Com os fundamentos teóricos estabelecidos neste capítulo – entendimento de agentes, ambientes e tipos de arquiteturas –, estamos prontos para nos aprofundar nas ferramentas e técnicas que permitem implementar agentes inteligentes na prática.

## 6.8 Exercícios práticos

1. Identificando sensores e atuadores: Pense em um agente de IA presente no seu dia a dia (pode ser um assistente virtual, um robô de limpeza, um aplicativo inteligente no celular). Descreva quais seriam os sensores e atuadores desse agente e que tipo de percepções ele obtém do ambiente. Por exemplo, no caso de um drone autônomo de entrega, liste seus sensores (câmera, GPS etc.) e atuadores (motores, garras).

2. Classificação do ambiente: Para cada um dos cenários, a seguir, classifique o ambiente segundo as propriedades discutidas (observável/parcelar, determinístico/esto- cástico etc.). Explique brevemente suas classificações.

a. Um jogo de xadrez entre dois jogadores IA.

b. Um aplicativo de previsão do tempo que recomenda se você deve levar guardachuva.

c. Um carro autônomo em tráfego urbano.

d. Um agente que joga pôquer online contra outros jogadores.

3. Projeto de regras simples: Considere o problema de um agente controlador de semáforos em uma cidade. Ele percebe o fluxo de carros em cada direção (exemplo: alto, médio, baixo) e deve decidir quando abrir ou fechar o sinal para otimizar o tráfego. Proponha um conjunto de regras de condição-ação estilo agente reflexo simples que esse controlador poderia usar. Por exemplo: “Se fluxo na direção Norte-Sul está alto e tempo aberto já > 1 minuto, então fechar Norte-Sul e abrir Leste-Oeste”.

4. Racionalidade vs. onisciência: Suponha que um agente meteorológico dá conselhos de agricultura a fazendeiros baseado na previsão do tempo. Num certo dia, ele aconselhou plantar sementes pois não haveria chuva forte; porém, ocorreu uma tempestade inesperada que prejudicou a plantação. Explique se o agente agiu de forma racional apesar do mau resultado, discutindo a diferença entre agir racionalmente e acertar sempre.

5. Extensão do agente aspirador: No exemplo prático do aspirador, o agente reflexo simples não tinha memória e poderia ficar repetindo ações mesmo com tudo limpo. Descreva como você implementaria um agente baseado em modelo para o aspirador. Que informação ele armazenaria em seu estado interno? Escreva em pseudocódigo (ou em Python®, se preferir) uma versão aprimorada agente_reflexo_modelo(percepção,estado_interno) que use memória para evitar repetir ações desnecessárias.

<!-- source_page: 18 -->

6. Objetivos e utilidade: Considere um agente planejador de rotas de viagem que tem o objetivo de levar uma pessoa do ponto A ao B. Liste pelo menos três fatores que poderiam compor a função de utilidade desse agente ao decidir a melhor rota (por exemplo, tempo de viagem, custo, paisagem bonita etc.). Como o agente equilibraria esses fatores para escolher a rota ótima?

Resolva os exercícios pensando nos conceitos apresentados. Esses problemas têm como objetivo reforçar seu entendimento sobre agentes inteligentes, sua interação com ambientes e as diferentes abordagens de projeto. Boa prática!

Saiba mais…

- O modelo PEAS ajuda a descrever o “trabalho” do agente (métrica de desempenho, ambiente, atuadores e sensores). https://aima.cs.berkeley.edu/4th-ed/pdfs/newchap02. pdf

- A ideia de racionalidade limitada explica por que agentes (e humanos) muitas vezes buscam uma solução “boa o bastante”, não a perfeita. https://www.scielo.br/j/rep/a/ CWfwPPVWKvLrndfxR9vYFHL/?format=html&lang=en

- POMDP é um modelo clássico para decisões sequenciais quando o agente não consegue observar totalmente o estado do ambiente. https://en.wikipedia.org/wiki/ Partially_observable_Markov_decision_process

- Sistemas multiagentes estudam como vários agentes interagem (cooperação/ competição) para resolver tarefas complexas. https://www.ibm.com/think/topics/ multiagent-system

- A diferença entre função do agente e programa do agente ajuda a separar “o que o agente deveria fazer” de “como ele foi implementado”. https://aima.cs.berkeley.edu/4thed/pdfs/newchap02.pdf

<!-- source_page: 19 -->

Para relembrar…

- Um agente percebe com sensores e age com atuadores para cumprir um objetivo.

- As propriedades-chave são autonomia, reatividade, proatividade, aprendizagem e habilidade social.

- O ambiente pode ser total/parcialmente observável, determinístico/estocástico, episódico/sequencial, estático/dinâmico, discreto/contínuo e mono/multiagente.

- Os tipos de agentes evoluem de reflexo simples até aprendizagem, passando por modelo, objetivos e utilidade.

- Ser racional não é “acertar sempre”, e sim escolher a melhor ação possível com a informação disponível.
