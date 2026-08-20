# Eat Control AI — Documento de entrega A1–A7

Versão: 1.0
Data: 18/08/2026
Equipe: Eat Control AI
Aplicativo: Android nativo, `com.eatcontrolai.app`, `minSdk 33`, `targetSdk 36`
Integração: Meta Wearables Device Access Toolkit (DAT) 0.9.0

> A1 a A7 são **seções deste documento**, não formatos de papel. Cada uma responde a um item da
> entrega. Podem ser colocadas em um único arquivo, na ordem apresentada.

## Legenda de estados

| Estado | Significado |
|---|---|
| `IMPLEMENTADO` | existe no código e é exercitado por teste automatizado |
| `VALIDADO` | executado com evidência reproduzível registrada |
| `NÃO VALIDADO EM HARDWARE` | implementado, ainda sem ensaio no Ray-Ban físico |
| `BLOQUEADO` | depende de terceiro, hardware ou validação profissional |

Nada neste documento apresenta meta como resultado. Onde não há medição, o texto diz que não há.

---

## A1 — Problema

A decisão alimentar acontece longe da consulta: no corredor do mercado, diante de um cardápio ou com
o prato já servido. É nesse instante que a pessoa precisa de informação, e é exatamente nele que
consultar aplicativo, buscar produto e digitar dados é inviável.

O problema específico que atacamos é a **fricção entre a orientação profissional e o momento da
escolha**. Três consequências observadas nas entrevistas exploratórias:

1. a pessoa decide por memória ou aparência, não pelo que está declarado no rótulo;
2. a informação existe na embalagem, mas está em letra pequena, invertida ou contraditória, como um
   produto anunciado como "zero lactose" cujo verso declara que contém leite;
3. o registro do que foi consumido acontece depois, ou não acontece.

O recorte é deliberadamente estreito: **responder a uma pergunta alimentar em segundos, por áudio,
sem tirar o celular do bolso e sem enviar imagem para a nuvem**.

Não tratamos o problema como "contar calorias". Tratamos como **decidir com evidência rastreável**.

---

## A2 — Público

**Público inicial:** pessoas em tratamento com análogos de GLP-1.

Motivadores desse recorte, relatados nas entrevistas:

- saciedade precoce, com refeições de volume pequeno;
- desconforto após determinadas refeições;
- dificuldade de decidir no momento da compra ou do pedido;
- preocupação com perda de massa magra.

**Expansão natural:** pessoas com restrições alimentares — alergia, intolerância, doença celíaca — e
cuidadores. A arquitetura já suporta esse grupo, porque o motor de decisão opera sobre restrições
configuradas, não sobre um diagnóstico específico.

**Limite ético explícito:** o público é clínico, o produto não é clínico. O aplicativo não
diagnostica, não prescreve, não comenta medicação e não usa a palavra "seguro". A literatura que
fundamenta o recorte está registrada em `docs/DATA_SOURCES.md` como advisory populacional
(`CLIN-AJCN-001`, claims `AJCN-GLP1-01` a `AJCN-GLP1-10`), e advisory populacional não é prescrição
individual.

---

## A3 — Fluxo principal: rótulo e código de barras

### Fluxo executado hoje

1. A pessoa olha para o produto e aciona a análise por voz ou por toque.
2. Um sinal sonoro confirma a captura antes de qualquer processamento.
3. O frame é capturado sob demanda: óculos, câmera do celular ou fonte simulada.
4. A cascata roteia por custo: leitor de código de barras primeiro, OCR somente se necessário.
5. As afirmações do rótulo são normalizadas e classificadas por hierarquia de evidência.
6. O motor determinístico compara com o perfil e devolve um de quatro estados.
7. A resposta é falada em frase curta e o registro entra no histórico local.

### Hierarquia de evidência

| Precedência | Origem | Exemplo |
|---|---|---|
| 2 | rótulo declarado | "contém leite" no verso |
| 3 | base estruturada por EAN | composição do catálogo local |
| 4 | confirmação do usuário | resposta a uma pergunta do app |
| 5 | texto bruto de OCR | linha reconhecida sem estrutura |

Inferência visual nunca ocupa o topo dessa hierarquia e nunca prova ausência de ingrediente.

### Quatro estados possíveis

`COMPATÍVEL` · `INCOMPATÍVEL` · `PRECISA DE CONFIRMAÇÃO` · `INFORMAÇÃO INSUFICIENTE`

A frase positiva é **"Não encontrei conflito nas evidências disponíveis."** Nunca "é seguro".

