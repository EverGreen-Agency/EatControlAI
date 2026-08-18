---
unit: 10
unit_title: "Bases de dados vetoriais"
section: "10.6"
section_title: "Atividade prática"
source_file: "Un10_curso_meta(1).pdf"
source_markdown: "units/UN10_Bases_de_dados_vetoriais.md"
source_pages: [16, 17, 18]
language: "pt-BR"
---

## 10.6 Atividade prática

Objetivo: exercitar a implementação de uma busca vetorial simples e compreender o impacto de chunking e filtros na relevância. Descrição: Vamos criar um pequeno conjunto de dados de exemplo, gerar embeddings (pode usar um modelo pré-treinado, como SentenceTransformer) e realizar buscas com e sem filtro de metadados. Em seguida, vamos analisar os resultados e tentar um re-ranking manual simples.

Tarefas:

**Figura 8 - Etapas de um Retrieval-Augmented Generation - RAG simples**

Fonte: autoria própria.

Sem verificar as respostas prontas, tente implementar e rodar o código descrito acima, analisando os outputs. Em seguida, reflita: os resultados pareciam fazer sentido? A filtragem por categoria melhorou a precisão? E como você implementaria um re-ranking mais robusto se fosse em produção (considerando usar um modelo ou regras mais elaboradas)?

Nota: Esta atividade pode ser feita em um notebook Python®. Caso não tenha acesso a modelos de embedding, concentre-se nas etapas lógicas (2) a (5) usando vetores simulados e funções de similaridade manuais. O importante é pensar no fluxo completo da busca semântica – desde a preparação dos dados (chunks + metadados) até a recuperação e refinamento dos resultados.

Saiba mais…

- Explicação conceitual acessível sobre busca vetorial: https://www.pinecone.io/learn/ vector-search/

- Comparação entre índices aproximados e exatos: https://towardsdatascience.com/ approximate-nearest-neighbors-for-vector-search-6d0f6b2bdb8d

- Guia prático sobre embeddings e recuperação semântica: https://huggingface.co/blog/ getting-started-with-embeddings

Para relembrar…

- Bases vetoriais armazenam embeddings e permitem busca semântica eficiente.

- Elas são fundamentais para recuperação de contexto e RAG.

- Diferentes ferramentas oferecem trade-offs entre desempenho e complexidade.

- Boas práticas como chunking e uso de metadados melhoram relevância.
