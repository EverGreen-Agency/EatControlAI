---
unit: 12
unit_title: "Curso de Kotlin/Android"
section: "12.10"
section_title: "Background e eficiência de energia"
source_file: "Un12_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN12_Curso_de_Kotlin_Android.md"
source_pages: [42, 43, 44, 45, 46, 47, 48, 49, 50, 51]
language: "pt-BR"
---

## 12.10 Background e eficiência de energia

### 12.10.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar as restrições do Android para execução em segundo plano e por que elas existem.

- Comparar foreground services e WorkManager e escolher o mecanismo certo para cada tarefa do app companion.

- Implementar um foreground service com o tipo correto e uma tarefa adiável com WorkManager e constraints.

- Explicar o throttling térmico e por que a inferência "em rajada" (bursty) supera a inferência contínua em bateria e desempenho.

- Aplicar boas práticas de economia de energia em pipelines de IA on-device e medir o consumo do seu app.

### 12.10.2 Pré-requisitos

- Tópico 1.2 — Coroutines e Flow (usaremos CoroutineWorker, delay e operadores de Flow).

- Tópico
1.3
—
Fundamentos
de
Android
(components, lifecycle, notificações).

- Tópico 1.4 — Permissões em runtime (POST_NOTIFICATIONS e permissões de microfone já vistas lá).

- Android Studio atualizado, com um projeto usando targetSdk 34 ou superior.

### 12.10.3 Conceito / fundamentação

#### 12.10.3.1 O problema: seu app não está sempre na tela

No hackathon, o app companion é o cérebro da solução: os óculos capturam imagem e áudio, enviam via Bluetooth, e é o celular que roda os modelos de IA. Só que o usuário não vai ficar com o app aberto na tela enquanto caminha pela rua — o celular estará no bolso, com a tela apagada. Se o seu pipeline morrer quando o app sai de primeiro plano, a solução inteira para de funcionar.

Ao mesmo tempo, o Android limita agressivamente o que apps podem fazer em segundo plano, justamente para proteger a bateria. Esse é o equilíbrio deste tópico: manter o pipeline vivo e gastar o mínimo de energia possível.

#### 12.10.3.2 O que o Android faz com apps em segundo plano

Desde o Android 8, o sistema impõe limites severos: processos em background podem ser encerrados a qualquer momento, e serviços comuns iniciados em background são proibidos. Além disso, quando o aparelho fica parado com a tela apagada, entra em Doze mode: o sistema adia acesso à rede, jobs e alarmes para janelas curtas de manutenção. O App Standby completa o cerco, rebaixando apps pouco usados para "buckets" com menos direitos de execução.

A consequência prática: você não escolhe "rodar em background" livremente. Você escolhe entre dois mecanismos sancionados pelo sistema — foreground service ou WorkManager — de acordo com a natureza da tarefa.

#### 12.10.3.3 Foreground service: trabalho contínuo e visível

Um foreground service é um serviço que roda com uma notificação permanente visível ao usuário. Em troca dessa transparência, o sistema o trata

como quase tão importante quanto o app em primeiro plano: ele não é adiado pelo Doze e raramente é morto.

Use foreground service quando a tarefa é contínua, iniciada pelo usuário e perceptível por ele: reproduzir áudio, rastrear localização em um treino ou — no nosso caso — manter a sessão com os óculos ativa, recebendo o streaming de áudio via Bluetooth e processando-o em tempo real.

Desde o Android 14 (API 34), todo foreground service precisa declarar um tipo (foregroundServiceType) que descreve o que ele faz — microphone, camera, connectedDevice, dataSync etc. — e a permissão correspondente no manifest. O sistema usa o tipo para aplicar regras específicas: por exemplo, um serviço do tipo microphone só tem acesso ao microfone se for iniciado enquanto o app está em primeiro plano.

#### 12.10.3.4 WorkManager: trabalho adiável e garantido

O WorkManager é a biblioteca do Jetpack para trabalho adiável e garantido: tarefas que não precisam rodar agora, mas precisam rodar em algum momento — mesmo que o app seja fechado ou o aparelho reinicie. Você descreve a tarefa em um Worker, define constraints (só com carregador, só no Wi-Fi, só com bateria não baixa) e o sistema escolhe o melhor momento de execução, respeitando Doze e App Standby.

No contexto do programa, é o mecanismo ideal para: baixar ou atualizar arquivos de modelo, sincronizar resultados com um servidor, limpar caches de frames e gerar relatórios de uso.

#### 12.10.3.5 Como escolher

Na Tabela 6 estão os critérios que orientam a decisão por foreground service ou WorkManager.

**Tabela 6 – Comparação entre Foreground Service e WorkManager Critério Foreground service WorkManager**

Quando roda Agora, continuamente Quando o sistema permitir Usuário percebe? Sim (notificação obrigatória) Não necessariamente

Sobrevive a reboot? Não (precisa ser reiniciado) Sim (trabalho persistido) Adiável? Não Sim (constraints) Exemplo no programa Streaming de áudio dos óculos + STT em tempo real Download noturno de um modelo atualizado

