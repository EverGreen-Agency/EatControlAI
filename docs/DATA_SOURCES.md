# Fontes de dados e evidências

Este arquivo é o registro canônico de proveniência do Eat Control AI. Fontes operacionais, evidência clínica e decisões de produto são dimensões diferentes: uma publicação pode fundamentar uma hipótese, mas não cria automaticamente uma regra individual, um limite ou uma prescrição.

## 1. Estados usados na tradução para produto

| Estado | Significado neste arquivo |
|---|---|
| `DECIDIDO` | guardrail ou decisão de produto/engenharia; não significa validação clínica |
| `HIPÓTESE` | tradução candidata da evidência para uma funcionalidade ou mensagem, ainda sem validação profissional |
| `PENDENTE DE VALIDAÇÃO` | não pode virar regra ou texto de runtime até revisão e aprovação por profissional habilitado |
| `VALIDADO EM CONTEÚDO` | profissional aprovou a substância da regra; ativação em runtime depende do registro formal da validação |

A qualidade e o escopo da fonte são registrados separadamente do estado da implementação.

## 2. Evidência clínica GLP-1

### CLIN-AJCN-001 — Advisory conjunto sobre prioridades nutricionais

- **Referência:** Mozaffarian D. et al. *Nutritional priorities to support GLP-1 therapy for obesity: a joint Advisory from the American College of Lifestyle Medicine, the American Society for Nutrition, the Obesity Medicine Association, and The Obesity Society*. The American Journal of Clinical Nutrition. 2025;122(1):344–367.
- **DOI:** [`10.1016/j.ajcnut.2025.04.023`](https://doi.org/10.1016/j.ajcnut.2025.04.023)
- **Artigo original:** PMCID [`PMC12612741`](https://pmc.ncbi.nlm.nih.gov/articles/PMC12612741/); PMID [`40450457`](https://pubmed.ncbi.nlm.nih.gov/40450457/)
- **Texto estruturado oficial:** [Europe PMC Full Text XML](https://www.ebi.ac.uk/europepmc/webservices/rest/PMC12612741/fullTextXML)
- **Correção publicada:** PMID [`41962912`](https://pubmed.ncbi.nlm.nih.gov/41962912/), DOI [`10.1016/j.ajcnut.2026.101303`](https://doi.org/10.1016/j.ajcnut.2026.101303). O registro PMC identifica o artigo como corrigido; a versão PMC é a referência online canônica.
- **Tipo e alcance:** advisory multidisciplinar que combina avaliação da literatura, conhecimento especializado e experiência clínica. O foco é o uso de GLP-1 para tratamento da obesidade, predominantemente no contexto dos Estados Unidos, com possíveis implicações globais.
- **O que sustenta:** prioridades gerais de avaliação, alimentação, hidratação, sintomas gastrointestinais, adequação nutricional, massa muscular/óssea, atividade física e suporte digital.
- **O que não sustenta sozinho:** prescrição individual; meta automática de macro; limite universal por alimento/refeição; diagnóstico; ajuste de medicamento; causalidade de sintoma para um alimento específico; precisão de macro inferido por imagem.
- **Acesso verificado em:** 18/08/2026.

### Conferência e paginação

A correspondência do texto integral foi conferida na versão canônica do PMC por título, DOI, PMCID, intervalo de páginas e conteúdo. A página 1 do PDF de trabalho corresponde à página 344 do periódico e a página 24 à página 367. Cópias locais de trabalho permanecem em acervo privado e não são anexadas nem versionadas; a referência reproduzível do projeto é o PMCID público acima.

### Síntese rastreável para requisitos de produto

Os resumos abaixo são paráfrases controladas. O estado classifica a **tradução para o produto**, não a existência da afirmação na publicação.

| Claim ID | Síntese da fonte | Seção e página do periódico | Implicação candidata no Eat Control | Estado |
|---|---|---|---|---|
| `AJCN-GLP1-01` | O cuidado deve ser individualizado e considerar condições médicas, preferências, contexto e metas da pessoa. | “Initiation of GLP-1 use with a patient-centered approach”, pp. 349–350 | O app usa perfil configurado e evidencia a origem das metas; não gera um plano universal. | `DECIDIDO` |
| `AJCN-GLP1-02` | Avaliação e acompanhamento clínico/nutricional abrangem hábitos, sintomas gastrointestinais, riscos de deficiência, transtornos alimentares, força/função muscular e contexto social. | “Completion of baseline nutritional assessment and screening”, pp. 349–351, Tabela 5 | Encaminhamento em dois níveis aprovado em `VAL-GLP1-R1`: orientar procura de profissional, ou interromper a orientação alimentar e recomendar avaliação médica. | `VALIDADO EM CONTEÚDO` |
| `AJCN-GLP1-03` | Durante início ou escalonamento, refeições menores e mais frequentes e ingestão adequada de líquidos podem ajudar em alguns sintomas; refeições grandes ou muito gordurosas podem piorar tolerabilidade em parte das pessoas. | “Management of GI side effects”, pp. 351–352 | Alerta qualitativo de tolerância aprovado, sem limite numérico e sem atribuir sintoma a um alimento; registro de desconforto pelo usuário vira regra pessoal. | `VALIDADO EM CONTEÚDO` |
| `AJCN-GLP1-04` | Redução importante do apetite e da ingestão pode elevar o risco de inadequação de nutrientes, tornando relevantes qualidade, variedade, monitoramento e ajustes individualizados. | “Nutritional deficiencies”, pp. 346–347; “Prevention and mitigation of nutrient deficiencies”, pp. 352–353 | Usar limites regulatórios de rotulagem para composição declarada e metas configuráveis para o resto; não completar número ausente. | `VALIDADO EM CONTEÚDO` |
| `AJCN-GLP1-05` | A publicação favorece alimentos densos em nutrientes e minimamente processados e descreve refeições pequenas e regulares, líquidos adequados e flexibilidade como estratégias gerais. | “Prevention and mitigation of nutrient deficiencies”, pp. 352–353, Tabela 6 | Avaliar o prato como conjunto, com atenções qualitativas; densidade nutricional e grau de processamento ficam fora da v1. | `VALIDADO EM CONTEÚDO` |
| `AJCN-GLP1-06` | Proteína suficiente é uma prioridade potencial, porém o método para definir a meta varia; peso atual pode superestimar necessidades e não há consenso sobre a melhor base. | “Preservation of muscle and bone mass”, pp. 353–354 | Meta configurável em gramas por dia, definida por profissional e usuário. Referências citadas na validação: 0,8 g/kg/dia geral, 1,2–1,6 g/kg/dia em perda ativa, 80–120 g/dia como alternativa prática. O app não calcula a meta. | `VALIDADO EM CONTEÚDO` |
| `AJCN-GLP1-07` | Aumentar proteína isoladamente pode não preservar massa muscular; treino estruturado de força é parte importante da estratégia, adaptado à capacidade individual. | “Preservation of muscle and bone mass”, pp. 353–354 | Ressalva obrigatória de exercício de força sempre que a mensagem mencionar meta de proteína; proibido afirmar preservação muscular. | `VALIDADO EM CONTEÚDO` |
| `AJCN-GLP1-08` | Acompanhamento deve reavaliar ingestão, hidratação, estado nutricional, tolerância e resposta ao tratamento; fotos e registros alimentares são citados como possíveis ferramentas. | “Prevention and mitigation of nutrient deficiencies”, p. 353 | Histórico local e fotos efêmeras podem apoiar autorregistro, sempre com correção manual e sem diagnóstico automático. | `DECIDIDO` |
| `AJCN-GLP1-09` | Plataformas digitais podem apoiar rastreamento, educação e comunicação, mas enfrentam limitações de acesso, alfabetização, confiança e evidência; mais pesquisa é necessária. | “Telehealth and digital platforms”, pp. 356–357, Tabela 7 | Tratar alertas automáticos e recomendações por IA como assistivos e incertos, não como cuidado clínico comprovado. | `DECIDIDO` |
| `AJCN-GLP1-10` | O advisory resume que efeitos gastrointestinais, inadequação nutricional e perda muscular/óssea exigem cuidado nutricional e comportamental antes, durante e depois do tratamento. | “Summary Takeaway Messages”, p. 360 | O núcleo do produto deve apoiar decisões alimentares rastreáveis e encaminhar o que exige cuidado profissional, sem comentar medicação. | `DECIDIDO` |

### Validação recebida

A rodada 1 está registrada em [`VALIDACAO_CLINICA_2026-08-18.md`](VALIDACAO_CLINICA_2026-08-18.md)
(`VAL-GLP1-R1`) e transformada em [`RULE_PACK_GLP1.md`](RULE_PACK_GLP1.md) (`glp1-rules-v1`).

### O que continua bloqueado

- ativação do rule pack em runtime, até o registro formal: nome, CRM ou CRN, data, escopo validado e
  não validado, autorização de citação e aprovação da redação final transformada;
- limite numérico para porção ou volume da refeição;
- meta de hidratação com valor padrão;
- densidade nutricional como índice e grau de processamento inferido por imagem;
- qualquer cálculo de meta a partir de peso corporal;
- atribuição de causa a um alimento específico para um sintoma relatado.

Uma transcrição clínica não deve ser convertida silenciosamente em regra: o profissional precisa
revisar a transformação final, e cada versão do pack exige nova aprovação registrada.

## 2.1 Fonte regulatória

### REG-ANVISA-001 — Rotulagem nutricional frontal

- **Escopo:** critério "ALTO EM" para alimento **embalado**, avaliado por 100 g ou 100 ml de produto.
- **Normas:** RDC 429/2020 e IN 75/2020, em vigor desde outubro de 2022.
- **Limites usados:** açúcar adicionado ≥ 15 g/100 g ou ≥ 7,5 g/100 ml; gordura saturada ≥ 6 g/100 g
  ou ≥ 3 g/100 ml; sódio ≥ 600 mg/100 g ou ≥ 300 mg/100 ml.
- **Referências consultadas:** [Sociedade Brasileira de Diabetes](https://diabetes.org.br/rotulos-o-que-mudou/);
  [reportagem citando a Anvisa](https://www.cnnbrasil.com.br/saude/como-a-anvisa-determina-se-um-alimento-e-considerado-alto-em-sodio/).
- **O que não sustenta:** aplicação a prato estimado por foto, veredito individual, ou conclusão
  clínica. É critério de rotulagem, não recomendação personalizada.
- **Estado:** `DECIDIDO` para composição declarada; proibido fora desse escopo.

## 2.2 Projeções de mercado

Usadas apenas como contexto de oportunidade nos artefatos de entrega, sempre atribuídas e nunca como
receita esperada do produto.

| Projeção | Fonte | Ressalva |
|---|---|---|
| Vendas de GLP-1 na casa de US$ 100 bilhões até 2030 | [McKinsey](https://www.mckinsey.com/featured-insights/themes/glp1s-are-changing-obesity-care-what-comes-next) | estimativas anteriores de US$ 150–200 bilhões foram revisadas para baixo ([análise Reuters](https://www.investing.com/news/stock-market-news/analysisobesity-market-sales-potential-tightens-as-novo-and-lilly-enter-new-era-4478211)) |
| Saúde digital para obesidade em torno de US$ 247 bilhões em 2030 | [The Business Research Company](https://www.thebusinessresearchcompany.com/report/digital-health-for-obesity-global-market-report) | outras casas projetam valores e horizontes divergentes |

Conteúdo das fontes externas foi parafraseado para conformidade com restrições de licenciamento.

## 3. Produto e código de barras

- **Candidata externa:** [Open Food Facts](https://world.openfoodfacts.org/), com licença e termos a conferir antes de distribuição.
- **Estado atual:** catálogo local demonstrativo.
- **Fallback obrigatório:** dado não localizado; nunca inventar composição para um EAN desconhecido.

## 4. Composição alimentar

- **Candidata brasileira:** TBCA.
- **Candidata complementar:** USDA FoodData Central.
- **Estado:** precedência, versão, licença, normalização por 100 g/ml, medidas caseiras e fatores de conversão ainda precisam ser definidos e validados antes de alimentar estimativas de prato.
- **Tabela própria:** deve preservar fonte, versão, transformação e unidade de cada valor; “normalizada” não significa clinicamente validada.

## 5. Perfil

- O perfil é entrada fornecida e corrigível pelo usuário/profissional; não é fonte de evidência clínica.
- Builds de produção não devem iniciar com uma pessoa fictícia nem metas numéricas demonstrativas.
- Metas configuradas são parâmetros do usuário/profissional, não recomendações geradas pelo app.

## 6. Dataset de visão

A estratégia é construir e versionar um dataset próprio em primeira pessoa, com:

- ângulos e distâncias reais;
- iluminação de supermercado, restaurante e casa;
- oclusões, pratos mistos e rótulos curvos/reflexivos;
- separação por pessoa/cena entre treino, validação e teste;
- rótulos, confiança e proveniência auditáveis.

Desempenho visual valida somente identificação/estimativa dentro do recorte medido. Não valida orientação clínica, ausência de alérgeno nem macro exato por foto.

> Conteúdo das fontes externas foi parafraseado para conformidade com restrições de licenciamento.