# Estrutura do repositório e estado atual — Eat Control AI

> **O que é este documento.** O mapa do repositório: o que existe, o que é real, o que é dado de
> demonstração e o que cada peça precisa virar. Ponto de entrada para quem chega no projeto.
>
> Não substitui o `docs/contexto-gpt.md` (visão de produto / North Star) nem o `docs/PRD.md`.
> Descreve o **repositório**, não o produto.
>
> Atualizado em **13 de agosto de 2026**.

---

## 1. Resumo

1. A **visão de produto está madura** — provavelmente o ativo mais forte do projeto.
2. O **app do paciente está completo na navegação**: cinco telas, tema próprio, e a vertical do
   rótulo funcionando ponta a ponta com OCR real.
3. **26 testes passam** e o **Decision Success Rate está em 100% (19/19 cenários)**.
4. O **DAT saiu de "não sei" para "sei exatamente o que falta"**: versão 0.9.0, dois bloqueios de
   credencial, nenhum deles técnico. Ver §5-A.
5. O prazo mais próximo **não é código**: é a **Entrega Final da Ideia em 22 de agosto** (documento
   + apresentação, em template obrigatório da organização).

---

## 2. Linha do tempo do programa (extraída do edital)

| Data (2026) | Etapa |
| :--- | :--- |
| 15–27 de julho | Inscrições |
| 28 de julho | Primeiro Filtro / inscrições homologadas |
| 1 de agosto | Liberação dos cursos online |
| **15 de agosto (sáb)** | **Ideathon online** — palestra de DAT às 10h30 |
| **22 de agosto** | **Entrega Final da Ideia** — documento + apresentação, em **template obrigatório** |
| 23–29 de agosto | Segundo Filtro |
| 31 de agosto | Resultado: 5 equipes selecionadas |
| **18 de setembro (sex)** | **Hackathon presencial — Meta São Paulo, 1 único dia** |

### Consequências práticas

- **O hackathon é um dia só.** Arquitetura, mocks e componentes precisam chegar prontos.
- **Os óculos e o smartphone são fornecidos pela organização no dia** e devolvidos no fim. A equipe
  só leva **notebooks próprios**. Vale manter um **APK assinado pronto** para `adb install`.
- Os óculos de um integrante são **ativo de teste antes do evento**, não o aparelho da demo.
- **Segundo Filtro:** viabilidade técnica (30), aderência ao toolkit/hardware (20), impacto (30),
  ética/privacidade/segurança (20).
- **Checkpoints obrigatórios do dia:** uso de IA; câmera ou microfone como entrada principal; output
  por áudio; **privacidade e dados**; **eficiência de bateria**.

---

## 3. Mapa do repositório

Legenda: ✅ pronto · 🟡 parcial · ⬜ template · ⚠️ pendência

### 3.1 Raiz

| Caminho | O que é | Status |
| :--- | :--- | :--- |
| `README.md` | Decisão-base e trilhas do MVP | ✅ |
| `preview.html` | Protótipo de UI em HTML que originou o design system | ✅ referência |
| `.gitignore` | Ignora PDFs dos cursos (150 MB), assets de página salva, históricos de IDE, keystores | ✅ |
| `scripts/benchmark.sh` | Roda o benchmark de modelos no aparelho físico | ✅ |
| `build.gradle.kts` · `settings.gradle.kts` · `gradle/` | AGP 9.1.1, Kotlin 2.2.10, Gradle 9.3.1, Compose BOM 2026.02.01, ML Kit 16.0.1 | ✅ |
| `.codex-history/` | Transcrições de **outro projeto** (EMDCREDITO). Ignorado pelo git; apagar quando quiser | ⚠️ |

Versionamento: repositório em `EverGreen-Agency/EatControlAI` (privado), 99 arquivos.

### 3.2 `docs/`

| Arquivo | Conteúdo | Status |
| :--- | :--- | :--- |
| `contexto-gpt.md` | **North Star.** 77 seções de visão, safety, arquitetura e negócio | ✅ |
| `00_PRODUCT_OVERVIEW.md` | Problema, proposta, limites, *reliability-first* | ✅ |
| `01_ESTRUTURA_E_ESTADO_ATUAL.md` | Este documento | ✅ |
| `SPEC.md` | Tipos de evidência, precedência, 4 estados, regra de segurança | ✅ implementado 1:1 |
| `METRICS.md` | DSR, latências, *false-safe rate*, *uncertainty recall* | ✅ DSR automatizado |
| `MODEL_BENCHMARK.md` | Protocolo de 10 passos + score ponderado | ✅ harness implementado |
| `PRD.md` · `SRS.md` · `SDD.md` · `USER_FLOWS.md` | Requisitos e desenho | 🟡 sem critérios de aceite |
| `DATA_SOURCES.md` | OFF, TBCA, USDA + dataset de visão | 🟡 |
| `ROADMAP.md` | Fases 0 a 3 | ⚠️ sem datas |

