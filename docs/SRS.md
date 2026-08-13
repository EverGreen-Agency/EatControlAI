# SRS — Software Requirements Specification

## Requisitos funcionais
- FR-001: parear/registrar sessão com dispositivo Meta compatível.
- FR-002: receber frame/foto da câmera dos óculos.
- FR-003: receber áudio/voz e gerar transcrição.
- FR-004: executar OCR local.
- FR-005: detectar/ler código de barras localmente.
- FR-006: consultar fonte nutricional/produto quando disponível.
- FR-007: carregar perfil do usuário.
- FR-008: executar regras determinísticas críticas.
- FR-009: executar inferência visual opcional com score de confiança.
- FR-010: gerar resposta curta.
- FR-011: sintetizar áudio e rotear aos óculos.
- FR-012: registrar métricas de cada etapa.
- FR-013: permitir trocar providers/modelos sem alterar o fluxo de domínio.

## Requisitos não funcionais
- NFR-001: núcleo demonstrável sem nuvem.
- NFR-002: p95 de interação alvo inicial < 2,5 s para fluxo OCR/barcode; meta agressiva < 1,5 s.
- NFR-003: nenhum estado de "seguro" derivado apenas de inferência probabilística.
- NFR-004: falhas devem degradar para "informação insuficiente".
- NFR-005: telemetria não deve armazenar imagem bruta por padrão.
- NFR-006: inferência deve ser benchmarkada em aparelho físico.
- NFR-007: cada provider deve expor versão, runtime, hardware e métricas.
