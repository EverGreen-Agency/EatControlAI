# Estrutura do repositório e estado atual — Eat Control AI

> Mapa do repositório: o que existe, o que é real, o que é dado de demonstração e o que cada peça
> precisa virar. Ponto de entrada para quem chega no projeto.
>
> Não substitui o `docs/contexto-gpt.md` (visão de produto) nem o `docs/PRD.md`. Descreve o
> **repositório**, não o produto.
>
> Atualizado em **13 de agosto de 2026**.

---

## 1. Resumo

1. **Duas verticais fecham ponta a ponta**: rótulo (OCR) e código de barras (EAN-13 lido de
   verdade), ambas com resposta por áudio e latência medida por etapa.
2. **Voz funciona**: reconhecimento on-device escolhe a trilha e dispara a análise.
3. **Três fontes de captura** atrás da mesma interface: óculos simulados, câmera do celular
   (CameraX) e o DAT — este último escrito sobre a API real, aguardando validação em hardware.
4. **50 testes passam**, DSR em 100% (19/19). Release com R8 e split por ABI: **30,2 MB** no
   aparelho-alvo, contra 130 MB do debug.
5. **O SDK do DAT está integrado** e o `DatGlassesGateway` compila contra a API 0.9.0. Ver §5-A.

---

## 2. Linha do tempo do programa

| Data (2026) | Etapa |
| :--- | :--- |
| **15 de agosto (sáb)** | **Ideathon online** — palestra de DAT às 10h30, entregáveis às 09h30 |
| **22 de agosto** | **Entrega Final da Ideia** — documento + apresentação |
| 23–29 de agosto | Segundo Filtro |
| 31 de agosto | Resultado: 5 equipes selecionadas |
| **18 de setembro (sex)** | **Hackathon presencial — Meta São Paulo, 1 único dia** |

Consequências que continuam valendo:

- **Óculos e smartphone são fornecidos pela organização no dia** e devolvidos no fim; a equipe só
  leva notebooks. Vale manter um **APK de release pronto** para `adb install`.
- **Critérios do Segundo Filtro:** viabilidade técnica (30), aderência ao toolkit (20), impacto (30),
  ética/privacidade/segurança (20).
- **Checkpoints do dia:** uso de IA; câmera ou microfone como entrada; output por áudio;
  privacidade e dados; eficiência de bateria.

---

## 3. Mapa do repositório

### 3.1 Raiz

| Caminho | O que é |
| :--- | :--- |
| `README.md` | Decisão-base e trilhas do MVP |
| `scripts/benchmark.sh` | Benchmark de modelos no aparelho físico |
| `gradle/libs.versions.toml` | AGP 9.1.1, Kotlin 2.2.10, Compose BOM 2026.02.01, ML Kit, CameraX 1.3.4, DataStore 1.1.1 |
| `.gitignore` | Ignora PDFs dos cursos, assets de página salva, históricos de IDE, keystores |
| `.codex-history/` | Transcrições de **outro projeto** (EMDCREDITO). Ignorado pelo git; apagar quando quiser |

Versionamento: `EverGreen-Agency/EatControlAI`, privado.

### 3.2 `docs/` e `docs/adr/`

`contexto-gpt.md` é o North Star. `SPEC.md` e `METRICS.md` estão implementados 1:1.
`PRD`, `SRS`, `SDD` e `USER_FLOWS` seguem sem critérios de aceite verificáveis — ver §5-E.

ADRs 0001–0005 aceitos e implementados. **ADR-0006 (DAT 0.9.0)** com coordenadas conhecidas e
bloqueado por credencial.

### 3.3 `app/src/main/` — o código

**Domínio** — Kotlin puro, sem Android; é o que os testes exercitam.

| Arquivo | Papel |
| :--- | :--- |
| `core/model/Models.kt` | Estados, `EvidenceType` com `rank`, `Restriction` (severidade + política de incerteza), `MealRecord`, `PrivacySettings` |
| `domain/label/` | `TextNormalizer`, `AllergenDictionary`, `LabelParser` (marcador + escopo de frase) |
| `domain/evidence/EvidenceBuilder.kt` | Promove `OCR_TEXT` (rank 5) a `DECLARED_LABEL` (rank 2) |
| `domain/decision/FoodDecisionEngine.kt` | Precedência, 4 estados, severidade e política |
| `domain/barcode/BarcodeRepository.kt` | Catálogo local de demonstração; afirmações vêm do `LabelParser` |
| `domain/voice/VoiceIntentParser.kt` | Comando falado → trilha + alérgeno em foco, determinístico |

