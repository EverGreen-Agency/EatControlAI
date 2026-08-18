---
corpus: "CEIA e Meta - AI Glasses Brasil"
unit: 11
title: "Memória e armazenamento de dados para agentes"
source_file: "Un11_curso_meta.pdf"
source_pages: 25
source_sha256: "a795cc9a6c872388ac5999b7b04b06f274a4d8a979d4eb79739056a3ff1acca3"
language: "pt-BR"
normalization: "text layer normalized; page provenance preserved; visual-only supplements transcribed from source pages when identified"
---

# Unidade 11: Memória e armazenamento de dados para agentes

> Fonte única: `Un11_curso_meta.pdf`. O conteúdo abaixo foi normalizado para busca/RAG, sem complementação por fontes externas.

<!-- source_page: 2 -->

# Unidade XI - Memória e armazenamento de dados para agentes

## 11.1 Tipos de memória

Agentes inteligentes inspiram-se em conceitos de memória humana para organizar e recuperar informações. Podemos categorizar a memória de um agente em alguns tipos principais, com papéis distintos.

### 11.1.1 Memória de curta duração (contexto de trabalho)

É a informação retida temporariamente enquanto é necessária para a tarefa imediata. No caso de um agente baseado em LLM, a memória de curto prazo corresponde basicamente ao conteúdo atualmente carregado na janela de contexto do modelo – por exemplo, as últimas mensagens na conversa ou as instruções atuais que o agente está seguindo. É similar à memória de trabalho humana, que dura segundos ou minutos. Essa memória tem capacidade limitada (exemplo: 4.000 tokens, 8.000 tokens etc., dependendo do modelo) e é volátil: se a conversa ou tarefa muda, ou se exceder o limite, informações antigas são esquecidas (a não ser que tenham sido movidas para a memória longa). Um agente utiliza a memória de curto prazo para manter coerência local – lembrar o que o usuário acabou de dizer, os detalhes que precisa para o próximo passo, o estado atual do raciocínio. Após completar a tarefa ou quando o contexto “estoura” o limite, essa memória é basicamente descartada ou condensada. Em implementações, manter a curta duração costuma ser tão simples quanto armazenar as últimas N interações em uma lista e enviá-las junto com a nova pergunta do usuário para o LLM (Belgusen, 2025). Por exemplo, em um chatbot, as últimas poucas trocas de mensagem ficam na janela; as mais antigas são removidas ou resumidas quando o limite se aproxima. Assim, a memória de curto prazo garante respostas contextualmente consistentes em conversas ou sessões curtas, mas não resolve necessidades de lembrança após longos intervalos.

<!-- source_page: 3 -->

### 11.1.2 Memória de longa duração (vetorial ou persistente)

Refere-se ao “conhecimento duradouro” que o agente pode consultar mesmo após muito tempo ou em qualquer contexto. Geralmente, é implementada por meio de uma base de conhecimento externa – pode ser uma base de dados vetorial com embeddings (daí chamamos de memória vetorial), um banco de dados relacional com fatos ou até arquivos de texto/JSON. O essencial é que essa memória persiste entre sessões e pode guardar volume grande de informações aprendidas ou fornecidas ao agente (Belgusen, 2025). Por exemplo, tudo o que um assistente pessoal “aprende” sobre você (seu nome, aniversário, preferências) deve ir para a memória de longo prazo, pois deve estar disponível amanhã, semana que vem ou mês que vem. Diferentemente da memória curta, que é passiva (o modelo “lembra” porque o texto ainda está no prompt), a memória longa requer um mecanismo ativo de recuperação. Tipicamente, busca por similaridade: o agente, ao receber uma nova pergunta, busca na memória vetorial por trechos relevantes para trazer para a memória de curto prazo (janela) antes de responder. Assim, a memória longa aumenta a capacidade do agente para além do que cabe em contexto. Ela também pode ser atualizada incrementalmente: novos fatos entram ao longo do tempo. Alguns subtipos dessa memória:

- Memória de conhecimento geral ou semântica: fatos e conceitos que o agente sabe sobre o mundo, domínios específicos etc.. Pode vir de ingestão de documentos, manuais, wikis (por isso, muitas vezes, materializada em embeddings vetoriais indexando esses documentos). Exemplo: um agente médico pode ter na memória semântica informações sobre doenças e sintomas (Belgusen, 2025).

- Memória especializada ou de habilidades: por vezes, chamada de memória procedimental, armazena como fazer algo (algoritmos, funções). Em agentes, isso geralmente está no próprio código ou em modelos de ferramenta (por exemplo, a habilidade de calcular ou navegar em certa API). Alguns frameworks se diferem, mas, para nosso contexto, podemos considerar isso parte de “conhecimento de longo prazo” do agente também.

### 11.1.3 Memória episódica

É a capacidade de lembrar de eventos específicos vividos pelo agente, com contexto de tempo e detalhes específicos daquela ocorrência (Belgusen, 2025). Para humanos, memória episódica é lembrar de um jantar na semana passada; para um agente de IA, seria lembrar de interações passadas ou experiências específicas que não são simplesmente fatos gerais. Por exemplo, “Ontem o usuário João reclamou de um bug e eu (agente) dei a volta errada no suporte, ele ficou frustrado” – isso é um episódio vivido. Em termos de implementação, memória episódica frequentemente se traduz em log de conversas e ações que o agente pode

<!-- source_page: 4 -->

consultar. Se o agente tem múltiplas sessões com um usuário, armazenar cada sessão (ou um resumo dela) com um timestamp é dotá-lo de memória episódica: ele pode depois recuperar “o que conversamos na sessão do dia tal”. Essa memória tem natureza autobiográfica, ou seja, ajuda o agente a “aprender” com a experiência. Por exemplo, um agente que tentou um procedimento e falhou poderia armazenar essa experiência para não repetir o erro (reforçando ou adaptando estratégias). Em aplicações, a memória episódica pode ser mantida junto com a vetorial: cada episódio (exemplo: chat completo, ou evento “Usuário fez X, Agente respondeu Y”) pode ser “embebido” em um vetor e guardado (Belgusen, 2025). Quando preciso, por similaridade ou por chave, o agente busca episódios passados. Em agente de reforço ou autônomos complexos, a memória episódica pode ser estruturada também em gráficos ou bancos relacionais, para permitir consultas tipo “quando foi a última vez que encontrei essa situação?”. Em síntese, a memória episódica dá ao agente noção de histórico pessoal – sem ela, cada interação seria como com um agente “diferente” sem a lembrança do aprendizado prévio.

