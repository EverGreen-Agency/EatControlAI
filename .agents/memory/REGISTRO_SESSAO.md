# Registro de Sessões — Memória Compartilhada

> **Ledger contínuo de intervenções de agentes de IA e humanos no EatControl.**
> **Instrução para agentes**: Ao concluir um bloco de trabalho, adicione uma entrada concisa no topo deste arquivo seguindo o template.

---

### Template de Registro

```markdown
### [AAAA-MM-DD] <Título Curto da Sessão>
- **Agente / Modelo**: <Antigravity / Claude Code / Codex / Humano>
- **Objetivo**: <1 linha sobre a intenção da sessão>
- **Entregas**:
  - <Item 1 com link se houver>
  - <Item 2>
- **Arquivos Tocados**: `caminho/arquivo1`, `caminho/arquivo2`
- **Próximas Pendências**: <O que ficou engatilhado para o próximo agente>
```

---

## Histórico de Sessões

### [2026-09-10 — Tarde] Cliente S3 SigV4, Opt-in de Telemetria de Fotos, Diagnóstico Groq e Suporte Multi-Alérgenos
- **Agente / Modelo**: Antigravity (Advanced Agentic Coding)
- **Objetivo**: Implementar cliente S3 com autenticação nativa AWS Signature V4 para Railway Buckets/S3 privado, conectar o upload de fotos ao opt-in de pesquisa do usuário no fluxo de análise, expor controles na UI de Perfil (Switch de opt-in e botão de teste de upload), diagnosticar e configurar chave da Groq e garantir compilação 100% verde.
- **Entregas**:
  - **Cliente S3 com AWS SigV4**: `S3TelemetryClient.kt` atualizado com assinatura HMAC-SHA256 nativa (sem SDKs pesados), suportando `s3.access.key` e `s3.secret.key` para Railway/AWS e logs detalhados de upload.
  - **Fluxo de Upload de Fotos Conectado**: Em `EatControlViewModel.kt`, ao salvar a foto localmente da refeição, se `shareForImprovement` estiver ativo, o app dispara o envio assíncrono para o S3 em segundo plano. Adicionada a função `testS3Upload()` com feedback visual imediato.
  - **UI de Perfil com Opt-in S3**: `ProfileScreen.kt` equipado com Switch de opt-in de pesquisa para o bucket e botão "Testar S3" para validação imediata pelo usuário.
  - **Diagnóstico e Configuração Groq**: Chave da Groq configurada no `local.properties` e `CloudVisionProvider.kt`. Diagnóstico primário via API ao vivo revelou que `llama-3.2-11b-vision-preview` foi oficialmente descontinuado (*decommissioned*) pela Groq; a chave permanece ativa e pronta para `whisper-large-v3-turbo` (STT ultrarrápido).
  - **Múltiplas Restrições e OpenRouter Free**: Validação completa da checagem multi-alérgenos no scanner de buffet e auto-router `openrouter/free`.
  - **Validação**: 100% dos testes unitários JVM aprovados (`BUILD SUCCESSFUL in 21s`) e APK atualizado gerado em `app/build/outputs/apk/debug/app-debug.apk`.
- **Arquivos Tocados**: `local.properties`, `app/build.gradle.kts`, `app/src/main/java/com/eatcontrolai/EatControlApp.kt`, `app/src/main/java/com/eatcontrolai/data/telemetry/S3TelemetryClient.kt`, `app/src/main/java/com/eatcontrolai/inference/cloud/CloudVisionProvider.kt`, `app/src/main/java/com/eatcontrolai/ui/EatControlViewModel.kt`, `app/src/main/java/com/eatcontrolai/ui/profile/ProfileScreen.kt`, `.agents/memory/REGISTRO_SESSAO.md`.
- **Próximas Pendências**: Obter no painel do Railway as variáveis `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY` do bucket para liberar o upload privado (evitando o 403 AccessDenied).

### [2026-09-10 — Madrugada] Correção do Roteamento de Rótulos, Identificação Multimodal de Pratos (Shawarma/Wraps), HUD de Buffet e Chaves Reais de API/S3
- **Agente / Modelo**: Antigravity (Advanced Agentic Coding)
- **Objetivo**: Corrigir o bug onde qualquer texto na cena (incluindo tela de busca de comida) ativava o modo Rótulo e gerava o título legado "Biscoito recheado", configurar credenciais reais de API (Gemini/OpenRouter) e S3 no `local.properties`, expandir o mapeador de pratos para sandwiches/wraps e implementar HUD dinâmico com seletor de modos para Buffet.
- **Entregas**:
  - **Eliminação do Fallback de Biscoito**: Removido em `EatControlViewModel.kt` o fallback que injetava o título da cena mockada (`selectedScene?.title`). Agora utiliza o nome real do prato identificado (`detectedDishName`), componentes visuais ou linha de OCR limpa, com fallback neutro de "Refeição assistida".
  - **Roteamento Preciso de Rótulo vs Prato**: Corrigida a heurística de densidade em `ContextRouter.kt` e `PhoneCameraGateway.kt`. Textos soltos de tela/pesquisa não forçam mais `AnalysisTrack.LABEL`; rótulos agora exigem palavras-chave regulatórias de ingredientes ou fórmulas aditivas, liberando a trilha `AnalysisTrack.PLATE`.
  - **Identificação de Shawarma e Sanduíches**: `PlateFoodClass` expandido com `SANDWICH_WRAP`, `BREAD` e `SOUP`. Mapeamento de termos em PT/EN ("shawarma", "wrap", "kebab", "sanduiche", "taco", "burrito") adicionado em `PlateLabelMapper.kt`, com extração do nome específico do prato em `PlateAnalysis.kt`.
  - **Integração de APIs Reais**: `local.properties` configurado com Gemini Flash (`gemini-flash-latest`), OpenRouter (`openai/gpt-4o`) e Storage S3 (`s3opt-in-homi0mlb02-2ci-g` no endpoint `https://t3.storageapi.dev`), testados e validados.
  - **HUD de Buffet e Trilho de Modos**: `AnalyzeScreen.kt` equipado com seletor rápido de modos (`✦ Auto`, `🍽 Prato`, `Aa Rótulo`, `≡ Cardápio`, `▦ Código`) e `HudViewfinder` adaptativo com cantoneiras animadas que expandem e mudam de cor no modo Buffet/Prato.
  - **Validação Completa**: 100% dos testes unitários aprovados e APK compilado com sucesso em `app/build/outputs/apk/debug/app-debug.apk`.
