/*
  EatControl — Interatividade, Micro-animações Cinemáticas e Simulador.
  GSAP 3 + ScrollTrigger com fallback para IntersectionObserver.
*/

(function () {
  'use strict';

  var CONTACT = {
    formEndpoint: '',
    whatsapp: '',
    email: 'eatcontrol.ai@gmail.com',
    googleDriveBrandKit: 'https://drive.google.com/drive/folders/1eatcontrol-brandkit-placeholder'
  };

  var reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

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
        stagger: 0.12,
        ease: 'power3.out'
      });

      window.gsap.from('.hero-mockup-wrapper', {
        y: 40,
        opacity: 0,
        scale: 0.94,
        duration: 1.1,
        delay: 0.25,
        ease: 'power3.out'
      });

      // Section headings
      window.gsap.utils.toArray('section:not(.hero)').forEach(function (sec) {
        window.gsap.from(sec.querySelectorAll('h2, .eyebrow, .muted'), {
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
    } else {
      // Fallback nativo simples
      var targets = document.querySelectorAll('[data-reveal]');
      if ('IntersectionObserver' in window) {
        var io = new IntersectionObserver(function (entries) {
          entries.forEach(function (entry) {
            if (entry.isIntersecting) {
              entry.target.style.opacity = '1';
              entry.target.style.transform = 'translateY(0)';
              io.unobserve(entry.target);
            }
          });
        }, { threshold: 0.1 });

        targets.forEach(function (el) {
          el.style.opacity = '0';
          el.style.transform = 'translateY(20px)';
          el.style.transition = 'opacity 0.6s cubic-bezier(0.16,1,0.3,1), transform 0.6s cubic-bezier(0.16,1,0.3,1)';
          io.observe(el);
        });
      }
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

  // ---------------------------------------------------- Simulador Interativo

  var SIM_SCENARIOS = {
    rotulo: {
      title: 'Leitura de Rótulo por OCR',
      item: 'Iogurte Grego "Zero Lactose"',
      verdictClass: 'verdict-incompativel',
      verdictText: 'INCOMPATÍVEL',
      evidenceRank: 'Rank 2 · DECLARED_LABEL',
      evidenceText: 'Texto frontal declara "Zero Lactose", mas a lista de ingredientes no verso declara expressamente "CONTÉM LEITE E DERIVADOS". Em caso de alergia à proteína do leite (APLV), prevalece o ingrediente declarado.',
      audioText: '"Atenção: embora anunciado como zero lactose, o produto contém proteína do leite na lista de ingredientes."'
    },
    barcode: {
      title: 'Código de Barras em Base Pública',
      item: 'Produto lido por código de barras',
      verdictClass: 'verdict-compativel',
      verdictText: 'COMPATÍVEL',
      evidenceRank: 'Rank 3 · BARCODE_DATABASE (Open Food Facts)',
      evidenceText: 'O EAN resolve no Open Food Facts e traz ingredientes, alérgenos declarados e composição por 100 g. Essa é a evidência mais forte que a análise consegue sem você digitar nada, porque vem do que o fabricante declarou.',
      audioText: '"Não encontrei conflito nas evidências disponíveis."'
    },
    prato: {
      title: 'Prato Assistido (Sem Falsas Promessas)',
      item: 'Filé de Frango Grelhado com Salada e Purê',
      verdictClass: 'verdict-atencao',
      verdictText: 'ATENÇÃO — CONFIRMAR QUANTIDADE',
      evidenceRank: 'Rank 6 · VISUAL_INFERENCE, resolvido por Rank 4 · USER_CONFIRMATION',
      evidenceText: 'Componentes identificados: Peito de frango grelhado (~120-150g) e vegetais folhosos. Purê pode conter manteiga ou creme de leite. O app não adivinha peso por foto: confirme a porção para registrar macros exatos.',
      audioText: '"Identifiquei frango e salada. Qual foi a quantidade de purê consumida?"'
    },
    cardapio: {
      title: 'Estruturação de Cardápio / Menu',
      item: 'Prato Executivo: Salmão com Risoto de Limão',
      verdictClass: 'verdict-atencao',
      verdictText: 'ATENÇÃO — GORDURA POTENCIAL',
      evidenceRank: 'Rank 5 · OCR_TEXT',
      evidenceText: 'Cardápio lido por OCR contextual. O salmão é fonte rica em ômega-3, mas o risoto tradicional utiliza queijo e manteiga em alta concentração, potencial gatilho para refluxo em usuários GLP-1.',
      audioText: '"O salmão é uma excelente escolha, mas peça o risoto com pouco queijo para evitar desconfortos gastrointestinais."'
    }
  };

  function setupSimulator() {
    var tabs = document.querySelectorAll('.sim-tab');
    if (!tabs.length) return;

    var itemName = document.getElementById('sim-item-name');
    var verdict = document.getElementById('sim-verdict-badge');
    var evidenceRank = document.getElementById('sim-evidence-rank');
    var evidenceText = document.getElementById('sim-evidence-text');
    var audioQuote = document.getElementById('sim-audio-quote');
    var trackTitle = document.getElementById('sim-track-title');

    function loadScenario(key) {
      var data = SIM_SCENARIOS[key];
      if (!data) return;

      if (itemName) itemName.textContent = data.item;
      if (trackTitle) trackTitle.textContent = data.title;
      if (verdict) {
        verdict.className = 'sim-verdict ' + data.verdictClass;
        verdict.textContent = data.verdictText;
      }
      if (evidenceRank) evidenceRank.textContent = data.evidenceRank;
      if (evidenceText) evidenceText.textContent = data.evidenceText;
      if (audioQuote) audioQuote.textContent = data.audioText;
    }

    tabs.forEach(function (tab) {
      tab.addEventListener('click', function () {
        tabs.forEach(function (t) { t.classList.remove('is-active'); });
        tab.classList.add('is-active');
        loadScenario(tab.getAttribute('data-tab'));
      });
    });

    loadScenario('rotulo');
  }

  // ------------------------------------------------- Cópia de Swatches de Cor

  function setupColorSwatches() {
    var swatches = document.querySelectorAll('[data-hex]');
    var toast = document.getElementById('copy-toast');

    function showToast(text) {
      if (!toast) return;
      toast.textContent = text;
      toast.classList.add('is-visible');
      setTimeout(function () {
        toast.classList.remove('is-visible');
      }, 2000);
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

  // ---------------------------------------------------- Formulário & Opt-in

  function setupForm() {
    var form = document.querySelector('[data-waitlist]');
    if (!form) return;

    var status = form.querySelector('.form-status');
    var submit = form.querySelector('[type="submit"]');

    form.addEventListener('submit', function (e) {
      e.preventDefault();
      var data = new FormData(form);
      var optinTraining = form.querySelector('#optin-treino');

      // Salva opt-in de treino de modelos no navegador para auditoria e sincronização
      if (optinTraining && optinTraining.checked) {
        try {
          localStorage.setItem('ec_optin_model_training', 'granted');
          localStorage.setItem('ec_optin_timestamp', new Date().toISOString());
        } catch (err) {}
      }

      if (!CONTACT.formEndpoint) {
        // Modo fallback: abre cliente de e-mail formatado
        var name = data.get('nome') || '';
        var email = data.get('email') || '';
        var context = data.get('contexto') || '';
        var body = 'Nome: ' + name + '\nE-mail: ' + email + '\nDúvida/Contexto: ' + context +
                   '\nOpt-in Treino IA: ' + (optinTraining && optinTraining.checked ? 'Sim' : 'Não');
        window.location.href = 'mailto:' + CONTACT.email + '?subject=' +
          encodeURIComponent('Vaga no Teste EatControl') + '&body=' + encodeURIComponent(body);
        return;
      }

      status.textContent = 'Enviando sua inscrição…';
      submit.disabled = true;

      fetch(CONTACT.formEndpoint, {
        method: 'POST',
        body: data,
        headers: { 'Accept': 'application/json' }
      }).then(function (res) {
        if (res.ok) {
          status.textContent = 'Inscrição confirmada! Entraremos em contato para o teste fechado.';
          form.reset();
        } else {
          status.textContent = 'Ocorreu um erro no envio. Use o link de e-mail abaixo.';
        }
      }).catch(function () {
        status.textContent = 'Falha de conexão. Por favor, envie um e-mail direto.';
      }).finally(function () {
        submit.disabled = false;
      });
    });
  }

  // ---------------------------------------------------------------- Inicialização

  document.addEventListener('DOMContentLoaded', function () {
    initAnimations();
    setupSpotlight();
    setupSimulator();
    setupColorSwatches();
    setupForm();
  });
})();
