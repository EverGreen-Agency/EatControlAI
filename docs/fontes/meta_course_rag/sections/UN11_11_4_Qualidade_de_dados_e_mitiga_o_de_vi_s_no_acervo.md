---
unit: 11
unit_title: "Memória e armazenamento de dados para agentes"
section: "11.4"
section_title: "Qualidade de dados e mitigação de viés no acervo"
source_file: "Un11_curso_meta.pdf"
source_markdown: "units/UN11_Mem_ria_e_armazenamento_de_dados_para_agentes.md"
source_pages: [15, 16, 17, 18]
language: "pt-BR"
---

## 11.4 Qualidade de dados e mitigação de viés no acervo

A efetividade e a segurança de um agente estão diretamente ligadas à qualidade dos dados em sua memória. Dados desatualizados, incorretos ou enviesados podem levar o agente a respostas ruins ou preconceituosas. Por isso, é vital estabelecer processos para garantir qualidade e mitigar vieses na base de conhecimento do agente.

### 11.4.1 Qualidade de dados: correção e atualidade

Primeiramente, o conteúdo armazenado deve ser factualmente correto na medida do possível. Isso requer:

- Fontes confiáveis: alimentar a memória semântica do agente com fontes de qualidade (documentação oficial, artigos verificados) em lugar de dados aleatórios da internet reduz a chance de erro. Se a base de documentos for ruidosa (por exemplo, posts de fórum não moderados), considere revisar ou filtrar antes de inserir. Uma ideia é usar ferramentas de verificação (outra IA ou heurísticas) durante a ingestão. Por exemplo: se inserir uma afirmação contraditória à outra já existente, deve-se sinalizar para revisão humana ou marcar ambas com alguma indicação de conflito.

- Atualizações periódicas: como mencionado, é necessário revalidar e atualizar informações com frequência. Por exemplo, se a memória contém dados passíveis de mudança (preços, nomes de CEOs [Diretores Executivos], etc.), ative gatilhos para atualizá-los (um crawler ou integração que detecta mudanças e atualiza). Ao detectar contradições (por exemplo, a base diz CEO = Alice, mas sabe-se que mudou para “Bob”, corrija rapidamente para evitar respostas incorretas). Isso às vezes requer intervenção manual, mas, se a arquitetura permitir, use versionamento (marcando Alice como antigo).

- Remoção de lixo e redundâncias: dados duplicados ou irrelevantes poluem a memória. Use deduplicação semântica (discutida na seção 5.2) para eliminar entradas praticamente iguais. Remova também itens que tenham se provado inúteis. E, se houver um trecho

cuja recuperação nunca tenha ajudado em nada, ou que se tenha descoberto estar errado, não hesite em excluí-lo ou desativá-lo. Uma base enxuta e limpa é preferível a uma casa gigante cheia de entulho.

### 11.4.2 Mitigação de vieses nos embeddings e conteúdo

Modelos de linguagem e embeddings herdaram vieses dos dados de treino (exemplo: associações estereotipadas) e, caso o nosso agente armazene ou amplie esses vieses, podemos ter respostas discriminatórias ou injustas. Assim, precisamos agir em duas frentes: nos dados inseridos e no comportamento da recuperação.

- Auditoria e curadoria dos dados de entrada: antes de adicionar um lote de dados à memória, avalie se ele contém linguagem tendenciosa ou desbalanceada. Por exemplo, se alimentamos a memória com descrições de profissões e todas descrevem homens em papéis de engenharia e mulheres em papéis de enfermagem, isso vai refletir viés. Solução: balancear o dataset – adicione exemplos diversos ou ajuste o texto. Um caso real é o seguinte: embeddings de palavras que associam fortemente “engenheiro” a masculino. Mitigação: se for usar esse embedding para buscas ou raciocínio, considere treiná-lo novamente com dados balanceados ou considere usar técnicas de “desviés” (abaixo). Outra forma é: filtrar conteúdo ofensivo/inapropriado – não deixe a memória guardar insultos ou generalizações indevidas a menos que haja motivo (exemplo: o agente é moderador, então até faz sentido manter lista de palavrões para detectá-los). Ferramentas de detecção de toxicidade ou viés (IBM AIF360, por exemplo) podem sinalizar problemas nos dados (Milvus, 2025).

