---
unit: 13
unit_title: "Curso do Meta SDK (DAT)"
section: "13.7"
section_title: "Desenvolvimento sem hardware (Mock Device Kit)"
source_file: "Un13_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN13_Curso_do_Meta_SDK_DAT.md"
source_pages: [55, 56, 57, 58, 59, 60, 61, 62, 63]
language: "pt-BR"
---

## 13.7 Desenvolvimento sem hardware (Mock Device Kit)

### 13.7.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar o que é o Mock Device Kit e como ele se encaixa na pilha do Device Access Toolkit (DAT) sem exigir mudanças no código do app.

- Configurar e parear um dispositivo simulado, controlando seu estado (ligado, vestido, aberto/fechado).

- Simular permissões do toolkit e streaming de mídia usando arquivo de vídeo, câmera do celular ou imagem de captura.

- Organizar o fluxo de trabalho da fase online do hackathon para desenvolver e testar a solução inteira antes de receber os óculos.

### 13.7.2 Pré-requisitos

- Tópicos 2.3 (Setup do projeto com o DAT), 2.4 (Registro e sessão) e 2.5 (Câmera e streaming) — o Mock Device Kit simula exatamente esses fluxos.

- Tópico 1.4 (Permissões em runtime) — a diferença entre permissões do Android e permissões do toolkit reaparece aqui.

- Android Studio Flamingo ou mais recente e um celular Android 10+ para rodar o app companion. Verificar: requisitos de versão na página de Setup oficial, pois o toolkit está em developer preview.

### 13.7.3 Conceito / fundamentação

#### 13.7.3.1 O que é o Mock Device Kit

O Mock Device Kit (MDK) é um componente do Device Access Toolkit que permite construir e testar integrações com os óculos Meta sem o hardware real. Ele fornece um dispositivo simulado que espelha as capacidades e o comportamento dos óculos: streaming de câmera, captura de foto, permissões e mudanças de estado do dispositivo.

A analogia útil é a de um dublê de cinema: para a câmera (o seu código), o dublê se comporta como o ator principal. Quando o MDK está habilitado, ele simula a pilha inteira do SDK — inclusive a conexão do app (o registro visto no 2.4) e as solicitações de permissão (Figura 25). Seu código funciona do mesmo jeito falando

com um dispositivo real ou com um mock: você não escreve uma versão "de mentira" do app; escreve o app definitivo e apenas troca a fonte do dispositivo.

**Figura 25 – A mesma pilha, duas bases: hardware real e Mock Device Kit**

Fonte: autoria própria.

#### 13.7.3.2 Por que isso é crítico no cronograma do AI Glasses Brasil

Os óculos Ray-Ban Meta só chegam às equipes na etapa presencial. Ou seja: toda a fase online do programa acontece sem o hardware. Sem o MDK, as equipes só poderiam começar a integração real no presencial — tarde demais para um hackathon.

Com o MDK, a fase online deixa de ser "estudo teórico" e vira desenvolvimento de verdade: você implementa registro, sessão, streaming, os modelos de IA on-device (Módulo 1) e a UI, tudo validado contra o dispositivo

simulado. No presencial, o trabalho que sobra é trocar o mock pelos óculos reais e ajustar o que só o hardware revela (latência real do Bluetooth, iluminação da câmera, consumo de bateria).

Na prática: planeje a divisão do time assumindo que a solução inteira será desenvolvida sem hardware: quem cuida do pipeline de visão (1.7) usa um vídeo H.265 como feed simulado; quem cuida de voz (1.8) desenvolve com o microfone do próprio celular; quem cuida da integração DAT valida sessão e permissões contra o mock. No dia do presencial, o "Start streaming" que funcionava com o mock deve funcionar com os óculos sem mudar uma linha da lógica do app.

#### 13.7.3.3 O que o MDK simula (e o que não simula)

O dispositivo simulado cobre quatro áreas:

- Estado do dispositivo — ligar/desligar (powerOn/powerOff), vestir/tirar (don/doff) e abrir/fechar as hastes (unfold/fold). Esses eventos são os mesmos que o SDK observa num dispositivo real (wear detection, hinge events).

- Registro e permissões — ao habilitar o MDK, o estado de registro do app já transita para Registered por padrão, e você pode configurar respostas de permissão (concedida, negada) para testar os dois caminhos.

- Mídia — o feed da câmera dos óculos é substituído por um arquivo de vídeo H.265 ou pela câmera do próprio celular; a captura de foto retorna uma imagem que você define.

