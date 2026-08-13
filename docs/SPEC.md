# SPEC — Core Decision Pipeline

## Input
`InteractionRequest`
- image/frame opcional
- audio opcional
- userProfileId
- timestamp
- scenarioType

## Evidence types
- DECLARED_LABEL
- BARCODE_DATABASE
- OCR_TEXT
- VISUAL_INFERENCE
- USER_CONFIRMATION
- PROFESSIONAL_RULE

## Precedência
1. Regra profissional explícita
2. Informação declarada em rótulo/base confiável
3. Confirmação do usuário
4. Inferência visual

## Decision states
- COMPATIBLE
- INCOMPATIBLE
- INSUFFICIENT_INFORMATION
- NEEDS_CONFIRMATION

## Regra de segurança
Inferência visual, isoladamente, não pode produzir uma afirmação de segurança para alergia/celiaquia.
