**AI GLASSES**  
**BRASIL 2026**

**Matriz de ideias, evidências e decisão estratégica**

Documento interno para seleção da proposta e aprofundamento da inscrição

# **1\. Regras do jogo e filtros**

## **Filtro oficial 1 — inscrição e seleção para o Ideathon**

| Critério | Pontos | O que precisamos provar |
| :---- | ----- | :---- |
| Originalidade | **30** | Combinação diferenciada de problema, público, fluxo e tecnologia. |
| Viabilidade inicial | **30** | Plausibilidade técnica e recorte que caiba no programa. |
| Aderência ao programa/hardware | **20** | Uso real de câmera/microfone e áudio; não apenas um app transportado para óculos. |
| Área de atuação e impacto | **20** | Relevância do público e consequência concreta do problema. |

*Fonte: edital, seção 11.1 \[E1\].*

## **Filtro oficial 2 — seleção das cinco equipes finalistas**

| Critério | Pontos | O que precisamos provar |
| :---- | ----- | :---- |
| Viabilidade técnica | **30** | Arquitetura madura e núcleo implementável. |
| Aderência ao toolkit/hardware | **20** | Integração efetiva e experiência coerente com óculos sem display. |
| Impacto da solução | **30** | Transformação mensurável para o público-alvo. |
| Ética, privacidade e segurança | **20** | Uso responsável de dados, limitações e humano no circuito. |

*Fonte: edital, seção 11.2 \[E1\].*

## **Filtros internos adicionais — 0 a 10**

| Filtro | Pergunta de decisão |
| :---- | :---- |
| **Problema estrutural** | Persistiria com pessoas competentes, organizadas e responsáveis? |
| **Essencialidade dos óculos** | O valor cai muito ao trocar os óculos por um celular? |
| **Força da demonstração** | A transformação fica clara em até dois minutos? |
| **Exequibilidade** | O núcleo pode funcionar no hackathon presencial? |
| **Diferenciação** | Há distância suficiente de recursos nativos e soluções existentes? |
| **Acesso à validação** | Conseguimos entrevistar usuários, especialistas ou compradores rapidamente? |
| **Ligação com a equipe** | A equipe tem história, experiência, dados ou acesso que dê credibilidade? |
| **Continuidade comercial** | Há comprador, recorrência e expansão depois do evento? |
| **Segurança/baixo risco** | É possível demonstrar sem prometer diagnóstico, conformidade ou segurança indevida? |

## **Teste de essencialidade dos óculos**

Uma ideia é forte para este programa quando combina pelo menos quatro condições: atividade em movimento; duas mãos necessárias; ponto de vista da pessoa contém informação; resposta imediata; desviar para uma tela prejudica segurança ou qualidade; tarefa repetitiva; ambiente variável; câmera fixa insuficiente; registro manual interrompe o trabalho; visão \+ voz \+ áudio são centrais.

## **Premissas técnicas para a final**

* É prudente chegar com arquitetura, backend, prompts, mocks e componentes preparados, deixando para o presencial a integração e validação com o hardware. O edital não esclarece formalmente o limite de código prévio

# **2, Eat Control Vision — copiloto alimentar para GLP-1 e restrições**

| TrilhaBem-Estar | ODS relacionadasODS 3 – Saúde e Bem-Estar |  |
| :---- | :---- | :---- |
| Filtro 193/100 | Filtro 290/100 | Índice interno89.1/100 |

Aplicativo móvel conectado aos óculos Meta que apoia decisões alimentares antes do consumo. O usuário olha para um prato, rótulo, cardápio ou produto; o sistema interpreta a informação, cruza-a com restrições e plano individual e responde por áudio. A proposta atende usuários em tratamento com GLP-1 e pessoas com restrições alimentares, como intolerância à lactose, alergia à proteína do leite, doença celíaca ou planos específicos definidos por profissionais \[I1\].

## **Contexto e problemática**

Pessoas em tratamento com GLP-1 e pessoas com restrições alimentares recebem listas, recomendações e planos, mas precisam interpretá-los sozinhas diante de cada prato, rótulo ou cardápio. O problema permanece mesmo para usuários responsáveis: ingredientes podem estar ocultos, nomes técnicos dificultam a leitura, a composição de pratos é incerta e a decisão precisa ocorrer em segundos, normalmente fora da consulta. No caso de GLP-1, tamanho da porção, preparo e tolerância individual também variam; em restrições, a diferença entre ingrediente declarado, possível contaminação e ausência de informação é crítica.

## **Solução simplificada**

