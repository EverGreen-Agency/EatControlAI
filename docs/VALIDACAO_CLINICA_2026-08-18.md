# Registro de validação clínica — rodada 1

Identificador: `VAL-GLP1-R1`
Data de recebimento: 18/08/2026
Origem: respostas do médico endocrinologista ao [`PEDIDO_VALIDACAO_CLINICA.md`](PEDIDO_VALIDACAO_CLINICA.md), com ponderações da equipe
Estado geral: `VALIDADO EM CONTEÚDO`, `PENDENTE DE REGISTRO FORMAL`

> Este documento registra o que foi respondido, o que muda no produto e o que ainda falta. As regras
> executáveis derivadas estão em [`RULE_PACK_GLP1.md`](RULE_PACK_GLP1.md).

## 1. Situação em uma linha

O conteúdo clínico necessário foi respondido e é suficiente para especificar o rule pack. O que falta
não é conteúdo: é a **identificação formal da validação** exigida pelo item 5.7 do pedido.

## 2. Mudança mais relevante: política de imagem

O pedido original afirmava que o aplicativo não usaria imagem para concluir nada sobre ingredientes.
O profissional considerou isso restritivo demais e ajustou o escopo.

| Antes | Depois da validação |
|---|---|
| Imagem não sustenta conclusão sobre ingredientes | Imagem identifica alimentos e ingredientes visualmente identificáveis e **gera análise** |
| Ausência só por declaração escrita | Ausência e presença de item **oculto** continuam proibidas por imagem |
| Sem afirmação sobre preparo | Preparo visível, como fritura e molho cremoso, pode ser apontado como ponto de atenção |
| Dúvida levava a insuficiência | Dúvida leva a **sinalizar incerteza e perguntar**, ou buscar rótulo/descrição do ingrediente |

Exemplo aprovado: identificar que existe um molho cremoso é permitido; afirmar que esse molho não
contém leite ou glúten sem informação adicional continua proibido.

Consequência de engenharia: a inferência visual deixa de ser apenas um candidato descartável e passa
a ser **evidência de identificação**, com incerteza explícita, mantendo-se abaixo de rótulo declarado,
base estruturada e confirmação do usuário na hierarquia.

## 3. Personalização em três fontes

O profissional definiu que a análise deve cruzar o alimento identificado com:

1. plano alimentar individualizado cadastrado no app;
2. metas e restrições do perfil;
3. **regras pessoais criadas pelo próprio usuário** a partir da experiência dele.

O exemplo aprovado é o ciclo de aprendizado: o usuário registra que passou mal após um lanche com
leite, isso se torna uma regra pessoal, e quando os óculos identificarem queijo, creme ou molho com
possível derivado de leite, o app alerta e oferece confirmação.

Esse é um requisito novo de produto: **regra pessoal derivada de desconforto relatado**. Ele não
existia no escopo anterior.

## 4. Propriedades e prioridades aprovadas

Todas as propriedades foram consideradas relevantes, com prioridade maior para o contexto GLP-1.

| Prioridade para os óculos | Propriedade |
|---|---|
| Alta | proteína por refeição |
| Alta | tamanho da porção e volume da refeição |
| Alta | gordura total e saturada, fritura e método de preparo |
| Alta | qualidade geral do prato |
| Média | molhos cremosos, açúcar e açúcar adicionado, fibras, sódio |
| Complementar | hidratação, presença de vegetais, densidade nutricional, grau de processamento |

Justificativa clínica registrada: saciedade, tolerância gastrointestinal e preservação de massa magra.

A avaliação deve tratar o prato **como conjunto**, não como itens isolados classificados em bom ou
ruim.

## 5. Limites: três regimes distintos

O profissional separou o que pode ser objetivo do que deve ser configurável ou qualitativo. Essa
separação é a espinha dorsal do rule pack.

| Regime | O que entra | Origem do número |
|---|---|---|
| Regulatório objetivo | açúcar adicionado, gordura saturada, sódio | limites de rotulagem frontal da Anvisa, aplicáveis a **alimento embalado por 100 g ou 100 ml** |
| Configurável | proteína, hidratação, porção, demais metas | plano alimentar, orientação profissional ou preferência do usuário |
| Qualitativo | fritura, molho cremoso, refeição muito volumosa | alerta sem limite numérico artificial |

Limites regulatórios confirmados para o critério "ALTO EM":

