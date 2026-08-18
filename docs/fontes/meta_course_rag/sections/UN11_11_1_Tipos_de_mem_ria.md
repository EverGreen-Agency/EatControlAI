---
unit: 11
unit_title: "Memória e armazenamento de dados para agentes"
section: "11.1"
section_title: "Tipos de memória"
source_file: "Un11_curso_meta.pdf"
source_markdown: "units/UN11_Mem_ria_e_armazenamento_de_dados_para_agentes.md"
source_pages: [2, 3, 4, 5, 6]
language: "pt-BR"
---

## 11.1 Tipos de memória

Agentes inteligentes inspiram-se em conceitos de memória humana para organizar e recuperar informações. Podemos categorizar a memória de um agente em alguns tipos principais, com papéis distintos.

### 11.1.1 Memória de curta duração (contexto de trabalho)

É a informação retida temporariamente enquanto é necessária para a tarefa imediata. No caso de um agente baseado em LLM, a memória de curto prazo corresponde basicamente ao conteúdo atualmente carregado na janela de contexto do modelo – por exemplo, as últimas mensagens na conversa ou as instruções atuais que o agente está seguindo. É similar à memória de trabalho humana, que dura segundos ou minutos. Essa memória tem capacidade limitada (exemplo: 4.000 tokens, 8.000 tokens etc., dependendo do modelo) e é volátil: se a conversa ou tarefa muda, ou se exceder o limite, informações antigas são esquecidas (a não ser que tenham sido movidas para a memória longa). Um agente utiliza a memória de curto prazo para manter coerência local – lembrar o que o usuário acabou de dizer, os detalhes que precisa para o próximo passo, o estado atual do raciocínio. Após completar a tarefa ou quando o contexto “estoura” o limite, essa memória é basicamente descartada ou condensada. Em implementações, manter a curta duração costuma ser tão simples quanto armazenar as últimas N interações em uma lista e enviá-las junto com a nova pergunta do usuário para o LLM (Belgusen, 2025). Por exemplo, em um chatbot, as últimas poucas trocas de mensagem ficam na janela; as mais antigas são removidas ou resumidas quando o limite se aproxima. Assim, a memória de curto prazo garante respostas contextualmente consistentes em conversas ou sessões curtas, mas não resolve necessidades de lembrança após longos intervalos.

### 11.1.2 Memória de longa duração (vetorial ou persistente)

Refere-se ao “conhecimento duradouro” que o agente pode consultar mesmo após muito tempo ou em qualquer contexto. Geralmente, é implementada por meio de uma base de conhecimento externa – pode ser uma base de dados vetorial com embeddings (daí chamamos de memória vetorial), um banco de dados relacional com fatos ou até arquivos de texto/JSON. O essencial é que essa memória persiste entre sessões e pode guardar volume grande de informações aprendidas ou fornecidas ao agente (Belgusen, 2025). Por exemplo, tudo o que um assistente pessoal “aprende” sobre você (seu nome, aniversário, preferências) deve ir para a memória de longo prazo, pois deve estar disponível amanhã, semana que vem ou mês que vem. Diferentemente da memória curta, que é passiva (o modelo “lembra” porque o texto ainda está no prompt), a memória longa requer um mecanismo ativo de recuperação. Tipicamente, busca por similaridade: o agente, ao receber uma nova pergunta, busca na memória vetorial por trechos relevantes para trazer para a memória de curto prazo (janela) antes de responder. Assim, a memória longa aumenta a capacidade do agente para além do que cabe em contexto. Ela também pode ser atualizada incrementalmente: novos fatos entram ao longo do tempo. Alguns subtipos dessa memória:

- Memória de conhecimento geral ou semântica: fatos e conceitos que o agente sabe sobre o mundo, domínios específicos etc.. Pode vir de ingestão de documentos, manuais, wikis (por isso, muitas vezes, materializada em embeddings vetoriais indexando esses documentos). Exemplo: um agente médico pode ter na memória semântica informações sobre doenças e sintomas (Belgusen, 2025).

- Memória especializada ou de habilidades: por vezes, chamada de memória procedimental, armazena como fazer algo (algoritmos, funções). Em agentes, isso geralmente está no próprio código ou em modelos de ferramenta (por exemplo, a habilidade de calcular ou navegar em certa API). Alguns frameworks se diferem, mas, para nosso contexto, podemos considerar isso parte de “conhecimento de longo prazo” do agente também.

