---
title: "O Agente Mínimo Viável — Palestra AI Glasses Brasil"
source_file: "Palestra-Agentes-IA-AI-Glasses.pptx.pdf"
author: "Frederico Barbosa Relvas"
organization: "CEIA / UFG"
event: "Ideathon · AI Glasses Brasil"
date: "2026-08-15"
document_type: "presentation"
language: "pt-BR"
rag_role: "source_transcription"
---

# O Agente Mínimo Viável

> Boas práticas para IA local, latência e desenho de interação por voz sem display.

Este arquivo preserva o conteúdo da apresentação por página/slide, com metadados de proveniência para facilitar recuperação em RAG.



---

## Página 1 — O Agente Mínimo Viável

**Seção:** Abertura  
**Tags:** AI Glasses Brasil, IA local, latência, voz, sem display

IDEATHON · AI GLASSES BRASIL
O Agente Mínimo
Viável
Boas práticas para IA local, latência e desenho
de interação por voz sem display
Frederico Barbosa Relvas · CEIA / UFG
15 de agosto de 2026


---

## Página 2 — Quem é Frederico Relvas

**Seção:** Abertura  
**Tags:** autor, CEIA, UFG, AKCIT

Me apresentando...
Quem sou eu?
Frederico Relvas
🎓 Bacharelando em Inteligência Artificial (UFG)
🔎 Pesquisador em Agentes & Visão (CEIA - UFG)
💼 Mentor de Conteúdo & Educação (AKCIT - UFG)


---

## Página 3 — Cenário da farmácia

**Seção:** Tese e exemplo  
**Tags:** wake word, medicamento, memória, resposta por voz

10h47.
Uma farmácia.
— "Ei Jarvis, posso tomar isso com o que eu já uso?"
Dois segundos de silêncio.
— "Esse é um anti-inflamatório. Ele conflita com o anticoagulante que você registrou
na terça. Vale confirmar com o farmacêutico."


---

## Página 4 — Seis decisões de projeto

**Seção:** Tese e exemplo  
**Tags:** VAD, STT, OCR, TTS, latência, cascata

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


---

## Página 5 — A tese da palestra

**Seção:** Tese e exemplo  
**Tags:** loop, orçamento, milissegundos, miliampères, megabytes

A tese desta palestra
Um agente em óculos é um loop:
percepção → processa + decide → fala.

Cada volta desse loop tem um orçamento —
milissegundos, miliampères e megabytes.
Projetar o agente é decidir como gastar esse
orçamento.


---

## Página 6 — Parte 1 — Anatomia do agente

**Seção:** Anatomia do agente  
**Tags:** seção

PARTE 1
Anatomia
do agente


---

## Página 7 — Gatilho, percepção, política e fala

**Seção:** Anatomia do agente  
**Tags:** gatilho, percepção, política, fala

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


---

## Página 8 — O modelo escolhe o especialista

**Seção:** Anatomia do agente  
**Tags:** YOLO, ML Kit, OCR, landmarks, LLM, memória

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


---

## Página 9 — A cascata é roteamento por custo

**Seção:** Anatomia do agente  
**Tags:** wake word, VAD, STT, política, roteamento

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


---

## Página 10 — Estado: onde mora o diferencial

**Seção:** Anatomia do agente  
**Tags:** estado, sessão, usuário, mundo, memória

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


---

## Página 11 — Parte 2 — O orçamento

**Seção:** Orçamento  
**Tags:** seção

PARTE 2
O orçamento


---

## Página 12 — Latência é a interface

**Seção:** Orçamento  
**Tags:** latência, primeira sílaba, TTS streaming, feedback

ORÇAMENTO 1 — MILISSEGUNDOS
Latência é a interface
Sem display não há barra de progresso. O usuário só percebe o silêncio — e interpreta silêncio como falha.
Tempo até a primeira sílaba
O que o usuário faz
Menos de 1 s
Percebe como instantâneo. É o alvo.
1 a 3 s
Tolera — se houver um som de confirmação no começo.
Mais de 3 s
Repete o comando. Agora você tem duas requisições concorrentes e um usuário
irritado.
Três técnicas que compram tempo:
Confirme antes de pensar — um "tic/ping" no início.
Fale enquanto pensa — TTS em streaming: a primeira frase sai antes da última decisão.
Prometa e volte — "deixa eu olhar" agora, resposta em 4 s. Melhor que 4 s de nada.


---

## Página 13 — Local não é tudo ou nada

**Seção:** Orçamento  
**Tags:** on-device, nuvem, Whisper tiny, Vosk, Piper

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


---

## Página 14 — Bateria é decisão, não otimização

**Seção:** Orçamento  
**Tags:** bateria, câmera, cache, frames, eventos

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


---

## Página 15 — Você não roda um modelo. Roda cinco.

**Seção:** Orçamento  
**Tags:** memória, INT8, INT4, YOLO nano, Whisper, Piper

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


---

## Página 16 — Parte 3 — O que cabe e o que não cabe

**Seção:** Escopo do MVP  
**Tags:** seção

PARTE 3
O que cabe
e o que não cabe


---

## Página 17 — Ideias que não fecham em um dia

**Seção:** Escopo do MVP  
**Tags:** MVP, escopo, agente estreito, latência, privacidade

O QUE NÃO CABE
Ideias que não fecham em um dia
A ideia
Por que não cabe
"Observa o dia todo e avisa quando for relevante"
Câmera contínua: bateria e privacidade morrem juntas.
"Um assistente conversacional geral"
Você compete com a Meta AI, que já mora no dispositivo.
"Cinco agentes especialistas se coordenando"
Cada salto multiplica latência.
A receita que cabe — um agente estreito:
1 gatilho nomeável · 2 a 3 ferramentas, não mais.
1 resposta falada de até quinze palavras · 1 estado que sobrevive à volta seguinte do loop.
Estreito não é pequeno: "ler rótulo de remédio para quem não enxerga bem" é estreito e é enorme.


---

## Página 18 — Quatro perguntas norteadoras

**Seção:** Escopo do MVP  
**Tags:** entrega, gatilho, ferramentas, latência, estado

PARA A ENTREGA DO DIA 22
Quatro perguntas norteadoras
A farmácia do começo era isto: um gatilho, três ferramentas, uma resposta falada.
Qual é o gatilho?
Quais são as ferramentas? Qual roda local, qual na nuvem, e por quê?
Quanto tempo até a primeira sílaba? Qual é o plano se passar de 3 s?
O que o agente lembra entre uma interação e a seguinte?


---

## Página 19 — Obrigado

**Seção:** Encerramento  
**Tags:** encerramento

Obrigado!
O Agente Mínimo Viável
Frederico Barbosa Relvas · CEIA / UFG
aiglassesbrasil@ceia.ufg.br
