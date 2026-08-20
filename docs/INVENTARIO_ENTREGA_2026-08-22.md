# Inventário de decisões e pendências — entrega 22/08/2026

Atualizado em: 18/08/2026
Responsável pelo acompanhamento: equipe Eat Control AI
Escopo: produto, implementação, Meta DAT, validação, documento A1–A7, diagrama e vídeo.

> Este é o checklist operacional da entrega. Não registrar credenciais, tokens, senhas ou conteúdo de keystores neste arquivo.

## 1. Legenda

| Estado | Significado |
|---|---|
| `DECIDIDO` | decisão de produto/engenharia fechada, ainda que a implementação possa faltar |
| `IMPLEMENTADO` | existe no código atual |
| `VALIDADO` | foi executado no ambiente-alvo e há evidência reproduzível |
| `HIPÓTESE` | baseline escolhida para teste, ainda não comprovada |
| `PENDENTE` | falta decisão, implementação ou informação |
| `BLOQUEADO` | depende de pessoa, credencial, hardware ou decisão externa |

Responsáveis usados no checklist:

- **EQUIPE**: ação humana no portal, aparelho, conta ou validação de negócio;
- **KIRO**: alteração de código/documentação que pode ser executada no repositório;
- **MÉDICO**: validação do endocrinologista;
- **CONJUNTO**: exige ação coordenada da equipe e do Kiro.

## 2. Resumo executivo

### 2.1 Decisões fechadas

- [x] `DECIDIDO` Público inicial: pessoas em tratamento com GLP-1.
- [x] `DECIDIDO` Restrições alimentares: personalização e expansão, não público principal inicial.
- [x] `DECIDIDO` Visão do produto: prato, cardápio, rótulo e código de barras.
- [x] `DECIDIDO` Frase positiva: **“Não encontrei conflito nas evidências disponíveis.”**
- [x] `DECIDIDO` Reliability-first: nunca usar “seguro”, nunca diagnosticar, prescrever ou ajustar medicamento.
- [x] `DECIDIDO` Caminho crítico da demonstração sem nuvem.
- [x] `DECIDIDO` Vídeo com demonstração real como núcleo; walkthrough apenas para contexto e capacidades futuras identificadas.
- [x] `DECIDIDO` Ocultar os controles sem comportamento: salvar fotos, compartilhar para melhoria e sincronizar histórico.
- [x] `DECIDIDO` Manter ações reais de privacidade: informar processamento local e permitir apagar histórico.
- [x] `DECIDIDO` Voz inicial: push-to-talk, STT estritamente on-device e TTS restrito a voz local; comprovação em modo avião permanece pendente no aparelho-alvo.
- [x] `DECIDIDO` O modo Automático continua sendo a UX principal; `MENU` e `PLATE` são modos assistidos explícitos e ficam disponíveis tanto em debug quanto em release. Controles exclusivamente diagnósticos continuam restritos a debug.
- [x] `DECIDIDO` Escopo funcional entregue localmente: automático, rótulo, código de barras, tabela nutricional, cardápio e prato assistido. Cardápio e prato exigem confirmação; prato não promete volume ou macros por foto.
- [x] `DECIDIDO` Evolução nutricional incremental: `Nutrition Snapshot` objetivo → acompanhamento diário → nutri digital com regras e linguagem validadas por profissional de saúde.

### 2.2 Verdade atual do produto

| Capacidade | Estado | Evidência/observação |
|---|---|---|
| Modo Automático | `IMPLEMENTADO` | UX principal; barcode primeiro e OCR apenas quando necessário |
| Rótulo/OCR | `IMPLEMENTADO` | ML Kit on-device |
| Código de barras | `IMPLEMENTADO` | ML Kit + catálogo local demonstrativo; EAN desconhecido não recebe composição inventada |
| Tabela nutricional / macros | `IMPLEMENTADO`, `VALIDADO` localmente | parser, totais, metas com consumo zero e atualização preservando os metadados do `MealRecord`; cobertos pela suíte JVM |
| Confirmação de alérgeno | `IMPLEMENTADO` | confirmação vira evidência e recalcula decisão |
| Persistência de perfil/histórico | `IMPLEMENTADO` | DataStore local, exclusão de backup e retrocompatibilidade dos campos assistidos |
| Onboarding/perfil de produção | `IMPLEMENTADO` | `ProfileState.Loading/NeedsOnboarding/Ready`, edição local de nome, GLP-1 e `MacroGoals`; perfil legado `demo-joao` migra para onboarding |
| Dashboard factual | `IMPLEMENTADO` | Home/Plan usam `dailyProgress`; não exibem nome, hidratação, meta ou percentual demonstrativo |
| Cardápio (`MENU`) | `IMPLEMENTADO`, não `VALIDADO` em hardware | OCR + `MenuParser` próprio; seção, nome, descrição, preço e termos observados; preço não vira nutriente; confirmação obrigatória antes do histórico |
| Prato (`PLATE`) | `IMPLEMENTADO`, não `VALIDADO` em hardware | ML Kit bundled/offline + classes fechadas + gate experimental `0,65`; desconhecidos não são forçados; confirmação obrigatória; sem volume/macros automáticos |
| Regras GLP-1 | `BLOQUEADO` | claims existem, mas nenhum rule pack pode executar sem validação profissional |
| Composição/porção/preparo de prato | `BLOQUEADO` | falta fonte auditável de composição e medidas; a v1 registra componentes confirmados, não macros |
| Perguntas sobre produto desconhecido | `PENDENTE` | confirmação atual cobre alérgenos e fluxos assistidos, não uma entrevista nutricional completa |
| Registro/autorização DAT | `IMPLEMENTADO`, não `VALIDADO` em hardware | `startRegistration()` retorna `Result`; UI distingue abertura do registro, autorização, conexão temporária e erros |
| Sessão DAT sob demanda | `IMPLEMENTADO`, não `VALIDADO` em hardware | estado autorizado é “conecta ao analisar”; captura usa sessão curta e fecha em `finally` |
| DAT em Mock Device Kit | `IMPLEMENTADO`, não `VALIDADO` nesta rodada | teste instrumentado existe, sem relatório instrumentado atual |
| Ray-Ban físico | `BLOQUEADO` | exige aparelho, Meta AI, contas, versão e canal configurados |
| Métricas físicas | `BLOQUEADO` | latência, bateria, temperatura, FPS, rotação e enquadramento dependem do hardware-alvo |