- Gestos de toque — simulação de tap e tap-and-hold na haste: durante um stream ativo, tap alterna pausa/retomada e tap-and-hold encerra a sessão.

O que o mock não substitui: características físicas do hardware real — qualidade óptica da câmera ultra-wide, comportamento do array de microfones com beamforming, autonomia de bateria e a instabilidade real do link Bluetooth.

E há uma ausência que não é lacuna, e sim consequência da arquitetura: o MDK não simula captura de áudio. Faz todo sentido — como visto no 2.6, o

microfone e os alto-falantes dos óculos não passam pelo DAT, e sim pelos perfis de áudio Bluetooth do Android. O MDK simula a pilha do DAT; áudio nunca esteve nela, então não há o que simular. A documentação oficial lista exatamente o que o dispositivo simulado espelha — streaming de câmera, captura de foto, permissões, estado do dispositivo e gestos de toque — e áudio não aparece.

Consequência prática para a fase online: desenvolva o pipeline de voz com um fone Bluetooth comum com suporte a HFP, como recomendado no 2.6.

#### 13.7.3.4 Duas formas de usar

Há dois caminhos complementares:

1. UI de debug do sample CameraAccess — o app de exemplo oficial traz uma folha de controles do MDK (disponível apenas em debug builds, as compilações de desenvolvimento). É o jeito mais rápido de ver o mock funcionando sem escrever código.

2. API do MDK no seu próprio app/testes — a classe MockDeviceKit é o ponto de entrada programático: você habilita o mock, pareia dispositivos e configura mídia via código, inclusive em testes instrumentados.

### 13.7.4 Na prática

#### 13.7.4.1 Caminho 1 — Explorar o MDK pela UI de debug do sample

Pré-condição: o repositório oficial meta-wearables-dat-android clonado e o sample CameraAccess compilando (setup do 2.3).

1. Habilite o MDK: rode o sample em modo debug, toque no ícone de debug (joaninha) na tela inicial e toque em Enable MockDeviceKit. Isso ativa o sistema de mock, que simula conexão e permissões — o sample completa o registro automaticamente e o app passa da etapa de conexão sem óculos e sem o app Meta AI.

2. Pareie um dispositivo simulado: no menu de debug, toque em Pair RayBan Meta. Um card do dispositivo mock aparece na folha. É possível parear até 3 dispositivos simultâneos; Unpair remove.

3. Mude o estado do dispositivo pelos toggles do card: Power (liga), Don (veste — e já abre as hastes automaticamente), Unfold (abre as hastes). O dispositivo precisa estar ligado e vestido para o streaming começar.

4. Configure a fonte de mídia expandindo o card: no camera source picker, escolha Front Camera/Back Camera (feed ao vivo da câmera do celular) ou Video File (vídeo da galeria). Para captura de foto, Select image define a imagem retornada — com câmera do celular como feed, a captura tira um still ao vivo e dispensa essa configuração.

5. Inicie o streaming: feche a folha de debug e toque em Start streaming. O feed configurado aparece como se viesse dos óculos.

6. Simule gestos: use os botões Tap (pausa/retoma o stream) e Tap and Hold (encerra a sessão) no card do dispositivo.

Atenção! O MDK exige vídeos no formato H.265 (HEVC). O sample Android não converte automaticamente. Se o stream não inicia ou fica preto com um vídeo da galeria, transcode antes com FFmpeg — a doc oficial sugere, por exemplo:

ffmpeg -i entrada.mp4 -c:v libx265 -tag:v hvc1 -vf "scale=540:960" saida.mov

**Código 2.7-01 · também no notebook companion, seção 2.7.4.1**

#### 13.7.4.2 Caminho 2 — Usar a API do MDK no seu app

O fluxo programático segue a mesma lógica da UI. Código baseado nos exemplos da API reference (Android DAT 0.8.0; confira a versão atual na doc).

▶ Código 2.7-02 – código completo no notebook companion (seção 2.7.4.2).

Pontos que merecem destaque:

- enable() aceita um MockDeviceKitConfig; com initiallyRegistered = false o app começa não registrado, o que permite testar a UI do fluxo de registro do 2.4.

- Para testar o caminho de permissão negada sem mexer no app:

▶ Código 2.7-03 – checkPermissionStatus(CAMERA) passará a responder Denied — código completo no notebook companion (seção 2.7.4.2).

