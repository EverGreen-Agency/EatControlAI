---
unit: 13
unit_title: "Curso do Meta SDK (DAT)"
section: "13.2"
section_title: "Arquitetura da solução"
source_file: "Un13_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN13_Curso_do_Meta_SDK_DAT.md"
source_pages: [11, 12, 13, 14, 15, 16, 17, 18]
language: "pt-BR"
---

## 13.2 Arquitetura da solução

### 13.2.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar o modelo de arquitetura óculos ⇄ Bluetooth ⇄ app companion e o papel de cada componente.

- Justificar por que os modelos de IA e o agente rodam no celular, e não nos óculos.

- Identificar por onde os dados entram (câmera, microfones) e saem (alto-falantes) da solução, e quais limites o Bluetooth impõe a esse tráfego.

- Relacionar cada checkpoint do hackathon — entrada por câmera/mic, saída por áudio e IA local — ao componente da arquitetura que o satisfaz.

### 13.2.2 Pré-requisitos

- Tópico 2.1 — Hardware dos óculos e visão geral do Wearables Device Access Toolkit (DAT).

- Tópico 1.6 — Conceito de edge AI (execução de modelos on-device).

- Ferramentas: nenhuma nova. Este é um tópico conceitual; o setup do ambiente vem no tópico 2.3.

### 13.2.3 Conceito / fundamentação

#### 13.2.3.1 A arquitetura em uma frase

Os óculos capturam e reproduzem; o celular pensa. Toda solução do hackathon segue o mesmo modelo: os óculos Ray-Ban Meta fornecem os sensores (câmera ultra-wide de 12 MP, array de 5 microfones) e os atuadores (alto-falantes open-ear); o Bluetooth transporta esses dados; e um app companion — no celular/notebook; no nosso programa, um app Android — recebe os dados, executa os modelos de IA e o agente, e devolve a resposta em áudio (Figura 19).

**Figura 19 – Arquitetura da solução: óculos, Bluetooth® e app companion**

Fonte: autoria própria.

#### 13.2.3.2 Os óculos são um periférico de I/O

Os Ray-Ban Meta não têm display, têm bateria pequena e um chip dimensionado para captura e conectividade — não para rodar redes neurais. Mais importante: o DAT não executa código de terceiros nos óculos. Não existe "instalar seu app nos óculos"; toda integração é um app mobile que acessa os sensores do dispositivo remotamente.

A analogia útil: pense nos óculos como um fone Bluetooth com câmera embutida. Assim como você não instala aplicativos no seu fone — ele só envia o áudio do microfone e toca o que o celular manda —, os óculos só enviam vídeo/áudio e reproduzem a resposta. I/O de entrada e saída, nada mais.

#### 13.2.3.3 O elo: Bluetooth

A conexão entre óculos e celular usa Bluetooth, e isso define os limites físicos da solução:

- Vídeo: o streaming da câmera trafega por Bluetooth Classic, cuja banda é limitada. Por isso o DAT trabalha com resoluções e frame rates modestos e adapta a qualidade automaticamente quando a banda aperta (detalhes no tópico 2.5).

- Áudio do microfone: chega ao app pelo perfil HFP (Hands-Free Profile), o mesmo usado por fones em chamadas — mono e com taxa de amostragem baixa (8 kHz dado da doc oficial, pode mudar com versões). Detalhes no tópico 2.6.

- Áudio de saída: o app toca áudio pelo canal Bluetooth padrão do sistema, como faria com qualquer fone; os alto-falantes open-ear dos óculos reproduzem (tópico 2.6).

Atenção! Bluetooth não é Wi-Fi. Projetar pensando em vídeo full-HD a 60 fps é o erro conceitual mais comum deste módulo — o link não entrega isso, e o DAT vai degradar a qualidade sozinho. Assuma frames comprimidos, em resolução moderada, e desenhe sua solução em torno disso.

#### 13.2.3.4 O app companion: onde tudo roda

O app Android que sua equipe vai construir concentra três responsabilidades:

1. Falar com os óculos — via DAT (mwdat-core, mwdat-camera), que gerencia descoberta do dispositivo, sessão e streams. O app não abre sockets Bluetooth manualmente: o registro e as permissões são intermediados pelo app Meta AI instalado no celular do usuário (fluxo detalhado no tópico 2.4).

2. Executar a IA — os modelos de visão (tópico 1.7) e de fala (tópico 1.8) rodam on-device no celular, com as técnicas de edge AI do tópico 1.6.

3. Orquestrar o agente — a lógica que decide o que fazer com cada entrada: transcrever a fala, analisar o frame, montar a resposta e enviá-la aos alto-falantes.

#### 13.2.3.5 Por que a compute fica no celular

Três razões se somam:

- Física: rodar um modelo de visão exige processamento, memória e energia que não cabem em uma armação de óculos sem esquentar, pesar e drenar a bateria em minutos. O celular tem SoC com aceleradores, gigabytes de RAM e bateria ordens de grandeza maior.

- Plataforma: mesmo que coubesse, o DAT não oferece execução de código nos óculos — a única superfície programável da solução é o app mobile.

