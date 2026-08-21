# Pedido de validação clínica — Eat Control AI

Versão do pedido: 1.1
Data: 18/08/2026
Destinatário: médico endocrinologista e/ou nutricionista responsável pela validação
Remetente: equipe Eat Control AI
Situação: **respondido na rodada 1**; respostas registradas em
[`VALIDACAO_CLINICA_2026-08-18.md`](VALIDACAO_CLINICA_2026-08-18.md) e transformadas em
[`RULE_PACK_GLP1.md`](RULE_PACK_GLP1.md)

> Falta apenas o item 5.7: identificação do profissional, registro, data, escopo, autorização de
> citação e aprovação da redação final transformada. Sem isso, as regras ficam especificadas e não
> entram em runtime como conteúdo clínico validado.

> Use este documento como está para enviar ao profissional. Não é necessário conhecimento técnico para respondê-lo.

### Pacote de evidência submetido

Este pedido deve ser avaliado junto da síntese rastreável `CLIN-AJCN-001` e dos claims `AJCN-GLP1-01` a `AJCN-GLP1-10` em [`DATA_SOURCES.md`](DATA_SOURCES.md). A fonte primária é o advisory conjunto publicado no *The American Journal of Clinical Nutrition*:

- texto integral canônico: [PMCID PMC12612741](https://pmc.ncbi.nlm.nih.gov/articles/PMC12612741/), [PMID 40450457](https://pubmed.ncbi.nlm.nih.gov/40450457/) e [DOI 10.1016/j.ajcnut.2025.04.023](https://doi.org/10.1016/j.ajcnut.2025.04.023);
- corrigendum: [PMID 41962912](https://pubmed.ncbi.nlm.nih.gov/41962912/).

Cópias locais de trabalho, quando existirem, permanecem em acervo privado e não são anexadas nem versionadas. A versão PMC é a referência reproduzível usada neste pedido.

A publicação informa prioridades populacionais e clínicas gerais. Ela **não** será convertida automaticamente em limite, meta ou prescrição individual. Os itens marcados como `HIPÓTESE` ou `PENDENTE DE VALIDAÇÃO` em `DATA_SOURCES.md` precisam da sua revisão; depois, enviaremos também a regra e a redação transformadas para aprovação final.

---

## 1. Em uma frase

Estamos construindo um aplicativo que usa óculos inteligentes e o celular para oferecer suporte educativo, em poucos segundos e por áudio, diante de uma escolha alimentar. Precisamos que qualquer **regra clínica** proposta a partir da literatura seja revisada, delimitada e aprovada por você antes de entrar no aplicativo.

## 2. O que o aplicativo já faz hoje

- Fotografa um rótulo ou código de barras, lê o texto no próprio aparelho, sem enviar nada para a internet.
- Identifica declarações do fabricante do tipo "contém leite", "pode conter traços de", "não contém glúten".
- Compara com as restrições cadastradas pela pessoa e responde em quatro estados: compatível, incompatível, precisa de confirmação, informação insuficiente.
- Quando a informação não é suficiente, ele pergunta em vez de afirmar.
- Guarda o histórico apenas no aparelho da pessoa.

## 3. O que o aplicativo NÃO faz, por decisão nossa

- Não dá diagnóstico.
- Não prescreve dieta.
- Não sugere, ajusta ou comenta medicação.
- Não usa a palavra "seguro".
- Não garante presença nem ausência de ingrediente oculto, alergênico ou componente de preparo apenas
  pela imagem. A identificação visual de alimentos e ingredientes visíveis é permitida e faz parte do
  escopo central; quando houver dúvida, o app sinaliza a incerteza e pede confirmação.

Queremos manter essas restrições. Se você discordar de alguma, por favor diga.

> Ajustado na rodada 1 de validação, a pedido do profissional. Registro em
> [`VALIDACAO_CLINICA_2026-08-18.md`](VALIDACAO_CLINICA_2026-08-18.md).

## 4. Contexto do público

O público inicial é composto por pessoas em tratamento com análogos de GLP-1. O que motivou esse recorte foi o relato recorrente de:

- saciedade precoce, com refeições de volume pequeno;
- desconforto após certas refeições;
- dificuldade de decidir no momento da compra ou do pedido;
- preocupação com perda de massa magra.

Se esse recorte estiver equivocado, ou se você considerar outro público prioritário, queremos saber antes de avançarmos.

---

## 5. O que precisamos de você

Não estamos pedindo uma "dieta universal". Estamos pedindo um conjunto pequeno de **regras demonstrativas, explícitas e configuráveis**, que o aplicativo aplicará de forma transparente e sempre citando a origem.

### 5.1 Propriedades que importam

Quais características de um alimento devem influenciar a resposta ao usuário? Por exemplo:

- [ ] gordura total
- [ ] gordura saturada
- [ ] tamanho da porção
- [ ] volume da refeição
- [ ] fritura ou método de preparo
- [ ] molhos cremosos
- [ ] teor de açúcar / açúcar adicionado
- [ ] fibras
- [ ] proteína por refeição
- [ ] sódio
- [ ] outras: ______________________________

### 5.2 Limites ou faixas

Para cada propriedade marcada acima, existe um limite ou faixa que você considera aceitável usar em uma demonstração?

| Propriedade | Limite/faixa sugerida | Unidade | Vale por refeição ou por dia? | Observação |
|---|---|---|---|---|
|  |  |  |  |  |
|  |  |  |  |  |
|  |  |  |  |  |

Se você preferir **não** fixar limites, podemos deixar os valores configuráveis por paciente. Nesse caso, indique quais campos devem ser configuráveis e qual a faixa segura de configuração.

### 5.3 Metas de macronutrientes

Pretendemos mostrar ao usuário quanto ele consumiu no dia e quanto falta para a meta dele.

- As metas devem ser definidas por quem? (paciente, profissional, ambos)
- Devem ser expressas em gramas por dia, gramas por quilo de peso, percentual do total energético, ou outra forma?
- Existe um mínimo de proteína que você considera relevante destacar para esse público?
- Faz sentido mostrar "faltam X g de proteína hoje"? Se sim, com que redação?
- Existe risco em mostrar essa informação sem acompanhamento? Se sim, qual?

### 5.4 Linguagem permitida

Precisamos das frases que o aplicativo pode dizer.

- Como comunicar possibilidade de desconforto **sem** afirmar causa?
- Que redação você aprova para "atenção" (algo a considerar, sem impedir)?
- Que redação você aprova para "informação insuficiente"?
- Que redação você aprova quando um alimento excede um limite configurado?
- Quando o aplicativo deve dizer "confirme com seu profissional"?

### 5.5 Linguagem proibida

Quais palavras ou construções não podem aparecer em nenhuma hipótese? Já bloqueamos "seguro", "diagnóstico" e "evita sintomas". O que mais você acrescentaria?

### 5.6 Situações de encaminhamento

Em quais situações o aplicativo deve parar de responder e orientar procura de profissional? Por exemplo: sintoma relatado, perda de peso acelerada, restrição múltipla, gravidez, comorbidade específica.

### 5.7 Registro da validação

Para rastreabilidade, precisamos anexar:

- nome e registro profissional (CRM/CRN);
- data da validação;
- versão das regras validadas (usaremos numeração, ex.: `glp1-rules-v1`);
- escopo do que foi validado e do que não foi;
- se você autoriza citação do seu nome nos materiais do projeto.

---

## 6. Como as suas respostas serão usadas

- Serão transformadas em um conjunto de regras versionado dentro do aplicativo, legível e auditável.
- O aplicativo exibirá qual versão de regra foi usada em cada resposta.
- Nada será inferido além do que você escrever. Onde não houver regra, o aplicativo dirá que a informação é insuficiente.
- Você poderá revisar e revogar qualquer regra em versões seguintes.

## 7. Perguntas que também nos ajudam

- Existe alguma informação que, na sua prática, os pacientes mais erram ao ler rótulos?
- Existe algum alerta que você gostaria que existisse e que ninguém oferece hoje?
- Há algo neste projeto que você considera arriscado do ponto de vista clínico?

## 8. Prazo

Precisamos de uma primeira versão até **20/08/2026** para conseguir demonstrar o produto com regras reais. Uma resposta parcial já é útil: preferimos poucas regras validadas a muitas regras inventadas por nós.

Obrigado. Qualquer trecho que você preferir responder por áudio ou em conversa, nós transcrevemos e enviamos de volta para conferência antes de implementar.
