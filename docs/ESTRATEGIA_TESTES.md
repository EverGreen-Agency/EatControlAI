# Estratégia de testes e cobertura

Status: `IMPLEMENTADO` (infraestrutura), `EM PROGRESSO` (cobertura de ramos)
Última medição: 18/08/2026

## 1. As duas pastas de teste

O projeto não tem back-end. Tem um app Android. Então "front e back" aqui significa camadas, e o Android separa testes em dois lugares:

| Pasta | Onde roda | Custo | O que vai aqui |
|---|---|---|---|
| `app/src/test` | JVM da máquina, sem emulador | segundos | domínio, dados, orquestração, ViewModel |
| `app/src/androidTest` | aparelho ou emulador | minutos, precisa de hardware | Compose de verdade, DAT com Mock Device Kit, ML Kit real |

`app/src/test/java/com/eatcontrolai/domain/nutrition/` é apenas uma pasta nova **dentro** da suíte JVM que já existia. Não é um mecanismo novo: são os testes do parser e da calculadora nutricional, ao lado dos que já existiam para rótulo, decisão, roteamento, voz, barcode e serialização.

## 2. O que 100% de cobertura significa aqui

Cobertura total de 100% no app inteiro seria um número comprado, não uma garantia. Três camadas não podem ser cobertas por teste JVM sem fingir:

- `glasses/` — DAT, CameraX. Só executa com óculos e celular reais.
- `inference/` — ML Kit, TTS, STT. Dependem de modelos e engines do aparelho.
- `ui/` — Compose. Verificado por teste de interface, não por linha coberta.

Por isso a régua de 100% é aplicada **onde o erro machuca**: as camadas determinísticas que decidem se a pessoa pode comer algo. Essas são puro Kotlin, sem Android, e portanto 100% é alcançável e significativo.

## 3. Medição atual

A execução consolidada de 18/08/2026 concluiu `156` testes em `20` suítes, sem falhas, erros ou skips. O rule pack GLP-1 entrou com `43` desses testes.

| Pacote/recorte | Linhas | Ramos |
|---|---|---|
| `domain/voice` | 100% (12/12) | 100% (6/6) |
| `domain/barcode` | 100% (37/37) | sem ramos |
| `domain/routing` | 100% (26/26) | 92,9% (13/14) |
| `domain/label` | 100% (103/103) | 78,3% (36/46) |
| `domain/nutrition` | 100% (137/137) | 79,2% (99/125) |
| `domain/evidence` | 100% (16/16) | 75% (3/4) |
| `domain/plate` | 100% (77/77) | 100% (12/12) |
| `domain/menu` | 99,2% (125/126) | 77,8% (42/54) |
| `domain/glp1` | 98,8% (337/341) | 85,6% (131/153) |
| `domain/decision` | 98,4% (126/128) | 83% (83/100) |
| **Domínio monitorado pelo gate** | **99,3% (996/1003)** | **82,7% (425/514)** |
| `core/model` | 97,8% (178/182) | 79,3% (23/29) |
| `metrics` | 93,5% (29/31) | 50% (2/4) |
| `data` | 56,1% (124/221) | 40% (36/90) |
| `orchestration` | 53,4% (140/262) | 37,9% (36/95) |
| **Total do módulo medido** | **83,9% (1467/1748)** | **70% (522/746)** |

Sete dos dez pacotes determinísticos estão em 100% de linhas. O rule pack `domain/glp1` entrou com 98,8% de linhas e 85,6% de ramos, acima da média do domínio. As lacunas restantes concentram-se em ramos de MENU, nutrição, rótulo e nos caminhos residuais de decisão. O total do módulo inclui costura Android e adaptadores que exigem validação própria; por isso ele não é usado como gate clínico.

## 4. Régua no build

`app/build.gradle.kts` define duas tarefas:

- `:app:coverageReport` — relatório HTML e XML de cobertura dos testes JVM.
- `:app:coverageVerify` — reprova o build se a cobertura das camadas de domínio cair.

`check` depende de `coverageVerify`, então queda de cobertura quebra o build, não só teste vermelho.

O piso atual é **linha 0,99 e ramo 0,82**, sempre abaixo do valor medido, não um número escolhido por otimismo. O alvo declarado é 100/100. A regra de convivência é simples: **quem sobe cobertura sobe o piso no mesmo commit.** Foi o que aconteceu ao entrar o rule pack: ramo medido subiu de 81,3% para 82,7% e o piso acompanhou de 0,80 para 0,82.

O gate foi verificado propositalmente: com o piso em 1,00 o build falha com `lines covered ratio is 0.99`. Ou seja, a régua mede de verdade — não é uma tarefa que passa vazia.

## 5. Ferramentas disponíveis

Já configuradas em `app/src/test`:

- JUnit 4 — suíte existente.
- `kotlinx-coroutines-test` — controle de tempo virtual em `suspend`.
- Turbine — asserção sobre `StateFlow`/`Flow` sem `sleep` nem corrida.
- Robolectric — permite testar ViewModel, DataStore e Compose no JVM, sem emulador.
- `compose-ui-test-junit4` — árvore semântica do Compose.
- `org.json` real — o `android.jar` traz apenas um stub que devolve nulo.

Aviso prático: a primeira execução com Robolectric baixa os artefatos `android-all` e leva vários minutos. As execuções seguintes usam cache.

## 6. Como fazer TDD daqui para frente

1. Escreva o teste que descreve o comportamento desejado e veja falhar.
2. Implemente o mínimo para passar.
3. Rode `:app:coverageReport` e olhe os ramos descobertos.
4. Feche os ramos que representam decisão real do produto.
5. Suba o piso em `coverageVerify` no mesmo commit.

Regra de escopo: comportamento de negócio entra em `src/test`. Integração com hardware entra em `src/androidTest` e roda quando houver aparelho.

## 7. Próximos alvos de cobertura, em ordem

1. `orchestration` — está em 53,4% de linhas e 37,9% de ramos; ampliar testes das combinações de provider, fallback e erro.
2. `data` — está em 56,1% de linhas e 40% de ramos; cobrir hidratação/escrita assíncrona e JSON corrompido.
3. Ramos de `domain/menu`, `domain/nutrition`, `domain/label` e `domain/evidence` — elevar o piso acima dos 80% atuais.
4. `core/model` — fechar os ramos restantes de `MacroGoals` e `NutritionFacts`.
5. ViewModel com Robolectric e Turbine.
6. Smoke de UI em Compose para onboarding, confirmação e registro.

## 8. Utilitário

`scripts/coverage-summary.ps1` imprime a tabela por pacote a partir do XML do JaCoCo:

```powershell
.\gradlew.bat :app:coverageReport
powershell -File scripts\coverage-summary.ps1
```
