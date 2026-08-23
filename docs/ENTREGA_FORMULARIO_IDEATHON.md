# Entrega Ideathon — respostas para o formulário

Prazo: **22/08/2026**. Uma resposta por equipe; o formulário salva progresso.

> **Rascunho para a equipe revisar antes de colar.** Os limites de caracteres são rígidos; cada
> campo traz a contagem verificada.

## Por que este arquivo existe

O `ENTREGA_FINAL_A1_A7.md` foi escrito como documento longo. **O formulário não aceita documento
longo.** As diferenças que importam:

| Campo | O que assumíamos | O que o formulário pede |
|---|---|---|
| A5 | seção de arquitetura | **3 a 5 trade-offs** no formato "Fizemos X em vez de Y", com ganho e custo |
| A6 | benchmark competitivo | **2 concorrentes**, nome (80) + diferencial (160) |
| A7 | discurso sobre IA e privacidade | **os 5 checkpoints do edital**, 200 caracteres cada |
| Diagrama | ilustrativo | **um nó por checkpoint**, tecnologias nomeadas, imagem + código Mermaid |

O formulário avisa: **a Meta AI nativa conta como concorrente.** Ignorá-la em A6 seria o erro mais
caro desta entrega.

---

## Identificação

**Nome da equipe:** selecionar na lista, conforme a inscrição.

**Trilha temática:** precisa ser a **mesma declarada na inscrição** — `Saúde` ou `Bem-Estar`.
Conferir antes de enviar; divergência aqui é problema formal.

**Título da solução** (máx. 100)

```
Eat Control AI
```

**Resumo em uma frase** (máx. 160)

```
Óculos que respondem por áudio se um alimento cabe no seu tratamento com GLP-1, cruzando o que você olha ou pergunta com seu perfil clínico.
```

---

## A1 — O problema (máx. 400)

```
Quem usa GLP-1 decide o que comer no mercado ou no restaurante, longe da consulta. A informação está no rótulo, mas em letra pequena, invertida ou contraditória: um iogurte anunciado como "zero lactose" declara leite no verso. Parar, sacar o celular, achar o app e digitar não acontece. A pessoa decide por aparência, e registra depois ou nunca.
```

## A2 — Usuário-alvo (máx. 300)

```
João, 45 anos, seis meses de GLP-1, acompanhado por endocrinologista. Sente saciedade precoce e desconforto após refeições gordurosas. Faz compras semanais e come fora três vezes por semana. Usaria os óculos várias vezes ao dia, segundos por vez, com as mãos ocupadas.
```

## A3 — Walkthrough de interação (mínimo 5 passos numerados)

```
1. João segura um pote no mercado, olha para o rótulo e diz "posso comer isso?". O microfone dos óculos capta por Bluetooth HFP e o STT roda no aparelho (SpeechRecognizer on-device), sem rede.
2. Um som curto confirma que ele foi ouvido, antes de qualquer processamento.
3. A câmera dos óculos captura UM frame via Meta DAT 0.9.0: createSession, addCamera, capturePhoto, e encerra a sessão.
4. Cascata por custo: ML Kit Barcode Scanning primeiro, porque é barato. Se acha o EAN, consulta o catálogo local e o OCR nem roda.
5. Sem código de barras, roda ML Kit Text Recognition. Um roteador determinístico decide pelo texto se é rótulo, cardápio ou prato.
6. O parser extrai declarações do fabricante, como "ALÉRGICOS: CONTÉM LEITE", e as promove a evidência de alta precedência.
7. O motor determinístico cruza com o perfil de João e devolve um de quatro estados: compatível, incompatível, precisa confirmar, informação insuficiente.
8. O TTS pt-BR fala em até 15 palavras pelos alto-falantes open-ear: "Não está alinhado ao seu perfil. Leite está declarado no rótulo."
9. A decisão entra no histórico local do celular e a imagem é descartada da memória.
```

## A4 — Walkthrough de exceção

```
Cenário: o molho do prato pode conter leite, e a foto não resolve.

Como o sistema percebe que deu errado: o rotulador de imagem identifica "molho cremoso" acima do limiar, mas nenhuma evidência declarada fala de leite. O motor fica com uma restrição crítica sem resposta, e a regra do produto é que ausência de declaração nunca vira permissão.

Como ele reage: não afirma nem libera. Classifica como PRECISA CONFIRMAR e formula uma pergunta em vez de um veredito. A inferência visual tem a menor precedência da hierarquia e nunca prova ausência de ingrediente.

Como o usuário fica sabendo: por áudio, nos óculos, em frase curta — "Não consigo confirmar isso apenas pela imagem. Quer me dizer os ingredientes?". A resposta dele entra como evidência de precedência alta e o motor recalcula na hora. Se ele não responder, nada entra no histórico.
```

---

## A5 — Trade-offs

### A5[1]

**a** (máx. 80)

```
Fizemos decisão por regras determinísticas em vez de LLM
```

**b** (máx. 160)

```
Resposta auditável e reproduzível, sem rede no caminho crítico. Num app que diz se você pode comer algo, alucinação não é bug tolerável.
```

**c** (máx. 160)

```
Custa generalização: cada regra é escrita e testada à mão. Mitigamos com hierarquia de evidência e o estado "informação insuficiente" quando não há regra.
```

### A5[2]

**a** (máx. 80)

```
Fizemos captura de um frame sob demanda em vez de stream contínuo
```

**b** (máx. 160)

```
Câmera contínua drena os óculos e é o pior cenário de privacidade. A sessão abre, pega um frame e fecha em finally, mesmo se a análise falhar.
```

**c** (máx. 160)

```
Custa proatividade: o app não avisa sozinho, só responde quando chamado. Mitigamos com gatilho barato, sem wake word ligada o dia todo.
```