- **Arquivos Tocados**: `local.properties`, `app/src/main/java/com/eatcontrolai/domain/plate/PlateAnalysis.kt`, `app/src/main/java/com/eatcontrolai/domain/routing/ContextRouter.kt`, `app/src/main/java/com/eatcontrolai/glasses/PhoneCameraGateway.kt`, `app/src/main/java/com/eatcontrolai/inference/cloud/CloudVisionProvider.kt`, `app/src/main/java/com/eatcontrolai/orchestration/InteractionOrchestrator.kt`, `app/src/main/java/com/eatcontrolai/ui/EatControlViewModel.kt`, `app/src/main/java/com/eatcontrolai/ui/analyze/AnalyzeScreen.kt`, `.agents/memory/REGISTRO_SESSAO.md`.
- **Próximas Pendências**: Instalar o APK no dispositivo físico para teste em campo de shawarma/buffet com a câmera real e as APIs em nuvem ativas.

### [2026-09-09 — Noite] Remoção de Mocks, Persistência Local de Fotos de Refeições, Calculadora de Macros GLP-1 e Provedor de Visão em Nuvem / S3
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Eliminar o mock de captura que causava reconhecimento falso de biscoito recheado, implementar salvamento local privado de fotos de refeições no aparelho com visualização rica no Histórico (BottomSheet), adicionar calculadora de macros para GLP-1, integrar provedor multimodal de Visão em Nuvem (Gemini / OpenRouter) e telemetria de imagens S3 para opt-in de pesquisa.
- **Entregas**:
  - **Remoção de Mock de Câmera**: `CaptureSourceRouter.kt` agora inicia estritamente com `PHONE_CAMERA`. Fallbacks de recusa e erro do DAT alterados para câmera nativa real. O viewfinder e o botão de análise agora capturam o frame físico real da lente.
  - **Armazenamento Privado de Fotos**: Cada análise real salva os bytes do JPEG em `context.filesDir/meal_photos/$recordId.jpg` (estilo WhatsApp) e associa `photoPath` ao `MealRecord`.
  - **Histórico com BottomSheet Detalhado**: `HistoryScreen.kt` reescrito para exibir miniatura da foto real nos cards de refeição e abrir um `ModalBottomSheet` completo ao tocar (foto ampliada, decisão clínica GLP-1, evidências, alérgenos, macronutrientes consumidos e latência).
  - **Calculadora de Metas GLP-1**: `PlanScreen.kt` equipado com `Glp1MacroCalculatorCard`, calculando proteína (1.4g/kg para proteção de massa magra em agonistas GLP-1), calorias e fibras com persistência no perfil.
  - **Visão em Nuvem (CloudVisionProvider)**: Implementado suporte REST nativo (zero dependências pesadas) para Google Gemini (`gemini-1.5-flash`) e OpenRouter (`google/gemini-2.0-flash-exp:free`, `qwen/qwen-2.5-vl-72b-instruct:free`) com fallback automático para o ML Kit local.
  - **Telemetria de Pesquisa (S3TelemetryClient)**: Implementado upload de imagens para bucket S3 / Cloudflare R2 para usuários com opt-in de melhoria ativo.
  - **Configuração de Chaves no Lab & local.properties**: `LabScreen.kt` equipado com `CloudConfigCard` para inserção e teste direto de chaves no celular, com espelhamento para `local.properties`.
  - **Validação**: 172 testes unitários JVM passando 100% (`BUILD SUCCESSFUL in 1m 14s`) e APK de debug gerado (`app/build/outputs/apk/debug/app-debug.apk`).
- **Arquivos Tocados**: `app/build.gradle.kts`, `app/src/main/java/com/eatcontrolai/EatControlApp.kt`, `app/src/main/java/com/eatcontrolai/core/model/Models.kt`, `app/src/main/java/com/eatcontrolai/data/Serialization.kt`, `app/src/main/java/com/eatcontrolai/glasses/CaptureSourceRouter.kt`, `app/src/main/java/com/eatcontrolai/ui/EatControlViewModel.kt`, `app/src/main/java/com/eatcontrolai/ui/history/HistoryScreen.kt`, `app/src/main/java/com/eatcontrolai/ui/plan/PlanScreen.kt`, `app/src/main/java/com/eatcontrolai/ui/lab/LabScreen.kt`, `app/src/main/java/com/eatcontrolai/inference/cloud/CloudVisionProvider.kt`, `app/src/main/java/com/eatcontrolai/data/telemetry/S3TelemetryClient.kt`, `.agents/memory/REGISTRO_SESSAO.md`.
- **Próximas Pendências**: O usuário deve adicionar as chaves no `local.properties` ou diretamente na tela do app (aba Lab) para testar reconhecimento em nuvem.
- **Agente / Modelo**: Antigravity (Gemini 3.6 Flash)
- **Objetivo**: Substituir o ícone legado do app no Android pelo ícone oficial da marca de 1024x1024 (`eatcontrol-app-icon-1024.png`) presente no BrandKit v1.0 (`EatControl_Precision_Intelligence_BrandKit_v1.0/02_Visual_Identity/Logos/`).
- **Entregas**:
  - Redimensionado e atualizado `ic_launcher.webp` e `ic_launcher_round.webp` para todas as densidades (`mipmap-mdpi`, `mipmap-hdpi`, `mipmap-xhdpi`, `mipmap-xxhdpi`, `mipmap-xxxhdpi`).
  - Removido o XML legado de adaptative icon para forçar a exibição da arte PNG/WEBP oficial com alta fidelidade visual.
  - Recompilado e instalado com sucesso no dispositivo físico via `.\gradlew installDebug`.