### 11.1.4 Memória semântica

Já mencionada acima dentro da longa duração, podemos reforçar: memória semântica é o conhecimento factual e conceitual do agente, desvinculado de ocasiões específicas em que foi aprendido. “Paris é capital da França” é semântico; não importa onde/como o agente aprendeu, é um fato estável que ele deve saber usar. Nos agentes atuais, parte dessa memória semântica vem embutida no LLM (porque foi pré-treinado com enormes corpora), e parte pode ser carregada via bancos de conhecimento externos (como bases vetoriais ou ontologias). É útil separar isso de memória episódica: a semântica responde “o quê/quem/como é X”, a episódica responde “o que aconteceu quando”. Em arquiteturas cognitivas, às vezes, existem módulos diferentes para cada (exemplo: um para lembrar casos específicos, outro para conhecimento geral). Ao projetar um agente, identifique quais informações entram em qual tipo: dados de perfil de usuário (nome, idade) são semânticos sobre o usuário, enquanto “última conversa do usuário” é episódica. Integração dos tipos de memória: um agente completo usará todos esses tipos em conjunto. A memória de curto prazo lida com o presente – contexto imediato da consulta ou tarefa corrente (Belgusen, 2025).

<!-- source_page: 5 -->

A memória de longo prazo (semântica e episódica) forma a base de conhecimento persistente, utilizada sob demanda via buscas ou chamadas diretas. A memória episódica alimenta a aprendizagem: o agente pode refletir sobre episódios armazenados para melhorar. Por fim, a memória semântica fornece o entendimento do mundo necessário para raciocinar em qualquer situação (por exemplo, saber que “chave inglesa” é uma ferramenta, ou que “ontem” significa dia anterior a hoje). Equilibrar e projetar como elas trabalham juntas é chave: muita dependência só da memória curta leva a esquecimentos de longo prazo; só memória longa, sem uma curta bem gerida, leva a contextos confusos (o agente tentará carregar coisas demais a cada passo). Nos próximos tópicos, exploraremos estratégias de como combinar essas memórias de forma eficiente. Exemplo ilustrativo: imagine um agente assistente que ajuda no gerenciamento de projetos:

**Figura 9 - Memória Semântica de um agente gerente de projetos**

Fonte: autoria própria.

Uma interação concreta: o usuário pergunta “Você se lembra se enfrentei problemas parecidos no Projeto X ano passado?”. O agente busca na memória vetorial por registros do projeto X do ano passado, encontra um episódio relevante (memória episódica via vetorial) e responde com base nisso, talvez combinando com conhecimento semântico (“Esse problema foi similar a um caso típico de risco em cronogramas, segundo literatura Y”).

<!-- source_page: 6 -->

Ele coloca esses dados na resposta, mantendo a conversa coesa. A memória curta garante que a resposta se refira corretamente à pergunta atual e use os dados recuperados. Assim, vemos todas atuando: curto prazo (contexto da pergunta/resposta atual), longo prazo/episódica (buscar histórico específico), semântica (contextualizar com conhecimento geral). Implementação simples – memória de curto prazo em código:

```text
# Memória de curto prazo simulada como uma lista circular
short_term_memory = []
# Função para adicionar interação na memória de curto prazo, mantendo últimas
3
def add_to_short_term(turn):
short_term_memory.append(turn)
# Mantém apenas os últimos 3 turnos (ex: último usuário e agente e usuário)
if len(short_term_memory) > 3:
short_term_memory.pop(0)
# Exemplo de uso:
add_to_short_term(“Usuário: Qual a previsão do tempo hoje?”)
add_to_short_term(“Agente: A previsão indica sol durante o dia inteiro.”)
add_to_short_term(“Usuário: Ótimo, então irei ao parque mais tarde.”)
print(“Memória de curto prazo atual:”)
for t in short_term_memory:
print(“ -”, t)
```

Saída esperada: a lista irá conter as três interações mais recentes (as duas do usuário e a resposta intermediária do agente). Se uma nova interação entrar, a mais antiga sairá. Esse mecanismo simples mostra como reter contexto imediato. Para memórias longa/episódica, implementaríamos funções de busca em um armazenamento externo (exemplo: buscar vetor similar ou consultar um dicionário de fatos). Em suma, entender os tipos de memória e seu papel ajuda a projetar agentes mais inteligentes: sabemos o que colocar na janela de contexto versus no banco vetorial; sabemos que experiências do agente devem ser registradas para aprendizado futuro; sabemos separar fatos gerais de eventos específicos. Esse “mapa” da memória no agente nos guiará nas próximas seções, nas quais veremos estratégias para usar essa memória de forma eficaz e segura.

<!-- source_page: 7 -->

## 11.2 Estratégias de retrieval-augmented generation (RAG)

Conforme visto, a técnica de RAG (geração aumentada por recuperação) é central para dar a agentes acesso à sua memória de longo prazo durante a geração de respostas. Vamos detalhar estratégias práticas para implementar RAG e gerenciar as restrições de janela de contexto dos modelos de linguagem.

### 11.2.1 Básico do retrieval-augmented generation (RAG)

