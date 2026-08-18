---
unit: 6
unit_title: "Fundamentos de sistemas de agentes"
section: "6.8"
section_title: "Exercícios práticos"
source_file: "Un6_curso_meta.pdf"
source_markdown: "units/UN06_Fundamentos_de_sistemas_de_agentes.md"
source_pages: [17, 18, 19]
language: "pt-BR"
---

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

6. Objetivos e utilidade: Considere um agente planejador de rotas de viagem que tem o objetivo de levar uma pessoa do ponto A ao B. Liste pelo menos três fatores que poderiam compor a função de utilidade desse agente ao decidir a melhor rota (por exemplo, tempo de viagem, custo, paisagem bonita etc.). Como o agente equilibraria esses fatores para escolher a rota ótima?

Resolva os exercícios pensando nos conceitos apresentados. Esses problemas têm como objetivo reforçar seu entendimento sobre agentes inteligentes, sua interação com ambientes e as diferentes abordagens de projeto. Boa prática!

Saiba mais…

- O modelo PEAS ajuda a descrever o “trabalho” do agente (métrica de desempenho, ambiente, atuadores e sensores). https://aima.cs.berkeley.edu/4th-ed/pdfs/newchap02. pdf

- A ideia de racionalidade limitada explica por que agentes (e humanos) muitas vezes buscam uma solução “boa o bastante”, não a perfeita. https://www.scielo.br/j/rep/a/ CWfwPPVWKvLrndfxR9vYFHL/?format=html&lang=en

- POMDP é um modelo clássico para decisões sequenciais quando o agente não consegue observar totalmente o estado do ambiente. https://en.wikipedia.org/wiki/ Partially_observable_Markov_decision_process

- Sistemas multiagentes estudam como vários agentes interagem (cooperação/ competição) para resolver tarefas complexas. https://www.ibm.com/think/topics/ multiagent-system

- A diferença entre função do agente e programa do agente ajuda a separar “o que o agente deveria fazer” de “como ele foi implementado”. https://aima.cs.berkeley.edu/4thed/pdfs/newchap02.pdf

Para relembrar…

- Um agente percebe com sensores e age com atuadores para cumprir um objetivo.

- As propriedades-chave são autonomia, reatividade, proatividade, aprendizagem e habilidade social.

- O ambiente pode ser total/parcialmente observável, determinístico/estocástico, episódico/sequencial, estático/dinâmico, discreto/contínuo e mono/multiagente.

- Os tipos de agentes evoluem de reflexo simples até aprendizagem, passando por modelo, objetivos e utilidade.

- Ser racional não é “acertar sempre”, e sim escolher a melhor ação possível com a informação disponível.
