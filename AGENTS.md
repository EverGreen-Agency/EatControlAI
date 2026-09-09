# Project Agent Configuration & Skills

Este repositório possui suporte unificado a agentes de IA (Antigravity, Claude Code, Codex).
As skills disponíveis para os agentes estão localizadas no diretório interoperável `.agents/skills/` (espelhado em `.claude/skills/`).

## 🧠 Memória Coletiva Interoperável (Obrigatório)

Antes de iniciar qualquer tarefa ou raciocínio neste projeto, o agente **DEVE consultar os arquivos em `.agents/memory/`**:
1. [CONTEXTO_CANONICO.md](./.agents/memory/CONTEXTO_CANONICO.md): O que é e o que não é o EatControl, beachhead GLP-1, equipe e pitch.
2. [ESTADO_E_ROADMAP.md](./.agents/memory/ESTADO_E_ROADMAP.md): O que está pronto, em andamento e descartado.
3. [DECISOES_ARQUITETURAIS.md](./.agents/memory/DECISOES_ARQUITETURAIS.md): Padrões inegociáveis (Reliability-First, zero LLM na decisão crítica, Kotlin puro no domínio).
4. [REGISTRO_SESSAO.md](./.agents/memory/REGISTRO_SESSAO.md): **Protocolo de Saída**: Ao concluir um bloco de trabalho, adicione uma entrada concisa no topo deste arquivo com suas entregas e pendências.

## Catálogo de Skills Disponíveis

- **Cinemática, Motion & Animações:**
  - `cinematic-site-components`: 30 módulos cinemáticos web standalone (GSAP, ScrollTrigger, vanilla JS/CSS).
  - `gsap-core`, `gsap-scrolltrigger`, `gsap-react`, `gsap-timeline`, `gsap-plugins`, `gsap-performance`, `gsap-frameworks`, `gsap-utils`: Conjunto oficial GreenSock.
  - `animate`, `animate-expo`, `animation-vocabulary`, `apple-design`, `ask-sonner`, `emil-design-eng`: Motion design e micro-interações por Emil Kowalski.
  - `img2threejs`: Reconstrução procedural Three.js.

- **Estilos de Design & UI Systems (Awesome Design Skills):**
  - `bento`, `glassmorphism`, `neobrutalism`, `neumorphism`, `claymorphism`, `editorial`, `gradient`, `futuristic`, `minimal`, `modern`, `shadcn`, `skeumorphism`, `sleek`, `retro`, `vibrant` (67 estilos de design ao todo, cada um com `DESIGN.md` e `SKILL.md`).
  - `taste-skill`, `minimalist-skill`, `brutalist-skill`, `brandkit`, `image-to-code-skill`, `redesign-skill`: Prevenção de layouts genéricos e direção de arte.

- **Frontend, Mobile & Shaders:**
  - `frontend-dev`, `fullstack-dev`, `shader-dev`: Desenvolvimento web avançado e shaders GLSL.
  - `android-native-dev`, `flutter-dev`, `react-native-dev`, `ios-application-dev`: Mobile nativo e cross-platform.

- **Engenharia de Software & Boas Práticas:**
  - `karpathy-guidelines`: Princípios de codificação pragmática por Andrej Karpathy.
  - `code-review`, `tdd`, `codebase-design`, `diagnosing-bugs`, `domain-modeling`, `implement`, `wayfinder`: Padrões de engenharia por Matt Pocock.
  - `gauntlet-loop`: Auto-validação em loop iterativo.
  - `last30days`: Pesquisa e síntese de dados atualizados na web.
  - `geo-audit`, `geo-schema`, `geo-content`, `geo-crawlers`: Otimização para motores de busca generativos (GEO).

- **Marketing, Growth & Conversão (MarketingSkills por Corey Haines):**
  - **Estratégia & Contexto**: `product-marketing` (lê `.agents/product-marketing.md`), `marketing-plan`, `marketing-ideas`, `marketing-psychology`, `marketing-council`, `marketing-loops`, `offers`, `pricing`, `customer-research`, `competitors`, `competitor-profiling`.
  - **CRO & Otimização de Conversão**: `cro`, `signup`, `onboarding`, `paywalls`, `popups`, `ab-testing`.
  - **Conteúdo, Copywriting & Criativos**: `copywriting`, `copy-editing`, `content-strategy`, `ad-creative`, `image`, `video`.
  - **SEO, ASO & Busca por IA (AEO)**: `seo-audit`, `ai-seo`, `schema`, `programmatic-seo`, `site-architecture`, `aso`.
  - **Aquisição, Tráfego Pago & Outreach**: `ads`, `cold-email`, `emails`, `sms`, `social`, `prospecting`, `lead-magnets`, `directory-submissions`.
  - **Growth Loops, Retenção & Parcerias**: `referrals`, `churn-prevention`, `community-marketing`, `co-marketing`, `influencer-marketing`, `events`, `free-tools`, `public-relations`, `launch`.
  - **Receita, Atribuição & Operações**: `analytics`, `attribution`, `revops`, `sales-enablement`.

## Como Utilizar
Os agentes devem consultar o arquivo `SKILL.md` dentro de `.agents/skills/<skill-name>/` (ou `.claude/skills/<skill-name>/`, ou `.codex/skills/<skill-name>/`) para carregar as instruções específicas de cada procedimento.