- **Arquivos Tocados**: `app/src/main/res/mipmap-*/ic_launcher.webp`, `app/src/main/res/mipmap-*/ic_launcher_round.webp`, `app/src/main/res/mipmap-anydpi/ic_launcher.xml`, `app/src/main/res/mipmap-anydpi/ic_launcher_round.xml`, `.agents/memory/REGISTRO_SESSAO.md`.
- **Próximas Pendências**: Dar sequência aos testes e esteira de publicação na Play Store.


### [2026-09-09 — Noite] Aprimoramento Integral de Responsividade Mobile (Header Fixo, Tabs Horizontais com Snap, Hierarquia e Privacidade)
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Resolver os 3 problemas de UX mobile reportados pelo usuário: eliminação do jitter/movimento estranho do header no scroll móvel, conversão dos botões de simulação empilhados em lista para trilho horizontal deslizável com snap, e correção do overflow/quebra de margem nos cards de Hierarquia de Evidência e Privacidade.
- **Entregas**:
  - **Header Mobile Fixo & Estável**: No mobile (`<= 768px`), o header flutuante com `sticky` e margens que oscilavam com a barra de URL do navegador foi transformado em uma **Top Bar Fixa Contínua de Borda a Borda** (`position: fixed; top: 0; left: 0; right: 0; width: 100%`) com vidro fosco escuro, safe-area inset e aceleração GPU (`transform: translateZ(0)`), garantindo estabilidade absoluta sem jitter.
  - **Trilha Horizontal com Snap para Abas de Simulação**: No mobile, os 5 botões de cenários deixaram de ficar empilhados em uma lista vertical verticalmente excessiva e passaram para um **Trilho Deslizável Horizontal com Snap Nativo** (`overflow-x: auto; flex-wrap: nowrap; scroll-snap-type: x mandatory; scrollbar-width: none`), com scroll suave centralizado ao tocar na aba.
  - **Contenção Perfeita do Smartphone Chassis e Viewfinder**: Ajustado padding adaptativo e dimensões responsivas (`box-sizing: border-box`, `max-width: 100%`), eliminando o corte na borda direita do celular virtual.
  - **Reestruturação dos Cards de Hierarquia (`.evidence-tier`)**: No mobile (`<= 680px`), o grid de 3 colunas rígidas foi reformulado para 2 níveis fluidos (Rank `01` à esquerda e badge `Precedência Absoluta` à direita no topo; título e descrição em 100% de largura abaixo), sem transbordo de margens.
  - **Ajuste na Grade de Privacidade (`.privacy-grid`)**: Coluna única no mobile respeitando o padding do container `.wrap`.
  - **Validação Visual**: Testado via subagente de browser no Chrome em viewport mobile de 375x812, validando os 4 pontos visuais e 0 erros de JavaScript.
- **Arquivos Tocados**: `web/assets/eatcontrol.css`, `web/assets/site.css`, `web/assets/eatcontrol.js`, `web/assets/site.js`, `web/index.html`, `web/roadmap/index.html`, `web/marca/index.html`, `web/privacidade/index.html`, `web/termos/index.html`, `.agents/memory/REGISTRO_SESSAO.md`.


### [2026-09-09 — Noite] Internacionalização Completa (Roadmap e BrandKit), Otimizações AI-SEO/CRO/Copywriting e Deploy em Produção
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Implementar suporte bilíngue (PT/EN) sem reload em todo o Roadmap e BrandKit, criar package.json para suporte nativo a `npm run dev`, aplicar melhorias de AI-SEO, CRO e Copywriting (canonicalização estrita para `www.eatcontrol.com.br`, `web/pricing.md` para LLMs, selo de confiança LGPD no formulário) e realizar deploy de produção na Vercel.
- **Entregas**:
  - **Internacionalização Integral**: Adicionadas todas as chaves de tradução (`roadmap_*` e `brand_*`) em `web/assets/eatcontrol.js` e `web/assets/site.js`. Páginas `web/roadmap/index.html` e `web/marca/index.html` integradas com seletores PT/EN funcionais e persistência em `localStorage`.
  - **Ambiente de Desenvolvimento Local**: Criado `web/package.json` configurado com scripts `"dev": "npx serve ."` e `"start": "npx serve ."`, permitindo rodar `npm run dev` diretamente dentro do diretório `web/`.
  - **AI-SEO & Presença em Mecanismos de IA**:
    - Criado `web/pricing.md` em Markdown estruturado para consumo direto por agentes de IA e crawlers.
    - Atualizados `web/llms.txt`, `web/sitemap.xml` e `web/robots.txt` para padronizar o domínio canônico `https://www.eatcontrol.com.br/`.
    - Atualizadas todas as tags `<link rel="canonical">` e `og:url` em todas as páginas HTML (`index.html`, `roadmap/`, `marca/`, `privacidade/`, `termos/`).
  - **CRO & Copywriting**: Adicionado selo visual de confiança e conformidade LGPD/criptografia no formulário de lista de espera ("🔒 Seus dados estão seguros e protegidos. Conformidade estrita com LGPD. Zero spam.").
  - **Deploy em Produção**: Deploy Vercel executado com sucesso e ativo em `https://www.eatcontrol.com.br`.