Fonte: autoria própria.

Regra de bolso: se atrasar a tarefa em 10 minutos quebra a experiência, é foreground service; se não quebra, é WorkManager (Figura 8).

**Figura 8 – Foreground service ou WorkManager?**

Fonte: autoria própria.

#### 12.10.3.6 Por que inferência "em rajada" (bursty)

Rodar um modelo de IA é a operação mais cara em energia que o app companion faz. A intuição ingênua — "quanto mais frames por segundo eu processar, melhor o produto" — é uma armadilha dupla:

1. Bateria: cada inferência consome uma quantidade quase fixa de energia. Inferência contínua a 30 fps consome ~30× mais do que 1 inferência por segundo, quase sempre sem ganho real de experiência. 2. Calor: o SoC do celular não tem ventoinha; ele dissipa calor passivamente pela carcaça. Sob carga contínua, a temperatura sobe até o sistema se proteger. A estratégia bursty inverte a lógica: rode o modelo em picos curtos e intensos — quando um gatilho acontece (o usuário fala um comando, uma foto chega dos óculos) — e deixe o hardware ocioso e esfriando no resto do tempo. Isso segue o princípio de race to sleep: terminar o trabalho o mais rápido possível para o chip voltar logo ao estado de baixo consumo, em vez de trabalhar devagar por muito tempo.

Analogia: um corredor consegue dar vários sprints de 10 segundos ao longo de uma hora, com pausas para recuperar. Se tentar correr a velocidade de sprint por uma hora contínua, o corpo entra em colapso e ele termina andando — mais devagar do que se tivesse alternado.

#### 12.10.3.7 Throttling térmico, explicado de forma simples

Quando a temperatura do SoC passa de certos limiares, o sistema reduz a frequência (clock) da CPU/GPU/NPU para gerar menos calor. Isso é o throttling térmico. O efeito em cascata é perverso para IA on-device:

- O clock cai → cada inferência demora mais.

- Inferências mais lentas mantêm o chip ocupado por mais tempo → mais energia gasta por inferência.

- O chip ocupado por mais tempo continua gerando calor → o throttling se aprofunda.

Ou seja: a inferência contínua não só drena a bateria — ela deixa o próprio modelo mais lento com o passar dos minutos. Uma demo que roda a 15 fps no primeiro minuto pode estar a 5 fps no décimo, com o celular quente na mão do jurado.

O Android expõe o estado térmico ao app: PowerManager.getThermalHeadroom() retorna uma previsão de quão perto do limite de throttling severo o aparelho estará (1.0 = no limite), e addThermalStatusListener notifica mudanças de status térmico. Você verá o uso na seção prática.

Na prática: este tópico é o coração do checkpoint de "Eficiência de bateria" do camp. A avaliação vai olhar exatamente para as decisões ensinadas aqui: seu pipeline usa foreground service só para o que é contínuo? O trabalho adiável tem constraints? A inferência dispara por gatilho ou roda em loop cego? Um pipeline contínuo de visão pode derrubar a bateria do celular em poucas horas — e lembre-se de que a bateria dos óculos também é finita e o streaming Bluetooth constante a consome; capturar sob demanda poupa os dois lados do link.

#### 12.10.3.8 Boas práticas de economia para IA on-device

- Gatilho, não loop: dispare inferência por evento (comando de voz, toque, chegada de foto), nunca em while(true).

- Reduza a entrada: menos resolução e menos frames por segundo = menos energia por resultado. Comece pequeno e só aumente se a qualidade exigir.

- Limite a taxa: imponha um teto de inferências por segundo (você implementará com sample de Flow, visto no 1.2).

- Respeite o estado térmico: consulte o thermal headroom antes de bursts pesados e recue quando o aparelho estiver quente.

- Use modelos otimizados: modelos quantizados e delegates de hardware (NPU/GPU) reduzem tempo e energia por inferência — os detalhes foram vistos mais a frente.

- Libere recursos: feche interpretadores, streams de áudio e conexões quando o burst termina.

- Meça, não adivinhe: use o Power Profiler do Android Studio e o Battery Historian para ver onde a energia realmente vai.

### 12.10.4 Na prática

#### 12.10.4.1 Passo 1 — Foreground service para o pipeline de áudio

Cenário: o usuário ativou o modo "assistente" e o app precisa continuar recebendo áudio dos óculos via Bluetooth e transcrevendo, mesmo com a tela apagada.

Manifest — permissões e declaração do serviço com tipos:

▶ Código 1.5-01 – Permissão geral de FGS + permissões por tipo (Android 14+) — código completo no notebook companion (seção 1.5.4.1).

O serviço:

▶ Código 1.5-02 – código completo no notebook companion (seção 1.5.4.1).

E o start, feito enquanto o app está visível (por exemplo, no clique do botão "ativar assistente"):

// Em uma Activity visível: startForegroundService(Intent(this, AudioPipelineService::class.java))

**Código 1.5-03 · também no notebook companion, seção 1.5.4.1**