### Demonstração reproduzível

| Passo | Resultado esperado |
|---|---|
| Rótulo "iogurte zero lactose" | `INCOMPATÍVEL`: o verso declara leite, apesar do apelo frontal |
| Desligar a restrição de leite e repetir | mesma imagem, decisão diferente, porque a regra é sobre o perfil |
| Rótulo "barra de proteína" | pergunta de confirmação; a resposta recalcula a decisão |
| Código de barras conhecido | composição vem da base estruturada, acima de qualquer OCR |
| Comando falado "posso comer isso?" | a análise dispara sozinha e responde por áudio |

**Estado:** `IMPLEMENTADO` e coberto por testes.
**Limite:** as regras específicas de GLP-1 — gordura, porção, preparo, desconforto — estão
`BLOQUEADO` até validação profissional registrada. O que existe hoje é o motor de restrições, não um
rule pack clínico.

---

## A4 — Fluxos de exceção

O produto foi construído em torno da ideia de que **não saber precisa ser um resultado legítimo**.

| Situação | Comportamento atual | Estado |
|---|---|---|
| Rótulo ilegível | declara informação insuficiente, sem adivinhar | `IMPLEMENTADO` |
| Código de barras lido, produto fora do catálogo | usa o que está escrito na embalagem; não inventa composição | `IMPLEMENTADO` |
| Alérgeno mencionado de forma ambígua | pergunta ao usuário e recalcula com a resposta | `IMPLEMENTADO` |
| Cardápio sem porção ou nutriente declarado | mostra o que foi observado e exige confirmação antes de registrar | `IMPLEMENTADO` |
| Prato reconhecido com baixa confiança | trata como palpite, pede confirmação e não calcula macros | `IMPLEMENTADO` |
| Voz não reconhecida | pede repetição e mantém o caminho por toque | `IMPLEMENTADO` |
| Reconhecimento offline indisponível | informa a limitação em vez de usar serviço de rede | `IMPLEMENTADO` |
| Entrevista nutricional completa para produto desconhecido | perguntas ainda limitadas a alérgenos e confirmação de itens | `PENDENTE` |

### Modo cardápio

OCR local mais um parser próprio, que separa seção, nome, descrição e preço. Preço nunca é
interpretado como nutriente ou porção. Nada entra no histórico antes da confirmação.

### Modo prato

Um provedor de rotulagem de imagem local e offline sugere candidatos dentro de doze classes fechadas
(arroz, feijão, frango, carne, peixe, massa, salada, legumes, ovo, fritura, queijo, sobremesa), com
limiar experimental de 0,65. Rótulos fora do dicionário não são forçados para uma classe conhecida.

O registro guarda os componentes confirmados e marca que houve inferência visual. **Macronutrientes
do prato não são calculados**, porque isso exigiria três elementos auditáveis:

> item confirmado + quantidade confirmada + fonte de composição versionada

Sem os três, o valor permanece desconhecido. Essa é uma decisão de produto, detalhada em A6.

---

## A5 — Arquitetura e decisões técnicas

### Camadas

```text
Óculos / Câmera do celular / Fonte simulada
        ↓  captura sob demanda
Gateway de captura (interface única)
        ↓
Percepção: OCR · código de barras · rotulagem de imagem · STT
        ↓
Agregador de evidências (tipo, origem, confiança, versão do modelo)
        ↓
Motor determinístico de decisão
        ↓
Composição da resposta → TTS
        ↓
Métricas por etapa + histórico local
```

### Decisões estruturais

| Decisão | Por quê |
|---|---|
| Android nativo com Compose | acesso direto a câmera, Bluetooth, TTS/STT e ao DAT |
| Local-first, sem nuvem no caminho crítico | privacidade, latência e funcionamento sem rede |
| Regras determinísticas, não LLM, na decisão | resposta auditável e reproduzível; sem alucinação em decisão alimentar |
| Providers atrás de interface | trocar OCR, visão, STT ou TTS sem tocar na UI |
| Cascata por custo | barcode antes de OCR economiza latência e bateria |
| Sessão de captura curta | o stream técnico abre, obtém o frame e fecha em seguida |
| Persistência local em DataStore | histórico no aparelho, com backup em nuvem desativado |

### Modelos e bibliotecas

| Função | Escolha | Onde executa |
|---|---|---|
| OCR | ML Kit Text Recognition | no aparelho |
| Código de barras | ML Kit Barcode Scanning | no aparelho |
| Candidatos visuais | ML Kit Image Labeling, modelo embarcado 17.0.9 | no aparelho, offline |
| Fala para texto | reconhecedor on-device do Android | no aparelho |
| Texto para fala | TTS do Android com voz local | no aparelho |
| Óculos | `mwdat-core`, `mwdat-camera`, `mwdat-mockdevice` em debug | aparelho + óculos |

