# Estratégia de cardápio, prato e visão computacional

Status: `IMPLEMENTADO LOCALMENTE` para pipelines, contratos e guardrails; `PENDENTE DE VALIDAÇÃO` para precisão visual, composição, latência e hardware
Data: 18/08/2026

Implementação atual: MENU usa OCR + `MenuParser` próprio; PLATE usa `MlKitImageLabelingProvider` bundled/offline, gate experimental `0,65`, classes fechadas e confirmação antes do histórico. Esse estado não valida qualidade no Ray-Ban nem autoriza cálculo de macros sem composição e quantidade auditáveis.

## 1. Decisão executiva

Aplicativos de nutrição por foto tornaram desejável o fluxo “fotografar e registrar”. O Eat Control deve oferecer fricção comparável, mas não copiar a promessa de que uma imagem única mede automaticamente todos os macros.

A v1 implementa dois pipelines distintos:

- **Cardápio:** OCR local + parser de opções + revisão factual.
- **Prato:** candidatos visuais locais + confirmação de componentes + registro assistido; quantidade permanece desconhecida nesta entrega.

Um macro só existe quando há três elementos auditáveis: **item confirmado + quantidade confirmada + fonte de composição**. Se faltar um deles, o valor fica desconhecido; não será preenchido por plausibilidade.

## 2. O que o benchmark competitivo ensina

