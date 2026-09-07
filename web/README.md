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

## Formulário e WhatsApp

O botão "Pedir uma vaga" era um `mailto:` — abre cliente de e-mail, e num celular
frequentemente não abre nada. Agora existe formulário de verdade, configurado em
`assets/site.js`:

```js
formEndpoint: '',   // formspree.io → New Form → cole a URL https://formspree.io/f/xxxxxxxx
whatsapp: '',       // só dígitos com país e DDD: '5551999999999'
```

Comportamento por configuração, sem estado quebrado em nenhum caso:

| Configurado | O que aparece |
|---|---|
| nada | botão de e-mail apenas (funciona, só não é o ideal) |
| `whatsapp` | botão de e-mail + botão do WhatsApp |
| `formEndpoint` | formulário completo com estados de envio, sucesso e erro |
| ambos | formulário + WhatsApp ao lado do enviar |

Se o envio falhar por rede, o próprio erro oferece o mesmo conteúdo por e-mail — o
visitante nunca fica sem saída.

Formspree grátis: 50 envios por mês, sem backend, sem cartão. Suficiente para o teste
fechado; se estourar, o mesmo formulário aponta para outro endpoint sem mudar HTML.

## Marca

Os arquivos vêm de `EatControl_Precision_Intelligence_BrandKit_v1.0/` e foram copiados
para `assets/`. Tokens de cor em `site.css` espelham `03_Design_System/tokens.css`:
Ink `#0B0F14`, Deep Ocean `#062439`, Signal Cyan `#2DD4E7`, Warm White `#F7F4EE`.
Títulos em Manrope, interface em Inter, ambos via Google Fonts.

Ao atualizar o brandkit, recopie os SVGs e confira os tokens — não edite as cores
diretamente no CSS sem refletir a mudança no kit.