A rastreabilidade clínica canônica está em [`DATA_SOURCES.md`](DATA_SOURCES.md), claims `AJCN-GLP1-01` a `AJCN-GLP1-10`. Este inventário registra estado operacional e não duplica nem promove esses claims a regras.

## 3. O que está travado na equipe

Estes são os únicos bloqueios que o Kiro não consegue resolver sozinho.

| ID | Ação da equipe | Por que é necessária | Bloqueia |
|---|---|---|---|
| EQ-01 | `CONCLUÍDO`: assinatura informada foi comparada ao certificado do debug keystore desta máquina e corresponde exatamente | permite usar o build debug no beta/Ideathon imediato; não define identidade de produção | — |
| EQ-02 | `CONCLUÍDO`: origem da assinatura cadastrada identificada como debug keystore local | elimina a incerteza anterior, mas não substitui um certificado release | — |
| EQ-03 | Decidir/criar o keystore definitivo de release e guardar senhas fora do repositório e do chat | Gradle aceita configuração release local, mas o keystore final ainda não existe | APK final instalável e identidade estável |
| EQ-04 | `CONCLUÍDO`: Application ID e Client Token foram configurados localmente, validados por presença/comprimento e resolvidos no manifest debug sem revelar valores | permite o build debug DAT; os segredos continuam fora do Git e deste documento | — |
| EQ-05 | Fazer os cliques autenticados no Developer Center: salvar configuração, criar versão e canal | o Kiro não acessa a sessão autenticada | beta DAT |
| EQ-06 | Fornecer e-mails associados a Meta Accounts para o canal de testes | canais DAT são invite-only | testes com outras contas |
| EQ-07 | Aceitar convites e selecionar canal/permissões no app Meta AI | ação por conta e aparelho | registro do app |
| EQ-08 | Disponibilizar o Ray-Ban pessoal e smartphone-alvo | Mock Device Kit não valida Bluetooth, lente, áudio ou frame físico | validação real |
| EQ-09 | Obter do endocrinologista regras aprovadas para gordura, porção, preparo e linguagem de desconforto | limites universais não podem ser inventados | motor GLP-1 e narrativa clínica |
| EQ-10 | `CONCLUÍDO`: classes visuais fechadas, gate experimental, confirmação obrigatória e limites de MENU/PLATE foram definidos e implementados sem promessa de macros automáticos | fecha o recorte local; benchmark e validação física permanecem em EQ-08 | — |

### 3.1 Dados esperados do endocrinologista

Não pedir uma “dieta universal”. Pedir um conjunto de regras demonstrativas, rastreáveis e configuráveis:

- quais propriedades importam: gordura total, gordura saturada, porção, volume, fritura, molho cremoso etc.;
- se existem limites demonstrativos aceitáveis e em que unidade;
- como comunicar “possível desconforto” sem afirmar causalidade;
- quando responder “atenção”, “precisa confirmar” ou “informação insuficiente”;
- quais frases são proibidas;
- versão/data da validação.

## 4. O que o Kiro pode adiantar

### 4.1 Lote P0 — pode começar sem decisão médica

- [x] `KIRO` Ocultar os três toggles sem comportamento na tela de perfil.
- [x] `KIRO` Substituir os toggles por um painel factual: processamento local, fotos não persistidas, histórico local.
- [x] `KIRO` Desabilitar backup e excluir DataStore das regras de backup/transferência Android.
- [x] `KIRO` Garantir que o frame saia da memória ao encerrar/substituir a interação.
- [x] `KIRO` Remover fallback do STT para o reconhecedor genérico potencialmente online.
- [x] `KIRO` Exigir `createOnDeviceSpeechRecognizer()` e mostrar fallback de UX quando indisponível.
- [x] `KIRO` Selecionar voz TTS pt-BR local e rejeitar voz que exige rede; validação em modo avião ainda é física.
- [x] `KIRO` Corrigir sequência HFP: rotear antes da voz/câmera e liberar após o fim do TTS/análise.
- [x] `KIRO` Adicionar metadados DAT por placeholders ao Manifest.
- [x] `KIRO` Implementar observação de `registrationState` na UI.
- [x] `KIRO` Implementar o fluxo de `Permission.CAMERA` do DAT.
- [x] `KIRO` Tratar `BLUETOOTH_CONNECT` em runtime.
- [x] `KIRO` Fechar stream/câmera/sessão DAT em `finally` e ao trocar de fonte.
- [x] `KIRO` Impedir conexão DAT duplicada e voltar para fallback quando a conexão falhar.
- [x] `KIRO` Preparar `signingConfig` que leia valores locais, sem incluir segredo no Git.