O Eat Control funciona como uma camada de decisão contextual. Para pratos, usa IA multimodal e perguntas de confirmação; para rótulos, OCR e parser de ingredientes; para produtos embalados, código de barras e bases alimentares. Em seguida, um motor próprio cruza o resultado com as restrições e o plano do usuário. Regras críticas — por exemplo, identificar “contém leite” explicitamente declarado — são determinísticas e não dependem exclusivamente de IA generativa. Quando faltarem dados, o sistema não certifica segurança: informa a incerteza e orienta a confirmação com o estabelecimento ou profissional \[I1\].

* Prato ou cardápio: interpretação probabilística, perguntas por voz e indicação explícita de confiança.  
* Rótulo: leitura dos ingredientes e alertas determinísticos baseados no perfil cadastrado.  
* Produto embalado: consulta por código de barras para complementar ingredientes e dados nutricionais.  
* Plano individual: regras e limites configurados pelo usuário e, idealmente, por profissional responsável.

## **Por que os óculos importam**

Média-alta, mas não absoluta. Em supermercados, restaurantes, buffets e durante o preparo, os óculos permitem olhar, perguntar e continuar com as mãos livres, sem abrir câmera, aplicativo ou teclado. A experiência também é mais discreta e aproxima a orientação do instante real da decisão. O celular continua sendo um substituto plausível, portanto a proposta precisa explorar fluidez, voz, múltiplas consultas rápidas e contexto em primeira pessoa — não apenas “tirar uma foto do prato”.

Um recorte de cenário usado para mostrar a importância dos óculos (e não o celular) seria de no self-service. O usuário não ficaria sacando o celular para tirar foto de tudo. Ele bastaria apontar e perguntar se poderia ou não comer algo.

Ativo da equipe: um dos integrantes informou que o time já possui os óculos. Isso permite testar enquadramento, latência, áudio e ergonomia antes da etapa presencial, reduzindo risco de execução e gerando evidências reais para a inscrição. É necessário confirmar a compatibilidade do modelo disponível com o Meta Wearables Device Access Toolkit \[I1\].

## **Evidências e dados**

* A Anvisa informa que a tirzepatida retarda o esvaziamento gástrico e registra eventos gastrointestinais relevantes; isso sustenta o problema de aplicar orientações alimentares individualizadas durante o tratamento, sem transformar o aplicativo em prescritor \[R21\].  
* A Anvisa também alertou para riscos do uso indevido de agonistas de GLP-1. A narrativa da solução deve reforçar acompanhamento responsável e não facilitar automedicação ou ajuste de dose \[R22\].  
* Reconhecimento de ingredientes e estimativa de porções por imagem permanecem desafios, sobretudo em pratos mistos. Por isso, a arquitetura com OCR, código de barras, regras determinísticas, perguntas de confirmação e níveis de confiança é mais defensável do que depender apenas de visão generativa \[R23\] \[I1\].

## **Já existe? Concorrência e diferenciação**

Já existem scanners de rótulos e códigos de barras, diários alimentares, aplicativos de alergias/intolerâncias e ferramentas de acompanhamento de GLP-1. A proposta não deve alegar categoria inédita. A diferenciação está na combinação: interação vestível em primeira pessoa, três modos de entrada (prato, rótulo e produto), plano individual, regras críticas determinísticas, declaração de incerteza e resposta por áudio antes do consumo. O principal substituto continua sendo o celular, e o principal risco competitivo é que apps existentes adicionem uma interface para wearables.

## **Arquitetura e fluxo recomendado**

Óculos capturam imagem e voz → aplicativo identifica prato, rótulo ou produto → IA/OCR interpreta → bases complementam dados → motor determinístico cruza com dieta e restrições → resposta curta volta por áudio aos óculos \[I1\].

* Óculos: Meta Wearables Device Access Toolkit.  
* Aplicativo companion: Kotlin/Android ou Swift/iOS.  
* Bases: Open Food Facts para produtos, USDA FoodData Central e TBCA para composição alimentar.

## **MVP demonstrável no hackathon**

O MVP deve priorizar confiabilidade e uma jornada completa, não tentar implementar todos os públicos e modos de análise no mesmo dia.

1. Conectar os óculos ao aplicativo e validar captura de imagem, comando de voz e retorno por áudio.  
2. Ler um rótulo e detectar, por regra determinística, uma declaração como “contém leite”.  
3. Consultar um produto por código de barras e cruzá-lo com o perfil fictício do usuário.  
4. Responder por áudio com três estados claros: compatível, incompatível ou informação insuficiente.  
5. Demonstrar que a IA multimodal analisa um prato, pergunta sobre ingrediente oculto e informa o nível de confiança.  
6. Opcional: registrar a decisão/refeição e gerar saldo ou relatório diário; não é requisito do núcleo da demo.

## **Riscos e perguntas em aberto**

O maior risco não é a IA “errar uma caloria”, mas produzir falsa segurança diante de alergia, doença celíaca, contaminação cruzada ou ingrediente não visível. O design deve ser reliability-first.

