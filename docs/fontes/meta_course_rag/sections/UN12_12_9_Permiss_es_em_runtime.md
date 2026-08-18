---
unit: 12
unit_title: "Curso de Kotlin/Android"
section: "12.9"
section_title: "Permissões em runtime"
source_file: "Un12_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN12_Curso_de_Kotlin_Android.md"
source_pages: [32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42]
language: "pt-BR"
---

## 12.9 Permissões em runtime

### 12.9.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar a diferença entre declarar uma permissão no AndroidManifest.xml e solicitá-la em runtime, e por que as duas

etapas são obrigatórias para permissões perigosas.

- Implementar o fluxo completo de solicitação de permissões com ActivityResultContracts, incluindo checagem prévia e exibição de justificativa (rationale).

- Tratar os três desfechos possíveis de um pedido: concessão, negação simples e negação permanente — direcionando o usuário às configurações do app quando necessário.

- Configurar as permissões exigidas pelo app companion dos óculos: câmera (CAMERA), microfone (RECORD_AUDIO) e Bluetooth (BLUETOOTH_CONNECT/BLUETOOTH_SCAN).

### 12.9.2 Pré-requisitos

- Tópico 1.1 (Introdução ao Kotlin): lambdas e funções de extensão.

- Tópico 1.3 (Fundamentos de Android): Activity, lifecycle e o papel do AndroidManifest.xml.

- Android Studio atualizado e um dispositivo físico ou emulador com Android 12 (API 31) ou superior — recomendado, pois é a partir dessa versão que valem as permissões de Bluetooth que usaremos.

### 12.9.3 Conceito / fundamentação

#### 12.9.3.1 O modelo em dois tempos: manifesto e runtime

O Android trata permissões em dois momentos distintos. No manifesto, o app declara tudo o que pretende usar — é como a lista de acessos que uma empresa entrega à portaria do prédio: sem constar na lista, nem adianta pedir. Em runtime, para as permissões consideradas sensíveis, o app precisa pedir a autorização ao usuário no momento do uso — é o crachá que a portaria só libera com a sua assinatura, na hora.

Esse modelo existe desde o Android 6.0 (API 23). Antes disso, todas as permissões eram concedidas na instalação; hoje, as sensíveis exigem consentimento explícito, e o usuário pode revogá-las a qualquer momento nas

configurações. Consequência direta para o seu código: nunca assuma que uma permissão concedida ontem continua concedida hoje — cheque sempre antes de usar o recurso.

#### 12.9.3.2 Permissões normais x perigosas

- Normais (ex.: INTERNET): risco baixo. Basta declarar no manifesto; o sistema concede automaticamente na instalação.

- Perigosas (ex.: CAMERA, RECORD_AUDIO): dão acesso a dados ou hardware sensíveis. Exigem declaração no manifesto e pedido em runtime.

- Permissões perigosas são organizadas em grupos (câmera, microfone, "dispositivos por perto"...). O diálogo do sistema é exibido por permissão solicitada, mas o agrupamento influencia como o usuário vê o pedido na tela de configurações.

#### 12.9.3.3 O fluxo de solicitação

O fluxo correto tem quatro decisões encadeadas (Figura 7):

1. Checar: a permissão já foi concedida? Se sim, use o recurso direto — não exiba diálogo à toa.

2. Justificar (rationale): se o usuário já negou uma vez, o sistema sinaliza que vale explicar por que o app precisa daquilo antes de pedir de novo.

3. Pedir: dispare o diálogo do sistema.

4. Tratar o resultado: concedida → prossiga; negada → degrade a experiência com elegância ou explique o impacto; negada permanentemente → oriente o usuário a habilitar manualmente nas configurações.

**Figura 7 – Fluxo de solicitação de permissão em runtime**

Fonte: autoria própria.

#### 12.9.3.4 Negação simples, negação permanente e "apenas desta vez"

Três desfechos negativos ou parciais merecem atenção:

- Negação simples: o usuário tocou "Não permitir" uma vez. Você ainda pode pedir de novo (idealmente após explicar o motivo).

- Negação permanente: a partir do Android 11 (API 30), se o usuário nega a mesma permissão repetidamente, o sistema passa a não exibir mais o diálogo — o pedido retorna negado na hora, silenciosamente. Em versões anteriores, isso acontecia via checkbox "Não perguntar novamente". Não existe API para "desnegar": o único caminho é o usuário habilitar manualmente em Configurações > Apps.

- "Apenas desta vez": no Android 11+, para câmera, microfone e localização o usuário pode conceder acesso temporário. A permissão é revogada automaticamente depois que o app sai de primeiro plano por um tempo — mais um motivo para checar antes de cada uso, e nunca cachear o resultado.

Nota11: o Android 11+ também revoga automaticamente permissões de apps que ficam meses sem uso (auto-reset). Para um app de hackathon isso raramente aparece, mas explica por que apps "perdem" permissões com o tempo.

#### 12.9.3.5 As permissões do programa dos óculos