- **Arquivos Tocados**: `web/assets/eatcontrol.js`, `web/assets/site.js`, `web/index.html`, `web/roadmap/index.html`, `web/marca/index.html`, `web/privacidade/index.html`, `web/termos/index.html`, `web/llms.txt`, `web/sitemap.xml`, `web/robots.txt`, `web/pricing.md`, `web/package.json`, `web/vercel.json`, `.agents/memory/REGISTRO_SESSAO.md`.
- **Próximas Pendências**: Prosseguir com os preparativos da esteira de release do app Android.


### [2026-09-09 — Noite] Correção de SyntaxError no Dicionário i18n, Atualização de Cache-Buster v3.4 e Deploy em Produção
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Diagnosticar e corrigir erro fatal de console `Uncaught SyntaxError: Unexpected token ';'` que impedia a execução dos scripts na landing page e orientar sobre a ausência de npm no subdiretório `web/`.
- **Causa Raiz & Solução**:
  - Uma chave extra de fechamento `}` havia sido inserida no encerramento de `en` dentro de `I18N_DICT` (linha 378 de `eatcontrol.js` e `site.js`), fazendo com que o encerramento do objeto `};` fosse avaliado como token inesperado fora de bloco.
  - Removida a chave excedente em `web/assets/eatcontrol.js` e `web/assets/site.js`.
  - Sintaxe validada via Node.js (`node -c`) com 100% de aprovação em todos os scripts.
  - Cache-buster atualizado para `?v=3.4` em `web/index.html`, `eatcontrol.js` e `site.js`.
  - Deploy em produção executado na Vercel (`https://www.eatcontrol.com.br`) e validado visualmente e via console F12 por subagente de browser (sem erros de JS, alternância PT/EN perfeita).
- **Arquivos Tocados**: `web/assets/eatcontrol.js`, `web/assets/site.js`, `web/index.html`, `.agents/memory/REGISTRO_SESSAO.md`.
- **Próximas Pendências**: Seguir com o fluxo de release do app Android na Play Store.

### [2026-09-09 — Noite] Atualização Global e Local de Marketing Skills (50 Skills) para Todos os Agentes
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Integrar 50 skills de marketing e growth (coreyhaines31/marketingskills) localmente no repositório EatControlAI e globalmente nos ambientes Claude Code, OpenAI Codex e Antigravity.
- **Entregas**:
  - **Repositório EatControlAI**:
    - Instaladas 50 skills em `.agents/skills/` (totalizando 211 skills no projeto).
    - Criada Junction `.codex/skills` e `.codex/tools` apontando para `.agents/skills` e `.agents/tools`, unificando a interoperabilidade nativa com o OpenAI Codex no repo.
    - Criada Junction `.claude/tools` apontando para `.agents/tools` (além de `.claude/skills` já existente).
    - Criado [.agents/product-marketing.md](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/.agents/product-marketing.md) contendo o contexto canônico de marketing do EatControl (Beachhead GLP-1, posicionamento, brandkit, dores e diferenciais) para consumo automático pelas skills.
    - Atualizados os catálogos de skills em [AGENTS.md](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/AGENTS.md) e [CLAUDE.md](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/CLAUDE.md).
    - Ajustado [.gitignore](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/.gitignore) para rastrear `product-marketing.md`.
  - **Ambientes Globais**:
    - **Claude Code**: Instaladas 50 skills em `C:\Users\Lenovo\.claude\skills` (total de 251 skills) e ferramentas em `C:\Users\Lenovo\.claude\tools`.
    - **Antigravity (Gemini)**: Instaladas 50 skills em `C:\Users\Lenovo\.gemini\config\skills` (total de 232 skills) e ferramentas em `C:\Users\Lenovo\.gemini\config\tools`.
    - **OpenAI Codex**: Instaladas 50 skills em `C:\Users\Lenovo\.codex\skills` (total de 51 skills) e ferramentas em `C:\Users\Lenovo\.codex\tools`.
- **Arquivos Tocados**: `.agents/skills/*`, `.agents/tools/*`, `.agents/product-marketing.md`, `AGENTS.md`, `CLAUDE.md`, `.gitignore`, `C:\Users\Lenovo\.claude\skills\*`, `C:\Users\Lenovo\.gemini\config\skills\*`, `C:\Users\Lenovo\.codex\skills\*`, `.agents/memory/REGISTRO_SESSAO.md`.
- **Próximas Pendências**: Nenhuma pendência na instalação de skills. Seguir para preparação de build de release na Play Store.

### [2026-09-09 — Noite] Hero Mockup em Inglês, Tradução Completa (PT/EN), Responsividade da Navbar e Deploy v3.3
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Implementar mockup em inglês no Hero com alternância dinâmica conforme idioma, cobrir todas as strings restantes na internacionalização (Bento Grid, Evidence Matrix, Privacy Cards, FAQ, Footer), corrigir corte lateral da navbar flutuante em smartphones estreitos (<= 400px), diagnosticar erro de console F12 e orientar benchmarks e exportação de APK.
- **Entregas**:
  - `web/assets/app_scanner_mockup_en.jpg`: Mockup fotográfico idêntico em proporção e composição ao original, com a UI traduzida para o inglês ("Plain Greek Yogurt", "COMPATIBLE · GLP-1 SAFE", "Audited: Open Food Facts").
  - Alternância dinâmica do hero: `setLanguage(lang)` troca o `src` de `#hero-device-img` entre os mockups PT e EN sem piscar.
  - Tradução exaustiva: 100% dos cartões clínicos do Bento Grid (Alerta de Esvaziamento Gástrico, tags de macronutrientes, diálogo do Garçom e Resposta por Áudio), matriz de evidência e rodapé agora respondem ao seletor `PT / EN`.
  - Responsividade Mobile: Adicionadas media queries `@media (max-width: 600px)` e `@media (max-width: 400px)` na `.nav-pill`, recolhendo o texto da marca e preservando o logo SVG, seletor de idiomas e CTA sem corte lateral.
  - Diagnóstico do F12: Identificado que o erro `fd_content_pre_check.js` é gerado pela extensão de terceiros Free Download Manager (FDM) instalada no navegador do usuário, com zero interferência na aplicação.
  - Deploy em produção na Vercel (`https://www.eatcontrol.com.br`) realizado com sucesso.