* Nunca declarar um prato “seguro” quando a composição não puder ser comprovada; usar “não foi possível confirmar”.  
* Separar avisos determinísticos de rótulos das inferências probabilísticas de pratos e cardápios.  
* Para alergias graves e doença celíaca, ausência de ingrediente no OCR não comprova ausência de traços ou contaminação cruzada.  
* SIBO e planos de GLP-1 devem ser configuráveis a partir de orientação profissional, sem recomendações universais ou ajuste de medicamento.  
* Minimizar armazenamento de imagens e explicar claramente tratamento de dados alimentares e de saúde.  
* Validar linguagem, regras e cenários com nutricionista/médico e usuários antes da submissão final.

## **Recorte recomendado para a inscrição**

Apresentar a visão ampla como copiloto para restrições e planos alimentares, mas demonstrar um recorte muito controlado: leitura de rótulo \+ código de barras \+ uma análise de prato com incerteza explícita. O caso “contém leite” é visualmente claro para a banca; GLP-1 entra como personalização do plano e continuidade comercial. A solução não deve prometer cobrir celiacos, alergias, SIBO e GLP-1 com o mesmo grau de certeza desde o primeiro MVP.

# **3\. Fontes e referências**

Pesquisa pública realizada até 1º de agosto de 2026\. “Não localizado” significa apenas que a pesquisa rápida não encontrou um produto exato; não é prova de inexistência. Para a ideia escolhida, recomenda-se uma busca dedicada de produtos, artigos, patentes e normas.

**\[E1\] Edital – Programa AI Glasses Brasil 2026\.** Arquivo fornecido pelo usuário. Critérios oficiais: pp. 10–11; checkpoints: p. 8; etapas e cronograma: pp. 2–6 e 11–15.

