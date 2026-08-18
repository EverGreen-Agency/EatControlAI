---
unit: 13
unit_title: "Curso do Meta SDK (DAT)"
section: "13.4"
section_title: "Registro e ciclo de sessão"
source_file: "Un13_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN13_Curso_do_Meta_SDK_DAT.md"
source_pages: [26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38]
language: "pt-BR"
---

## 13.4 Registro e ciclo de sessão

### 13.4.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar por que o SDK precisa ser inicializado uma única vez por processo e o que acontece quando uma API é chamada antes da inicialização.

- Implementar o fluxo de registro do app com Wearables.startRegistration e o de desregistro com Wearables.startUnregistration, observando registrationState e devices.

- Descrever o ciclo de vida de uma Session (IDLE → STARTING → STARTED ⇄ PAUSED → STOPPING → STOPPED) e o papel do Stream como capability anexada à sessão.

- Implementar a reação correta a pause/resume e ao encerramento da sessão, sem tentar "reviver" uma sessão encerrada.

- Configurar o monitoramento de disponibilidade do dispositivo via devicesMetadata (campo linkState).

### 13.4.2 Pré-requisitos

- Tópico 1.2 — Coroutines e Flow: todo o estado do toolkit é exposto como StateFlow/SharedFlow; você vai coletar flows o tempo todo.

- Tópico 2.2 — Arquitetura: entender o papel do app Meta AI como intermediário entre seu app e os óculos.

- Tópico 2.3 — Setup: projeto Android com as dependências mwdat (versão 0.8.0) configuradas, manifest com o scheme de callback e Developer Mode habilitado no app Meta AI.

- Óculos Ray-Ban Meta pareados com o app Meta AI — ou o Mock Device Kit, que será visto no tópico 2.7.

### 13.4.3 Conceito / fundamentação

#### 13.4.3.1 Onde estamos na arquitetura

Seu app nunca fala diretamente com os óculos. Como visto no 2.2, o caminho é: seu app → SDK (DAT) → app Meta AI → Bluetooth → óculos. Isso explica duas características centrais deste tópico: o registro acontece dentro do app Meta AI (é ele quem pergunta ao usuário se confia no seu app), e o estado da sessão é comandado pelo sistema — seu app observa o estado, não o controla sozinho.

Uma analogia: o app Meta AI é a portaria de um prédio. O registro é o seu cadastro na portaria (feito uma vez); a sessão é cada visita — a portaria pode suspender ou encerrar a visita a qualquer momento (óculos guardados, outro visitante prioritário), e cabe a você reagir, não discutir.

#### 13.4.3.2 Inicialização do SDK: uma vez por processo

Antes de qualquer outra chamada, o SDK precisa ser inicializado:

Wearables.initialize(context)

**Código 2.4-01 · também no notebook companion, seção 2.4.3.2**

A regra é: uma vez por processo, no startup do app. O lugar natural é o onCreate() da sua classe Application, garantindo que a inicialização aconteça antes de qualquer Activity ou ViewModel tocar no SDK.

Atenção! O erro típico é chamar qualquer API do toolkit antes de initialize. Segundo a documentação oficial, isso resulta em WearablesError.NOT_INITIALIZED — e os métodos do objeto Wearables lançam WearablesException quando o SDK não foi inicializado. O sintoma clássico: o app funciona quando você abre pela tela principal, mas quebra quando o Android recria o processo direto em outra tela. Inicializar na Application elimina essa classe de bug. Nota 29: initialize retorna um DatResult<Unit, WearablesError> — o tipo de resultado padrão do toolkit, no estilo do Result do Kotlin (com fold, getOrElse, onSuccess/onFailure).

#### 13.4.3.3 Registro: apresentando seu app aos óculos

O registro estabelece a confiança entre seu app e a plataforma dos óculos. Ele acontece uma única vez, através do app Meta AI, com os óculos conectados. Depois de registrado, seu app aparece na lista de apps conectados do Meta AI, e o usuário pode desregistrá-lo por lá a qualquer momento.

O SDK expõe duas chamadas e dois pontos de observação:

- Wearables.startRegistration(activity) — abre o app Meta AI, onde o usuário completa o fluxo de registro. Quando ele termina, o resultado é processado automaticamente e refletido no estado.

- Wearables.startUnregistration(activity) — mesmo mecanismo, para desregistrar.

