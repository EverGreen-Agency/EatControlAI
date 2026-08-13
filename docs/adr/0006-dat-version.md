# ADR-0006 — Versão do Meta DAT

Status: **Proposed** — coordenadas identificadas, integração bloqueada por credenciais

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