Em um pipeline retrieval-augmented, toda vez que o agente vai gerar uma resposta, seja respondendo uma pergunta do usuário ou tomando uma decisão, ele faz previamente uma etapa de recuperação: uma ou mais consultas à sua base de conhecimento (memória vetorial, documentos, APIs) para coletar informações potencialmente relevantes (Qdrant, 2025). O resultado dessa busca (tipicamente, alguns trechos de texto ou dados) é, então, injetado no prompt/pergunta passado ao modelo gerador. Assim, o LLM recebe não apenas a pergunta do momento, mas também um contexto extra contendo conhecimento específico que ele de outra forma não teria. A receita básica de RAG é: Query -> Retrieve top-K docs -> Concatenar docs + Query -> Feed ao LLM para Answer.

### 11.2.2 Estratégias de recuperação relevantes

Escolher bem o que recuperar e quanto recuperar é crítico. Aqui estão algumas táticas:

- Recuperação por similaridade semântica direta: a mais comum – pegar a pergunta do usuário (ou estado atual da conversa), gerar um embedding dela, buscar na base vetorial os K itens mais similares e usar todos eles no prompt. K costuma ser 3 a 5. Essa estratégia é simples e funciona bem se a pergunta for clara e o acervo de conhecimento não for gigante. Deve-se ter cuidado com o tamanho: incluir K trechos pode consumir boa parte dos tokens da janela. Caso a janela seja pequena (exemplo: 2.048 tokens) e os trechos longos, talvez reduzir K ou resumir os trechos seja necessário.

- Recuperação hierárquica: se o acervo for muito grande ou composto de documentos extensos, pode-se fazer uma busca em duas fases. Exemplo: primeiro busca-se pelos N documentos mais relevantes (apenas títulos ou embeddings de documentos inteiros). Então, pega-se esses documentos top e realiza buscas mais refinadas dentro deles (por exemplo, cada documento é dividido em seções/chunks e busca-se nos chunks daquele doc que aparentemente é relevante). Isso evita desperdiçar tokens com documentos pouco relevantes. Um exemplo: ao perguntar algo que pode estar em um manual específico, primeiro descobre qual manual via busca de título, depois extrai trechos dentro dele.

<!-- source_page: 8 -->

- Combinação de memória episódica + semântica: se o agente tem tanto conhecimento geral quanto histórico pessoal, pode ser útil consultar ambos. Por exemplo, dado uma nova pergunta do usuário, o agente pode buscar simultaneamente na base de conhecimento global (documentos técnicos, FAQ) e na base de memórias de interações passadas daquele usuário. Então, você deve mesclar os resultados, limitando-os para não “explodir” a janela. Isso assegura que a resposta considere tanto fatos quanto contexto pessoal.

- Uso de contexto recente e extenso: uma estratégia óbvia mas relevante: sempre inclua na janela o contexto curto relevante – isso pode ser as últimas falas do chat (como vimos na memória curta) e também resumos de interações passadas se forem relevantes à pergunta atual. Por exemplo, se o usuário retoma um tópico de ontem, talvez um breve resumo do que foi discutido ontem seja concatenado ao prompt atual. Essa inclusão pode ser feita via recuperação também: a pergunta atual pode acionar uma busca na memória de episódios por “sessão de ontem com este assunto”.

- Explorar janelas de contexto maiores: modelos mais novos têm janelas enormes (exemplo: GPT-4® com 32.000 tokens, Claude® com 100.000 tokens). Com essas, uma abordagem é simplesmente permitir colocar mais informação diretamente, reduzindo a necessidade de triagem apertada. Por exemplo, com 100.000 tokens, talvez possamos inserir um documento inteiro que foi recuperado. Entretanto, pesquisas mostram que janelas maiores têm retornos decrescentes e podem até confundir o modelo se muita coisa irrelevante for embutida (Pinecone, 2025). Ou seja, continua válido usar RAG mesmo com janelas grandes, para focar apenas no que é relevante. Grandes contextos são úteis, porém, alimentá-los indiscriminadamente com “todo conhecimento” não é eficiente nem seguro (risco de vazar dados ou incorrer em custos altos de processamento) (Smith, 2025).

### 11.2.3 Gestão da janela de contexto (limites)

A janela de contexto de um LLM define quantos tokens de “memória ativa” ele pode ter. Se a entrada ultrapassar, teremos que cortar ou resumir. Estratégias para lidar com isso:

- Truncamento simples: a abordagem mais básica – cortar as partes mais antigas do histórico, quando necessário, mantendo apenas as recentes. Isso preserva as últimas instruções e conversas, mas sacrifica a memória de interações antigas. É implementado como remover do início da lista de diálogo quando o tamanho em tokens exceder o limite. Funciona bem para conversas lineares onde detalhes antigos não importam tanto. Mas pode falhar se a informação crucial estava no início.

- Sumarização de contexto antigo: em vez de simplesmente jogar fora, podese resumir blocos antigos de conversa e inseri-los em lugar do texto original. Por exemplo, se houver 50 mensagens anteriores, o agente pode gerar um resumo delas quando ficarem distantes, e manter esse resumo no contexto (atualizando conforme o necessário). Assim, preserva-se o “ponto central” do que aconteceu, ocupando menos tokens. Essa técnica requer ter confiança de que o resumo não omitiu nada importante

<!-- source_page: 9 -->

para a conversa futura – o que nem sempre é fácil. Uma melhoria é re-sumarizar incrementalmente: a cada vez que precisar remover detalhes, deve-se fazer um novo resumo que acumule informações.

- Segmentação por tópicos (context window segmentation): se a conversa ou tarefa muda de assunto, talvez seja possível “esquecer” contextos passados inteiros. Alguns agentes detectam tópicos e limpam a memória curta ao transicionar. Exemplo: num chat de suporte, se terminou assunto do produto A e começou outro do produto B, o agente pode arquivar a conversa do tópico A (talvez guardando episodicamente) e liberar espaço para o novo tópico sem perda. Isso evita preencher a janela com dados não relacionados à pergunta atual.

- Cache de contexto ampliado: uma ideia emergente é cache augmented generation ou memory streaming, onde parte do contexto é armazenado externamente e o modelo, se bem instruído, pode “pedir” detalhes quando precisar. Por exemplo, se a janela é de 2.000 tokens, mas a conversa tem 10.000 tokens de histórico relevante, o agente poderia carregar partes em blocos, mediante solicitação explícita, como um mecanismo de paginação de memória. Isso ainda é experimental e, muitas vezes, preferimos delegar ao desenvolvedor via RAG em vez do modelo por si só.