| Nutriente | Sólidos, por 100 g | Líquidos, por 100 ml |
|---|---|---|
| Açúcar adicionado | ≥ 15 g | ≥ 7,5 g |
| Gordura saturada | ≥ 6 g | ≥ 3 g |
| Sódio | ≥ 600 mg | ≥ 300 mg |

Fontes: [Sociedade Brasileira de Diabetes](https://diabetes.org.br/rotulos-o-que-mudou/) e
[reportagem citando a Anvisa](https://www.cnnbrasil.com.br/saude/como-a-anvisa-determina-se-um-alimento-e-considerado-alto-em-sodio/),
referentes à RDC 429/2020 e à IN 75/2020, em vigor desde outubro de 2022.
Conteúdo das fontes externas foi parafraseado para conformidade com restrições de licenciamento.

**Restrição técnica registrada:** esses limites valem para composição declarada por 100 g ou 100 ml de
produto embalado. Eles **não** podem ser aplicados a um prato estimado por foto, porque não há massa
nem composição declarada. Aplicar o critério a uma estimativa visual produziria um alerta com aparência
regulatória e sem base.

## 6. Proteína

| Pergunta | Resposta registrada |
|---|---|
| Quem define a meta | profissional e usuário, em conjunto |
| Unidade exibida | gramas por dia, por simplicidade nos óculos |
| Referências citadas | 0,8 g/kg/dia como referência geral; 1,2–1,6 g/kg/dia propostos durante perda ativa de peso; 80–120 g/dia como alternativa prática |
| Individualização | obrigatória; a necessidade não é universal |
| Mensagem aceita | "Faltam aproximadamente 25 g para sua meta de proteína hoje" |
| Riscos apontados | meta inadequada, ignorar condições individuais, foco excessivo em proteína |
| Ressalva obrigatória | proteína é parte do cuidado; preservação muscular também envolve exercício de força |

Decisão de engenharia: o app **não calcula** a meta a partir de peso. Ele aceita uma meta em gramas
por dia, registra quem a definiu e compara com o consumo confirmado.

## 7. Linguagem

### Aprovada

| Situação | Padrão aprovado |
|---|---|
| Possibilidade de desconforto | "Essa refeição é mais volumosa e gordurosa. Algumas pessoas em uso de GLP-1 podem ter menor tolerância." |
| Atenção | "Atenção: o principal ponto desta escolha é a fritura e o volume da porção." |
| Informação insuficiente | "Não consigo confirmar isso apenas pela imagem. Quer me dizer os ingredientes?" |
| Limite configurado excedido | "Essa refeição ultrapassa o limite de sódio configurado no seu perfil." |
| Encaminhamento | "Esse ponto depende da sua condição ou plano individual. Confirme com seu profissional." |

Lógica de composição aprovada: **identificar, contextualizar, orientar**.

### Proibida

Além de "seguro", "diagnóstico" e "evita sintomas", ficam proibidos:

"vai causar", "não vai causar", "garantido", "sem risco", "cura", "trata", "previne", "indicado",
"contraindicado", "proibido", "liberado", "ideal para quem usa GLP-1", "isso preserva músculo",
"evita perda muscular" e qualquer orientação sobre dose ou alteração de medicação.

## 8. Encaminhamento em dois níveis

**Nível 1 — orientar procura de profissional:** sintomas gastrointestinais persistentes ou
recorrentes; perda de peso muito acelerada; dificuldade frequente para comer ou atingir metas;
fraqueza ou suspeita de perda importante de massa muscular; múltiplas restrições; gravidez; doença
renal, gastrointestinal ou outra comorbidade relevante; histórico de transtorno alimentar; dúvidas
sobre suplementação, medicamento ou dose.

**Nível 2 — interromper orientação alimentar e recomendar avaliação médica:** dor abdominal intensa ou
persistente, vômitos repetidos, incapacidade de manter líquidos, sinais importantes de desidratação,
reação alérgica relevante.

Regra de produto derivada: quando deixa de ser decisão alimentar e passa a ser questão clínica, o app
encaminha e para de opinar sobre a refeição.

## 9. Erros comuns de leitura de rótulo

Registrados como requisitos de interface, não como regra clínica:

- confundir porção com embalagem inteira;
- confundir valor por 100 g com valor por porção;
- confundir açúcar total com açúcar adicionado;
- acreditar na alegação frontal sem conferir a tabela.

Isso reforça um comportamento que o app já tem: distinguir base por porção e por 100 g, e exigir
confirmação de porções antes de somar consumo.

## 10. Alertas baseados em histórico

Aprovados como diferencial, por exemplo: proteína abaixo da meta cadastrada nos últimos dias, ou
desconforto registrado após refeições semelhantes.

Requisito novo: o app precisa registrar **desconforto relatado** vinculado a uma refeição para que
esse alerta exista.

## 11. Contexto de mercado citado pela equipe

Verificado antes de entrar nos artefatos de entrega, e classificado como projeção de terceiros:

| Afirmação da equipe | Verificação |
|---|---|
| Mercado de medicamentos GLP-1/obesidade pode superar US$ 100 bilhões por ano até 2030 | consistente com projeção da [McKinsey](https://www.mckinsey.com/featured-insights/themes/glp1s-are-changing-obesity-care-what-comes-next); análises recentes indicam revisão para baixo de estimativas anteriores de US$ 150–200 bilhões ([Reuters via Investing](https://www.investing.com/news/stock-market-news/analysisobesity-market-sales-potential-tightens-as-novo-and-lilly-enter-new-era-4478211)) |
| Mercado de saúde digital para obesidade pode chegar a cerca de US$ 247 bilhões até 2030 | compatível com projeção de US$ 247,33 bilhões em 2030, CAGR de 25,6% ([The Business Research Company](https://www.thebusinessresearchcompany.com/report/digital-health-for-obesity-global-market-report)); outras casas projetam valores diferentes, o que exige apresentar como estimativa, não como fato |

Regra de honestidade: nos artefatos de entrega, esses números aparecem como projeções atribuídas, com
faixa e divergência declaradas. Não como receita esperada do produto.

## 12. O que ainda falta — bloqueia ativação em runtime

O item 5.7 do pedido não foi respondido. Sem ele, as regras ficam especificadas mas não são
habilitadas como conteúdo clínico validado.

- [ ] nome do profissional responsável;
- [ ] registro profissional (CRM ou CRN);
- [ ] data formal da validação;
- [ ] escopo validado e escopo explicitamente não validado;
- [ ] autorização, ou negativa, para citar o nome nos materiais do projeto;
- [ ] aprovação da **redação final transformada**, conforme o rule pack, e não apenas dos exemplos;
- [ ] confirmação de que os limites da Anvisa serão usados apenas para alimento embalado com composição declarada.

## 13. Lacunas técnicas que precisam de decisão adicional

Estas não impedem a versão 1, mas precisam ficar registradas como não resolvidas:

| Lacuna | Por que importa | Encaminhamento proposto |
|---|---|---|
| Definição operacional de "porção volumosa" | não há número aprovado nem medição por foto | comparar com a porção prevista no plano cadastrado; se não houver plano, apenas descrever |
| Meta de hidratação | citada como propriedade, sem alvo | tratar como meta configurável, sem valor padrão |
| Grau de processamento | não é determinável de forma confiável por imagem | usar somente quando houver rótulo ou catálogo |
| Densidade nutricional | não há fórmula aprovada | manter fora da v1 |
| Guardrail de regra pessoal | usuário pode criar regra nutricionalmente danosa | limitar regra pessoal a evitar/observar item, sem criar meta clínica; escalar quando houver múltiplas exclusões |
| Detecção dos gatilhos de encaminhamento | exige entrada de sintoma | criar registro de sintoma e revisão periódica, sem inferir sintoma automaticamente |

## 14. Efeito nos claims rastreáveis

Atualização registrada em [`DATA_SOURCES.md`](DATA_SOURCES.md):

| Claim | Antes | Agora |
|---|---|---|
| `AJCN-GLP1-03` | `HIPÓTESE` | `VALIDADO EM CONTEÚDO`, como alerta qualitativo de tolerância |
| `AJCN-GLP1-04` | `HIPÓTESE` | `VALIDADO EM CONTEÚDO`, com limites regulatórios e metas configuráveis |
| `AJCN-GLP1-05` | `HIPÓTESE` | `VALIDADO EM CONTEÚDO`, como avaliação qualitativa do prato como conjunto |
| `AJCN-GLP1-06` | `PENDENTE DE VALIDAÇÃO` | `VALIDADO EM CONTEÚDO`, com meta configurável e sem cálculo automático |
| `AJCN-GLP1-07` | `PENDENTE DE VALIDAÇÃO` | `VALIDADO EM CONTEÚDO`, com ressalva obrigatória de exercício |
| `AJCN-GLP1-02` | `PENDENTE DE VALIDAÇÃO` | `VALIDADO EM CONTEÚDO`, via encaminhamento em dois níveis |

`VALIDADO EM CONTEÚDO` significa: o profissional aprovou a substância. A ativação em runtime depende
do registro formal do item 12.
