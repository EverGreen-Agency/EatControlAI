# Métricas

## Métrica norte do MVP
**Decision Success Rate (DSR):**
percentual de cenários de teste em que o sistema retorna o estado esperado sem violar a política de segurança.

## Latência
Registrar:
- capture_ms
- transport_ms
- stt_ms
- ocr_ms
- barcode_ms
- vision_inference_ms
- rule_engine_ms
- tts_start_ms
- end_to_end_ms

Relatar p50, p90, p95 e máximo.

## Qualidade por tarefa
### OCR
- exact match / normalized match
- character error rate (CER)
- word error rate (WER)
- recall de termos críticos ("contém leite", "glúten", etc.)

### Barcode
- decode success rate
- time-to-first-decode
- lookup success rate

### Object detection
- precision
- recall
- mAP@0.5
- mAP@0.5:0.95
- latency/frame
- FPS sustentado

### STT
- WER em pt-BR
- command intent accuracy
- latency por utterance

### TTS
- time-to-first-audio
- completion rate
- avaliação humana de inteligibilidade

## Segurança
- false-safe rate: deve tender a 0 nos casos críticos
- uncertainty recall: % dos casos realmente ambíguos em que o sistema declara incerteza

## Device
- peak RAM
- model size
- package size
- CPU/GPU delegate
- temperatura
- battery drain por 10 min
