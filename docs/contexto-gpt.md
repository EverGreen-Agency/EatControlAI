# Eat Control AI — Visão de Produto e Aplicativo para Implementação

> **Documento de contexto e direção para desenvolvimento**
>
> Este documento deve servir como **North Star do produto**, não como uma especificação imutável. Ele consolida a visão atual do Eat Control AI, as decisões já tomadas, a arquitetura conceitual, os usuários, os fluxos e o recorte do MVP.
>
> O objetivo é dar contexto suficiente para que um agente de desenvolvimento, como Claude Code, consiga **entender o produto como um todo, tomar boas decisões locais, propor melhorias e construir de forma coerente**, sem precisar seguir cegamente cada detalhe sugerido aqui.
>
> Sempre que houver conflito entre simplicidade, velocidade e uma arquitetura excessivamente sofisticada, priorizar:
>
> 1. uma jornada funcional end-to-end;
> 2. confiabilidade;
> 3. capacidade de demonstração;
> 4. arquitetura que permita evolução posterior;
> 5. evitar complexidade prematura.

---

# 1. Contexto do projeto

O **Eat Control AI** está sendo desenvolvido no contexto do **AI Glasses Brasil 2026**, programa/hackathon realizado com Meta e CEIA e voltado à criação de soluções usando os óculos inteligentes da família Meta.

A solução nasceu inicialmente como **Eat Control Vision**, uma das ideias avaliadas internamente para o programa, e evoluiu para o posicionamento atual:

> **Eat Control AI — inteligência alimentar para quem usa GLP-1.**

O produto parte de uma constatação simples:

Pessoas que utilizam medicamentos GLP-1 passam meses dentro de uma jornada de tratamento, mas grande parte das decisões alimentares continua acontecendo sem acompanhamento profissional, no cotidiano.

O medicamento pode atuar sobre fome, saciedade e metabolismo.

Mas continua cabendo à pessoa decidir:

* o que comer;
* quanto comer;
* como montar refeições;
* se há proteína suficiente;
* como está sua hidratação;
* se determinada escolha está alinhada ao plano definido;
* se certos alimentos parecem associados a desconfortos;
* como construir hábitos sustentáveis ao longo do tratamento.

O Eat Control AI pretende ocupar o espaço existente **entre a prescrição e a decisão cotidiana**.

Não quer substituir o médico ou nutricionista.

Quer levar contexto e orientação educativa para o instante em que a decisão acontece.

---

# 2. Tese central do produto

A tese é:

> **A alimentação acontece no mundo real, e não dentro de uma tela.**

Aplicativos tradicionais normalmente exigem:

1. pegar o celular;
2. desbloquear;
3. abrir o aplicativo;
4. fotografar;
5. preencher informações;
6. esperar;
7. interpretar o resultado.

Com óculos inteligentes, a interação pode se aproximar de:

> **OLHAR → PERGUNTAR → OUVIR → SEGUIR**

O usuário está diante de um prato, produto, buffet, cardápio ou rótulo.

Ele olha.

Pergunta algo.

O sistema interpreta o contexto.

Cruza a informação com o perfil daquele usuário.

E devolve uma orientação curta por áudio.

A redução de fricção é uma das razões centrais para o uso dos AI Glasses.

---

# 3. O produto não é “um app dos óculos”

Essa distinção é fundamental para toda a arquitetura.

O Eat Control AI é:

> **um produto mobile completo com inteligência local, backend e integração opcional com AI Glasses.**

Os óculos são uma interface privilegiada desse produto.

Eles não são o produto inteiro.

A arquitetura conceitual possui cinco grandes componentes:

```text
1. Eat Control Patient App
   ↓
2. Edge AI / Decision Engine
   ↓
3. Eat Control Backend
   ↓
4. Professional / Admin Platform
   ↓
5. Meta Wearables Integration
```

Esses componentes podem evoluir em momentos diferentes.

O MVP não precisa implementar todos no mesmo nível de profundidade.

---

# 4. Papel dos Ray-Ban Meta

Para o hardware utilizado no programa, pensar nos óculos essencialmente como:

### Inputs

* câmera em primeira pessoa;
* microfones;
* contexto natural daquilo para onde o usuário está olhando.

### Outputs

* áudio pelos alto-falantes open-ear.

Os óculos utilizados no programa não possuem display.

Portanto, não devemos projetar uma experiência visual que dependa de informação aparecendo na lente.

A experiência primária dos óculos é:

```text
VISÃO
+
VOZ
↓
PROCESSAMENTO NO TELEFONE
↓
ÁUDIO
```

Os óculos capturam e reproduzem.

**O smartphone executa o aplicativo e a inteligência.**

O material oficial do programa descreve justamente a arquitetura em que o app companion Android realiza lógica e inferência, enquanto câmera, microfones e alto-falantes dos óculos servem como interface.

---

# 5. Papel do Meta AI

Não confundir o **Meta AI App** com o **Eat Control AI App**.

O Meta AI é parte da infraestrutura necessária para os óculos.

Ele cuida de aspectos como:

