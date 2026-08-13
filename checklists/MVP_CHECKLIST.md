# MVP Checklist

> Atualizado em 13/08/2026. `[~]` = parcial.
> Regra deste arquivo: só marcar `[x]` o que roda e tem teste. Item marcado sem prova vira surpresa
> ruim no dia do hackathon.

## Produto

- [ ] Congelar recorte do MVP
- [x] Congelar persona principal — paciente GLP-1 ("João"), `docs/PRD.md` P1
- [x] Definir frase de valor — "Inteligência alimentar para quem usa GLP-1"
- [x] Definir limites de segurança — `docs/00_PRODUCT_OVERVIEW.md`, implementados no motor
- [x] Template da Entrega Final — sai no Ideathon (15/08)

## DAT

- [ ] **Criar projeto no Wearables Developer Center** → emite `APPLICATION_ID` e `CLIENT_TOKEN`
- [ ] **Gerar token do GitHub com escopo `read:packages`** → sem ele o Gradle não baixa o SDK
- [x] Registrar versão DAT no ADR — 0.9.0, `docs/adr/0006-dat-version.md`
- [ ] Sync Gradle com os artefatos `com.meta.wearable:mwdat-*`
- [ ] Developer Mode nos óculos
- [ ] Mock Device Kit oficial (`mwdat-mockdevice`) — bloqueado pelos dois primeiros itens
- [x] Substituto próprio: `MockGlassesGateway`, sem depender de credencial
- [ ] Registro/permissões do toolkit
- [ ] Foto/stream pelos óculos
- [ ] Áudio HFP
- [ ] TTS roteado para os óculos
- [ ] Teste em hardware real

## AI

- [x] OCR baseline — ML Kit on-device (`MlKitOcrProvider`)
- [x] Barcode baseline — ML Kit on-device (`MlKitBarcodeProvider`), decodificação verificada por
      teste instrumentado sobre barras EAN-13 renderizadas
- [~] Base de produtos — catálogo **local de demonstração** (`BarcodeRepository`), 4 EANs fictícios.
      A integração real com Open Food Facts / TBCA continua pendente
- [x] STT baseline — `AndroidSttProvider` com `createOnDeviceSpeechRecognizer` (sem rede quando o
      aparelho suporta), pt-BR
- [x] TTS baseline — `AndroidTtsProvider`, pt-BR, com *time-to-first-audio*
- [x] Rule engine — `FoodDecisionEngine` com precedência de evidência e os 4 estados
- [x] Dataset de decisão — 19 cenários em `benchmark/decision_scenarios.csv`, DSR no build
- [ ] Dataset de percepção — faltam **fotos reais** de rótulo e de embalagem; hoje as imagens são
      renderizadas
- [ ] Modelo visual somente se necessário
- [ ] Quantização quando houver modelo customizado
- [ ] Benchmark em celular físico — harness pronto (`./scripts/benchmark.sh`), falta rodar e registrar

## Demo

- [x] Rótulo "contém leite" — cenário `DEC-001`; o caso mais forte é `DEC-009` (iogurte "zero
      lactose" que declara `CONTÉM LEITE` no verso)
- [x] Barcode — EAN lido de verdade, cruzado com o catálogo, com caso de produto desconhecido
- [x] Caso ambíguo — `DEC-006` e `DEC-014`
- [x] Resposta por áudio
- [x] Pergunta por voz — comando falado escolhe a trilha e dispara a análise
- [x] Métricas visíveis — latência por etapa na folha de resultado
- [x] Plano B se óculos falharem — `PhoneCameraGateway` (câmera do celular) e `MockGlassesGateway`,
      trocáveis por um toque na tela de análise

## Privacidade e bateria (checkpoints pontuados no dia 18/09)

- [x] Requisitos escritos — `NFR-008` e `NFR-009` no `SRS.md`
- [x] Nenhuma imagem gravada em disco; persistência só de texto (perfil, preferências, decisões)
- [x] Telemetria só com latência, provider e versão de modelo
- [x] Tela de privacidade com opt-in e padrões conservadores, mais "apagar histórico"
- [x] Captura sob demanda — a câmera liga só na tela de análise e desliga ao sair; sem stream contínuo
- [ ] Medir consumo em 10 min de uso no aparelho físico
- [ ] Medir tamanho do APK de release e registrar