- Wearables.registrationState: StateFlow<RegistrationState> — o estado atual do registro.

- Wearables.devices: StateFlow<Set<DeviceIdentifier>> — o conjunto de dispositivos que o SDK pode usar.

- RegistrationState é um enum com cinco constantes (Tabela 21). O erro de registro não fica no estado — vem de um fluxo à parte, Wearables.registrationErrorStream: Flow<RegistrationError>

**Tabela 21 – Estados do RegistrationState e seus respectivos significados Estado Significado**

UNAVAILABLE Registro indisponível no momento (restrição de sistema — ex.: app Meta AI ausente).

AVAILABLE Registro pode ser iniciado. REGISTERING Fluxo de registro em andamento. REGISTERED App registrado com sucesso. UNREGISTERING Fluxo de desregistro em andamento.

Fonte: autoria própria.

Atenção! Em Developer Mode, apenas um app de terceiros pode permanecer registrado por vez. Registrar um novo app desregistra automaticamente o anterior. No hackathon, se um colega instalar e registrar o build dele no mesmo celular, o seu app perde o registro silenciosamente — se registrationState voltar para Available sem você pedir, é isso. Nota 30: como visto no 2.3, o retorno do Meta AI para o seu app depende do intent filter com o URI scheme declarado no manifest. Sem ele, o fluxo de registro não consegue voltar.

#### 13.4.3.4 Sessão: acesso sustentado ao dispositivo

Com o app registrado, o acesso aos sensores dos óculos acontece dentro de uma device session — um período de acesso sustentado às capacidades do dispositivo (em contraste com transactions, interações curtas controladas pelo sistema, como o "Hey Meta").

O fluxo é: 1. Wearables.createSession(deviceSelector) cria a sessão para um dispositivo que case com o seletor (AutoDeviceSelector deixa o SDK

escolher; SpecificDeviceSelector usa um dispositivo que você indicar). A resolução do dispositivo é imediata, contra o snapshot atual de Wearables.devices. 2. A sessão nasce em IDLE. Chame session.start() para conectar. 3. Capabilities são anexadas à sessão — o Stream de câmera entra via session.addStream(...) (detalhes de câmera no tópico 2.5; microfone e áudio no 2.6). Quando a sessão para, todas as capabilities anexadas param juntas (cascading stop). createSession pode falhar com DeviceSessionError.NO_ELIGIBLE_DEVICE (...) ou DeviceSessionError.SESSION_ALREADY_EXISTS — existe garantia de uma sessão ativa por dispositivo (singleton). Se já há uma sessão não encerrada, crie capabilities nela em vez de criar outra sessão.

#### 13.4.3.5 O ciclo de estados da sessão

O estado da sessão que você criou é exposto em session.state: StateFlow<DeviceSessionState> (Figura 21):

**Figura 21 – Ciclo de vida da DeviceSession**

Fonte: autoria própria.

Diferentes tipos de estados de sessão são descritos na Tabela 22.

**Tabela 22 – Diferentes tipos de estados de sessão Estado Significado O que seu app faz IDLE Criada, ainda não conectou. Chamar start(). STARTING Conectando ao dispositivo. Aguardar; mostrar progresso. STARTED Ativa; capabilities funcionam. Trabalhar (adicionar stream etc.).**

PAUSED Suspensa temporariamente pelo sistema. Segurar o trabalho e aguardar.

STOPPING Encerrando. Aguardar.

STOPPED Encerrada — estado terminal. Liberar recursos; nova sessão se preciso.

Fonte: autoria própria.

Três regras importam mais que tudo:

- start() e stop() são fire-and-forget: retornam na hora e o trabalho acontece em background. A confirmação vem observando session.state. Chamar start() fora de IDLE, ou stop() em sessão já parada, é no-op.

- STOPPED é terminal. Sessão parada não renasce: crie outra com createSession.

- Erros chegam por um flow separado, session.errors: SharedFlow<DeviceSessionError> (ex.: DEVICE_DISCONNECTED, DEVICE_POWERED_OFF).

Observe state e errors juntos.

O sistema pode mudar o estado sem seu app pedir: um gesto do usuário abre outra experiência, outro app inicia uma sessão, o usuário dobra ou tira os óculos (desconectando o Bluetooth), remove seu app no Meta AI, ou a conectividade entre o Meta AI e os óculos cai. O estado não expõe o motivo da transição — reaja ao estado observado sem presumir a causa.

