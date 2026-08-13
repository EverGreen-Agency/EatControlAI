# SDD — Software Design Description

## Arquitetura
Glasses/DAT
  → Capture Gateway
  → Intent Router
  → Vision / Voice pipelines
  → Evidence Aggregator
  → Deterministic Rule Engine
  → Decision Composer
  → TTS / App output
  → Metrics + History

## Camadas
### Device
Integração DAT, câmera, microfone, speakers, lifecycle e permissões.

### Perception
OCR, barcode, detecção/classificação e STT.

### Evidence
Normaliza evidências em um formato comum:
- source
- type
- value
- confidence
- provenance
- timestamp

### Decision
Regras determinísticas + política de confiança.

### Personalization
Perfil, fase do tratamento, metas, sintomas e preferências.

### Output
Áudio curto + registro no app.

### Observability
Latência por etapa, consumo, versão de modelo, erro e qualidade.
