# Estrutura do repositório e estado atual — Eat Control AI

> **O que é este documento.** Um mapa honesto do repositório em **12 de agosto de 2026**: o que
> existe, o que é real, o que é template do Android Studio e o que cada peça precisa virar.
> Serve como ponto de entrada para quem chega no projeto.
>
> Ele **não substitui** o `docs/contexto-gpt.md` (visão de produto / North Star) nem o `docs/PRD.md`.
> Ele descreve o **repositório**, não o produto.
>
> **Estado:** a vertical do rótulo está fechada ponta a ponta e o app roda. Detalhes na seção 3.5.

---

## 1. Resumo em cinco linhas

1. A **visão de produto está madura e bem escrita** — provavelmente o ativo mais forte do projeto.
2. A **documentação de engenharia ainda é esqueleto**: nomeia as coisas certas, mas sem critérios de
   aceite e sem rastreabilidade. A exceção é o motor de decisão, que agora tem spec executável.
3. A **vertical do rótulo funciona**: `captura → OCR real → parser → evidência → regra determinística
   → áudio → métricas`, com óculos simulados e sem depender do DAT.
4. **22 testes passam e o Decision Success Rate está em 100% (15/15 cenários)**.
5. O prazo mais próximo **não é código**: é a **Entrega Final da Ideia em 22 de agosto** (documento +
   apresentação, em template obrigatório da organização).

---

## 2. Linha do tempo do programa (extraída do edital)

Esta é a informação que mais deveria influenciar o planejamento e que **não estava registrada em
nenhum documento do repositório**.

| Data (2026) | Etapa | Situação |
| :--- | :--- | :--- |
| 15–27 de julho | Inscrições | passado |
| 28 de julho | Primeiro Filtro / inscrições homologadas | passado |
| 1 de agosto | Liberação dos cursos online (DAT, Wayfarer, Kotlin, agentes de IA) | passado |
| **15 de agosto (sáb)** | **Ideathon online** — palestras, workshop de pitch, palestra de DAT (10h30) | **em 3 dias** |
| **22 de agosto** | **Entrega Final da Ideia** — documento revisado + apresentação, em **template obrigatório** | **em 10 dias** |
| 23–29 de agosto | Segundo Filtro | — |
| 31 de agosto | Resultado: 5 equipes selecionadas | — |
| **18 de setembro (sex)** | **Hackathon presencial — Meta São Paulo, 1 único dia** | — |

### Consequências práticas

- **O hackathon é um dia só.** Toda arquitetura, mocks e componentes devem chegar prontos; o
  presencial é integração + validação com hardware + pitch.
- **Os óculos e o smartphone são fornecidos pela organização no dia** e devolvidos no fim. A equipe
  só pode levar **notebooks próprios**. O app precisa instalar e rodar em um aparelho desconhecido —
  vale manter sempre um **APK assinado pronto** para `adb install`, sem depender de build na hora.
- Os óculos de um integrante são um **ativo de teste antes do evento**, não o aparelho da demo.
- **Critérios do Segundo Filtro:** viabilidade técnica (30), aderência ao toolkit/hardware (20),
  impacto (30), ética/privacidade/segurança (20).
- **Os 5 checkpoints técnicos obrigatórios do dia:** uso de IA; câmera ou microfone como entrada
  principal; output por áudio; **privacidade e dados**; **eficiência de bateria**.
  Os dois últimos **ainda não existem como requisitos no `SRS.md`** — e são pontuados.

---

## 3. Mapa completo do repositório

Legenda: ✅ pronto · 🟡 esqueleto/parcial · ⬜ template intocado · ⚠️ pendência · 🗑️ ruído

### 3.1 Raiz