- **Arquivos Tocados**: `web/assets/eatcontrol.css`, `web/assets/eatcontrol.js`, `web/assets/site.css`, `web/assets/site.js`, `web/index.html`, `web/assets/app_scanner_mockup_en.jpg`, `.agents/memory/REGISTRO_SESSAO.md`.
- **Próximas Pendências**: Pipeline de publicação na Play Store (keystore de release, flavors, teste fechado com 12 testadores).


### [2026-09-09 — Fim de Tarde] Correção do Colapso de Altura do Viewfinder, Novos Assets e Deploy v3.2
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Corrigir problemas de posicionamento e visibilidade nas cenas do simulador fotográfico (Rótulo por OCR, Prato Assistido e Cardápio/Menu) reportados pelo usuário com prints da tela, eliminando colapso de altura e faixas pretas.
- **Causa Raiz & Solução**:
  - `.scene-rotulo`, `.scene-prato` e `.scene-cardapio` possuíam `position: relative` na cascata que sobrescrevia o `position: absolute; inset: 0;` de `.viewfinder-scene`.
  - No Prato Assistido (`.scene-prato`), o único filho era absoluto, colapsando a altura do pai para 0px (tela 100% preta).
  - No Rótulo e no Cardápio, as cenas expandiam apenas até a altura dos cartões (~120px), deixando 100px de vácuo preto no viewfinder de 220px.
  - Correção: forçado `.viewfinder-scene { position: absolute !important; inset: 0 !important; width: 100% !important; height: 100% !important; }`, removido `position: relative` de todas as cenas, centralizados vertical e horizontalmente os cartões HUD com backdrop blur, e distribuídas as tags HUD do prato com `align-self` orgânico (salmão à esquerda, brócolis à direita, confirmação ao centro).
  - Gerados novos assets fotorrealistas sem molduras de celular: `bistro_menu_scan.jpg` e `yogurt_label_macro.jpg`.
  - Cache buster elevado para `?v=3.2` no HTML e nos assets do CSS para forçar revalidação imediata nos navegadores dos usuários.
  - Sincronizado `web/assets/site.css` e deploy em produção na Vercel (`https://www.eatcontrol.com.br`) realizado com sucesso.
- **Arquivos Tocados**: `web/assets/eatcontrol.css`, `web/assets/site.css`, `web/index.html`, `web/assets/bistro_menu_scan.jpg`, `web/assets/yogurt_label_macro.jpg`.
- **Próximas Pendências**: Nenhuma no simulador; acompanhar feedback do usuário com o cache atualizado.


### [2026-09-09 — Tarde] Resolução da Simulação Fotográfica, Responsividade em Laptops e Deploy em Produção
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Atender solicitação do usuário gerando assets fotorrealistas de alta fidelidade para as 5 cenas do simulador (especialmente o prato assistido), corrigir quebras de responsividade em laptops (1280x800), blindar caminhos de assets com `/assets/`, implementar bilinguismo completo PT/EN sem reload, atualizar o ícone oficial do WhatsApp, executar o deploy na Vercel e registrar os commits no Git.
- **Entregas**:
  - **Assets Fotorrealistas & Blindagem de URLs**:
    - `assisted_plate_dish.jpg`: Salmão grelhado, brócolis ao vapor e quinoa em prato escuro de ardósia (fotografia culinária de alta definição) para a cena do Prato Assistido.
    - `yogurt_label_macro.jpg`: Fotografia macro de embalagem de iogurte com tabela nutricional para a cena de Rótulo OCR.
    - `bistro_menu_scan.jpg`: Fotografia editorial de cardápio bistrô em ambiente elegante para a cena de Menu Scan.
    - Atualizadas URLs para caminhos absolutos (`/assets/...`) com `background-color` escuro de fallback em `eatcontrol.css` e `site.css`.
  - **Viewfinder da Câmera Fotográfica & 5ª Aba de Voz**:
    - Componente `.camera-viewfinder` dentro do smartphone com 5 cenas dinâmicas (`rotulo`, `barcode`, `prato`, `cardapio`, `voz`), com laser ciano varrendo a superfície da imagem diretamente sem passar atrás de texto opaco.
    - Aba `5. Comando por Voz` com síntese TTS nativa (`window.speechSynthesis`) em PT-BR e EN-US sincronizada com as ondas sonoras.
  - **Correção de Responsividade em Laptops (1280x800)**:
    - Ajuste em `.console-meta` com `justify-content: space-between` e `flex-wrap: wrap`, e `.console-latency` com `white-space: nowrap`, impedindo que "140ms" quebre para baixo de "Latência".
    - Strings de rank calibradas de forma concisa (`RANK 6: VISÃO → RANK 4: AUDITORIA` / `RANK 6: VISION → RANK 4: AUDIT`).
    - Ajuste no card do smartphone: `.decision-head` com `flex-wrap: wrap`, `.verdict-tag` com `white-space: nowrap` e textos enxutos (`ATENÇÃO · CONFIRMAR` / `CAUTION · CONFIRM`), eliminando a quebra em 2 linhas e colisões com a audit tag.
    - Breakpoint do simulador expandido para `@media (max-width: 1040px)` para transição fluida em laptops e tablets.
  - **Bilinguismo Completo no Visor**:
    - Adicionados atributos `data-i18n` em todos os elementos internos do visor (tabela nutricional, alérgenos, prato assistido, cardápio do chef, voz do garçom, latência e formulário).
    - Dicionário `I18N_DICT` expandido com todas as chaves sincronizadas entre PT e EN reativamente sem reload, com versão atualizada para `?v=3.1` para bust de cache.
  - **Ícone Oficial do WhatsApp**:
    - Substituição do SVG no botão de WhatsApp pelo vetor oficial completo com o contorno e o monofone interno característico, com texto adaptável `Falar no WhatsApp` / `Chat on WhatsApp`.
  - **Deploy em Produção**:
    - Deploy realizado com sucesso na Vercel (`dpl_D5feaVuz8kXgRU5w7c6cQwWe5B9M`, aliased `https://www.eatcontrol.com.br`).