### 11.1.3 Memória episódica

É a capacidade de lembrar de eventos específicos vividos pelo agente, com contexto de tempo e detalhes específicos daquela ocorrência (Belgusen, 2025). Para humanos, memória episódica é lembrar de um jantar na semana passada; para um agente de IA, seria lembrar de interações passadas ou experiências específicas que não são simplesmente fatos gerais. Por exemplo, “Ontem o usuário João reclamou de um bug e eu (agente) dei a volta errada no suporte, ele ficou frustrado” – isso é um episódio vivido. Em termos de implementação, memória episódica frequentemente se traduz em log de conversas e ações que o agente pode

consultar. Se o agente tem múltiplas sessões com um usuário, armazenar cada sessão (ou um resumo dela) com um timestamp é dotá-lo de memória episódica: ele pode depois recuperar “o que conversamos na sessão do dia tal”. Essa memória tem natureza autobiográfica, ou seja, ajuda o agente a “aprender” com a experiência. Por exemplo, um agente que tentou um procedimento e falhou poderia armazenar essa experiência para não repetir o erro (reforçando ou adaptando estratégias). Em aplicações, a memória episódica pode ser mantida junto com a vetorial: cada episódio (exemplo: chat completo, ou evento “Usuário fez X, Agente respondeu Y”) pode ser “embebido” em um vetor e guardado (Belgusen, 2025). Quando preciso, por similaridade ou por chave, o agente busca episódios passados. Em agente de reforço ou autônomos complexos, a memória episódica pode ser estruturada também em gráficos ou bancos relacionais, para permitir consultas tipo “quando foi a última vez que encontrei essa situação?”. Em síntese, a memória episódica dá ao agente noção de histórico pessoal – sem ela, cada interação seria como com um agente “diferente” sem a lembrança do aprendizado prévio.

### 11.1.4 Memória semântica

Já mencionada acima dentro da longa duração, podemos reforçar: memória semântica é o conhecimento factual e conceitual do agente, desvinculado de ocasiões específicas em que foi aprendido. “Paris é capital da França” é semântico; não importa onde/como o agente aprendeu, é um fato estável que ele deve saber usar. Nos agentes atuais, parte dessa memória semântica vem embutida no LLM (porque foi pré-treinado com enormes corpora), e parte pode ser carregada via bancos de conhecimento externos (como bases vetoriais ou ontologias). É útil separar isso de memória episódica: a semântica responde “o quê/quem/como é X”, a episódica responde “o que aconteceu quando”. Em arquiteturas cognitivas, às vezes, existem módulos diferentes para cada (exemplo: um para lembrar casos específicos, outro para conhecimento geral). Ao projetar um agente, identifique quais informações entram em qual tipo: dados de perfil de usuário (nome, idade) são semânticos sobre o usuário, enquanto “última conversa do usuário” é episódica. Integração dos tipos de memória: um agente completo usará todos esses tipos em conjunto. A memória de curto prazo lida com o presente – contexto imediato da consulta ou tarefa corrente (Belgusen, 2025).

A memória de longo prazo (semântica e episódica) forma a base de conhecimento persistente, utilizada sob demanda via buscas ou chamadas diretas. A memória episódica alimenta a aprendizagem: o agente pode refletir sobre episódios armazenados para melhorar. Por fim, a memória semântica fornece o entendimento do mundo necessário para raciocinar em qualquer situação (por exemplo, saber que “chave inglesa” é uma ferramenta, ou que “ontem” significa dia anterior a hoje). Equilibrar e projetar como elas trabalham juntas é chave: muita dependência só da memória curta leva a esquecimentos de longo prazo; só memória longa, sem uma curta bem gerida, leva a contextos confusos (o agente tentará carregar coisas demais a cada passo). Nos próximos tópicos, exploraremos estratégias de como combinar essas memórias de forma eficiente. Exemplo ilustrativo: imagine um agente assistente que ajuda no gerenciamento de projetos:

**Figura 9 - Memória Semântica de um agente gerente de projetos**

Fonte: autoria própria.

Uma interação concreta: o usuário pergunta “Você se lembra se enfrentei problemas parecidos no Projeto X ano passado?”. O agente busca na memória vetorial por registros do projeto X do ano passado, encontra um episódio relevante (memória episódica via vetorial) e responde com base nisso, talvez combinando com conhecimento semântico (“Esse problema foi similar a um caso típico de risco em cronogramas, segundo literatura Y”).

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