### 4.2 Lote P1 — pode avançar com schema, mas as regras finais dependem do médico

- [ ] `KIRO` Criar `NutritionEvidence`: gordura total/saturada, porção, preparo, fonte e confiança clínica estruturada.
- [ ] `KIRO` Criar interface de rule pack GLP-1 local e versionado.
- [ ] `KIRO` Criar mensagens GLP-1 com linguagem aprovada, sem diagnóstico ou garantia.
- [x] `KIRO` Implementar cardápio como OCR + `MenuParser` próprio + revisão e confirmação factual.
- [ ] `KIRO` Implementar fallback completo de produto desconhecido: OCR → catálogo local → perguntas → insuficiente.
- [ ] `KIRO` Fazer o foco falado (“tem glúten?”, “é gorduroso?”) influenciar a interação.

### 4.3 Lote P2 — recorte local implementado; validação física pendente

- [x] `CONJUNTO` Definir classes visuais fechadas para a demo de prato.
- [x] `KIRO` Criar provider visual local com score, versão e proveniência.
- [x] `KIRO` Integrar rotulagem ML Kit bundled apenas como hipótese, nunca como evidência de ausência.
- [x] `KIRO` Exigir confirmação/correção dos componentes antes do histórico.
- [ ] `CONJUNTO` Fechar composição, medidas e perguntas de preparo/ingrediente oculto/porção; até lá não calcular macros.
- [ ] `CONJUNTO` Validar gate, classes e taxa de correção em fotos reais no aparelho-alvo.

### 4.4 Estratégia incremental de nutrição e macros

A proposta de “nutri digital” deve aparecer na ideação, mas não como prescrição já entregue. O caminho reduz risco técnico e clínico:

#### Etapa N1 — Nutrition Snapshot (núcleo funcional implementado)

Entrada por tabela nutricional OCR ou produto estruturado; saída factual e auditável:

- nutrientes por porção e, quando disponível, por 100 g/ml;
- tamanho e quantidade de porções consumidas confirmados pelo usuário;
- proteína, carboidratos, gorduras totais/saturadas, fibras, sódio e energia;
- soma diária local e comparação objetiva com metas configuradas;
- estado `INFORMAÇÃO_INSUFICIENTE` quando unidade, porção ou leitura não forem confiáveis;
- proveniência e confiança por campo; nunca completar número ausente por inferência silenciosa.

Critério de aceite: uma foto legível de tabela ou item do catálogo produz estrutura revisável; o usuário confirma porção; totais são matematicamente reproduzíveis; nenhuma sugestão clínica é emitida.

#### Etapa N2 — acompanhamento diário

- painel de consumo versus metas configuradas;
- indicação factual de “faltam X g para a meta configurada” ou “excede X g”, sem classificar alimento isolado como bom/ruim;
- histórico e tendências locais;
- metas definidas pelo usuário/profissional, não inventadas pelo app;
- correções manuais e exclusão de dados.

#### Etapa N3 — nutri digital assistiva (após filtro 1)

- sugestões contextuais de opções com mais proteína/carboidrato/fibra ou menos gordura, sempre explicando a evidência;
- rule pack versionado e aprovado por nutricionista/endocrinologista para o recorte GLP-1;
- sem diagnóstico, prescrição, ajuste de medicamento ou substituição de acompanhamento profissional;
- escalonamento para “confirme com seu profissional” quando houver condição clínica, meta conflitante ou baixa confiança.

`IMPLEMENTADO`: núcleo N1 para tabela nutricional/produto estruturado, confirmação, totais locais e metas configuradas. `IMPLEMENTADO`, recorte inicial de N2: consumo diário versus metas locais, inclusive metas com consumo zero. `PENDENTE`: tendências e correções mais amplas de N2. `BLOQUEADO`: N3 e qualquer recomendação GLP-1 até o filtro 1 e a validação profissional. MENU e PLATE não geram macros sem item, quantidade e fonte auditáveis.

## 5. Decisão de privacidade e controles

### 5.1 Decisão

`DECIDIDO`: ocultar, não implementar agora:

- salvar fotos;
- compartilhar para melhoria;
- sincronizar histórico.

### 5.2 Justificativa

- “Salvar fotos” contradiz retenção zero e não agrega valor à demonstração.
- Compartilhamento exige destino, consentimento, revogação, segurança, política de retenção e base legal.
- Sincronização exige backend, identidade, conflito e exclusão remota.
- Nenhum dos três melhora o loop principal “olhar → perguntar → ouvir”.

### 5.3 O que permanece na interface

- “Processamento realizado no telefone.”
- “Fotos não são salvas.”
- “Histórico fica neste aparelho.” somente depois de corrigir backup.
- botão real “Apagar histórico”.
- versão da política/regra usada na análise, se houver espaço.

### 5.4 Critérios de aceite