#### 13.4.3.6 Pause e resume

Quando a sessão vai para PAUSED:

- A conexão com o dispositivo continua viva.

- Os streams param de entregar dados enquanto durar a pausa.

- A retomada é do sistema: a sessão volta a STARTED (ou vai a STOPPED) sozinha.

Atenção! Não tente reiniciar uma sessão enquanto ela está pausada. O padrão correto é: em PAUSED, segure o processamento e aguarde; em STARTED, retome; em STOPPED, libere recursos e ofereça ao usuário a opção de reconectar.

#### 13.4.3.7 Disponibilidade do dispositivo

Antes de criar sessão — e para reagir a desconexões — monitore os metadados do dispositivo:

- Wearables.devicesMetadata[deviceId] expõe um StateFlow com metadados, incluindo o campo linkState (LinkState: CONNECTED / CONNECTING / DISCONNECTED).

- Wearables.getDeviceState(deviceId) expõe um StateFlow<DeviceState> com a saúde do dispositivo (ex.: nível térmico). O estado da sessão vem de session.state (visto acima) — não existe um método de estado de sessão por dispositivo no DAT 0.8.

Nota 31: o estado da sessão é único — session.state: StateFlow<DeviceSessionState> (IDLE/STARTING/STARTED/PAUSED/STOPPING/STOPPED). O DAT 0.8 não tem um "estado de sessão por dispositivo"; a saúde do dispositivo (térmica) vem de Wearables.getDeviceState(deviceId): StateFlow<DeviceState>.

O caso físico mais comum: dobrar as hastes dos óculos desconecta o Bluetooth, para os streams ativos e força a sessão para STOPPED. Reabrir as hastes

restaura o Bluetooth quando os óculos estão por perto, mas não recria a sessão — seu app deve criar uma nova quando o dispositivo voltar a ficar disponível.

Na prática: no hackathon, isso acontece na demo: alguém guarda os óculos na capa entre um teste e outro, e o app "para de funcionar". Um app bem-feito observa devicesMetadata e, quando available volta a true, mostra um botão de reconexão (ou reconecta sozinho). É a diferença entre uma demo que trava e uma que se recupera na frente da banca.

### 13.4.4 Na prática

Vamos montar o esqueleto de registro + sessão de um app companion. Os trechos assumem as dependências e o manifest do tópico 2.3 e usam viewModelScope (tópico 1.2).

#### 13.4.4.1 Passo 1 — Inicializar o SDK na Application

