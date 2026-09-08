# Onde colocar cada credencial

Checklist único. Cada linha diz o arquivo exato, e se o valor é segredo ou público.

A distinção importa: **chave de servidor é segredo e nunca entra no Git**; **ID de analytics
e chave pública de SDK já viajam dentro do app ou da página**, então versioná-los não expõe
nada que o usuário não possa ler abrindo o DevTools. Tratar as duas categorias igual leva a
um dos dois erros: vazar o que importa, ou esconder o que não precisa.

---

## 1. Aplicativo Android

Tudo em **`local.properties`**, na raiz. O arquivo já está no `.gitignore` e **nunca** deve
sair de lá.

```properties
# Já existentes
sdk.dir=C\:\\Users\\...\\Android\\Sdk
github_token=ghp_...                  # read:packages, para o SDK do Meta DAT resolver
mwdat_application_id=...
mwdat_client_token=...

# Assinatura de release — as quatro juntas ou nenhuma (o Gradle checa isso)
release_store_file=keystores/eatcontrol-release.jks
release_store_password=...
release_key_alias=eatcontrol-release
release_key_password=...

# RevenueCat — chave PÚBLICA de SDK do Android (começa com "goog_")
# Painel → Project Settings → API Keys → Public app key
revenuecat_android_key=goog_...

# Gemini — chave de API para visão e interpretação de fala
gemini_api_key=...

# S3 na Railway, para as fotos do opt-in de treino
s3_endpoint=https://...railway.app
s3_bucket=eatcontrol-training
s3_access_key=...
s3_secret_key=...
```

**O keystore em si** (`eatcontrol-release.jks`) fica em `keystores/` — pasta ignorada pelo
Git. Senha e arquivo em lugares diferentes: arquivo no disco, senhas no cofre da equipe.

**O AAB** não se guarda: é gerado por `./gradlew bundleRelease` e sai em
`app/build/outputs/bundle/release/`. Você sobe esse arquivo no Play Console e pronto.

**A ficha da Play Store** não é arquivo do projeto — título, descrição, screenshots e ícone
1024 são preenchidos direto no Play Console. Os assets vêm do brandkit.

> ⚠️ A chave do Gemini e as credenciais do S3 vão **dentro do APK** se forem usadas direto do
> aplicativo. Isso é aceitável para começar, mas significa que alguém pode extraí-las. Quando
> houver volume, essas duas devem migrar para um proxy servidor. A do RevenueCat não tem esse
> problema: ela é pública por design.

---

## 2. Site

Valores **públicos** — ficam versionados mesmo, porque já são visíveis no navegador.

**`web/assets/analytics.js`**, no bloco `CONFIG`:

```js
clarityId: '',          // clarity.microsoft.com → Settings → Overview
posthogKey: '',         // app.posthog.com → Project Settings → Project API Key
gaMeasurementId: '',    // GA4, formato G-XXXXXXXXXX
```

**`web/assets/site.js`**, no bloco `CONTACT`:

```js
formEndpoint: '',       // formspree.io → New Form → https://formspree.io/f/xxxxxxxx
whatsapp: '',           // só dígitos, com país e DDD: 5551999999999
```

Enquanto estiverem vazios: nenhum script de medição carrega, nenhum banner de consentimento
aparece, e o botão de e-mail assume o lugar do formulário. O site funciona publicado hoje,
sem nada configurado.

---

## 3. Ordem que destrava mais rápido

Existe uma corrente de dependências e ela começa em você:

1. **Keystore** → sem ele não há AAB
2. **AAB** → sem ele não há teste interno no Play
3. **Teste interno** → é onde você e o Luís usam o app de verdade
4. **Produtos no Play Console** → a RevenueCat precisa deles para validar compra
5. **Chave da RevenueCat** → aí o paywall passa de código a coisa testável

Fora dessa corrente, e portanto paralelizáveis a qualquer momento: chave do Gemini, S3,
Formspree, WhatsApp, Clarity, PostHog e GA4.

---

## 4. Onde nada disso deve aparecer

Nunca em: mensagem de chat, anexo de e-mail, print de tela, commit, issue, ou neste arquivo.
O `.gitignore` cobre `local.properties`, `*.jks`, `*.keystore` e `keystores/` — mas o
`.gitignore` protege contra descuido, não contra decisão. Conferir `git diff` antes de
commitar continua sendo obrigação de quem commita.