| Produto/artefato | Expectativa de UX observada | O que o Eat Control adota | O que não assume |
|---|---|---|---|
| [MyFitnessPal Meal Scan](https://blog.myfitnesspal.com/watch/log-meals-like-magic-with-meal-scan/) | reduzir busca manual ao registrar uma refeição | captura rápida e revisão | precisão transferível ao nosso recorte |
| [Foodvisor](https://play.google.com/store/apps/details?id=io.foodvisor.foodvisor) | foto/barcode com informação nutricional | entrada multimodal e diário | que porção ou prato misto estejam corretos |
| [Cal AI](https://www.calai.app/) | macros calculados a partir de foto | resposta imediata quando há dados | macro exato sem fonte/quantidade |
| [BiteSnap](http://www.getbitesnap.com/) | reconhecimento visual para registro | candidatos editáveis | reconhecimento como evidência clínica |

Esses links registram posicionamento de fornecedores, não validação independente. Uma comparação publicada de plataformas mostra que identificação de alimentos e estimativa dietética são problemas distintos e precisam de avaliação própria ([estudo comparativo](https://pmc.ncbi.nlm.nih.gov/articles/PMC7752530/)).

Conteúdo externo foi parafraseado para conformidade com restrições de licenciamento.

## 3. Modelo de evidência

Cada campo deve carregar origem e, quando aplicável, confiança:

1. **Declarado:** tabela nutricional, embalagem ou catálogo versionado.
2. **Observado:** texto de cardápio reconhecido por OCR.
3. **Inferido:** candidato produzido por visão; nunca prova ingrediente oculto ou ausência.
4. **Confirmado:** escolha/correção explícita do usuário.
5. **Calculado:** transformação reproduzível de composição por unidade × quantidade confirmada.

A confirmação muda a autoria do dado, não transforma uma composição sem fonte em verdade. Valores inferidos devem permanecer distinguíveis no histórico.

## 4. Cardápio (`MENU`)

### 4.1 Por que não é tabela nutricional

Cardápios variam em layout, seção, descrição e preço; normalmente não declaram porção ou nutrientes. Reutilizar cegamente o parser de tabela faria números monetários parecerem quantidades nutricionais e perderia a relação entre nome e descrição.

### 4.2 Pipeline da v1

1. Captura explícita por telefone, mock ou DAT.
2. OCR local preserva texto bruto e blocos/linhas.
3. `MenuParser`:
   - normaliza espaços sem apagar o texto original;
   - identifica cabeçalhos prováveis;
   - agrupa nome e descrição;
   - reconhece preço somente como preço;
   - descarta linhas vazias/ruído sem fabricar conteúdo.
4. UI mostra opções revisáveis.
5. Usuário seleciona e confirma uma opção; edição textual é evolução futura.
6. Resultado factual explica quais palavras foram observadas e quais informações faltam.

### 4.3 Saída permitida

- nome/descrição/preço observados;
- termos explícitos como “frito”, “grelhado” ou ingredientes escritos;
- pergunta de confirmação quando o texto é ambíguo;
- `INFORMAÇÃO_INSUFICIENTE` quando OCR/estrutura não sustentam uma opção.

Não é permitido inferir receita completa, porção, macro, ausência de alérgeno ou “melhor opção clínica”. Pesos para comparar opções permanecem bloqueados em `DATA_SOURCES.md` até validação profissional.

## 5. Prato (`PLATE`)

### 5.1 Por que uma foto não mede o prato

Em um prato não existe declaração do fabricante, somente pixels. Uma vista monocular perde profundidade e sofre oclusão, perspectiva e mistura de ingredientes. Métodos de volume normalmente adicionam referência de escala, múltiplas vistas ou reconstrução; exemplos incluem [estimativa com referente circular](https://pmc.ncbi.nlm.nih.gov/articles/PMC3328298/) e [reconstrução monocular em escala](https://arxiv.org/html/2601.20051).

Assim:

- imagem não comprova ausência de leite, glúten ou outro ingrediente;
- classe visual não define receita nem ingrediente oculto;
- tamanho aparente não define gramas;
- um número plausível e errado é pior que “não informado”.

**Atualização após validação clínica (`VAL-GLP1-R1`, 18/08/2026).** A identificação visual passa a ser
escopo central, e não apenas um candidato descartável. O que a imagem **pode** sustentar:

- identificar alimentos e ingredientes visualmente identificáveis;
- apontar preparo visível, como fritura e molho cremoso;
- gerar análise qualitativa do prato como conjunto;
- disparar atenção quando um item identificado corresponde a restrição, meta ou regra pessoal.

O que continua proibido: garantir presença ou ausência de ingrediente oculto ou alergênico, afirmar
receita completa e estimar quantidade em gramas. Nesses casos, o app sinaliza incerteza e pergunta, ou
oferece ler o rótulo e a descrição do item.

### 5.2 Contrato do provider visual

O provider local retorna uma lista de candidatos:

- rótulo normalizado;
- score bruto do modelo;
- id/versão do provider;
- classe fechada mapeada, quando houver;
- limitações conhecidas.

Ele não retorna decisão clínica. Um candidato abaixo do gate experimental é omitido ou apresentado como baixa confiança. O gate é parâmetro de engenharia a ser benchmarkado, não threshold clínico.

### 5.3 Classes fechadas iniciais

A v1 pode mapear somente estas classes visuais amplas:

`arroz`, `feijão`, `frango`, `carne`, `peixe`, `massa`, `salada`, `legumes`, `ovo`, `fritura`, `queijo`, `sobremesa`.

A classe descreve aparência geral; não garante ingrediente, receita ou composição. Rótulos fora do dicionário permanecem candidatos não suportados. A lista deve ser validada em fotos reais brasileiras antes de qualquer alegação de cobertura.

### 5.4 Fluxo assistido da v1

1. Captura sob demanda.
2. Provider local retorna candidatos e scores.
3. UI mostra os candidatos e pede confirmação ou correção por seleção na taxonomia fechada.
4. Usuário escolhe um ou mais componentes; sem seleção confirmada, nada é registrado.
5. Nesta entrega, quantidade/medida permanece desconhecida; a foto não estima volume.
6. Somente uma tabela local com fonte/versionamento e uma quantidade confirmada poderão converter o item confirmado em nutrientes no futuro.
7. Antes de confirmação, nada entra nos totais diários.
8. O resultado mantém marcador de estimativa e proveniência visual.

Enquanto a fonte de composição e os fatores de medidas caseiras não estiverem fechados, a primeira entrega registra os componentes confirmados e informa que macros do prato não foram calculados.

A análise qualitativa aprovada em `VAL-GLP1-R1` não depende dessa fonte: proteína presente, vegetais
presentes, fritura, molho cremoso e comparação da porção com o plano cadastrado são achados
observacionais, especificados em [`RULE_PACK_GLP1.md`](RULE_PACK_GLP1.md). Volume só é tratado como
atenção quando existe porção prevista no plano; sem plano, o app descreve e não julga.

### 5.5 Guardrails obrigatórios

- inferência visual pode **alertar** sobre item identificado que conflita com restrição, meta ou regra
  pessoal, mas nunca **liberar** uma restrição crítica;
- ausência de alérgeno nunca é afirmada pela imagem;
- ingrediente oculto e composição de molho exigem rótulo, descrição ou confirmação do usuário;
- baixa confiança ou classe fora do recorte leva a informação insuficiente;
- confirmação é obrigatória antes do histórico;
- macro estimado nunca se mistura silenciosamente com valor declarado de rótulo;
- captura e bitmap são liberados ao fechar/substituir a interação;
- nenhum frame é enviado à nuvem no caminho crítico.

## 6. Escolha de modelo

### V1 — provider local pronto

Usar um provider de rotulagem on-device já empacotado/gerenciado pelo stack Android, atrás de uma interface substituível. Vantagens: entrega e benchmark rápidos. Limitações: classes genéricas, vocabulário possivelmente não brasileiro e score sem calibração para o domínio.

### V2 — classificador próprio LiteRT

Só avançar após:

1. dataset versionado de refeições brasileiras em primeira pessoa;
2. separação por pessoa/cena entre treino, validação e teste;
3. taxonomia e política de itens mistos;
4. baseline da v1 registrada;
5. teste de MobileNet/EfficientNet-Lite ou equivalente;
6. quantização int8 comparada com float em precisão, latência e tamanho;
7. análise de erro por classe e iluminação.

### Fora do recorte atual

Segmentação + volume, reconstrução 3D e modelo multimodal em nuvem não entram no caminho crítico. O primeiro exige protocolo de captura/escala; o último contradiz retenção zero e processamento local.

## 7. Dados de composição e porção

TBCA é candidata principal para alimentos brasileiros e USDA FoodData Central pode ser complementar, mas nada entra no APK antes de definir:

- versão/licença;
- alimento e código de origem;
- unidade por 100 g/ml;
- transformação aplicada;
- fator de medida caseira e sua fonte;
- política para receita/prato misto.

Uma medida escolhida pelo usuário não é automaticamente precisa; é uma entrada confirmada e corrigível. Sem fator auditável, manter apenas o registro descritivo.

## 8. Critérios de aceite

### MENU

- [ ] OCR sobre imagens representativas reais gera ao menos uma opção revisável; parser já está coberto por fixtures textuais.
- [x] Preços não são parseados como nutrientes/porções.
- [x] Texto observado e opção derivada mantêm proveniência no resultado assistido.
- [x] Entrada insuficiente não fabrica composição, porção ou macro.
- [x] Confirmação explícita é exigida antes do histórico.

### PLATE

- [x] Provider local retorna identificação/versionamento técnico e score bruto.
- [x] Classes fora do recorte não são forçadas para uma classe conhecida.
- [x] Usuário precisa confirmar/corrigir antes do registro.
- [x] Nenhum macro surge sem fonte de composição e quantidade.
- [x] Nenhum caminho visual afirma ausência ou segurança.
- [x] Fixtures JVM cobrem gate, baixa confiança, desconhecidos, sinônimos, limites e provider indisponível.
- [ ] Latência, top-k, enquadramento e taxa de correção são medidos no aparelho-alvo.

## 9. Métricas e protocolo

- top-1/top-3 por classe visual;
- cobertura: percentual de cenas que pertencem à taxonomia;
- taxa de aceitação/correção/rejeição pelo usuário;
- precisão estrutural do parser de menu por campo;
- p50/p90/p95 de captura, OCR/provider e pipeline total;
- bateria/temperatura em sessões curtas;
- matriz telefone × DAT × iluminação × distância;
- auditoria de retenção de imagem e comportamento sem rede.

Resultados de mock ou fixture validam somente lógica. Ray-Ban físico é obrigatório para validar enquadramento, rotação, Bluetooth, áudio, latência e qualidade de frame.

## 10. Dependências ainda bloqueadas

1. Fonte/versionamento da composição e fatores de medida caseira.
2. Benchmark e gate de score do provider visual.
3. Validação das classes com fotos reais brasileiras.
4. Critérios clínicos/pesos de comparação aprovados.
5. Hardware Ray-Ban + Meta AI + conta/canal do parceiro.

Claims clínicos e seus estados permanecem somente em [`DATA_SOURCES.md`](DATA_SOURCES.md); esta estratégia não os transforma em regra.