O app companion conversa com os óculos via Bluetooth e processa visão e voz on-device — como visto no 1.3, ele é um app Android comum, então está sujeito a exatamente este modelo de permissões. As que nos interessam estão descritas na

**Tabela 5.**

**Tabela 5 – Permissões Android utilizadas pelo aplicativo companion Permissão Para que serve no programa Tipo**

CAMERA Perigosa — runtime

Acesso à câmera (no telefone, a câmera local; o acesso ao stream da câmera dos óculos via SDK será visto no tópico 2.5)

RECORD_AUDIO Perigosa — runtime

Captura de áudio, incluindo áudio que chega por Bluetooth a partir do array de microfones dos óculos (detalhes no tópico 2.6)

BLUETOOTH_CONNECT Conectar-se a dispositivos pareados — é ela que permite Perigosa — runtime, grupo "Dispositivos por perto"

falar com os óculos (Android 12+)

BLUETOOTH_SCAN Procurar dispositivos Bluetooth próximos (Android 12+) Perigosa — runtime, grupo "Dispositivos por perto"

Equivalentes legadas para Android 11 ou inferior BLUETOOTH / BLUETOOTH_ADMIN Normais — só manifesto (com maxSdkVersion="30")

Fonte: autoria própria.

Antes do Android 12, escanear Bluetooth exigia também permissão de localização (ACCESS_FINE_LOCATION), porque beacons permitem inferir posição. No Android 12+, declarar BLUETOOTH_SCAN com a flag neverForLocation elimina essa exigência.

O app companion vive sob dois modelos de permissão que funcionam em paralelo:

1. Permissões do Android (o assunto deste tópico). O DAT exige apenas BLUETOOTH, BLUETOOTH_CONNECT e INTERNET. Note o que não está na lista: consumir a câmera e os microfones dos óculos não exige CAMERA nem RECORD_AUDIO no telefone, porque quem fala com o hardware é o app Meta AI — o seu app recebe os dados já entregues pelo toolkit. CAMERA só entra se você usar a câmera do próprio celular como feed do Mock Device (tópico 2.7); RECORD_AUDIO só entra no caminho de áudio por Bluetooth com AudioRecord (tópico 2.6).

2. Permissões do toolkit, concedidas dentro do app Meta AI e não pelo diálogo do Android. Elas exigem que seu app esteja registrado primeiro — sem registro, o pedido falha; com registro mas sem permissão, o app conecta e não acessa a câmera. E o usuário escolhe entre permitir uma vez (temporário) ou permitir sempre (persistente): o mesmo dilema do "Apenas desta vez" que você viu acima, com a mesma consequência prática — cheque antes de usar, nunca cacheie. O fluxo completo é o tópico 2.4.

A lição deste tópico vale para as duas camadas: o mecanismo é sempre checar → justificar → pedir → tratar o desfecho. O que muda é quem exibe o diálogo.

Na prática: no dia do hackathon, a primeira coisa que seu app faz é conectar-se aos óculos. Se BLUETOOTH_CONNECT não tiver sido concedida, a conexão falha com SecurityException antes de qualquer modelo de IA rodar. Trate as permissões como o "portão de entrada" da sua demo: peça todas no onboarding, valide antes de cada fluxo e tenha uma tela de recuperação para o caso de negação.

### 12.9.4 Na prática

Vamos montar o fluxo completo em uma Activity: declarar, checar, justificar, pedir e tratar todos os desfechos.

#### 12.9.4.1 Passo 1 — Declarar no manifesto

▶ Código 1.4-01 – AndroidManifest.xml, dentro de <manifest> — código completo no notebook companion (seção 1.4.4.1).

Atenção! Esquecer a declaração no manifesto é o erro mais silencioso deste capítulo. O pedido em runtime não quebra — o diálogo simplesmente nunca aparece e o resultado volta negado na hora. Se o seu pop-up "não abre", confira o manifesto antes de qualquer outra coisa.

#### 12.9.4.2 Passo 2 — Função de checagem

▶ Código 1.4-02 – código completo no notebook companion (seção 1.4.4.2).

#### 12.9.4.3 Passo 3 — Montar a lista de permissões do programa

As permissões de Bluetooth só existem a partir da API 31, então a lista depende da versão do sistema:

▶ Código 1.4-03 – código completo no notebook companion (seção 1.4.4.3).

#### 12.9.4.4 Passo 4 — Registrar o launcher e tratar o resultado

A API moderna é registerForActivityResult com o contrato RequestMultiplePermissions. O registro precisa acontecer na inicialização da Activity (como propriedade), antes de ela chegar ao estado STARTED:

▶ Código 1.4-04 – código completo no notebook companion (seção 1.4.4.4).

XXX:Atenção! Dentro do callback de resultado, shouldShowRequestPermissionRationale(permissao) retornando false após uma negação é o sinal de negação permanente. Não existe método isPermanentlyDenied() no framework — essa combinação (negado + rationale falso) é a forma padrão de detectar.

#### 12.9.4.5 Passo 5 — Checar, justificar e pedir