* pareamento;
* gerenciamento dos óculos;
* conexão;
* firmware;
* permissões;
* Developer Mode;
* autorização de aplicativos que utilizam o Meta Wearables Device Access Toolkit.

O usuário não deve precisar viver dentro do Meta AI para utilizar o Eat Control.

Depois da configuração inicial, a experiência cotidiana deve acontecer principalmente através:

* dos próprios óculos;
* do Eat Control App.

---

# 6. O Eat Control App

Precisamos construir nosso próprio aplicativo.

O **Eat Control Patient App** é o principal produto operacional.

Para o hackathon, a direção atual é:

> **Android nativo em Kotlin.**

O programa foi estruturado especificamente ao redor de Kotlin/Android e do Meta DAT, e o material explica que o companion app é responsável pela IA, pelo agente e pela UI de configuração.

O aplicativo deve funcionar tanto:

### Com os óculos

experiência hands-free e natural;

quanto:

### Sem os óculos

usando câmera, microfone e tela do smartphone.

Isso é estrategicamente importante.

Os óculos devem **melhorar radicalmente a experiência**, e não tornar o produto impossível de utilizar sem eles.

Também facilita desenvolvimento, testes, aquisição de usuários e potencial modelo comercial futuro.

---

# 7. Posicionamento atual

O posicionamento principal foi refinado para:

> **Inteligência alimentar para quem usa GLP-1.**

O deck atual apresenta exatamente essa direção e organiza o produto ao redor de:

**captura → interpretação → personalização → orientação → registro**.

Evitar transformar o produto inicial em uma plataforma que tenta resolver simultaneamente:

* GLP-1;
* doença celíaca;
* alergias graves;
* intolerâncias;
* diabetes;
* SIBO;
* todas as dietas;
* todas as patologias;
* todas as necessidades nutricionais.

A visão pode crescer.

O MVP deve ser muito mais controlado.

---

# 8. Persona principal

## Paciente / usuário em tratamento com GLP-1

Exemplo conceitual:

**João, 45 anos.**

Está utilizando GLP-1 e recebe acompanhamento profissional.

Ao longo do dia surgem perguntas como:

* Posso comer isso?
* Essa refeição está equilibrada para meu objetivo?
* Tem proteína suficiente?
* Estou bebendo água suficiente?
* Estou comendo pouco demais?
* Esse tipo de refeição parece estar associado aos sintomas que registrei?
* Como foram minhas escolhas esta semana?

O produto deve responder principalmente ao problema:

> **“O que essa escolha significa dentro do meu contexto?”**

e não apenas:

> “Que alimento é esse?”

---

# 9. Personalização

O Eat Control não deve produzir recomendações universais como se todos os usuários de GLP-1 fossem iguais.

A orientação deve considerar um **perfil individual**.

Possíveis dimensões:

```text
UserProfile

identificação básica
objetivos
preferências
restrições
metas
orientações profissionais
fase da jornada
histórico
refeições anteriores
sintomas reportados
hidratação
hábitos
feedback
```

Exemplo:

```text
João

Tratamento:
GLP-1

Objetivos:
- melhorar qualidade alimentar
- preservar massa magra
- aumentar consistência de proteína
- melhorar hidratação

Orientações cadastradas:
- priorizar proteína em refeições principais
- evitar refeições excessivamente volumosas

Restrição pessoal:
- lactose
```

O sistema pode analisar uma refeição usando esse contexto.

---

# 10. Papel do profissional de saúde

O profissional é um stakeholder extremamente importante, mas não precisa receber um aplicativo completo no primeiro MVP.

No futuro, provavelmente utilizará uma **interface web profissional**.

Possíveis responsabilidades:

* cadastrar orientações;
* definir objetivos;
* acompanhar histórico autorizado;
* visualizar padrões;
* revisar registros;
* receber relatórios;
* ajustar recomendações dentro de sua atuação profissional.

Exemplo conceitual:

```text
Profissional
   ↓
Painel Eat Control

Paciente João
├── objetivos
├── orientações
├── histórico
├── padrões
├── sintomas registrados
└── relatórios
```

A intenção não é o sistema substituir o profissional.

É criar uma camada contínua entre consultas.

---

# 11. Papel do administrador

Um terceiro perfil futuro é o administrador da plataforma Eat Control.

Possíveis responsabilidades:

* usuários;
* clínicas;
* profissionais;
* versões de modelos;
* regras;
* integrações;
* fontes alimentares;
* feature flags;
* auditoria;
* analytics;
* configuração de produto.

Esse painel não é prioridade para o MVP do hackathon.

---

# 12. Estrutura conceitual do aplicativo

O aplicativo mobile pode evoluir para os seguintes módulos.

Não interpretar esta lista como obrigação de implementar tudo imediatamente.

---

## 12.1 Autenticação

Possibilidades:

```text
Entrar
Criar conta
Recuperar acesso
```

No primeiro protótipo técnico, autenticação pode ser simplificada ou mockada.

Mas o domínio deve assumir que usuários possuem identidade própria.

---

