# Claude Code Configuration & Skills

Este projeto e o ambiente do Claude possuem suporte nativo a skills modulares instaladas em `.claude/skills/` (e `.agents/skills/`), além do repositório global em `~/.claude/skills/`.

## 🧠 Memória Coletiva do Projeto (Obrigatório)

O EatControl utiliza um sistema compartilhado de memória entre agentes em `.agents/memory/`.
Ao iniciar qualquer sessão, **leia imediatamente**:
1. [.agents/memory/CONTEXTO_CANONICO.md](./.agents/memory/CONTEXTO_CANONICO.md) (Produto, GLP-1, posicionamento e BrandKit)
2. [.agents/memory/ESTADO_E_ROADMAP.md](./.agents/memory/ESTADO_E_ROADMAP.md) (Pronto, em construção e pendências reais)
3. [.agents/memory/DECISOES_ARQUITETURAIS.md](./.agents/memory/DECISOES_ARQUITETURAIS.md) (Reliability-First, zero LLM na decisão de saúde)
4. [.agents/memory/REGISTRO_SESSAO.md](./.agents/memory/REGISTRO_SESSAO.md) (Ao concluir alterações, registre um resumo com data, entregas e pendências)

## Catálogo de Skills de UI, Design & Frontend

### 1. Componentes Cinemáticos & Motion
- **`cinematic-site-components`**: 30 módulos cinemáticos completos em single-file HTML/CSS/JS (GSAP + ScrollTrigger). Consulte [.claude/skills/cinematic-site-components/SKILL.md](./.claude/skills/cinematic-site-components/SKILL.md) e o catálogo de códigos em `modules/`.
- **GSAP Oficial**: `gsap-core`, `gsap-scrolltrigger`, `gsap-react`, `gsap-timeline`, `gsap-plugins`, `gsap-performance`, `gsap-frameworks`, `gsap-utils`.
- **Micro-interações (Emil Kowalski)**: `animate`, `animate-expo`, `animation-vocabulary`, `apple-design`, `ask-sonner`, `emil-design-eng`, `find-animation-opportunities`, `improve-animations`.
- **3D Procedural**: `img2threejs` (modelagem Three.js procedural via referência visual).

### 2. Estéticas de UI & Design Systems (Awesome Design Skills)
Consulte os arquivos `SKILL.md` e `DESIGN.md` em cada pasta de estilo dentro de `.claude/skills/`:
- **Layouts & Grids**: `bento`, `minimal`, `modern`, `clean`, `sleek`, `spacious`, `square`.
- **Estilos Visuais**: `glassmorphism`, `neobrutalism`, `neumorphism`, `claymorphism`, `gradient`, `neon`, `futuristic`, `cosmic`, `dithered`, `matrix`.
- **Editorial & Tipografia**: `editorial`, `contemporary`, `corporate`, `mono`, `bold`, `refined`, `material`, `shadcn`.
- **Direção de Arte & Anti-Slop (Taste-Skill)**: `taste-skill`, `minimalist-skill`, `brutalist-skill`, `brandkit`, `image-to-code-skill`, `redesign-skill`, `gpt-tasteskill`, `soft-skill`.

### 3. Engenharia de Frontend & Mobile
- **Frontend & Fullstack**: `frontend-dev`, `fullstack-dev`, `shader-dev`.
- **Mobile Nativo & Cross-Platform**: `android-native-dev`, `react-native-dev`, `flutter-dev`, `ios-application-dev`.

### 4. Engenharia de Software & Práticas
- **Diretrizes Karpathy**: `karpathy-guidelines` (regras contra complexidade acidental e preservação de contratos).
- **Matt Pocock**: `code-review`, `tdd`, `codebase-design`, `diagnosing-bugs`, `domain-modeling`, `implement`, `wayfinder`.
- **Qualidade & Pesquisa**: `gauntlet-loop`, `last30days`, suite `geo-*` (SEO generativo).

### 5. Marketing, Growth, Conversão & Go-To-Market (MarketingSkills)
- **Estratégia & Mercado**: `product-marketing` (lê `.agents/product-marketing.md`), `marketing-plan`, `marketing-ideas`, `marketing-psychology`, `marketing-council`, `marketing-loops`, `offers`, `pricing`, `customer-research`, `competitors`, `competitor-profiling`.
- **CRO & Conversão**: `cro`, `signup`, `onboarding`, `paywalls`, `popups`, `ab-testing`.
- **Conteúdo & Criativos**: `copywriting`, `copy-editing`, `content-strategy`, `ad-creative`, `image`, `video`.
- **SEO & Otimização para IA**: `seo-audit`, `ai-seo`, `schema`, `programmatic-seo`, `site-architecture`, `aso`.
- **Aquisição & Outbound**: `ads`, `cold-email`, `emails`, `sms`, `social`, `prospecting`, `lead-magnets`, `directory-submissions`.
- **Growth Loops & Parcerias**: `referrals`, `churn-prevention`, `community-marketing`, `co-marketing`, `influencer-marketing`, `events`, `free-tools`, `public-relations`, `launch`.
- **Receita & Mensuração**: `analytics`, `attribution`, `revops`, `sales-enablement`.

## Como Executar
O Claude Code pode ler e carregar qualquer uma dessas skills diretamente pelo caminho `.claude/skills/<skill-name>/SKILL.md` ou usando a skill global correspondente em `~/.claude/skills/<skill-name>/`.
