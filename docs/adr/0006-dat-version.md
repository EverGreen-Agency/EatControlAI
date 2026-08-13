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

## Superfície da API (levantada por inspeção dos AARs em 13/08/2026)

A documentação pública não descreve tudo, então o mapa abaixo veio de `javap` sobre os artefatos
baixados. É o que `DatGlassesGateway` usa.

```text
Wearables.initialize(context)              → DatResult<Unit, WearablesError>
Wearables.isDevMode                        → Boolean
Wearables.registrationState                → StateFlow<RegistrationState>
Wearables.devices                          → StateFlow<Set<DeviceIdentifier>>
Wearables.startRegistration(activity)      → abre o fluxo de autorização no Meta AI
Wearables.checkPermissionStatus(Permission)
Wearables.createSession(DeviceSelector)    → DatResult<DeviceSession, DeviceSessionError>

DeviceSession.start() / stop()
DeviceSession.state                        → StateFlow<DeviceSessionState>
DeviceSession.errors                       → SharedFlow<DeviceSessionError>
session.addCamera(StreamConfiguration)     → DatResult<Camera, DeviceSessionError>   (extensão)
session.removeCamera()

Camera.state                               → StateFlow<CameraState>
Camera.stream                              → Stream
Stream.start() / stop()
Stream.capturePhoto()                      → DatResult<PhotoData, CaptureError>   (suspend)
Stream.videoStream                         → Flow<VideoFrame>
VideoFrame(buffer: ByteBuffer, width, height, presentationTimeUs, isCompressed, isCodecConfig)

DeviceSelector: AutoDeviceSelector | SpecificDeviceSelector
VideoQuality: HIGH | MEDIUM | LOW
StreamState: STARTING · STARTED · STREAMING · PAUSED · STOPPING · STOPPED · CLOSED
```

Duas descobertas que valem registro:

- **`Permission` só tem `CAMERA`.** Não há permissão de microfone na 0.9.0, e não existe artefato de
  áudio entre os publicados (`mwdat-core`, `mwdat-camera`, `mwdat-display`, `mwdat-mockdevice`). A
  trilha de voz continua usando o microfone do telefone.
- **`PhotoData` é uma interface vazia.** `capturePhoto()` existe e devolve `DatResult<PhotoData,
  CaptureError>`, mas o tipo público não expõe membro nenhum — os bytes só existem na implementação
  interna. Na prática, o disparo aciona o obturador e a imagem é lida do `videoStream`. Perguntar no
  Ideathon se é limitação do preview ou se há caminho documentado.

O Mock Device Kit oficial está disponível: `MockDeviceKit.enable(config)` e
`pairGlasses(GlassesModel.RAYBAN_META | OAKLEY_META_HSTN | OAKLEY_META_VANGUARD |
RAYBAN_META_OPTICS | META_GLASSES)`.

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

## Estado da integração

- [x] Token do GitHub com `read:packages` em `local.properties`
- [x] Repositório Maven configurado em `settings.gradle.kts`, restrito ao grupo `com.meta.wearable`
- [x] Artefatos `mwdat-core`, `mwdat-camera` e `mwdat-mockdevice` resolvendo
- [x] Projeto criado no Wearables Developer Center; `APPLICATION_ID` e `CLIENT_TOKEN` emitidos
- [x] `DatGlassesGateway` escrito sobre a API real e compilando
- [ ] Preencher **Package** (`com.eatcontrolai`) e **App signature** na Configuration
- [ ] Ligar **Camera access** com justificativa
- [ ] Validar captura em óculos reais — só então entra no `CaptureSourceRouter`
- [ ] Confirmar o formato do frame (assumimos NV21 com `compressVideo = false`)

Enquanto a captura não passar por hardware, o app usa `MockGlassesGateway` e `PhoneCameraGateway`.
`DatGlassesGateway` fica construído e fora do seletor — botão que não funciona é pior que ausência
de botão.

## Bloqueios anteriores (resolvidos)

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
