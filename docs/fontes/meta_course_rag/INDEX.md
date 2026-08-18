# Índice do corpus RAG - CEIA + Meta

Este pacote contém **somente os materiais `Un1` a `Un13` fornecidos no projeto**. O texto das unidades foi extraído da camada textual dos PDFs e normalizado. Em páginas identificadas como essencialmente visuais, foi adicionada transcrição/descrição do próprio conteúdo visual.

| Unidade | Título | PDF fonte | Páginas | Seções | Chunks | Markdown |
|---:|---|---|---:|---:|---:|---|
| 1 | Como os modelos aprendem? | `Un1_curso_meta.pdf` | 8 | 7 | 8 | `units/UN01_Como_os_modelos_aprendem.md` |
| 2 | Principais abordagens e aplicações | `Un2_curso_meta.pdf` | 136 | 5 | 100 | `units/UN02_Principais_abordagens_e_aplica_es.md` |
| 3 | Introdução à Geração de Texto com Modelos de Linguagem | `Un3_curso_meta(1).pdf` | 13 | 3 | 13 | `units/UN03_Introdu_o_Gera_o_de_Texto_com_Modelos_de_Linguagem.md` |
| 4 | Engenharia de Prompts para Geração de Texto | `Un4_curso_meta(1).pdf` | 15 | 4 | 12 | `units/UN04_Engenharia_de_Prompts_para_Gera_o_de_Texto.md` |
| 5 | Ferramentas para Manipulação de Modelos de Linguagem | `Un5_curso_meta(1).pdf` | 20 | 4 | 22 | `units/UN05_Ferramentas_para_Manipula_o_de_Modelos_de_Linguagem.md` |
| 6 | Fundamentos de sistemas de agentes | `Un6_curso_meta.pdf` | 20 | 9 | 30 | `units/UN06_Fundamentos_de_sistemas_de_agentes.md` |
| 7 | Arquiteturas de agentes | `Un7_curso_meta.pdf` | 12 | 6 | 20 | `units/UN07_Arquiteturas_de_agentes.md` |
| 8 | Ecossistema de ferramentas - visão geral | `Un8_curso_meta(1).pdf` | 18 | 7 | 29 | `units/UN08_Ecossistema_de_ferramentas_vis_o_geral.md` |
| 9 | Laboratório 01 - Seu primeiro agente | `Un9_curso_meta.pdf` | 16 | 6 | 24 | `units/UN09_Laborat_rio_01_Seu_primeiro_agente.md` |
| 10 | Bases de dados vetoriais | `Un10_curso_meta(1).pdf` | 19 | 7 | 32 | `units/UN10_Bases_de_dados_vetoriais.md` |
| 11 | Memória e armazenamento de dados para agentes | `Un11_curso_meta.pdf` | 25 | 5 | 38 | `units/UN11_Mem_ria_e_armazenamento_de_dados_para_agentes.md` |
| 12 | Curso de Kotlin/Android | `Un12_Material_de_apoio_Meta.pdf` | 94 | 14 | 93 | `units/UN12_Curso_de_Kotlin_Android.md` |
| 13 | Curso do Meta SDK (DAT) | `Un13_Material_de_apoio_Meta.pdf` | 64 | 7 | 60 | `units/UN13_Curso_do_Meta_SDK_DAT.md` |

**Total de chunks:** 481

## Estrutura

- `units/`: um Markdown consolidado por unidade, com marcadores de página (`source_page`).
- `sections/`: arquivos menores por seção principal, úteis para copilotos que indexam arquivos diretamente.
- `chunks/meta_course_chunks.jsonl`: chunks prontos para embeddings/vector store, com unidade, seção e páginas de origem.
- `manifest.json`: inventário, hashes SHA-256, páginas e mapeamento de seções.

## Recomendação de ingestão

Para RAG vetorial, prefira `chunks/meta_course_chunks.jsonl`. Para copilotos que fazem busca no repositório, prefira `sections/`. Use `units/` como fonte humana/canônica para conferência de contexto e proveniência.

Não misture esses arquivos com documentação externa no mesmo namespace se a intenção for responder estritamente segundo o curso.
