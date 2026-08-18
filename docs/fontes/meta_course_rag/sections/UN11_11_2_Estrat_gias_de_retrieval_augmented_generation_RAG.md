---
unit: 11
unit_title: "Memória e armazenamento de dados para agentes"
section: "11.2"
section_title: "Estratégias de retrieval-augmented generation (RAG)"
source_file: "Un11_curso_meta.pdf"
source_markdown: "units/UN11_Mem_ria_e_armazenamento_de_dados_para_agentes.md"
source_pages: [7, 8, 9, 10, 11]
language: "pt-BR"
---

## 11.2 Estratégias de retrieval-augmented generation (RAG)

Conforme visto, a técnica de RAG (geração aumentada por recuperação) é central para dar a agentes acesso à sua memória de longo prazo durante a geração de respostas. Vamos detalhar estratégias práticas para implementar RAG e gerenciar as restrições de janela de contexto dos modelos de linguagem.

### 11.2.1 Básico do retrieval-augmented generation (RAG)

Em um pipeline retrieval-augmented, toda vez que o agente vai gerar uma resposta, seja respondendo uma pergunta do usuário ou tomando uma decisão, ele faz previamente uma etapa de recuperação: uma ou mais consultas à sua base de conhecimento (memória vetorial, documentos, APIs) para coletar informações potencialmente relevantes (Qdrant, 2025). O resultado dessa busca (tipicamente, alguns trechos de texto ou dados) é, então, injetado no prompt/pergunta passado ao modelo gerador. Assim, o LLM recebe não apenas a pergunta do momento, mas também um contexto extra contendo conhecimento específico que ele de outra forma não teria. A receita básica de RAG é: Query -> Retrieve top-K docs -> Concatenar docs + Query -> Feed ao LLM para Answer.

### 11.2.2 Estratégias de recuperação relevantes

Escolher bem o que recuperar e quanto recuperar é crítico. Aqui estão algumas táticas:

- Recuperação por similaridade semântica direta: a mais comum – pegar a pergunta do usuário (ou estado atual da conversa), gerar um embedding dela, buscar na base vetorial os K itens mais similares e usar todos eles no prompt. K costuma ser 3 a 5. Essa estratégia é simples e funciona bem se a pergunta for clara e o acervo de conhecimento não for gigante. Deve-se ter cuidado com o tamanho: incluir K trechos pode consumir boa parte dos tokens da janela. Caso a janela seja pequena (exemplo: 2.048 tokens) e os trechos longos, talvez reduzir K ou resumir os trechos seja necessário.

- Recuperação hierárquica: se o acervo for muito grande ou composto de documentos extensos, pode-se fazer uma busca em duas fases. Exemplo: primeiro busca-se pelos N documentos mais relevantes (apenas títulos ou embeddings de documentos inteiros). Então, pega-se esses documentos top e realiza buscas mais refinadas dentro deles (por exemplo, cada documento é dividido em seções/chunks e busca-se nos chunks daquele doc que aparentemente é relevante). Isso evita desperdiçar tokens com documentos pouco relevantes. Um exemplo: ao perguntar algo que pode estar em um manual específico, primeiro descobre qual manual via busca de título, depois extrai trechos dentro dele.

- Combinação de memória episódica + semântica: se o agente tem tanto conhecimento geral quanto histórico pessoal, pode ser útil consultar ambos. Por exemplo, dado uma nova pergunta do usuário, o agente pode buscar simultaneamente na base de conhecimento global (documentos técnicos, FAQ) e na base de memórias de interações passadas daquele usuário. Então, você deve mesclar os resultados, limitando-os para não “explodir” a janela. Isso assegura que a resposta considere tanto fatos quanto contexto pessoal.