| Caminho | O que é | Status |
| :--- | :--- | :--- |
| `README.md` | Decisão-base, 9 trilhas do MVP, aviso de que o DAT não está fixado de propósito | ✅ |
| `.gitignore` | Ignora `datasets/raw/`, `models/private/`, `benchmark/results/`, keystores, `.env` | ✅ |
| — | **Não existe `.git`** — por decisão da equipe, o versionamento começa quando houver sinal verde | ⚠️ ver §5-D |
| `build.gradle.kts` / `settings.gradle.kts` / `gradle.properties` | Build de topo, módulo único `:app` | ⬜ |
| `gradle/libs.versions.toml` | AGP 9.1.1, Kotlin 2.2.10, Compose BOM 2026.02.01, coroutines 1.10.2, ML Kit 16.0.1 | ✅ |
| `gradle/wrapper/` | Gradle 9.3.1 | ⬜ |
| `local.properties` | Caminho do SDK local (não versionar) | ✅ |
| `.codex-history/` | 10 transcrições, ~1,3 MB — **de outro projeto** (EMDCREDITO, Django/React). Zero menções a Eat Control | 🗑️ ver §5-I |

### 3.2 `docs/` — documentação de produto e engenharia

| Arquivo | Conteúdo | Status |
| :--- | :--- | :--- |
| `contexto-gpt.md` | **North Star.** 77 seções: tese, personas, política de evidência, safety, Edge AI, métricas, modelo de negócio | ✅ excelente |
| `00_PRODUCT_OVERVIEW.md` | Problema, proposta, limites, princípio *reliability-first* | ✅ |
| `01_ESTRUTURA_E_ESTADO_ATUAL.md` | Este documento | ✅ |
| `SPEC.md` | `InteractionRequest`, 6 tipos de evidência, precedência, 4 estados, regra de segurança | ✅ o melhor doc técnico — e agora implementado 1:1 |
| `PRD.md` | Objetivo, 3 personas, jobs-to-be-done, recorte do MVP | 🟡 |
| `SRS.md` | FR-001…013 + NFR-001…007 | 🟡 faltam privacidade e bateria |
| `SDD.md` | Pipeline de alto nível + 7 camadas | 🟡 nomes divergem do código |
| `USER_FLOWS.md` | UF-01 a UF-05 | 🟡 UF-01 implementado |
| `METRICS.md` | DSR, latências por etapa, *false-safe rate*, *uncertainty recall*, métricas de device | ✅ |
| `MODEL_BENCHMARK.md` | Protocolo de 10 passos + score ponderado | ✅ |
| `DATA_SOURCES.md` | OFF, TBCA, USDA + dataset de visão em primeira pessoa | 🟡 |
| `ROADMAP.md` | Fases 0 a 3 | ⚠️ sem datas; desalinhado do §2 |

### 3.3 `docs/adr/`

| ADR | Decisão | Status |
| :--- | :--- | :--- |
| `0001` | App nativo Kotlin/Android | Accepted |
| `0002` | Caminho crítico funciona sem nuvem | Accepted |
| `0003` | OCR/detecção/STT/TTS atrás de interfaces estáveis | Accepted — implementado |
| `0004` | Começar por ML Kit on-device | Proposed — implementado |
| `0005` | TTS/STT nativo primeiro | Proposed — TTS implementado |
| `0006` | **Versão do Meta DAT** | ⚠️ **TODO — bloqueia a trilha dos óculos reais** |

### 3.4 `docs/fontes/` — material-fonte (não editar)

Edital oficial, matriz de ideias (Eat Control: 93/100 no Filtro 1, 90/100 no Filtro 2), deck do
pitch, 13 PDFs dos cursos Meta e o plano de estudo.

### 3.5 `app/src/main/java/com/eatcontrolai/` — o código

Tudo sob o pacote único `com.eatcontrolai`. `applicationId` e `namespace` também.

