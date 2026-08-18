---
unit: 6
unit_title: "Fundamentos de sistemas de agentes"
section: "6.6"
section_title: "Sistemas multiagentes"
source_file: "Un6_curso_meta.pdf"
source_markdown: "units/UN06_Fundamentos_de_sistemas_de_agentes.md"
source_pages: [12, 13, 14]
language: "pt-BR"
---

## 6.6 Sistemas multiagentes

Até aqui focamos em um agente individual interagindo com o ambiente. Entretanto, muitos cenários do mundo real envolvem múltiplos agentes atuando simultaneamente, seja cooperando em uma tarefa comum, seja competindo pelos mesmos recursos, ou simplesmente compartilhando o espaço. Chamamos de sistemas multiagentes (Multi-agents Systems [MAS]) os ambientes e problemas em que várias entidades inteligentes autônomas coexistem e possivelmente interagem. Em um MAS, cada agente tem suas percepções e objetivos (que podem ser alinhados ou não com os dos outros agentes). As interações entre agentes podem trazer diversos comportamentos emergentes e desafios adicionais (Figura 3).

**Figura 3 - Comportamentos e Desafios de Sistemas Multiagentes**

Fonte: autoria própria. Um exemplo concreto de MAS é o trânsito urbano: imagine carros autônomos (cada carro é um agente) circulando e interagindo. Eles têm um objetivo individual (levar seus passageiros ao destino), mas também devem cooperar implicitamente para evitar acidentes e engarrafamentos, seguindo regras de tráfego. Se cada carro agisse de forma puramente egoísta, o resultado poderia ser ruim para todos (congestionamentos piores, mais acidentes). Pesquisas nessa área exploram como dotar cada agente-veículo de comportamentos que levem a um bem comum (como fluir melhor o trânsito) enquanto atendem aos objetivos individuais. Outro exemplo são robôs de resgate em desastre operando juntos: eles compartilham informações sobre áreas vasculhadas, vítimas encontradas, e dividem zonas de busca, cooperando para cobrir terreno mais rápido do que seria possível individualmente. Há também exemplos em domínios virtuais, como agentes financeiros automatizados em bolsas de valores ou bots autônomos em jogos online interagindo entre si e com humanos. Sistemas multiagentes podem trazer grande robustez e eficiência, pois vários agentes podem trabalhar em paralelo, cobrindo diferentes aspectos de uma tarefa complexa. Contudo, projetá-los exige atenção para evitar comportamentos indesejados emergentes (por exemplo, quando agentes competitivos podem entrar em ciclos destrutivos ou agentes cooperativos podem ficar presos esperando uns pelos outros). Tecnologias modernas, como as que veremos nos próximos capítulos (por exemplo, frameworks como LangChain® e Crew® para agentes baseados em large language models

[LLM]), estão facilitando a implementação de times de agentes IA que cooperam entre si para resolver problemas maiores. Veremos adiante estratégias e ferramentas para lidar com comunicação entre agentes, coordenação via serviços (como mensageria RabbitMQ® ou armazenamento de estado compartilhado via Redis®), e outras práticas de engenharia que permitem levar agentes do laboratório para aplicações de produção.
