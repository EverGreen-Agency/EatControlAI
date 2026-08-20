# Roteiro do vídeo de demonstração

Versão: 1.0
Data: 18/08/2026
Duração alvo: 2 a 3 minutos
Regra central: demonstração real é o núcleo; walkthrough aparece apenas como contexto ou futuro, e
sempre rotulado.

## 1. Preparação antes de gravar

- [ ] instalar o APK no aparelho que será filmado;
- [ ] configurar um perfil de verdade no onboarding, com nome curto e restrição de leite;
- [ ] definir uma meta de proteína, para que o painel do dia tenha significado;
- [ ] limpar o histórico, para o registro aparecer sendo criado;
- [ ] preparar os itens físicos: um produto com rótulo contraditório, um produto com código de barras,
      um cardápio impresso e um prato montado;
- [ ] silenciar notificações e ativar gravação de tela com áudio interno;
- [ ] testar a rota Bluetooth uma vez antes de gravar;
- [ ] gravar também uma execução na fonte simulada, como plano B.

## 2. Estrutura por tempo

| Tempo | Conteúdo | Tipo | Texto ou ação |
|---|---|---|---|
| 0:00–0:15 | contexto | walkthrough | "Quem faz tratamento com GLP-1 decide o que comer no mercado, no restaurante, sem o profissional ao lado." |
| 0:15–0:30 | problema | narrativa | mostrar embalagem anunciada como zero lactose e o verso declarando leite |
| 0:30–1:05 | rótulo | demonstração real | olhar, perguntar por voz, ouvir `INCOMPATÍVEL` e ver a evidência citada |
| 1:05–1:20 | perfil muda a resposta | demonstração real | desligar a restrição, repetir e mostrar decisão diferente com a mesma imagem |
| 1:20–1:40 | código de barras e tabela | demonstração real | ler o EAN, confirmar porções e ver o total do dia mudar |
| 1:40–2:00 | exceção | demonstração real | rótulo ambíguo gera pergunta; a resposta recalcula a decisão |
| 2:00–2:20 | cardápio e prato | demonstração real | mostrar candidatos, confirmar componentes e dizer em voz alta que macros não são calculados |
| 2:20–2:40 | privacidade e arquitetura | síntese | processamento no aparelho, foto não salva, histórico local, sessão curta dos óculos |
| 2:40–3:00 | próximos passos | walkthrough | validação clínica, composição auditável e métricas em hardware |

## 3. Narração sugerida

**Abertura.** "Eat Control AI responde a uma pergunta alimentar em segundos, por áudio, com
processamento no próprio aparelho."

**Rótulo.** "A pergunta foi feita por voz. O aplicativo capturou um frame, leu o rótulo no aparelho e
comparou com as restrições do meu perfil. A resposta é incompatível, e ele mostra qual frase do rótulo
sustenta isso."

**Perfil.** "Mesma imagem, mesma leitura. Mudou o perfil, então mudou a decisão. A regra é sobre a
pessoa, não sobre o produto."

**Código de barras.** "Aqui a composição vem de base estruturada, que tem precedência sobre texto
solto. Eu confirmo quantas porções comi, e só então o consumo entra no total do dia."

**Exceção.** "Quando a informação não é suficiente, ele pergunta em vez de afirmar. A minha resposta
entra como evidência e a decisão é recalculada pelo mesmo motor."

**Cardápio e prato.** "No cardápio, ele mostra o que está escrito, sem transformar preço em nutriente.
No prato, ele sugere componentes e pede confirmação. Ele não estima gramas por foto, então não afirma
macros do prato. Um número errado com cara de precisão seria pior que dizer que não sabe."

**Privacidade.** "A câmera abre sob demanda e fecha depois da análise. A foto não é salva. O histórico
fica neste aparelho, sem backup em nuvem."

**Fechamento.** "O núcleo funciona hoje. As regras específicas de GLP-1 entram depois da validação
profissional, e as métricas de latência e bateria serão publicadas quando medidas no hardware."

## 4. Frases proibidas

Nunca dizer:

- "é seguro";
- "diagnostica" ou "detecta doença";
- "calcula os macros do prato automaticamente";
- "evita sintomas";
- "substitui o nutricionista";
- "validado clinicamente", enquanto a validação não existir.

## 5. Rótulos obrigatórios na tela

| Cena | Rótulo |
|---|---|
| fonte simulada | "óculos simulados" |
| catálogo de produtos | "catálogo de demonstração" |
| candidato visual | "palpite visual, precisa de confirmação" |
| qualquer número não medido | "meta, ainda não medido" |
| trecho encenado | "ilustração" |

## 6. Plano B

Se o hardware falhar durante a gravação:

1. usar a fonte simulada e dizer isso em voz alta;
2. manter o mesmo roteiro, porque a pipeline é a mesma a jusante da captura;
3. exibir os testes automatizados como evidência de que a lógica é reproduzível;
4. não regravar prometendo hardware que não foi validado.

## 7. Checklist de auditoria antes de publicar

- [ ] toda cena de inferência é real, e o que é simulado está identificado;
- [ ] nenhum número aparece como resultado sem medição;
- [ ] o app não é chamado de clínico, validado ou seguro;
- [ ] o limite do prato é dito em voz alta;
- [ ] nenhuma credencial, token, e-mail ou dado pessoal aparece na tela;
- [ ] o vídeo, o documento A1–A7 e o app contam a mesma história.
