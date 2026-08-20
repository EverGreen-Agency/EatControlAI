# Benchmark competitivo — A6

Versão: 1.0
Data: 18/08/2026
Escopo: alternativas reais que a pessoa usaria no lugar do Eat Control, e o que assumimos ou não a
partir delas.

> Os links registram posicionamento público de fornecedores, não validação independente. Onde há
> evidência científica, ela está citada separadamente.
> Conteúdo das fontes externas foi parafraseado para conformidade com restrições de licenciamento.

## 1. Categorias de alternativa

| Categoria | Exemplos | Proposta central |
|---|---|---|
| Diário alimentar com busca | MyFitnessPal e similares | registrar consumo e acompanhar metas |
| Foto que estima nutrientes | [Foodvisor](https://play.google.com/store/apps/details?id=io.foodvisor.foodvisor), [Cal AI](https://www.calai.app/), [BiteSnap](http://www.getbitesnap.com/) | fotografar a refeição e obter valores |
| Scanner de rótulo e código de barras | leitores de EAN, bases abertas como [Open Food Facts](https://world.openfoodfacts.org/) | identificar produto e ler composição |
| Assistente genérico em óculos | assistentes de voz embarcados | responder perguntas amplas |
| Cuidado profissional | nutricionista e endocrinologista | orientação clínica individual |

O concorrente mais relevante para o nosso recorte é a categoria de foto, porque foi ela que criou a
expectativa de "fotografar e registrar" ([MyFitnessPal Meal Scan](https://blog.myfitnesspal.com/watch/log-meals-like-magic-with-meal-scan/)).

## 2. Comparação por dimensão

| Dimensão | Diário manual | Foto que estima macros | Scanner de rótulo | Assistente genérico | Eat Control AI |
|---|---|---|---|---|---|
| Funciona sem usar as mãos | não | não | não | parcial | sim, a partir do campo de visão |
| Resposta por áudio curta | não | não | não | sim | sim |
| Opera sem rede no caminho crítico | parcial | normalmente não | parcial | normalmente não | sim |
| Aplica restrição pessoal cadastrada | parcial | raramente | parcial | não | sim, com severidade e política |
| Mostra origem de cada informação | não | raramente | parcial | não | sim, por precedência |
| Admite não saber | não | raramente | parcial | raramente | sim, estado explícito |
| Promete macro exato de prato misto | não | sim | não | às vezes | não, por decisão |
| Imagem sai do aparelho | não se aplica | frequentemente sim | varia | sim | não |
| Recorte GLP-1 | não | não | não | não | sim, como público inicial |

## 3. O que adotamos das soluções de foto

1. fricção baixa: uma ação e um resultado;
2. entrada multimodal, com foto e código de barras;
3. revisão do que foi reconhecido antes de registrar;
4. histórico que acumula o dia.

## 4. O que recusamos, e por quê

A promessa de que uma foto única mede todos os macronutrientes não se sustenta na evidência
disponível:

- avaliação de aplicativos de avaliação dietética por imagem encontrou superestimação de calorias e
  subestimação de carboidratos frente à referência, com desempenho variável entre nutrientes
  ([Journal of the Academy of Nutrition and Dietetics, 2024](https://www.jandonline.org/article/S2212-2672(24)00610-5/fulltext));
- comparação de métodos automáticos por imagem relatou faixa ampla de erro relativo para calorias e
  volume, melhor em alimentos simples que em pratos compostos
  ([PubMed 38060823](https://pubmed.ncbi.nlm.nih.gov/38060823/));
- avaliadores humanos também erram com frequência ao estimar calorias por foto
  ([PubMed 30401671](https://pubmed.ncbi.nlm.nih.gov/30401671/));
- identificação do alimento e estimativa da ingestão são problemas distintos e exigem avaliação
  própria ([estudo comparativo de plataformas](https://pmc.ncbi.nlm.nih.gov/articles/PMC7752530/));
- validações de aplicativos em uso livre indicam necessidade de melhoria antes de tratar o valor como
  medida confiável ([PubMed 38888538](https://pubmed.ncbi.nlm.nih.gov/38888538/)).

Consequência de produto: um macro só existe com **item confirmado, quantidade confirmada e fonte de
composição versionada**. Sem os três, o app informa que não sabe.

Para o público em GLP-1, essa escolha é mais que estética. A pessoa está tomando decisão sobre
tolerância, proteína e volume; um número plausível e errado desloca a decisão com aparência de
precisão.

## 5. Onde perdemos hoje

Honestidade competitiva também é registrar desvantagens:

| Desvantagem | Situação |
|---|---|
| Base de produtos | catálogo local demonstrativo; concorrentes têm bases grandes |
| Cálculo de macro do prato | não entregamos; concorrentes entregam, ainda que com erro |
| Amplitude de alimentos reconhecidos | doze classes visuais fechadas |
| Maturidade de mercado | protótipo funcional, sem base de usuários |
| Métricas de campo | latência, bateria e precisão ainda não medidas em hardware |

## 6. Estratégia de resposta

1. Cálculo semiautomático: confirmar item e quantidade em medidas caseiras, com composição
   versionada, para entregar número auditável sem fingir medição por foto.
2. Base de composição brasileira com versão, licença e fatores de conversão registrados.
3. Ampliar classes visuais só após validação com fotos reais brasileiras.
4. Publicar métricas medidas, separadas das metas.
5. Manter o diferencial que nenhuma dessas categorias oferece: decisão personalizada, auditável e por
   áudio, no instante da escolha.

## 7. Dois concorrentes para citar na entrega

Para a comparação exigida em A6, a escolha recomendada é:

| Concorrente | Por que é o comparável certo |
|---|---|
| Aplicativo de foto que estima macros | representa a expectativa de UX que o público já conhece |
| Scanner de rótulo com base de produtos | representa a alternativa mais próxima do nosso caminho de rótulo e código de barras |

O substituto mais honesto de todos continua sendo o celular na mão: funciona, mas exige parar, digitar
e confiar em um número sem proveniência.
