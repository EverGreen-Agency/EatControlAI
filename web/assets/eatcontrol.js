/*
  EatControl — Interatividade, Micro-animações Cinemáticas, Simulador e Bilinguismo.
  GSAP 3 + ScrollTrigger com fallback para IntersectionObserver.
*/

(function () {
  'use strict';

  var CONTACT = {
    formEndpoint: '',
    whatsapp: 'https://wa.me/5511999999999',
    email: 'eatcontrol.ai@gmail.com',
    googleDriveBrandKit: 'https://drive.google.com/drive/folders/1BloJ8GZsRYgw84Djf_riHyDdJxG4KaXe?usp=sharing'
  };

  var reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  var currentLang = 'pt';
  var currentScenarioKey = 'rotulo';
  var isAudioPlaying = false;
  var currentUtterance = null;

  // ------------------------------------------------------------------ GSAP & Reveal

  function initAnimations() {
    if (reduced) return;

    if (window.gsap && window.ScrollTrigger) {
      window.gsap.registerPlugin(window.ScrollTrigger);

      // Hero entry
      window.gsap.from('.hero-content > *', {
        y: 28,
        opacity: 0,
        duration: 0.9,
        stagger: 0.1,
        ease: 'power3.out'
      });

      window.gsap.from('.hero-visual', {
        y: 35,
        opacity: 0,
        scale: 0.96,
        duration: 1.1,
        delay: 0.2,
        ease: 'power3.out'
      });

      // Section headings
      window.gsap.utils.toArray('section:not(.hero)').forEach(function (sec) {
        window.gsap.from(sec.querySelectorAll('h2, .section-eyebrow, .section-muted'), {
          scrollTrigger: {
            trigger: sec,
            start: 'top 85%',
            toggleActions: 'play none none none'
          },
          y: 24,
          opacity: 0,
          duration: 0.8,
          stagger: 0.1,
          ease: 'power3.out'
        });
      });

      // Bento cards reveal
      window.gsap.from('.bento-card', {
        scrollTrigger: {
          trigger: '.bento-grid',
          start: 'top 80%',
          toggleActions: 'play none none none'
        },
        y: 30,
        opacity: 0,
        duration: 0.7,
        stagger: 0.12,
        ease: 'power3.out'
      });
    }
  }

  // ----------------------------------------------------------- Spotlight Cards

  function setupSpotlight() {
    if (reduced || !window.matchMedia('(hover: hover)').matches) return;

    var cards = document.querySelectorAll('.spotlight-card');
    cards.forEach(function (card) {
      card.addEventListener('pointermove', function (e) {
        var rect = card.getBoundingClientRect();
        card.style.setProperty('--mx', (e.clientX - rect.left) + 'px');
        card.style.setProperty('--my', (e.clientY - rect.top) + 'px');
      });
    });
  }

  // ---------------------------------------------------- Dicionário de Idiomas (PT / EN)

  var I18N_DICT = {
    pt: {
      nav_tech: 'Tecnologia',
      nav_sim: 'Simulador',
      nav_hierarchy: 'Hierarquia',
      nav_privacy: 'Privacidade',
      nav_faq: 'Dúvidas',
      nav_roadmap: 'Roadmap',
      nav_cta: 'Acesso Antecipado',
      hero_badge: 'PROTOCOLO GLP-1 · OZEMPIC / MOUNJARO / WEGOVY',
      hero_title: 'A decisão acontece no mercado,<br><span class="hero-gradient">não no consultório.</span>',
      hero_lede: 'Aponte a câmera para um rótulo, código de barras ou cardápio. O EatControl audita a composição nutricional no próprio smartphone e avisa se aquilo compromete o seu plano.',
      hero_btn_join: 'Entrar no teste fechado',
      hero_btn_sim: 'Ver simulador ao vivo',
      hero_trust_off: 'Base Open Food Facts',
      hero_trust_local: 'Decisão On-Device',
      hero_trust_hallucination: 'Zero Alucinação de Calorias',
      sim_eyebrow: 'Simulação em Tempo Real',
      sim_title: 'Experimente o ciclo de decisão clínica',
      sim_muted: 'O produto opera sob a cadência <strong>Olhar → Perguntar → Ouvir → Seguir</strong>. Selecione o cenário para testar a inteligência contextual:',
      sim_tab_rotulo: '1. Rótulo por OCR',
      sim_tab_barcode: '2. Código de Barras',
      sim_tab_prato: '3. Prato Assistido',
      sim_tab_cardapio: '4. Cardápio / Menu',
      sim_tab_voz: '5. Comando por Voz',
      sim_latency: 'Latência: 140ms',
      sim_play_label: 'Ouvir',
      sim_pause_label: 'Pausar',
      sim_cta: 'Testar este fluxo no seu telefone →',
      sim_mock_nutrititle: 'TABELA NUTRICIONAL',
      sim_mock_lactose: 'Lactose declarada',
      sim_mock_frontal: '0g (Frontal)',
      sim_mock_milk: 'CONTÉM LEITE E DERIVADOS',
      sim_mock_target: 'ALVO',
      sim_mock_ean: 'EAN-13 DETECTADO · 120ms',
      sim_mock_salmon: '● Salmão Grelhado [96%]',
      sim_mock_broccoli: '● Brócolis ao Vapor [94%]',
      sim_mock_portion: 'Gramatura: Requer Confirmação',
      sim_mock_menu_head: 'BISTRO · PRATOS DO CHEF',
      sim_mock_menu_title: 'Risoto de Mignon ao Molho de Queijo',
      sim_mock_menu_alert: 'Alerta: Gorgonzola & Nata Fresca',
      sim_mock_voice_hud: 'VOZ DO GARÇOM DETECTADA',
      cta_pill: 'Lote 01 · Vagas Limitadas',
      cta_title: 'Participe do teste fechado do EatControl',
      cta_desc: 'Estamos selecionando os primeiros usuários em tratamento com GLP-1 para testar o aplicativo Android e nos ajudar a refinar as regras de segurança alimentar.',
      cta_label_name: 'Seu nome',
      cta_label_email: 'Seu e-mail principal',
      cta_label_challenge: 'Qual seu maior desafio alimentar com a medicação? (opcional)',
      cta_consent: '<strong>Opt-in de Pesquisa (Opcional):</strong> Concordo em compartilhar rótulos escaneados de forma 100% anonimizada para a calibração dos motores de inteligência alimentar do EatControl.',
      cta_submit: 'Garantir vaga no teste',
      cta_whatsapp: 'Falar no WhatsApp',
      cta_trust_seal: 'Privacidade rigorosa (LGPD) · Sem spam · Acesso 100% gratuito ao teste fechado',
      bento_eyebrow: 'Arquitetura de Percepção',
      bento_title: 'Cinco caminhos. Uma única resposta confiável.',
      bento_muted: 'O aplicativo escolhe autonomamente a trilha computacional de maior fidelidade para o contexto à sua frente:',
      bento_1_badge: 'OCR On-Device',
      bento_1_title: 'Leitura de Rótulo & Ingredientes',
      bento_1_desc: 'Quando não há código de barras, a câmera lê a tabela nutricional oficial e os avisos de alérgenos no próprio aparelho. O motor prioriza avisos regulatórios obrigatórios da Anvisa sobre qualquer marketing de embalagem.',
      bento_1_ui_head: 'TABELA NUTRICIONAL (OCR AUDIT)',
      bento_1_ui_status: 'AUDITADO',
      bento_1_ui_row1_label: 'Proteínas Totais',
      bento_1_ui_row1_val: '16g por porção',
      bento_1_ui_row2_label: 'Açúcar Adicionado',
      bento_1_ui_row2_val: '0g declarado',
      bento_2_badge: 'EAN-13 · 120ms',
      bento_2_title: 'Código de barras oficial',
      bento_2_desc: 'Lê o código em milissegundos e busca os dados na base auditável do Open Food Facts e tabelas públicas. Informação declarada sem digitação manual.',
      bento_3_badge: 'Triagem de Risco',
      bento_3_title: 'Cardápio Contextual',
      bento_3_desc: 'Identifica os pratos e ingredientes prováveis em restaurantes. Alerta sobre potenciais gatilhos de refluxo ou sobrecarga lipídica antes do pedido.',
      bento_3_alert_pill: 'Alerta de Esvaziamento Gástrico',
      bento_3_alert_desc: 'Molho com alto teor de creme de leite pode provocar náusea tardia com GLP-1.',
      bento_4_badge: 'Visão Computacional Honesta',
      bento_4_title: 'Prato Assistido sem Alucinação',
      bento_4_desc: 'Reconhece componentes visuais sem inventar gramaturas milagrosas. Se há dúvida sobre a densidade ou porção, o EatControl declara a incerteza e pede sua confirmação.',
      bento_4_tag1: 'Proteína Magra (Salmão)',
      bento_4_tag2: 'Vegetal Fibroso (Brócolis)',
      bento_4_tag3: 'Carboidrato Complexo (Quinoa)',
      bento_4_tag4: 'Confirmação de Gramatura Necessária',
      bento_5_badge: 'Voz On-Device · Resposta Falada < 1s',
      bento_5_title: 'Ouça a descrição do garçom ou pergunte por voz',
      bento_5_desc: 'No restaurante, buffet ou na cozinha, você não precisa ficar digitando. O EatControl escuta a descrição dos ingredientes falada pelo garçom (ou sua própria pergunta), extrai alérgenos e densidade lipídica no próprio smartphone, e devolve o veredito por voz discretamente no fone, óculos ou viva-voz.',
      bento_5_waiter_head: 'GARÇOM (DESCRIÇÃO DO PRATO)',
      bento_5_waiter_quote: '"Esse molho é preparado com manteiga noisette, queijo gorgonzola e redução de nata fresca."',
      bento_5_audio_head: 'RESPOSTA POR ÁUDIO (TTS 820ms)',
      bento_5_verdict: 'INCOMPATÍVEL',
      bento_5_audio_quote: '"Atenção: altíssima concentração de gordura e nata. Risco severo de náusea tardia e refluxo com medicação GLP-1."',
      hero_chip_top_title: 'Auditado: Open Food Facts',
      hero_chip_top_sub: 'EAN 7891000 · 120ms',
      hero_chip_bot_title: 'GLP-1 Safe · 16g Proteína',
      hero_chip_bot_sub: 'Zero Açúcar Adicionado',
      evid_eyebrow: 'Diferencial Inegociável',
      evid_title: 'Reliability-First: A Hierarquia de Evidência',
      evid_muted: 'Enquanto aplicativos convencionais usam inteligência artificial para chutar calorias a partir de fotos, o EatControl opera sob uma régua matemática de precedência clínica:',
      evid_tier1_title: 'Rótulo Oficial Declarado (DECLARED_LABEL)',
      evid_tier1_desc: 'Tabela nutricional oficial e lista legal de ingredientes confirmada via OCR de alta precisão.',
      evid_tier1_badge: 'Precedência Absoluta',
      evid_tier2_title: 'Base Pública Auditável (BARCODE_CATALOG)',
      evid_tier2_desc: 'Registros no Open Food Facts e tabelas públicas consolidadas (TBCA / IBGE).',
      evid_tier2_badge: 'Alta Confiança',
      evid_tier3_title: 'Confirmação Explícita do Usuário (USER_CONFIRMATION)',
      evid_tier3_desc: 'Resposta verbal ou toque direto à pergunta do app sobre a quantidade consumida.',
      evid_tier3_badge: 'Auditável',
      evid_tier4_title: 'OCR de Texto Bruto (RAW_OCR)',
      evid_tier4_desc: 'Leitura de itens em cardápios de restaurantes sem tabela formal de macronutrientes.',
      evid_tier4_badge: 'Contextual',
      evid_tier5_title: 'Inferência Visual de Prato (VISUAL_INFERENCE)',
      evid_tier5_desc: 'Identificação assistida de classes. Nunca gera macros numéricos sem confirmação.',
      evid_tier5_badge: 'Incerteza Declarada',
      evid_showcase_chip: 'Zero Alucinação de Calorias por Foto',
      priv_eyebrow: 'Privacidade Soberana',
      priv_title: 'Dados de saúde pertencem a você, não à nuvem.',
      priv_muted: 'Arquitetado sob conformidade estrita com a LGPD (Lei 13.709/2018, Arts. 7º e 11º) com processamento no aparelho:',
      priv_card1_title: 'Fotos descartadas na memória',
      priv_card1_desc: 'As fotos capturadas para leitura de rótulo ou prato são processadas na RAM do smartphone e descartadas imediatamente. Nenhuma foto é armazenada em servidores.',
      priv_card2_title: 'Histórico criptografado localmente',
      priv_card2_desc: 'Suas preferências alimentares, regras cadastradas e sintomas ficam armazenados exclusivamente no banco de dados local do seu smartphone.',
      priv_card3_title: 'Apagar histórico com 1 clique',
      priv_card3_desc: 'Você pode resetar todo o histórico, regras de intolerância e sintomas armazenados no aplicativo a qualquer instante diretamente nas configurações.',
      faq_eyebrow: 'Dúvidas Frequentes',
      faq_title: 'Perguntas frequentes sobre o EatControl',
      faq_muted: 'Entenda como o copiloto atua no suporte a escolhas alimentares seguras, determinísticas e sem alucinações.',
      faq_q1: 'O que é o EatControl e como ele funciona?',
      faq_a1: 'O <strong>EatControl</strong> é um copiloto de inteligência alimentar contextual. Você aponta a câmera do smartphone para o código de barras, rótulo nutricional ou cardápio e recebe, em menos de 1 segundo, uma orientação objetiva sobre a compatibilidade daquele alimento com suas metas e seu plano de saúde — com triagem clínica calibrada para quem utiliza medicamentos GLP-1.',
      faq_q2: 'O EatControl estima calorias tirando foto do prato?',
      faq_a2: '<strong>Não.</strong> Aplicativos tradicionais tentam inferir peso e calorias por foto e erram centenas de quilocalorias sem sinalizar incerteza. O EatControl adota o princípio <em>Reliability-First</em>: baseia-se prioritariamente em dados declarados na embalagem ou catálogo auditado (Open Food Facts). Quando a foto de um prato não possui porção declarada, o aplicativo declara <strong>INSUFICIENTE</strong> e pergunta a quantidade antes de opinar.',
      faq_q3: 'Como o app apoia quem está em tratamento com Ozempic, Mounjaro ou Wegovy?',
      faq_a3: 'Análogos de GLP-1 reduzem o apetite e retardam o esvaziamento do estômago. O consumo acidental de alimentos com alta densidade de gordura saturada, ultraprocessados ou açúcares pode desencadear náuseas intensas, refluxo ou constipação. O EatControl audita a composição contra a biblioteca de regras de segurança gástrica do GLP-1 para evitar crises no cotidiano.',
      faq_q4: 'Como é garantida a privacidade dos meus dados e fotos?',
      faq_a4: 'Arquitetura <strong>local-first</strong> e privacidade por design (LGPD). As imagens da câmera são lidas na memória volátil (RAM) e descartadas imediatamente. Seu histórico de análises, sintomas e preferências alimentares ficam armazenados exclusivamente no seu dispositivo, sem envio para servidores remotos.',
      faq_q5: 'Preciso de óculos inteligentes para usar o EatControl?',
      faq_a5: '<strong>Não.</strong> O EatControl é um aplicativo Android 100% autônomo para smartphone. Ele utiliza a câmera nativa, OCR local e assistente de voz do aparelho. A integração com smart glasses (óculos inteligentes) é uma interface complementar para conveniência futura mãos-livres.',
      faq_q6: 'Como funciona o entendimento por voz quando o garçom descreve o prato no restaurante?',
      faq_a6: 'O EatControl conta com processamento de fala para texto (STT) e sintetizador de voz (TTS) 100% on-device em português brasileiro. Ao ativar o modo de escuta no restaurante, o app capta a explicação do garçom sobre o preparo do prato ou a sua própria pergunta falada, extrai os ingredientes críticos (lactose, gordura saturada, condimentos irritantes) e responde no seu fone, óculos ou viva-voz em menos de 1 segundo se aquela refeição é compatível com seu estômago e medicação GLP-1.',
      footer_disclaimer: 'Orientação contextual e educativa. O aplicativo não formula diagnósticos, não altera posologias e não substitui o acompanhamento de endocrinologista ou nutricionista.',
      footer_roadmap: 'Roadmap Aberto',
      footer_brand: 'BrandKit Oficial',
      footer_privacy: 'Privacidade',
      footer_terms: 'Termos de Uso',
      footer_rights: '© 2026 EatControl AI. Todos os direitos reservados.',
      footer_clinical: 'Feito com rigor clínico no Brasil.',
      roadmap_hero_badge: 'CONSTRUINDO EM PÚBLICO · TRANSPARÊNCIA RADICAL',
      roadmap_hero_title: 'Roadmap aberto',
      roadmap_hero_lede: 'O que está pronto no aplicativo, o que estamos construindo agora e o que decidimos deixar de fora. Inclui o que deu errado — é a parte que costuma ensinar mais.',
      roadmap_hero_faint: 'Última auditoria técnica: 7 de setembro de 2026.',
      roadmap_col1_title: 'Pronto',
      roadmap_tag_done: 'Concluído',
      roadmap_card1_title: 'Quatro trilhas de análise',
      roadmap_card1_desc: 'Código de barras, rótulo por OCR, cardápio e prato assistido — todas rodando no aparelho, com roteamento autônomo pelo caminho mais rápido e seguro.',
      roadmap_card2_title: 'Motor de decisão com hierarquia de evidência',
      roadmap_card2_desc: 'Quatro estados possíveis, incluindo declaração expressa de incerteza. O que o fabricante declara em rótulo legal prevalece sobre inferência probabilística de IA.',
      roadmap_card3_title: 'Base pública de produtos (Open Food Facts)',
      roadmap_card3_desc: 'Catálogo conectado à maior base aberta de alimentos: escanear um código de barras real traz ingredientes, alérgenos declarados e composição por 100g.',
      roadmap_card4_title: 'Camada clínica GLP-1 ligada ao app',
      roadmap_card4_desc: 'Regras parametrizadas para limites de rotulagem Anvisa (RDC 727) sobre composição declarada, com triagem imediata de gatilhos gástricos para quem usa Ozempic, Mounjaro ou Wegovy.',
      roadmap_card5_title: 'Regras pessoais e registro de sintomas',
      roadmap_card5_desc: 'Cadastro de intolerâncias e gatilhos individuais. Ao registrar sinal de alerta médico, o app interrompe a avaliação alimentar e orienta buscar atendimento profissional.',
      roadmap_col2_title: 'Construindo',
      roadmap_tag_doing: 'Em Andamento',
      roadmap_card6_time: 'meta: 20 set 2026',
      roadmap_card6_title: 'Publicação na Google Play Store',
      roadmap_card6_desc: 'Assinatura de release, ficha da loja e distribuição para teste fechado. Transição de projeto de engenharia para produto instalável em escala.',
      roadmap_card7_time: 'em decisão clínica',
      roadmap_card7_title: 'Reconhecimento de prato assistido',
      roadmap_card7_desc: 'Hoje o app reconhece categorias amplas (salada, frango, legumes). Decidindo o caminho para receitas compostas e misturas culinárias complexas com salvaguarda total de privacidade.',
      roadmap_card8_time: 'meta: 25 set 2026',
      roadmap_card8_title: 'Planos e Assinatura',
      roadmap_card8_desc: 'Nível gratuito funcional para decisões rotineiras no mercado e plano Pro para acompanhamento nutricional contínuo com relatórios de tolerância.',
      roadmap_col3_title: 'Fora de Escopo',
      roadmap_tag_todo: 'Decisão Consciente',
      roadmap_card9_time: 'decisão de segurança',
      roadmap_card9_title: 'Deduzir gramatura por foto',
      roadmap_card9_desc: 'Calculamos macronutrientes a partir do rótulo legal e da porção que você confirma. Não inferimos peso por imagem porque esse método erra centenas de calorias sem avisar. Preferimos perguntar a alucinar.',
      roadmap_card10_time: 'decisão de privacidade',
      roadmap_card10_title: 'Sincronização de saúde na nuvem',
      roadmap_card10_desc: 'Exigiria transmitir dados médicos sensíveis para servidores remotos. Enquanto não houver necessidade explícita autorizada, seu histórico permanece criptografado no próprio dispositivo.',
      roadmap_card11_time: 'etapa futura',
      roadmap_card11_title: 'Óculos inteligentes no lançamento',
      roadmap_card11_desc: 'O app nasceu com compatibilidade com o Meta Wearables Toolkit. No entanto, o smartphone no supermercado é onde 99% das decisões acontecem agora. Wearables virão em fases posteriores.',
      roadmap_cta_badge: 'FEEDBACK CLÍNICO',
      roadmap_cta_title: 'Acompanhe de perto ou participe do teste',
      roadmap_cta_desc: 'Publicamos abertamente o que funcionou e o que quebrou. Se você está em tratamento com GLP-1 e deseja testar as versões antecipadas no seu smartphone, entre na lista fechada.',
      roadmap_cta_btn: 'Garantir vaga no teste',
      roadmap_cta_back: '← Voltar à página principal',
      brand_hero_badge: 'BrandKit Oficial v1.0 · Território Precision Intelligence',
      brand_hero_title: 'Manual de Identidade & Design System',
      brand_hero_lede: 'Todos os elementos visuais, paleta de cores, tipografia e diretrizes oficiais da marca EatControl. Para imprensa, investidores, parceiros e desenvolvedores.',
      brand_drive_title: 'Repositório Completo no Google Drive',
      brand_drive_desc: 'Faça o download do kit vetorial completo (.AI, .EPS, .SVG, .PNG 4K), pranchas de aplicações e decks de pitch.',
      brand_drive_btn: 'Abrir Pasta no Google Drive',
      brand_pos_eyebrow: 'Posicionamento',
      brand_pos_title: 'Território: Precision Intelligence',
      brand_pos_desc: 'O equilíbrio visual do EatControl combina tecnologia cirúrgica com estilo de vida humano e credibilidade clínica.',
      brand_pos_card1_title: 'Precisão Inteligente',
      brand_pos_card1_desc: 'Linhas nítidas, alto contraste, modo escuro profundo (Obsidian #070B10) e toques tecnológicos em Signal Cyan. O software transmite exatidão científica e ausência de ruído.',
      brand_pos_card2_title: 'Estilo de Vida Saudável',
      brand_pos_card2_desc: 'Fotografia real de alimentos naturais e rotina, acolhimento visual e empatia com a pessoa em tratamento com GLP-1. O app não é uma planilha médica fria.',
      brand_pos_card3_title: 'Inteligência Clínica',
      brand_pos_card3_desc: 'Hierarquia de evidência, linguagem médica rigorosa sem rodeios e foco em nutrição baseada em dados auditáveis.',
      brand_logos_eyebrow: 'Ativos Oficiais',
      brand_logos_title: 'Logotipos & Símbolos',
      brand_logos_desc: 'Use os arquivos vetoriais para aplicações digitais e impressas. O símbolo representa foco, momento de escolha e inteligência alimentar.',
      brand_logo_dark_title: 'Logo Horizontal (Fundo Escuro)',
      brand_logo_dark_desc: 'Para fundos Obsidian (#070B10) ou Deep Ocean.',
      brand_logo_light_title: 'Logo Horizontal (Fundo Claro)',
      brand_logo_light_desc: 'Para papéis, fundos claros ou impressos.',
      brand_mark_title: 'Símbolo / Mark Oficial',
      brand_mark_desc: 'Ícone isolado para avatares, favicons e interfaces.',
      brand_appicon_title: 'Ícone de Aplicativo (Android)',
      brand_appicon_desc: 'Ícone oficial para Google Play e dispositivos móveis.',
      brand_btn_svg: 'SVG Vetorial',
      brand_btn_png: 'PNG Alta Res',
      brand_colors_eyebrow: 'Design System',
      brand_colors_title: 'Paleta de Cores Oficial',
      brand_colors_desc: 'Clique em qualquer cartão para copiar o código hexadecimal para a sua área de transferência.',
      brand_color_obsidian: 'Obsidian (Fundo Base)',
      brand_color_ocean: 'Deep Ocean (Superfície)',
      brand_color_cyan: 'Signal Cyan (Acento)',
      brand_color_warmwhite: 'Warm White (Texto)',
      brand_color_positive: 'Positive (Compatível)',
      brand_color_attention: 'Attention (Alerta)',
      brand_type_eyebrow: 'Tipografia',
      brand_type_title: 'Fontes Oficiais',
      brand_type_desc: 'Combinamos a imponência geométrica moderna da Manrope com a neutralidade e legibilidade técnica da Inter.',
      brand_type_manrope_desc: 'Utilizada para títulos principais (H1, H2, H3), números em destaque e logomarca. Transmite robustez, modernidade e foco.',
      brand_type_inter_desc: 'Utilizada para textos corridos, botões, formulários, métricas analíticas e interface do aplicativo Android. Legibilidade perfeita em telas pequenas.',
      brand_rules_eyebrow: 'Regras de Ouro',
      brand_rules_title: 'Como usar a marca EatControl',
      brand_rules_desc: 'Diretrizes inegociáveis para garantir integridade e credibilidade clínica.',
      brand_rules_do_title: 'O que fazer',
      brand_rules_do_1: 'Escrever o nome da empresa como <strong>EatControl</strong> (palavra única, camel case).',
      brand_rules_do_2: 'Aplicar o logotipo com respiro mínimo equivalente à metade da altura do símbolo.',
      brand_rules_do_3: 'Usar o acento Signal Cyan (`#2DD4E7`) para CTAs e elementos de foco interativo.',
      brand_rules_do_4: 'Garantir alto contraste de texto (WCAG AA) contra as superfícies escuras.',
      brand_rules_dont_title: 'O que NÃO fazer',
      brand_rules_dont_1: 'Não adicione o sufixo "AI" na marca de consumo (não usar "EatControl AI" como nome comercial).',
      brand_rules_dont_2: 'Não distorça, gire ou altere as cores vetoriais do símbolo oficial.',
      brand_rules_dont_3: 'Não utilize gradientes roxos ou efeitos genéricos de "IA mágica".',
      brand_rules_dont_4: 'Não utilize o logotipo sobre fundos de baixo contraste ou texturas poluídas.',
      brand_footer_disclaimer: 'Manual de identidade visual e BrandKit oficial v1.0. Território Precision Intelligence. Todos os ativos são propriedade do EatControl.',
      brand_nav_back: '← Site Principal',
      brand_nav_logos: 'Logotipos',
      brand_nav_colors: 'Cores & DS',
      brand_nav_type: 'Tipografia'
    },
    en: {
      nav_tech: 'Technology',
      nav_sim: 'Simulator',
      nav_hierarchy: 'Evidence Hierarchy',
      nav_privacy: 'Privacy',
      nav_faq: 'FAQ',
      nav_roadmap: 'Roadmap',
      nav_cta: 'Early Access',
      hero_badge: 'GLP-1 PROTOCOL · OZEMPIC / MOUNJARO / WEGOVY',
      hero_title: 'The critical food decision happens in the grocery store,<br><span class="hero-gradient">not at the clinic.</span>',
      hero_lede: 'Point your phone camera at a nutrition label, barcode, or restaurant menu. EatControl audits the nutritional composition on-device and warns you if it conflicts with your health protocol.',
      hero_btn_join: 'Join Closed Beta',
      hero_btn_sim: 'Watch Live Simulator',
      hero_trust_off: 'Open Food Facts Database',
      hero_trust_local: 'On-Device Inference',
      hero_trust_hallucination: 'Zero Calorie Hallucination',
      hero_chip_top_title: 'Audited: Open Food Facts',
      hero_chip_top_sub: 'EAN 7891000 · 120ms',
      hero_chip_bot_title: 'GLP-1 Safe · 16g Protein',
      hero_chip_bot_sub: 'Zero Added Sugar',
      sim_eyebrow: 'Real-Time Simulation',
      sim_title: 'Experience the Clinical Decision Cycle',
      sim_muted: 'The engine runs on the cadence: <strong>Look → Ask → Listen → Proceed</strong>. Select a scenario to test contextual intelligence:',
      sim_tab_rotulo: '1. OCR Label Audit',
      sim_tab_barcode: '2. Barcode Lookup',
      sim_tab_prato: '3. Assisted Plate',
      sim_tab_cardapio: '4. Menu Scan',
      sim_tab_voz: '5. Voice Command',
      sim_latency: 'Latency: 140ms',
      sim_play_label: 'Listen',
      sim_pause_label: 'Pause',
      sim_cta: 'Test this flow on your smartphone →',
      sim_mock_nutrititle: 'NUTRITION FACTS',
      sim_mock_lactose: 'Declared Lactose',
      sim_mock_frontal: '0g (Front)',
      sim_mock_milk: 'CONTAINS MILK & DAIRY',
      sim_mock_target: 'TARGET',
      sim_mock_ean: 'EAN-13 DETECTED · 120ms',
      sim_mock_salmon: '● Grilled Salmon [96%]',
      sim_mock_broccoli: '● Steamed Broccoli [94%]',
      sim_mock_portion: 'Portion: Requires Audit',
      sim_mock_menu_head: 'BISTRO · CHEF SPECIALS',
      sim_mock_menu_title: 'Filet Mignon Risotto in Cheese Sauce',
      sim_mock_menu_alert: 'Alert: Gorgonzola & Heavy Cream',
      sim_mock_voice_hud: 'WAITER VOICE DETECTED',
      cta_pill: 'Cohort 01 · Limited Spots',
      cta_title: 'Join the EatControl Closed Beta',
      cta_desc: 'We are onboarding initial GLP-1 patients to test the Android app and help calibrate food safety audit engines.',
      cta_label_name: 'Your full name',
      cta_label_email: 'Your primary email',
      cta_label_challenge: 'What is your biggest dietary challenge on medication? (optional)',
      cta_consent: '<strong>Research Opt-in (Optional):</strong> I agree to share anonymized scanned labels to calibrate EatControl nutrition intelligence models.',
      cta_submit: 'Request Beta Access',
      cta_whatsapp: 'Chat on WhatsApp',
      cta_trust_seal: 'Strict privacy (LGPD / GDPR) · Zero spam · 100% free closed beta access',
      bento_eyebrow: 'Perception Architecture',
      bento_title: 'Five Pathways. A Single Verifiable Answer.',
      bento_muted: 'The application autonomously picks the highest-fidelity computation pipeline for the exact context in front of you:',
      bento_1_badge: 'On-Device OCR',
      bento_1_title: 'Label OCR & Ingredient Audit',
      bento_1_desc: 'When barcodes are absent, the on-device camera reads the official nutrition panel and statutory allergen declarations. Regulatory alerts strictly override front-package marketing claims.',
      bento_1_ui_head: 'NUTRITION FACTS (OCR AUDIT)',
      bento_1_ui_status: 'AUDITED',
      bento_1_ui_row1_label: 'Total Protein',
      bento_1_ui_row1_val: '16g per serving',
      bento_1_ui_row2_label: 'Added Sugar',
      bento_1_ui_row2_val: '0g declared',
      bento_2_badge: 'EAN-13 · 120ms',
      bento_2_title: 'Official Barcode Verification',
      bento_2_desc: 'Decodes EAN-13 in milliseconds and cross-references verified public databases like Open Food Facts. Manufacturer-declared data with zero manual typing.',
      bento_3_badge: 'Risk Screening',
      bento_3_title: 'Contextual Restaurant Menu',
      bento_3_desc: 'Screens dishes and sauces in dining environments. Proactively warns about silent reflux triggers, high lipid density, and delayed gastric emptying.',
      bento_3_alert_pill: 'Gastric Delay Alert',
      bento_3_alert_desc: 'High heavy-cream sauces can trigger delayed nausea and reflux under GLP-1.',
      bento_4_badge: 'Honest Computer Vision',
      bento_4_title: 'Honest Plate Vision Without Hallucination',
      bento_4_desc: 'Identifies visual meal components without inventing fictional calorie numbers. When portion size or density is uncertain, EatControl asks for explicit confirmation.',
      bento_4_tag1: 'Lean Protein (Salmon)',
      bento_4_tag2: 'Fibrous Veggie (Broccoli)',
      bento_4_tag3: 'Complex Carb (Quinoa)',
      bento_4_tag4: 'Portion Weight Confirmation Required',
      bento_5_badge: 'On-Device Voice · Spoken Output < 1s',
      bento_5_title: 'Listen to the Waiter or Ask by Voice',
      bento_5_desc: 'At restaurants, buffets, or dinner parties, you don’t need to type. EatControl listens to the ingredients spoken by the waiter (or your question), extracts allergens and lipid loads on-device, and speaks the verdict directly to your earbuds.',
      bento_5_waiter_head: 'WAITER (DISH DESCRIPTION)',
      bento_5_waiter_quote: '"This sauce is prepared with brown butter, gorgonzola cheese, and fresh heavy cream."',
      bento_5_audio_head: 'AUDIO RESPONSE (TTS 820ms)',
      bento_5_verdict: 'INCOMPATIBLE',
      bento_5_audio_quote: '"Warning: very high fat and heavy cream concentration. Severe risk of delayed nausea and reflux under GLP-1 medication."',
      evid_eyebrow: 'Core Engineering Principle',
      evid_title: 'Reliability-First: The Clinical Evidence Hierarchy',
      evid_muted: 'While conventional apps rely on LLMs guessing calories from pictures, EatControl operates under a strict mathematical hierarchy of clinical precedence:',
      evid_tier1_title: 'Declared Official Label (DECLARED_LABEL)',
      evid_tier1_desc: 'Official nutrition facts panel and statutory ingredients verified via high-precision OCR.',
      evid_tier1_badge: 'Absolute Precedence',
      evid_tier2_title: 'Auditable Public Catalog (BARCODE_CATALOG)',
      evid_tier2_desc: 'Open Food Facts records and audited public nutrition databases (USDA / TBCA).',
      evid_tier2_badge: 'High Confidence',
      evid_tier3_title: 'Explicit User Confirmation (USER_CONFIRMATION)',
      evid_tier3_desc: 'Spoken reply or single tap answering the app inquiry regarding consumed quantity.',
      evid_tier3_badge: 'Auditable',
      evid_tier4_title: 'Raw Text OCR (RAW_OCR)',
      evid_tier4_desc: 'Screening dish items on restaurant menus lacking statutory macro tables.',
      evid_tier4_badge: 'Contextual',
      evid_tier5_title: 'Visual Plate Inference (VISUAL_INFERENCE)',
      evid_tier5_desc: 'Assisted food category classification. Never generates numerical macros without confirmation.',
      evid_tier5_badge: 'Declared Uncertainty',
      evid_showcase_chip: 'Zero Calorie Hallucination from Photos',
      priv_eyebrow: 'Sovereign Privacy',
      priv_title: 'Your health data belongs to you, not the cloud.',
      priv_muted: 'Engineered under local-first principles and strict data privacy compliance, performing AI inference on-device without cloud lock-in:',
      priv_card1_title: 'Photos Discarded in RAM',
      priv_card1_desc: 'Photos captured for label or meal audits are processed in phone RAM and immediately discarded. No photo is stored on servers.',
      priv_card2_title: 'Locally Encrypted History',
      priv_card2_desc: 'Your dietary preferences, protocol rules, and logged symptoms are stored strictly in your phone local database.',
      priv_card3_title: 'Wipe History with 1 Click',
      priv_card3_desc: 'You can purge all history, allergen rules, and logged events at any time directly in the app settings.',
      faq_eyebrow: 'Frequently Asked Questions',
      faq_title: 'Frequently asked questions about EatControl',
      faq_muted: 'Understand how the copilot supports safe, deterministic food choices without hallucinations.',
      faq_q1: 'What is EatControl and how does it work?',
      faq_a1: '<strong>EatControl</strong> is a contextual food intelligence copilot. You point your smartphone camera at a barcode, nutrition label, or restaurant menu and receive, in under 1 second, objective guidance on that food compatibility with your goals and health plan — with clinical screening calibrated for GLP-1 patients.',
      faq_q2: 'Does EatControl estimate calories by taking photos of plates?',
      faq_a2: '<strong>No.</strong> Traditional apps attempt to guess weight and calories from photos, missing by hundreds of calories without declaring uncertainty. EatControl adopts the <em>Reliability-First</em> principle: relying primarily on manufacturer-declared labels or audited public databases (Open Food Facts). When a photo lacks declared portions, the app declares <strong>INSUFFICIENT</strong> and asks for quantity before offering an assessment.',
      faq_q3: 'How does the app support users on Ozempic, Mounjaro, or Wegovy?',
      faq_a3: 'GLP-1 receptor agonists reduce appetite and significantly delay gastric emptying. Accidental intake of high saturated fat, ultra-processed items, or hidden sugars can trigger severe nausea, reflux, or constipation. EatControl audits food composition against GLP-1 gastric safety rules to prevent daily adverse episodes.',
      faq_q4: 'How is data privacy and photo security guaranteed?',
      faq_a4: '<strong>Local-first</strong> architecture and privacy by design (LGPD / GDPR). Camera frames are analyzed in volatile RAM and immediately discarded. Your scan history, symptoms, and dietary preferences remain exclusively on your device, never transmitted to remote servers.',
      faq_q5: 'Do I need smart glasses to use EatControl?',
      faq_a5: '<strong>No.</strong> EatControl is a 100% standalone Android app for your smartphone. It uses the native camera, local OCR, and on-device voice assistant. Integration with smart glasses is an optional complementary hands-free interface for future convenience.',
      faq_q6: 'How does voice understanding work when a waiter describes a dish?',
      faq_a6: 'EatControl features 100% on-device speech-to-text (STT) and voice synthesis (TTS). When you activate listening mode at a restaurant, the app captures the waiter culinary explanation or your spoken question, extracts key ingredients (lactose, heavy creams, irritating spices), and responds discreetly in your earbuds in under 1 second.',
      footer_disclaimer: 'Contextual and educational guidance. The application does not formulate clinical diagnoses, does not alter dosages, and does not replace the guidance of an endocrinologist or registered dietitian.',
      footer_roadmap: 'Open Roadmap',
      footer_brand: 'Official BrandKit',
      footer_privacy: 'Privacy',
      footer_terms: 'Terms of Use',
      footer_rights: '© 2026 EatControl AI. All rights reserved.',
      footer_clinical: 'Engineered with clinical rigor in Brazil.',
      roadmap_hero_badge: 'BUILDING IN PUBLIC · RADICAL TRANSPARENCY',
      roadmap_hero_title: 'Public Roadmap',
      roadmap_hero_lede: 'What is ready in the app, what we are building right now, and what we intentionally left out. Including what failed — that is where we learn the most.',
      roadmap_hero_faint: 'Last technical audit: September 7, 2026.',
      roadmap_col1_title: 'Ready',
      roadmap_tag_done: 'Completed',
      roadmap_card1_title: 'Four Analysis Pathways',
      roadmap_card1_desc: 'Barcode, OCR label, restaurant menu, and assisted plate — all running on-device with autonomous routing along the fastest and safest pathway.',
      roadmap_card2_title: 'Decision Engine with Evidence Hierarchy',
      roadmap_card2_desc: 'Four possible states, including explicit declaration of uncertainty. Manufacturer statutory labels override probabilistic AI inference.',
      roadmap_card3_title: 'Public Product Database (Open Food Facts)',
      roadmap_card3_desc: 'Catalog connected to the largest open food database: scanning a real barcode retrieves ingredients, declared allergens, and nutrition facts per 100g.',
      roadmap_card4_title: 'Clinical GLP-1 Layer Integrated',
      roadmap_card4_desc: 'Parametrized clinical rules for Anvisa RDC 727 statutory labeling and immediate gastric delay screening for Ozempic, Mounjaro, and Wegovy users.',
      roadmap_card5_title: 'Personal Rules & Symptom Logging',
      roadmap_card5_desc: 'Configuration of individual intolerances and triggers. When a medical red flag is logged, the app pauses food screening and advises seeking medical attention.',
      roadmap_col2_title: 'Building',
      roadmap_tag_doing: 'In Progress',
      roadmap_card6_time: 'target: Sep 20, 2026',
      roadmap_card6_title: 'Google Play Store Release',
      roadmap_card6_desc: 'Release signing, store listing, and closed testing track distribution. Transitioning from engineering project to installable product at scale.',
      roadmap_card7_time: 'clinical review',
      roadmap_card7_title: 'Assisted Plate Recognition',
      roadmap_card7_desc: 'Currently recognizes broad food categories (salad, poultry, greens). Formulating clinical safeguards for compound recipes and complex dishes.',
      roadmap_card8_time: 'target: Sep 25, 2026',
      roadmap_card8_title: 'Plans & Subscriptions',
      roadmap_card8_desc: 'Fully functional free tier for grocery store decisions and Pro tier for longitudinal tolerance reporting with healthcare providers.',
      roadmap_col3_title: 'Out of Scope',
      roadmap_tag_todo: 'Conscious Decision',
      roadmap_card9_time: 'safety decision',
      roadmap_card9_title: 'Guessing Portion Grams by Photo',
      roadmap_card9_desc: 'We compute macros from statutory labels and confirmed portions. We never guess weight from photos because visual volumetric estimation errs by hundreds of calories without declaring uncertainty.',
      roadmap_card10_time: 'privacy decision',
      roadmap_card10_title: 'Cloud Health Synchronization',
      roadmap_card10_desc: 'Would require transmitting sensitive health metrics to remote servers. Your scan history and symptom logs remain encrypted locally on your device.',
      roadmap_card11_time: 'future phase',
      roadmap_card11_title: 'Smart Glasses at Launch',
      roadmap_card11_desc: 'Engineered with Meta Wearables Toolkit compatibility, but the smartphone in the supermarket is where 99% of daily food decisions happen today.',
      roadmap_cta_badge: 'CLINICAL FEEDBACK',
      roadmap_cta_title: 'Follow Progress or Join the Beta',
      roadmap_cta_desc: 'We transparently share what worked and what broke. If you are on GLP-1 medication and want to test early builds on your phone, join our closed testing cohort.',
      roadmap_cta_btn: 'Join Closed Beta',
      roadmap_cta_back: '← Back to Homepage',
      brand_hero_badge: 'Official BrandKit v1.0 · Precision Intelligence Territory',
      brand_hero_title: 'Brand Identity & Design System',
      brand_hero_lede: 'Visual identity elements, color palette, typography, and official guidelines for EatControl. For press, investors, partners, and developers.',
      brand_drive_title: 'Complete Google Drive Repository',
      brand_drive_desc: 'Download the full vector kit (.AI, .EPS, .SVG, 4K PNG), brand application sheets, and pitch decks.',
      brand_drive_btn: 'Open Google Drive Folder',
      brand_pos_eyebrow: 'Positioning',
      brand_pos_title: 'Territory: Precision Intelligence',
      brand_pos_desc: 'EatControl visual balance unites surgical technology with human lifestyle and clinical credibility.',
      brand_pos_card1_title: 'Precision Intelligence',
      brand_pos_card1_desc: 'Crisp lines, high contrast, deep Obsidian dark mode (#070B10), and Signal Cyan tech accents. Transmits scientific precision and zero noise.',
      brand_pos_card2_title: 'Healthy Lifestyle',
      brand_pos_card2_desc: 'Authentic food photography, visual warmth, and empathy for GLP-1 patients. The app is never a cold medical spreadsheet.',
      brand_pos_card3_title: 'Clinical Intelligence',
      brand_pos_card3_desc: 'Evidence hierarchy, rigorous plain-spoken clinical language, and auditable data-driven nutrition.',
      brand_logos_eyebrow: 'Official Assets',
      brand_logos_title: 'Logos & Marks',
      brand_logos_desc: 'Use vector assets for digital and print media. The symbol embodies focus, the decision moment, and food intelligence.',
      brand_logo_dark_title: 'Horizontal Logo (Dark Background)',
      brand_logo_dark_desc: 'For Obsidian (#070B10) or Deep Ocean surfaces.',
      brand_logo_light_title: 'Horizontal Logo (Light Background)',
      brand_logo_light_desc: 'For print, light backgrounds, or white paper.',
      brand_mark_title: 'Official Mark / Symbol',
      brand_mark_desc: 'Standalone mark for avatars, favicons, and UI.',
      brand_appicon_title: 'Android App Icon',
      brand_appicon_desc: 'Official icon for Google Play and mobile home screens.',
      brand_btn_svg: 'Vector SVG',
      brand_btn_png: 'High-Res PNG',
      brand_colors_eyebrow: 'Design System',
      brand_colors_title: 'Official Color Palette',
      brand_colors_desc: 'Click any card to copy the hex code to your clipboard.',
      brand_color_obsidian: 'Obsidian (Base Background)',
      brand_color_ocean: 'Deep Ocean (Surface)',
      brand_color_cyan: 'Signal Cyan (Accent)',
      brand_color_warmwhite: 'Warm White (Text)',
      brand_color_positive: 'Positive (Compatible)',
      brand_color_attention: 'Attention (Alert)',
      brand_type_eyebrow: 'Typography',
      brand_type_title: 'Official Typography',
      brand_type_desc: 'Combining the modern geometric presence of Manrope with the neutral clarity of Inter.',
      brand_type_manrope_desc: 'Used for display headlines (H1, H2, H3), key metrics, and the logotype. Conveys modern focus and robustness.',
      brand_type_inter_desc: 'Used for body copy, UI buttons, forms, and mobile app screens. Optimized for readability on handheld devices.',
      brand_rules_eyebrow: 'Golden Rules',
      brand_rules_title: 'Brand Guidelines & Usage',
      brand_rules_desc: 'Non-negotiable guidelines preserving clinical authority and visual integrity.',
      brand_rules_do_title: 'Do\'s',
      brand_rules_do_1: 'Spell the company name as <strong>EatControl</strong> (single word, camel case).',
      brand_rules_do_2: 'Provide clear space around the logo equal to at least half the mark height.',
      brand_rules_do_3: 'Use Signal Cyan (`#2DD4E7`) exclusively for primary CTAs and interactive highlights.',
      brand_rules_do_4: 'Ensure high contrast ratios (WCAG AA compliant) against dark background surfaces.',
      brand_rules_dont_title: 'Don\'ts',
      brand_rules_dont_1: 'Do not append the "AI" suffix to the consumer brand (do not use "EatControl AI" as the trade name).',
      brand_rules_dont_2: 'Do not distort, rotate, or alter vector color layers of the official mark.',
      brand_rules_dont_3: 'Do not use purple gradients or generic "magical AI" sparkle tropes.',
      brand_rules_dont_4: 'Do not place the logo over busy textures or low-contrast backgrounds.',
      brand_footer_disclaimer: 'Official brand guidelines and BrandKit v1.0. Precision Intelligence territory. All assets property of EatControl.',
      brand_nav_back: '← Main Website',
      brand_nav_logos: 'Logos',
      brand_nav_colors: 'Colors & DS',
      brand_nav_type: 'Typography'
    }
  };

  // ---------------------------------------------------- Cenários do Simulador (PT / EN)

  var SIM_SCENARIOS = {
    pt: {
      rotulo: {
        title: 'Leitura de Rótulo por OCR',
        desc: 'O motor de percepção cruza a lista legal de ingredientes contra a sua lista de intolerâncias e diretrizes de GLP-1. Promessas de marketing na frente da embalagem nunca anulam o verso obrigatório.',
        item: 'Iogurte Grego "Zero Lactose"',
        verdictClass: 'tag-incompativel',
        verdictText: 'INCOMPATÍVEL',
        auditTag: 'ANVISA RDC 727',
        evidenceRank: 'RANK 2 · DECLARED_LABEL',
        evidenceText: 'Texto frontal declara "Zero Lactose", mas a lista legal de ingredientes no verso declara expressamente "CONTÉM LEITE E DERIVADOS". Em caso de alergia à proteína do leite (APLV), prevalece o ingrediente declarado.',
        audioText: 'Atenção: embora anunciado como zero lactose, o produto contém proteína do leite na lista de ingredientes.',
        checklist: [
          'Confronto de alérgenos obrigatórios (RDC 727/2022)',
          'Detecção de açúcares ocultos e polióis irritantes',
          'Execução local no processador do smartphone'
        ]
      },
      barcode: {
        title: 'Código de Barras em Base Pública',
        desc: 'O EAN é consultado no banco de dados aberto do Open Food Facts. Informação declarada pelo fabricante sem digitação manual, com composição nutricional oficial.',
        item: 'Barra de Proteína Crisp 16g',
        verdictClass: 'tag-compativel',
        verdictText: 'COMPATÍVEL',
        auditTag: 'OPEN FOOD FACTS EAN-13',
        evidenceRank: 'RANK 3 · BARCODE_DATABASE',
        evidenceText: 'O código de barras resolve no Open Food Facts e traz alérgenos declarados, fibras (6g) e teor protéico adequado para preservar massa magra em terapia com GLP-1.',
        audioText: 'Produto compatível com seu protocolo. 16 gramas de proteína e teor de gordura adequado para sua digestão.',
        checklist: [
          'Consulta instantânea em banco auditável (Open Food Facts)',
          'Preservação de massa magra calculada por porção',
          'Alerta imediato de polióis fermentáveis (FODMAPs)'
        ]
      },
      prato: {
        title: 'Prato Assistido (Visão Honesta)',
        desc: 'Reconhece componentes visuais sem inventar gramaturas milagrosas. O app declara incerteza e pede sua confirmação para registrar macros com fidelidade clínica.',
        item: 'Filé de Salmão com Brócolis e Quinoa',
        verdictClass: 'tag-atencao',
        verdictText: 'ATENÇÃO · CONFIRMAR',
        auditTag: 'COMPUTER VISION',
        evidenceRank: 'RANK 6: VISÃO → RANK 4: AUDITORIA',
        evidenceText: 'Componentes identificados: Salmão (~140g) e vegetais fibrosos. O aplicativo não inventa gramatura por foto: confirme a porção para computar a ingestão real.',
        audioText: 'Identifiquei salmão grelhado e brócolis. Qual foi o tamanho aproximado da porção consumida?',
        checklist: [
          'Segmentação de classes alimentares sem alucinação de calorias',
          'Declaração explícita de incerteza volumétrica',
          'Confirmação em 1 toque para calibração de dados clínicos'
        ]
      },
      cardapio: {
        title: 'Cardápio Contextual de Restaurante',
        desc: 'Triagem de pratos e molhos em cardápios impressos ou digitais. Alerta sobre potenciais gatilhos de refluxo, gorduras saturadas e retardo gástrico antes do pedido.',
        item: 'Prato Executivo: Risoto de Queijo com Mignon',
        verdictClass: 'tag-atencao',
        verdictText: 'ATENÇÃO · LIPÍDIOS',
        auditTag: 'GLP-1 GASTRIC ALERT',
        evidenceRank: 'RANK 5 · OCR CONTEXTUAL',
        evidenceText: 'Cardápio lido por OCR contextual. O risoto tradicional contém alta concentração de manteiga e creme de leite, forte gatilho para retardo no esvaziamento gástrico e náusea.',
        audioText: 'O risoto tem teor elevado de gordura e nata que pode provocar desconforto gástrico com medicação GLP-1. Peça opção grelhada.',
        checklist: [
          'Varredura óptica de nomes e descrições de pratos',
          'Detecção de gatilhos de refluxo e esvaziamento lento',
          'Sugestão de substituição antes de fazer o pedido'
        ]
      },
      voz: {
        title: 'Comando por Voz & Diálogo com Garçom',
        desc: 'No restaurante, buffet ou cozinha, fale com o app ou deixe-o escutar a descrição do garçom. O EatControl transcreve o áudio on-device em menos de 1s, isola gatilhos de intolerância e responde privativamente no fone ou viva-voz.',
        item: 'Descrição Falada pelo Garçom',
        verdictClass: 'tag-incompativel',
        verdictText: 'INCOMPATÍVEL · NATA',
        auditTag: 'WHISPER ON-DEVICE',
        evidenceRank: 'RANK 1 · VOZ DO GARÇOM',
        evidenceText: 'Áudio captado e transcrito localmente: "molho com redução de nata fresca e queijo gorgonzola". O algoritmo identificou sobrecarga de lipídios e derivados lácteos de alto risco para refluxo sob GLP-1.',
        audioText: 'Atenção: o garçom descreveu nata e queijo no molho. Risco severo de refluxo e intolerância. Sugiro pedir o molho à parte.',
        checklist: [
          'Transcrição local rápida com Whisper On-Device',
          'Extração instantânea de alérgenos e densidade lipídica',
          'Feedback sonoro privado diretamente nos fones ou óculos'
        ]
      }
    },
    en: {
      rotulo: {
        title: 'Label OCR & Ingredient Audit',
        desc: 'The on-device engine audits the official ingredients list against your food intolerances and GLP-1 guidelines. Front-package marketing claims never override required back-panel disclosures.',
        item: 'Greek Yogurt "Zero Lactose"',
        verdictClass: 'tag-incompativel',
        verdictText: 'INCOMPATIBLE',
        auditTag: 'REGULATORY COMPLIANCE',
        evidenceRank: 'RANK 2 · DECLARED_LABEL',
        evidenceText: 'Front claims "Zero Lactose", but statutory ingredient list explicitly declares "CONTAINS MILK PROTEIN". In case of cow milk protein allergy, declared ingredients prevail.',
        audioText: 'Warning: although marketed as zero lactose, this item contains milk protein in its ingredient list.',
        checklist: [
          'Cross-examination of statutory allergen declarations',
          'Detection of hidden sugars and irritating polyols',
          'Private execution entirely on your device processor'
        ]
      },
      barcode: {
        title: 'Official Barcode Lookup',
        desc: 'Scans official EAN barcodes in milliseconds and fetches verified records from Open Food Facts. Manufacturer-declared nutrition without manual data entry.',
        item: 'Crisp Protein Bar 16g',
        verdictClass: 'tag-compativel',
        verdictText: 'COMPATIBLE',
        auditTag: 'OPEN FOOD FACTS EAN-13',
        evidenceRank: 'RANK 3 · BARCODE_DATABASE',
        evidenceText: 'Verified barcode resolves in Open Food Facts with certified allergens, 6g fiber, and optimal protein ratio to preserve lean mass during GLP-1 therapy.',
        audioText: 'Product compatible with your protocol. 16 grams of protein and optimal fat ratio for easy digestion.',
        checklist: [
          'Instant query on public open food repository',
          'Lean muscle mass preservation tracking',
          'Proactive warning for fermentable polyols (FODMAPs)'
        ]
      },
      prato: {
        title: 'Honest Computer Vision Plate Audit',
        desc: 'Identifies visual meal components without hallucinating fictional calorie numbers. When portion size or density is uncertain, EatControl asks for confirmation.',
        item: 'Salmon Fillet with Broccoli & Quinoa',
        verdictClass: 'tag-atencao',
        verdictText: 'CAUTION · CONFIRM',
        auditTag: 'COMPUTER VISION',
        evidenceRank: 'RANK 6: VISION → RANK 4: AUDIT',
        evidenceText: 'Components detected: Grilled salmon (~140g) and fibrous greens. The app never invents grams from photos: confirm your portion for clinically accurate macros.',
        audioText: 'I identified grilled salmon and broccoli. What was the approximate portion size?',
        checklist: [
          'Visual component segmentation without hallucinated calories',
          'Explicit declaration of volumetric uncertainty',
          'Single-tap user confirmation for clinical fidelity'
        ]
      },
      cardapio: {
        title: 'Contextual Restaurant Menu Scan',
        desc: 'Screens dishes and sauces on printed or digital menus. Warns against silent gastric delay triggers, saturated fats, and high-lipid sauces before ordering.',
        item: 'Executive Dish: Cheese Risotto with Filet',
        verdictClass: 'tag-atencao',
        verdictText: 'CAUTION · LIPIDS',
        auditTag: 'GLP-1 GASTRIC ALERT',
        evidenceRank: 'RANK 5 · CONTEXTUAL OCR',
        evidenceText: 'Menu scanned via contextual OCR. Traditional risotto contains heavy butter and cream reductions—a known trigger for delayed gastric emptying and acute nausea under GLP-1.',
        audioText: 'This risotto has high cream and butter content that can trigger gastric discomfort under GLP-1. Consider asking for a grilled alternative.',
        checklist: [
          'Optical scan of culinary names and preparation notes',
          'Identification of gastric delay and nausea triggers',
          'Actionable alternative suggestion before ordering'
        ]
      },
      voz: {
        title: 'Voice Command & Waiter Dialogue',
        desc: 'At restaurants or dining out, simply speak or listen to the waiter describing the dish. EatControl transcribes on-device in under 1 second, isolates trigger ingredients, and delivers private spoken feedback.',
        item: 'Spoken Waiter Dish Description',
        verdictClass: 'tag-incompativel',
        verdictText: 'INCOMPATIBLE · DAIRY',
        auditTag: 'WHISPER ON-DEVICE',
        evidenceRank: 'RANK 1 · WAITER SPOKEN AUDIO',
        evidenceText: 'Spoken audio transcribed on-device: "sauce with heavy cream reduction and gorgonzola". Algorithmic pipeline detected high lipid load and dairy derivatives that trigger severe gastric delay under GLP-1.',
        audioText: 'Warning: the waiter described heavy cream and blue cheese in the sauce. High risk of nausea under GLP-1. Ask for sauce on the side.',
        checklist: [
          'Fast on-device transcription with Whisper model',
          'Instant extraction of allergens and lipid density',
          'Discreet audio feedback directly into your earbuds or glasses'
        ]
      }
    }
  };

  // ---------------------------------------------------- Setup do Simulador Interativo

  function setupSimulator() {
    var tabs = document.querySelectorAll('.sim-btn');
    if (!tabs.length) return;

    var itemName = document.getElementById('sim-item-name');
    var verdict = document.getElementById('sim-verdict-badge');
    var auditTag = document.getElementById('sim-audit-tag');
    var evidenceRank = document.getElementById('sim-evidence-rank');
    var evidenceText = document.getElementById('sim-evidence-text');
    var audioQuote = document.getElementById('sim-audio-quote');
    var trackTitle = document.getElementById('sim-track-title');
    var trackDesc = document.getElementById('sim-track-desc');
    var decisionCard = document.querySelector('.phone-decision-card');
    var playBtn = document.getElementById('sim-audio-play');
    var playLabel = document.getElementById('sim-play-label');
    var audioWave = document.getElementById('sim-audio-wave');
    var checklistEl = document.querySelector('.console-checklist');

    function stopCurrentSpeech() {
      if (window.speechSynthesis) {
        window.speechSynthesis.cancel();
      }
      isAudioPlaying = false;
      if (playBtn) playBtn.classList.remove('is-playing');
      if (playLabel) playLabel.textContent = currentLang === 'pt' ? 'Ouvir' : 'Listen';
      if (audioWave) audioWave.classList.remove('active');
    }

    function loadScenario(key) {
      currentScenarioKey = key;
      var langData = SIM_SCENARIOS[currentLang] || SIM_SCENARIOS.pt;
      var data = langData[key] || langData.rotulo;

      stopCurrentSpeech();

      // Switch active scene in viewfinder
      var scenes = document.querySelectorAll('.viewfinder-scene');
      scenes.forEach(function (s) { s.classList.remove('active'); });
      var activeScene = document.getElementById('scene-' + key);
      if (activeScene) activeScene.classList.add('active');

      if (window.gsap && decisionCard && !reduced) {
        window.gsap.fromTo(decisionCard, { opacity: 0.5, y: 6 }, { opacity: 1, y: 0, duration: 0.3, ease: 'power2.out' });
      }

      if (itemName) itemName.textContent = data.item;
      if (trackTitle) trackTitle.textContent = data.title;
      if (trackDesc) trackDesc.textContent = data.desc;
      if (auditTag) auditTag.textContent = data.auditTag;
      if (verdict) {
        verdict.className = 'verdict-tag ' + data.verdictClass;
        verdict.textContent = data.verdictText;
      }
      if (evidenceRank) evidenceRank.textContent = data.evidenceRank;
      if (evidenceText) evidenceText.textContent = data.evidenceText;
      if (audioQuote) audioQuote.textContent = '"' + data.audioText + '"';

      // Update checklist if elements exist
      if (checklistEl && data.checklist) {
        checklistEl.innerHTML = data.checklist.map(function (item) {
          return '<div class="check-row">' +
            '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#2dd4e7" stroke-width="2.5"><path d="M20 6L9 17l-5-5"/></svg>' +
            '<span>' + item + '</span>' +
          '</div>';
        }).join('');
      }
    }

    // Tab buttons listener
    tabs.forEach(function (tab) {
      tab.addEventListener('click', function () {
        tabs.forEach(function (t) { t.classList.remove('is-active'); });
        tab.classList.add('is-active');
        loadScenario(tab.getAttribute('data-tab'));
      });
    });

    // Voice playback button
    if (playBtn) {
      playBtn.addEventListener('click', function (e) {
        e.preventDefault();

        if (isAudioPlaying) {
          stopCurrentSpeech();
          return;
        }

        var langData = SIM_SCENARIOS[currentLang] || SIM_SCENARIOS.pt;
        var data = langData[currentScenarioKey] || langData.rotulo;
        var textToSpeak = data.audioText;

        if ('speechSynthesis' in window) {
          window.speechSynthesis.cancel();
          currentUtterance = new SpeechSynthesisUtterance(textToSpeak);
          currentUtterance.lang = currentLang === 'pt' ? 'pt-BR' : 'en-US';
          currentUtterance.rate = 1.02;

          currentUtterance.onstart = function () {
            isAudioPlaying = true;
            playBtn.classList.add('is-playing');
            if (playLabel) playLabel.textContent = currentLang === 'pt' ? 'Pausar' : 'Pause';
            if (audioWave) audioWave.classList.add('active');
          };

          currentUtterance.onend = function () {
            stopCurrentSpeech();
          };

          currentUtterance.onerror = function () {
            stopCurrentSpeech();
          };

          window.speechSynthesis.speak(currentUtterance);
        } else {
          // Visual fallback if TTS unsupported
          isAudioPlaying = true;
          playBtn.classList.add('is-playing');
          if (audioWave) audioWave.classList.add('active');
          setTimeout(function () {
            stopCurrentSpeech();
          }, 4000);
        }
      });
    }

    // Expose loadScenario to language switcher
    window.eatcontrol_loadScenario = loadScenario;
    loadScenario('rotulo');
  }

  // ---------------------------------------------------- Seletor de Idioma (PT | EN)

  function setupLanguageSwitch() {
    var langButtons = document.querySelectorAll('.lang-btn');
    if (!langButtons.length) return;

    var savedLang = localStorage.getItem('ec_lang') || 'pt';
    setLanguage(savedLang);

    langButtons.forEach(function (btn) {
      btn.addEventListener('click', function () {
        var lang = btn.getAttribute('data-lang');
        if (lang && lang !== currentLang) {
          setLanguage(lang);
        }
      });
    });

    function setLanguage(lang) {
      currentLang = lang;
      try {
        localStorage.setItem('ec_lang', lang);
      } catch (err) {}

      document.documentElement.lang = lang === 'pt' ? 'pt-BR' : 'en';

      // Update button visual states
      langButtons.forEach(function (b) {
        var isCurrent = b.getAttribute('data-lang') === lang;
        b.classList.toggle('is-active', isCurrent);
        b.setAttribute('aria-pressed', isCurrent ? 'true' : 'false');
      });

      var dict = I18N_DICT[lang] || I18N_DICT.pt;

      // Update static data-i18n elements
      document.querySelectorAll('[data-i18n]').forEach(function (el) {
        var key = el.getAttribute('data-i18n');
        if (dict[key]) {
          el.innerHTML = dict[key];
        }
      });

      // Update simulator tabs text
      document.querySelectorAll('[data-i18n-tab]').forEach(function (el) {
        var tabKey = el.getAttribute('data-i18n-tab');
        var span = el.querySelector('span');
        if (span && dict['sim_tab_' + tabKey]) {
          span.textContent = dict['sim_tab_' + tabKey];
        }
      });

      // Swap hero device mockup image based on selected language
      var heroImg = document.getElementById('hero-device-img');
      if (heroImg) {
        heroImg.src = lang === 'en' 
          ? '/assets/app_scanner_mockup_en.jpg?v=3.4' 
          : '/assets/app_scanner_mockup.jpg?v=3.4';
        heroImg.alt = lang === 'en'
          ? 'EatControl Camera Scanner in action on smartphone'
          : 'EatControl Camera Scanner em funcionamento no smartphone';
      }

      // Reload active simulator scenario in selected language
      if (window.eatcontrol_loadScenario) {
        window.eatcontrol_loadScenario(currentScenarioKey);
      }
    }
  }

  // ------------------------------------------------- Cópia de Swatches de Cor (Marca)

  function setupColorSwatches() {
    var swatches = document.querySelectorAll('[data-hex]');
    var toast = document.getElementById('copy-toast');

    function showToast(text) {
      if (!toast) {
        toast = document.createElement('div');
        toast.id = 'copy-toast';
        toast.className = 'toast';
        var container = document.querySelector('.toast-container');
        if (!container) {
          container = document.createElement('div');
          container.className = 'toast-container';
          document.body.appendChild(container);
        }
        container.appendChild(toast);
      }
      toast.textContent = text;
      toast.style.display = 'block';
      setTimeout(function () {
        toast.style.display = 'none';
      }, 2500);
    }

    swatches.forEach(function (swatch) {
      swatch.addEventListener('click', function () {
        var hex = swatch.getAttribute('data-hex');
        if (!hex) return;

        if (navigator.clipboard && navigator.clipboard.writeText) {
          navigator.clipboard.writeText(hex).then(function () {
            showToast('Código copiado: ' + hex);
          }).catch(function () {
            showToast('HEX: ' + hex);
          });
        } else {
          showToast('HEX: ' + hex);
        }
      });
    });
  }

  // -------------------------------------------------------- Formulário de Waitlist

  function setupWaitlistForm() {
    var form = document.querySelector('[data-waitlist]');
    if (!form) return;

    form.addEventListener('submit', function (e) {
      e.preventDefault();

      var nome = form.querySelector('[name="nome"]').value.trim();
      var email = form.querySelector('[name="email"]').value.trim();
      var consent = form.querySelector('[name="consent_training"]') ? form.querySelector('[name="consent_training"]').checked : false;

      if (!nome || !email) return;

      var submitBtn = form.querySelector('button[type="submit"]');
      submitBtn.disabled = true;
      submitBtn.textContent = currentLang === 'pt' ? 'Garantindo vaga...' : 'Securing spot...';

      // Telemetria local
      if (window.eatcontrol_analytics && window.eatcontrol_analytics.trackWaitlistJoin) {
        window.eatcontrol_analytics.trackWaitlistJoin({
          consent_training_data: consent
        });
      }

      // Armazenamento local
      try {
        var leads = JSON.parse(localStorage.getItem('eatcontrol_waitlist') || '[]');
        leads.push({
          nome: nome,
          email: email,
          consent: consent,
          date: new Date().toISOString()
        });
        localStorage.setItem('eatcontrol_waitlist', JSON.stringify(leads));
      } catch (err) {
        console.warn('LocalStorage inacessível:', err);
      }

      setTimeout(function () {
        submitBtn.textContent = currentLang === 'pt' ? 'Vaga Garantida!' : 'Spot Secured!';
        submitBtn.style.background = '#42c98b';
        submitBtn.style.color = '#050b11';

        var toastContainer = document.querySelector('.toast-container');
        if (!toastContainer) {
          toastContainer = document.createElement('div');
          toastContainer.className = 'toast-container';
          document.body.appendChild(toastContainer);
        }
        var toast = document.createElement('div');
        toast.className = 'toast';
        toast.textContent = currentLang === 'pt'
          ? 'Inscrição confirmada! Você receberá o convite para o APK em breve.'
          : 'Registration confirmed! You will receive your APK invite soon.';
        toastContainer.appendChild(toast);

        setTimeout(function () {
          toast.remove();
        }, 4000);

        form.reset();
      }, 600);
    });
  }

  // ------------------------------------------------------------- Inicialização

  document.addEventListener('DOMContentLoaded', function () {
    initAnimations();
    setupSpotlight();
    setupSimulator();
    setupLanguageSwitch();
    setupColorSwatches();
    setupWaitlistForm();
  });

})();
