---
unit: 11
unit_title: "Memória e armazenamento de dados para agentes"
section: "11.3"
section_title: "Persistência, versionamento leve e políticas de retenção"
source_file: "Un11_curso_meta.pdf"
source_markdown: "units/UN11_Mem_ria_e_armazenamento_de_dados_para_agentes.md"
source_pages: [11, 12, 13, 14, 15]
language: "pt-BR"
---

## 11.3 Persistência, versionamento leve e políticas de retenção

Ao projetar a memória de um agente, tão importante quanto armazenar dados é decidir por quanto tempo e em que forma armazená-los. Memórias de longo prazo podem crescer indefinidamente se não houver critérios de retenção, e conteúdos podem ficar desatualizados se não houver políticas de versionamento e atualização. Vamos abordar práticas para persistir dados com segurança, manter “snapshots” ou versões leves de memória, e reter apenas o necessário pelo tempo adequado. Persistência da memória do agente: quando falamos em memória longa, assumimos que ela precisa ser persistente – isto é, sobreviver a reinicializações do agente, desligamentos de sistema etc.. Em termos práticos, isso significa usar algum armazenamento não-volátil: pode ser um banco de dados (vetorial, SQL, NoSQL), ou arquivos em disco (JSON, Comma- Separated Value [CSV], índices FAISS salvos). A escolha depende do volume e do padrão de acesso. O crucial é que sem persistência, o agente “esqueceria” tudo a cada restart, o que derrota o propósito de memória de longo prazo. Então, assegure-se de que:

- Se usar uma base vetorial in-memory durante a execução, faça backup dos dados regularmente em disco ou um repositório (por exemplo, Chroma® oferece persistir em SQLite, Qdrant® escreve em seu banco interno);

- Se usar arquivos (exemplo: guardar conversas em JSON), defina convenções para ler e escrever incrementalmente sem perda ou corrupção;

- Considere a durabilidade: se múltiplos agentes ou instâncias acessam a mesma memória, talvez um DB central seja melhor que arquivos dispersos;

- Tenha também uma estratégia de recuperação: exemplo: se o índice vetorial corromper, você consegue reindexar a partir das fontes originais que ainda tem salvas? É prudente manter as entradas originais (texto, eventos) separadas dos embeddings calculados – assim, se precisar recalcular embeddings com um novo modelo ou reconstruir índice, você tem os dados base. Persistir apenas embeddings sem referência ao dado original pode ser problemático se houver necessidade de interpretar ou “re-embedar” no futuro.

### 11.3.1 Versionamento leve de memória

Ao atualizar informações na memória (por exemplo, corrigir um dado ou acrescentar nova informação que substitui antiga), pode ser útil manter um rastro de versões em vez de simplesmente sobrescrever. Isso não significa precisar de um git complexo, mas algumas abordagens leves:

- Marcar itens com timestamp ou versão: em vez de deletar ou editar diretamente um vetor, pode-se inserir a nova versão com um campo “versão = 2” e talvez manter a antiga com “versão = 1 (obsoleta)”. Na busca, pode-se filtrar para só pegar a versão mais recente de cada item ou dar prioridade à maior versão. Isso garante auditabilidade e possibilidade de reverter se um update deu problema. Por exemplo, se um artigo tiver edição, você guarda ambos e, no metadado, indica qual é a versão vigente.

- Cópias de segurança periódicas (snapshots): mesmo sem versionar cada item, ter backups datados da memória (por exemplo, um dump do banco vetorial a cada semana) pode ser visto como versão temporal. Assim, se algo corromper ou se o agente “aprender” algo indevido e poluir a base, você tem um snapshot antigo para comparar ou restaurar. Isso está relacionado a governança também (auditoria).

- Versionamento de embeddings por modelo: um caso específico: se você trocar o modelo de embedding (exemplo: antes usava ada-002® da OpenAI®, agora vai usar text-embedding-babbage ou um próprio), os vetores antigos podem não ser compatíveis com os novos em termos de escala/distribuição. Uma estratégia de versionamento é manter índices separados por modelo de embedding ou um campo indicando o modelo. Assim, você evita misturar embeddings que não devem ser comparados diretamente. Pode inclusive “re-embedar” gradualmente: por exemplo, mantenha rodando dois índices e vá migrando.

- Memória de interação incremental: para memória episódica, uma forma de versionar é resumir episódios anteriores mas guardar o original bruto em algum lugar. Por exemplo, depois de 100 diálogos, você gera um sumário e armazena como “Memória consolidada”, mas ainda tem acesso aos logs completos arquivados. Assim, a “versão resumida” substitui a detalhada no uso diário, porém a fonte está lá para investigação ou se precisar re-extrair algum detalhe. Isso une retenção e versionamento.

### 11.3.2 Políticas de retenção de dados

Não é desejável guardar tudo eternamente. Além de questões legais (LGPD, que discutiremos na próxima seção) e de armazenamento finito, memorizar absolutamente tudo pode até prejudicar o desempenho – tanto do sistema (buscas mais lentas em base gigante) quanto do modelo (pode trazer dados ultrapassados/confusos). Então, defina políticas claras para quanto tempo ou quanta coisa manter:

**Figura 10 - Aspectos relevantes para retenção de dados**

Fonte: autoria própria.

Uma atenção especial: dados pessoais. Retenção deve ser minimizada (princípio da minimização da LGPD). Então, se o agente guarda informação pessoal do usuário, deve ter regra para eliminar após um tempo ou anonimar. Abordaremos mais adiante. Exemplo prático: expurgando memória antiga. Suponha que seu agente armazene cada interação do usuário na base vetorial com um timestamp. Você pode implementar uma rotina diária que:

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

versionamento podem incluir esse marco: v1 do index (com modelo antigo) mantido até v2 ficar pronto, depois v1 é retirado. Assim, tenha isso em mente para não ficar preso a embeddings antiquados. Resumindo: a persistência garante que a memória do agente realmente exista a longo prazo; o versionamento leve fornece rastreabilidade e segurança em atualizações; a retenção evita acúmulo desnecessário e mantém a memória relevante e conforme as políticas (empresariais ou legais). Um agente com memória bem mantida é confiável e eficiente – não carrega bagagem desnecessária e pode evoluir seu conhecimento de forma controlada.