### 11.2.4 Quando retrieval-augmented generation (RAG) pode ser dispensado?

Com janelas ultragrandes (exemplo: modelos com 1 milhão de tokens, que estão sendo experimentados), surge a pergunta: e se eu simplesmente der todo o conhecimento no contexto? De fato, certos avanços apontam que para alguns casos, um modelo pode trabalhar só com contexto extenso em vez de buscas – isso simplifica a arquitetura, mas apresenta limites práticos. Por exemplo, alimentar 10 milhões de tokens (Llama 4 Scout®) é extremamente custoso e lento e a resposta ainda seria limitada pela capacidade do modelo de focar no trecho certo. A comunidade conclui que RAG continuará relevante mesmo com janelas gigantes (Smith, 2025), porque:

1. Dados corporativos e em constante mudança não podem ficar todos no prompt sempre: RAG busca só o necessário (minimizando vazamento e custo);

2. Trazer tudo prejudica a acurácia: LLMs perdem desempenho quando há muito ruído de contexto, preferindo menos informação, mas de qualidade;

3. Considerações de compliance e privacidade: se você injeta uma base inteira de dados confidenciais no contexto sempre, aumenta o risco de esses dados “vazarem” em respostas indevidas ou logs – melhor recuperar seletivamente conforme permissão (exem- plo: apenas dados daquele usuário/região).

<!-- source_page: 10 -->

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

<!-- source_page: 11 -->

é aquele que consegue usar a informação certa, na quantidade certa, no momento certo. E isso vem de combinar buscas semânticas inteligentes com técnicas de contorno das limitações de memória de trabalho do LLM.

## 11.3 Persistência, versionamento leve e políticas de retenção

Ao projetar a memória de um agente, tão importante quanto armazenar dados é decidir por quanto tempo e em que forma armazená-los. Memórias de longo prazo podem crescer indefinidamente se não houver critérios de retenção, e conteúdos podem ficar desatualizados se não houver políticas de versionamento e atualização. Vamos abordar práticas para persistir dados com segurança, manter “snapshots” ou versões leves de memória, e reter apenas o necessário pelo tempo adequado. Persistência da memória do agente: quando falamos em memória longa, assumimos que ela precisa ser persistente – isto é, sobreviver a reinicializações do agente, desligamentos de sistema etc.. Em termos práticos, isso significa usar algum armazenamento não-volátil: pode ser um banco de dados (vetorial, SQL, NoSQL), ou arquivos em disco (JSON, Comma- Separated Value [CSV], índices FAISS salvos). A escolha depende do volume e do padrão de acesso. O crucial é que sem persistência, o agente “esqueceria” tudo a cada restart, o que derrota o propósito de memória de longo prazo. Então, assegure-se de que:

- Se usar uma base vetorial in-memory durante a execução, faça backup dos dados regularmente em disco ou um repositório (por exemplo, Chroma® oferece persistir em SQLite, Qdrant® escreve em seu banco interno);

- Se usar arquivos (exemplo: guardar conversas em JSON), defina convenções para ler e escrever incrementalmente sem perda ou corrupção;

- Considere a durabilidade: se múltiplos agentes ou instâncias acessam a mesma memória, talvez um DB central seja melhor que arquivos dispersos;

- Tenha também uma estratégia de recuperação: exemplo: se o índice vetorial corromper, você consegue reindexar a partir das fontes originais que ainda tem salvas? É prudente manter as entradas originais (texto, eventos) separadas dos embeddings calculados – assim, se precisar recalcular embeddings com um novo modelo ou reconstruir índice, você tem os dados base. Persistir apenas embeddings sem referência ao dado original pode ser problemático se houver necessidade de interpretar ou “re-embedar” no futuro.

### 11.3.1 Versionamento leve de memória

Ao atualizar informações na memória (por exemplo, corrigir um dado ou acrescentar nova informação que substitui antiga), pode ser útil manter um rastro de versões em vez de simplesmente sobrescrever. Isso não significa precisar de um git complexo, mas algumas abordagens leves:

<!-- source_page: 12 -->

- Marcar itens com timestamp ou versão: em vez de deletar ou editar diretamente um vetor, pode-se inserir a nova versão com um campo “versão = 2” e talvez manter a antiga com “versão = 1 (obsoleta)”. Na busca, pode-se filtrar para só pegar a versão mais recente de cada item ou dar prioridade à maior versão. Isso garante auditabilidade e possibilidade de reverter se um update deu problema. Por exemplo, se um artigo tiver edição, você guarda ambos e, no metadado, indica qual é a versão vigente.

- Cópias de segurança periódicas (snapshots): mesmo sem versionar cada item, ter backups datados da memória (por exemplo, um dump do banco vetorial a cada semana) pode ser visto como versão temporal. Assim, se algo corromper ou se o agente “aprender” algo indevido e poluir a base, você tem um snapshot antigo para comparar ou restaurar. Isso está relacionado a governança também (auditoria).

- Versionamento de embeddings por modelo: um caso específico: se você trocar o modelo de embedding (exemplo: antes usava ada-002® da OpenAI®, agora vai usar text-embedding-babbage ou um próprio), os vetores antigos podem não ser compatíveis com os novos em termos de escala/distribuição. Uma estratégia de versionamento é manter índices separados por modelo de embedding ou um campo indicando o modelo. Assim, você evita misturar embeddings que não devem ser comparados diretamente. Pode inclusive “re-embedar” gradualmente: por exemplo, mantenha rodando dois índices e vá migrando.

- Memória de interação incremental: para memória episódica, uma forma de versionar é resumir episódios anteriores mas guardar o original bruto em algum lugar. Por exemplo, depois de 100 diálogos, você gera um sumário e armazena como “Memória consolidada”, mas ainda tem acesso aos logs completos arquivados. Assim, a “versão resumida” substitui a detalhada no uso diário, porém a fonte está lá para investigação ou se precisar re-extrair algum detalhe. Isso une retenção e versionamento.

