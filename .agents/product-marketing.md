# Product Marketing Context — EatControl

**Document version:** v1.0  
**Last updated:** 2026-09-09  

---

## Product Overview
- **Product Name:** EatControl
- **One-liner:** Copiloto de inteligência alimentar contextual que traduz o que a pessoa come, suas metas e seu quadro de saúde em orientações práticas no momento exato da escolha.
- **Tagline:** Food intelligence for real life.
- **Brand Idea:** *Intelligence at the moment of choice*.
- **Investor Positioning:** The intelligence layer for everyday food decisions — starting with GLP-1 users.
- **What it does:** O EatControl analisa alimentos em tempo real através de código de barras (EAN-13), leitura de tabela nutricional por OCR, cardápio de restaurante, prato assistido e escuta de garçom/voz. Em menos de 1 segundo e com privacidade total (*on-device*), emite um veredito determinístico (`COMPATÍVEL`, `ATENÇÃO`, `INCOMPATÍVEL` ou `INSUFICIENTE`), orientando escolhas alimentares sem estimativas volumétricas enganosas.
- **Product category:** Food Intelligence / HealthTech / GLP-1 Companion App.
- **Product type:** Mobile App (Android nativo Kotlin/Compose) com transição futura para Smart Glasses (Meta Wearables).
- **Business model:** Freemium (análise instantânea e vereditos básicos gratuitos; regras avançadas personalizadas, histórico estendido e acompanhamento clínico sob assinatura recorrente).

---

## Target Audience
- **Target Audience / Beachhead:** Pacientes em terapia de emagrecimento ou tratamento metabólico com agonistas do receptor GLP-1 (Ozempic, Wegovy, Mounjaro, Rybelsus) e pessoas em reeducação alimentar consciente.
- **Geografia Inicial:** Brasil (2º maior mercado consumidor de GLP-1 do mundo) com arquitetura desenhada para expansão global (EUA, Europa).
- **Primary use case:** Tomada de decisão alimentar segura no momento exato da compra no supermercado ou do pedido em restaurantes, prevenindo crises gastrointestinais (refluxo, náusea, vômito) e garantindo aporte proteico ideal para evitar sarcopenia (perda de massa magra).
- **Jobs to be done (JTBD):**
  1. "Me diga em 2 segundos se posso comer ou pedir este item sem passar mal hoje à noite."
  2. "Garanta que eu atinja minha meta diária de proteína sem me obrigar a pesar cada grama de comida em balança."
  3. "Interprete o rótulo do supermercado ou a fala do garçom para mim sem que eu precise decifrar tabelas confusas."
- **Specific Scenarios:**
  - *Supermercado:* Mirar a câmera no código de barras ou na tabela nutricional do iogurte e receber confirmação tátil/sonora imediata.
  - *Restaurante:* Ouvir o garçom dizer os ingredientes do molho ("leva creme de leite e gorgonzola") e o app alertar imediatamente sobre excesso lipídico incompatível com a dose de GLP-1.
  - *Refeição:* Analisar o prato assistido e validar se os blocos nutricionais (proteína magra + vegetais) estão equilibrados.

---

## Personas & Stakeholders

| Persona | O que valoriza | Principal Desafio | Proposta de Valor do EatControl |
| :--- | :--- | :--- | :--- |
| **Usuário B2C (Paciente GLP-1)** | Certeza rápida, evitar náuseas/refluxo, praticidade extrema sem contar calorias. | Ansiedade a cada refeição; mais de 90 decisões alimentares mensais sem auxílio médico. | Feedback auditivo/visual em <1s: "Compatível" ou "Atenção: alto teor de gordura para seu estágio". |
| **Médico Prescritor / Endocrinologista** | Adesão do paciente ao tratamento, manutenção da massa magra, redução de efeitos colaterais. | Paciente abandona o remédio por efeitos colaterais gástricos ou perde músculo por falta de proteína. | Ferramenta confiável de suporte contínuo baseada em evidência determinística sem alucinações de IA. |
| **Nutricionista Clínico** | Paciente fazendo escolhas consistentes e respeitando metas de nutrientes sem fadiga de registro. | Pacientes mentem ou esquecem de registrar no diário alimentar em apps de calorias. | Orientação no momento do consumo com regras personalizadas definidas em conjunto. |