### A5[3]

**a** (máx. 80)

```
Fizemos OCR e leitura de barras locais em vez de na nuvem
```

**b** (máx. 160)

```
Funciona em modo avião e no mercado sem sinal, com latência previsível. A imagem nunca sai do aparelho, o que sustenta a promessa de privacidade.
```

**c** (máx. 160)

```
Custa 11 MB de biblioteca nativa por ABI e precisão menor que modelos grandes. Mitigamos com split por ABI e declarando incerteza quando a leitura falha.
```

### A5[4]

**a** (máx. 80)

```
Fizemos push-to-talk em vez de wake word própria sempre ligada
```

**b** (máx. 160)

```
O gatilho custa zero em repouso. O "Hey Meta" pertence ao assistente da Meta e não é acessível a apps de terceiros pelo DAT.
```

**c** (máx. 160)

```
Custa toque para abrir a interação. Mitigamos com sessão curta pós-toque: perguntas seguintes ("e isso?") não pedem novo toque, na mesma janela.
```

### A5[5]

**a** (máx. 80)

```
Fizemos motor de 4 estados em vez de resposta binária sim/não
```

**b** (máx. 160)

```
Distingue "não pode" de "não sei", em vez de simplificar os dois numa negativa. É a mesma lógica que sustenta cada resposta com evidência, não só um veredito.
```

**c** (máx. 160)

```
Custa uma resposta mais longa que sim/não. Mitigamos com o limite de 15 palavras por fala, testado, para a interação caber em segundos.
```

---

## A6 — Âncora de originalidade

### A6[1]

**Nome** (máx. 80)

```
Meta AI nativa dos óculos
```

**Diferencial** (máx. 160)

```
A Meta AI descreve o que vê. Nós cruzamos com a restrição cadastrada, citamos a origem de cada informação, declaramos incerteza e guardamos histórico local.
```

### A6[2]

**Nome** (máx. 80)

```
Cal AI e Foodvisor, apps que estimam macros por foto
```

**Diferencial** (máx. 160)

```
Eles entregam um número imediato de calorias. Nós recusamos macro por foto sem quantidade e fonte confirmadas, e respondemos por áudio sem usar as mãos.
```

---

## A7 — Os cinco checkpoints (máx. 200 cada)

**A7.1 — Uso de IA** (máx. 200)

```
ML Kit on-device: OCR, leitura de código de barras e rotulagem de imagem. STT on-device do Android. A decisão é determinística e auditável. Comprovação: 172 testes automatizados, DSR 100%.
```

**A7.2 — Câmera ou microfone** (máx. 200)

```
A câmera dos óculos é o canal principal, via Meta DAT 0.9.0, capturando um frame sob demanda. O microfone entra por Bluetooth HFP, já que o DAT não expõe API de áudio.
```

**A7.3 — Output por áudio** (máx. 200)

```
TTS do Android com voz pt-BR local, pelos alto-falantes open-ear. Resposta limitada a 15 palavras, com teste que falha se passar. Um som curto confirma antes do processamento.
```

**A7.4 — Privacidade e dados** (máx. 200)

```
Tudo local: nenhuma imagem sai do aparelho nem é gravada em disco. Histórico só no celular, backup em nuvem desativado e botão real de apagar. Sem conta remota nem sincronização.
```

**A7.5 — Eficiência de bateria** (máx. 200)

```
Captura por evento, nunca stream contínuo; a sessão dos óculos fecha em finally após cada análise. A cascata evita rodar OCR quando o código de barras resolve. Split por ABI reduz o pacote.
```

---

## Seção B — Diagrama

**B1:** exportar PNG ou SVG de [mermaid.live](https://mermaid.live) com o diagrama de checkpoints de
[`DIAGRAMA_ARQUITETURA.md`](DIAGRAMA_ARQUITETURA.md) §0.

**B2:** enviar o mesmo código Mermaid em `.txt`.

Requisitos do formulário: um nó por checkpoint, tecnologias nomeadas nos nós, direção do fluxo com
setas.

---

## Seção C — Vídeo

Link acessível até o fim da avaliação, **entre 2 e 3 minutos**. Roteiro em
[`ROTEIRO_VIDEO.md`](ROTEIRO_VIDEO.md).

---

## O que mudou desde a inscrição (máx. 500)

```
A inscrição propunha um copiloto para GLP-1 e restrições em geral. Estreitamos para GLP-1 como público inicial, com restrições como expansão. A arquitetura ficou local-first, sem nuvem no caminho crítico, e a decisão virou motor determinístico com hierarquia de evidência, não modelo generativo. Acrescentamos quatro trilhas com roteamento automático por custo: produto, rótulo, cardápio e prato assistido. E passamos a tratar "não sei" como resposta legítima, com estado próprio.
```

## Por que vocês mudaram (máx. 500)

```
Três motivos. A palestra do Ideathon mostrou que a primeira decisão é o desenho do loop dentro dos orçamentos de latência, bateria e memória, não a escolha do modelo. A documentação do DAT mostrou que os óculos não entregam áudio e que a captura é por sessão curta, o que fechou o desenho de gatilho e consumo. E a validação com endocrinologista delimitou o que a imagem pode afirmar: identifica item visível, nunca prova ausência de ingrediente oculto.
```

---

## Confirmações finais

- **Coerência entre artefatos:** conferir que documento, diagrama e vídeo contam a mesma história.
- **Autoria e uso de IA:** as decisões técnicas são da equipe; IA apoiou a redação.
- **Escopo mantido:** o recorte estreitou dentro do mesmo problema e público. Se a equipe entender
  que isso configura mudança de escopo, marcar a segunda opção — os dois campos acima já respondem.