# 12.2 Onboarding

O onboarding é extremamente importante porque cria o contexto que diferencia o Eat Control de um scanner genérico de alimentos.

Possíveis etapas:

### Objetivo

```text
O que você quer melhorar?

[ ] Qualidade alimentar
[ ] Proteína
[ ] Hidratação
[ ] Regularidade
[ ] Organização das refeições
[ ] Outro
```

### Jornada

```text
Você utiliza GLP-1?

[ Sim ]
[ Não ]
```

Evitar pedir informações medicamentosas além do necessário durante o MVP.

### Preferências/restrições

```text
Existe alguma preferência ou restrição
que devemos considerar?
```

### Orientações profissionais

```text
Você possui orientações definidas
por médico ou nutricionista?

[ Adicionar ]
[ Não agora ]
```

---

# 12.3 Home / Hoje

A Home deve transmitir que o produto é um **companheiro contínuo**, e não somente uma câmera inteligente.

Exemplo:

```text
Olá, João

HOJE

Proteína
████████░

Hidratação
██████░░░

3 refeições registradas

[ Analisar alimento ]

────────────────

Seus óculos
● Ray-Ban Meta conectado

────────────────

Últimas escolhas

12:40
Frango + arroz + legumes

09:14
Iogurte + fruta
```

No MVP, métricas podem ser simplificadas ou parcialmente mockadas se necessário.

---

# 12.4 Analisar alimento

Essa é uma das áreas centrais do produto.

Deve funcionar tanto com óculos quanto diretamente pelo smartphone.

Possíveis modos:

```text
[ Prato ]

[ Rótulo ]

[ Código de barras ]

[ Cardápio ]

[ Buscar manualmente ]
```

Não necessariamente precisamos apresentar ao usuário cinco botões.

O sistema pode futuramente detectar automaticamente o tipo de entrada.

---

# 13. Uma pipeline, múltiplos inputs

Arquiteturalmente, evitar implementar uma lógica completamente diferente para câmera do celular e câmera dos óculos.

Idealmente:

```text
Meta Glasses ─────┐
                  │
Phone Camera ─────┼──→ Analysis Pipeline
                  │
Gallery ──────────┘
```

O restante do sistema não deveria se importar muito com a origem do frame.

Isso facilita:

* desenvolvimento sem hardware;
* testes automatizados;
* fallback;
* comparação;
* expansão futura.

---

# 14. Fluxo de rótulo

Este é um dos melhores cenários para o primeiro MVP porque é controlável e demonstrável.

Fluxo:

```text
Usuário olha para o rótulo
        ↓
captura
        ↓
OCR
        ↓
parser
        ↓
informações declaradas
        ↓
perfil do usuário
        ↓
motor determinístico
        ↓
decisão
        ↓
resposta curta
```

Exemplo:

O OCR encontra:

> CONTÉM LEITE

O usuário possui uma restrição cadastrada relacionada a leite.

O sistema pode produzir uma conclusão determinística baseada em informação explicitamente declarada.

Esse tipo de evidência deve possuir prioridade maior do que inferência probabilística de visão.

A matriz original já recomendava justamente OCR, código de barras, regras determinísticas e declaração explícita de incerteza.

---

# 15. Código de barras

Outro fluxo altamente confiável:

```text
câmera
 ↓
EAN / GTIN
 ↓
lookup
 ↓
produto
 ↓
ingredientes / composição
 ↓
perfil
 ↓
regras
 ↓
orientação
```

Possíveis fontes já consideradas:

* Open Food Facts;
* TBCA;
* USDA FoodData Central.

No futuro, o ideal é o app não precisar conhecer diretamente todas as fontes.

Podemos normalizá-las através de nosso próprio backend.

---

# 16. Prato / visão computacional

A análise de prato é uma funcionalidade muito interessante para a demonstração, mas deve ser tratada como **probabilística**.

Possíveis objetivos:

* identificar grupos de alimentos;
* detectar alimentos visíveis;
* entender composição geral;
* indicar presença provável de vegetais/proteína/carboidratos;
* gerar perguntas de confirmação;
* contextualizar uma resposta.

Evitar promessas como:

> “Este prato possui exatamente 524 kcal.”

ou:

> “Este alimento é seguro para sua alergia.”

a partir de uma única fotografia.

A própria matriz de ideias identificou reconhecimento de ingredientes e estimativa de porções em pratos mistos como um risco técnico e recomendou separar inferência visual de evidência declarada.

---

# 17. Política de evidência

O sistema deve tratar fontes diferentes de informação com níveis diferentes de confiança.

Uma possível hierarquia:

```text
1. Orientação profissional explícita
2. Informação declarada em rótulo
3. Informação estruturada de base confiável
4. Confirmação do usuário
5. OCR interpretado
6. Inferência visual
```

Isso não precisa ser exatamente uma ordenação linear em todas as situações.

Mas o conceito é importante.

---

# 18. Estados de decisão

Evitar respostas binárias excessivamente confiantes.

Uma taxonomia inicial poderia ser:

```text
COMPATIBLE

INCOMPATIBLE

NEEDS_CONFIRMATION

INSUFFICIENT_INFORMATION
```

Ou uma representação equivalente.

O sistema deve saber dizer:

> **“Não tenho informação suficiente para confirmar.”**

Isso é uma feature.

Não uma falha.

---

# 19. Safety / limites fundamentais

O Eat Control AI:

* não diagnostica;
* não altera doses;
* não recomenda iniciar ou interromper medicamento;
* não substitui médicos ou nutricionistas;
* não promete precisão clínica por fotografia;
* não certifica segurança em casos de alergia grave;
* não deve esconder incerteza.

O próprio pitch atual explicita esses limites.

O produto deve ser construído como:

> **apoio alimentar + orientação educativa + construção de hábitos.**

---

# 20. Resultado da análise

A resposta não precisa existir apenas em áudio.

Quando a consulta vier pelos óculos:

### Imediato

resposta curta pelos alto-falantes.

### Persistente

resultado salvo no aplicativo.

Exemplo:

```text
FRANGO + ARROZ + LEGUMES

Boa base.

✓ Vegetais identificados
✓ Fonte de proteína identificada

Atenção:
Seu perfil prioriza proteína nesta refeição.

Confiança
Alta

[ Registrar ]

[ Corrigir análise ]
```

O feedback do usuário pode posteriormente ajudar:

* melhorar classificadores;
* detectar erros;
* construir histórico;
* criar datasets;
* personalizar o sistema.

---

# 21. Áudio

A experiência nos óculos deve usar respostas curtas.

Não transformar o TTS em um podcast.

Exemplo bom:

> “Boa base vegetal. Parece faltar proteína.”

Em vez de:

> “Analisando detalhadamente esta refeição, pude observar que existem diversos alimentos presentes…”

O produto deve ser concebido para uso real em:

* supermercado;
* restaurante;
* buffet;
* cozinha;
* casa;
* trabalho.

---

# 22. Histórico

O histórico é importante para transformar análises isoladas em uma jornada.

Possível estrutura:

```text
Hoje

12:42
Almoço

09:13
Café da manhã

────────────

Ontem

20:04
Jantar
```

Abrindo um registro:

```text
Almoço — 12:42

Imagem

Itens identificados

Orientação fornecida

Confiança

Correções do usuário

Sintomas relacionados, se registrados

Fonte da análise
```

---

# 23. Registro

O fluxo deve tentar evitar preenchimento manual excessivo.

Sempre que possível:

```text
analisou
 ↓
orientou
 ↓
confirmou
 ↓
registrou
```

Um dos diferenciais do wearable é justamente reduzir fricção.

---

# 24. Insights

Não precisa estar completo no primeiro hackathon, mas é uma parte importante da visão.

Com histórico suficiente, o Eat Control pode detectar padrões como:

> “Nos últimos cinco dias você consumiu menos proteína no café da manhã.”

ou:

> “Sua hidratação costuma ficar abaixo da meta durante a tarde.”

ou:

> “Você relatou desconforto em quatro refeições semelhantes.”

O objetivo é evoluir:

```text
DECISÃO
↓
HISTÓRICO
↓
PADRÕES
↓
INTELIGÊNCIA COMPORTAMENTAL
```

O pitch atual já apresenta essa evolução como:

**assistente alimentar → inteligência comportamental → plataforma integrada.**

---

# 25. Sintomas

O registro de sintomas pode futuramente aumentar muito o valor do sistema.

Exemplos:

```text
Como você se sentiu?

[ Bem ]

[ Náusea ]

[ Muito cheio ]

[ Refluxo ]

[ Outro ]
```

Evitar inferir causalidade médica.

O sistema pode identificar correlações e padrões para o usuário/profissional revisar.

---

# 26. Perfil

O aplicativo deve possuir uma área clara de perfil.

Possibilidades:

```text
Meu perfil

Objetivos
Metas
Orientações
Preferências
Restrições
Tratamento
Profissional
Privacidade
Integrações
```

---

# 27. Área de óculos

Uma área específica pode mostrar:

```text
SEUS ÓCULOS

Ray-Ban Meta

● conectado

Permissão da câmera
✓

Microfone
✓

Áudio
✓

[ Testar câmera ]

[ Testar áudio ]

[ Gerenciar conexão ]
```

Quando alguma autorização específica do ecossistema Meta for necessária, o aplicativo pode orientar ou encaminhar o usuário para o fluxo apropriado.

---

# 28. Arquitetura de alto nível

Conceitualmente:

```text
                         EAT CONTROL CLOUD
                    ┌────────────────────────┐
                    │ Auth                   │
                    │ Users                  │
                    │ Profiles               │
                    │ Guidelines             │
                    │ History                │
                    │ Food Knowledge         │
                    │ Model Configuration    │
                    │ Analytics              │
                    └───────────┬────────────┘
                                │
                              HTTPS
                                │
                    ┌───────────▼────────────┐
                    │                        │
                    │   EAT CONTROL APP      │
                    │   Android / Kotlin     │
                    │                        │
                    │ UI                     │
                    │ Profile                │
                    │ History                │
                    │ Settings               │
                    │                        │
                    │ Edge AI Engine         │
                    │ OCR                    │
                    │ Barcode                │
                    │ STT                    │
                    │ Vision                 │
                    │ Rules                  │
                    │ TTS                    │
                    │                        │
                    │ Local Storage          │
                    └───────────┬────────────┘
                                │
                               DAT
                                │
                    ┌───────────▼────────────┐
                    │     META / GLASSES     │
                    │ camera                 │
                    │ microphone             │
                    │ audio                  │
                    └────────────────────────┘
```

---

# 29. Edge AI

Um objetivo técnico importante do projeto é executar o máximo possível do **caminho crítico de interação no smartphone**.

Idealmente:

```text
óculos
 ↓
telefone
 ↓
inferência local
 ↓
decisão
 ↓
TTS
 ↓
óculos
```

Isso traz:

* menor dependência de internet;
* menor latência;
* maior privacidade;
* experiência mais resiliente;
* demonstração clara de Edge AI.

O material do programa coloca explicitamente a lógica e os modelos de IA no companion app do smartphone.

---

# 30. Backend remoto

**Ter Edge AI não significa não ter backend.**

O Eat Control deve possuir backend remoto.

A diferença é:

> **o backend cloud não precisa estar no caminho crítico de cada inferência.**

Possíveis responsabilidades do backend:

```text
Auth
Users
Profiles
Professional guidelines
History
Meal records
Symptoms
Device metadata
Model versions
Feature flags
Food data normalization
Sync
Analytics
Audit
Professional access
Admin
```

---

# 31. Offline-first

Uma arquitetura interessante:

```text
INTERAÇÃO

Glasses
↓
Phone
↓
Local AI
↓
Local decision
↓
Local history
↓
Audio
```

Depois:

```text
SINCRONIZAÇÃO

Phone
↓
Cloud
```

Assim, um restaurante com internet instável não necessariamente impede a funcionalidade principal.

---

# 32. Persistência local

Provavelmente utilizar:

* Room / SQLite;
* DataStore para preferências/configurações.

Persistir localmente:

* perfil mínimo necessário;
* histórico recente;
* cache de produtos;
* configurações;
* regras;
* estado de sincronização;
* versões relevantes.

---

# 33. Fontes alimentares

Fontes já consideradas no projeto:

### Open Food Facts

Produtos embalados e código de barras.

### TBCA

Tabela Brasileira de Composição de Alimentos.

### USDA FoodData Central

Composição nutricional complementar.

No MVP podemos utilizar:

```text
pequena base/cache conhecida
+
APIs quando disponíveis
```

para garantir uma demo estável.

No futuro:

```text
OFF ───┐
TBCA ──┼→ Eat Control Food Normalization → App
USDA ──┘
```

---

# 34. IA modular

Não acoplar o aplicativo diretamente a um único modelo.

Pensar em capabilities/providers.

Por exemplo:

```text
OcrProvider

BarcodeProvider

ObjectDetectionProvider

ImageClassificationProvider

SttProvider

TtsProvider
```

O restante do produto deve depender das interfaces.

Não da implementação.

Assim podemos trocar:

```text
MediaPipe
↕
YOLO
↕
novo modelo
```

sem reescrever o domínio.

---

# 35. Estratégia de escolha de modelos

Não escolher modelos apenas por popularidade.

Criar benchmarks.

Cada necessidade deve ter:

```text
TASK

→ candidatos
→ dataset
→ accuracy/quality
→ latency
→ RAM
→ model size
→ battery
→ robustness
→ device
```

O modelo escolhido deve representar um bom ponto na fronteira:

> **qualidade × velocidade × tamanho × consumo.**

---

# 36. OCR

Baseline recomendada:

> **ML Kit Text Recognition on-device.**

Não treinar OCR próprio sem necessidade.

Casos:

* ingredientes;
* avisos;
* tabelas;
* cardápios;
* embalagens.

---

# 37. Barcode

Baseline:

> **ML Kit Barcode Scanning.**

O resultado deve ser convertido em uma identificação normalizada do produto.

---

# 38. STT

Baseline:

> Android on-device speech recognition.

Depois benchmarkar alternativas como modelos Whisper pequenos/quantizados caso seja necessário melhorar:

* offline;
* ruído;
* precisão;
* controle.

---

# 39. TTS

Baseline:

> Android TextToSpeech.

O objetivo inicial é:

* baixa latência;
* compreensão;
* funcionamento local;
* áudio chegando corretamente aos óculos.

Naturalidade extrema da voz não é prioridade do MVP.

---

# 40. Visão computacional

Não definir antecipadamente YOLO como solução obrigatória.

Testar.

Possíveis alternativas:

* ML Kit;
* MediaPipe Tasks;
* YOLO nano;
* classificadores customizados;
* modelos LiteRT.

COCO deve ser tratado como dataset/classes gerais, e não como solução específica para entendimento alimentar.

É provável que alimentação exija:

* fine-tuning;
* dataset próprio;
* ou uso combinado de modelos.

---

# 41. LiteRT / MediaPipe

Uma filosofia interessante:

```text
ML Kit
↓
quando solução pronta resolve

MediaPipe
↓
quando pipeline pronto + modelo customizável resolve

LiteRT
↓
quando precisamos controlar diretamente
modelo customizado e runtime
```

---

# 42. Otimização

Para modelos próprios:

### Primeiro

baseline.

### Depois

quantização INT8.

### Depois, se necessário

* QAT;
* pruning;
* outras otimizações.

Evitar aplicar técnicas só porque aparecem no curso.

Cada otimização deve resolver um problema real de:

* latency;
* memory;
* battery;
* APK size.

---

# 43. Fine-tuning

Aplicar principalmente quando modelos genéricos não reconhecem as classes necessárias.

Um candidato possível:

> detector de alimentos customizado.

Não fazer fine-tuning simplesmente por demonstração tecnológica.

---

# 44. LoRA

Não é prioridade inicial.

Pode fazer sentido futuramente para:

* LLM local;
* VLM;
* especialização de modelo;
* adapters por domínio.

Mas não deve bloquear o MVP.

---

# 45. Poda

Pruning não é prioridade.

Só aplicar se houver evidência de ganho real no runtime/hardware escolhido.

---

# 46. Professor-aluno

Não planejar destilação própria como requisito do hackathon.

Se modelos pequenos destilados já estiverem disponíveis, podem ser benchmarkados.

---

# 47. Motor determinístico

Esse componente é fundamental.

Nem toda decisão deve ser produzida por IA generativa.

Exemplo:

```text
IF
profile.restrictions contains MILK

AND

label.explicitlyDeclares containsMilk

THEN

decision = incompatible
```

Esse tipo de regra é:

* auditável;
* previsível;
* testável;
* explicável.

IA probabilística deve gerar evidências.

O motor de decisão decide como usar essas evidências.

---

# 48. LLM / agente

Não é obrigatório que cada interação passe por um LLM.

Um fluxo simples pode ser:

```text
OCR
↓
parser
↓
rule engine
↓
template
↓
TTS
```

Um modelo de linguagem pode ser usado quando realmente adiciona valor:

* interpretação contextual;
* composição da resposta;
* perguntas de confirmação;
* explicação;
* análise de histórico.

Evitar colocar LLM no meio apenas porque o projeto envolve IA.

---

# 49. Privacidade

A alimentação e a jornada de saúde podem constituir dados sensíveis.

O produto deve nascer com princípios como:

* data minimization;
* processamento local quando possível;
* não armazenar imagem sem necessidade;
* opt-in claro;
* controle de compartilhamento;
* possibilidade de exclusão;
* separação entre telemetria e conteúdo pessoal.

Possível tela:

```text
PRIVACIDADE

Processamento de imagens
● No dispositivo

Salvar fotos
○ Nunca
○ Apenas refeições registradas
○ Sempre

Compartilhar dados para melhoria
[ off ]
```

---

# 50. Jornada de primeiro uso

Fluxo conceitual:

```text
Instala Eat Control
        ↓
cria conta
        ↓
onboarding
        ↓
configura objetivos
        ↓
configura orientações
        ↓
configura restrições/preferências
        ↓
conecta Ray-Ban Meta
        ↓
autoriza integração
        ↓
testa câmera/áudio
        ↓
Home
```

---

# 51. Jornada cotidiana pelos óculos

Exemplo:

```text
João está em um restaurante

        ↓

olha para o prato

        ↓

ativa interação Eat Control

        ↓

"Como está esse prato para mim?"

        ↓

imagem + áudio chegam ao Eat Control App

        ↓

STT

        ↓

vision/OCR/context

        ↓

perfil + histórico

        ↓

decision engine

        ↓

"Boa base vegetal.
Parece faltar proteína."

        ↓

resultado salvo
```

---

# 52. Jornada pelo smartphone

O mesmo sistema pode receber:

```text
camera do telefone
```

em vez de:

```text
camera dos óculos
```

Fluxo de negócio deve permanecer similar.

---

# 53. MVP técnico

Antes de construir dez telas, fechar uma vertical end-to-end.

Primeiro milestone:

```text
Mock/Glasses
↓
captura
↓
OCR
↓
perfil fictício
↓
rule engine
↓
TTS
↓
resposta
```

Depois adicionar:

```text
barcode
```

Depois:

```text
STT
```

Depois:

```text
visual food analysis
```

---

# 54. MVP de produto

Uma primeira versão minimamente coerente poderia possuir:

```text
Splash
Login simples
Onboarding
Home
Analyze
Result
History
Profile
Glasses
Settings
```

Nem todas precisam possuir profundidade total.

---

# 55. O que não deixar para o último dia

O projeto não deve chegar ao final com:

* modelos sofisticados;
* dashboard bonito;
* várias APIs;

mas sem conseguir fazer:

```text
GLASSES → PHONE → AI → AUDIO
```

A prioridade é fechar a cadeia completa.

