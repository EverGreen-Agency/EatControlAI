# Site do Eat Control

HTML estático, sem build. Quatro páginas, uma folha de estilo, zero dependência.

## Publicar na Vercel

1. `vercel.com/new` → importar este repositório.
2. **Root Directory: `web`** — é o único ajuste necessário.
3. Framework preset: *Other*. Sem comando de build, sem diretório de saída.
4. Deploy.

Domínio: aponte `eatcontrol.app` em *Settings → Domains*. Antes disso, os endereços
canônicos e o sitemap apontam para esse domínio e precisam ser trocados se o domínio final
for outro.

## Estrutura

```
index.html          landing
roadmap/            roadmap aberto — a peça de build in public
privacidade/        exigida pelo Google Play
termos/             exigida pelo Google Play
assets/site.css     tokens iguais aos de ui/theme/Color.kt
llms.txt            resumo para motores de resposta por IA
robots.txt          libera crawlers de busca e de IA
sitemap.xml
```

## Antes de publicar

- [ ] Trocar `contato@eatcontrol.app` pelo endereço real, em todas as páginas.
- [ ] Revisar a política de privacidade com quem responde juridicamente pelo projeto. O
      conteúdo descreve com precisão o que o aplicativo faz hoje, mas descrição técnica
      correta não é o mesmo que peça jurídica revisada.
- [ ] Confirmar o domínio e ajustar `canonical`, `og:url` e `sitemap.xml` se mudar.
- [ ] Ao publicar o app, trocar os `mailto:` do CTA pelo selo da Play Store.

## Manutenção do roadmap

A página de roadmap é o artefato de build in public e deve mudar toda semana. Três blocos:
pronto, em construção e descartado. O terceiro é o que dá credibilidade aos outros dois —
publicar o que foi cortado, e por quê, é o que diferencia transparência de marketing.
