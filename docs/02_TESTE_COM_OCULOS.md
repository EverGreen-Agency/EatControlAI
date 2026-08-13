# Guia de teste com os óculos Ray-Ban Meta

> Para quem tem os óculos em mãos. Não precisa saber programar — só seguir os passos e anotar o que
> aconteceu. **Reservar cerca de 1 hora**, sendo boa parte só de instalação e espera.
>
> Se algum passo travar, anote em que ponto travou e o que apareceu na tela. Um passo que falha é
> informação tão útil quanto um que funciona.

---

## O que você vai fazer, em uma frase

Instalar o nosso aplicativo no celular, autorizar ele a conversar com os óculos, e conferir se os
óculos conseguem tirar uma foto de um rótulo e o aplicativo consegue ler.

---

## 1. O que você precisa ter

| Item | Detalhe |
| :--- | :--- |
| Os óculos | Ray-Ban Meta (Gen 1 ou Gen 2), Ray-Ban Meta Optics, ou Meta Ray-Ban Display |
| Um celular Android | Android 13 ou mais novo |
| App Meta AI | Instalado, atualizado e com os óculos já pareados |
| Conta Meta | A mesma que você usa nos óculos |
| Nosso aplicativo | O arquivo `.apk` que vou te enviar |
| Alguma embalagem | Uma caixa de biscoito, um iogurte, qualquer coisa com lista de ingredientes |

Ter uma boa conexão Wi-Fi ajuda: a primeira sincronização dos óculos pode baixar atualização.

---

## 2. Antes de instalar: preparar os óculos

### 2.1 Atualizar tudo

Abra o app **Meta AI** e confira:

1. Se o app pede atualização, atualize.
2. Toque nos óculos dentro do app e veja se há atualização de **firmware**. Se houver, faça e espere
   terminar — pode levar alguns minutos e os óculos precisam estar com bateria.

**Anote:** a versão do app Meta AI e a versão do firmware dos óculos. Vamos precisar registrar.

### 2.2 Ligar o Modo Desenvolvedor

Isso é o que permite um aplicativo ainda não publicado conversar com os óculos.

No app **Meta AI**, procure por **Configurações** → **Conexões de apps** (*App connections*). Deve
existir uma opção de **Modo desenvolvedor** (*Developer mode*). Ligue.

> Se não encontrar essa opção, pare aqui e me avise. Pode ser que a conta precise estar cadastrada
> como desenvolvedora, e isso resolvo do meu lado.

---

## 3. Instalar o nosso aplicativo

Vou te enviar um arquivo chamado **`app-debug.apk`**.

1. Baixe o arquivo no celular.
2. Toque nele para instalar.
3. O Android vai avisar que é um app de fonte desconhecida e pedir permissão. Aceite — é o nosso
   aplicativo, ainda não publicado na loja.
4. Se o Play Protect reclamar, escolha **instalar mesmo assim**.

O app se chama **Eat Control AI** e tem o ícone padrão do Android por enquanto.

---

## 4. Primeiro teste: o app funciona sozinho?

Antes de envolver os óculos, vamos ver se o aplicativo está de pé.

1. Abra o **Eat Control AI**.
2. Vá na aba **Analisar** (segunda, no rodapé).
3. Em **Fonte de captura**, deixe em **Óculos simulados**.
4. Em **Modo de entrada**, deixe em **Rótulo**.
5. Em **O que os óculos estão vendo**, escolha **Iogurte "zero lactose"**.
6. Toque em **Analisar rótulo**.

**O que deveria acontecer:** o celular fala em voz alta uma frase, e sobe uma folha pela parte de
baixo da tela com o resultado **INCOMPATÍVEL**.

**Anote:**
- [ ] Falou em voz alta? A voz saiu clara?
- [ ] Apareceu INCOMPATÍVEL?
- [ ] Na folha de resultado, quanto deu o `end_to_end_ms`?

> Esse teste não usa os óculos. Ele só confirma que o aplicativo está funcionando antes de a gente
> começar a investigar problemas de hardware.

---

## 5. Segundo teste: a câmera do celular

1. Ainda na aba **Analisar**, toque em **Câmera do celular** na Fonte de captura.
2. O Android vai pedir permissão de câmera. Aceite.
3. Aponte a câmera para a embalagem que você separou, focando na lista de ingredientes.
4. Toque em **Analisar rótulo**.