### Qualidade verificada em 18/08/2026

| Item | Resultado |
|---|---|
| Suíte JVM | 109 testes, 17 suítes, zero falhas, erros ou skips |
| Cobertura do domínio determinístico | 99,5% de linhas (652/655), 81,3% de ramos (292/359) |
| Cobertura total do módulo | 81,2% de linhas (1123/1383), 67,7% de ramos (389/575) |
| Gate no build | `check` reprova queda de cobertura, não apenas teste vermelho |
| Compilação, lint e verificação | aprovados |
| APK debug | `168,63 MiB`, assinatura v2 válida, certificado conferido |

O gate alto é aplicado onde o erro machuca: parsing, evidência, decisão, roteamento, cardápio e
prato. Adaptadores de hardware ficam fora porque só executam de verdade em aparelho.

---

## A6 — Comparação com alternativas

### Panorama

| Alternativa | O que resolve bem | Onde falha para este público |
|---|---|---|
| Diários alimentares manuais | histórico e metas | exigem digitação e memória; não ajudam no instante da decisão |
| Leitores de rótulo e scanners de código de barras | identificação rápida de produto | dependem de o produto existir na base; exigem celular na mão |
| Aplicativos de foto que estimam macros | fricção baixa e sensação de automatismo | precisão dependente de porção e receita; ver evidência abaixo |
| Assistente genérico do celular | conversa ampla | não aplica restrição pessoal nem cita origem da informação |
| Consulta profissional | orientação clínica real | não está presente no supermercado ou no restaurante |

### Por que não prometemos macro automático por foto

Estudos independentes mostram que estimativa nutricional por imagem ainda tem erro relevante:

- uma avaliação de aplicativos de avaliação dietética por imagem encontrou superestimação de
  calorias e subestimação de carboidratos frente à referência
  ([Journal of the Academy of Nutrition and Dietetics, 2024](https://www.jandonline.org/article/S2212-2672(24)00610-5/fulltext));
- uma comparação de métodos automáticos relatou faixas amplas de erro relativo para calorias e
  volume, com desempenho melhor em imagens de alimentos simples
  ([PubMed 38060823](https://pubmed.ncbi.nlm.nih.gov/38060823/));
- mesmo avaliadores humanos acertam pouco ao estimar calorias por foto
  ([PubMed 30401671](https://pubmed.ncbi.nlm.nih.gov/30401671/));
- identificação de alimento e estimativa dietética são problemas distintos, com avaliação própria
  ([estudo comparativo de plataformas](https://pmc.ncbi.nlm.nih.gov/articles/PMC7752530/)).

Conteúdo das fontes externas foi parafraseado para conformidade com restrições de licenciamento.

### Nosso posicionamento

Adotamos a fricção baixa dessas soluções e **recusamos a promessa de precisão que elas não
sustentam**. Onde um concorrente exibe um número plausível, o Eat Control exibe o que observou, o que
falta e de onde veio cada informação. Para o público em GLP-1, um número errado com aparência de
precisão é pior que um "não sei" honesto.

### Diferenciais concretos

1. captura sem as mãos, a partir do campo de visão, com resposta por áudio;
2. hierarquia de evidência explícita, com proveniência por campo;
3. quatro estados, incluindo insuficiência declarada;
4. confirmação do usuário como evidência de peso alto que recalcula a decisão;
5. funcionamento no aparelho, sem enviar imagem para servidor;
6. cobertura de teste alta na camada que decide.

---

## A7 — IA, câmera, áudio, privacidade e bateria

### Uso de IA

| Camada | Tipo | Papel |
|---|---|---|
| OCR | modelo no aparelho | ler texto de rótulo, tabela e cardápio |
| Código de barras | decodificação no aparelho | identificar o produto |
| Rotulagem de imagem | modelo embarcado | sugerir candidatos de componentes do prato |
| Fala para texto | modelo no aparelho | acionar a interação por voz |
| Interpretação do comando | parser determinístico | escolher a trilha sem depender de rede |
| Decisão | regras determinísticas | manter a resposta auditável |

A IA percebe; ela não decide sozinha. A decisão é determinística de propósito.

### Câmera

Captura acontece somente após ação explícita. Durante a ação, a sessão abre um stream técnico
temporário para obter uma foto ou frame e encerra logo após a análise. Não há captura contínua nem em
segundo plano. A imagem permanece em memória, é processada no aparelho, não é gravada em arquivo e
não é enviada aos nossos servidores.

### Áudio

O toolkit dos óculos não entrega áudio, então microfone e alto-falantes usam os perfis Bluetooth do
Android: A2DP para saída de mídia e HFP para voz bidirecional. A rota HFP é configurada antes da
captura de voz e liberada ao final da resposta. Um sinal sonoro confirma a interação antes de
qualquer inferência.

### Privacidade

| Prática | Estado |
|---|---|
| Processamento no aparelho no caminho crítico | `IMPLEMENTADO` |
| Foto não persistida em arquivo ou banco local | `IMPLEMENTADO` |
| Frame liberado ao fechar ou substituir o resultado | `IMPLEMENTADO` |
| Backup em nuvem e transferência de aparelho desativados para os dados locais | `IMPLEMENTADO` |
| Apagar histórico de verdade | `IMPLEMENTADO` |
| Perfil começa vazio, sem persona fictícia nem meta inventada | `IMPLEMENTADO` |
| Controles sem comportamento removidos da interface | `IMPLEMENTADO` |
| Credenciais fora do repositório | `IMPLEMENTADO` |

Não há upload, conta remota nem sincronização. O que não existe não pode vazar.

### Bateria e eficiência

Decisões que reduzem consumo:

- captura por evento, sem stream permanente;
- cascata que evita OCR quando o código de barras resolve;
- modelos pequenos no aparelho, sem chamada de rede;
- sessão dos óculos aberta apenas durante a interação;
- divisão do APK por arquitetura no build de release.

**Medição:** `BLOQUEADO`. Latência por etapa já é instrumentada no código, mas os números de
latência real, bateria, temperatura e qualidade de frame exigem o Ray-Ban físico. Este documento não
apresenta esses valores porque eles ainda não foram medidos.

---

## Estado consolidado da entrega

| Capacidade | Estado |
|---|---|
| Rótulo, código de barras e modo automático | `IMPLEMENTADO` |
| Tabela nutricional, porção confirmada e total diário | `IMPLEMENTADO` |
| Onboarding, perfil e metas do usuário | `IMPLEMENTADO` |
| Cardápio assistido | `IMPLEMENTADO`, `NÃO VALIDADO EM HARDWARE` |
| Prato assistido, sem macro automático | `IMPLEMENTADO`, `NÃO VALIDADO EM HARDWARE` |
| Registro e autorização DAT | `IMPLEMENTADO`, `NÃO VALIDADO EM HARDWARE` |
| Voz offline e roteamento Bluetooth | `IMPLEMENTADO`, `NÃO VALIDADO EM HARDWARE` |
| Rule pack GLP-1 | `BLOQUEADO` por validação profissional |
| Composição e medidas para macro de prato | `BLOQUEADO` por fonte auditável |
| Métricas físicas | `BLOQUEADO` por hardware |
| Release de produção assinado | `BLOQUEADO` por identidade de assinatura |

## Roadmap imediato

1. Validar autorização, captura, áudio e encerramento no Ray-Ban do parceiro.
2. Medir latência, bateria, temperatura e qualidade de frame.
3. Obter validação clínica e transformar apenas o que for aprovado em rule pack versionado.
4. Fechar fonte de composição e fatores de medida para habilitar cálculo semiautomático do prato.
5. Criar a identidade de assinatura de release e o canal de distribuição.

## Documentos de apoio

| Documento | Conteúdo |
|---|---|
| `docs/PRD.md` | requisitos e escopo de produto |
| `docs/DATA_SOURCES.md` | proveniência clínica e claims rastreáveis |
| `docs/PEDIDO_VALIDACAO_CLINICA.md` | protocolo de validação profissional |
| `docs/ESTRATEGIA_PRATO_VISAO.md` | limites de cardápio, prato e visão |
| `docs/ESTRATEGIA_TESTES.md` | estratégia de teste e cobertura medida |
| `docs/DIAGRAMA_ARQUITETURA.md` | diagramas de arquitetura e fronteiras |
| `docs/BENCHMARK_COMPETITIVO.md` | comparação competitiva detalhada |
| `docs/ROTEIRO_VIDEO.md` | roteiro da demonstração |
| `docs/GUIA_KEYSTORE_RELEASE.md` | identidade de assinatura e custódia |
| `docs/INVENTARIO_ENTREGA_2026-08-22.md` | checklist operacional e bloqueios |