### 11.3.2 Políticas de retenção de dados

Não é desejável guardar tudo eternamente. Além de questões legais (LGPD, que discutiremos na próxima seção) e de armazenamento finito, memorizar absolutamente tudo pode até prejudicar o desempenho – tanto do sistema (buscas mais lentas em base gigante) quanto do modelo (pode trazer dados ultrapassados/confusos). Então, defina políticas claras para quanto tempo ou quanta coisa manter:

<!-- source_page: 13 -->

**Figura 10 - Aspectos relevantes para retenção de dados**

Fonte: autoria própria.

Uma atenção especial: dados pessoais. Retenção deve ser minimizada (princípio da minimização da LGPD). Então, se o agente guarda informação pessoal do usuário, deve ter regra para eliminar após um tempo ou anonimar. Abordaremos mais adiante. Exemplo prático: expurgando memória antiga. Suponha que seu agente armazene cada interação do usuário na base vetorial com um timestamp. Você pode implementar uma rotina diária que:

<!-- source_page: 14 -->

```text
from datetime import datetime, timedelta
HOJE = datetime.now()
limite = HOJE - timedelta(days=30) # por exemplo, 30 dias
# Pseudo-código para remover vetores antigos:
vetores_a_remover = index.busque_por_filtro({“timestamp”: {“$lt”: limite}})
index.remover(vetores_a_remover)
```

Isso removeria tudo mais antigo que 30 dias. Se quiser ser mais suave: em vez de remover, mova para um arquivo de backup (exporte as interações antigas e guarde offline por um período). Assim, a base ativa fica limpa mas você não perdeu dados completamente, caso precise revisitar (pensando em “debug” ou auditoria). Essa é uma forma de archiving.

### 11.3.3 Mitigação de inconsistências e adaptações

Lembrar que a memória do agente pode conter vieses ou erros – falaremos de viés em 6.4 – mas, nesta seção, no contexto de versionamento: é útil você poder corrigir dados errados quando detectados. Uma política pode ser: quando usuário corrigir o agente (“Na verdade minha data de nascimento é 1985, não 1988”), o agente deve: Atualizar a entrada na memória semântica do perfil do usuário. Opcional: manter rastro da correção (versão anterior era 1988, marcada como incorreta). Propagar isso para futuras consultas. Exemplo: se as duas versões ainda existirem (velha e nova), talvez manter flag no metadado ativo: False na antiga; assim não será usada (Belgusen, 2025). Dessa forma, garantimos que o agente não continue replicando a informação antiga. Esse é um ponto de versionamento no conteúdo: se há memórias conflituosas, resolva marcando qual delas é válida.

### 11.3.4 Re-embedding e atualização de modelo

Por fim, uma política de retenção/versionamento raramente lembrada: se o modelo de embedding tiver atualização (exemplo: sai uma versão melhor, ou você fine-tune um embedder novo), pode valer a pena processar todo o acervo de tempos em tempos. Isso é custoso, mas algumas empresas o fazem. Por exemplo, a cada X meses elas fazem re-embedding de documentos com um modelo mais novo com o objetivo de melhorar a qualidade das buscas. Com isso, as versões antigas de vetores podem ser descartadas depois de migrar. Planos de

<!-- source_page: 15 -->

versionamento podem incluir esse marco: v1 do index (com modelo antigo) mantido até v2 ficar pronto, depois v1 é retirado. Assim, tenha isso em mente para não ficar preso a embeddings antiquados. Resumindo: a persistência garante que a memória do agente realmente exista a longo prazo; o versionamento leve fornece rastreabilidade e segurança em atualizações; a retenção evita acúmulo desnecessário e mantém a memória relevante e conforme as políticas (empresariais ou legais). Um agente com memória bem mantida é confiável e eficiente – não carrega bagagem desnecessária e pode evoluir seu conhecimento de forma controlada.

## 11.4 Qualidade de dados e mitigação de viés no acervo

A efetividade e a segurança de um agente estão diretamente ligadas à qualidade dos dados em sua memória. Dados desatualizados, incorretos ou enviesados podem levar o agente a respostas ruins ou preconceituosas. Por isso, é vital estabelecer processos para garantir qualidade e mitigar vieses na base de conhecimento do agente.

### 11.4.1 Qualidade de dados: correção e atualidade

Primeiramente, o conteúdo armazenado deve ser factualmente correto na medida do possível. Isso requer:

- Fontes confiáveis: alimentar a memória semântica do agente com fontes de qualidade (documentação oficial, artigos verificados) em lugar de dados aleatórios da internet reduz a chance de erro. Se a base de documentos for ruidosa (por exemplo, posts de fórum não moderados), considere revisar ou filtrar antes de inserir. Uma ideia é usar ferramentas de verificação (outra IA ou heurísticas) durante a ingestão. Por exemplo: se inserir uma afirmação contraditória à outra já existente, deve-se sinalizar para revisão humana ou marcar ambas com alguma indicação de conflito.

- Atualizações periódicas: como mencionado, é necessário revalidar e atualizar informações com frequência. Por exemplo, se a memória contém dados passíveis de mudança (preços, nomes de CEOs [Diretores Executivos], etc.), ative gatilhos para atualizá-los (um crawler ou integração que detecta mudanças e atualiza). Ao detectar contradições (por exemplo, a base diz CEO = Alice, mas sabe-se que mudou para “Bob”, corrija rapidamente para evitar respostas incorretas). Isso às vezes requer intervenção manual, mas, se a arquitetura permitir, use versionamento (marcando Alice como antigo).

- Remoção de lixo e redundâncias: dados duplicados ou irrelevantes poluem a memória. Use deduplicação semântica (discutida na seção 5.2) para eliminar entradas praticamente iguais. Remova também itens que tenham se provado inúteis. E, se houver um trecho