### 3.3 `docs/adr/`

| ADR | Decisão | Status |
| :--- | :--- | :--- |
| `0001` | App nativo Kotlin/Android | Accepted |
| `0002` | Caminho crítico sem nuvem | Accepted |
| `0003` | Providers atrás de interfaces estáveis | Accepted — implementado |
| `0004` | ML Kit OCR primeiro | Proposed — implementado |
| `0005` | TTS/STT nativo primeiro | Proposed — TTS implementado |
| `0006` | **Meta DAT 0.9.0** | **Proposed** — coordenadas conhecidas, bloqueado por credenciais |

### 3.4 `app/src/main/` — 33 arquivos, ~4.100 linhas

**Domínio** (Kotlin puro, sem Android — é o que os testes exercitam)

| Arquivo | Papel |
| :--- | :--- |
| `core/model/Models.kt` | `DecisionState`, `EvidenceType` (com `rank`), `Allergen`, `Restriction` (severidade + política de incerteza), `Evidence`, `UserProfile`, `MealRecord`, `PrivacySettings` |
| `domain/label/TextNormalizer.kt` | Caixa alta, sem acento, hifenização de quebra de linha recomposta |
| `domain/label/AllergenDictionary.kt` | Sinônimos de rótulo brasileiro, casamento por fronteira de palavra |
| `domain/label/LabelParser.kt` | Marcador + escopo de frase → `LabelClaim` |
| `domain/evidence/EvidenceBuilder.kt` | Promove `OCR_TEXT` (rank 5) a `DECLARED_LABEL` (rank 2) |
| `domain/decision/FoodDecisionEngine.kt` | Precedência, 4 estados, severidade e política de incerteza |

**Percepção e dispositivo**

| Arquivo | Papel |
| :--- | :--- |
| `inference/Providers.kt` · `ModelRegistry.kt` | Interfaces trocáveis + `ProviderSet` ativo |
| `inference/mlkit/MlKitOcrProvider.kt` | OCR on-device |
| `inference/androidtts/AndroidTtsProvider.kt` | TTS pt-BR com *time-to-first-audio* |
| `glasses/GlassesGateway.kt` | Fronteira do dispositivo + `GlassesStatus` + `DatGlassesGateway` (TODO) |
| `glasses/MockGlassesGateway.kt` · `MockLabelRenderer.kt` · `MockScene.kt` | Óculos simulados; 5 cenas com gabarito de decisão |
| `orchestration/InteractionOrchestrator.kt` | Costura a UF-01 e cronometra cada etapa |
| `metrics/MetricsRecorder.kt` | 9 etapas de `METRICS.md`; guarda só números |
| `benchmark/ProviderBenchmark.kt` | Harness compartilhado entre terminal e tela de Laboratório |

**Interface**

| Arquivo | Papel |
| :--- | :--- |
| `ui/theme/` | Paleta, tipografia e tema escuro portados do `preview.html` |
| `ui/components/Components.kt` | `EcCard`, `EcChip`, `StateBadge`, `StatCard`, `EcToggle`, `DemoTag` |
| `ui/EatControlRoot.kt` | Navegação de 5 destinos, barra inferior, snackbar |
| `ui/EatControlViewModel.kt` | Estado único do app + loop de confirmação + benchmark |
| `ui/home/HomeScreen.kt` | Hoje: hero, métricas, últimas escolhas, card dos óculos |
| `ui/analyze/AnalyzeScreen.kt` · `ResultSheet.kt` | Modos, POV, análise e resultado em bottom sheet |
| `ui/plan/PlanScreen.kt` | Restrições com severidade, precedência de evidência, jornada GLP-1 |
| `ui/history/HistoryScreen.kt` | Registros reais agrupados por dia |
| `ui/profile/ProfileScreen.kt` | Privacidade, óculos, processamento |
| `ui/lab/LabScreen.kt` | Laboratório de modelos (só em build de debug) |
| `data/Repositories.kt` | Perfil, privacidade e histórico em memória |

**Testes** — 26 no total

| Arquivo | Cobre |
| :--- | :--- |
| `test/.../LabelParserTest.kt` | 12 casos: dois-pontos, negação, sinônimos, escopo de frase, hifenização |
| `test/.../FoodDecisionEngineTest.kt` | 13 casos: 4 estados, NFR-003, precedência, severidade, políticas, confirmação |
| `test/.../DecisionScenariosTest.kt` | Lê `benchmark/decision_scenarios.csv` e calcula o **DSR** |
| `androidTest/.../OcrBenchmarkTest.kt` | Benchmark de OCR em aparelho físico |

### 3.5 `benchmark/`, `checklists/`, `datasets/`