**Anote:**
- [ ] A imagem ao vivo apareceu na tela?
- [ ] Ele conseguiu ler o texto da embalagem? (a folha de resultado mostra o **texto reconhecido** lá
      embaixo — confira se bate com o que está escrito na caixa)
- [ ] Qual foi a decisão?

Se o texto sair todo errado ou de cabeça para baixo, **tire um print e me mande**. É exatamente o
tipo de problema que precisamos achar agora, e não no dia do evento.

---

## 6. Terceiro teste: os óculos de verdade

Esta é a parte que só você consegue fazer.

1. Coloque os óculos e confirme que estão conectados no app Meta AI.
2. No **Eat Control AI**, aba **Analisar**, toque em **Ray-Ban Meta** na Fonte de captura.
3. Toque em **Parear e autorizar no Meta AI**.
4. O app Meta AI deve abrir pedindo autorização para o Eat Control usar a câmera dos óculos.
   **Aceite.**
5. Você deve voltar sozinho para o Eat Control.
6. Olhe para a embalagem, com ela bem enquadrada à sua frente.
7. Toque em **Analisar rótulo** no celular.

**Anote com cuidado, porque aqui é onde mais coisa pode dar errado:**

- [ ] O app Meta AI abriu pedindo autorização? Que texto exatamente apareceu?
- [ ] Depois de aceitar, você voltou sozinho para o Eat Control, ou teve que voltar na mão?
- [ ] Apareceu alguma mensagem de erro em vermelho na tela? **Copie o texto exato.**
- [ ] Os óculos deram algum sinal — luz acendendo, som, vibração — quando a foto foi tirada?
- [ ] A foto apareceu no aplicativo? Estava reta ou torta? Colorida ou com cor estranha?
- [ ] Ele conseguiu ler o texto?

> **Se der erro aqui, é esperado.** Essa parte nunca foi testada com óculos de verdade — é
> literalmente por isso que estamos pedindo o teste. O erro exato é a informação que precisamos.

---

## 7. Se sobrar tempo: os testes chatos mas importantes

Se o passo 6 funcionou, valem mais alguns:

**Repetição.** Faça 10 análises seguidas pelos óculos. Anote se alguma falhou e em qual tentativa.
Coisas que funcionam uma vez e quebram na quinta são comuns.

**Bateria.** Anote a porcentagem de bateria dos óculos antes, use por 10 minutos fazendo análises, e
anote depois. Quanto caiu?

**Aquecimento.** Os óculos esquentaram de forma perceptível?

**Modo avião.** Coloque o celular em modo avião e repita uma análise de rótulo. **Deveria funcionar
igual** — nossa promessa é que a inteligência roda no aparelho, sem internet. Se falhar, é um achado
importante.

**Ruído.** Na aba Analisar tem um botão **Falar**. Toque, pergunte *"posso comer isso?"* e veja se
ele entende. Se puder, teste também num lugar barulhento.

---

## 8. O que me mandar de volta

Mande do jeito que for mais fácil — mensagem, áudio, foto do caderno:

1. As respostas dos itens marcados acima.
2. **Prints de qualquer erro**, com o texto completo.
3. A versão do app Meta AI, a versão do firmware dos óculos e o modelo do celular.
4. Qualquer coisa que tenha parecido estranha, mesmo que você não saiba explicar.

---

## 9. Se você tiver um computador à mão

Opcional, e ajuda muito. Com o celular ligado no computador por cabo USB e a **depuração USB**
ligada nas opções de desenvolvedor do Android:

```bash
adb logcat -s EC_DAT:I EC_BENCH:I
```

Isso mostra o log detalhado do que o aplicativo está fazendo com os óculos. Deixe rodando durante o
teste do passo 6 e me mande o resultado.

---

## Perguntas que a gente ainda não sabe responder

Se durante o teste você descobrir alguma dessas, avise — são pontos em aberto:

- Dá para disparar a análise **pelos óculos**, sem tocar no celular? Botão na haste, toque, comando
  de voz?
- Os óculos avisam de alguma forma quando um aplicativo de terceiro está usando a câmera?
- Quanto tempo a sessão fica aberta antes de cair sozinha?
