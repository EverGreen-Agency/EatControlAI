/*
  EatControl — Interatividade, Micro-animações Cinemáticas e Simulador.
  GSAP 3 + ScrollTrigger com fallback para IntersectionObserver.
*/

(function () {
  'use strict';

  var CONTACT = {
    formEndpoint: '',
    whatsapp: 'https://wa.me/5511999999999',
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

  // ---------------------------------------------------- Simulador Interativo

  var SIM_SCENARIOS = {
    rotulo: {
      title: 'Leitura de Rótulo por OCR',
      desc: 'O motor de percepção cruza a lista legal de ingredientes contra a sua lista de intolerâncias e diretrizes de GLP-1. Promessas de marketing na frente da embalagem nunca anulam o verso obrigatório.',
      item: 'Iogurte Grego "Zero Lactose"',
      verdictClass: 'tag-incompativel',
      verdictText: 'INCOMPATÍVEL',
      auditTag: 'ANVISA RDC 727',
      evidenceRank: 'RANK 2 · DECLARED_LABEL',
      evidenceText: 'Texto frontal declara "Zero Lactose", mas a lista legal de ingredientes no verso declara expressamente "CONTÉM LEITE E DERIVADOS". Em caso de alergia à proteína do leite (APLV), prevalece o ingrediente declarado.',
      audioText: '"Atenção: embora anunciado como zero lactose, o produto contém proteína do leite na lista de ingredientes."'
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
      audioText: '"Produto compatível com seu protocolo. 16g de proteína e teor de gordura adequado para sua digestão."'
    },
    prato: {
      title: 'Prato Assistido (Visão Honesta)',
      desc: 'Reconhece componentes visuais sem inventar gramaturas milagrosas. O app declara incerteza e pede sua confirmação para registrar macros com fidelidade clínica.',
      item: 'Filé de Salmão com Brócolis e Quinoa',
      verdictClass: 'tag-atencao',
      verdictText: 'ATENÇÃO — CONFIRMAR QUANTIDADE',
      auditTag: 'COMPUTER VISION + CONFIRMAÇÃO',
      evidenceRank: 'RANK 6 · VISUAL_INFERENCE → RANK 4 · USER_CONFIRMATION',
      evidenceText: 'Componentes identificados: Salmão (~140g) e vegetais fibrosos. O aplicativo não inventa gramatura por foto: confirme a porção para computar a ingestão real.',
      audioText: '"Identifiquei salmão e brócolis. Qual foi o tamanho aproximado da porção consumida?"'
    },
    cardapio: {
      title: 'Cardápio Contextual de Restaurante',
      desc: 'Triagem de pratos e molhos em cardápios impressos ou digitais. Alerta sobre potenciais gatilhos de refluxo, gorduras saturadas e retardo gástrico antes do pedido.',
      item: 'Prato Executivo: Risoto de Queijo com Mignon',
      verdictClass: 'tag-atencao',
      verdictText: 'ATENÇÃO — GORDURA POTENCIAL',
      auditTag: 'OCR CONTEXTUAL · GLP-1 GASTRIC ALERT',
      evidenceRank: 'RANK 5 · OCR_TEXT',
      evidenceText: 'Cardápio lido por OCR contextual. O risoto tradicional contém alta concentração de manteiga e creme de leite, forte gatilho para retardo no esvaziamento gástrico e náusea.',
      audioText: '"O risoto tem teor elevado de gordura que pode provocar desconforto gástrico severo com GLP-1. Peça opção grelhada."'
    }
  };

  function setupSimulator() {
    var tabs = document.querySelectorAll('.sim-btn, .sim-tab');
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

    function loadScenario(key) {
      var data = SIM_SCENARIOS[key];
      if (!data) return;

      if (window.gsap && decisionCard && !reduced) {
        window.gsap.fromTo(decisionCard, { opacity: 0.4, y: 8 }, { opacity: 1, y: 0, duration: 0.35, ease: 'power2.out' });
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
      var originalText = submitBtn.textContent;
      submitBtn.disabled = true;
      submitBtn.textContent = 'Garantindo vaga...';

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
        submitBtn.textContent = 'Vaga Garantida!';
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
        toast.textContent = 'Inscrição confirmada! Você receberá o convite para o APK em breve.';
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
    setupColorSwatches();
    setupWaitlistForm();
  });

})();