| Caminho | O que é | Status |
| :--- | :--- | :--- |
| `benchmark/decision_scenarios.csv` | **19 cenários** — a spec executável do `FR-008` | ✅ |
| `benchmark/candidates.yaml` | Catálogo de candidatos por tarefa | ✅ |
| `benchmark/scenarios.csv` | 4 cenários de percepção que referenciam imagens inexistentes | ⚠️ |
| `checklists/MVP_CHECKLIST.md` | Estado real por trilha + privacidade/bateria | ✅ |
| `checklists/REAL_GLASSES_TEST.md` | 19 itens de teste com óculos reais | ✅ |

---

## 4. O que é real e o que é demonstração

Decisão de projeto: **nada na tela finge ser medido**. Números que o app não calcula levam a marca
`DEMO`, e campos que a fonte não reporta ficam vazios.

| Tela | Real | Demonstração |
| :--- | :--- | :--- |
| Hoje | Análises registradas, últimas escolhas, estado dos óculos | Proteína e hidratação (marcados `DEMO`) |
| Analisar | Captura, OCR, parser, decisão, TTS, latências | — |
| Meu plano | Restrições, severidade, política de incerteza, precedência, orientações | Prioridades do dia (marcadas `DEMO`) |
| Histórico | Todos os registros | — |
| Perfil | Chaves de privacidade, teste de câmera e áudio | — |

Dois exemplos concretos da regra:

- A bateria dos óculos aparece como **"—"**, não como "74%": a fonte simulada não reporta bateria.
- Não existe percentual de confiança em componentes visuais, porque não existe modelo de visão. O
  protótipo HTML mostrava "87%" — um número que ninguém consegue explicar se a banca perguntar.

---

## 5. Achados e pendências

### ⚠️ A — DAT: dois bloqueios, nenhum técnico

O `ADR-0006` saiu de TODO. O SDK é a versão **0.9.0**, artefatos `com.meta.wearable:mwdat-core`,
`mwdat-camera` e `mwdat-mockdevice` (o `mwdat-display` não serve — os óculos do programa não têm
display). O que falta:

1. **Token do GitHub com escopo `read:packages`.** O SDK não está no Maven Central; está no GitHub
   Packages, e sem o token o Gradle não baixa nada.
2. **Projeto no Wearables Developer Center**, que emite `APPLICATION_ID` e `CLIENT_TOKEN` para a
   atestação do app.

Consequência que costuma passar batido: **o Mock Device Kit oficial da Meta também está atrás dessas
credenciais.** É por isso que o `MockGlassesGateway` deste repositório existe.

### ⚠️ B — Privacidade e bateria ainda não são requisitos

São checkpoints pontuados no dia 18/09. O `SRS.md` só tem o `NFR-005`. O código já se comporta bem —
nenhuma imagem é gravada em disco, a telemetria guarda só latência e versão de modelo, e a tela de
Perfil expõe as chaves — mas comportamento não documentado não pontua.

### ⚠️ C — Persistência

Perfil, privacidade e histórico vivem em memória e se perdem ao fechar o app. É trabalho de
DataStore/Room, não de UI.

### ⚠️ D — Tamanho do pacote

APK de debug: **76 MB**. O modelo de OCR custa **1,5 MB**; o peso é 41 MB de bibliotecas nativas em
4 ABIs e 32 MB de dex sem minificação. Um release com `isMinifyEnabled` e só `arm64-v8a` deve cair
para poucas dezenas de MB. Medir e registrar antes de 18/09.

### ⚠️ E — `ROADMAP.md` sem datas

O calendário está na §2 deste documento, mas o roadmap ainda trata as fases como blocos sem prazo.

### 🟡 F — Documentos ainda não são *spec driven*

O motor de decisão tem spec executável (CSV → DSR no build). `PRD`, `SRS`, `SDD` e `USER_FLOWS`
continuam sem critérios de aceite verificáveis. O caminho é repetir o padrão do CSV.

### ✅ Resolvidos nesta rodada

Pacote unificado em `com.eatcontrolai` e `applicationId` definitivo (importa porque o registro no
Wearables Developer Center fica atrelado ao package name); versionamento inicializado; motor de
decisão com os quatro estados alcançáveis e os falsos negativos de rótulo cobertos por teste;
navegação e design system completos.

---

## 6. Como rodar

### 6.1 O aplicativo

```bash
./gradlew :app:assembleDebug     # gera app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug           # instala no aparelho conectado
./gradlew :app:testDebugUnitTest # 26 testes e o DSR
```

No Android Studio: escolher o aparelho e apertar ▶ **Run**.

**O roteiro de demonstração mais forte hoje:** aba **Analisar** → cena **"Iogurte zero lactose"** →
**Analisar rótulo**. Dá `INCOMPATÍVEL`, porque o verso declara `CONTÉM LEITE` apesar do "zero
lactose" na frente. Depois vá em **Meu plano**, desligue a restrição de leite e repita: mesma imagem,
mesmo OCR, decisão diferente.