---

# 56. Demo ideal

Uma demonstração particularmente forte:

### Cenário 1 — Rótulo

Usuário olha para embalagem.

Pergunta:

> “Isso está alinhado ao meu perfil?”

OCR detecta informação.

Motor cruza com perfil.

Resposta por áudio.

---

### Cenário 2 — Barcode

Produto identificado.

Dados recuperados.

Perfil consultado.

Resposta.

---

### Cenário 3 — Prato

Visão identifica componentes prováveis.

Há ingrediente incerto.

Sistema pergunta:

> “Esse molho contém creme?”

Usuário responde.

Sistema atualiza evidências.

Retorna orientação.

Esse cenário demonstra inteligência sem fingir certeza.

---

# 57. Métricas de produto/IA

O sistema deve ter observabilidade desde cedo.

Capturar:

```text
capture latency
STT latency
OCR latency
barcode latency
vision latency
decision latency
TTS start latency
end-to-end latency
```

Além de:

```text
model version
runtime
device
RAM
errors
confidence
```

---

# 58. Métrica importante de segurança

Além de accuracy:

> **False-Safe Rate**

Quantos cenários perigosos/incertos receberam uma resposta excessivamente segura?

Objetivo para casos críticos:

```text
~0
```

---

# 59. Métrica de incerteza

> **Uncertainty Recall**

Quando a informação realmente é insuficiente, com que frequência o sistema reconhece que não sabe?

Uma solução madura não é aquela que sempre responde.

É aquela que sabe quando não deveria afirmar.

---

# 60. Backend: entidades iniciais

Não tratar como schema definitivo.

Possíveis entidades:

```text
User

Profile

Goal

Guideline

Restriction

Professional

Device

Interaction

Evidence

Decision

MealRecord

SymptomRecord

FoodProduct

ModelVersion

FeatureFlag
```

O modelo deve poder evoluir sem trauma.

---

# 61. Plataforma profissional

Pós-MVP ou segunda fase.

Provavelmente web.

Fluxo:

```text
Professional Web
        ↓
Eat Control API
        ↓
Patient profile
```

Permissões e consentimento serão essenciais.

---

# 62. Modelo de negócio

O pitch atual trabalha com progressão:

## Fase 1 — B2C

* assinatura;
* acompanhamento alimentar;
* histórico;
* metas;
* personalização.

## Fase 2 — B2B2C

* profissionais;
* clínicas;
* operadoras;
* empregadores;
* farmácias;
* farmacêuticas;
* licença por paciente;
* white label;
* APIs.

A tese comercial é:

> **Não competir com o tratamento. Aumentar o valor da jornada.**

---

# 63. Evolução estratégica

A visão do produto pode ser entendida em três estágios.

## 1 — Assistente alimentar

```text
contexto
orientação
histórico
metas
```

## 2 — Inteligência comportamental

```text
padrões
gatilhos
lembretes
adesão
personalização
```

## 3 — Plataforma integrada

```text
paciente
profissional
clínica
dispositivos
dados autorizados
```

---

# 64. Diferencial real

Evitar vender o diferencial como:

> “Temos um modelo de visão.”

Qualquer concorrente pode ter.

O diferencial desejado é a combinação:

```text
FIRST-PERSON CONTEXT

+

LOW-FRICTION INTERACTION

+

EDGE AI

+

PERSONAL PROFILE

+

DETERMINISTIC SAFETY RULES

+

LONGITUDINAL HISTORY

+

PROFESSIONAL CONTINUITY
```

---

# 65. Princípios de UX

O Eat Control deve parecer:

* rápido;
* discreto;
* útil;
* humano;
* contextual;
* não julgador.

Evitar linguagem moralista como:

> “comida ruim”

ou:

> “você errou”.

Preferir:

> “Essa opção parece ter pouca proteína para a meta cadastrada.”

---

# 66. Princípios de desenvolvimento

Ao implementar:

### Priorizar

* modularidade;
* testabilidade;
* interfaces;
* observabilidade;
* graceful degradation;
* offline-first;
* simplicidade.

### Evitar

* arquitetura distribuída desnecessária;
* microserviços prematuros;
* overengineering;
* modelos gigantes sem benchmark;
* cloud no caminho crítico sem necessidade;
* acoplamento ao DAT por toda a aplicação;
* acoplamento direto a um único modelo.

---

# 67. Boundary do DAT

Todo detalhe específico do Meta Wearables Device Access Toolkit deve ficar concentrado em uma camada própria.

Exemplo conceitual:

```text
GlassesGateway
```

O domínio do Eat Control não deveria saber:

* detalhes de registro do DAT;
* callbacks específicos;
* tipos do SDK;
* detalhes Bluetooth.

Isso é particularmente importante porque o DAT está em developer preview e pode evoluir.

---

# 68. Desenvolvimento sem hardware

O programa possui **Mock Device Kit** justamente para permitir desenvolvimento sem depender do Ray-Ban físico.

Então a arquitetura deve permitir:

```text
MockGlassesGateway

ou

DatGlassesGateway
```