**Percepção e captura**

| Arquivo | Papel |
| :--- | :--- |
| `inference/Providers.kt` · `ModelRegistry.kt` | Interfaces trocáveis + conjunto ativo |
| `inference/mlkit/` | `MlKitOcrProvider`, `MlKitBarcodeProvider` |
| `inference/androidtts/AndroidTtsProvider.kt` | TTS pt-BR com *time-to-first-audio* |
| `inference/androidstt/AndroidSttProvider.kt` | STT com `createOnDeviceSpeechRecognizer` (sem rede) |
| `glasses/GlassesGateway.kt` | Fronteira do dispositivo + `GlassesStatus` |
| `glasses/MockGlassesGateway.kt` · `MockLabelRenderer` · `Ean13Renderer` | Óculos simulados; renderiza rótulo e barras EAN-13 reais |
| `glasses/PhoneCameraGateway.kt` | Câmera do celular via CameraX, com correção de rotação |
| `glasses/CaptureSourceRouter.kt` | Troca de fonte em runtime sem recriar nada a jusante |
| `orchestration/InteractionOrchestrator.kt` | Trilhas de rótulo e de produto, cronometradas |
| `metrics/MetricsRecorder.kt` | 9 etapas de `METRICS.md`; só números |
| `benchmark/ProviderBenchmark.kt` | Harness compartilhado entre terminal e tela de Laboratório |
| `data/` | `LocalStore` (DataStore), `Serialization` (org.json), repositórios com hidratação |

**Interface** — `ui/` com tema próprio, navegação de 5 destinos e as telas Hoje, Analisar,
Meu plano, Histórico, Perfil, mais o Laboratório em build de debug.

**Testes — 50 unitários + 2 instrumentados**

| Arquivo | Cobre |
| :--- | :--- |
| `LabelParserTest` | 12 casos de leitura de rótulo |
| `FoodDecisionEngineTest` | 13 casos: 4 estados, NFR-003, precedência, severidade, confirmação |
| `DecisionScenariosTest` | 19 cenários do CSV → **DSR** |
| `BarcodeRepositoryTest` | Catálogo, EANs válidos, gabarito das cenas |
| `Ean13RendererTest` | Dígito verificador, 95 módulos, guardas, paridade |
| `VoiceIntentParserTest` | Trilha, foco e transcrição vazia |
| `SerializationTest` | Ida e volta, JSON corrompido, enum de versão futura |
| `OcrBenchmarkTest` *(aparelho)* | Latência e DSR por provider |
| `BarcodeScanInstrumentedTest` *(aparelho)* | ML Kit decodifica as barras renderizadas |

---

## 4. O que é real e o que é demonstração

Regra: **nada na tela finge ser medido**. Números que o app não calcula levam a marca `DEMO`, e
campos que a fonte não reporta ficam vazios.

| Tela | Real | Demonstração |
| :--- | :--- | :--- |
| Hoje | Análises registradas, últimas escolhas, estado da fonte | Proteína e hidratação |
| Analisar | Captura, OCR, barcode, voz, decisão, TTS, latências | — |
| Meu plano | Restrições, severidade, política, precedência, orientações | Prioridades do dia |
| Histórico | Todos os registros, persistidos | — |
| Perfil | Privacidade, apagar histórico, testes de câmera e áudio | — |

A bateria dos óculos aparece como **"—"**: nem o mock nem a câmera do celular reportam bateria de
wearable. E não existe percentual de confiança visual, porque não existe modelo de visão.

---

## 5. Achados e pendências

### ⚠️ A — DAT integrado, falta validar em hardware

O SDK **0.9.0** resolve e compila: `mwdat-core`, `mwdat-camera` e `mwdat-mockdevice` (este só em
debug). `DatGlassesGateway` usa o ciclo real de sessão. O mapa completo da API, levantado por
inspeção dos AARs, está no `ADR-0006`.

O que falta é hardware:

- confirmar o formato do frame (assumimos NV21 com `compressVideo = false`);
- entender `PhotoData`, que é uma interface **vazia** na 0.9.0 — `capturePhoto()` existe mas não
  expõe os bytes pela API pública;
- preencher **Package** e **App signature** na Configuration do Developer Center e ligar **Camera
  access**.

