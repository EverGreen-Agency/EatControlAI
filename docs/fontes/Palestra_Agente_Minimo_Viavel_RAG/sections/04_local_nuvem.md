---
title: "Onde cada peça deve rodar"
source_file: "Palestra-Agentes-IA-AI-Glasses.pptx.pdf"
source_pages: [13]
author: "Frederico Barbosa Relvas"
date: "2026-08-15"
language: "pt-BR"
rag_role: "semantic_section"
---

# Onde cada peça deve rodar

## Síntese fiel ao material

A decisão local versus nuvem deve ser feita peça por peça. Wake word e VAD devem ser locais; detector/OCR, STT e TTS têm forte justificativa para rodar localmente; raciocínio pode ir à nuvem quando isso compra qualidade.

## Conteúdo fonte por página

### Página 13 — Local não é tudo ou nada

ORÇAMENTO 2 — ONDE CADA PEÇA RODA
Local não é tudo ou nada
Local não é obrigatório — mas dá pontos extras. A pergunta é peça por peça, não tudo ou nada.
Peça
Local?
Por quê
Wake word
Sempre
Nuvem aqui seria áudio em streaming 24/7.
VAD
Sempre
Barato, e evita mandar silêncio para a rede.
Detector / OCR
Vale muito
Já são leves.
STT
Vale muito
Whisper tiny ou Vosk dão conta de comando curto.
TTS
Vale muito
Piper pt-BR. Sustenta o argumento de privacidade.
Raciocínio
Depende
Onde a nuvem realmente compra qualidade.
