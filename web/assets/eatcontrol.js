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
      bento_eyebrow: 'Arquitetura de Percepção',
      bento_title: 'Cinco caminhos. Uma única resposta confiável.',
      bento_muted: 'O aplicativo escolhe autonomamente a trilha computacional de maior fidelidade para o contexto à sua frente:',
      bento_1_title: 'Leitura de Rótulo & Ingredientes',
      bento_1_desc: 'Quando não há código de barras, a câmera lê a tabela nutricional oficial e os avisos de alérgenos no próprio aparelho. O motor prioriza avisos regulatórios obrigatórios da Anvisa sobre qualquer marketing de embalagem.',
      bento_2_title: 'Código de barras oficial',
      bento_2_desc: 'Lê o código em milissegundos e busca os dados na base auditável do Open Food Facts e tabelas públicas. Informação declarada sem digitação manual.',
      bento_3_title: 'Cardápio Contextual',
      bento_3_desc: 'Identifica os pratos e ingredientes prováveis em restaurantes. Alerta sobre potenciais gatilhos de refluxo ou sobrecarga lipídica antes do pedido.',
      bento_4_title: 'Prato Assistido sem Alucinação',
      bento_4_desc: 'Reconhece componentes visuais sem inventar gramaturas milagrosas. Se há dúvida sobre a densidade ou porção, o EatControl declara a incerteza e pede sua confirmação.',
      bento_5_title: 'Ouça a descrição do garçom ou pergunte por voz',
      bento_5_desc: 'No restaurante, buffet ou na cozinha, você não precisa ficar digitando. O EatControl escuta a descrição dos ingredientes falada pelo garçom (ou sua própria pergunta), extrai alérgenos e densidade lipídica no próprio smartphone, e devolve o veredito por voz discretamente no fone, óculos ou viva-voz.',
      evid_eyebrow: 'Diferencial Inegociável',
      evid_title: 'Reliability-First: A Hierarquia de Evidência',
      evid_muted: 'Enquanto aplicativos convencionais usam inteligência artificial para chutar calorias a partir de fotos, o EatControl opera sob uma régua matemática de precedência clínica:',
      priv_eyebrow: 'Privacidade Soberana',
      priv_title: 'Dados de saúde pertencem a você, não à nuvem.',
      priv_muted: 'Arquitetado sob conformidade estrita com a LGPD (Lei 13.709/2018, Arts. 7º e 11º) com processamento no aparelho:'
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
      bento_eyebrow: 'Perception Architecture',
      bento_title: 'Five Pathways. A Single Verifiable Answer.',
      bento_muted: 'The application autonomously picks the highest-fidelity computation pipeline for the exact context in front of you:',
      bento_1_title: 'Label OCR & Ingredient Audit',
      bento_1_desc: 'When barcodes are absent, the on-device camera reads the official nutrition panel and statutory allergen declarations. Regulatory alerts strictly override front-package marketing claims.',
      bento_2_title: 'Official Barcode Verification',
      bento_2_desc: 'Decodes EAN-13 in milliseconds and cross-references verified public databases like Open Food Facts. Manufacturer-declared data with zero manual typing.',
      bento_3_title: 'Contextual Restaurant Menu',
      bento_3_desc: 'Screens dishes and sauces in dining environments. Proactively warns about silent reflux triggers, high lipid density, and delayed gastric emptying.',
      bento_4_title: 'Honest Plate Vision Without Hallucination',
      bento_4_desc: 'Identifies visual meal components without inventing fictional calorie numbers. When portion size or density is uncertain, EatControl asks for explicit confirmation.',
      bento_5_title: 'Listen to the Waiter or Ask by Voice',
      bento_5_desc: 'At restaurants, buffets, or dinner parties, you don’t need to type. EatControl listens to the ingredients spoken by the waiter (or your question), extracts allergens and lipid loads on-device, and speaks the verdict directly to your earbuds.',
      evid_eyebrow: 'Core Engineering Principle',
      evid_title: 'Reliability-First: The Clinical Evidence Hierarchy',
      evid_muted: 'While conventional apps rely on LLMs guessing calories from pictures, EatControl operates under a strict mathematical hierarchy of clinical precedence:',
      priv_eyebrow: 'Sovereign Privacy',
      priv_title: 'Your health data belongs to you, not the cloud.',
      priv_muted: 'Engineered under local-first principles and strict data privacy compliance, performing AI inference on-device without cloud lock-in:'
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
