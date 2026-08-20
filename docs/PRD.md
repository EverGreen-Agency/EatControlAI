# PRD — Eat Control AI

Versão: 1.1
Atualizado em: 18/08/2026
Estado: núcleo local-first em implementação; regras clínicas executáveis dependem de validação profissional.

## 1. Objetivo do produto

Ajudar uma pessoa em tratamento com GLP-1 a responder, no instante da escolha alimentar:

> **“Diante do que estou vendo agora, qual é a melhor escolha para mim e por quê?”**

O Eat Control usa o smartphone e, quando disponível, Ray-Ban Meta como interface hands-free. A resposta deve ser curta, rastreável e proporcional à evidência disponível. O produto apoia autorregistro e decisão; não diagnostica, não prescreve, não ajusta medicamento e não substitui acompanhamento profissional.

## 2. Princípios obrigatórios

1. **Perfil real antes de personalização.** Builds de produção não iniciam com pessoa, consumo ou metas fictícias.
2. **Evidência antes de conclusão.** Dado declarado em rótulo/catálogo, texto observado, inferência visual e confirmação do usuário permanecem distinguíveis.
3. **Incerteza visível.** Ausência de informação nunca é convertida em ausência de risco.
4. **Local-first.** Capturas são processadas no telefone, mantidas somente em memória durante a interação e não enviadas a servidor no caminho crítico.
5. **Confirmação antes de registro.** Quantidade, porção e itens inferidos precisam ser revisáveis.
6. **Metas não são recomendações do app.** Só entram por configuração do usuário/profissional e sua origem deve ser comunicada.
7. **Sem regra clínica implícita.** Literatura fundamenta requisitos e hipóteses, não thresholds automáticos.

## 3. Personas

### P1 — Pessoa em tratamento com GLP-1
Quer reduzir a fricção para avaliar e registrar alimentos, acompanhar consumo confirmado e entender por que faltam informações.

### P2 — Profissional de saúde
Define ou revisa metas e regras fora do app e precisa distinguir dado observado, estimado e confirmado. Compartilhamento remoto não faz parte do MVP.

### P3 — Equipe de produto/validação
Versiona fontes, regras, modelos e critérios de aceite sem apresentar hipótese como resultado validado.

## 4. Jobs-to-be-done

- “Quando estou diante de um produto, cardápio ou prato, quero registrar o que consigo confirmar sem preencher um formulário longo.”
- “Quando comparo opções, quero fatos observáveis e o motivo da comparação.”
- “Quando a imagem não basta, quero que o app pergunte ou diga que não sabe.”
- “Quero acompanhar consumo frente às metas que configurei, sem números demonstrativos.”
- “Quero usar os óculos sob demanda, sem câmera ou conexão permanentes.”

## 5. Base clínica e limites de tradução

O registro canônico é [`DATA_SOURCES.md`](DATA_SOURCES.md). Este PRD referencia claims; não os replica.

- `AJCN-GLP1-01`: perfil e acompanhamento individualizáveis.
- `AJCN-GLP1-03` e `AJCN-GLP1-05`: perguntas sobre porção/preparo e comparação de opções são hipóteses, não regras prontas.
- `AJCN-GLP1-04` e `AJCN-GLP1-06`: consumo confirmado pode ser somado, mas metas e limites não são inferidos pelo app.
- `AJCN-GLP1-08`: histórico corrigível pode apoiar autorregistro.
- `AJCN-GLP1-09` e `AJCN-GLP1-10`: suporte digital é assistivo e deve encaminhar o que exige cuidado profissional.

Qualquer rule pack GLP-1, threshold nutricional, peso de comparação ou mensagem sobre sintomas permanece bloqueado até aprovação no formato de [`PEDIDO_VALIDACAO_CLINICA.md`](PEDIDO_VALIDACAO_CLINICA.md).

## 6. Escopo funcional da versão

### FR-01 — Perfil local configurável

- Estados explícitos: `Loading`, `NeedsOnboarding` e `Ready` (ou equivalentes).
- Primeiro uso solicita nome de exibição, indicação de uso de GLP-1 e metas nutricionais opcionais.
- Nenhuma meta numérica é criada silenciosamente.
- O usuário pode editar e persistir o perfil localmente.
- Enquanto o armazenamento hidrata, a UI não exibe fallback fictício.

### FR-02 — Dashboard factual

- Consumo diário deriva somente de registros confirmados no histórico.
- Cada meta configurada aparece mesmo quando o consumo é zero.
- A UI distingue “consumido” de “meta configurada”.
- Hidratação, percentuais ou tendências só aparecem quando houver modelo e dados reais correspondentes.
- Editar confirmação/porção preserva os demais campos do registro e recalcula os totais sem duplicação.

### FR-03 — Fontes e sessão DAT

- Fontes: câmera do telefone, mock diagnóstico e DAT/Ray-Ban quando autorizado.
- Registro, autorização e sessão temporária são estados diferentes na UI.
- Falha ao abrir Meta AI, registrar, obter permissão ou conectar produz mensagem acionável.
- Selecionar DAT não mantém câmera/conexão aberta; a sessão começa ao analisar/testar e fecha em `finally`.
- O stream técnico temporário existe apenas para obter a foto/frame solicitada.

### FR-04 — Automático, rótulo e código de barras

