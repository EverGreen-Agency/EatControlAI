/*
  Comportamento do site: revelação ao rolar, borda-holofote nos cartões e envio do formulário.

  Sem biblioteca de animação. Um ScrollTrigger do GSAP faria o mesmo reveal por ~70 KB de
  JavaScript; IntersectionObserver faz por algumas linhas, e a página inteira existe para ser
  rápida e indexável. Motion library entra quando houver algo que ela realmente resolva.
*/

(function () {
  'use strict';

  // ---------------------------------------------------------------- configuração

  var CONTACT = {
    /*
      Endpoint que recebe o formulário. Formspree resolve sem backend:
      formspree.io → New Form → cole aqui a URL "https://formspree.io/f/xxxxxxxx".
      Vazio = o formulário não aparece e o botão de e-mail assume.
    */
    formEndpoint: '',

    /* Só dígitos, com país e DDD: '5511999999999'. Vazio = botão do WhatsApp não aparece. */
    whatsapp: '',

    email: 'eatcontrol.ai@gmail.com'
  };

  var reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  // ------------------------------------------------------------------ revelação

  function setupReveal() {
    var targets = document.querySelectorAll('[data-reveal]');
    if (!targets.length) return;

    // Sem IntersectionObserver, ou com movimento reduzido, o conteúdo já nasce visível:
    // a classe que esconde só é aplicada quando há como revelar de volta.
    if (reduced || !('IntersectionObserver' in window)) return;

    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        entry.target.classList.add('is-in');
        io.unobserve(entry.target);
      });
    }, { rootMargin: '0px 0px -12% 0px', threshold: 0.08 });

    targets.forEach(function (el, i) {
      el.classList.add('reveal');
      // Escalonamento curto entre irmãos: sugere ordem sem virar espera.
      el.style.transitionDelay = Math.min(i % 4, 3) * 60 + 'ms';
      io.observe(el);
    });
  }

  // -------------------------------------------------------------- borda-holofote

  function setupSpotlight() {
    if (reduced || !window.matchMedia('(hover: hover)').matches) return;

    document.addEventListener('pointermove', function (event) {
      var card = event.target.closest ? event.target.closest('.card') : null;
      if (!card) return;
      var box = card.getBoundingClientRect();
      card.style.setProperty('--mx', (event.clientX - box.left) + 'px');
      card.style.setProperty('--my', (event.clientY - box.top) + 'px');
    }, { passive: true });
  }

  // ------------------------------------------------------------------ formulário

  function mailtoHref(name, email, note) {
    var body = 'Nome: ' + name + '\nE-mail: ' + email + '\n\n' + note;
    return 'mailto:' + CONTACT.email +
      '?subject=' + encodeURIComponent('Quero testar o Eat Control') +
      '&body=' + encodeURIComponent(body);
  }

  function setupForm() {
    var form = document.querySelector('[data-waitlist]');
    if (!form) return;

    var status = form.querySelector('.form-status');
    var submit = form.querySelector('[type="submit"]');

    // Sem endpoint configurado o formulário não fica no ar prometendo o que não faz:
    // ele some e o bloco alternativo (e-mail, e WhatsApp se houver) assume.
    if (!CONTACT.formEndpoint) {
      form.hidden = true;
      return;
    }

    form.addEventListener('submit', function (event) {
      event.preventDefault();
      var data = new FormData(form);

      status.textContent = 'Enviando…';
      status.removeAttribute('data-tone');
      submit.disabled = true;

      fetch(CONTACT.formEndpoint, {
        method: 'POST',
        body: data,
        headers: { Accept: 'application/json' }
      }).then(function (response) {
        if (!response.ok) throw new Error('resposta ' + response.status);
        form.reset();
        status.textContent = 'Recebido. Respondemos no e-mail que você deixou.';
        status.setAttribute('data-tone', 'ok');
      }).catch(function () {
        // Falha de rede não pode virar beco sem saída: o mesmo conteúdo vai por e-mail.
        var href = mailtoHref(data.get('nome') || '', data.get('email') || '', data.get('contexto') || '');
        status.innerHTML = 'Não consegui enviar agora. ' +
          '<a href="' + href + '">Mande por e-mail</a> que a gente responde igual.';
        status.setAttribute('data-tone', 'error');
      }).then(function () {
        submit.disabled = false;
      });
    });
  }

  // -------------------------------------------------------------------- WhatsApp

  function setupWhatsapp() {
    var slots = document.querySelectorAll('[data-whatsapp]');
    if (!slots.length) return;

    if (!CONTACT.whatsapp) {
      slots.forEach(function (el) { el.hidden = true; });
      return;
    }

    var text = encodeURIComponent('Oi! Quero entrar no teste fechado do Eat Control.');
    slots.forEach(function (el) {
      el.href = 'https://wa.me/' + CONTACT.whatsapp + '?text=' + text;
      el.hidden = false;
    });
  }

  // ----------------------------------------------------------------------- fluxo

  function init() {
    setupReveal();
    setupSpotlight();
    setupForm();
    setupWhatsapp();

    document.querySelectorAll('[data-mailto]').forEach(function (el) {
      el.href = 'mailto:' + CONTACT.email +
        '?subject=' + encodeURIComponent('Quero testar o Eat Control');
    });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