<!-- source_page: 16 -->

cuja recuperação nunca tenha ajudado em nada, ou que se tenha descoberto estar errado, não hesite em excluí-lo ou desativá-lo. Uma base enxuta e limpa é preferível a uma casa gigante cheia de entulho.

### 11.4.2 Mitigação de vieses nos embeddings e conteúdo

Modelos de linguagem e embeddings herdaram vieses dos dados de treino (exemplo: associações estereotipadas) e, caso o nosso agente armazene ou amplie esses vieses, podemos ter respostas discriminatórias ou injustas. Assim, precisamos agir em duas frentes: nos dados inseridos e no comportamento da recuperação.

- Auditoria e curadoria dos dados de entrada: antes de adicionar um lote de dados à memória, avalie se ele contém linguagem tendenciosa ou desbalanceada. Por exemplo, se alimentamos a memória com descrições de profissões e todas descrevem homens em papéis de engenharia e mulheres em papéis de enfermagem, isso vai refletir viés. Solução: balancear o dataset – adicione exemplos diversos ou ajuste o texto. Um caso real é o seguinte: embeddings de palavras que associam fortemente “engenheiro” a masculino. Mitigação: se for usar esse embedding para buscas ou raciocínio, considere treiná-lo novamente com dados balanceados ou considere usar técnicas de “desviés” (abaixo). Outra forma é: filtrar conteúdo ofensivo/inapropriado – não deixe a memória guardar insultos ou generalizações indevidas a menos que haja motivo (exemplo: o agente é moderador, então até faz sentido manter lista de palavrões para detectá-los). Ferramentas de detecção de toxicidade ou viés (IBM AIF360, por exemplo) podem sinalizar problemas nos dados (Milvus, 2025).

- Desviés nos vetores: existe pesquisa sobre remover vieses de embeddings a posteriori. Uma técnica é encontrar a direção no espaço vetorial que representa um certo viés e projetar os vetores, removendo esse componente (Milvus, 2025). Por exemplo, no caso de gênero: calcula-se o vetor diferença gênero = embedding (“homem”) - embedding (“mulher”) e então ajustam-se outros vetores para que traços irrelevantes não estejam alinhados nessa direção (Milvus, 2025). Isso pode “desenviesar” relações (por exemplo: tornar “engenheiro” equidistante de homem e mulher). Há bibliotecas para isso, entre elas a word embedding fairness evaluation (WEFE), ou utilitários do Fairlearn®. Para agentes que carregam muitos embeddings textuais, aplicar um passe de desviés nos vetores antes de indexar pode ser benéfico. Porém, tenha cuidado: isso pode degradar o desempenho de recuperar certos conceitos se não for bem executado. É um trade-off: leve se notar viés forte. Uma alternativa menos intrusiva é a augmentação contrafactual de dados. Por exemplo, para cada frase sobre um gênero, adicionar uma variante trocando o gênero. É o caso de “O enfermeiro cuidou do paciente” em que se deve guardar “A enfermeira cuidou do paciente”. Isso, durante o embedding training, ou mesmo na base do agente, ajuda a diluir associações unilaterais.

<!-- source_page: 17 -->

- Monitoramento contínuo de resultados: é importante observar as respostas do agente e analisar se há vieses emergindo. É preciso verificar, por exemplo, se com a pergunta “Quem são bons programadores?” o agente lista apenas homens. Trata-se de uma pista de viés. Ao identificá-las, volte à base e veja o que pode estar causando isso: se são os dados de treinamento do LLM, ou se é a memória contida. Se for memória contida (por exemplo, nos casos de uso do acervo de um perfil), considere adicionar diversidade.

- Reclassificação e fairness no pós-busca: além de consertar a base, uma camada de mitigação pode existir no momento da busca. Suponha que o usuário pergunta algo e a base retorna 5 resultados, mas todos têm um viés parecido. Podemos aplicar um rerank que privilegie diversidade ou penalize vieses. Exemplo: se for um sistema de recomendação de candidatos (embedding de currículos), garanta que os resultados incluam diversidade demográfica, se igualmente qualificados. Isso exigiria metadados e uma reordenação fairness-aware. Essa abordagem é sensível, uma vez que não se deve enviesar para corrigir viés, conforme preceitos éticos. Mas, em alguns contextos, como na busca por conteúdo, ferramentas como a heurística de diversidade do ElasticSearch® podem ser úteis.

- Política de exclusão de conteúdo problemático: defina também que, se certa informação for identificada como “não deveria estar aqui” (a exemplo de dados pessoais inadvertidamente coletados ou discurso de ódio replicado de algum documento antigo), ela deve ser removida ou isolada. Além disso, mantenha um processo de remoção, o que também entra em retenção: se um usuário pede “delete tudo que sabe sobre mim”, você deve poder localizar e deletar as informações. Mais adiante, falaremos de direitos que constam na LGPD.

### 11.4.3 Exemplo de viés e correção

Digamos que você note que, quando o agente está compondo uma resposta sobre liderança executiva, ele sempre usa “ele” como pronome padrão. Isso pode ser reflexo de viés nos dados. Dessa forma, inspecione a memória semântica: talvez a base de conhecimento tenha muitos perfis de CEO masculinos e poucos femininos, então as respostas aprendidas tendem a ser “ele”. Entre as mitigações possíveis estão: adicionar exemplos de lideranças femininas nos dados (balancear); ajustar o prompt do agente para ser neutro (por exemplo, evitar assumir gênero quando não fornecido); e, se possível, calibrar o modelo com fine-tuning leve, enfatizando neutralidade em ocupações.

<!-- source_page: 18 -->

Outro caso é o seguinte: embeddings de palavras podem conter proximidades indevidas (por exemplo: “terrorista” mais próximo de certa etnia). Se o seu agente usa uma busca vetorial pura, isso poderá levar a associações ofensivas. Dessa forma, aplique o desviés nesse nível ou filtre resultados por lista proibida, ajudando a evitar que, por exemplo, uma query com o nome de uma etnia retorne um documento sobre crime se isso for enviesado e indevido.