- [x] toggles sem comportamento não aparecem no release;
- [x] imagem não é gravada em arquivo/DataStore;
- [x] imagem não permanece no ViewModel após fechar/substituir resultado;
- [x] DataStore não entra em cloud backup/device transfer;
- [x] apagar histórico remove os `MealRecord` locais;
- [x] nenhum upload ocorre no caminho crítico implementado.

## 6. Decisão de voz offline

### 6.1 O que as fontes locais sugerem

1. A palestra “O Agente Mínimo Viável” diz que local versus nuvem é decisão peça a peça, não obrigação absoluta. STT e TTS são fortes candidatos a local por latência e privacidade:
   `docs/fontes/Palestra_Agente_Minimo_Viavel_RAG/sections/04_local_nuvem.md` (página 13).
2. O curso recomenda, para o primeiro hackathon, push-to-talk e APIs nativas; para garantia 100% local, cita Whisper tiny/Vosk e Piper:
   `docs/fontes/meta_course_rag/sections/UN12_12_13_Voz_no_dispositivo_STT_e_TTS_M_dulo_1.md` (páginas 80–93).
3. `EXTRA_PREFER_OFFLINE` é preferência, não garantia. `createOnDeviceSpeechRecognizer()` usa apenas o reconhecedor local quando o pacote pt-BR está instalado.
4. TTS nativo pode usar rede conforme a voz instalada; o curso manda testar em modo avião.
5. “Hey Meta” não é acessível ao app. A alternativa é push-to-talk ou wake word própria.
6. DAT não entrega áudio. Microfone e alto-falantes usam HFP/A2DP do Android:
   `docs/fontes/meta_course_rag/sections/UN13_13_6_Microfone_e_alto_falantes_via_Bluetooth.md` (páginas 48–55).
7. O ADR do projeto é mais rígido que a palestra: o caminho crítico deve funcionar sem nuvem:
   `docs/adr/0002-local-first-edge-ai.md`.

### 6.2 Decisão para a entrega

`DECIDIDO`:

- gatilho: push-to-talk;
- STT: Android on-device apenas;
- sem fallback para reconhecedor genérico;
- se pt-BR local não existir, manter análise por toque e informar indisponibilidade da voz offline;
- TTS: engine Android com voz pt-BR baixada e comprovada em modo avião;
- respostas com até 15 palavras sempre que possível;
- feedback sonoro imediato antes do processamento;
- Whisper/Vosk/Piper ficam em benchmark pós-entrega, salvo falha da baseline nativa.

### 6.3 Critérios de aceite

- [ ] comando funciona em modo avião;
- [ ] nenhum pacote de áudio é enviado à rede;
- [ ] ausência do recognizer local não aciona serviço genérico;
- [ ] fallback visual/toque continua funcional;
- [ ] TTS fala em modo avião;
- [ ] HFP é configurado antes da captura quando voz e câmera forem usadas juntas;
- [ ] roteamento Bluetooth é liberado ao final;
- [ ] erro `NO_MATCH` pede repetição sem travar o fluxo;
- [ ] primeira confirmação sonora ocorre antes da inferência;
- [ ] medir tempo até primeira sílaba.

## 7. Demonstração versus walkthrough

### 7.1 Decisão

`DECIDIDO`: demonstração real como núcleo do vídeo.

Se fosse necessário escolher apenas um formato, escolher **demonstração**, porque viabilidade técnica vale pontos e já existe um app funcional.

### 7.2 Estrutura recomendada para 2–3 minutos

| Tempo | Conteúdo | Tipo |
|---:|---|---|
| 0:00–0:20 | pessoa em GLP-1 diante de uma decisão alimentar | walkthrough contextual |
| 0:20–0:40 | problema e evidência das três entrevistas exploratórias | narrativa |
| 0:40–1:35 | captura → barcode/OCR → regra → resposta por áudio | demonstração real |
| 1:35–1:55 | rótulo ilegível/produto desconhecido → pergunta/fallback | demonstração real, se implementado |
| 1:55–2:15 | perfil e histórico com dados identificados como demonstração | demonstração real |
| 2:15–2:35 | cardápio/prato assistidos: candidato → confirmação → registro; deixar macros automáticos como expansão não entregue | demonstração real + limites explícitos |
| 2:35–2:50 | arquitetura local, privacidade e impacto | síntese |

### 7.3 Regras de honestidade

- [ ] dados pré-carregados são identificados como “dados de demonstração”;
- [ ] cena encenada não é apresentada como inferência real;
- [ ] prato/cardápio só são chamados de funcionais se executarem a pipeline gravada;
- [ ] número não medido aparece como meta, nunca resultado;
- [ ] mock aparece como mock;
- [ ] não usar “seguro”, “diagnóstico” ou “evita sintomas”.

## 8. Quanto deve ser definido agora

### 8.1 Regra

Quanto mais definido, melhor **quando a definição é verificável**. Uma baseline explícita é melhor que “a definir”; um número arbitrário é pior que uma hipótese de benchmark.

### 8.2 Definir completamente nos artefatos

- problema, público e proposta de valor;
- gatilho;
- atores e permissões;
- fluxos principal e de exceção;
- ferramentas e ordem de roteamento;
- fronteiras óculos/smartphone/nuvem;
- formatos de entrada e saída;
- dados persistidos e descartados;
- precedência das evidências;
- guardrails clínicos e de privacidade;
- baseline de modelos/providers;
- critérios de aceite;
- métricas e método de medição;
- roadmap e status real.

