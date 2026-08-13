# ADR-0006 — Versão do Meta DAT

Status: **Accepted** — Versão 0.9.0 fixada; isolamento arquitetural mantido via `GlassesGateway`

## Contexto


O Meta Wearables Device Access Toolkit está em developer preview. O `README.md` deste repositório
optou por não fixar versão até que houvesse decisão registrada. Esta ADR registra o levantamento
feito em 13/08/2026.

## Coordenadas

Versão documentada: **0.9.0**

Repositório Maven — **GitHub Packages**, não Maven Central:

```kotlin
// settings.gradle.kts
maven {
    url = uri("https://maven.pkg.github.com/facebook/meta-wearables-dat-android")
    credentials {
        username = ""
        password = System.getenv("GITHUB_TOKEN")
            ?: localProperties.getProperty("github_token")
    }
}
```

Artefatos:

```toml
mwdat-core       = { group = "com.meta.wearable", name = "mwdat-core",       version.ref = "mwdat" }
mwdat-camera     = { group = "com.meta.wearable", name = "mwdat-camera",     version.ref = "mwdat" }
mwdat-display    = { group = "com.meta.wearable", name = "mwdat-display",    version.ref = "mwdat" }
mwdat-mockdevice = { group = "com.meta.wearable", name = "mwdat-mockdevice", version.ref = "mwdat" }
```

`mwdat-display` não interessa a este projeto: os óculos do programa não têm display, e o edital é
explícito de que áudio é o único canal de saída.

Manifesto:

```xml
<meta-data android:name="com.meta.wearable.mwdat.APPLICATION_ID" android:value="${mwdat_application_id}" />
<meta-data android:name="com.meta.wearable.mwdat.CLIENT_TOKEN"  android:value="${mwdat_client_token}" />
```

Permissões: `BLUETOOTH`, `BLUETOOTH_CONNECT`, `INTERNET` e — opcionalmente — `CAMERA`, quando o mock
device é alimentado pela câmera do telefone.

## Decisão

Adotar a **0.9.0** assim que os dois bloqueios abaixo forem resolvidos, e manter todo o SDK dentro de
`DatGlassesGateway`, atrás da interface `GlassesGateway` já existente.

## Hardware compatível

Ray-Ban Meta (Gen 1 e Gen 2), Ray-Ban Meta Optics e Meta Ray-Ban Display. Confirmar qual modelo o
integrante da equipe possui — é o primeiro item de `checklists/REAL_GLASSES_TEST.md`.

## Como o app é ativado

O toolkit **não** dá stream contínuo por padrão. O fluxo é explícito:

```text
Wearables.createSession(...)   → abre a sessão
  → attach de capability       → câmera (foto ou stream) / microfone
  → captura                    → foto única com EXIF e correção de orientação
  → fim da sessão
```

O `Stream` é uma *capability* que precisa ser anexada de propósito; a foto é um disparo. Ou seja, a
arquitetura do SDK já empurra para captura sob demanda, que é o que o `NFR-009` pede e o que o
checkpoint de bateria do edital cobra.

O Developer Center pede justificativa de permissão separada para **câmera, microfone e invocação por
voz** — o que indica que existe um caminho de invocação por voz para app de terceiro. Confirmar o
gatilho exato na palestra de DAT do Ideathon (15/08, 10h30).

Apps em developer mode aparecem para o usuário em **Meta AI → App connections → Developer mode apps**.

## Bloqueios (nenhum é técnico — são de credencial)

1. **Token do GitHub com escopo `read:packages`.** Sem ele o Gradle não baixa nem o `mwdat-core` nem
   o `mwdat-mockdevice`. Vai em `local.properties` como `github_token=...` (o arquivo já está no
   `.gitignore`) ou na variável de ambiente `GITHUB_TOKEN`.
2. **Projeto no Wearables Developer Center**, que emite o `APPLICATION_ID` e o `CLIENT_TOKEN` usados
   na atestação do app. É o primeiro item da seção DAT do `checklists/MVP_CHECKLIST.md`.

Consequência importante: o **Mock Device Kit oficial da Meta também está atrás dessas credenciais**.
É por isso que o `MockGlassesGateway` deste repositório existe — ele não depende de nada e permite
trabalhar enquanto os dois itens acima não saem.

## Riscos

- Developer preview: a API pode mudar entre versões. O boundary `GlassesGateway` limita o estrago.
- `applicationId` participa da atestação. Já foi fixado em `com.eatcontrolai` justamente para não
  precisar reemitir credenciais depois.

## A registrar quando a integração acontecer

- [ ] Data da decisão e versão efetivamente usada
- [ ] Modelo de óculos testado e versão de firmware
- [ ] Versão do app Meta AI
- [ ] Known issues encontrados