### 11.4.4 Conteúdo gerado versus conteúdo armazenado

Lembre-se de que, apesar de você limpar a base do agente, o próprio modelo de linguagem subjacente pode ter vieses de treinamento. A mitigação desta situação requer prompts de segurança e técnicas como moderação na saída. Mas quanto à memória do agente, ao menos você controla o acervo de conhecimento adicional, então faça dele um exemplo de qualidade e equidade.

### 11.4.5 Conclusão

Mantenha a memória do agente acurada, atual e inclusiva. Isso implica curadoria ativa dos dados (com possível ajuda de ferramentas automatizadas de fairness), e um loop de feedback com o qual você monitora o agente em produção, identifica problemas de dados e os corrige. Um agente é tão bom quanto sua base de conhecimento, logo, investir em qualidade e reduzir viés não é apenas uma questão ética, mas também melhora objetivamente a performance, com oferta de respostas corretas e úteis para todos os usuários.

## 11.5 Checklists de governança e Lei Geral de Proteção de Dados Pessoais (LGPD)

Desenvolver agentes inteligentes com memória requer também atenção à governança de dados e conformidade legal, especialmente com leis de proteção de dados pessoais, como a Lei Geral de Proteção de Dados do Brasil (LGPD) e a GDPR na Europa. Nesta seção, consolidamos um checklist de boas práticas para garantir que a memória do agente seja usada de forma responsável, respeitando privacidade e segurança.

<!-- source_page: 19 -->

### 11.5.1 Minimização de dados

Colete e armazene apenas os dados pessoais estritamente necessários para a finalidade do agente (Smith, 2025). Por exemplo: se o agente precisa lembrar preferências do usuário, não guarde informações irrelevantes como localização exata ou histórico completo de navegação, a menos que isso tenha um propósito claro. Evite transformar a memória do agente em um depósito geral de qualquer informação. A LGPD enfatiza a minimização, conforme o art.6, III – garantir que o tratamento se limite ao necessário, o que deve ser efetivamente implementado. Ao projetar os metadados e conteúdos a armazenar, pergunte-se: “eu realmente preciso disso para cumprir a funcionalidade?” Caso a resposta seja “não”, não armazene. Além de compliance, isso reduz riscos, tendo em vista que o que não se tem, também não vaza). Um exemplo prático disso é: em vez de armazenar o nome completo do usuário em cada registro de conversa, talvez baste um ID interno. Ou ao guardar um documento, remova antes os campos sensíveis não utilizados. Vale também aplicar técnicas de anonimização/ pseudonimização sempre que possível para minimizar identificação direta (Ecommit, 2025).

### 11.5.2 Consentimento e transparência

Caso o agente faça a coleta de dados pessoais ou sensíveis do usuário para memória longa, obtenha consentimento explícito e informado ou a base legal adequada (Ecommit, 2025). O usuário deve saber quais os tipos de dados que o agente guarda, por quanto tempo e para que finalidade. Isso pode ser apresentado como política de privacidade ou em uma interface. Um exemplo prático seria: “Posso lembrar de suas preferências de filmes para sugestões futuras?”, com a opção de aceitar. A LGPD exige consentimento para dados sensíveis e, mesmo para dados não sensíveis, é necessário transparência total sobre o tratamento, de acordo com o princípio da transparência. Portanto, inclua notificações claras no onboarding do usuário sobre o uso de memória. Caso o agente opere dentro de uma empresa com base em legítimo interesse, ainda assim honre preferências do usuário quando possível. Um detalhe: registro de consentimentos – guarde prova do consentimento dado, com timestamp, pois pode ser necessário demonstrar conformidade. Em caso de negativa de consentimento, ou se o usuário se retirar, assegure-se de que o agente pare de guardar novos dados daquela categoria e remova os existentes conforme aplicável.

<!-- source_page: 20 -->

### 11.5.3 Anonimização/pseudonimização

Sempre que possível, transforme dados pessoais em formatos anonimizados antes de armazenar. Caso o agente precise se lembrar de que “o usuário gosta de comida italiana”, isso não requer saber o nome real do usuário, o que pode ser armazenado sob um ID (pseudonimização). E se os dados forem usados para análises ou compartilhados para melhoria do agente, faça isso em nível agregado ou anonimizado, sem identificar indivíduos (Ecommit, 2025). A anonimização verdadeira (irreversível) pode ser difícil no que diz respeito à utilidade. Assim, muitas vezes opta-se pela pseudonimização (reversível com chave). Certifique-se de que as chaves que ligam o ID anônimo à identidade real sejam guardadas separadamente e com segurança. Uma dica: se o agente é um chatbot público, não associe logs a identidades a menos que isso seja necessário. E se associar (login), use pseudônimos internos. Além disso, recomenda-se mascarar/ remover dados pessoais do conteúdo sempre que não forem relevantes. Nesse sentido, se um documento contiver Cadastro de Pessoas Físicas (CPF) ou e-mail e isso não for necessário para a tarefa, retire-o antes de indexar. Quanto menos PII dentro da base, menor o risco.

### 11.5.4 Segurança dos dados armazenados

Implemente controles rigorosos de segurança na memória persistente. Isso inclui criptografia (em repouso e em trânsito) para a base de dados vetorial ou arquivos que guardam memórias (Smith, 2025). No caso de o agente estar em nuvem, assegure-se de que o banco de vetores esteja protegido com firewalls e autenticação robusta. Além disso, faça o controle do acesso em que somente pessoas/sistemas autorizados devem poder ler ou modificar a memória. Mantenha logs de acesso para auditoria, observando quem acessou dados e quando isso foi feito. A segurança é parte da governança; o vazamento de memórias de agente que contêm conversas de usuários seria uma violação grave. Lembre-se de que embeddings podem, em teoria, ser invertidos (ataques de extração) para recuperar dados originais. Sendo assim, trate vetores como dados sensíveis e não os exponha livremente.

<!-- source_page: 21 -->