Enquanto isso, o gateway fica fora do `CaptureSourceRouter`: botão que não funciona é pior que
ausência de botão.

### ⚠️ B — Base de produtos é catálogo de demonstração

`BarcodeRepository` tem 4 EANs fictícios. A integração real com Open Food Facts / TBCA
(`docs/DATA_SOURCES.md`) é trabalho seguinte e entra atrás da mesma interface.

### ⚠️ C — Dataset de percepção é sintético

As imagens são renderizadas, não fotografadas. Serve para regressão e benchmark comparativo, mas não
mede o que acontece com rótulo curvo, reflexo de embalagem e luz de supermercado — que é o que o
`datasets/README.md` pede.

### ⚠️ D — Medições de aparelho pendentes

O harness existe (`./scripts/benchmark.sh`) e nunca rodou em hardware. Faltam também os 10 minutos de
consumo de bateria do `MVP_CHECKLIST`.

### 🟡 E — Documentos ainda não são *spec driven*

O motor de decisão tem spec executável. `PRD`, `SRS`, `SDD` e `USER_FLOWS` continuam sem critérios de
aceite verificáveis.

### ✅ Resolvidos nesta rodada

Barcode passou a decodificar de verdade; STT deixou de devolver string fixa; a câmera do celular
deixou de ser um stub delegando ao mock; perfil, privacidade e histórico persistem; APK de release
caiu de 68 MB para 22 MB.

---

## 6. Como rodar

```bash
./gradlew installDebug            # instala no aparelho conectado
./gradlew :app:testDebugUnitTest  # 50 testes e o DSR
./gradlew :app:assembleRelease    # APKs por ABI em app/build/outputs/apk/release/
./scripts/benchmark.sh            # benchmark de modelos no aparelho físico
```

### Roteiro de demonstração

1. **Analisar → Rótulo → "Iogurte zero lactose" → Analisar.** Dá `INCOMPATÍVEL`: o verso declara
   `CONTÉM LEITE` apesar do "zero lactose" na frente.
2. **Meu plano → desligar a restrição de leite → repetir.** Mesma imagem, mesmo OCR, decisão
   diferente.
3. **Rótulo → "Barra de proteína".** Abre a **pergunta de confirmação**; a resposta entra como
   evidência de rank 4 e a decisão é recalculada pelo mesmo motor.
4. **Código de barras → "EAN · Iogurte natural".** O ML Kit lê o EAN das barras renderizadas e a
   composição vem do catálogo, com rank acima de qualquer OCR.
5. **Falar** → "posso comer isso?" → a análise dispara sozinha.
6. **Fonte de captura → Câmera do celular** → aponte para uma embalagem real.

### Tamanho do pacote

| Build | Tamanho |
| :--- | ---: |
| Debug universal | 176,8 MB |
| Release universal (R8) | 135,1 MB |
| **Release arm64-v8a** | **44,1 MB** |
| Release armeabi-v7a | 32,3 MB |
| Modelos de IA embarcados | ~5 MB |
| `libmlkitcommonpipeline.so` (rotulagem visual) | ~11 MB por ABI |

Medido em 19/08/2026, com DAT 0.9.0 e a trilha de prato. O release arm64 saiu de 22,2 MB para
44,1 MB em duas rodadas: o SDK do DAT somou ~8 MB e a rotulagem visual do prato somou ~14 MB. Os
modelos de IA continuam sendo a menor parte — o peso é biblioteca nativa.

O peso nunca foi a inteligência: são bibliotecas nativas em múltiplas ABIs.

### Onde se troca de modelo

Um lugar só — o `ModelRegistry` em `AppContainer`
([EatControlApp.kt](../app/src/main/java/com/eatcontrolai/EatControlApp.kt)). Nenhuma tela conhece o
ML Kit. Catálogo de candidatos em `benchmark/candidates.yaml`, régua em `docs/MODEL_BENCHMARK.md`.

---

## 7. Próximos passos

1. **Destravar o DAT** (§5-A) — são dois cadastros.
2. Rodar `./scripts/benchmark.sh` no aparelho e registrar os números.
3. Fotografar rótulos reais e colocar no dataset de percepção.
4. Open Food Facts de verdade no lugar do catálogo de demonstração.
5. Medir bateria em 10 minutos de uso.
6. Trilha de prato (visão) — só depois das anteriores, e com incerteza explícita.

---

*Base: leitura completa de `docs/`, do edital, da matriz de ideias, do deck e de todo o código em
`app/src/`.*