### 8.3 Definir como baseline experimental

| Item | Baseline | Estado |
|---|---|---|
| Captura | `capturePhoto()` por evento; sessão curta | `IMPLEMENTADO`, falta validar no Ray-Ban físico |
| Stream técnico | apenas durante captura; fechamento em `finally` | `IMPLEMENTADO`, falta validar no hardware |
| Configuração | comparar HIGH/15, HIGH/24, MEDIUM/15 e MEDIUM/24 | `HIPÓTESE` |
| LOW | não usar inicialmente para OCR de texto pequeno | `HIPÓTESE` |
| Imagem interna | Bitmap/HEIC → JPEG; fallback de frame precisa validar layout | `IMPLEMENTADO`, parcial |
| Distorção | centralização + crop central + correção de perspectiva | `HIPÓTESE` |
| Undistortion | somente após calibração no hardware real | `PENDENTE DE MEDIÇÃO` |
| OCR | ML Kit on-device | `IMPLEMENTADO` |
| Barcode | ML Kit on-device | `IMPLEMENTADO` |
| STT | SpeechRecognizer on-device | `IMPLEMENTADO`, falta validar em modo avião/aparelho-alvo |
| TTS | Android TTS com voz offline instalada | `IMPLEMENTADO`, falta validar em modo avião/aparelho-alvo |
| Tabela nutricional | OCR + parser estruturado + confirmação de porção | `IMPLEMENTADO`, suíte JVM aprovada |
| Cardápio | OCR + `MenuParser` próprio + revisão/confirmação | `IMPLEMENTADO`, falta benchmark/hardware |
| Prato | ML Kit bundled + classes fechadas + confirmação | `IMPLEMENTADO`, falta benchmark/hardware; sem composição/macros |
| Regras GLP-1 | rule pack local validado por médico | `BLOQUEADO` por EQ-09 |
| RAG em runtime | não usar no caminho crítico do MVP | `DECIDIDO` |

### 8.4 Uso dos arquivos RAG

Os corpus em `docs/fontes/*_rag/` fundamentam decisões de engenharia e preservam proveniência do curso. Eles **não** são automaticamente uma base médica para o app.

- Usar `sections/` para recuperar tópicos específicos.
- Usar `units/`/`FULL.md` para confirmar contexto e números.
- Usar `chunks/*.jsonl` somente se houver pipeline de ingestão/consulta.
- Não colocar o curso inteiro dentro do APK.
- Orientações GLP-1 de runtime devem vir de rule pack próprio, versionado e validado pelo endocrinologista.

## 9. Meta DAT — checklist de configuração e distribuição

### 9.1 Estado verificado localmente em 18/08/2026

- [x] package Android: `com.eatcontrolai.app`;
- [x] App signature do Developer Center corresponde ao certificado do debug keystore desta máquina;
- [x] Camera access habilitado no portal;
- [x] `mwdat_application_id` existe em `local.properties`, não é placeholder e tem 16 caracteres;
- [x] `mwdat_client_token` existe em `local.properties`, não é placeholder e tem 52 caracteres;
- [x] Manifest debug regenerado e verificado: os dois metadados foram resolvidos com os mesmos comprimentos, sem imprimir os valores;
- [x] `local.properties`, `*.jks`, `*.keystore` e snapshots do Developer Center estão ignorados pelo Git;
- [x] APK debug fresco: `app\build\outputs\apk\debug\app-debug.apk` (`176824051` bytes; `168,63 MiB`);
- [x] SHA-256 do APK: `724B3C7F5975A32DA6EA0A72271E1E5BDF6A041AB795B933E558CAE9BBE7E2DE`;
- [x] `apksigner` validou assinatura v2, um signatário; v1/v3/v3.1/v4 ausentes;
- [x] certificado debug SHA-256: `dd81e9c98aa58d4a514c1c2a4dfd8cc41546c9864425ff5e011f39ab7078ed66`; DN `C=US, O=Android, CN=Android Debug`;
- [ ] rationale corrigida precisa ser salva manualmente no portal autenticado;
- [ ] keystore e certificado de release ainda não existem/configurados;
- [ ] versão ainda não criada;
- [ ] release channel ainda não criado.

O HTML salvo do Developer Center permanece somente local e não deve ser reproduzido, anexado ou versionado, pois pode conter o Client Token.

### 9.2 Justificativa final para Camera access

Substituir no portal a afirmação incorreta de que não existe streaming por este texto:

> A câmera é acionada somente após uma solicitação explícita do usuário. Durante essa ação, o SDK abre um stream técnico temporário em HIGH/15 FPS para obter uma única foto ou frame e o encerra imediatamente após a análise. Não há captura contínua nem em segundo plano. A imagem permanece apenas em memória, é processada localmente no telefone, não é persistida e não é enviada aos nossos servidores.

Esse texto corresponde ao código atual: `stream.start()` é necessário para receber a foto/frame, enquanto o `finally` fecha stream, câmera e sessão após cada interação. Não usar “não há streaming”; usar “não há captura contínua nem em segundo plano”.

### 9.3 Assinatura: escolha explícita antes de gerar

Existem dois caminhos diferentes:

1. **Beta imediato com o parceiro:** manter o APK debug assinado pelo debug keystore já cadastrado no portal. Serve para validar Ray-Ban/DAT, mas não é uma identidade de produção.
2. **Release de produção:** criar uma chave definitiva, guardar keystore e senhas fora do repositório e cadastrar no Developer Center a assinatura desse certificado antes de gerar o canal correspondente.

A chave de produção não deve ser gerada silenciosamente: perder o arquivo ou as senhas impede futuras atualizações assinadas com a mesma identidade. Depois de escolher local seguro, alias e senhas, executar manualmente em PowerShell (o comando solicitará os dados sensíveis sem colocá-los no chat):

```powershell
New-Item -ItemType Directory -Path "keystores" -Force
keytool -genkeypair -v -keystore "keystores\eatcontrol-release.jks" -alias "eatcontrol-release" -keyalg RSA -keysize 4096 -validity 10000
keytool -list -v -keystore "keystores\eatcontrol-release.jks" -alias "eatcontrol-release"
```

Guardar cópia criptografada e testada do `.jks` e das senhas. Em seguida, adicionar somente a esta máquina:

```properties
release_store_file=keystores/eatcontrol-release.jks
release_store_password=<senha do keystore>
release_key_alias=eatcontrol-release
release_key_password=<senha da chave>
```

O Gradle já lê essas quatro propriedades. No estado atual, todas estão ausentes; portanto ainda não existe APK release com identidade definitiva.

### 9.4 Antes de criar uma versão DAT

Para o **beta debug já cadastrado**:

- [x] Application ID e Client Token configurados localmente;
- [x] Manifest mesclado validado sem revelar credenciais;
- [ ] instalar o APK debug desta máquina no smartphone do parceiro;
- [ ] salvar a rationale corrigida;
- [ ] validar registro, permissão, captura e áudio em hardware real.

Para o **release definitivo**:

- [ ] criar e fazer backup do keystore de produção;
- [ ] configurar as quatro propriedades de assinatura locais;
- [ ] obter a assinatura do certificado release e cadastrá-la no Developer Center;
- [ ] gerar/instalar o APK assinado correspondente;
- [ ] validar câmera e áudio novamente, pois assinatura e variante mudaram.

### 9.5 Passos no portal — responsabilidade da equipe

- [ ] abrir **Configuration**, substituir a rationale e clicar **Save**;
- [ ] confirmar que package e App signature correspondem exatamente ao APK que será instalado;
- [ ] abrir **Distribute → Versions** e clicar **Create new version**;
- [ ] selecionar o tipo oferecido para a primeira versão;
- [ ] aguardar o status `Ready`;
- [ ] abrir **Release channels**;
- [ ] criar `eatcontrol-beta` com descrição clara;
- [ ] associar a versão `Ready`;
- [ ] adicionar os e-mails vinculados às Meta Accounts dos testadores;
- [ ] revisar e clicar **Create release channel**;
- [ ] confirmar recebimento e aceite dos convites.

### 9.6 Passos no Meta AI — cada testador

- [ ] aceitar o convite no e-mail associado à Meta Account;
- [ ] abrir Meta AI e confirmar que os óculos estão pareados;
- [ ] abrir `Settings → Release Channel` e selecionar `eatcontrol-beta`;
- [ ] abrir `Settings → App connections`;
- [ ] registrar/conectar o Eat Control;
- [ ] conceder a permissão de câmera solicitada;
- [ ] instalar no telefone o APK cuja assinatura foi cadastrada;
- [ ] testar autorização, captura sob demanda, encerramento da sessão e revogação.

> O release channel autoriza a integração DAT; ele não distribui o APK. O APK deve ser enviado e instalado separadamente.

### 9.7 Pacote compacto para o parceiro

#### Beta debug testável agora

- [x] Artefato gerado: `app\build\outputs\apk\debug\app-debug.apk`.
- [x] SHA-256 e certificado conferidos; a assinatura debug corresponde à cadastrada no portal.
- [ ] **Equipe:** salvar a rationale de câmera da seção 9.2 no portal autenticado.
- [ ] **Equipe:** criar uma versão, aguardar `Ready`, criar/associar o canal `eatcontrol-beta` e convidar as Meta Accounts.
- [ ] **Parceiro:** receber o APK por canal privado, conferir o SHA-256 e instalá-lo separadamente no smartphone-alvo.
- [ ] **Parceiro:** aceitar o convite, parear o Ray-Ban no Meta AI, selecionar `eatcontrol-beta` e conectar o Eat Control em **App connections**.
- [ ] **Parceiro:** conceder câmera e validar abertura do registro, autorização e o estado “Autorizado · conecta ao analisar”.
- [ ] **Conjunto:** executar capturas sob demanda de rótulo/barcode, MENU e PLATE; confirmar que MENU/PLATE só entram no histórico após confirmação.
- [ ] **Conjunto:** validar áudio Bluetooth/HFP, STT/TTS offline e fallback visual/toque.
- [ ] **Conjunto:** confirmar encerramento da sessão após cada análise e testar revogação/desconexão.
- [ ] **Conjunto:** registrar modo avião, enquadramento, rotação, formato do frame, latência, bateria e temperatura no aparelho real.

#### Release definitivo ainda bloqueado

- [ ] definir/criar o keystore release, alias, senhas e backup criptografado testado;
- [ ] configurar as quatro propriedades locais de assinatura;
- [ ] cadastrar o certificado release no Developer Center;
- [ ] gerar e validar o APK release correspondente;
- [ ] repetir autorização, captura, áudio e revogação com a variante release.

