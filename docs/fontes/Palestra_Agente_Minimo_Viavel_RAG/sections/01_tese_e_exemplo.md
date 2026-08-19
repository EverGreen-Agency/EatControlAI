---
title: "Tese e exemplo de referência"
source_file: "Palestra-Agentes-IA-AI-Glasses.pptx.pdf"
source_pages: [3, 4, 5]
author: "Frederico Barbosa Relvas"
date: "2026-08-15"
language: "pt-BR"
rag_role: "semantic_section"
---

# Tese e exemplo de referência

## Síntese fiel ao material

A palestra parte de um cenário em farmácia para mostrar que um agente útil nos óculos não é uma única chamada de modelo. Ele é uma cascata: gatilho, detecção de fala, transcrição, percepção visual, consulta de estado/decisão e fala. A tese central é tratar cada volta do loop como um orçamento de latência, energia e memória.

## Conteúdo fonte por página

### Página 3 — Cenário da farmácia

10h47.
Uma farmácia.
— "Ei Jarvis, posso tomar isso com o que eu já uso?"
Dois segundos de silêncio.
— "Esse é um anti-inflamatório. Ele conflita com o anticoagulante que você registrou
na terça. Vale confirmar com o farmacêutico."

### Página 4 — Seis decisões de projeto

A MESMA CENA, QUADRO A QUADRO
Seis decisões de projeto
Repare no que não apareceu nessa lista: "qual modelo eu uso". Nenhuma das seis decisões é sobre escolher modelo.
#
O que o agente fez
Orçamento gasto
"Ei Jarvis" acorda a cascata
≈ 0 ms · modelo minúsculo, sempre ligado
VAD confirma que há fala e autoriza a escuta
dezenas de ms
STT transcreve a pergunta
200–600 ms
Um frame da câmera → OCR do rótulo
150–400 ms
Consulta o que sabe dela + decide
300 ms a 2 s — a escolha mora aqui
TTS fala 18 palavras
300 ms até a primeira sílaba

### Página 5 — A tese da palestra

A tese desta palestra
Um agente em óculos é um loop:
percepção → processa + decide → fala.

Cada volta desse loop tem um orçamento —
milissegundos, miliampères e megabytes.
Projetar o agente é decidir como gastar esse
orçamento.
