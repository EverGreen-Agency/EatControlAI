# MVP Checklist

> Atualizado em 13/08/2026. `[~]` = parcial. Itens sem dono são o gargalo real.

## Produto
- [ ] Congelar recorte do MVP
- [x] Congelar persona principal — paciente GLP-1 ("João"), `docs/PRD.md` P1
- [x] Definir frase de valor — "Inteligência alimentar para quem usa GLP-1"
- [x] Definir limites de segurança — `docs/00_PRODUCT_OVERVIEW.md` + implementados em código
- [ ] **Obter o template obrigatório da Entrega Final da Ideia (22/08)**

## DAT
- [ ] **Criar projeto no Wearables Developer Center** → emite `APPLICATION_ID` e `CLIENT_TOKEN`
- [ ] **Gerar token do GitHub com escopo `read:packages`** → sem ele o Gradle não baixa o SDK
- [x] Registrar versão DAT no ADR — 0.9.0, `docs/adr/0006-dat-version.md`
- [ ] Sync Gradle com os artefatos `com.meta.wearable:mwdat-*`
- [ ] Developer Mode nos óculos
- [ ] Mock Device Kit oficial (`mwdat-mockdevice`) — bloqueado pelos dois primeiros itens
- [~] Substituto próprio: `MockGlassesGateway` funcionando, sem depender de credencial
- [ ] Registro/permissões
- [ ] Foto/stream
- [ ] Áudio HFP
- [ ] TTS nos óculos
- [ ] Teste em hardware real

## AI
- [x] OCR baseline — ML Kit on-device, `MlKitOcrProvider`
- [x] Barcode baseline — ML Kit on-device (`MlKitBarcodeProvider`) + Open Food Facts cache (`BarcodeRepository`)
- [x] STT baseline — `AndroidSttProvider` on-device
- [x] TTS baseline — `AndroidTtsProvider`, pt-BR, com métrica de *time-to-first-audio*
- [x] Rule engine — `FoodDecisionEngine` com precedência de evidência e os 4 estados
- [~] Dataset de benchmark — 15 cenários de decisão em `benchmark/decision_scenarios.csv`;
      faltam imagens reais para OCR/barcode/visão
- [ ] Modelo visual somente se necessário
- [ ] Quantização quando houver modelo customizado
- [ ] Benchmark em celular físico

## Demo
- [x] Rótulo "contém leite" — cenário `DEC-001`, e o caso melhor: `DEC-009` (iogurte "zero lactose"
      que declara `CONTÉM LEITE` no verso)
- [x] Barcode — busca por EAN 13/8 com promoção de evidência
- [x] Caso ambíguo — `DEC-006` e `DEC-014`
- [x] Resposta por áudio
- [x] Métricas visíveis — latência por etapa na tela de análise
- [x] Plano B se óculos falharem — `CameraXGlassesGateway` + `MockGlassesGateway`

## Privacidade e bateria (checkpoints pontuados no dia 18/09)
- [x] Nenhuma imagem persistida; telemetria só com números — requisitos `NFR-008` e `NFR-009` no `SRS.md`
- [ ] Tela de privacidade com opt-in (salvar fotos / usar dados para melhoria)
- [ ] Estratégia de bateria documentada (captura sob demanda, duty-cycle, sem stream contínuo)
- [ ] Medir consumo em 10 min de uso no aparelho físico