- Em testes instrumentados, o padrão oficial é um caso de teste base que obtém o MockDeviceKit no @Before, concede as permissões Android via shell (pm grant ... BLUETOOTH_CONNECT e CAMERA) e chama mockDeviceKit.disable() no @After (e enable() no @Before) para limpar o estado entre testes. O guia de testes usa device.services.camera (não há getCameraKit no 0.8).

Na prática: estruture seu app para que o MDK fique atrás de uma flag de debug (por exemplo, habilitar o mock apenas quando BuildConfig.DEBUG for true, como faz o sample). Assim, o mesmo APK que a equipe usa a fase online inteira vira o APK do presencial: desligou o mock, a sessão real (createSession → start → addStream) passa a receber os óculos reais e todo o pipeline de IA construído no Módulo 1 continua funcionando sem alteração.

#### 13.7.4.3 Erros comuns

- "Start streaming" não habilita / dispositivo não aparece → o mock está pareado, mas não está ligado e vestido. Chame powerOn() e don() (ou ligue os toggles Power e Don na UI de debug).

- Stream não inicia com arquivo de vídeo → o vídeo não está em H.265. Transcode com FFmpeg; prefira arquivos pequenos, pois vídeos grandes elevam o uso de memória.

- Feed da câmera do celular falha → a permissão Android CAMERA não foi concedida em runtime (isso é permissão do Android, não do toolkit — a distinção do 1.4).

- Foto capturada aparece "deitada" → comportamento documentado: a imagem retornada pelo mock é rotacionada 90° para reproduzir o dispositivo real. Trate a rotação no seu pipeline, não como bug.

### 13.7.5 Resumo / cheatsheet

- Mock Device Kit = dispositivo simulado que espelha os óculos Meta: estado, registro, permissões, streaming, captura e gestos de toque — sem hardware.

- O código do app é o mesmo com mock ou dispositivo real; o MDK troca a base da pilha do SDK, não a sua lógica.

- Crítico no AI Glasses Brasi: os óculos só chegam no presencial — toda a fase online roda sobre o MDK.

- Fluxo mínimo: getInstance(context) → enable() → pairGlasses(GlassesModel.RAYBAN_META) → powerOn() + don() → configurar mídia → sessão normal do SDK.

- Streaming só inicia com o dispositivo ligado e vestido; até 3 mocks pareados ao mesmo tempo.

- Mídia simulada: vídeo em H.265 (obrigatório), câmera do celular (frontal/traseira, exige permissão Android CAMERA) ou imagem fixa para captura (retorna rotacionada 90°).

- Permissões do toolkit: permissions.set(...) controla o status consultado; setRequestResult(...) controla a resposta da solicitação (padrão: Granted).

- disable() restaura a pilha real e limpa o estado — use enable()/disable() entre testes (não há reset() no 0.8).

### 13.7.6 Referências

- MOCK DEVICE KIT — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/develop/dat/mock-device-kit/. Visão geral do kit e passo a passo da UI de debug do sample CameraAccess (parear, estados, mídia, gestos).

- HOW TO TEST WITH MOCK DEVICE KIT ON ANDROID — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/develop/dat/testing-mdk-androi d/. Padrões para testes instrumentados: caso de teste base, permissões via shell, feed e captura mockados.

- MOCKDEVICEKIT CLASS (ANDROID DAT API REFERENCE) — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/reference/android/dat/0.6/com_ meta_wearable_dat_mockdevice_mockdevicekit. API do ponto de entrada: enable/disable, pairGlasses, permissions, pairedDevices.

- META-WEARABLES-DAT-ANDROID (REPOSITÓRIO OFICIAL) — META/GITHUB. Disponível em: https://github.com/facebook/meta-wearables-dat-android. Código do sample CameraAccess com a UI de debug do MDK.

#### 13.7.6.1 Para ir além

MOCKCAMERAKIT INTERFACE — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/reference/android/da t/0.6/com_meta_wearable_dat_mockdevice_api_camera_mockcamerak it. Detalhes de setCameraFeed (arquivo × câmera do celular) e setCapturedImage.

MOCKRAYBANMETA INTERFACE — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/reference/android/da t/0.6/com_meta_wearable_dat_mockdevice_api_mockraybanmeta. Todos os métodos de estado do dispositivo simulado (don/doff, fold/unfold, powerOn/powerOff).

MOCKPERMISSIONS INTERFACE — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/reference/android/dat/0.6/c om_meta_wearable_dat_mockdevice_api_permissions_mockpermissio ns. Controle fino das respostas de permissão do toolkit.