- Uso de contexto recente e extenso: uma estratégia óbvia mas relevante: sempre inclua na janela o contexto curto relevante – isso pode ser as últimas falas do chat (como vimos na memória curta) e também resumos de interações passadas se forem relevantes à pergunta atual. Por exemplo, se o usuário retoma um tópico de ontem, talvez um breve resumo do que foi discutido ontem seja concatenado ao prompt atual. Essa inclusão pode ser feita via recuperação também: a pergunta atual pode acionar uma busca na memória de episódios por “sessão de ontem com este assunto”.

- Explorar janelas de contexto maiores: modelos mais novos têm janelas enormes (exemplo: GPT-4® com 32.000 tokens, Claude® com 100.000 tokens). Com essas, uma abordagem é simplesmente permitir colocar mais informação diretamente, reduzindo a necessidade de triagem apertada. Por exemplo, com 100.000 tokens, talvez possamos inserir um documento inteiro que foi recuperado. Entretanto, pesquisas mostram que janelas maiores têm retornos decrescentes e podem até confundir o modelo se muita coisa irrelevante for embutida (Pinecone, 2025). Ou seja, continua válido usar RAG mesmo com janelas grandes, para focar apenas no que é relevante. Grandes contextos são úteis, porém, alimentá-los indiscriminadamente com “todo conhecimento” não é eficiente nem seguro (risco de vazar dados ou incorrer em custos altos de processamento) (Smith, 2025).

### 11.2.3 Gestão da janela de contexto (limites)

A janela de contexto de um LLM define quantos tokens de “memória ativa” ele pode ter. Se a entrada ultrapassar, teremos que cortar ou resumir. Estratégias para lidar com isso:

- Truncamento simples: a abordagem mais básica – cortar as partes mais antigas do histórico, quando necessário, mantendo apenas as recentes. Isso preserva as últimas instruções e conversas, mas sacrifica a memória de interações antigas. É implementado como remover do início da lista de diálogo quando o tamanho em tokens exceder o limite. Funciona bem para conversas lineares onde detalhes antigos não importam tanto. Mas pode falhar se a informação crucial estava no início.

- Sumarização de contexto antigo: em vez de simplesmente jogar fora, podese resumir blocos antigos de conversa e inseri-los em lugar do texto original. Por exemplo, se houver 50 mensagens anteriores, o agente pode gerar um resumo delas quando ficarem distantes, e manter esse resumo no contexto (atualizando conforme o necessário). Assim, preserva-se o “ponto central” do que aconteceu, ocupando menos tokens. Essa técnica requer ter confiança de que o resumo não omitiu nada importante

para a conversa futura – o que nem sempre é fácil. Uma melhoria é re-sumarizar incrementalmente: a cada vez que precisar remover detalhes, deve-se fazer um novo resumo que acumule informações.

- Segmentação por tópicos (context window segmentation): se a conversa ou tarefa muda de assunto, talvez seja possível “esquecer” contextos passados inteiros. Alguns agentes detectam tópicos e limpam a memória curta ao transicionar. Exemplo: num chat de suporte, se terminou assunto do produto A e começou outro do produto B, o agente pode arquivar a conversa do tópico A (talvez guardando episodicamente) e liberar espaço para o novo tópico sem perda. Isso evita preencher a janela com dados não relacionados à pergunta atual.

- Cache de contexto ampliado: uma ideia emergente é cache augmented generation ou memory streaming, onde parte do contexto é armazenado externamente e o modelo, se bem instruído, pode “pedir” detalhes quando precisar. Por exemplo, se a janela é de 2.000 tokens, mas a conversa tem 10.000 tokens de histórico relevante, o agente poderia carregar partes em blocos, mediante solicitação explícita, como um mecanismo de paginação de memória. Isso ainda é experimental e, muitas vezes, preferimos delegar ao desenvolvedor via RAG em vez do modelo por si só.

### 11.2.4 Quando retrieval-augmented generation (RAG) pode ser dispensado?