| Arquivo | Papel | Status |
| :--- | :--- | :--- |
| `EatControlApp.kt` | `Application` + `AppContainer`: composição manual das dependências e perfil de demo do "João" | ✅ |
| `MainActivity.kt` | Monta o tema e a tela única | ✅ |
| **core** | | |
| `core/model/Models.kt` | `DecisionState`, `EvidenceType` (com `rank` de precedência), `Allergen`, `ClaimPolarity`, `LabelClaim`, `Evidence`, `UserProfile`, `Decision`, `DecisionReason` | ✅ implementa `SPEC.md` |
| **domain** | | |
| `domain/label/TextNormalizer.kt` | Caixa alta, remoção de acentos, recomposição de hifenização de quebra de linha | ✅ |
| `domain/label/AllergenDictionary.kt` | Sinônimos de rótulo brasileiro, casamento por fronteira de palavra, termo mais longo primeiro | ✅ auditável por humano |
| `domain/label/LabelParser.kt` | Marcador + escopo de frase → `LabelClaim` | ✅ |
| `domain/evidence/EvidenceBuilder.kt` | Promove `OCR_TEXT` (rank 5) para `DECLARED_LABEL` (rank 2) quando há declaração explícita | ✅ é o que faz a hierarquia funcionar |
| `domain/decision/FoodDecisionEngine.kt` | Precedência, os 4 estados, regra de que ausência de declaração nunca vira permissão | ✅ |
| **glasses** | | |
| `glasses/GlassesGateway.kt` | Fronteira do dispositivo + `DatGlassesGateway` (TODO até ADR-0006) | ✅ boundary |
| `glasses/MockGlassesGateway.kt` | Óculos simulados: renderiza a embalagem em bitmap e devolve JPEG | ✅ |
| `glasses/MockScene.kt` | 5 cenas em formato de rotulagem brasileira | ✅ |
| **inference** | | |
| `inference/Providers.kt` | `OcrProvider`, `BarcodeProvider`, `SttProvider`, `TtsProvider`, `ObjectDetectionProvider` + `InferenceMeta` | ✅ |
| `inference/ModelRegistry.kt` | `ProviderSet` trocável por configuração | ✅ |
| `inference/mlkit/MlKitOcrProvider.kt` | OCR on-device real | ✅ |
| `inference/androidtts/AndroidTtsProvider.kt` | TTS pt-BR + medição de *time-to-first-audio* | ✅ |
| **metrics** | | |
| `metrics/MetricsRecorder.kt` | `Stage` (as 9 etapas de `METRICS.md`), `StageMetric`, `InMemoryMetricsRecorder` | ✅ só números, nunca conteúdo |
| **orchestration** | | |
| `orchestration/InteractionOrchestrator.kt` | Costura a UF-01 e cronometra cada etapa | ✅ |
| **ui** | | |
| `ui/analyze/AnalyzeViewModel.kt` | Estado da tela, troca de cena e de restrição ao vivo | ✅ |
| `ui/analyze/AnalyzeScreen.kt` | POV, perfil, decisão, evidências, texto do OCR e latências | ✅ |
| `ui/theme/*` | Tema Material 3 | ⬜ template |
| `AndroidManifest.xml` | Permissões justificadas uma a uma, `EatControlApp` registrada | ✅ |

**Testes** (`app/src/test/java/com/eatcontrolai/`) — 22 testes, todos passando:

| Arquivo | Cobre |
| :--- | :--- |
| `domain/label/LabelParserTest.kt` | 12 casos: dois-pontos, negação, sinônimos, escopo de frase, hifenização, fronteira de palavra |
| `domain/decision/FoodDecisionEngineTest.kt` | 9 casos: os 4 estados alcançáveis, NFR-003, precedência profissional, tamanho da resposta |
| `benchmark/DecisionScenariosTest.kt` | Lê `benchmark/decision_scenarios.csv` e calcula o **DSR** |

### 3.6 `benchmark/`, `checklists/`, `datasets/`

| Caminho | O que é | Status |
| :--- | :--- | :--- |
| `benchmark/decision_scenarios.csv` | **15 cenários do motor de decisão** — a spec executável do `FR-008` | ✅ |
| `benchmark/candidates.yaml` | Candidatos por tarefa (OCR, barcode, detecção, STT, TTS) | ✅ |
| `benchmark/scenarios.csv` | 4 cenários de percepção que referenciam imagens ainda inexistentes | ⚠️ |
| `checklists/MVP_CHECKLIST.md` | Checklist Produto / DAT / AI / Demo | ✅ |
| `checklists/REAL_GLASSES_TEST.md` | 19 itens de teste com óculos reais | ✅ muito bom |
| `datasets/README.md` | Política de split e regra de primeira pessoa | ✅ |

