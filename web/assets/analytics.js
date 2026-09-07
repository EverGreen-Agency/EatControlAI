/*
  Medição de uso, atrás de consentimento.

  Três decisões que valem explicar:

  1. **Nada carrega antes do "aceitar".** Clarity grava sessão e PostHog identifica
     visitante; num site de saúde isso é tratamento de dado pessoal, e a LGPD pede base legal.
     Consentimento explícito é a base mais simples de sustentar, e é a única que não exige
     advogado para justificar depois.

  2. **Sem ID configurado, nada existe** - nem script, nem banner, nem cookie. O site
     funciona hoje, com esta configuração vazia, sem nenhum aviso na tela. Isso evita o
     modo de falha clássico de banner de cookie que aparece sem haver cookie nenhum.

  3. **Global Privacy Control é respeitado como recusa.** Quem já declarou no navegador
     que não quer ser rastreado não precisa dizer de novo aqui.
*/

(function () {
  'use strict';

  // ---------------------------------------------------------------- configuração

  var CONFIG = {
    // Microsoft Clarity: mapa de calor e gravação de sessão. Grátis e ilimitado.
    // Pegue em clarity.microsoft.com → Settings → Overview.
    clarityId: '',

    // PostHog: funil, eventos de produto e retenção.
    // Pegue em app.posthog.com → Project Settings → Project API Key.
    posthogKey: '',
    posthogHost: 'https://us.i.posthog.com'
  };

  var STORAGE_KEY = 'ec-consent';

  // ------------------------------------------------------------------- utilidades

  function configured() {
    return Boolean(CONFIG.clarityId || CONFIG.posthogKey);
  }

  function readConsent() {
    try {
      return window.localStorage.getItem(STORAGE_KEY);
    } catch (e) {
      // Navegador com armazenamento bloqueado. Sem memória do consentimento, o
      // comportamento seguro é não medir.
      return 'denied';
    }
  }

  function writeConsent(value) {
    try {
      window.localStorage.setItem(STORAGE_KEY, value);
    } catch (e) {
      /* sem persistência: o banner volta na próxima visita, o que é melhor que medir sem permissão */
    }
  }

  /** Sinal explícito do navegador de que a pessoa não quer ser rastreada. */
  function optedOutByBrowser() {
    return navigator.globalPrivacyControl === true || navigator.doNotTrack === '1';
  }

  // ---------------------------------------------------------------------- carga

  function loadClarity(id) {
    window.clarity = window.clarity || function () {
      (window.clarity.q = window.clarity.q || []).push(arguments);
    };
    var s = document.createElement('script');
    s.async = true;
    s.src = 'https://www.clarity.ms/tag/' + id;
    document.head.appendChild(s);
  }

  function loadPostHog(key, host) {
    var s = document.createElement('script');
    s.async = true;
    s.src = host + '/static/array.js';
    s.onload = function () {
      if (window.posthog && window.posthog.init) {
        window.posthog.init(key, {
          api_host: host,
          // A pessoa consentiu com medição de uso, não com perfil persistente entre sessões.
          persistence: 'localStorage',
          autocapture: true,
          capture_pageview: true,
          // Mascarar entrada de texto: nada que alguém digite deve chegar ao painel.
          mask_all_text: false,
          mask_all_element_attributes: false,
          session_recording: { maskAllInputs: true }
        });
      }
    };
    document.head.appendChild(s);
  }

  function start() {
    if (CONFIG.clarityId) loadClarity(CONFIG.clarityId);
    if (CONFIG.posthogKey) loadPostHog(CONFIG.posthogKey, CONFIG.posthogHost);
  }

  // ---------------------------------------------------------------------- banner

  function showBanner() {
    var bar = document.createElement('div');
    bar.className = 'consent';
    bar.setAttribute('role', 'dialog');
    bar.setAttribute('aria-label', 'Preferência de medição de uso');
    bar.innerHTML =
      '<p>Usamos medição de uso para entender o que funciona neste site. ' +
      'Nada é coletado sem a sua permissão. ' +
      '<a href="/privacidade/">Como tratamos dados</a>.</p>' +
      '<div class="consent-actions">' +
      '<button type="button" data-consent="denied" class="btn btn-ghost">Recusar</button>' +
      '<button type="button" data-consent="granted" class="btn btn-primary">Aceitar</button>' +
      '</div>';

    bar.addEventListener('click', function (event) {
      var choice = event.target.getAttribute('data-consent');
      if (!choice) return;
      writeConsent(choice);
      bar.remove();
      if (choice === 'granted') start();
    });

    document.body.appendChild(bar);
  }

  // ------------------------------------------------------------------------ fluxo

  if (!configured()) return;

  if (optedOutByBrowser()) {
    writeConsent('denied');
    return;
  }

  var consent = readConsent();
  if (consent === 'granted') {
    start();
  } else if (consent !== 'denied') {
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', showBanner);
    } else {
      showBanner();
    }
  }
})();