Com janelas ultragrandes (exemplo: modelos com 1 milhão de tokens, que estão sendo experimentados), surge a pergunta: e se eu simplesmente der todo o conhecimento no contexto? De fato, certos avanços apontam que para alguns casos, um modelo pode trabalhar só com contexto extenso em vez de buscas – isso simplifica a arquitetura, mas apresenta limites práticos. Por exemplo, alimentar 10 milhões de tokens (Llama 4 Scout®) é extremamente custoso e lento e a resposta ainda seria limitada pela capacidade do modelo de focar no trecho certo. A comunidade conclui que RAG continuará relevante mesmo com janelas gigantes (Smith, 2025), porque:

1. Dados corporativos e em constante mudança não podem ficar todos no prompt sempre: RAG busca só o necessário (minimizando vazamento e custo);

2. Trazer tudo prejudica a acurácia: LLMs perdem desempenho quando há muito ruído de contexto, preferindo menos informação, mas de qualidade;

3. Considerações de compliance e privacidade: se você injeta uma base inteira de dados confidenciais no contexto sempre, aumenta o risco de esses dados “vazarem” em respostas indevidas ou logs – melhor recuperar seletivamente conforme permissão (exem- plo: apenas dados daquele usuário/região).

Portanto, uma estratégia equilibrada é: aproveite janelas longas para enviar mais informação útil (como mais documentos relevantes ou contexto conversacional maior), mas continue usando um filtro/recuperação inteligente para decidir o que entra na janela. De fato, RAG pode se combinar a modelos de contexto estendido: em vez de top 3 documentos, talvez top 10 se couber, mas ainda top-k, não “all”. Exemplo de pipeline RAG com contexto:

1. O usuário faz uma pergunta ou o agente enfrenta um problema.

2. Com base na pergunta/estado, o agente formula uma query de busca (às vezes, é a própria pergunta do usuário, talvez enriquecida com alguns termos).

3. O agente consulta sua memória vetorial: retorna, digamos, cinco trechos.

4. O agente filtra esses trechos por metadados se aplicável (exemplo: descarta os que não correspondem ao idioma do usuário, usando metadado de idioma).

5. Se muitos trechos > janela, o agente aplica um critério de corte – por exemplo, escolhe os três com maior escore ou condensa alguns se forem do mesmo documento.

6. O agente insere esses trechos no prompt junto com a pergunta do usuário, possivelmente, prefixados por algo como “Informações úteis:” ou marcadores de citação.

7. O prompt completo (trechos + conversa recente + nova pergunta) é enviado ao LLM.

8. O LLM gera a resposta, que agora inclui conteúdo acurado, baseado nos dados fornecidos e não apenas memorizado. Talvez o agente inclua referências (se foi instruído a citar fontes etc.).

Essa é a essência do RAG. Quanto à janela de contexto, note que no Passo 5 houve uma decisão de quantos trechos caberiam. Um agente sofisticado pode ter lógica para isso: ele pode estimar quantos tokens tem a conversa atual + pergunta + trechos, e se exceder, ele reduz K ou resume parte do histórico. Outra estratégia relacionada é a de janela móvel para chat prolongado: em cada nova interação, use as últimas N mensagens (que caibam) + possivelmente um pequeno resumo do que veio antes disso. Isso junto aos dados recuperados da memória longa. Essa janela “desliza” conforme a conversa continua, sempre mantendo contexto recente. Essa técnica é usada pelo ChatGPT®, por exemplo, não se “lembra” literalmente de tudo que foi dito dezenas de interações atrás, mas usa uma janela razoável. Em resumo, as estratégias de RAG e gerenciamento de contexto andam de mãos dadas: RAG alimenta o modelo com dados externos relevantes e a gestão de contexto garante que nem o histórico local nem os dados recuperados extrapolem o limite do modelo. Um bom agente

é aquele que consegue usar a informação certa, na quantidade certa, no momento certo. E isso vem de combinar buscas semânticas inteligentes com técnicas de contorno das limitações de memória de trabalho do LLM.