---

## 4. Arquitetura: alvo × código

| Camada do `SDD.md` | Pacote | Estado |
| :--- | :--- | :--- |
| Device (DAT, câmera, mic) | `glasses/` | mock funcional; DAT bloqueado no ADR-0006 |
| Perception (OCR, barcode, detecção, STT) | `inference/` | OCR e TTS reais; resto são interfaces |
| Evidence (normalização) | `core/model/` + `domain/evidence/` | ✅ com promoção de rank |
| Decision (regras + confiança) | `domain/decision/` + `domain/label/` | ✅ |
| Personalization (perfil, metas, sintomas) | `core/model/UserProfile` | mínimo: restrições + metas |
| Output (áudio + registro) | `inference/androidtts/` + `ui/analyze/` | áudio ✅, histórico ainda não |
| Observability | `metrics/` | ✅ em memória |

Fluxo da UF-01, hoje:

```text
Mock/Óculos ──✓── Capture ──✓── OCR (ML Kit) ──✓── Parser ──✓── Evidência ──✓── Regras ──✓── TTS ──✓── Métricas
```

**A vertical fecha ponta a ponta.** O que falta é largura (barcode, câmera real, STT, visão,
histórico), não profundidade.

---

## 5. Achados — o que foi resolvido e o que continua aberto

### ✅ A — Pacote unificado

Tudo migrou de `com/example/eatcontrolai` para `com.eatcontrolai`, e `applicationId`/`namespace`
acompanharam. Importa porque **o registro no Wearables Developer Center costuma ficar atrelado ao
package name** — trocar depois de registrar daria retrabalho.

### ✅ B — O esqueleto virou código vivo

`InteractionOrchestrator`, `FoodDecisionEngine`, `ModelRegistry` e `MetricsRecorder` agora são
instanciados pelo `AppContainer` e exercitados pela tela.

### ✅ C — Bugs do motor de decisão

O motor anterior tinha quatro problemas, todos com teste de regressão agora:

| Problema | Antes | Agora |
| :--- | :--- | :--- |
| `COMPATIBLE` inalcançável | nenhum caminho retornava | `DEC-005`, `DEC-010`, `DEC-013` |
| `CONTÉM: LEITE` não casava (*false-safe*) | substring `"contém leite"` falhava | `DEC-002` |
| `"não contém leite"` dava incompatível | substring casava dentro da negação | `DEC-005` |
| Precedência do `SPEC.md` ignorada | evidências viravam uma string única | `EvidenceType.rank` + teste de regra profissional |

Duas regras de domínio nasceram desse trabalho e valem destaque no pitch:

- **Ausência de declaração nunca vira permissão.** Um rótulo que não fala de leite não é um rótulo
  que garante ausência de leite → `NEEDS_CONFIRMATION`, nunca `COMPATIBLE` (`DEC-007`).
- **"Zero lactose" não prova ausência de proteína do leite.** A lactose foi quebrada por enzima; a
  proteína continua lá. Tratar como ausência seria um *false-safe* para APLV (`DEC-014`), e o rótulo
  "zero lactose" que declara `CONTÉM LEITE` no verso é um ótimo momento de demo (`DEC-009`).

### ✅ E — Timeline documentada

O calendário do edital está na §2 deste documento. O `ROADMAP.md` ainda precisa absorver as datas.

### ✅ H — Dependências e permissões

ML Kit Text Recognition, coroutines e lifecycle-viewmodel-compose declarados; `CAMERA`,
`RECORD_AUDIO` e `INTERNET` no manifesto, cada uma com o requisito que a justifica.

### ⚠️ D — Sem controle de versão *(decisão consciente da equipe)*