### 11.5.5 Direitos dos titulares (acesso, correção, eliminação)

Prepar ando-se para LGPD/GDPR, o seu agente deve respeitar solicitações do usuário relacionadas a seus dados:

### 11.5.6 Privacy by design e por padrão

Desde o início, incorpore conceitos como privacy by design e, já nas fases de concepção do agente, inclua proteções de privacidade, a exemplo da anonimização, do consent screen e do encryption. O conceito de privacy by default indica que a configuração padrão deve ser a mais restritiva em coleta de dados. Assim, caso o agente tenha o modo de lembrar conversas, talvez, por padrão, ele não se lembre a não ser que o usuário peça ou consinta, dependendo do contexto. Isso minimiza riscos de extrair dados sem querer. Além disso, inclua também uma revisão legal no ciclo de desenvolvimento.

<!-- source_page: 22 -->

### 11.5.7 Treinamento e conscientização

Na situação de o agente ser operado por uma organização, treine a equipe envolvida para o manuseio correto de dados do agente. Desenvolvedores e engenheiros devem entender as obrigações da LGPD e seguir procedimentos, entre eles, não puxar conversas reais para testar sem anonimizar etc.. Também tenha políticas internas integradas, a exemplo da Política de Segurança da Informação (PSI) ou do Código de Conduta, para explicitar como os agentes de IA e seus dados devem ser tratados. Em seguida, registre o aceite e faça revisões periódicas.

### 11.5.8 Documentação e Avaliação de Impacto de Proteção de Dados (DPIA)

Mantenha uma documentação, como o registo das atividades de tratamento de dados (RoPA), das operações de dados do agente, atentando-se para informações sobre os dados pessoais, onde eles estão armazenados, a sua finalidade e quem tem acesso a eles. Isso ajuda na transparência e na auditoria. E ainda: se o agente lida com dados sensíveis ou de alto risco, realize uma avaliação de impacto de proteção de dados (DPIA) antes do lançamento (Tamer, 2025). Adicionalmente a isso, avalie riscos, como, por exemplo, o vazamento de conversa privada, a inferência de perfil sensível, e planeje controles mitigadores. Tenha pronto um plano de resposta a incidentes adaptado: se o agente começar a vazar informação ou for alvo de vazamento, pergunte-se: “como devo agir?”; “quem deve ser notificado?” A LGPD exige notificação à autoridade e aos afetados, em certos casos. Por fim, lembre-se de que compliance não é um obstáculo à inovação, mas sim parte integrante dela. Incorporar governança de dados desde o início pode até melhorar o design do agente, pois, como vimos, a minimização e a organização de dados levam a um sistema mais simples e eficiente. Nesse sentido, o agente responsável gera valor sustentável, portanto, use a lista acima não só como obrigação, mas como guia de qualidade para sua solução de IA.

<!-- source_page: 23 -->

### 11.5.9 Atividade prática

Considere o seguinte cenário hipotético: você está desenvolvendo um agente de IA de assistência médica que armazena informações sobre pacientes, entre as quais, nome, sintomas relatados, histórico de consultas via chat. Você precisa fornecer respostas personalizadas em interações futuras, assim, com base nesse cenário, faça o seguinte:

1. Checklist LGPD: Identifique pelo menos cinco medidas específicas que você deve tomar para que o agente esteja em conformidade com a LGPD. Uma dica é: cubra di- ferentes pontos do checklist, a exemplo de “como obter consentimento do paciente”, “como garantir minimização, um plano de retenção desses dados médicos, etc”, em seguida relacione essas medidas em formato de lista.

2. Plano de resposta a incidente: Descreva brevemente como você reagiria se descobrisse que houve um acesso não autorizado à base de dados vetorial contendo as memórias dos pacientes. Quais passos imediatos você tomaria e o que comunicaria aos pacientes e autoridades? Neste ponto você não precisa entrar em detalhe legal; apenas foque em alto nível como: “acionar equipe de segurança, notificar DPO, etc..”

3. Anonimização de dados: Proponha uma estratégia de anonimização ou pseudonimização dos dados de pacientes para fins de pesquisa e melhoria do modelo do agente, de forma que os pesquisadores possam usar os dados sem comprometer a privacidade. Neste caso, que técnicas você empregaria?

4. Consentimento informado (exercício de escrita): Rascunhe um texto curto (entre 2 e 3 linhas) que poderia ser mostrado ao paciente no primeiro uso, explicitando, de forma clara, que dados o agente pretende armazenar e pedindo permissão. Lembre-se de ser simples e transparente.

5. Análise de minimização: Dado o escopo do agente de assistência médica, avalie se algum dado atualmente planejado para armazenamento pode ser considerado excesso (não necessário para a função). Por exemplo, o agente realmente precisa guardar o nome real do paciente, ou um código já basta? Liste algum dado que poderia deixar de ser coletado e armazenado sem perder funcionalidade.

Realize esta atividade escrevendo as respostas para cada item e, ao final, verifique se suas propostas cobrem adequadamente os princípios de minimização, consentimento, segurança e direitos do titular. Não forneça soluções “de cabeça” apenas, baseie-se nas melhores práticas discutidas. Embora não haja uma resposta única, seu resultado deve refletir uma mentalidade de privacidade por design na construção do agente.

<!-- source_page: 24 -->

Saiba mais…

- Estudo aprofundado sobre memória em agentes baseados em LLMs: https://lilianweng.github.io/posts/2023-06-23-agent/

- Discussão prática sobre RAG e limites de contexto: https://www.anyscale.com/blog/ retrieval-augmented-generation

- Introdução à privacidade por design em sistemas de dados: https://www.enisa.europa. eu/topics/data-protection/privacy-by-design

Para relembrar…

- Agentes utilizam diferentes tipos de memória para manter contexto e conhecimento.

- Memórias de curta e longa duração atendem a necessidades distintas.

- RAG amplia a capacidade do agente ao recuperar informações externas relevantes.

- Governança, qualidade de dados e LGPD são essenciais para uso responsável da memória.
