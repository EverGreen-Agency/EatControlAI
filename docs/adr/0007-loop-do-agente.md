# ADR-0007 — O loop do agente

Status: **Accepted** — responde as quatro perguntas norteadoras da palestra do Ideathon (15/08/2026)

## Contexto

A palestra "O Agente Mínimo Viável" (Frederico Barbosa Relvas) muda a ordem das decisões: em óculos
sem display, a primeira decisão **não é qual modelo usar**, e sim como desenhar o loop dentro de três
orçamentos — **milissegundos, miliampères e megabytes**.

O loop tem quatro peças: **gatilho → percepção → política → fala**. E a arquitetura recomendada é uma
**cascata por custo**, onde cada estágio barato autoriza o seguinte. A frase que resume:

> "O LLM é o especialista caro, não o porteiro."

A palestra encerra com quatro perguntas para a entrega de 22/08. Este ADR as responde.

---

## 1. Qual é o gatilho?

**Toque no app, ou o comando de voz "posso comer isso?" pelo botão Falar.**

Não existe alternativa: o wake word "Hey Meta" pertence ao assistente da Meta e **não faz parte do
DAT** (ADR-0006). Nenhum app de terceiro é chamado falando com os óculos. Quem abre a sessão é sempre
o telefone.

Consequência boa: gatilho explícito é o mais barato que existe. Não há wake word rodando o dia todo,
não há detecção de mudança de cena, não há câmera ligada esperando algo acontecer. A palestra é dura
com isso — *"o gatilho errado esvazia a bateria antes do almoço"* — e o nosso simplesmente não gasta
nada enquanto o usuário não pede.

Custo: **≈ 0 mA em repouso.**

## 2. Quais são as ferramentas, onde rodam e por quê?

Três, todas locais.

| Ferramenta | Pergunta que responde | Custo | Onde roda |
| :--- | :--- | :--- | :--- |
| **Leitor de barras** (ML Kit) | "Tem um produto identificável aqui?" | baixo | local |
| **OCR** (ML Kit) | "Tem texto que eu preciso ler?" | médio, sob demanda | local |
| **Catálogo de produtos** | "O que eu já sei sobre este item?" | ≈ zero | local |

Mais entrada e saída: **STT** on-device (`createOnDeviceSpeechRecognizer`) e **TTS** da plataforma.

**Nenhum LLM.** Não é adiamento — é decisão. A política do Eat Control é um motor determinístico com
hierarquia de evidência (`docs/SPEC.md`), e ela é o diferencial defensável do projeto, não um
substituto temporário. Um LLM não decidiria melhor se um rótulo declara leite; decidiria de forma
menos auditável, mais lenta e com dependência de rede no caminho crítico.

Tudo local também mantém a pilha pequena. A palestra estima 100–130 MB para um conjunto típico
(Porcupine, Whisper tiny, YOLO nano, ML Kit, Piper). O nosso usa STT e TTS da plataforma, sem
detector e sem wake word: **os modelos embarcados somam 2,4 MB.**

## 3. Quanto tempo até a primeira sílaba, e o plano se passar de 3 s?

**Alvo: < 1 s.** Régua da palestra: abaixo de 1 s parece instantâneo; entre 1 e 3 s é tolerável com
feedback; acima de 3 s o usuário repete o comando e passam a existir duas requisições concorrentes.

Implementado:

- **Confirmação sonora imediata** (`Earcon`) antes de qualquer inferência. O usuário sabe que foi
  ouvido em dezenas de milissegundos, não quando a resposta fica pronta.
- **Métrica `first_audio_ms`** — interação → primeira sílaba. É a que o usuário sente, e é diferente
  da soma das etapas. `end_to_end_ms` continua medido, mas não é o número que importa para UX.
- **Cascata que evita trabalho**: quando o código de barras resolve, o OCR nem é chamado.

Pendente: **TTS em streaming** (falar antes de terminar de decidir) e a mensagem intermediária
("deixa eu olhar"). Só valem a pena se a medição em aparelho mostrar `first_audio_ms` acima de 1 s —
otimizar antes de medir é o erro que o `MODEL_BENCHMARK.md` já proíbe.

## 4. O que sobrevive entre uma interação e a seguinte?

A palestra separa em três níveis. O nosso estado hoje:

| Nível | O que guarda | Situação |
| :--- | :--- | :--- |
| **Usuário** | perfil, restrições com severidade e política de incerteza, metas, orientações, privacidade | ✅ persistido em DataStore |
| **Histórico** | decisões anteriores, evidências usadas, latência | ✅ persistido |
| **Sessão** | item em foco, última pergunta, refeição em curso | ⚠️ não implementado |
| **Mundo** | "este rótulo eu li há 4 segundos" | ⚠️ não implementado |

O estado de usuário é o que a palestra chama de diferencial — *"sem estado, seu agente responde a
fotos; com estado, ele acompanha uma pessoa"*. Ele existe e muda a decisão de verdade: o mesmo
rótulo produz respostas diferentes conforme a restrição cadastrada.

Falta o estado de **sessão**, que habilitaria o "e isso?" apontando para outro item da mesma
refeição, e o de **mundo**, que é também uma economia de bateria: não reprocessar um rótulo lido há
segundos.

---

## Decisão

Adotar o loop de quatro peças como modelo mental oficial do Eat Control, com roteamento por custo
implementado em `ContextRouter` e a métrica `first_audio_ms` como indicador primário de UX.

## Consequências

- O modo **Automático** vira o padrão da tela de análise: a cascata escolhe a trilha em vez de o
  usuário escolher o modo.
- Respostas faladas ficam limitadas a **15 palavras**, com teste que falha se passar.
- `docs/METRICS.md` ganha `first_audio_ms` e `earcon_ms`.
- Estado de sessão e de mundo entram no backlog como trabalho identificado, não como omissão.

## O que deliberadamente não fazemos

A palestra lista três ideias que não cabem em um dia. Todas já estavam fora do nosso recorte, e
continuam:

- **"observa o dia todo"** — câmera contínua mata bateria e privacidade juntas;
- **assistente conversacional geral** — competiria com a Meta AI, que já mora no dispositivo;
- **vários agentes especialistas coordenando** — cada salto multiplica latência.
