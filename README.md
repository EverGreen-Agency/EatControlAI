# Eat Control AI — Hackathon Bootstrap

Scaffold inicial para o projeto do AI Glasses Brasil.

## Decisão-base
- App companion **native Android / Kotlin** para o hackathon.
- Óculos = captura (câmera/microfone) + saída (áudio).
- IA principal = **on-device no smartphone**.
- Arquitetura de modelos com interfaces `plug-and-play` para trocar implementações e executar benchmarks.
- Regras críticas de segurança alimentar separadas da inferência probabilística.

## Trilhas do MVP
1. DAT / conexão / captura / áudio
2. OCR de rótulo
3. Barcode
4. Motor determinístico
5. Perfil do usuário
6. STT/TTS
7. Benchmark de modelos
8. Registro de métricas
9. Análise visual de prato (experimental / confidence-aware)

## Antes de compilar
O Meta DAT está em developer preview. Não há versão/artefato fixado neste scaffold de propósito.
Copie as coordenadas Gradle e requisitos **da versão atual** do README/documentação oficial do DAT
e registre a escolha em `docs/adr/0006-dat-version.md`.