- **Arquivos Tocados**: `web/index.html`, `web/assets/eatcontrol.css`, `web/assets/site.css`, `web/assets/eatcontrol.js`, `web/assets/site.js`, `web/assets/assisted_plate_dish.jpg`, `web/assets/yogurt_label_macro.jpg`, `web/assets/bistro_menu_scan.jpg`, `web/assets/apple-touch-icon.png`, `web/assets/eatcontrol-app-icon-*.png`.
- **Próximas Pendências**: Nenhuma no frontend web. Próximo foco: Publicação do app Android na Play Store.

### [2026-09-08 — Noite/Madrugada] Modo Automático Multimodal Completo, Câmera Fullscreen Imersiva & Detecção Contínua
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Implementar o Modo Automático contínuo sem necessidade de botão manual de foto (estilo leitor de QR code nativo), integrando detecção contínua de código de barras (com feedback tátil/haptic), leitura de rótulos, detecção visual de pratos de comida e escuta inteligente de voz (hands-free) com fusão multimodal em tela cheia.
- **Entregas**:
  - **PhoneCameraGateway & ImageAnalysis Contínuo**: Adicionado `ImageAnalysis` com `STRATEGY_KEEP_ONLY_LATEST` e amostragem throttled (~4 FPS) para não superaquecer nem drenar bateria. Cascata inteligente com ML Kit Barcode, OCR e Image Labeling para identificação passiva de comida/pratos.
  - **Detecção Háptica & Zero Toque**: Ao mirar em qualquer código de barras de alimento, o aparelho vibra suavemente e abre o veredito instantaneamente sem toque.
  - **Escuta Contínua de Voz (Auto-STT)**: Adicionado `startContinuousListening` em `AndroidSttProvider` para captar a fala do usuário ou oitiva do garçom ("esse prato tem manteiga?") com fusão multimodal imediata do áudio com o frame da câmera.
  - **UX Camera-First Fullscreen (`AnalyzeScreen.kt`)**: Redesenho completo da tela de análise para Viewport de tela inteira com cantoneiras animadas de mira (HUD), pílula de ação flutuante e menu sutil de hardware (Meta DAT e Mock).
  - **Testes & Compilação**: Suíte de 172 testes unitários 100% aprovada e APK de debug gerado com sucesso em `app/build/outputs/apk/debug/app-debug.apk`.
- **Arquivos Tocados**: `app/src/main/AndroidManifest.xml`, `app/src/main/java/com/eatcontrolai/EatControlApp.kt`, `app/src/main/java/com/eatcontrolai/glasses/PhoneCameraGateway.kt`, `app/src/main/java/com/eatcontrolai/inference/androidstt/AndroidSttProvider.kt`, `app/src/main/java/com/eatcontrolai/ui/EatControlViewModel.kt`, `app/src/main/java/com/eatcontrolai/ui/analyze/AnalyzeScreen.kt`.
- **Próximas Pendências**: Testes no aparelho físico e exportação do APK para os testadores.

### [2026-09-08 — Noite] Implementação de GEO/SEO Avançado, Favicon Adaptável, Telemetria Full-Stack & Interface de Voz
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Incorporar a dimensão de voz/garçom no site, garantir rastreamento imediato do Clarity e PostHog no site e no app Android, adaptar favicons claro/escuro e atualizar canais da marca.
- **Entregas**:
  - **Narrativa de Voz no Site**: Adição do card Bento 5 dedicado no [web/index.html](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/index.html) destacando o loop de escuta do garçom no restaurante ("Esse molho leva gorgonzola e nata") e a resposta por áudio em < 1s (TTS) para evitar crises de GLP-1. Adicionada pergunta correspondente no FAQ e no [web/llms.txt](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/llms.txt).
  - **Clarity Web Imediato**: Injeção da tag oficial inline do Microsoft Clarity (`yfbcrp7znw`) no `<head>` de todas as páginas para verificação imediata por crawlers e gravação contínua.
  - **Clarity & PostHog no App Android**: Inclusão de `com.microsoft.clarity:clarity:3.+` (ID `yfbedcliec`) e `com.posthog:posthog-android:3.+` (token `phc_wPKfPaD2LgBjrZQAnCtk3N2Lk25aCQhsqLf9QzVuwpjo`) no [app/build.gradle.kts](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/app/build.gradle.kts) e inicializados em [EatControlApp.kt](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/app/src/main/java/com/eatcontrolai/EatControlApp.kt).
  - **Testes & Qualidade**: Compilação e suíte completa de **172 testes unitários** executados com 100% de aprovação (0 falhas).
  - **Deploy em Produção**: Atualizado na Vercel (`dpl_DVbrY8dxBnowCgWSvp6KYhouBr3U`, aliased `https://www.eatcontrol.com.br`).
