---
title: "Anatomia do agente"
source_file: "Palestra-Agentes-IA-AI-Glasses.pptx.pdf"
source_pages: [7, 8, 9, 10]
author: "Frederico Barbosa Relvas"
date: "2026-08-15"
language: "pt-BR"
rag_role: "semantic_section"
---

# Anatomia do agente

## Síntese fiel ao material

O agente é decomposto em quatro peças: gatilho, percepção, política e fala. A política não deve usar o LLM como porta de entrada para tudo; detector, OCR, landmarks e memória são ferramentas de custos distintos. O sistema deve rotear por custo e manter estado de sessão, usuário e mundo.

## Conteúdo fonte por página

### Página 7 — Gatilho, percepção, política e fala

O LOOP TEM QUATRO PEÇAS
Gatilho, percepção, política, fala
Geralmente temos bem definido o nossa peça 3, mas se nossas outras peças não estiverem tão bem definidas isso pode
trazer problemas!
GATILHO
Quando eu acordo? Wake
word, gesto, evento de
sensor, mudança de cena.
O gatilho errado esvazia a
bateria antes do almoço.
PERCEPÇÃO
O que eu olho? Um frame?
Três por segundo? Cada
frame custa banda
Bluetooth e inferência.
POLÍTICA
O que eu faço? Regras,
roteador ou LLM.
Aqui mora a inteligência —
e é aqui que a latência
estoura.
FALA
O que eu digo? Sem
display, a resposta falada
é a interface inteira.

### Página 8 — O modelo escolhe o especialista

ANCORANDO NA AULA 7
O modelo escolhe o especialista
Cada tarefa do catálogo de visão da Aula 7 vira uma ferramenta — e a política decide qual chamar.
Um bom sistema chama o LLM o mínimo possível. O LLM é o especialista caro, não o porteiro.
Ferramenta
A pergunta que ela responde
Custo
Detector (YOLO / ML Kit)
"Tem algo relevante no quadro?"
Baixo
OCR
"Tem texto que eu preciso ler?"
Médio, sob demanda
Landmarks
"O usuário está apontando para alguma coisa?"
Baixo
Memória / histórico
"O que eu já sei sobre essa pessoa?"
≈ zero
LLM
"Preciso raciocinar sobre isso?"
Alto — última instância

### Página 9 — A cascata é roteamento por custo

ANCORANDO NA AULA 8
A cascata é roteamento por custo
A cascata de ativação não é um detalhe de voz. É o padrão de arquitetura do agente inteiro.
WAKE WORD
sempre ligado
custo ≈ 0
VAD
fala ou silêncio?
custo baixo
STT
só após fala
custo médio
POLÍTICA
só se houver pedido
custo alto
AÇÃO / FALA
resposta por áudio
Generalize para visão: houve movimento? → tem objeto relevante? → vale ler o texto? → vale raciocinar? Cada
estágio só autoriza o próximo; os estágios caros ficam dormentes.

### Página 10 — Estado: onde mora o diferencial

O QUE SOBREVIVE ENTRE AS VOLTAS
Estado: onde mora o diferencial
Os óculos veem em primeira pessoa, o dia inteiro. O diferencial não está no frame — está no que o agente
lembra do frame anterior.
Sem estado, seu agente responde a fotos. Com estado, ele acompanha uma pessoa.
SESSÃO
O que está acontecendo agora: os
últimos segundos, o objeto em foco, a
intenção que ficou pendente.
USUÁRIO
O que ele já registrou, prefere, toma,
evita ou está procurando. É o que
transforma resposta genérica em
resposta dele.
MUNDO
O que o agente já aprendeu do
ambiente: "esse rótulo eu li há 4
segundos", "essa sala eu já mapeei".