▶ Código 1.4-05 – código completo no notebook companion (seção 1.4.4.5). O diálogo de rationale é um diálogo seu (um AlertDialog simples resolve), explicando em uma frase por que o app precisa do acesso — por exemplo: "Precisamos do Bluetooth para conectar aos seus óculos e do microfone para ouvir seus comandos de voz".

#### 12.9.4.6 Passo 6 — Tratar a negação permanente

Quando a negação é permanente, chamar launch() de novo é inútil: o resultado volta negado sem diálogo. O único caminho é levar o usuário à tela de detalhes do app:

▶ Código 1.4-06 –código completo no notebook companion (seção 1.4.4.6).

Na UI, mostre uma mensagem clara ("Permissão de Bluetooth desativada. Habilite em Configurações para conectar aos óculos") com um botão que chama openAppSettings(). Ao voltar da tela de configurações, cheque as permissões de novo (por exemplo, no onResume() ou ao reentrar no fluxo) — o usuário pode ou não ter habilitado.

Atenção! Negação permanente é o caso que mais derruba demo de hackathon! Durante os testes, é comum negar o diálogo algumas vezes "só para ver" — e aí o pop-up para de aparecer para sempre naquele aparelho, sem nenhum aviso. Se as permissões "sumiram", vá em Configurações > Apps > seu app > Permissões e habilite manualmente, ou reinstale o app para zerar o estado. No código, sempre implemente o caminho de recuperação via openAppSettings().

#### 12.9.4.7 Erros comuns

- Diálogo nunca aparece, resultado sempre negado → permissão fora do manifesto (ou negação permanente já ativada). Confira o manifesto primeiro; depois, o estado em Configurações.

- IllegalStateException ao registrar o launcher → registerForActivityResult foi chamado tarde demais (ex.: dentro de um click listener). Registre sempre como propriedade da Activity/Fragment.

- SecurityException ao conectar no Android 12+ → BLUETOOTH_CONNECT declarada mas não concedida em runtime, ou ausente do manifesto. As permissões legadas BLUETOOTH/BLUETOOTH_ADMIN não valem na API 31+.

- Permissão "some" entre sessões → o usuário concedeu "Apenas desta vez". Nunca cacheie o resultado; cheque com hasPermission() a cada uso.

Nota 12: em Jetpack Compose, o equivalente é rememberLauncherForActivityResult(ActivityResultContracts.R equestMultiplePermissions()) { ... } — a lógica de checagem, rationale e tratamento é idêntica. Na prática: estruture o app companion para pedir as permissões em uma tela de onboarding dedicada, antes da primeira tentativa de conexão com os óculos — e não espalhadas pelo app. Assim, quando o fluxo de registro e sessão do SDK entrar em cena (tópico 2.4), o terreno já está limpo, e a banca do hackathon nunca verá um pop-up de permissão no meio da demo.

### 12.9.5 Resumo / cheatsheet

- Permissão perigosa = declarar no manifesto e pedir em runtime (desde o Android 6.0/API 23); só manifesto não basta.

- Fluxo: checkSelfPermission → rationale (se já negou) → launch() → tratar resultado.

- API moderna: registerForActivityResult(ActivityResultContracts.Reque stMultiplePermissions()), registrado como propriedade da Activity.

- Permissões do programa: CAMERA, RECORD_AUDIO, BLUETOOTH_CONNECT e BLUETOOTH_SCAN (Android 12+; legadas BLUETOOTH/BLUETOOTH_ADMIN com maxSdkVersion="30").

- BLUETOOTH_SCAN com neverForLocation dispensa permissão de localização no Android 12+.

- Negação permanente: negado + shouldShowRequestPermissionRationale == false → diálogo não abre mais; recuperação só via Configurações (ACTION_APPLICATION_DETAILS_SETTINGS).

- "Apenas desta vez" (Android 11+) revoga câmera/microfone automaticamente — cheque a permissão antes de cada uso, nunca cacheie.

- Permissão fora do manifesto = pedido negado silenciosamente, sem diálogo.

### 12.9.6 Referências

- PERMISSIONS ON ANDROID / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/guide/topics/permissions/overview — visão geral do modelo de permissões: tipos, grupos e ciclo de vida.

- REQUEST RUNTIME PERMISSIONS / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/training/permissions/requesting — fluxo oficial de solicitação com ActivityResultContracts, rationale e tratamento de negação.

- BLUETOOTH PERMISSIONS / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/develop/connectivity/bluetooth/bt-permissions

— BLUETOOTH_CONNECT/BLUETOOTH_SCAN, flag neverForLocation e compatibilidade com versões antigas.

#### 12.9.6.1 Para ir além

APP PERMISSIONS BEST PRACTICES / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/training/permissions/usage-notes — boas práticas de UX: quando pedir, como explicar e como degradar sem a permissão.

META WEARABLES DEVELOPER CENTER. Disponível em: https://developers.meta.com/wearables — documentação oficial do Device Access Toolkit, incluindo requisitos do app companion.
