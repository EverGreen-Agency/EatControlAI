# Como usar este corpus em copilotos de código

## Arquivos recomendados

- `Palestra_Agente_Minimo_Viavel_FULL.md`: fonte completa, por slide.
- `sections/*.md`: melhor opção para copilotos que indexam arquivos do repositório.
- `chunks/palestra_chunks.jsonl`: ingestão em banco vetorial ou pipeline RAG.
- `SUMMARY.md`: visão rápida; não substitui a fonte para decisões técnicas.

## Estratégia de recuperação

Priorize `sections/` para perguntas conceituais e o arquivo `FULL.md` quando a pergunta exigir números, exemplos, ferramentas específicas ou a redação da palestra.

## Regra de grounding

As respostas devem distinguir:
1. o que a palestra afirma;
2. decisões do projeto derivadas dessas afirmações;
3. informação externa ou documentação atualizada.

Não trate os valores de latência, pesos e recomendações da palestra como benchmarks universais. Eles são referências do material e precisam ser medidos no dispositivo-alvo.

## Metadados úteis

Cada chunk inclui:
- `chunk_id`;
- `source_file`;
- `source_pages`;
- `section`;
- `title`;
- `tags`;
- `text`.

Sugestão de filtros: `section`, `tags`, `source_pages`.