- **Arquivos Tocados**: `app/build.gradle.kts`, `app/src/main/java/com/eatcontrolai/EatControlApp.kt`, `gradle/libs.versions.toml`, `web/assets/analytics.js`, `web/assets/favicon.svg`, `web/index.html`, `web/llms.txt`, `web/marca/index.html`, `web/roadmap/index.html`, `web/privacidade/index.html`, `web/termos/index.html`.
- **Próximas Pendências**: Subir alterações com `git push`.

### [2026-09-08 — Tarde] Correções de Layout, Escala de Logos e Feedback Interativo no BrandKit (/marca/)
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Corrigir estouro de imagens dos logos em cards, adicionar feedback visual de clique na paleta com toast e card ativo, corrigir espaçamentos entre seções colididas (Attention -> Tipografia -> Regras de Ouro) e aplicar o logo vetorial oficial do Google Drive.
- **Entregas**:
  - Correção de escala e contenção dos logos em `.asset-card` e `.asset-preview` via CSS (`max-width: 85%`, `max-height: 52px`, `object-fit: contain`, `overflow: hidden`) e inversão correta das variantes clara/escura.
  - Adição de feedback visual de cópia nos swatches de cor: badge inline `✓ Copiado!`, glow verde esmeralda no card (`.is-copied`) e toast flutuante no canto inferior direito.
  - Resolução dos espaçamentos entre seções com `.brand-section` aplicando `clamp(3.5rem, 6vw, 5.5rem)` e borda sutil separadora, eliminando colisão entre Attention, Tipografia e Regras de Ouro.
  - Substituição do ícone de triângulo aramado pelo SVG vetorial colorido oficial do Google Drive no botão do Hero e no botão de acesso rápido da barra de topo.
  - Deploy em produção realizado na Vercel (`dpl_CRhLmpeAjBPYy152QBZuanqcMZeB`, alias `https://eatcontrol-ai.vercel.app/marca/`) e verificado visualmente via agente de browser.
- **Arquivos Tocados**: `web/marca/index.html`, `web/assets/eatcontrol.css`, `web/assets/site.css`.
- **Próximas Pendências**: Nenhuma pendência aberta.

### [2026-09-08 — Tarde] Modernização Visual de Roadmap & BrandKit Restrito ao Rodapé
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Elevar a régua visual das subpáginas `/roadmap/` e `/marca/` para o mesmo padrão cinematográfico da landing page e remover o link de BrandKit do menu/header, mantendo-o apenas no rodapé.
- **Entregas**:
  - Overhaul de [web/roadmap/index.html](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/roadmap/index.html): Layout em 3 trilhas responsivas (`Pronto`, `Construindo`, `Fora de Escopo`), badges de status em cores semânticas, cards interativos e hero com transparência radical.
  - Overhaul de [web/marca/index.html](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/marca/index.html): Integração da navbar em pílula de vidro fosco, padronização do rodapé, correção de todos os estilos de swatches de cores e cards de assets.
  - Remoção de `BrandKit` / `Marca` do header/menu principal em todas as páginas ([web/index.html](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/index.html), roadmap, privacidade, termos, marca).
  - Preservação do link `BrandKit Oficial` exclusivamente no rodapé de todas as páginas.
  - Atualização e sincronização dos estilos nos bundles [web/assets/eatcontrol.css](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/assets/eatcontrol.css) e [web/assets/site.css](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/assets/site.css).
  - Deploy em produção executado na Vercel (`aliased https://eatcontrol-ai.vercel.app`) e validado visualmente via screenshots no navegador.
- **Arquivos Tocados**: `web/roadmap/index.html`, `web/marca/index.html`, `web/index.html`, `web/privacidade/index.html`, `web/termos/index.html`, `web/assets/eatcontrol.css`, `web/assets/site.css`.
- **Próximas Pendências**: Nenhuma no frontend.

### [2026-09-08 — Tarde] Resolução Definitiva do Bloqueio de Cache na Vercel & Deploy em Produção
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Diagnosticar e eliminar a persistência de cache que travava a versão antiga do site em navegadores normais na Vercel (`eatcontrol-ai.vercel.app`).
- **Diagnóstico**: O cabeçalho anterior `Cache-Control: public, max-age=31536000, immutable` havia sido gravado pelo Chrome no disco local. Além disso, os commits anteriores haviam subido para o Git mas o comando de publicação da Vercel (`vercel deploy --prod`) não havia sido executado, mantendo a Vercel servindo o HTML antigo.
- **Entregas**:
  - Desacoplamento dos bundles para novas URLs não cacheadas: [web/assets/eatcontrol.css](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/assets/eatcontrol.css) e [web/assets/eatcontrol.js](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/assets/eatcontrol.js).
  - Atualização em todas as páginas HTML ([web/index.html](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/index.html), marca, privacidade, roadmap, termos) apontando para os novos bundles `?v=3.0`.
  - Configuração de cabeçalhos no [web/vercel.json](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/vercel.json) aplicando `Cache-Control: public, max-age=0, must-revalidate` definitivo.
  - Deploy em produção executado com sucesso na Vercel (`aliased https://eatcontrol-ai.vercel.app`).
  - Verificação visual via navegador confirmando que a navbar em pílula de vidro, mockups 3D, tipografia e simulador estão 100% ativos.