- Desviés nos vetores: existe pesquisa sobre remover vieses de embeddings a posteriori. Uma técnica é encontrar a direção no espaço vetorial que representa um certo viés e projetar os vetores, removendo esse componente (Milvus, 2025). Por exemplo, no caso de gênero: calcula-se o vetor diferença gênero = embedding (“homem”) - embedding (“mulher”) e então ajustam-se outros vetores para que traços irrelevantes não estejam alinhados nessa direção (Milvus, 2025). Isso pode “desenviesar” relações (por exemplo: tornar “engenheiro” equidistante de homem e mulher). Há bibliotecas para isso, entre elas a word embedding fairness evaluation (WEFE), ou utilitários do Fairlearn®. Para agentes que carregam muitos embeddings textuais, aplicar um passe de desviés nos vetores antes de indexar pode ser benéfico. Porém, tenha cuidado: isso pode degradar o desempenho de recuperar certos conceitos se não for bem executado. É um trade-off: leve se notar viés forte. Uma alternativa menos intrusiva é a augmentação contrafactual de dados. Por exemplo, para cada frase sobre um gênero, adicionar uma variante trocando o gênero. É o caso de “O enfermeiro cuidou do paciente” em que se deve guardar “A enfermeira cuidou do paciente”. Isso, durante o embedding training, ou mesmo na base do agente, ajuda a diluir associações unilaterais.

- Monitoramento contínuo de resultados: é importante observar as respostas do agente e analisar se há vieses emergindo. É preciso verificar, por exemplo, se com a pergunta “Quem são bons programadores?” o agente lista apenas homens. Trata-se de uma pista de viés. Ao identificá-las, volte à base e veja o que pode estar causando isso: se são os dados de treinamento do LLM, ou se é a memória contida. Se for memória contida (por exemplo, nos casos de uso do acervo de um perfil), considere adicionar diversidade.

- Reclassificação e fairness no pós-busca: além de consertar a base, uma camada de mitigação pode existir no momento da busca. Suponha que o usuário pergunta algo e a base retorna 5 resultados, mas todos têm um viés parecido. Podemos aplicar um rerank que privilegie diversidade ou penalize vieses. Exemplo: se for um sistema de recomendação de candidatos (embedding de currículos), garanta que os resultados incluam diversidade demográfica, se igualmente qualificados. Isso exigiria metadados e uma reordenação fairness-aware. Essa abordagem é sensível, uma vez que não se deve enviesar para corrigir viés, conforme preceitos éticos. Mas, em alguns contextos, como na busca por conteúdo, ferramentas como a heurística de diversidade do ElasticSearch® podem ser úteis.

- Política de exclusão de conteúdo problemático: defina também que, se certa informação for identificada como “não deveria estar aqui” (a exemplo de dados pessoais inadvertidamente coletados ou discurso de ódio replicado de algum documento antigo), ela deve ser removida ou isolada. Além disso, mantenha um processo de remoção, o que também entra em retenção: se um usuário pede “delete tudo que sabe sobre mim”, você deve poder localizar e deletar as informações. Mais adiante, falaremos de direitos que constam na LGPD.

### 11.4.3 Exemplo de viés e correção

Digamos que você note que, quando o agente está compondo uma resposta sobre liderança executiva, ele sempre usa “ele” como pronome padrão. Isso pode ser reflexo de viés nos dados. Dessa forma, inspecione a memória semântica: talvez a base de conhecimento tenha muitos perfis de CEO masculinos e poucos femininos, então as respostas aprendidas tendem a ser “ele”. Entre as mitigações possíveis estão: adicionar exemplos de lideranças femininas nos dados (balancear); ajustar o prompt do agente para ser neutro (por exemplo, evitar assumir gênero quando não fornecido); e, se possível, calibrar o modelo com fine-tuning leve, enfatizando neutralidade em ocupações.

Outro caso é o seguinte: embeddings de palavras podem conter proximidades indevidas (por exemplo: “terrorista” mais próximo de certa etnia). Se o seu agente usa uma busca vetorial pura, isso poderá levar a associações ofensivas. Dessa forma, aplique o desviés nesse nível ou filtre resultados por lista proibida, ajudando a evitar que, por exemplo, uma query com o nome de uma etnia retorne um documento sobre crime se isso for enviesado e indevido.

### 11.4.4 Conteúdo gerado versus conteúdo armazenado

Lembre-se de que, apesar de você limpar a base do agente, o próprio modelo de linguagem subjacente pode ter vieses de treinamento. A mitigação desta situação requer prompts de segurança e técnicas como moderação na saída. Mas quanto à memória do agente, ao menos você controla o acervo de conhecimento adicional, então faça dele um exemplo de qualidade e equidade.

### 11.4.5 Conclusão

Mantenha a memória do agente acurada, atual e inclusiva. Isso implica curadoria ativa dos dados (com possível ajuda de ferramentas automatizadas de fairness), e um loop de feedback com o qual você monitora o agente em produção, identifica problemas de dados e os corrige. Um agente é tão bom quanto sua base de conhecimento, logo, investir em qualidade e reduzir viés não é apenas uma questão ética, mas também melhora objetivamente a performance, com oferta de respostas corretas e úteis para todos os usuários.