Não há `.git`, por opção: a equipe não quer registrar data de início de codificação antes do sinal
verde. Enquanto isso valer, **não há como desfazer nada**. Mitigação sugerida: cópia zipada do
diretório ao fim de cada sessão de trabalho, guardada fora da pasta do projeto.

### ⚠️ F — Privacidade e bateria ainda não são requisitos

São checkpoints pontuados no dia do hackathon. O `SRS.md` só tem o `NFR-005`. Faltam requisitos com
ID sobre retenção de imagem, opt-in, processamento local, duty-cycle de câmera e meta de consumo.

O código já se comporta bem (o `InMemoryMetricsRecorder` guarda só números; nenhuma imagem é
persistida), mas comportamento não documentado não pontua.

### ⚠️ G — "Conectar óculos" no MVP × realidade do hardware

O `PRD.md` §4 lista "Conectar óculos ao app" no MVP, mas os óculos só existem no dia 18/09. O
`MockGlassesGateway` resolve o desenvolvimento; o `PRD.md` deveria dizer isso explicitamente.

### ⚠️ I — `.codex-history/` é ruído de outro projeto

1,3 MB de conversas do EMDCREDITO. Não apaguei porque sem git a remoção é irreversível e o conteúdo
é seu — decida se apaga ou move para fora do projeto. Em qualquer caso, entra no `.gitignore` antes
do primeiro commit.

### 🟡 J — Os outros documentos ainda não são *spec driven*

O motor de decisão agora tem spec executável (`benchmark/decision_scenarios.csv` → DSR no build).
`PRD`, `SRS`, `SDD` e `USER_FLOWS` continuam sem critérios de aceite verificáveis e sem
rastreabilidade FR → módulo → teste. O caminho é repetir o padrão do CSV para as outras trilhas.

---

## 6. Como rodar e visualizar

### 6.1 O aplicativo

Ambiente já pronto: SDK Android 36.1, build-tools 36/37, JDK 21, Gradle 9.3.1 e dois emuladores
(`Pixel_10`, `Pixel_6a`).

```bash
./gradlew :app:assembleDebug     # gera app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug           # instala no emulador/aparelho conectado
./gradlew :app:testDebugUnitTest # roda os 22 testes e imprime o DSR
```

No Android Studio: escolher o AVD `Pixel_10` e apertar ▶ **Run**.

**O que você vai ver:** a tela "Eat Control AI" com cinco blocos —

1. **Perfil — João**, com as restrições como chips clicáveis;
2. **O que os óculos estão vendo**, com as cinco cenas de rótulo;
3. **Ponto de vista**, onde aparece o frame realmente capturado;
4. **Analisar rótulo**, que dispara a pipeline e fala o resultado em voz alta;
5. **Decisão + Evidências + Latência por etapa**.

O gesto mais interessante para demonstrar: escolher o **"Iogurte zero lactose"**, analisar com a
restrição de leite ligada (dá `INCOMPATÍVEL`, porque o verso declara `CONTÉM LEITE`), depois desligar
a restrição e analisar de novo. Mesma imagem, mesmo OCR, decisão diferente — é a personalização
ficando visível.

### 6.2 O "simulador dos óculos" — são duas coisas diferentes

| | **Mock Device Kit (Meta)** | **`MockGlassesGateway` (nosso)** |
| :--- | :--- | :--- |
| O que é | Ferramenta do DAT que simula o wearable para a API do toolkit | Implementação falsa da **nossa** `GlassesGateway` |
| Precisa de | Versão do DAT decidida (ADR-0006) + projeto no Wearables Developer Center | Nada |
| Fonte da imagem | O que o kit fornecer | Rótulo renderizado em bitmap, lido por **OCR real** |
| Serve para | Validar a integração real com o toolkit | Desenvolver e demonstrar toda a lógica sem hardware |
| Disponível | Depois do ADR-0006 | **Funcionando** |

