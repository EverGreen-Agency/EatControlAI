# Decisões Arquiteturais — EatControl

> **Regras e padrões de engenharia de software e arquitetura do EatControl.**
> Qualquer alteração estrutural no código deve respeitar estas diretrizes.

---

## 1. Princípios Fundamentais

### 1.1 Confiabilidade Antes de Inferência (*Reliability-First*)
Nenhum modelo probabilístico (visão ou linguagem) pode emitir afirmações de compatibilidade clínica ou nutricional sem respaldo em dados declarados. Se houver dúvida ou ausência de dados, o sistema **deve retornar `INSUFICIENTE`** e solicitar esclarecimentos.

### 1.2 Zero LLM no Caminho Crítico de Decisão
A decisão alimentar de aprovar, reprovar ou alertar sobre uma refeição é tomada pelo **motor determinístico de regras** (`FoodDecisionEngine` + `glp1-rules-v1`). LLMs não decidem se um alimento é seguro para o paciente.

### 1.3 Domínio Puro em Kotlin
A camada de domínio (`domain/` e `core/model/`) é escrita em Kotlin puro, sem dependências de classes do framework Android (`android.*`). Isso permite que os 172 testes rodem na JVM em poucos segundos com 100% de determinismo.

### 1.4 On-Device e Privacidade por Design
- **STT/TTS**: Operação on-device (`AndroidSttProvider` com `createOnDeviceSpeechRecognizer` e `AndroidTtsProvider`). Nenhum áudio de voz do usuário é transmitido para servidores de terceiros.
- **Histórico**: Armazenamento local no dispositivo via DataStore serializado.

---

## 2. Hierarquia Canônica de Evidência

Ao avaliar um alimento, evidências de maior peso prevalecem rigorosamente sobre as de menor peso:

| Rank | Tipo | Descrição |
| :---: | :--- | :--- |
| **1** | `DECLARED_LABEL` | Tabela nutricional e lista de ingredientes oficial do rótulo confirmada por OCR. |
| **2** | `BARCODE_CATALOG` | Composição cadastrada em base pública auditável (Open Food Facts / TBCA) via código de barras. |
| **3** | `USER_CONFIRMATION` | Resposta explícita do usuário a um pedido de esclarecimento do app. |
| **4** | `RAW_OCR` | Leitura de texto bruto sem estrutura formal de rótulo (ex: cardápios). |
| **5** | `VISUAL_INFERENCE` | Classificação visual de prato. Nunca gera macros ou certezas sem confirmação. |

---

## 3. Roteamento e Abstração de Hardware

- **Interfaces Plug-and-Play**: Acesso a sensores e câmeras é mediado por `GlassesGateway`.
- **Roteador Dinâmico**: `CaptureSourceRouter` permite alternar em runtime entre a câmera do smartphone (`PhoneCameraGateway`), o simulador (`MockGlassesGateway`) e o hardware real Meta (`DatGlassesGateway`), sem recriar os pipelines de processamento.
- **Model Registry**: Provedores de IA (`MlKitOcrProvider`, `MlKitBarcodeProvider`, etc.) são injetados via `ModelRegistry` em `AppContainer`. Nenhuma tela ou ViewModel instancia bibliotecas de visão diretamente.

---

## 4. Padrões de Versão e Git

- **Conventional Commits**: `<type>(<scope>): <subject>` (ex: `feat(glp1): ...`, `fix(ui): ...`).
- **Segurança Absoluta**: Nunca versionar `local.properties`, arquivos de chaves `.jks`, credenciais ou arquivos HTML do Meta Developer Center.
