---
title: "Bateria e memória"
source_file: "Palestra-Agentes-IA-AI-Glasses.pptx.pdf"
source_pages: [14, 15]
author: "Frederico Barbosa Relvas"
date: "2026-08-15"
language: "pt-BR"
rag_role: "semantic_section"
---

# Bateria e memória

## Síntese fiel ao material

O maior ganho de bateria vem de evitar processamento desnecessário: evento em vez de varredura, um frame por interação, cache e descarte de frames. A pilha completa inclui vários modelos e buffers; a apresentação estima cerca de 100–130 MB para entrada/saída antes de adicionar um LLM local.

## Conteúdo fonte por página

### Página 14 — Bateria é decisão, não otimização

ORÇAMENTO 3 — MILIAMPÈRES
Bateria é decisão, não otimização
O maior ganho de bateria de um agente não vem de quantizar o modelo. Vem de decidir não olhar.
O que drena
Câmera em stream contínuo.
Inferência a cada frame que chega.
LLM chamado a cada turno de conversa.
Wake word mal calibrada disparando sozinha.
O que preserva
Gatilho por evento, não por varredura.
Um frame por interação, não trinta por segundo.
Cache de resultado: "esse rótulo eu li há 4 s".
Descarte de frames durante a inferência (Aula 7).

### Página 15 — Você não roda um modelo. Roda cinco.

ORÇAMENTO 4 — MEGABYTES
Você não roda um modelo. Roda cinco.
Some ativações, KV cache e o buffer do DAT. Se o plano é um LLM local de 7B, faça essa conta hoje!
Peça do agente
Modelo típico
Pesos (aprox.)
Wake word + VAD
Porcupine + Silero
3–7 MB
STT
Whisper tiny INT8
~40 MB
Detector
YOLO nano INT8
3–6 MB
OCR
ML Kit on-device
~20 MB
TTS
Piper pt-BR
30–60 MB
Subtotal de entrada e saída
~100–130 MB
+ LLM local 3B INT4
+1,5 GB