▶ Código 2.4-02 – GlassesApp.kt — registre esta classe no AndroidManifest (android:name=".GlassesA — código completo no notebook companion (seção 2.4.4.1).

Erro comum: inicializar dentro de uma Activity. Se o processo for recriado em outra tela (ou um WorkManager acordar o app), alguma chamada ao SDK acontece antes do initialize e você recebe WearablesException/NOT_INITIALIZED. Na Application, a ordem é garantida.

#### 13.4.4.2 Passo 2 — Observar registro e dispositivos

▶ Código 2.4-03 – RegistrationViewModel.kt — código completo no notebook companion (seção 2.4.4.2).

Como registrationState é um StateFlow, você recebe o valor atual imediatamente ao coletar — a UI já nasce no estado certo.

#### 13.4.4.3 Passo 3 — Disparar registro e desregistro

▶ Código 2.4-04 – Chamados a partir de cliques na UI; precisam de uma Activity, — código completo no notebook companion (seção 2.4.4.3).

Repare que não há callback nem valor de retorno: o resultado do fluxo é processado automaticamente pelo SDK e aparece no registrationState que você já observa no Passo 2. É o padrão dispare e observe o estado, que se repete na sessão.

#### 13.4.4.4 Passo 4 — Criar a sessão e observar estado + erros

▶ Código 2.4-05 – SessionViewModel.kt — código completo no notebook companion (seção 2.4.4.4).

Erro comum: tratar start() como operação síncrona e chamar addStream na linha seguinte. Como a sessão ainda está em STARTING, você recebe erro — o stream só pode ser adicionado depois de STARTED (adicionar com a sessão em IDLE retorna DeviceSessionError.SESSION_IDLE). A solução é reagir à transição para STARTED dentro do collect, como acima.

#### 13.4.4.5 Passo 5 — Anexar um Stream à sessão

▶ Código 2.4-06 – Dentro de onSessionLive(), com a sessão em STARTED — código completo no notebook companion (seção 2.4.4.5).

Só um stream por sessão: uma segunda chamada retorna DeviceSessionError.CAPABILITY_ALREADY_ADDED (remova antes com removeStream()). Configuração de qualidade, frame rate e o processamento dos frames são assunto do tópico 2.5.

#### 13.4.4.6 Passo 6 — Monitorar a disponibilidade do dispositivo

▶ Código 2.4-07 – Visão por dispositivo: útil para decidir QUANDO criar/recriar a sessão — código completo no notebook companion (seção 2.4.4.6).

Na prática: sem óculos físicos na equipe, todo esse fluxo — registro, estados da sessão, pause e desconexão — pode ser exercitado com o Mock Device Kit, que inclusive simula transições de estado. É o assunto do tópico 2.7; o código acima permanece o mesmo, o que muda é o dispositivo por trás.

### 13.4.5 Resumo / cheatsheet

- Wearables.initialize(context): uma vez por processo, na Application; API chamada antes disso → NOT_INITIALIZED/WearablesException.

- Registro é feito no app Meta AI: startRegistration(activity)/startUnregistration(activit y); resultado chega via Wearables.registrationState (UNAVAILABLE/AVAILABLE/REGISTERING/REGISTERED/UNREGISTER ING); erros via Wearables.registrationErrorStream.

- Dispositivos visíveis: Wearables.devices (StateFlow<Set<DeviceIdentifier>>); metadados e disponibilidade: Wearables.devicesMetadata[id] (campo linkState).

- createSession(selector) → sessão nasce IDLE; erros: NO_ELIGIBLE_DEVICE, SESSION_ALREADY_EXISTS (uma sessão ativa por dispositivo).

- Ciclo: IDLE → STARTING → STARTED ⇄ PAUSED → STOPPING → STOPPED; STOPPED é terminal — retomar = criar nova sessão.

- start()/stop() são fire-and-forget: confirme pelo session.state; erros vêm por session.errors.

- Em PAUSED: conexão viva, streams sem dados; não reinicie — aguarde STARTED ou STOPPED.

- Dobrar as hastes: Bluetooth cai, streams param, sessão → STOPPED; reabrir restaura o Bluetooth, mas a sessão nova é por sua conta.

### 13.4.6 Referências

- INTEGRATE WEARABLES DEVICE ACCESS TOOLKIT INTO YOUR ANDROID APP / META. Disponível em: https://wearables.developer.meta.com/docs/build-integration-android/. Passo a passo oficial de manifest, Gradle, inicialização, registro, sessão e stream no Android.

- SESSION LIFECYCLE / META. Disponível em: https://wearables.developer.meta.com/docs/lifecycle-events/. Estados da sessão, transições comuns, pause/resume e monitoramento de disponibilidade do dispositivo.

- PERMISSIONS AND REGISTRATION / META. Disponível em: https://wearables.developer.meta.com/docs/permissions-requests/. Modelo de registro via app Meta AI, comportamento multi-dispositivo e regras do Developer Mode.

- WEARABLES OBJECT — API REFERENCE (ANDROID DAT 0.8) / META. Disponível em: https://wearables.developer.meta.com/docs/reference/android/dat/0.6/com_ meta_wearable_dat_core_wearables. Assinaturas de initialize, startRegistration, startUnregistration, createSession, registrationState, devices e getDeviceState.

- SESSION CLASS — API REFERENCE (ANDROID DAT 0.8) / META. Disponível em: https://wearables.developer.meta.com/docs/reference/android/dat/0.6/com_ meta_wearable_dat_core_session_session. Ciclo DeviceSessionState, semântica fire-and-forget de start/stop, errors e addStream/removeStream.

- META-WEARABLES-DAT-ANDROID / META (GITHUB). Disponível em: https://github.com/facebook/meta-wearables-dat-android. Repositório oficial com o sample app Android completo para comparar com sua implementação.

#### 13.4.6.1 Para ir além

MOCK DEVICE KIT BASICS / META. Disponível em: https://wearables.developer.meta.com/docs/mock-device-kit/. Como simular dispositivo e transições de sessão sem hardware (base para o tópico 2.7).

SETUP / META. Disponível em: https://wearables.developer.meta.com/docs/getting-startedtoolkit/. Requisitos de versão do app Meta AI, firmware dos óculos e Developer Mode.
