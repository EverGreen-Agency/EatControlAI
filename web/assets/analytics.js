/*
  EatControl — Medição de uso e gestão de consentimento (LGPD).
  Suporte estruturado para Microsoft Clarity, PostHog e Google Analytics 4 (GA4).
  Armazenamento e gestão de opt-in para treinamento de modelos de IA (Clarifai / Gemini / próprio).
*/

(function () {
  'use strict';

  var CONFIG = {
    // Microsoft Clarity (mapa de calor e gravação de sessão)
    clarityId: 'yfbcrp7znw',

    // PostHog (análise de produto e funis)
    posthogKey: 'phc_wPKfPaD2LgBjrZQAnCtk3N2Lk25aCQhsqLf9QzVuwpjo',
    posthogHost: 'https://us.i.posthog.com',

    // Google Analytics 4 (GA4: ex: 'G-XXXXXXXXXX')
    gaMeasurementId: '',

    // Webhook/Endpoint para ingestão de telemetria de treino de modelos (opcional)
    modelTrainingEndpoint: ''
  };

  var STORAGE_KEY = 'ec_user_consent';
  var TRAINING_OPTIN_KEY = 'ec_optin_model_training';

  function hasAnyServiceConfigured() {
    return Boolean(CONFIG.clarityId || CONFIG.posthogKey || CONFIG.gaMeasurementId);
  }

  function readConsent() {
    try {
      return window.localStorage.getItem(STORAGE_KEY);
    } catch (e) {
      return 'denied';
    }
  }

  function writeConsent(val) {
    try {
      window.localStorage.setItem(STORAGE_KEY, val);
    } catch (e) {}
  }

  function optedOutByBrowser() {
    return navigator.globalPrivacyControl === true || navigator.doNotTrack === '1';
  }

  // ------------------------------------------------ Carregadores de Scripts

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
          persistence: 'localStorage',
          autocapture: true,
          capture_pageview: true,
          mask_all_text: false,
          session_recording: { maskAllInputs: true }
        });
      }
    };
    document.head.appendChild(s);
  }

  function loadGA4(id) {
    var s = document.createElement('script');
    s.async = true;
    s.src = 'https://www.googletagmanager.com/gtag/js?id=' + id;
    document.head.appendChild(s);

    window.dataLayer = window.dataLayer || [];
    function gtag() { window.dataLayer.push(arguments); }
    window.gtag = gtag;
    gtag('js', new Date());
    gtag('config', id, { anonymize_ip: true });
  }

  // ------------------------------------------------ Execução & Banner

  function activateTracking() {
    if (CONFIG.clarityId) loadClarity(CONFIG.clarityId);
    if (CONFIG.posthogKey) loadPostHog(CONFIG.posthogKey, CONFIG.posthogHost);
    if (CONFIG.gaMeasurementId) loadGA4(CONFIG.gaMeasurementId);
  }

  function createConsentBanner() {
    if (!hasAnyServiceConfigured() || optedOutByBrowser()) return;

    var existing = readConsent();
    if (existing === 'granted') {
      activateTracking();
      return;
    }
    if (existing === 'denied') return;

    var bar = document.createElement('aside');
    bar.className = 'consent-bar';
    bar.setAttribute('role', 'region');
    bar.setAttribute('aria-label', 'Privacidade e medição de uso');

    bar.innerHTML =
      '<div class="wrap consent-wrap" style="display:flex;justify-content:space-between;align-items:center;gap:1rem;flex-wrap:wrap;padding:0.85rem 1rem;background:rgba(16,22,29,0.95);border:1px solid rgba(45,212,231,0.25);border-radius:12px;margin-bottom:1rem;box-shadow:0 8px 32px rgba(0,0,0,0.5);">' +
        '<p style="margin:0;font-size:0.85rem;color:#cfd6dd;">Usamos medição anônima para saber o que melhorar no app. Você aceita?</p>' +
        '<div style="display:flex;gap:0.6rem;">' +
          '<button type="button" class="btn btn-sm btn-primary" data-consent="yes">Aceitar</button>' +
          '<button type="button" class="btn btn-sm btn-ghost" data-consent="no">Recusar</button>' +
        '</div>' +
      '</div>';

    bar.style.position = 'fixed';
    bar.style.bottom = '1rem';
    bar.style.left = '0';
    bar.style.right = '0';
    bar.style.zIndex = '999';

    document.body.appendChild(bar);

    bar.querySelector('[data-consent="yes"]').addEventListener('click', function () {
      writeConsent('granted');
      bar.remove();
      activateTracking();
    });

    bar.querySelector('[data-consent="no"]').addEventListener('click', function () {
      writeConsent('denied');
      bar.remove();
    });
  }

  document.addEventListener('DOMContentLoaded', createConsentBanner);
})();