> **Limite explícito:** o APK disponível é um **beta debug**, não um release de produção. O modo PLATE registra componentes visuais confirmados; não mede volume nem calcula macros automaticamente sem quantidade e fonte de composição auditáveis.

## 10. Implementação por prioridade e data

### 18/08 — P0: confiança, privacidade e integração

- [x] ocultar toggles sem comportamento;
- [x] corrigir backup e retenção;
- [x] tornar voz estritamente offline no código;
- [x] corrigir HFP;
- [x] configurar Manifest DAT;
- [x] implementar registro/permissão/lifecycle e diagnóstico de autorização;
- [x] remover perfil/metas demonstrativos e implementar onboarding/edição local;
- [x] tornar Home/Plan factuais com `dailyProgress`;
- [ ] definir assinatura release.

### 19/08 — P1: núcleo GLP-1 e exceções

- [ ] obter primeira versão do rule pack médico;
- [ ] implementar `NutritionEvidence` clínico estruturado;
- [ ] implementar regras e mensagens GLP-1;
- [x] implementar cardápio OCR + `MenuParser` + confirmação;
- [x] implementar `Nutrition Snapshot`: tabela, porção, macros e totais objetivos;
- [x] implementar tela de configuração de metas de macro;
- [x] implementar PLATE assistido conforme `docs/ESTRATEGIA_PRATO_VISAO.md`;
- [ ] implementar fallback completo de produto desconhecido/perguntas;
- [ ] revisar linguagem com o endocrinologista.

### 20/08 — P2: hardware e prato

- [ ] executar matriz de captura HIGH/MEDIUM × 15/24;
- [ ] confirmar formato, rotação e distorção;
- [ ] testar voz HFP e TTS nos óculos;
- [x] definir gate experimental inicial de prato em `0,65` (hipótese de engenharia, não confiança clínica);
- [ ] benchmarkar o gate e as classes em fotos reais;
- [ ] registrar falhas e fallback.

### 21/08 — validação e gravação

- [x] executar suíte JVM atual (`109` testes, `17` suítes, zero falhas/erros/skips);
- [x] executar compilação, cobertura, lint e `check`;
- [ ] executar testes instrumentados;
- [ ] medir latência por etapa;
- [ ] medir bateria/temperatura por sessão curta;
- [ ] criar versão/canal DAT;
- [ ] gravar demonstração real;
- [ ] congelar texto A1–A7 e Mermaid.

### 22/08 — auditoria final

- [ ] documento, diagrama e vídeo contam a mesma história;
- [x] implementado/planejado/validado estão separados neste inventário;
- [x] metas não aparecem como resultados no app;
- [ ] todas as fontes e versões estão citadas nos artefatos finais;
- [x] APK beta debug e fallback mock estão disponíveis;
- [ ] vídeo final está disponível;
- [x] nenhuma credencial foi adicionada ao diff ou aos registros de validação.

## 11. Entregáveis A1–A7

| Item | Conteúdo | Estado |
|---|---|---|
| A1 | problema específico no instante da decisão alimentar | `DECIDIDO`, falta redação final |
| A2 | pessoa em GLP-1; restrições como expansão | `DECIDIDO`, falta redação final |
| A3 | walkthrough rótulo/barcode com regra GLP-1 | `PENDENTE` de motor GLP-1 |
| A4 | rótulo ilegível + produto desconhecido + perguntas | `PENDENTE` de implementação |
| A5 | Android nativo, local-first, regras determinísticas, providers e fallback | `DECIDIDO` |
| A6 | comparação com scanners/diários e substituto celular | `PENDENTE` de pesquisa competitiva dedicada |
| A7 | IA, câmera, áudio, privacidade e bateria | `DECIDIDO`, falta evidência medida |
| Mermaid | óculos / smartphone / nuvem futura, tecnologias e formatos | `PENDENTE` de atualização final |
| Vídeo | demonstração real + breve contexto/walkthrough futuro | `DECIDIDO`, falta gravação |

## 12. Métricas e evidências

### 12.1 Metas — ainda não resultados

- primeira confirmação sonora: imediata;
- primeira sílaba: alvo < 1 s; aceitável entre 1–3 s com feedback;
- p95 OCR/barcode ponta a ponta: alvo documental < 2,5 s;
- false-safe em cenários críticos: alvo zero;
- retenção de imagem em disco: zero;
- funcionamento do caminho crítico em modo avião: obrigatório para a demo;
- captura sob demanda: uma interação por gatilho, sem monitoramento contínuo.

### 12.2 Evidência a produzir

- [ ] tabela com aparelho, Android, modelo/provider e versão;
- [ ] p50/p90/p95 por estágio;
- [ ] WER/intenção para comandos pt-BR;
- [ ] time-to-first-audio;
- [ ] taxa de OCR em fotos reais;
- [ ] taxa de barcode em produtos reais;
- [ ] matriz de decisões GLP-1 revisada pelo médico;
- [ ] comportamento sem rede;
- [ ] consumo de bateria e temperatura;
- [ ] resultado de Mock Device Kit e Ray-Ban físico;
- [ ] tamanho do APK por ABI.

### 12.3 Validação local do loop inicial — 18/08/2026

