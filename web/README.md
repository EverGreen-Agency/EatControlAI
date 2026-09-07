# Site do Eat Control

HTML estático, sem build. Quatro páginas, uma folha de estilo, zero dependência.

## Publicar na Vercel

1. `vercel.com/new` → importar este repositório.
2. **Root Directory: `web`** — é o único ajuste necessário.
3. Framework preset: *Other*. Sem comando de build, sem diretório de saída.
4. Deploy.

Domínio: aponte `eatcontrol.com.br` em *Settings → Domains*. Antes disso, os endereços
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

- [ ] CONFERIR o e-mail. As páginas usam `eatcontrol.ai@gmail.com`, exatamente como foi informado — se faltou um "l" em "eatcontrol", corrija antes de publicar.
- [ ] Revisar a política de privacidade com quem responde juridicamente pelo projeto. O
      conteúdo descreve com precisão o que o aplicativo faz hoje, mas descrição técnica
      correta não é o mesmo que peça jurídica revisada.
- [ ] Confirmar o domínio e ajustar `canonical`, `og:url` e `sitemap.xml` se mudar.
- [ ] Ao publicar o app, trocar os `mailto:` do CTA pelo selo da Play Store.

## Manutenção do roadmap

A página de roadmap é o artefato de build in public e deve mudar toda semana. Três blocos:
pronto, em construção e descartado. O terceiro é o que dá credibilidade aos outros dois —
publicar o que foi cortado, e por quê, é o que diferencia transparência de marketing.

## Medição de uso

Vazio por padrão: sem ID configurado, nenhum script carrega e **nenhum banner aparece**.
Para ligar, preencha em `assets/analytics.js`:

```js
clarityId:  'xxxxxxxx',   // clarity.microsoft.com → Settings → Overview
posthogKey: 'phc_xxxxx',  // app.posthog.com → Project Settings → Project API Key
```

No momento em que um dos dois é preenchido, a barra de consentimento passa a aparecer e
nada é carregado antes do "Aceitar". Navegador com Global Privacy Control ou Do Not Track
é tratado como recusa, sem perguntar.

Google Analytics ficou de fora de propósito: acrescenta pouco sinal sobre o que o Clarity e
o PostHog já mostram, e acrescenta uma transferência internacional a mais para justificar
na política. Se for necessário depois, entra pelo mesmo portão de consentimento.

**A política de privacidade descreve exatamente esta configuração** (seção 9). Ao trocar
de ferramenta, atualize a seção junto — política que não corresponde ao código é pior que
política nenhuma.