- Automático tenta evidência estruturada antes de OCR livre.
- Rótulo preserva texto observado e proveniência.
- Código desconhecido não recebe composição inventada.
- Resultado usa estados equivalentes a compatível, incompatível, precisa confirmar e informação insuficiente, conforme a força da evidência.

### FR-05 — Cardápio (`MENU`)

- Pipeline próprio: OCR → normalização de linhas → parser de seções/opções → revisão do usuário.
- Preserva nome, descrição e preço textual quando observados; não trata preço como nutriente.
- Não reutiliza o parser de tabela nutricional como atalho.
- Sem composição declarada, o app não inventa calorias ou macros.
- Comparação clínica entre opções só será habilitada com critérios versionados e aprovados; antes disso, a saída é factual.

### FR-06 — Prato (`PLATE`) v1 assistido

- Captura sob demanda e provider visual local retornam candidatos e confiança, nunca certeza.
- O usuário confirma/corrige os componentes antes do registro.
- Porção vem de seleção/entrada do usuário, não de volume inferido por uma única foto.
- Macro só é calculado quando item, quantidade e fonte de composição estiverem presentes; caso contrário, permanece desconhecido.
- Inferência visual isolada nunca afirma ausência de alérgeno nem produz conclusão positiva para restrição crítica.
- Histórico e UI identificam claramente conteúdo estimado versus declarado/confirmado.

A especificação detalhada está em [`ESTRATEGIA_PRATO_VISAO.md`](ESTRATEGIA_PRATO_VISAO.md).

### FR-07 — Resultado e explicação

Toda análise deve apresentar, conforme aplicável:

- conclusão limitada;
- motivo em linguagem curta;
- origem da evidência;
- confiança/limitação;
- pergunta ou ação seguinte;
- possibilidade de corrigir antes de registrar.

A frase positiva padrão é: **“Não encontrei conflito nas evidências disponíveis.”** Nunca usar “seguro”.

### FR-08 — Privacidade e correção

- Fotos não são persistidas.
- Histórico e perfil ficam no aparelho e são excluídos de backup/transferência.
- O usuário pode apagar o histórico.
- Não há upload no caminho crítico.
- Dados estimados ou confirmados podem ser corrigidos/excluídos.

## 7. Benchmark de experiência, não de precisão

Produtos de nutrição por foto estabeleceram a expectativa de captura rápida e diário automático: [MyFitnessPal Meal Scan](https://blog.myfitnesspal.com/watch/log-meals-like-magic-with-meal-scan/) reduz busca manual; [Foodvisor](https://play.google.com/store/apps/details?id=io.foodvisor.foodvisor) anuncia reconhecimento por foto e barcode; [Cal AI](https://www.calai.app/) anuncia cálculo de calorias e macros por foto; [BiteSnap](http://www.getbitesnap.com/) apresenta reconhecimento de alimentos para registro.

Essas descrições comerciais orientam somente a UX competitiva. Elas não validam precisão para comida brasileira, porção, pratos mistos ou uso clínico. O diferencial exigido do Eat Control é: captura hands-free, processamento local, proveniência, confirmação e recusa explícita de precisão não demonstrada. Estudos comparativos de plataformas de reconhecimento também reforçam que reconhecimento e avaliação dietética devem ser medidos separadamente ([comparação de plataformas](https://pmc.ncbi.nlm.nih.gov/articles/PMC7752530/)).

Conteúdo externo foi parafraseado para conformidade com restrições de licenciamento.

## 8. Fora do escopo desta versão

- Prescrição de dieta, diagnóstico, ajuste de dose ou recomendação de medicamento.
- Meta clínica calculada automaticamente por peso, IMC ou imagem.
- Macro exato ou ausência de ingrediente/alérgeno derivados de uma foto.
- Monitoramento contínuo por câmera/microfone.
- Backend, sincronização, dashboard médico ou multi-tenant B2B.
- Treino de um modelo visual próprio sem dataset, protocolo e benchmark definidos.

## 9. Critérios de aceite

- [ ] Instalação limpa abre onboarding, nunca “João” ou números sintéticos.
- [ ] Perfil e metas persistem e podem ser editados localmente.
- [ ] Dashboard mostra valores derivados do histórico, incluindo zero para meta configurada sem consumo.
- [ ] Atualizações de um registro não apagam confirmação, nutrientes ou porção já existentes.
- [ ] DAT diferencia autorização, conexão temporária e erro visível.
- [ ] `MENU` produz opções revisáveis a partir de OCR real.
- [ ] `PLATE` exige confirmação e não promete macro automático sem dados suficientes.
- [ ] Nenhum modo afirma segurança a partir de inferência visual.
- [ ] Suíte unitária, gate de cobertura do domínio e build debug passam.
- [ ] Limitações dependentes de Ray-Ban físico são declaradas no checklist de distribuição.

## 10. Métricas de engenharia

Até medição, todos os números são metas, não resultados:

- latência ponta a ponta por modo e provider (p50/p90/p95);
- taxa de parse revisável de cardápios;
- top-1/top-k e cobertura de classes do provider de prato;
- taxa de correção do usuário e registros recusados por insuficiência;
- retenção de imagem em disco: zero;
- false-safe em cenários críticos: zero;
- bateria/temperatura e comportamento offline no aparelho-alvo.