- [x] gate completo executado com `cmd /c "gradlew.bat :app:compileDebugKotlin :app:testDebugUnitTest :app:coverageReport :app:coverageVerify :app:check --no-daemon --console=plain"` — `BUILD SUCCESSFUL in 10m 40s`;
- [x] `:app:testDebugUnitTest` — `109` testes em `17` suítes; zero falhas, erros ou skips;
- [x] cobertura total — `81,2%` de linhas (`1123/1383`) e `67,7%` de ramos (`389/575`);
- [x] cobertura do domínio determinístico — `99,5%` de linhas (`652/655`) e `81,3%` de ramos (`292/359`);
- [x] `:app:coverageVerify` — gate de domínio mantido em `0,99` para linhas e `0,80` para ramos;
- [x] relatório JaCoCo: `app/build/reports/jacoco/coverageReport/coverageReport.xml`;
- [x] lint e `:app:check` — concluídos sem falha;
- [x] `cmd /c "gradlew.bat :app:assembleDebug --no-daemon --console=plain"` — `BUILD SUCCESSFUL in 8m 50s`;
- [x] APK debug — tamanho, hash e assinatura v2 registrados nas seções 9.1 e 9.7;
- [x] `git diff --check` — sem erro de whitespace; restam apenas avisos LF/CRLF do Windows;
- [x] Application ID, Client Token e material de assinatura não foram impressos nem versionados;
- [ ] modo avião, TTS/HFP, DAT físico, frame, rotação, latência, bateria e temperatura — dependem do aparelho/óculos.

Avisos não bloqueantes observados: incompatibilidade de versão do XML do SDK (ferramenta entende até 3 e encontrou 4) e bibliotecas `.so` que não puderam ser stripadas no `assembleDebug`. Ambos os builds foram concluídos com sucesso.

## 13. Riscos principais

| Risco | Estado | Mitigação |
|---|---|---|
| público GLP-1 sem lógica GLP-1 | alto | implementar rule pack antes do vídeo |
| prato amplo demais para o prazo | alto | classes fechadas + confirmação ou marcar como futuro |
| falsa segurança médica/alimentar | alto | evidência/proveniência + insuficiência explícita |
| rationale da Meta divergir do stream real | alto | lifecycle curto + texto corrigido |
| assinatura do portal não corresponder ao APK | alto | comparar certificado antes da versão |
| voz usar rede sem transparência | alto | recognizer on-device apenas + modo avião |
| TTS não sair nos óculos | médio | sequência HFP e teste físico |
| backup contradizer “local” | alto | excluir DataStore/desabilitar backup |
| toggles sugerirem funções inexistentes | médio | ocultar no MVP |
| métricas documentais sem medição | alto | publicar como metas até medir |

## 14. Perguntas ainda abertas

- [x] Qual certificado gerou a App signature cadastrada? **Debug keystore local, verificado por comparação exata.**
- [ ] Há um keystore release definitivo? Onde será guardado?
- [ ] Quais regras GLP-1 o endocrinologista aprova?
- [x] Quais classes fechadas e método de confirmação tornam prato funcional e demonstrável? **Doze classes amplas, gate experimental `0,65`, desconhecidos não forçados e confirmação/correção obrigatória; sem macro automático.**
- [x] Quais campos/unidades e metas configuráveis entram no primeiro `Nutrition Snapshot`? **Valores declarados por porção/100 g ou ml quando disponíveis, consumo confirmado e `MacroGoals` locais editáveis; ausências não são inventadas.**
- [ ] Qual catálogo offline de produtos fará parte da demo?
- [ ] Qual aparelho Android será o alvo oficial?
- [ ] Quais contas Meta entrarão no canal beta?
- [ ] Quais dois concorrentes serão comparados em A6?

## 15. Referências usadas para estas decisões

### Fontes locais do curso/RAG

- `docs/fontes/Palestra_Agente_Minimo_Viavel_RAG/sections/03_orcamento_latencia.md`
- `docs/fontes/Palestra_Agente_Minimo_Viavel_RAG/sections/04_local_nuvem.md`
- `docs/fontes/Palestra_Agente_Minimo_Viavel_RAG/sections/05_bateria_memoria.md`
- `docs/fontes/Palestra_Agente_Minimo_Viavel_RAG/sections/06_escopo_mvp.md`
- `docs/fontes/meta_course_rag/sections/UN12_12_13_Voz_no_dispositivo_STT_e_TTS_M_dulo_1.md`
- `docs/fontes/meta_course_rag/sections/UN13_13_6_Microfone_e_alto_falantes_via_Bluetooth.md`
- `docs/fontes/meta_course_rag/sections/UN02_2_5_Computa_o_em_nuvem_computa_o_de_borda_e_IoT.md`

### Decisões e requisitos internos

- `docs/adr/0002-local-first-edge-ai.md`
- `docs/adr/0005-stt-tts-strategy.md`
- `docs/SRS.md`
- `docs/SDD.md`
- `docs/METRICS.md`
- `docs/MODEL_BENCHMARK.md`
- `docs/contexto-gpt.md`

### Documentação oficial atual

- https://wearables.developer.meta.com/docs/develop/dat/build-integration-android
- https://wearables.developer.meta.com/docs/develop/dat/manage-projects/
- https://wearables.developer.meta.com/docs/develop/dat/set-up-release-channels/
- https://wearables.developer.meta.com/docs/develop/dat/permissions-requests/
