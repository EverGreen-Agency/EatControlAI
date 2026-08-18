---
unit: 7
unit_title: "Arquiteturas de agentes"
section: "7.4"
section_title: "Como escolher a arquitetura para cada problema"
source_file: "Un7_curso_meta.pdf"
source_markdown: "units/UN07_Arquiteturas_de_agentes.md"
source_pages: [7, 8, 9]
language: "pt-BR"
---

## 7.4 Como escolher a arquitetura para cada problema

Diante das diferentes arquiteturas de agentes – reflexiva, baseada em modelo, híbrida (e variações deliberativas) – surge a pergunta: como decidir qual arquitetura adotar em um projeto específico? Não há uma única resposta, mas alguns critérios fundamentais ajudam a guiar essa escolha. Em geral, deve-se considerar a complexidade do problema e do ambiente, a disponibilidade de dados e conhecimento sobre o domínio, os requisitos de latência (tempo de resposta) e os custos envolvidos, tanto de implementação quanto de processamento. Podemos pensar nesses fatores como dimensões de uma matriz de decisão que aponta para a arquitetura mais indicada:

- 
Complexidade e dinamismo do ambiente: problemas simples e estáticos tendem a
ser bem atendidos por agentes reativos, que são suficientes para tarefas básicas em
ambientes previsíveis (Ataide, 2025). Por exemplo, se todas as condições relevantes
podem ser mapeadas em algumas regras fixas e o ambiente não muda de forma
inesperada, a simplicidade do agente reflexivo é uma vantagem (menos chances de
erro e fácil de implementar). Já cenários complexos ou muito dinâmicos – onde o estado
do mundo muda constantemente, há muitas variáveis ou contingências – geralmente
exigem memória ou planejamento. Nesses casos, arquiteturas baseadas em modelo (ou
até agentes de aprendizado) serão mais eficazes em lidar com a complexidade (Ataide,
2025). Em suma, quanto maior a incerteza e variedade de situações no ambiente, mais
o pêndulo se afasta do puramente reativo em direção a arquiteturas com estado interno
ou híbridas.

- Disponibilidade de dados e conhecimento prévio: esse fator está relacionado à quanta informação o agente pode ter ou adquirir sobre o mundo em que opera. Se há dados abundantes para modelar o ambiente ou um conhecimento de domínio robusto (por exemplo, regras do negócio, modelos preditivos), vale a pena investir em uma arquitetura baseada em modelo ou deliberativa, pois o agente poderá usar esse conhecimento para tomar decisões melhores. Por outro lado, se o domínio é muito desconhecido, imprevisível ou difícil de modelar, talvez uma abordagem mais simples e reativa seja preferível inicialmente – pelo menos, até reunir dados suficientes para construir modelos confiáveis. Além disso, quando a observabilidade do ambiente é baixa (o agente não consegue enxergar todo o estado a partir de percepções diretas), é praticamente mandatória a presença de um estado interno para estimar as partes ocultas; caso contrário, um agente reflexivo ficaria “cego” para informações não percebidas naquele exato momento.

- Requisitos de latência e responsividade: quanto tempo o agente pode gastar “pensando” antes de agir? Se o requisito da aplicação demanda respostas em tempo real ou próximo de tempo real, arquiteturas pesadamente deliberativas podem falhar em atender às expectativas. Agentes reflexivos ou camadas reativas dentro de híbridos são ideais quando a latência precisa ser mínima, pois eles tomam decisões quase instantâneas. Já se a aplicação tolera um certo atraso para obter uma resposta mais otimizada ou inteligente, então arquiteturas com planejamento são viáveis. Considere um agente financeiro dando recomendações de investimento: é aceitável ele levar alguns segundos analisando dados antes de responder (favorável a uma abordagem deliberativa). Mas em controle de um robô em equilíbrio, cada milissegundo conta (favorável a uma reação direta). Um compromisso comum é adotar híbridos, onde tarefas críticas usam reação imediata, enquanto tarefas estratégicas rodam em paralelo numa camada deliberativa mais lenta. Assim, avalie cuidadosamente as metas de desempenho (Service Level Objective [SLO]/Service Level Agreement [SLA]) ao escolher a arquitetura.

- Custos de desenvolvimento e computação: a infraestrutura disponível e o custo de processamento também influenciam. Agentes reflexivos são tipicamente mais baratos de implementar e executar porque requerem menos código sofisticado, menos memória e central processing unit (CPU), e nenhuma necessidade de bancos de dados complexos ou modelos de machine learning (ML). Já agentes com modelo interno, planejamento ou aprendizado podem exigir mais recursos computacionais (por exemplo, para manter um mapa, rodar algoritmos de busca ou inferência, consultas à base vetorial, chamadas a modelos de linguagem etc.). Se o projeto tem restrições orçamentárias ou de hardware, talvez seja prudente iniciar com uma arquitetura mais simples e expandir conforme necessário. Da mesma forma, do ponto de vista de tempo de desenvolvimento, arquiteturas híbridas ou deliberativas implicam projetar mais componentes (módulo de memória, planejador etc.), o que aumenta a complexidade do software. Um produto viável mínimo (Minimum Viable Product [MVP]) de agente, muitas vezes, começa reativo e evolui para incorporar memória e planejamento em iterações futuras, caso os requisitos assim demandem.

Em resumo, a escolha da arquitetura deve equilibrar esses fatores de forma a atender aos objetivos do projeto com a solução mais simples possível, porém, sem sacrificar funcionalidades essenciais. Uma dica prática é começar avaliando o problema: se ele se assemelha a um mapeamento direto de entradas para saídas conhecido (como regras de negócio claras ou reflexos fixos), vá pelo caminho reflexivo. Se o problema envolve objetivos a alcançar, histórico relevante ou predições sobre o futuro, prepare-se para incluir modelo interno ou técnicas de planejamento. E se tanto respostas rápidas quanto raciocínio aprofundado forem cruciais, a via híbrida provavelmente será a mais adequada. Lembre-se também de que essas arquiteturas não são muros intransponíveis. É possível iniciar com um agente simples e iterativamente adicionar camadas de memória ou módulos planejadores conforme o sistema cresce em escopo e entendimento.