O segundo momento: escolha **"Barra de proteína"** (que diz `PODE CONTER LEITE`). O resultado abre a
**pergunta de confirmação** — e a resposta entra como evidência de rank 4, com a decisão recalculada
pelo mesmo motor. Não é texto trocado na tela.

### 6.2 Benchmark de modelos no aparelho

```bash
./scripts/benchmark.sh
```

Roda `OcrBenchmarkTest` no celular conectado por USB, com warm-up e repetições, e imprime p50/p90/p95
e o DSR por provider. Para comparar um candidato novo, acrescente-o à lista `providers` do teste.

A mesma coisa dentro do app: build de debug → **Hoje** → **Laboratório de modelos**. É o mesmo
`ProviderBenchmark`, então os números batem.

A qualidade é medida por **decisão acertada**, não por texto idêntico: um OCR que lê "CONTEM LEITF"
pode ter erro de caractere baixo e ainda produzir a decisão errada — e é a decisão que chega ao
usuário.

### 6.3 Onde se troca de modelo

Um lugar só: o `ModelRegistry` montado em `AppContainer` ([EatControlApp.kt](../app/src/main/java/com/eatcontrolai/EatControlApp.kt)).

```kotlin
val models = ModelRegistry(
    ProviderSet(ocr = MlKitOcrProvider(), tts = tts)
)
```

Trocar de OCR é trocar essa linha; em runtime, `models.swap(outroConjunto)`. Nenhuma tela conhece o
ML Kit. O catálogo de candidatos está em `benchmark/candidates.yaml` e a régua em
`docs/MODEL_BENCHMARK.md`.

### 6.4 O "simulador dos óculos"

Não há nada a inicializar: o `MockGlassesGateway` **é** a fonte de captura do app e conecta sozinho
ao abrir. Ele renderiza a embalagem em bitmap e entrega o JPEG para o OCR real — substitui o
**hardware**, não a **inteligência**.

| | **Mock Device Kit (Meta)** | **`MockGlassesGateway` (nosso)** |
| :--- | :--- | :--- |
| Simula | O dispositivo para a API do DAT | O dispositivo para o nosso app |
| Precisa de | Token `read:packages` + credenciais do Developer Center | Nada |
| Disponível | Depois do §5-A | **Funcionando** |

Quando o DAT entrar, `DatGlassesGateway` implementa a mesma interface e o resto do app não muda.

### 6.5 Para que serve o Android Studio

Não é só emulador. **Compose Preview** renderiza uma tela sem instalar o app — muda o ritmo do
trabalho de UI. O **Profiler de energia/CPU/memória** é literalmente o checkpoint de bateria do dia
18/09. Mais Logcat, debugger, Layout Inspector e o `adb`.

Rodar no aparelho físico por USB é melhor e deve continuar assim: o `NFR-006` exige benchmark em
hardware real, e ML Kit e TTS se comportam diferente no emulador. Emulador serve para conferir
tamanhos de tela.

---

## 7. Sequência de trabalho

### ✅ Concluído

Fundação (pacote, `applicationId`, dependências, permissões, git), vertical do rótulo com OCR real,
motor determinístico com spec executável, design system, navegação de 5 telas, loop de confirmação,
harness de benchmark por terminal e por tela.

### Próximo

1. Barcode (ML Kit) + cache local do Open Food Facts.
2. CameraX — mesma pipeline, outra fonte de frame; e fotos reais de rótulo no lugar do bitmap.
3. Persistência com DataStore/Room.
4. STT para a pergunta falada.
5. `SRS.md` com requisitos de privacidade e bateria.
6. Medição de bateria em 10 min de uso, no aparelho físico.

### Depois

Modelo visual de prato, quantização, backend e sincronização.

### Fora do código, no caminho crítico até 22/08

1. **Conseguir o template obrigatório de entrega.** Tarefa nº 1 — é o único item que pode reprovar a
   entrega por questão formal.
2. **Gerar o token do GitHub e criar o projeto no Wearables Developer Center** (§5-A).
3. Dividir papéis dos três integrantes.
4. Datar o `ROADMAP.md` e escrever o que **não** será feito no dia 18/09.

---

## 8. Perguntas em aberto

1. A equipe recebeu o **template obrigatório** da Entrega Final da Ideia?
2. Qual o modelo exato dos óculos do integrante, e ele é compatível com o DAT?
3. Existe acesso a nutricionista/médico para validar linguagem e regras antes de 22/08? A matriz de
   ideias coloca isso como condição para a submissão final.
4. O projeto no Wearables Developer Center já foi criado?

---

*Base: leitura completa de `docs/`, do edital, da matriz de ideias, do deck, do `preview.html` e de
todo o código em `app/src/`.*
