---
title: "Resumo — O Agente Mínimo Viável"
source_file: "Palestra-Agentes-IA-AI-Glasses.pptx.pdf"
author: "Frederico Barbosa Relvas"
date: "2026-08-15"
language: "pt-BR"
---

# Resumo — O Agente Mínimo Viável

A palestra defende que, em aplicações para AI Glasses sem display, a pergunta principal não é “qual modelo usar?”, mas **como desenhar o loop do agente dentro de limites de latência, bateria e memória**.

## Tese central

Um agente nos óculos funciona como um ciclo:

**percepção → processamento/decisão → fala**

Cada ciclo gasta três orçamentos principais:
- **milissegundos**: tempo até a resposta;
- **miliampères**: energia/bateria;
- **megabytes**: modelos, ativações, caches e buffers.

## Arquitetura proposta

O loop é decomposto em quatro peças:
1. **Gatilho** — quando o agente acorda;
2. **Percepção** — o que vale observar;
3. **Política** — como decide e qual ferramenta chama;
4. **Fala** — como responde em áudio.

A apresentação recomenda uma **cascata por custo**. Componentes baratos filtram e autorizam os mais caros. O LLM deve ser usado apenas quando houver necessidade real de raciocínio.

## Ferramentas citadas

- Wake word + VAD;
- STT;
- detector de objetos (YOLO / ML Kit);
- OCR;
- landmarks;
- memória/histórico;
- LLM;
- TTS.

## Estado e memória

O diferencial não está apenas em interpretar um frame isolado. O agente deve preservar:
- **estado de sessão**;
- **estado do usuário**;
- **estado do mundo/ambiente**.

A formulação da palestra é: sem estado, o agente responde a fotos; com estado, acompanha uma pessoa.

## Latência

O material trata o tempo até a primeira sílaba como parte da UX:
- **< 1 s**: alvo, sensação de resposta instantânea;
- **1–3 s**: aceitável com feedback sonoro;
- **> 3 s**: risco de o usuário repetir o comando.

Técnicas recomendadas:
- confirmação sonora imediata;
- TTS em streaming;
- mensagem intermediária antes da resposta final.

## Local versus nuvem

A decisão deve ser feita por componente:
- Wake word: local;
- VAD: local;
- detector/OCR: forte candidato a local;
- STT: forte candidato a local;
- TTS: forte candidato a local;
- raciocínio: depende; nuvem pode ser usada quando aumenta substancialmente a qualidade.

## Bateria

O principal princípio é **não processar o que não precisa ser processado**:
- evitar câmera contínua;
- gatilho por evento;
- usar poucos frames;
- cachear resultados recentes;
- descartar frames enquanto uma inferência já está em andamento;
- evitar chamar LLM a cada turno.

## Memória de execução

A apresentação lembra que o app não roda “um modelo”, e sim uma pilha. Exemplos indicados:
- Porcupine + Silero;
- Whisper tiny INT8;
- YOLO nano INT8;
- ML Kit on-device;
- Piper pt-BR.

O subtotal citado para entrada/saída é de aproximadamente **100–130 MB**, antes de adicionar um LLM local. Um LLM 3B INT4 adicionaria aproximadamente **1,5 GB** de pesos, sem contar ativações, KV cache e buffer do DAT.

## Recorte de MVP

A “receita que cabe” é:
- **1 gatilho nomeável**;
- **2 a 3 ferramentas**;
- **1 resposta falada de até 15 palavras**;
- **1 estado** que persiste para a próxima volta do loop.

A palestra desaconselha, para um MVP de um dia:
- observar continuamente o dia inteiro;
- criar um assistente conversacional geral;
- coordenar muitos agentes especialistas.

## Quatro perguntas para o projeto

1. Qual é o gatilho?
2. Quais ferramentas serão usadas, onde rodam e por quê?
3. Qual o tempo até a primeira sílaba e qual o fallback se passar de 3 segundos?
4. O que o agente lembra entre uma interação e a seguinte?
