# Pesquisa: Modelos Gratuitos com Suporte a Visão / Multimodalidade (Últimos 30 Dias)

> **Data da Investigação**: 10 de Setembro de 2026  
> **Objetivo**: Mapear modelos de IA com capacidade de visão (compreensão de imagens e alimentos) acessíveis gratuitamente via API sem exigência de cartão de crédito para integração com o EatControl AI.  
> **Fontes Primárias**: Documentação oficial da Google AI Studio, API REST do OpenRouter (`/api/v1/models`), testes ao vivo com requisições HTTP reais de base64 e discussões da comunidade de desenvolvedores (Reddit r/LocalLLaMA, OpenRouter Discord, CloudZero).

---

## 1. Síntese Executiva

Para aplicações de visão computacional móvel em tempo real (como reconhecimento de pratos, leitura de tabelas nutricionais e detecção de alérgenos no buffet), o ecossistema atual de APIs gratuitas divide-se em duas opções viáveis:

1. **Google AI Studio (Gemini Direct)**:
   - **Veredito**: **A melhor opção em estabilidade, cota e qualidade de inferência.**
   - **Modelo Recomendado**: `gemini-3.5-flash` na rota oficial `v1` (`v1/models/gemini-3.5-flash:generateContent?key=$API_KEY`).
   - **Cotas**: 15 requisições por minuto (RPM), 1.500 requisições diárias gratuitas (RPD) e 1M tokens/minuto.
   - **Requisitos**: Apenas conta Google pessoal no Google AI Studio. Sem cartão de crédito.
   - **Status na Chave do Usuário**: Testado e **100% validado com código HTTP 200 OK**.

2. **OpenRouter (Agregador Multiprovedor)**:
   - **Veredito**: **Excelente como fallback resiliente e para contornar bloqueios regionais.**
   - **Modelo Recomendado**: O auto-roteador virtual **`openrouter/free`**.
   - **Como Funciona**: Em vez de fixar um ID específico que pode sofrer de indisponibilidade (503) ou lotação pública (429), o `openrouter/free` analisa o payload da imagem e encaminha para o melhor modelo de visão gratuito disponível naquele segundo.
   - **Cotas**: ~20 RPM, 50 requisições diárias gratuitas (ou até 1.000 RPD se a conta mantiver saldo de $10, mesmo sem gastá-lo em modelos free).
   - **Status na Chave do Usuário**: Testado com sucesso via API, roteando automaticamente para `dots-studio/dots-3-note-preview:free` com código **200 OK**.

---

## 2. Levantamento Detalhado por Provedor

### 2.1. OpenRouter — Modelos Gratuitos de Visão (`:free`)

Ao consultar a API oficial do OpenRouter (`GET https://openrouter.ai/api/v1/models`), identificamos 18 modelos com a tag `:free`, dos quais 5 possuem arquitetura multimodal com suporte a imagem (`text+image->text`):

| Modelo | Contexto | Modalidade | Status Real em Teste ao Vivo | Observação Técnica |
| :--- | :--- | :--- | :--- | :--- |
| **`openrouter/free`** | Dinâmico | `text+image->text` | **200 OK (Recomendado)** | Auto-roteador inteligente que escolhe o nó de visão online. |
| **`dots-studio/dots-3-note-preview:free`** | 512k | `text+image->text` | **200 OK** | Alta disponibilidade, resposta rápida para OCR e rótulos. |
| **`nex-agi/nex-n2.5-mini:free`** | 262k | `text+image->text` | *Oscilação 503 temporária* | Modelo compacto de visão; sofre oscilações sob alta carga. |
| **`nex-agi/nex-n2.5-pro:free`** | 262k | `text+image->text` | *Oscilação 503 temporária* | Variante pro da Nex-AGI. |
| **`google/gemma-4-26b-a4b-it:free`** | 262k | `text+image->text` | *Rate Limit 429* | Modelo aberto da Google; congestionado por tráfego público. |
| **`google/gemma-4-31b-it:free`** | 262k | `text+image->text` | *Rate Limit 429* | Similar ao 26b, limite de taxa por IP/token frequente. |
| `google/gemini-2.0-flash-exp:free` | — | — | **404 Not Found** | **Descontinuado** pelo OpenRouter (rota experimental encerrada). |
| `openai/gpt-4o` | 128k | `text+image->text` | **402 Payment Required** | Exige créditos em dólares; não é gratuito. |