▶ Nota13: o comportamento dos tipos de FGS depende da versão do Android do aparelho de teste (validado no Android 16).

▶ Atenção! Dois erros clássicos aqui:
1) Tentar iniciar o foreground service com o app em background lança
ForegroundServiceStartNotAllowedException no Android 12+ —
sempre inicie a partir de uma tela visível ou de um gatilho permitido pelo
sistema.
2) Esquecer o foregroundServiceType no manifest ou na chamada de
startForeground gera exceção no Android 14+ — declare nos dois lugares.

#### 12.10.4.2 Passo 2 — WorkManager para o trabalho adiável

Cenário: baixar uma versão atualizada do modelo, mas só com o celular carregando e no Wi-Fi — nunca no meio da demo.

Dependência (build.gradle.kts do módulo):

implementation("androidx.work:work-runtime-ktx:2.11.2")

**Código 1.5-04 · também no notebook companion, seção 1.5.4.2**

O worker e o agendamento:

▶ Código 1.5-05 – código completo no notebook companion (seção 1.5.4.2).

Atenção! Não use WorkManager para nada que o usuário está esperando em tempo real. Com Doze e App Standby, "adiável" pode significar horas de espera — é o comportamento esperado, não um bug.

#### 12.10.4.3 Passo 3 — Inferência bursty com limite de taxa e freio térmico

Cenário: frames chegam dos óculos e você roda um modelo de visão, mas com teto de taxa e recuo quando o aparelho esquenta.

▶ Código 1.5-06 – código completo no notebook companion (seção 1.5.4.3).

Nota 14: getThermalHeadroom exige API 30+ e pode retornar NaN em aparelhos sem suporte; há limite de frequência de chamadas. Trate NaN como "sem informação" e mantenha só o limite de taxa (Validado no Android 16). Na prática: para o checkpoint de bateria, este padrão é o seu argumento técnico: mostre que o app processa sob demanda, com teto de taxa e freio térmico. Uma demo que continua rápida e com o celular frio depois de 10 minutos vale mais do que qualquer slide.

#### 12.10.4.4 Passo 4 — Medir o consumo

Antes de otimizar, meça:

1. Power Profiler (Android Studio): mostra consumo estimado por recurso (CPU, rede, GPU) em tempo real enquanto você exercita o app. 2. Battery Historian: colete com adb shell dumpsys batterystats (após adb shell dumpsys batterystats --reset e uma sessão de uso) e visualize a linha do tempo de wakeups, jobs e serviços do seu app. Compare uma sessão com inferência contínua e outra com o padrão bursty — a diferença aparece na primeira medição.

### 12.10.5 Resumo / cheatsheet

- Android restringe background (Doze, App Standby); os caminhos sancionados são foreground service e WorkManager.

- Foreground service = contínuo, imediato, com notificação; exige foregroundServiceType + permissão por tipo no Android 14+.

- WorkManager = adiável e garantido; use constraints (carregador, Wi-Fi, bateria) e trabalho único nomeado.

- Regra de bolso: atrasar 10 min quebra a experiência? FGS. Não quebra? WorkManager.

- Bursty > contínuo: picos curtos + ociosidade = menos energia e sem throttling (race to sleep).

- Throttling térmico: calor → clock menor → inferência mais lenta → mais calor; contínuo entra nessa espiral.

- Freios no pipeline: gatilho em vez de loop, sample para teto de taxa, getThermalHeadroom para recuo.

- Meça com Power Profiler e Battery Historian antes e depois de otimizar.

### 12.10.6 Referências

- FOREGROUND SERVICES / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/develop/background-work/services/foregroun d-services. Tipos de serviço, permissões por tipo e restrições de start por versão.

- WORKMANAGER / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/topic/libraries/architecture/workmanager. Guia da biblioteca: workers, constraints, trabalho único e periódico.

- BACKGROUND WORK OVERVIEW / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/develop/background-work/background-tasks. Árvore de decisão oficial entre os mecanismos de execução em segundo plano.

- OPTIMIZE FOR DOZE AND APP STANDBY / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/training/monitoring-device-state/doze-standb y. Como Doze e App Standby afetam seu app e como testá-los com adb.

- POWERMANAGER / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/reference/android/os/PowerManager. Referência de getThermalHeadroom e addThermalStatusListener.

- THERMAL API / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/games/optimize/adpf/thermal. Guia de adaptação de carga ao estado térmico (escrito para jogos, vale para inferência).

#### 12.10.6.1 Para ir além

BATTERY HISTORIAN / GOOGLE (GITHUB). Disponível em: https://github.com/google/battery-historian. Ferramenta de visualização dos batterystats coletados via adb.

APP STANDBY BUCKETS / ANDROID DEVELOPERS. Disponível em: https://developer.android.com/topic/performance/appstandby. Como o sistema classifica apps por uso e o que cada bucket permite.

LITERT / GOOGLE AI EDGE. Disponível em: https://ai.google.dev/edge/litert. Runtime de inferência on-device e suas opções de aceleração de hardware.