**\[R1\] Meta — Our AI Wearables Are “Changing the Game” for Disabled People.** [https://about.fb.com/news/2026/05/meta-ai-wearables-changing-the-game-for-disabled-people/](https://about.fb.com/news/2026/05/meta-ai-wearables-changing-the-game-for-disabled-people/)

**\[R2\] World Health Organization — Disability.** [https://www.who.int/news-room/fact-sheets/detail/disability-and-health](https://www.who.int/news-room/fact-sheets/detail/disability-and-health)

**\[R3\] World Health Organization — Blindness and vision impairment.** [https://www.who.int/news-room/fact-sheets/detail/blindness-and-visual-impairment](https://www.who.int/news-room/fact-sheets/detail/blindness-and-visual-impairment)

**\[R4\] Be My Eyes — The app that describes the world for you.** [https://www.bemyeyes.com/bme-app/](https://www.bemyeyes.com/bme-app/)

**\[R5\] RNIB — Shaving for blind and partially sighted people.** [https://www.rnib.org.uk/living-with-sight-loss/independent-living/shaving/](https://www.rnib.org.uk/living-with-sight-loss/independent-living/shaving/)

**\[R6\] Google — Lookout, assisted vision.** [https://play.google.com/store/apps/details?id=com.google.android.apps.accessibility.reveal](https://play.google.com/store/apps/details?id=com.google.android.apps.accessibility.reveal)

**\[R7\] World Health Organization — Dengue fact sheet.** [https://www.who.int/news-room/fact-sheets/detail/dengue-and-severe-dengue](https://www.who.int/news-room/fact-sheets/detail/dengue-and-severe-dengue)

**\[R8\] Ministério da Saúde — alerta sobre possível aumento de arboviroses em 2026\.** [https://www.gov.br/saude/pt-br/assuntos/noticias-ms/2026/julho/ministerio-da-saude-alerta-estados-e-municipios-sobre-possivel-aumento-de-arboviroses-no-pais-devido-ao-el-nino](https://www.gov.br/saude/pt-br/assuntos/noticias-ms/2026/julho/ministerio-da-saude-alerta-estados-e-municipios-sobre-possivel-aumento-de-arboviroses-no-pais-devido-ao-el-nino)

**\[R9\] Scientific Reports — High-resolution mapping of urban Aedes aegypti immature abundance.** [https://www.nature.com/articles/s41598-024-67914-w](https://www.nature.com/articles/s41598-024-67914-w)

**\[R10\] Corpo de Bombeiros Militar de Goiás — Vistoria de Habite-se.** [https://www.bombeiros.go.gov.br/vistoria-habitese](https://www.bombeiros.go.gov.br/vistoria-habitese)

**\[R11\] Fraunhofer — Visual Fire Safety Inspection Framework Using Computer Vision Algorithms.** [https://publica.fraunhofer.de/entities/publication/7c640a5c-870c-4538-aad7-e18288fc95bd](https://publica.fraunhofer.de/entities/publication/7c640a5c-870c-4538-aad7-e18288fc95bd)

**\[R12\] TeamViewer Frontline — Maintenance and inspection.** [https://www.teamviewer.com/en/products/frontline/solutions/maintenance-inspection/](https://www.teamviewer.com/en/products/frontline/solutions/maintenance-inspection/)

**\[R13\] Starzyńska et al. — Computer vision-based analysis of buildings and built environments: systematic review.** [https://arxiv.org/abs/2208.00881](https://arxiv.org/abs/2208.00881)

**\[R14\] Cushley et al. — Street audit for barriers faced by people with visual impairment.** [https://doi.org/10.1080/23748834.2025.2496015](https://doi.org/10.1080/23748834.2025.2496015)

**\[R15\] OmniPath — Multi-Modal Agentic Framework for Auditing Wheelchair Accessibility.** [https://arxiv.org/abs/2606.24129](https://arxiv.org/abs/2606.24129)

**\[R16\] Patel et al. — Trends in Workplace Wearable Technologies and Connected-Worker Solutions.** [https://arxiv.org/abs/2205.11740](https://arxiv.org/abs/2205.11740)

**\[R17\] UNEP — Panorama Global do Manejo de Resíduos em 2024\.** [https://www.unep.org/pt-br/resources/panorama-global-do-manejo-de-residuos-em-2024](https://www.unep.org/pt-br/resources/panorama-global-do-manejo-de-residuos-em-2024)

**\[R18\] Scan your Trash — Participatory data capture for plastic sorting.** [https://www.sciencedirect.com/science/article/pii/S221282712600661X](https://www.sciencedirect.com/science/article/pii/S221282712600661X)

**\[R19\] Sensors — Artificial Vision Systems for Fruit Inspection and Classification: systematic review.** [https://www.mdpi.com/1424-8220/25/5/1524](https://www.mdpi.com/1424-8220/25/5/1524)

**\[R20\] Journal of Agricultural and Food Chemistry — Hyperspectral Imaging and Deep Learning for Fruit and Vegetable Inspection.** [https://pubs.acs.org/doi/10.1021/acs.jafc.4c11492](https://pubs.acs.org/doi/10.1021/acs.jafc.4c11492)

**\[R21\] Anvisa — Mounjaro (tirzepatida): nova indicação e riscos.** [https://www.gov.br/anvisa/pt-br/assuntos/medicamentos/novos-medicamentos-e-indicacoes/mounjaro-r-tirzepatida-nova-indicacao](https://www.gov.br/anvisa/pt-br/assuntos/medicamentos/novos-medicamentos-e-indicacoes/mounjaro-r-tirzepatida-nova-indicacao)

**\[R22\] Anvisa — alerta para risco associado ao uso indevido de canetas emagrecedoras.** [https://www.gov.br/anvisa/pt-br/assuntos/noticias-anvisa/2026/anvisa-emite-alerta-para-risco-de-pancreatite-aguda-associada-ao-uso-indevido-de-canetas-emagrecedoras](https://www.gov.br/anvisa/pt-br/assuntos/noticias-anvisa/2026/anvisa-emite-alerta-para-risco-de-pancreatite-aguda-associada-ao-uso-indevido-de-canetas-emagrecedoras)

**\[R23\] Food Portion Estimation: From Pixels to Calories.** [https://arxiv.org/abs/2602.05078](https://arxiv.org/abs/2602.05078)

**\[R24\] World Health Organization — Deafness and hearing loss.** [https://www.who.int/news-room/fact-sheets/detail/deafness-and-hearing-loss](https://www.who.int/news-room/fact-sheets/detail/deafness-and-hearing-loss)

**\[R25\] Hand Talk — Aplicativo tradutor para Libras.** [https://www.handtalk.me/br/aplicativo/](https://www.handtalk.me/br/aplicativo/)

**\[R26\] Alyami et al. — Reviewing 25 years of continuous sign language recognition research.** [https://doi.org/10.1016/j.ipm.2024.103774](https://doi.org/10.1016/j.ipm.2024.103774)

**\[R27\] AI-Based Approaches for Brazilian Sign Language Recognition — systematic review.** [https://doi.org/10.5753/webmedia.2025.16082](https://doi.org/10.5753/webmedia.2025.16082)

\[I1\] Detalhamento conceitual, técnico e de stack do Eat Control Vision, fornecido por integrante da equipe em 1º de agosto de 2026\. Inclui público, fluxo, arquitetura, prioridades de MVP e informação de que a equipe possui os óculos Meta.

Nota metodológica: As notas refletem a leitura estratégica da equipe e não representam pontuação oficial. Informações \[I1\] são propostas e ativos declarados pela equipe, ainda sujeitos a prova técnica. Dados clínicos, normativos, alimentares e de acessibilidade precisam ser validados por especialistas e usuários antes da submissão.