# Model Benchmark Protocol

## Regra
Nenhum modelo entra no app por preferência pessoal. Entra após benchmark no mesmo dataset e aparelho-alvo.

## Processo
1. Definir tarefa.
2. Criar dataset de teste representativo de primeira pessoa.
3. Fixar pré-processamento.
4. Listar candidatos.
5. Rodar warm-up.
6. Rodar N repetições.
7. Medir qualidade e latência.
8. Testar versão quantizada.
9. Comparar degradação de qualidade.
10. Selecionar Pareto frontier (qualidade × latência × tamanho × energia).

## Score sugerido
`score = 0.45*quality + 0.25*latency + 0.15*size + 0.10*energy + 0.05*integration`

Normalizar cada eixo para 0–1.
Para tarefas críticas, aplicar constraint antes do score (ex.: recall mínimo).

## Regra de troca
Toda implementação deve respeitar a interface da tarefa para ser substituível por configuração.