---

## Problems & Pain Points
- **Core problem:** Medicamentos GLP-1 controlam a saciedade, mas **não decidem o que comer**. O paciente tem 20 minutos de consulta por mês, mas enfrenta 90 decisões alimentares desassistidas.
- **Why alternatives fall short:**
  - *Contadores de caloria convencionais por foto (MyFitnessPal, etc.):* Tentam "adivinhar" o peso da comida por foto, errando centenas de calorias e gerando enorme fricção manual de digitação.
  - *Chatbots genéricos de IA:* Alucinam dados nutricionais e não possuem rigor clínico ou determinismo de segurança.
  - *Tabelas de rótulos:* Complexas, com letras minúsculas e porções manipuladas pela indústria alimentícia.
- **Emotional tension:** Medo constante de passar mal (náuseas súbitas em reuniões ou eventos sociais), frustração de reganho de peso (efeito sanfona) e fadiga de decisão alimentar.

---

## Competitive Landscape
- **Concorrentes Diretos:** Apps de diário e contagem calórica (MyFitnessPal, FatSecret, Yazio). *Deficiência:* Fricção manual insustentável e foco obsessivo em calorias em vez de compatibilidade biológica real.
- **Concorrentes Secundários:** Apps de scanner de alimentos (Yuka, Desrotulando). *Deficiência:* Avaliação estática e generalista da embalagem, sem personalização para o quadro clínico e farmacológico individual do usuário (ex: dosagem de semaglutida).
- **Concorrentes Indiretos:** Chatbots genéricos (ChatGPT, Claude direto). *Deficiência:* Alucinam macros, dependem de digitação demorada e não oferecem cascata determinística de evidência auditável.

---

## Differentiation
- **Princípio *Reliability-First*:** Hierarquia de evidência rigorosa (`Rótulo Declarado > Catálogo Auditável EAN > Confirmação Humana > OCR Bruto > Inferência Visual`).
- **Zero LLM no caminho crítico de decisão:** Decisão tomada pelo `FoodDecisionEngine` em Kotlin puro, auditável e livre de alucinações médicas.
- **Latência Ultra-Baixa & On-Device:** Decisões em < 1 segundo com processamento local, preservando a privacidade sob a LGPD.
- **Multi-Modalidade Total:** 4 trilhas integradas (Barcode, Rótulo OCR, Cardápio, Prato) + Escuta contínua de voz (STT/TTS em PT-BR).
- **Território de Marca *Precision Intelligence*:** Visual cinematográfico escuro e sóbrio (`Ink #0B0F14`, `Deep Ocean #062439`, `Signal Cyan #2DD4E7`), fugindo do clichê médico asséptico ou de academias espalhafatosas.

---

## Objections & Counter-Arguments
1. *"O app vai adivinhar calorias de feijoada por foto?"*
   - **Resposta:** Não. O EatControl não é um adivinhador visual de calorias. Ele identifica os componentes e, quando necessário, faz perguntas objetivas ou lê a tabela nutricional declarada.
2. *"Posso confiar que não vou passar mal?"*
   - **Resposta:** O motor aplica regras conservadoras e determinísticas alinhadas a diretrizes de tolerância gastrointestinal para GLP-1. Se faltarem dados, o app declara `INSUFICIENTE` em vez de arriscar sua saúde.
3. *"Dá trabalho demais registrar tudo?"*
   - **Resposta:** Zero digitação no dia a dia. Basta apontar a câmera ou ouvir o garçom para receber o veredito instantâneo no fone ou visor.

---

## Brand Voice & Style
- **Tom:** Preciso, confiante, acolhedor, científico e sem afetação (*Precision Intelligence*).
- **Personalidade:** Parceiro inteligente, guardião discreto, rigoroso na ciência e fluido na experiência.
- **Termos Recomendados:** Decisão alimentar, copiloto de inteligência alimentar, compatibilidade nutricional, evidência declarada, momento da escolha.
- **Termos a Evitar:** "Contador de calorias mágico por foto", "dieta milagrosa", "cura da obesidade", "adivinhação de prato".