sem alterar o resto do sistema.

Isso também melhora testes automatizados.

---

# 69. Membro com hardware real

Um membro da equipe possui os óculos.

Esse é um ativo importante.

Usar esse membro como Hardware QA para testar:

```text
connect
disconnect
reconnect
capture
stream
voice
STT
TTS
latency
battery
heat
noise
real-world framing
```

Benchmark final de modelos deve acontecer em smartphone físico, não apenas em emulador.

---

# 70. Prioridade de execução

Uma sequência recomendada:

```text
1. App Kotlin compila

2. Arquitetura básica

3. Mock Device Kit

4. Capture

5. TTS

6. OCR

7. Rule Engine

8. Result UI

9. History local

10. Barcode

11. STT

12. Real DAT

13. Real glasses

14. Benchmark

15. Vision model

16. Quantization/optimization

17. Backend sync

18. Polish
```

A ordem pode mudar se circunstâncias técnicas indicarem algo melhor.

---

# 71. Liberdade para implementação

Este documento não deve impedir decisões melhores encontradas durante desenvolvimento.

O agente/dev pode:

* alterar nomenclaturas;
* reorganizar módulos;
* propor fluxo melhor;
* mudar componentes;
* simplificar;
* adiar features;
* adicionar testes;
* sugerir arquitetura mais adequada.

Mas deve preservar as intenções centrais.

---

# 72. Intenções centrais que não devem se perder

## A

O Eat Control é um **produto completo**, não apenas uma demo de computador de visão.

## B

O paciente é a persona principal inicial.

## C

GLP-1 é o posicionamento principal atual.

## D

O smartphone é o cérebro computacional da experiência wearable.

## E

Os Ray-Ban Meta são interface de captura e áudio.

## F

Precisamos do nosso próprio Eat Control App.

## G

Também haverá backend cloud.

## H

O caminho crítico da IA deve ser local sempre que razoável.

## I

O aplicativo deve funcionar sem os óculos.

## J

A solução deve declarar incerteza.

## K

Regras críticas não devem depender exclusivamente de LLM/VLM.

## L

Não estamos construindo um dispositivo médico que diagnostica ou prescreve.

## M

O objetivo maior é transformar decisões isoladas em inteligência longitudinal sobre hábitos.

---

# 73. Definição curta do produto

Se for necessário resumir toda a visão em uma frase:

> **Eat Control AI é um companheiro alimentar inteligente para pessoas em jornada com GLP-1, que entende o contexto real da alimentação por câmera e voz, cruza essa informação com objetivos e orientações individuais, responde no instante da decisão e transforma essas interações em histórico e inteligência comportamental ao longo do tempo.**

---

# 74. Definição curta da arquitetura

> **O Eat Control é um aplicativo Android/Kotlin com inteligência Edge no smartphone, backend para identidade, sincronização e plataforma, e integração via Meta DAT com Ray-Ban Meta para oferecer visão, voz e áudio hands-free.**

---

# 75. Pergunta que deve guiar decisões

Sempre que houver dúvida de produto ou arquitetura, perguntar:

> **Isso ajuda o usuário a tomar uma decisão alimentar melhor, com menos fricção, usando seu contexto individual, sem fingir uma certeza que o sistema não possui?**

Se a resposta for não, provavelmente a feature não pertence ao núcleo do Eat Control.

---

# 76. Resultado esperado do desenvolvimento inicial

Não tentar construir a empresa inteira.

A primeira versão deve provar quatro coisas:

### 1. Produto

Existe uma experiência coerente do Eat Control.

### 2. Wearable

Os óculos realmente reduzem fricção e não são um mero acessório decorativo.

### 3. Tecnologia

A cadeia:

```text
percepção
→ interpretação
→ personalização
→ decisão
→ áudio
```

funciona.

### 4. Futuro

Existe uma arquitetura crível para transformar essa experiência em:

* histórico;
* inteligência comportamental;
* acompanhamento profissional;
* produto recorrente;
* plataforma.

---

# 77. Orientação final ao agente de desenvolvimento

Ao receber este documento:

1. considere-o contexto de produto;
2. inspecione o repositório atual antes de alterar arquitetura;
3. identifique o que já existe;
4. não reimplemente componentes funcionando sem motivo;
5. proponha um plano incremental;
6. priorize uma vertical completa;
7. registre decisões arquiteturais importantes;
8. crie interfaces para componentes substituíveis;
9. adicione observabilidade desde cedo;
10. trate safety e incerteza como requisitos funcionais;
11. mantenha a experiência mobile como produto completo;
12. mantenha a integração wearable desacoplada;
13. prefira soluções simples antes de otimizações sofisticadas;
14. benchmarke antes de escolher modelos definitivos;
15. preserve liberdade para evoluir a arquitetura conforme evidências reais apareçam.

**O objetivo não é simplesmente fazer os Ray-Ban reconhecerem comida.**

O objetivo é construir o primeiro núcleo de uma plataforma que possa acompanhar a pessoa **no instante de cada decisão e ao longo de toda sua jornada alimentar.**