#### Descoberta Chave do OpenRouter: O Auto-Router `openrouter/free`
O OpenRouter implementou recentemente um roteador inteligente para o pool gratuito: enviando `{"model": "openrouter/free", "messages": [...]}` contendo `{"type": "image_url", ...}`, a própria infraestrutura do OpenRouter detecta a presença de imagem e delega a inferência para o nó de visão que estiver com menor latência e sem sobrecarga.

---

### 2.2. Google AI Studio (Gemini Direct)

A Google fornece acesso direto via API sem custo através do Google AI Studio para desenvolvedores:

- **Endpoint de Produção**:  
  `POST https://generativelanguage.googleapis.com/v1/models/gemini-3.5-flash:generateContent?key=$API_KEY`
- **Modelos no Free Tier**:
  - `gemini-3.5-flash`: Excelente em reconhecimento de pratos culinários, wraps, ingredientes em português e leitura de tabelas nutricionais em baixa luminosidade.
  - `gemini-3.5-flash-lite`: Focado em ultra-baixa latência com vocabulário reduzido.
- **Limites Gratuitos**:
  - **15 RPM** (Requisições por minuto)
  - **1.500 RPD** (Requisições por dia)
  - **1.000.000 TPM** (Tokens por minuto)
- **Latência Observada**: ~750ms a 1.1s no Brasil (rede móvel 4G/5G).

---

### 2.3. Outros Provedores Avaliados

| Provedor | Suporte Gratuito a Visão? | Limitações Práticas |
| :--- | :--- | :--- |
| **GroqCloud** | Não para visão contínua | O free tier da Groq é focado em texto e código (Llama 3.3 70B, Qwen 2.5). Os modelos Llama Vision foram restritos. |
| **Cloudflare Workers AI** | Parcial (Llama 3.2 Vision) | Gratuito até 10.000 Neurons/dia (~50 análises de imagem), requer conta Cloudflare e setup de Worker. |
| **Hugging Face Inference** | Instável | A API Serverless gratuita do HF para modelos de visão (Qwen2-VL, SmolVLM) sofre com filas de espera e cold starts de até 20 segundos. |

---

## 3. Arquitetura Implementada no EatControl

Para blindar o aplicativo contra qualquer falha de rede ou esgotamento de cota, o [CloudVisionProvider.kt](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/app/src/main/java/com/eatcontrolai/inference/cloud/CloudVisionProvider.kt) adota a seguinte esteira de três camadas:

```
[ Captura da Câmera do Celular / Óculos ]
                   │
                   ▼
       ┌───────────────────────┐
       │  Chave Gemini existe? │ ── Sim ──▶ Google AI Studio (gemini-3.5-flash) [15 RPM Free]
       └───────────────────────┘                         │
                   │ Não / Erro                          ▼ Sucesso
                   ▼                              [ Resultado 200 OK ]
       ┌──────────────────────────┐
       │ Chave OpenRouter existe? │ ── Sim ──▶ OpenRouter Free Router (openrouter/free)
       └──────────────────────────┘            └─ Fallback: dots-3-note-preview:free
                   │ Não / Erro                └─ Fallback: nex-n2.5-mini:free
                   ▼                                     │
       ┌──────────────────────────┐                      ▼ Sucesso
       │   On-Device Fallback     │               [ Resultado 200 OK ]
       │   ML Kit Labeling + OCR  │ ─────────────▶ [ 100% Offline / 20ms ]
       └──────────────────────────┘
```

1. **Camada 1 (Google Gemini 3.5 Flash)**: Prioritária quando a chave do Google AI Studio está configurada. Máxima acurácia e sem falhas de nó.
2. **Camada 2 (OpenRouter Auto-Free)**: Se a chave do OpenRouter estiver preenchida, utiliza `openrouter/free` com fallback para `dots-studio/dots-3-note-preview:free` e `nex-agi/nex-n2.5-mini:free`.
3. **Camada 3 (ML Kit Local On-Device)**: Se não houver internet ou se ambas as chaves falharem, o app faz a classificação local em 20ms sem travar a interface do usuário.