- Requisito do programa: o checkpoint de IA local exige que a inferência aconteça on-device, sem depender de nuvem. O celular é o único lugar da arquitetura capaz de satisfazer isso.

Na prática: essa divisão define onde sua equipe gasta esforço no hackathon. Os óculos são iguais para todos os times — o que diferencia sua solução é o que roda no celular: a escolha do modelo, a otimização da inferência e a inteligência do agente.

#### 13.2.3.6 Arquitetura × checkpoints

Os checkpoints de acordo com a arquitetura estão descritos na Tabela 20, a seguir.

**Tabela 20 – Relação entre checkpoints, componentes da arquitetura e caminhos dos dados Checkpoint Componente que satisfaz Caminho do dado**

Entrada por câmera Câmera dos óculos + stream do DAT Óculos → Bluetooth → app companion

Entrada por microfone Microfones dos óculos + áudio HFP Óculos → Bluetooth → app companion

Saída por áudio Alto-falantes open-ear + áudio BT do sistema App companion → Bluetooth → óculos

IA local Modelos on-device no app companion Inferência inteira no celular, sem nuvem

Fonte: autoria própria.

### 13.2.4 Na prática

Este tópico é conceitual, então o exercício aqui é rastrear um ciclo completo de dados pela arquitetura — o mesmo ciclo que sua solução do hackathon vai executar milhares de vezes:

1. O usuário fala ou aponta o olhar para algo → microfones/câmera dos óculos capturam.

2. Os dados atravessam o Bluetooth e chegam ao app companion.

3. O app roda STT sobre o áudio (tópico 1.8) e/ou o modelo de visão sobre o frame (tópico 1.7) — tudo on-device.

4. O agente decide a resposta e gera a fala com TTS (tópico 1.8).

5. O áudio volta pelo Bluetooth e toca nos alto-falantes dos óculos.

Em código, o esqueleto do app companion espelha exatamente esses blocos. Não se preocupe com os detalhes de cada chamada agora — cada uma tem seu próprio tópico:

▶ Código 2.2-01 – Esqueleto conceitual do app companion — código completo no notebook companion (seção 2.2.4).

Repare no que o código não contém: nenhuma linha roda nos óculos, nenhum socket Bluetooth é aberto à mão, nenhuma chamada vai para a nuvem. O DAT abstrai o transporte; seu trabalho é o que acontece dentro do collect.

Atenção! Erro comum de primeira semana: procurar os óculos no adb devices ou perguntar "como faço deploy nos óculos?". O alvo do deploy é sempre o celular. Os óculos nunca aparecem para você como um dispositivo Android — apenas como um Device dentro do DAT. Na prática: como o ciclo inteiro (captura → BT → inferência → BT → áudio) soma latências, meça o tempo de ponta a ponta desde o início do desenvolvimento. Uma resposta que demora 5 segundos mata a experiência de voz — e otimizar a inferência no celular (tópico 1.6) é onde você tem mais controle sobre esse tempo.

### 13.2.5 Resumo / cheatsheet

- Arquitetura: óculos (câmera/mic/alto-falantes) ⇄ Bluetooth ⇄ app companion Android (DAT + modelos de IA + agente).

- Os óculos são periférico de I/O: capturam e reproduzem; não executam código de terceiros.

- Toda a compute — modelos e agente — roda no celular, on-device.

- O Bluetooth é o gargalo: vídeo comprimido em resolução/fps modestos; áudio de mic via HFP mono.

- O app não fala Bluetooth direto com os óculos: o DAT e o app Meta AI intermediam registro, permissões e sessão.

- Checkpoints: câmera e mic = entrada pelos óculos; áudio = saída pelos óculos; IA local = inferência no celular, sem nuvem.

- Deploy é sempre no celular; os óculos aparecem para o app apenas como um dispositivo do DAT.

### 13.2.6 Referências

- INTEGRATION OVERVIEW — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/build-overview/. Visão geral oficial da integração: sessões, componentes do toolkit (core e câmera) e uso de microfones/alto-falantes via Bluetooth.

- WEARABLES DEVICE ACCESS TOOLKIT — DOCUMENTAÇÃO. META. Disponível em: https://wearables.developer.meta.com/docs. Portal central da documentação do DAT, incluindo setup, integração Android e API reference.

- META-WEARABLES-DAT-ANDROID. META (GITHUB). Disponível em: https://github.com/facebook/meta-wearables-dat-android. Repositório oficial do SDK Android, com apps de exemplo que implementam a arquitetura deste tópico.

- FAQ — META WEARABLES. Disponível em: https://developers.meta.com/wearables/faq/. Perguntas frequentes sobre o programa de desenvolvedores e o toolkit.

2.2.6.1 Para ir além MICROPHONES AND SPEAKERS — META WEARABLES DEVELOPER CENTER. Disponível em:

https://wearables.developer.meta.com/docs/microphones-and-speakers/. Detalha o caminho de áudio (HFP e saída) que o tópico 2.6 vai aprofundar.

SESSION LIFECYCLE — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/lifecycle-events/. Ciclo de vida da sessão entre app e óculos, base para o tópico 2.4.