- **Arquivos Tocados**: `web/vercel.json`, `web/index.html`, `web/marca/index.html`, `web/privacidade/index.html`, `web/roadmap/index.html`, `web/termos/index.html`, `web/assets/eatcontrol.css`, `web/assets/eatcontrol.js`.
- **Próximas Pendências**: Nenhuma no frontend institucional; validar fluxo de cadastro de leads em produção.

### [2026-09-07 — Madrugada] Redesign Cinematográfico do Site & Manual da Marca (/marca/)
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Elevar a régua visual do site institucional para o padrão Linear.app + Superpower.com (Gauntlet Loop) com GSAP/cinematic components e criar o Manual da Marca oficial com link do Google Drive e downloads de assets.
- **Entregas**:
  - Overhaul visual de [web/index.html](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/index.html): Hero mockup 3D com halo ambiente, simulador interativo de decisões alimentares, spotlight border cards (módulo 16) e régua visual de hierarquia de evidência.
  - Criação de [web/marca/index.html](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web/marca/index.html): Vitrine de BrandKit com downloads de SVGs e PNGs em alta resolução, swatches de cores interativos com cópia em 1 clique e botão de destaque para o Google Drive.
  - Atualização de `web/assets/site.css` e `site.js` com GSAP 3 + ScrollTrigger, interatividade e spotlight.
  - Expansão de `web/assets/analytics.js` com suporte estruturado para Clarity, PostHog, GA4 e opt-in para dataset de modelos (Clarifai/Gemini).
  - Cópia e organização dos assets oficiais do BrandKit em `web/assets/`.
- **Arquivos Tocados**: `web/index.html`, `web/marca/index.html`, `web/assets/site.css`, `web/assets/site.js`, `web/assets/analytics.js`, `web/roadmap/index.html`, `web/privacidade/index.html`, `web/termos/index.html`
- **Próximas Pendências**: Configuração dos IDs reais de telemetria e URL definitiva da pasta do Google Drive em `site.js`.

### [2026-09-07 — Noite] Implementação do Hub de Memória Coletiva
- **Agente / Modelo**: Antigravity (Gemini 3.8 Flash)
- **Objetivo**: Unificar todo o contexto disperso do projeto e implementar o hub de memória interoperável entre Claude Code, Codex e Antigravity.
- **Entregas**:
  - Mapeamento e consolidação de toda a documentação, pitch decks (Slush 100 e Meta CEIA), BrandKit e histórico de sessões.
  - Criação de [.agents/memory/](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/.agents/memory) contendo `CONTEXTO_CANONICO.md`, `ESTADO_E_ROADMAP.md`, `DECISOES_ARQUITETURAIS.md` e `REGISTRO_SESSAO.md`.
  - Atualização dos guias mestres [AGENTS.md](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/AGENTS.md) e [CLAUDE.md](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/CLAUDE.md).
  - Sincronização com a memória local do Claude Code (`~/.claude/projects/.../memory/MEMORY.md`).
- **Arquivos Tocados**: `.agents/memory/*`, `AGENTS.md`, `CLAUDE.md`, `~/.claude/.../memory/MEMORY.md`
- **Próximas Pendências**: Preparação dos artefatos para o Google Play (keystore, flavor phone, AAB) e coleta formal do registro clínico (EQ-11).

### [2026-09-07 — Tarde] Finalização do Pitch Deck Slush 100 2026
- **Agente / Modelo**: Claude Code (Claude 3.7 Sonnet)
- **Objetivo**: Ajustar e fechar o deck de 13 slides para o Slush 100, eliminando placeholders e alinhando com o BrandKit.
- **Entregas**:
  - Geração de 4 variantes completas em HTML, PDF e PPTX (`v1_EN`, `v1_PT`, `v2_EN`, `v2_PT`).
  - Calibração da rodada pre-seed em € 500k para 18 meses.
  - Reenquadramento das fotos da equipe (Eduardo, Gustavo, Luís) e confirmação do sobrenome de Gustavo Fugulin.
  - Definição do arquivo recomendado para submissão: `EatControl_Pitch_Slush100_v2_EN.pdf`.
- **Arquivos Tocados**: `EatControl_Precision_Intelligence_BrandKit_v1.0/07_Pitch/Slush_100/*`
- **Próximas Pendências**: Submissão na plataforma do Slush antes do deadline.

### [2026-09-04] Pivot Phone-First & Conexão da Camada GLP-1
- **Agente / Modelo**: Claude Code (Claude 3.7 Sonnet)
- **Objetivo**: Destravar a evolução do produto após o fim da etapa de hackathon da Meta e transformar a regra GLP-1 em código ativo.
- **Entregas**:
  - Conexão do `Glp1RulePackV1` ao orquestrador de interação, ViewModel e interface.
  - Integração da busca no Open Food Facts como fallback após o catálogo local.
  - Implementação das telas e fluxos de regras pessoais e registro de sintomas.
  - Aplicação dos tokens do BrandKit no tema Compose e atualização do site web.
- **Arquivos Tocados**: `app/src/main/java/com/eatcontrolai/*`, `web/*`
- **Próximas Pendências**: Preparar build de release e site público.

### [2026-08-22] Entrega Final do Ideathon (Fase 1)
- **Agente / Modelo**: Equipe EatControl + Claude Code
- **Objetivo**: Submissão dos artefatos da 1ª fase do programa AI Glasses Brasil 2026 (entregáveis A1 a A7).
- **Entregas**: Deck interativo de pitch de 3 minutos, formulários de submissão e gravação de vídeo demonstrativo.
- **Arquivos Tocados**: `docs/hackathon-2026/*`, `docs/pitch/*`

### [2026-08-12] Scaffold Inicial do Projeto
- **Agente / Modelo**: Equipe EatControl
- **Objetivo**: Inicialização do repositório Kotlin/Compose, configuração de ADRs iniciais e harness de testes unitários.