O mock renderiza a embalagem e passa o JPEG pelo ML Kit — ele substitui o **hardware**, não a
**inteligência**. Se o OCR errar a leitura, a demo erra junto, como erraria com os óculos. Isso
mantém honesto o que a demo prova.

Quando o DAT entrar, `DatGlassesGateway` implementa a mesma interface e o resto do app não muda.

### 6.3 Tamanho do pacote — uma medição, não um palpite

`docs/METRICS.md` lista *package size* como métrica de device, e o edital pontua eficiência. Primeira
medição do APK de debug (12/08/2026):

| Parte | Tamanho |
| :--- | ---: |
| `lib/` — 4 ABIs (x86, x86_64, armeabi-v7a, arm64-v8a) | 41,1 MB |
| `classes*.dex` sem minificação | 32,1 MB |
| **modelo de OCR do ML Kit** (`assets/mlkit-google-ocr-models`) | **1,5 MB** |
| Total | 76,1 MB |

O modelo de IA custa 1,5 MB — o peso é do build de debug. Um release com `isMinifyEnabled` e um
único ABI (`arm64-v8a`, que é o que qualquer aparelho fornecido pela organização vai usar) deve cair
para a casa de poucas dezenas de MB. Vale medir e registrar antes do dia 18/09.

Nota de ADR pendente: usamos a variante **bundled** do ML Kit (`com.google.mlkit:text-recognition`),
não a que depende do Google Play Services. Custa alguns MB a mais e é a escolha certa aqui —
funciona offline, sem depender do que estiver instalado no aparelho emprestado, e sustenta a
afirmação de Edge AI do ADR-0002.

---

## 7. Sequência de trabalho

### ✅ Bloco 0 — Fundação

Pacote unificado, `applicationId` definitivo, dependências e permissões declaradas, diretório vazio
`eat-control-ai-bootstrap/` removido. **`git init` pendente por decisão da equipe.**

### ✅ Bloco 1 — Vertical do rótulo

`MockGlassesGateway` → `MlKitOcrProvider` → `LabelParser` → `EvidenceBuilder` → `FoodDecisionEngine`
→ `AndroidTtsProvider` → tela com POV, evidências e latências. Spec executável com DSR no build.

### Bloco 2 — Ampliar

1. Barcode (ML Kit) + cache local de produtos do Open Food Facts.
2. CameraX — mesma pipeline, outra fonte de frame; e fotos reais de rótulo no lugar do bitmap.
3. STT para a pergunta falada.
4. Perfil real + persistência (DataStore/Room) + histórico.
5. Telas de Home / Histórico / Perfil / Óculos.

### Bloco 3 — Depois

Modelo visual de prato, quantização, benchmark em aparelho físico, backend e sincronização.

### Fora do código, no caminho crítico até 22/08

1. **Conseguir o template obrigatório de entrega** — sem ele, a entrega corre risco de reprovação
   formal. Tarefa nº 1 da equipe.
2. Dividir papéis dos três integrantes (documento/pitch × código × hardware QA).
3. Datar o `ROADMAP.md` e escrever explicitamente o que **não** será feito no dia 18/09.
4. Adicionar ao `SRS.md` os requisitos de **privacidade** e **bateria**.
5. Preencher o ADR-0006 com o que sair da palestra de DAT do Ideathon (15/08, 10h30).

---

## 8. Perguntas em aberto

1. A equipe recebeu o **template obrigatório** da Entrega Final da Ideia?
2. Qual o modelo exato dos óculos do integrante, e ele é compatível com o DAT? (primeiro item do
   `REAL_GLASSES_TEST.md`)
3. Quem, dos três, escreve código? Há experiência prévia com Kotlin/Android?
4. Existe acesso a nutricionista/médico para validar linguagem e regras antes de 22/08? A matriz de
   ideias coloca isso como condição para a submissão final.
5. O projeto no Wearables Developer Center já foi criado?

---

*Atualizado em 12/08/2026. Base: leitura completa de `docs/`, do edital, da matriz de ideias, do
deck e de todo o código em `app/src/`.*
