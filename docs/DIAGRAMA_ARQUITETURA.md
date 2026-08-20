# Diagramas de arquitetura — Eat Control AI

Versão: 1.0
Data: 18/08/2026
Formato: Mermaid

> Google Docs não renderiza Mermaid. Para incluir no documento final, cole o código em
> [mermaid.live](https://mermaid.live), exporte PNG ou SVG e insira a imagem.

## 1. Fronteiras: óculos, smartphone e nuvem

```mermaid
flowchart LR
    subgraph OCULOS["Ray-Ban Meta"]
        CAM["Câmera"]
        MIC["Microfones"]
        SPK["Alto-falantes"]
    end

    subgraph FONE["Smartphone Android"]
        DAT["DAT: sessão curta de captura"]
        BT["Bluetooth: A2DP saída, HFP voz"]
        PERC["Percepção no aparelho"]
        DEC["Motor determinístico"]
        UI["App Compose"]
        DB["DataStore local"]
    end

    subgraph NUVEM["Nuvem — fora do caminho crítico"]
        FUT["Bases externas e modelos maiores: futuro"]
    end

    CAM -->|"frame sob demanda"| DAT
    MIC -->|"voz"| BT
    DAT --> PERC
    BT --> PERC
    PERC --> DEC
    DEC --> UI
    DEC --> DB
    DEC -->|"resposta falada"| BT
    BT --> SPK
    NUVEM -.->|"não usada na decisão"| FONE
```

A nuvem aparece tracejada de propósito: nenhuma etapa da decisão depende dela.

## 2. Cascata automática por custo

```mermaid
flowchart TD
    START["Gatilho: voz ou toque"] --> EAR["Sinal sonoro imediato"]
    EAR --> CAP["Captura de um frame"]
    CAP --> BAR["Leitor de código de barras"]
    BAR -->|"EAN encontrado"| CAT{"Produto no catálogo?"}
    BAR -->|"sem código"| OCR["OCR no aparelho"]
    CAT -->|"sim"| EVB["Evidência estruturada"]
    CAT -->|"não"| OCR
    OCR --> ROUTE{"Roteador de contexto"}
    ROUTE -->|"rótulo"| EVL["Afirmações declaradas"]
    ROUTE -->|"indefinido"| INS["Informação insuficiente"]
    EVB --> RULES["Motor determinístico"]
    EVL --> RULES
    INS --> RULES
    RULES --> OUT["Resposta curta por áudio"]
    OUT --> HIST["Registro local"]
```

O OCR só é pago quando o caminho mais barato não resolve.

## 3. Hierarquia de evidência e estados

```mermaid
flowchart TD
    subgraph EV["Evidências, por precedência"]
        E2["2 · rótulo declarado"]
        E3["3 · base estruturada por EAN"]
        E4["4 · confirmação do usuário"]
        E5["5 · texto bruto de OCR"]
        E6["6 · inferência visual"]
    end

    E2 --> ENG["Motor determinístico"]
    E3 --> ENG
    E4 --> ENG
    E5 --> ENG
    E6 --> ENG

    ENG --> S1["COMPATÍVEL"]
    ENG --> S2["INCOMPATÍVEL"]
    ENG --> S3["PRECISA DE CONFIRMAÇÃO"]
    ENG --> S4["INFORMAÇÃO INSUFICIENTE"]

    S3 -->|"resposta do usuário"| E4
```

Inferência visual entra por último e nunca prova ausência de ingrediente.

## 4. Ciclo de vida da sessão dos óculos

```mermaid
sequenceDiagram
    participant U as Usuário
    participant App as Eat Control
    participant Meta as Meta AI
    participant Glass as Óculos

    U->>App: toca em conectar óculos
    App->>Meta: abre fluxo de autorização
    Meta-->>App: registrado
    App->>U: "Autorizado · conecta ao analisar"
    U->>App: pede uma análise
    App->>Glass: abre sessão e stream técnico
    Glass-->>App: um frame
    App->>App: processa no aparelho
    App->>Glass: fala a resposta
    App->>Glass: encerra stream, câmera e sessão
```

O encerramento roda em bloco `finally`: falha na análise não deixa a sessão aberta.

## 5. Fluxos assistidos: cardápio e prato

```mermaid
flowchart TD
    IMG["Captura explícita"] --> KIND{"Modo"}

    KIND -->|"cardápio"| MOCR["OCR"]
    MOCR --> MP["Parser de cardápio: seção, nome, descrição, preço"]
    MP --> MREV["Opções revisáveis"]

    KIND -->|"prato"| VIS["Rotulagem de imagem offline"]
    VIS --> GATE{"Score acima do limiar?"}
    GATE -->|"não"| UNK["Candidato descartado"]
    GATE -->|"sim"| MAP{"Classe dentro do recorte?"}
    MAP -->|"não"| UNK
    MAP -->|"sim"| CAND["Componentes sugeridos"]

    MREV --> CONF["Confirmação explícita do usuário"]
    CAND --> CONF
    UNK --> CONF

    CONF -->|"confirmado"| REC["Registro com proveniência"]
    CONF -->|"não confirmado"| NADA["Nada entra no histórico"]

    REC --> MACRO{"Item, quantidade e fonte de composição?"}
    MACRO -->|"os três presentes"| CALC["Cálculo reproduzível"]
    MACRO -->|"falta algum"| DESC["Macros permanecem desconhecidos"]
```

Este é o guardrail central do produto: sem os três elementos, não existe número.

## 6. Camadas de código

```mermaid
flowchart TD
    UI["ui · Compose"] --> VM["EatControlViewModel"]
    VM --> ORCH["orchestration · InteractionOrchestrator"]
    ORCH --> PERC["inference · OCR, barcode, rotulagem, STT, TTS"]
    ORCH --> GLASS["glasses · óculos, câmera, simulação"]
    ORCH --> DOM["domain · rótulo, cardápio, prato, nutrição, decisão"]
    VM --> DATA["data · perfil, histórico, serialização"]
    DOM --> MODEL["core/model · estados e evidências"]
    DATA --> MODEL
    ORCH --> MET["metrics · latência por etapa"]
```

`domain`, `core/model` e `data` são Kotlin sem dependência de Android, e é por isso que a cobertura
alta se concentra ali.
