---
corpus: "CEIA e Meta - AI Glasses Brasil"
unit: 2
title: "Principais abordagens e aplicações"
source_file: "Un2_curso_meta.pdf"
source_pages: 136
source_sha256: "7a9dc7f471ed719d892e1ab321cb3bd327a74bb22294411f4480490f167ae915"
language: "pt-BR"
normalization: "text layer normalized; page provenance preserved; visual-only supplements transcribed from source pages when identified"
---

# Unidade 2: Principais abordagens e aplicações

> Fonte única: `Un2_curso_meta.pdf`. O conteúdo abaixo foi normalizado para busca/RAG, sem complementação por fontes externas.

<!-- source_page: 2 -->

# Unidade II - Principais abordagens e aplicações

## 2.1 Ciência de Dados e Mineração de Dados

A Ciência e a Mineração de Dados são áreas cruciais no mundo da IA, permitindo que transformemos grandes volumes de informações brutas em conhecimento útil e estratégico. Imagine que você tem um vasto oceano de informações – cada transação, cada clique, cada interação do cliente é como uma gota nesse oceano. Por meio da Ciência de Dados e da Mineração de Dados, podemos identificar padrões e extrair conhecimento que, à primeira vista, parecem ser dados aleatórios. Na realidade, os dados coletados e armazenados corretamente podem refletir comportamentos ou tendências humanas que, com certeza, serão úteis para alguém. Um exemplo palpável disso foi relatado no livro O Poder do Hábito: uma rede de supermercados norte-americana analisou os hábitos de compra de seus clientes e percebeu que determinadas combinações de produtos (como itens de cuidado pessoal, vitaminas e outros produtos relacionados) indicavam, com alta probabilidade, que uma cliente estava grávida. Com essa informação, a rede de supermercados podia direcionar conteúdos específicos para esse público.

<!-- source_page: 3 -->

Notebook Colab

a) Objetivos de Aprendizagem

- Neste notebook, você vai colocar a mão na massa e explorar na prática tudo o que vimos no Tópico 4.1 – Ciência de Dados e Mineração de Dados. 🚀

Nosso objetivo é te ajudar a transformar a teoria em ação, aplicando os conceitos em problemas reais. Assim, você não só entende o conteúdo, mas também descobre como gerar valor em desafios do mundo profissional. 💡

b) Contexto 💥💥💥

Você foi contratado como cientista de dados do Estado de Goiás e, no seu primeiro dia, recebeu um conjunto de dados sobre os hábitos alimentares dos cidadãos. Sua missão é levantar insights valiosos para uma reunião que acontecerá em breve, onde uma equipe especializada usará essas informações para desenvolver estratégias que promovam uma alimentação mais saudável para todos os goianos. Vamos começar? Primeiramente, lembre-se do que discutimos nas aulas e no ebook: a metodologia AGEMC, que é fundamental para orientar cada etapa da Ciência de Dados:

- Ask (Perguntar): Defina as questões e objetivos que você deseja responder com os dados. 🤔

- Get (Obter): Reúna e organize os dados necessários para a análise. 📥

- Explore (Explorar): Analise os dados para identificar padrões e insights. 🔍

- Model (Modelar): Aplique modelos e técnicas para interpretar os dados de forma robusta. 📈

- Communicate (Comunicar): Apresente os resultados de forma clara e estratégica, transformando dados em decisões informadas. 💬

Cada uma dessas etapas é essencial para transformar os dados em conhecimento que pode impactar positivamente a saúde dos goianos. Vamos colocar a mão na massa e começar essa jornada!

<!-- source_page: 4 -->

c) Como funciona o colab?

```text
### EXECUTE ESSA CÉLULA ###
# O Colab é uma IDE super fácil de usar:
# 🏽 basta clicar no ícone de "play" em cada célula!
```

```text
# Essa célula está instalando bibliotecas
!pip install pyfiglet cowsay termcolor
```

Requirement already satisfied: pyfiglet in /usr/local/lib/python3.11/distpackages (1.0.2) Requirement already satisfied: cowsay in /usr/local/lib/python3.11/dist-packages (6.1) Requirement already satisfied: termcolor in /usr/local/lib/python3.11/distpackages (3.0.1)

```text
# Execute a célula e preencha com seu nome
name = input('Qual seu nome? ')
```

Qual seu nome? Lisandra

Clique duas vezes (ou pressione "Enter") para editar

```text
import time
from pyfiglet import figlet_format
import cowsay
from termcolor import colored
```

```text
# Cria um título com arte ASCII usando pyfiglet
titulo = figlet_format("Hello, world", font="slant")
print(colored(titulo, "cyan"))
```

# Pequena pausa para o efeito dramático time.sleep(1)

# Uma mensagem divertida usando o cowsay cowsay.cow(f"{name}, prepare-se para uma jornada épica em Ciência de Dados!")

# Outra pausa para dar tempo de apreciar a mensagem time.sleep(1)

```text
# Mensagem final de boas-vindas
print(colored("\n🚀 Lembre-se de seguir a ordem de execução para garantir que
tudo funcione corretamente. 😉", "magenta"))
```

<!-- source_page: 5 -->

__ __ ____ __ __ / / / /__ / / /___ _ ______ _____/ /___/ / / /_/ / _ \/ / / __ \ | | /| / / __ \/ ___/ / __ / / __ / __/ / / /_/ / | |/ |/ / /_/ / / / / /_/ / /_/ /_/\___/_/_/\____( ) |__/|__/\____/_/ /_/\__,_/ |/

_________________________________________________ / \ | Lisandra, prepare-se para uma jornada épica em Ci | | ência de Dados! | \ / ================================================= \ \ ^__^ (oo)\_______ (__)\ )\/\ ||----w | || ||

🚀 Lembre-se de seguir a ordem de execução para garantir que tudo funcione corretamente. 😉

d) ASK (Perguntar): Definindo Nossas Questões 🧐🧐🧐

O primeiro passo é definir as perguntas que queremos responder com os dados. No nosso caso, algumas questões importantes são:

- Quais são os principais fatores de estilo de vida que contribuem para a obesidade em Goiás? 🤔

- Existe alguma relação entre obesidade e histórico familiar, consumo de alimentos calóricos, frequência de atividade física, etc.? 📊

- Podemos identificar grupos de pessoas com maior risco de obesidade? 🔍

- Como podemos usar essas informações para criar políticas públicas mais eficazes? 💡

Essas perguntas vão orientar nossa análise e ajudar a transformar dados em insights úteis para a saúde pública!

<!-- source_page: 6 -->

e) GET (Obter): Carregando e Preparando os Dados 🏾🏾🏾

Agora, vamos carregar os dados do dataset de obesidade e prepará-los para a análise. Vale lembrar que os dados usados aqui vêm da plataforma Kaggle e são apenas ilustrativos – ou seja, não correspondem aos dados reais do Estado de Goiás! 🚀📊

f) Importação das bibliotecas Python

```text
# 1° execução
# Sempre execute a célula de bibliotecas antes de qualquer outra célula
# Imagine que você precisa pegar o livro na biblioteca, antes de começar a ler
```

```text
# Bibliotecas mais usadas
import numpy as np #numpy: Para realizar cálculos matemáticos.
import pandas as pd #pandas: Para manipular e analisar os dados em formato
de tabelas.
import matplotlib.pyplot as plt #matplotlib e seaborn: Para criar gráficos e
visualizações.
import seaborn as sns
```

```text
# A variavel df agora recebeu a importação da nossa planilha
df = pd.read_csv("https://drive.google.com/uc?id=1OrkM5_hdIOX3itEr0Tcoc7IH0n-
80z0Vk&export=download")
```

g) 👀 Visualizando os dados

df.head()

Agora sabemos um pouco mais sobre as colunas desse dataset. Note que as informações estão em inglês, isso significa que para selecionar uma coluna ou linha, precisamos passar os valores em inglês.

<!-- source_page: 7 -->

- Gender – Gênero: Possui as variáveis 'Male' (masculino) ou 'Female' (feminino).

- Age: A idade da pessoa em anos.

- Height: Altura em metros.

- Weight: Peso em quilogramas.

- family_history_with_overweight: Indica se a pessoa possui histórico familiar de sobrepeso (yes/no).

- FAVC: Se a pessoa consome alimentos com alto teor calórico com frequência (yes/no).

- FCVC: Frequência de consumo de vegetais (escala de 1 a 3).

- NCP: Número de refeições principais por dia.

- CAEC: Frequência de consumo de alimentos entre as refeições, ou seja, famoso beliscar (Never (Nunca), Sometimes (Ás vezes), Frequently (Com frequência), Always (Sempre)).

- SMOKE: Indica se a pessoa fuma (yes/no).

- CH2O: Ingestão diária de água (escala de 1 a 3).

- SCC: Se a pessoa monitora sua ingestão calórica (yes/no).

- FAF: Frequência de atividade física (escala de 0 a 3).

- TUE: Tempo gasto utilizando tecnologia (escala de 0 a 3).

- CALC: Frequência de consumo de álcool (Never, Sometimes, Frequently, Always).

- MTRANS: Principal meio de transporte (Automobile (Automóvel), Bike, Motorbike (Motocicleta), Public Transportation (Transporte público), Walking (Ambulante)).

- NObeyesdad: Nível de obesidade (Insufficient Weight (Peso insuficiente), Normal Weight (Normal), Overweight Level I (Sobrepeso nível I), Overweight Level II (Sobrepeso nível II), Obesity Type I (Obesidate tipo 1), Obesity Type II (Obesidate tipo 2), Obesity Type III ((Obesidate tipo 3))).

```text
# Podemos descobrir também quais são os tipos de cada coluna
# Ou seja, quais tipos de valores que elas carregam, se são palavras ou números
df.info()
```

<!-- source_page: 8 -->

<class 'pandas.core.frame.DataFrame'> RangeIndex: 2111 entries, 0 to 2110 Data columns (total 17 columns): # Column Non-Null Count Dtype --- ------ -------------- ----- 0 Gender 2111 non-null object 1 Age 2111 non-null int64 2 Height 2111 non-null float64 3 Weight 2111 non-null float64 4 family_history_with_overweight 2111 non-null object 5 FAVC 2111 non-null object 6 FCVC 2111 non-null float64 7 NCP 2111 non-null float64 8 CAEC 2111 non-null object 9 SMOKE 2111 non-null object 10 CH2O 2111 non-null float64 11 SCC 2111 non-null object 12 FAF 2111 non-null float64 13 TUE 2111 non-null float64 14 CALC 2111 non-null object 15 MTRANS 2111 non-null object 16 NObeyesdad 2111 non-null object dtypes: float64(7), int64(1), object(9) memory usage: 280.5+ KB

f) 💦 Limpeza e tratamento dos dados

df.isnull().sum()

<!-- source_page: 9 -->

g) 😌 Padronização dos dados

A padronização é fundamental. Nesta etapa, transformamos dados categóricos (por exemplo, palavras como "feminino" e "masculino") em variáveis numéricas. Isso ocorre porque, na maioria dos modelos de machine learning, trabalhamos exclusivamente com números para gerar funções que se ajustem aos dados que possuímos.

# Primeiro vamos tratar os dados categoricos ordinais # Estamos transformando o conteudo dessas colunas em "inteiros" (igual aos conjuntos dos números naturais) df['FCVC'] = df['FCVC'].astype(int) df['NCP'] = df['NCP'].astype(int) df['CH2O'] = df['CH2O'].astype(int) df['FAF'] = df['FAF'].astype(int) df['TUE'] = df['TUE'].astype(int)

# Agora vamos tranformar as colunas categoricas em numéricas # Aqui vamos atribuir valores númericos a palavras df['Gender'] = df['Gender'].replace({'Male': 0, 'Female': 1}) # Masculno agora é 0 e Feminino 1 df['family_history_with_overweight'] = df['family_history_with_overweight']. replace({'no': 0,

'yes': 1}) # Histórico de obsidade: 0 para não e 1 para sim df['FAVC'] = df['FAVC'].replace({'no': 0, 'yes': 1}) # Se consome alimento com alto teor de caloriadas: 0 para não e 1 para sim df['SMOKE'] = df['SMOKE'].replace({'no': 0, 'yes': 1}) # Se fuma: 0 para não e 1 para sim df['SCC'] = df['SCC'].replace({'no': 0, 'yes': 1}) # Se monitora as calórias ingeridas: 0 para não e 1 para sim df['CAEC'] = df['CAEC'].replace({'no': 0, 'Sometimes': 1, 'Frequently': 2, 'Always': 3}) # Se belisca: 0 para não e 1 para sim df['CALC'] = df['CALC'].replace({'no': 0, 'Sometimes': 1, 'Frequently': 2, 'Always': 3}) # Se consome alcool: 0 para não e 1 para sim

```text
# Por fim, vamos aplicar o one hot encode para a coluna MTRANS
# O one hot encode é uma abordagem um pouco complexa, então, fica aqui uma
referência para você ler
# https://medium.com/@ealexbarros/como-realizar-o-processo-de-enconding-de-
vari%C3%A1veis-categ%C3%B3ricas-de280c7aa08f
dummies = pd.get_dummies(df['MTRANS'], prefix='MTRANS')
df = pd.concat([df, dummies], axis=1)
df.drop('MTRANS', axis=1, inplace=True)
```

```text
# Vamos visualizar o resultado
df.head()
```

<!-- source_page: 10 -->

<ipython-input-47-40a60a9aa827>:11: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['Gender'] = df['Gender'].replace({'Male': 0, 'Female': 1}) # Masculno agora é 0 e Feminino 1 <ipython-input-47-40a60a9aa827>:12: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['family_history_with_overweight'] = df['family_history_with_overweight']. replace({'no': 0, <ipython-input-47-40a60a9aa827>:15: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['FAVC'] = df['FAVC'].replace({'no': 0, 'yes': 1}) # Se consome alimento com alto teor de caloriadas: 0 para não e 1 para sim <ipython-input-47-40a60a9aa827>:16: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['SMOKE'] = df['SMOKE'].replace({'no': 0, 'yes': 1}) # Se fuma: 0 para não e 1 para sim <ipython-input-47-40a60a9aa827>:17: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['SCC'] = df['SCC'].replace({'no': 0, 'yes': 1}) # Se monitora as calórias ingeridas: 0 para não e 1 para sim <ipython-input-47-40a60a9aa827>:18: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['CAEC'] = df['CAEC'].replace({'no': 0, 'Sometimes': 1, 'Frequently': 2, 'Always': 3}) # Se belisca: 0 para não e 1 para sim <ipython-input-47-40a60a9aa827>:19: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['CALC'] = df['CALC'].replace({'no': 0, 'Sometimes': 1, 'Frequently': 2, 'Always': 3}) # Se consome alcool: 0 para não e 1 para sim

<!-- source_page: 11 -->

h) Explore (Explorar): Analisando os Dados e Buscando Padrões

Agora que os dados estão limpos e preparados, podemos começar a explorá-los para buscar padrões e tendências.

```text
# Tamanho do dataset (linhas, colunas)
df.shape
```

(2111, 21)

```text
# Vamos começar calculando algumas estatísticas descritivas,
# como a média, o desvio padrão, o mínimo e o máximo de cada coluna:
df.describe()
```

1. Gender (Gênero):

- Com 2111 registros, a média de 0.494 indica uma distribuição relativamente equilibrada entre as categorias (0 para "Male" e 1 para "Female").

2. Age (Idade):

- A média de 24.32 anos e a mediana de 23 anos mostram que a maioria dos indivíduos é jovem.

<!-- source_page: 12 -->

3. Height (Altura):

- A altura média de 1.70 metros, com um desvio padrão de 0.093, aponta para uma distribuição consistente, com valores variando de 1.45 a 1.98 metros.

4. Weight (Peso):

- Com uma média de 86.59 kg e mediana de 83 kg, nota-se que a distribuição pode estar assimétrica, com alguns indivíduos pesando significativamente mais (valor máximo de 173 kg).

5. family_history_with_overweight (Histórico Familiar com Sobrepeso):

- A média de 0.8176, junto com os quartis iguais a 1, mostra que aproximadamente 82% dos indivíduos possuem histórico familiar de sobrepeso.

- Isso pode ser um indicador importante para riscos genéticos ou hábitos alimentares herdados.

6. FAVC (Consumo Frequente de Alimentos com Alto Teor Calórico):

- Com média de 0.8839 e quartis fixos em 1, quase 88% dos indivíduos consomem alimentos calóricos com frequência, sugerindo uma alta exposição a alimentos potencialmente prejudiciais à saúde.

7. FCVC (Frequência de Consumo de Vegetais):

- A média de 2.217 e a mediana de 2 indicam que a maioria consome vegetais em uma frequência intermediária (escala de 1 a 3), com um leve viés para valores mais altos (25º percentil em 2 e 75º em 3).

8. NCP (Número de Refeições Principais):

- A média de 2.53 e a mediana de 3 sugerem que a maioria dos indivíduos faz três refeições principais por dia, embora haja variações (mínimo de 1 e máximo de 4).

9. CAEC (Frequência de Consumo de Alimentos Entre as Refeições):

- Com média de 1.14 e mediana de 1, a maioria consome lanches ou alimentos entre as refeições com pouca frequência.

- O valor máximo de 3 mostra que alguns indivíduos têm hábitos de consumo mais frequentes fora das refeições principais.

10. SMOKE (Fumo):

- A média de 0.0208 indica que apenas cerca de 2% dos registros são de fumantes, o que é confirmado pelos quartis (todos 0) e o valor máximo (1).

<!-- source_page: 13 -->

11. CH2O (Ingestão Diária de Água):

- A média de 1.717 e a mediana de 2 sugerem uma ingestão moderada de água, na escala de 1 a 3, com a maioria dos indivíduos na faixa intermediária.

12. SCC (Monitoramento da Ingestão Calórica):

- A média de 0.0455, com a mediana e os quartis em 0, indica que apenas cerca de 4.5% dos indivíduos monitoram sua ingestão calórica, o que evidencia uma baixa adesão a esse hábito.

13. FAF (Frequência de Atividade Física):

- Com média de 0.743 e mediana de 1, observa-se que a maioria dos indivíduos pratica atividade física com pouca frequência, embora haja casos de pessoas que se exercitam mais (valor máximo de 3).

14. TUE (Tempo Gasto Usando Tecnologia):

- A média de 0.381 e a mediana de 0 indicam que a maioria dos indivíduos passa pouco tempo usando tecnologia (na escala de 0 a 3), embora alguns cheguem até 2.

15. CALC (Frequência de Consumo de Álcool):

- Com média de 0.731 e mediana de 1, a maioria dos indivíduos consome álcool ocasionalmente, com variação até o valor máximo de 3.

Resumo Geral: A amostra é composta principalmente por jovens adultos com uma distribuição de gênero quase equilibrada. Destaca-se que a maioria apresenta histórico familiar de sobrepeso e um consumo frequente de alimentos com alto teor calórico. Em contrapartida, os hábitos saudáveis, como o monitoramento da ingestão calórica e a prática regular de atividade física, são baixos. Essas informações são essenciais para identificar áreas prioritárias em campanhas de saúde e intervenções nutricionais, principalmente para orientar políticas públicas voltadas à prevenção da obesidade.

<!-- source_page: 14 -->

i) Agora vamos análisar alguns gráficos

# Histograma da idade:

```text
plt.hist(df['Age'], bins=20)
plt.xlabel('Idade')
plt.ylabel('Frequência')
plt.title('Distribuição da Idade')
plt.show()
```

**Figura 4 - Distribuição da Idade**

Fonte: autoria própria

# Gráfico de barras da frequência de consumo de vegetais:

```text
sns.countplot(x='FCVC', data=df)
plt.xlabel('Frequência de Consumo de Vegetais')
plt.ylabel('Contagem')
plt.title('Frequência de Consumo de Vegetais')
plt.show()
```

<!-- source_page: 15 -->

**Figura 5 - Frequência de Consumo de Vegetais**

Fonte: autoria própria

```text
# Agora vamos transformar a coluna NObeyesdad que classifica os tipos de
obsidade
# Nessa variavel podemos perceber que o nível tem uma ordem de progressão
# Portato, agora usaremos o OrdinalEncoder
```

from sklearn.preprocessing import OrdinalEncoder

```text
# Definindo a ordem das categorias de obesidade (do menor para o maior risco)
categories = [["Insufficient_Weight", "Normal_Weight", "Overweight_Level_I",
```

"Overweight_Level_II", "Obesity_Type_I", "Obesity_Type_II", "Obesity_Type_III"]]

```text
# Instanciando o OrdinalEncoder com as categorias definidas
encoder = OrdinalEncoder(categories=categories)
```

```text
# Aplicando o encoder à coluna "NObeyesdad"
# É necessário transformar a coluna em um array 2D (usando df[['NObeyesdad']])
df['NObeyesdad'] = encoder.fit_transform(df[['NObeyesdad']]).astype(int)
```

# Exibindo as primeiras linhas para verificar a transformação df[['NObeyesdad']].head()

<!-- source_page: 16 -->

# Matriz de correlação:

selected_cols = [ 'Weight', 'family_history_with_overweight', 'FAVC', 'FCVC', 'FAF', 'TUE', 'MTRANS_Automobile', 'MTRANS_Walking', 'NObeyesdad' ]

```text
corr_filtered = df[selected_cols].corr()
plt.figure(figsize=(12, 10))
sns.heatmap(corr_filtered, annot=True, cmap='coolwarm', fmt=".2f")
plt.title('Matriz de Correlação')
```

plt.show()

**Figura 6 - Matriz de Correlação**

Fonte: autoria própria

<!-- source_page: 17 -->

Este gráfico é um mapa de calor que mostra a correlação entre as variáveis. Cada quadrado representa a relação entre duas variáveis, com valores que vão de -1 (relação inversa perfeita) a 1 (relação direta perfeita). Cores mais quentes (vermelho) indicam fortes correlações positivas, enquanto cores mais frias (azul) indicam fortes correlações negativas. Valores próximos de 0 significam pouca ou nenhuma correlação. Esse gráfico ajuda a identificar rapidamente quais variáveis estão fortemente relacionadas.

Resumo das correlações mais importantes:

- Weight vs. NObeyesdad:

- Correlação positiva mais forte. Quanto maior o peso, maior a probabilidade de estar em níveis elevados de obesidade. Isso é esperado, pois o nível de obesidade tende a aumentar conforme o peso sobe.

- family_history_with_overweight vs. NObeyesdad:

- Correlação positiva moderada. Indivíduos com histórico familiar de sobrepeso tendem a apresentar maiores níveis de obesidade.

- FAVC (Consumo de alimentos calóricos) vs. NObeyesdad:

- Correlação positiva moderada. Pessoas que consomem alimentos com alto teor calórico frequentemente têm maior probabilidade de atingir níveis mais altos de obesidade.

- FCVC (Frequência de consumo de vegetais) vs. NObeyesdad:

- Correlação negativa. Um maior consumo de vegetais costuma estar associado a níveis mais baixos de obesidade.

- FAF (Frequência de atividade física) vs. NObeyesdad:

- Correlação negativa. Praticar atividades físicas regularmente tende a reduzir o nível de obesidade.

- TUE (Tempo gasto em tecnologia) vs. NObeyesdad:

- Correlação positiva. Quanto mais tempo usando tecnologia (geralmente associado ao sedentarismo), maior o risco de obesidade.

- MTRANS (Modo de transporte) vs. NObeyesdad:

- Andar a pé (Walking) mostra correlação negativa com obesidade, enquanto o uso de automóvel apresenta correlação positiva, reforçando a influência do sedentarismo.

<!-- source_page: 18 -->

⚠️ Observação importante Você deve estar reparando que usamos muito NObeyesdad para comparar com as outras colunas, isso ocorre porque NObeyesdad é a nossa coluna alvo, também chamada de label ou target. Em um conjunto de dados, o label ou target é a variável que queremos prever ou classificar. Ela representa o resultado final que o modelo de aprendizado supervisionado deve aprender a identificar a partir das demais informações (as features). Ou seja, ao construir um modelo preditivo, usamos as demais colunas (como peso, consumo alimentar, histórico familiar, entre outras) para prever o valor de NObeyesdad. Essa variável é fundamental, pois orienta o treinamento do modelo, permitindo que ele aprenda a associar padrões nos dados com os diferentes níveis de obesidade e, assim, faça previsões precisas em novos registros.

j) Model (Modelar): Criando Modelos de Machine Learning 🦾🦾🦾

Nesse caso, como temos features com uma correlção muito esperada, como obsidade e peso, precisamos escolher as features que vamos usar no modelo para evitar:

1. Overfitting - Quando os dados se ajustão demais aos dados de treinamento e não conseguem generalizar.

2. Viés - O modelo pode atribuir importância indevida a features redundantes. Vamos remover:

- Peso (Weight) e obesidade (NObeyesdad) têm correlação esperada e óbvia. Poderíamos remover Weight se o objetivo é prever o nível de obesidade.

- Fumo (Smoke) porque apenas 2% dos dados indicam fumantes.

- Monitoramento de Calorias (SCC) porque apenas 4.5% dos indivíduos monitoram calorias.

- Beliscar entre refeições (CAEC) porque é redundante com NCP

- Deixar apenas MTRANS_Walking e MTRANS_Motorbike, porque as depois colunas são redundantes

- Histórico Familiar (family_history_with_overweight), embora correlacionada com o target, pode ser um vazamento indireto. Se o target é obesidade e o histórico familiar é um critério diagnóstico, o modelo pode aprender a

"trapacear".

<!-- source_page: 19 -->

```text
# Agora, vamos criar modelos de Machine Learning para prever o nível de
obesidade com base nos dados.
from sklearn.model_selection import train_test_split
```

remove_features = ['Height', 'Weight', 'MTRANS_Automobile', 'MTRANS_Public_ Transportation',

'MTRANS_Motorbike']

```text
cols_to_drop = remove_features + ['NObeyesdad']
# Separamos as features da nossa label
X = df.drop(cols_to_drop, axis=1)
y = df['NObeyesdad']
```

```text
# Usamos a biblioteca Sklearn para dividir o dataset em partes de treinamento
e de teste
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_
state=42)
```

from sklearn.ensemble import RandomForestClassifier

```text
# Selecionamos um dos modelos disponíveis na biblioteca Sklearn
# Você pode encontrar vários na documentação: https://scikit-learn.org/stable/
user_guide.html
model = RandomForestClassifier(random_state=42)
```

# treinamento do modelo usando os nossos dados model.fit(X_train, y_train)

# Vamos avaliar o desempenho do modelo no conjunto de teste:

from sklearn.metrics import accuracy_score, classification_report

```text
y_pred = model.predict(X_test)
accuracy = accuracy_score(y_test, y_pred)
print(f'Acurácia: {accuracy}')
```

print(classification_report(y_test, y_pred))

<!-- source_page: 20 -->

Acurácia: 0.8091482649842271 precision recall f1-score support

0
0.77 0.87 0.82
86
1
0.67 0.65 0.66
93
2
0.74 0.74 0.74
88
3
0.74 0.72 0.73
79
4
0.83 0.76 0.80
102
5
0.89 0.92 0.91
88
6
1.00 0.99 0.99
98

accuracy
0.81
634
macro avg
0.81 0.81 0.81
634
weighted avg
0.81 0.81 0.81
634

from sklearn.metrics import confusion_matrix

```text
# Gerar a matriz de confusão
cm = confusion_matrix(y_test, y_pred)
```

```text
# Plotar a matriz de confusão
plt.figure(figsize=(8, 6))
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues',
xticklabels=model.classes_,
yticklabels=model.classes_)
plt.xlabel('Predito')
plt.ylabel('Verdadeiro')
plt.title('Matriz de Confusão')
plt.show()
```

<!-- source_page: 21 -->

**Figura 7 - Matriz de Confusão**

Fonte: autoria própria

A acurácia nos diz qual a porcentagem de previsões corretas do modelo. Ou seja, em 93% das vezes acertamos corretamente a categoria de peso. O relatório de classificação nos dá mais detalhes sobre o desempenho do modelo para cada classe.

# Podemos usar o modelo para identificar as features mais importantes para prever o nível de obesidade:

```text
feature_importances = model.feature_importances_
features = X.columns
indices = np.argsort(feature_importances)[::-1]
```

```text
plt.figure(figsize=(12, 6))
plt.title('Importância das Features')
plt.bar(range(len(features)), feature_importances[indices], align='center')
plt.xticks(range(len(features)), [features[i] for i in indices], rotation=45)
plt.show()
```

<!-- source_page: 22 -->

**Figura 8 - Importância das Features**

Fonte: autoria própria

Agora que temos um modelo com 93% de acurácia, sabemos quais são as colunas/ features importantes que determinam, com maior certeza, os fatores da obesidade. Isso parece interessante para apresentar em uma reunião, certo? Lembram-se das nossas perguntas iniciais?

- Quais são os principais fatores de estilo de vida que contribuem para a obesidade em Goiás? 🤔

- Existe alguma relação entre obesidade e histórico familiar, consumo de alimentos calóricos, frequência de atividade física, etc.? 📊

- Podemos identificar grupos de pessoas com maior risco de obesidade? 🔍

- Como podemos usar essas informações para criar políticas públicas mais eficazes? 💡

k) Communicate (Comunicar): Apresentando os Resultados 🗣️ 🗣️ 🗣️

A análise dos dados ainda não terminou. Podemos querer aprofundar a interpretação de algumas informações. Por exemplo, a coluna "Gênero" aparece como a quinta variável mais importante no modelo, o que nos leva a questionar: existe um equilíbrio entre homens e mulheres ou algum dos gêneros apresenta uma tendência

<!-- source_page: 23 -->

maior à obesidade? Essa investigação pode revelar qual gênero tem um papel significativo nas disparidades dos níveis de obesidade e, consequentemente, auxiliar na elaboração de políticas públicas mais direcionadas.

```text
# Calculando a média do nível de obesidade para cada gênero
mean_obesity_by_gender = df.groupby('Gender')['NObeyesdad'].mean()
print(mean_obesity_by_gender)
```

```text
# Visualizando a média do nível de obesidade por gênero com um gráfico de
barras
plt.figure(figsize=(8,6))
sns.barplot(x='Gender', y='NObeyesdad', data=df, palette='viridis')
plt.title('Média do Nível de Obesidade por Gênero')
plt.xlabel('Gênero')
plt.ylabel('Média do Nível de Obesidade')
plt.show()
```

Gender 0 3.050562 1 3.175455 Name: NObeyesdad, dtype: float64 <ipython-input-60-187940caf2ee>:7: FutureWarning:

Passing 'palette' without assigning 'hue' is deprecated and will be removed in v0.14.0. Assign the 'x' variable to 'hue' and set 'legend=False' for the same effect.

sns.barplot(x='Gender', y='NObeyesdad', data=df, palette='viridis')

**Figura 9 - Média do Nível de Obesidade por Gênero**

Fonte: autoria própria

<!-- source_page: 24 -->

Lembrando que codificamos 0 para homens e 1 para mulheres, os resultados indicam que, em média, as mulheres apresentam um nível de obesidade ligeiramente superior ao dos homens. Contudo, essa diferença não é muito expressiva. Portanto, podemos concluir que, embora o gênero seja uma feature importante no modelo, sua influência sobre os níveis de obesidade é moderada, sugerindo que outros fatores também desempenham papéis significativos nessa variável. Se você precisa comunicar seus principais achados – digamos que, por exemplo, em uma Olimpíada de IA – aqui vão algumas sugestões:

1. Contextualize o Problema:

- Comece apresentando o cenário e a importância do estudo. Por exemplo, "O desafio de entender os fatores que influenciam a obesidade é crucial para desenvolver políticas de saúde eficazes."

2. Defina Perguntas-Chave:

- Formule questões que o seu trabalho pretende responder, como:

- "Quais fatores contribuem para os níveis de obesidade?"

- "Como o gênero impacta esses níveis?"

- Essas perguntas guiam a narrativa e deixam claro o foco da análise. 3. Conte a História dos Dados:

- Descreva brevemente as fontes e as transformações realizadas (como a codificação do gênero e a criação da variável ordinal para o nível de obesidade).

- Detalhe os métodos utilizados (modelos de machine learning, análise de correlação, etc.) de forma simplificada.

- Utilize gráficos e mapas de calor para ilustrar os principais achados – por exemplo, um gráfico de barras mostrando a importância das features e um mapa de calor para as correlações entre as variáveis.

4. Por fim, destaque os Insights:

- Ressalte as descobertas mais relevantes.

- Mostre como os resultados podem orientar ações reais, como campanhas de saúde, intervenções nutricionais e programas de incentivo à atividade física.

<!-- source_page: 25 -->

### 2.1.1 Conceitos e fundamentos

A Ciência de Dados é um campo multidisciplinar que integra habilidades de probabilidade, estatística, algoritmo, dentre outros, para transformar dados em informações valiosas. Em sua prática, o profissional utiliza um método que envolve compreender o problema, identificar padrões e contar uma história com os dados. A sigla AGEMC (Ask, Get, Explore, Model, Communication) caracteriza o método cíclico de extração de informações a partir de dados. Na Figura 10, estão ilustradas as cinco etapas fundamentais desta metodologia.

**Figura 10 - Método AGEMC.**

Fonte: Adaptada de Wickham & Grolemund (2017).

A descrição destas cinco etapas do AGEMC está apresentada na Figura 11.

<!-- source_page: 26 -->

**Figura 11 - Descrição das etapas do método AGEMC.**

Fonte: Adaptada de Wickham & Grolemund (2017).

Imagine um grupo de estudantes que deseja descobrir se o tempo de estudo influencia as notas em uma prova. Primeiro, eles formulam a pergunta central: "Será que alunos que estudam mais horas obtêm melhores notas?", depois coletam os dados registrando as horas estudadas e as notas em uma planilha. Em seguida, exploram os dados com gráficos e estatísticas para identificar padrões, aplicam uma regressão linear para quantificar a influência das horas estudadas nas notas e, por fim, comunicam os resultados por meio de um relatório ou apresentação que destaca a correlação encontrada e ressalta que outros fatores também podem impactar o desempenho. Na Figura 12, é apresentado um gráfico em que cada ponto azul (X) representa um aluno, relacionando suas horas de estudo e a nota obtida na prova. O gráfico ilustra uma tendência geral de que, conforme aumentam as horas de estudo, as notas tendem a melhorar, embora haja variações individuais devido a outros fatores.

<!-- source_page: 27 -->

**Figura 12 - Relação entre horas de estudo e nota na prova.**

Fonte: Autoria própria.

Já a Mineração de Dados, também conhecida como "Data Mining", é uma das etapas dentro da Ciência de Dados. Ela se concentra na descoberta de padrões, tendências e informações relevantes em grandes conjuntos de dados, usando técnicas de IA e estatística.

#### 2.1.1.1 Tratamento dos dados é muito importante!

Para que a Ciência de Dados e a Mineração de Dados produzam resultados confiáveis e úteis, o tratamento dos dados é fundamental. Dados brutos geralmente são incompletos, inconsistentes e ruidosos (contêm erros ou informações irrelevantes). O tratamento de dados envolve a limpeza, transformação e organização dos dados para garantir sua qualidade e adequação para análise.

#### 2.1.1.2 Pensando os dados de maneira estratégica

A Ciência de Dados e a Mineração de Dados não são apenas sobre técnicas e algoritmos; elas também exigem uma mentalidade estratégica. É preciso entender o problema que se quer resolver, definir os objetivos da análise e escolher as técnicas mais adequadas para cada situação. Além disso, é importante interpretar os resultados de forma crítica e comunicar os insights de forma clara e eficaz.

<!-- source_page: 28 -->

Agora é sua vez!

Acesse o notebook para colocar em prática o conteúdo adquirido e sintetizar o tópico de Ciência de Dados.

Saiba mais…

Imagine que um grupo de estudantes deseja saber qual a causa dos ataques de tubarões. Conversam com moradores de uma determinada ilha para entender como o problema ocorre. Os moradores da ilha relatam informações como: horário dos ataques, principais alvos (turistas) e o que comeram antes de entrar no mar. Acontece que a ilha é famosa por seu maravilhoso sorvete de chocolate, então todos os turistas consomem esse sorvete. Então, se coletarmos os dados de ataques de tubarão e consumo de sorvete podemos chegar à conclusão de que existe uma correlação entre essas duas variáveis. A Figura 13 ilustra essa correlação. Isso não é interessante?

**Figura 13 - Venda de sorvete versus ataques de tubarão**

Fonte: Anderson (2023). continua continua

<!-- source_page: 29 -->

Na Figura 13, a linha azul representa a quantidade de vendas de sorvete ao longo de um ano, enquanto a linha vermelha indica a quantidade de ataques de tubarão. Nota-se que ambas as linhas apresentam grande proximidade. Mas qual é o problema dessa análise? Curiosamente, os dados são correlacionados: os turistas consomem sorvete e, ao mesmo tempo, são os maiores alvos de ataques de tubarão. No entanto, essa análise é insuficiente para determinar que "tomar sorvete causa ataques de tubarão". É mais provável que, quando está quente, mais pessoas consumam sorvete e nadem no mar, o que explica a correlação entre as duas variáveis. A seguir, algumas recomendações de sites que exploram dados e gráficos. - 
No
site
Statorials®,
inspirado
no
Spurious
Correlation
(Anderson,
2023), encontram-se alguns gráficos inusitados que mostram correlações surpreendentes entre variáveis completamente desconexas, como a do tubarão e do sorvete.

- No site Gapminder, por exemplo, você encontra diversas perguntas de conhecimentos gerais, cada uma acompanhada por uma taxa de acerto. Em outras palavras, há uma probabilidade de errar, pois os dados não mentem. Será que você consegue acertar? GAPMINDER. The Worldview Upgrader. Disponível em: https://upgrader.gapminder. org. Acesso em: 1 fev. 2025.

## 2.2 Processamento de Linguagem Natural

Durante toda a história da humanidade, a linguagem foi a principal ferramenta para nos comunicarmos, transmitirmos ideias e organizarmos sociedades. Por meio da fala e da escrita, desenvolvemos culturas, ciência, literatura e sistemas políticos. A linguagem humana, no entanto, é extremamente complexa: envolve regras gramaticais, contextos sociais, ambiguidade, ironia, intenção e emoção. Quando a inteligência artificial se propõe a compreender a linguagem humana, ela está tentando decifrar um dos sistemas mais sofisticados já criados – e é exatamente essa a missão do campo chamado Processamento de Linguagem Natural (NLP - Natural Language Processing). O NLP busca construir sistemas capazes de compreender e gerar linguagem humana com sentido. Isso não significa apenas traduzir palavras para comandos, mas sim interpretar significados, considerar contextos e reagir de forma coerente. O grande desafio é que a linguagem não é uma sequência fixa de códigos: é viva, adaptável, diversa e até contraditória. Ao tentar automatizar o entendimento de linguagem, o NLP enfrenta problemas como ambiguidade semântica (uma mesma palavra pode

<!-- source_page: 30 -->

ter vários significados), variações linguísticas (diferentes formas de dizer a mesma coisa) e o papel do contexto (uma frase pode mudar totalmente de sentido dependendo da situação). Portanto, o NLP não é apenas uma aplicação da IA em interfaces tecnológicas: é uma tentativa de ensinar as máquinas a lidarem com a complexidade da comunicação humana, abrindo portas para avanços em educação, ciência, saúde, acessibilidade, entre muitas outras áreas.

Notebook Colab

a) Objetivos de Aprendizagem

Apresentar, na prática, conceitos básicos de Processamento de Linguagem Natural (PLN), incluindo classificação de texto com Bag of Words e construção de um chatbot com modelo de linguagem.

b) 📘 Introdução ao Processamento de Linguagem Natural

O Processamento de Linguagem Natural é uma área da Inteligência Artificial que capacita máquinas a entender, interpretar, gerar e interagir com a linguagem humana. Este notebook é uma introdução prática ao PLN (ou NLP, da sigla em inglês) com Python, dividida em duas partes:

1. Classificação de Texto com Bag of Words: Representamos textos numericamente e treinamos um classificador simples usando Naive Bayes.

2. Uso de Modelos de Linguagem para Construção de Chatbots: mostramos como utilizar um modelo de linguagem para gerar respostas às perguntas do usuário.

Vamos começar!

c) 🧠 Parte 1: Representação e Classificação de Texto

Nesta primeira parte, vamos entender como o computador “lê” texto e como podemos ensinar um modelo a dizer se uma frase é educacional ou não.

<!-- source_page: 31 -->

d) ⚙️ Instalando as bibliotecas necessárias

Antes de usar qualquer ferramenta em Python, precisamos instalar os pacotes que não fazem parte da instalação padrão. Isso é comum em notebooks como o Google Colab. 📌 Objetivo: Instalar o NLTK (para processamento de linguagem) e scikit-learn (para classificação).

```text
# Esta célula instala pacotes externos que serão usados no notebook.
# Ela é necessária apenas uma vez no início.
# "nltk" é uma biblioteca para processamento de texto.
# "scikit-learn" é usada para machine learning (classificação de texto).
```

!pip install nltk scikit-learn --quiet

f) 📚 Importando bibliotecas e baixando recursos

Agora que instalamos as bibliotecas, vamos carregá-las no notebook e preparar o que for necessário. 📌 Objetivo: importar ferramentas de NLP e machine learning e baixar os recursos de tokenização do NLTK.

```text
# Importação das bibliotecas principais
import pandas as pd # Manipulação de dados em formato de tabela (DataFrames)
from nltk.tokenize import word_tokenize, sent_tokenize # Funções de
tokenização
from sklearn.feature_extraction.text import CountVectorizer # Vetorização
de texto (Bag-of-Words)
from sklearn.naive_bayes import MultinomialNB # Classificador Naive Bayes
from sklearn.metrics import accuracy_score # Métrica de acurácia para
avaliação
```

```text
import nltk
# Download the necessary punkt_tab resource
nltk.download('punkt_tab")
nltk.download('punkt") #
```

[nltk_data] Downloading package punkt_tab to /root/nltk_data... [nltk_data] Package punkt_tab is already up-to-date! [nltk_data] Downloading package punkt to /root/nltk_data... [nltk_data] Package punkt is already up-to-date! True

<!-- source_page: 32 -->

g) ✂️ Tokenização: Frases e Palavras

Antes de podermos ensinar o computador a entender um texto, precisamos quebrá-lo em partes menores que ele possa processar. Esse processo se chama tokenização. 🔹 O que são tokens? Tokens são os fragmentos básicos do texto, como frases, palavras ou partes de palavras. Por exemplo:

- Um texto pode ser dividido em frases, usando pontuação como ponto final, interrogação ou exclamação.

- Cada frase, por sua vez, pode ser dividida em palavras. 🔹 Por que isso é importante? O computador não entende o texto como um todo. Ele precisa que a informação seja dividida para que possa:

- Contar palavras,

- Medir frequência de termos,

- Construir vetores numéricos,

- Alimentar modelos de aprendizado de máquina. 👉 Em resumo: tokenizar é o primeiro passo essencial para qualquer tarefa de Processamento de Linguagem Natural.

texto = "Hoje é um ótimo dia para aprender NLP! Vamos começar agora mesmo?"

```text
# Separar em frases
frases = sent_tokenize(texto)
print("Frases:", frases)
```

```text
# Separar em palavras
for i, frase in enumerate(frases):
print(f"Tokens da frase {i+1}:", word_tokenize(frase))
```

Frases: ['Hoje é um ótimo dia para aprender NLP!", 'Vamos começar agora mesmo?"] Tokens da frase 1: ['Hoje", 'é", 'um", 'ótimo", 'dia", 'para", 'aprender", 'NLP", '!"] Tokens da frase 2: ['Vamos", 'começar", 'agora", 'mesmo", '?"]

<!-- source_page: 33 -->

h) 🧮 Vetorização com Bag of Words

Modelos de machine learning não conseguem interpretar texto diretamente — eles trabalham apenas com números. Por isso, antes de usarmos um modelo, precisamos transformar o texto em uma representação numérica. 🔹 O que é a técnica Bag of Words? Uma forma simples e eficaz de fazer isso é usando o método Bag of Words (BoW). Essa técnica:

- Cria um vocabulário com todas as palavras que aparecem no nosso conjunto de textos;

- Conta quantas vezes cada palavra aparece em cada frase;

- Ignora a ordem das palavras, focando apenas na frequência. Por exemplo, se tivermos as frases:

- "Eu gosto de NLP"

- "NLP é incrível" O BoW criaria um vetor para cada frase, com base na contagem das palavras "eu", "gosto", "de", "nlp", "é", "incrível". 🔹 Para que serve isso? Com esses vetores numéricos, conseguimos:

- Alimentar modelos de classificação ou agrupamento,

- Medir similaridade entre frases,

- Criar representações úteis para visualização e análise. 👉 Agora que entendemos o conceito, vamos aplicar o Bag of Words na prática!

```text
# Nosso conjunto de frases com temas educacionais e não educacionais
corpus = [
```

"Estou estudando processamento de linguagem natural.", "Machine learning é uma subárea da IA.", "Hoje assisti uma palestra sobre inteligência artificial.", "O time ganhou a final do campeonato.", "Assisti um filme muito bom ontem.", "Vamos ao show da banda neste fim de semana." ]

# Rótulos correspondentes: o que cada frase representa continua continua

<!-- source_page: 34 -->

rotulos = [ "educacional", "educacional", "educacional", "não educacional", "não educacional", "não educacional" ]

```text
# Criamos um vetor que transforma texto em números com base na contagem de
palavras
vectorizer = CountVectorizer()
```

```text
# Transformamos o corpus em uma matriz de contagem
X = vectorizer.fit_transform(corpus)
```

```text
# Visualizamos o vocabulário aprendido (as palavras únicas)
print("Vocabulário:", vectorizer.get_feature_names_out())
```

```text
# Mostramos os dados em formato de tabela
df_freq = pd.DataFrame(X.toarray(), columns=vectorizer.get_feature_names_out())
df_freq.index = [f"Frase {i+1}" for i in range(len(corpus))]
df_freq
```

Vocabulário: ['ao" 'artificial" 'assisti" 'banda" 'bom" 'campeonato" 'da" 'de" 'do" 'estou" 'estudando" 'filme" 'fim" 'final" 'ganhou" 'hoje" 'ia" 'inteligência" 'learning" 'linguagem" 'machine" 'muito" 'natural" 'neste" 'ontem" 'palestra" 'processamento" 'semana" 'show" 'sobre" 'subárea" 'time" 'um" 'uma" 'vamos"]

i) 🤖 Classificação com Naive Bayes

Agora que conseguimos representar o texto como vetores numéricos (com o Bag of Words), estamos prontos para treinar um modelo de classificação. 🔹 O que vamos fazer? Vamos ensinar o computador a prever se uma frase é do tipo:

- Educacional (relacionada a estudo, ciência, tecnologia etc.), ou

- Não educacional (relacionada a lazer, cotidiano, eventos etc.)

<!-- source_page: 35 -->

Para isso, vamos usar um modelo chamado Naive Bayes, que é bastante comum e eficaz para tarefas simples de classificação de texto. 🔹 Mas atenção! ⚠️ Este é apenas um exemplo didático, com poucas frases de treino. Na prática, um modelo de machine learning precisa de centenas ou milhares de exemplos para aprender bem e generalizar para novos dados. Nosso objetivo aqui é apenas demonstrar o funcionamento básico de uma classificação automática de texto.

📌 Objetivo desta etapa

- Treinar um modelo Naive Bayes com um conjunto simples de frases e rótulos;

- Usar esse modelo para prever a categoria de frases novas.

```text
# Criamos o modelo de classificação com Naive Bayes
modelo = MultinomialNB()
```

# Treinamos o modelo com as frases e seus rótulos modelo.fit(X, rotulos)

j) 🔍Testando o Modelo com Novas Frases

Vamos agora verificar se o modelo consegue prever corretamente o tipo de frases que ele nunca viu antes.

```text
# Novas frases para testar
novas_frases = [
```

"Quero aprender mais sobre redes neurais.", "O show ontem foi espetacular!", "Estou estudando inteligência artificial.", "Meu time venceu o campeonato!" ]

```text
# Convertendo para o mesmo formato numérico usado no treinamento
X_test = vectorizer.transform(novas_frases)
```

# Fazemos as previsões continua continua

<!-- source_page: 36 -->

predicoes = modelo.predict(X_test)

```text
# Exibimos os resultados de cada frase
for frase, categoria in zip(novas_frases, predicoes):
print(f""{frase}" => Categoria prevista: {categoria}")
```

'Quero aprender mais sobre redes neurais." => Categoria prevista: educacional 'O show ontem foi espetacular!" => Categoria prevista: não educacional 'Estou estudando inteligência artificial." => Categoria prevista: educacional 'Meu time venceu o campeonato!" => Categoria prevista: não educacional

k) 📈 Avaliando o Modelo

Depois de treinar e testar um modelo, é importante saber se ele está acertando ou errando nas previsões. 🔹 Como avaliamos? Se tivermos os rótulos verdadeiros das frases que usamos no teste (ou seja, sabemos qual deveria ser a resposta correta), podemos comparar com as previsões do modelo. Uma forma simples de avaliação é a acurácia, que mede quantas vezes o modelo acertou, em relação ao total de exemplos. Por exemplo, se testamos 4 frases e o modelo acertou 3 delas, a acurácia será de 75%. 📌 Objetivo desta etapa Verificar a qualidade do modelo comparando suas previsões com as respostas corretas.

```text
# Rótulos reais (verdadeiros) das frases de teste
y_true = ["educacional", "não educacional", "educacional", "não educacional"]
```

```text
# Comparamos com as previsões
acc = accuracy_score(y_true, predicoes)
```

```text
# Mostramos a acurácia (porcentagem de acertos)
print(f"Acurácia do modelo no teste: {acc:.2f}")
```

Acurácia do modelo no teste: 1.00

<!-- source_page: 37 -->

A mensagem Acurácia do modelo no teste: 1.00 indica que o modelo acertou todas as previsões no conjunto de teste. Embora isso pareça excelente, é importante lembrar que usamos poucos exemplos e frases simples. Isso significa que o resultado pode não se repetir com dados reais ou mais variados. ✅Mesmo assim, conseguimos observar na prática como é possível:

- Representar texto como números,

- Treinar um classificador básico,

- E prever automaticamente a categoria de frases novas. Com isso, encerramos a parte de classificação de texto com Bag of Words e Naive Bayes.

l) Parte 2: 🤖 Utilizando Modelo de Linguagem para Construção de um Chatbot Simples

m) 📦 Instalação das bibliotecas necessárias

Instalamos a biblioteca huggingface-hub para acessar modelos hospedados na Hugging Face.

!pip install huggingface-hub --quiet

Instalamos a biblioteca gradio para termos uma interface de chat.

!pip install gradio

n) 🔑 Configuração do token de acesso

Para usar os modelos da Hugging Face — uma plataforma que oferece acesso a modelos de IA pré-treinados — é necessário criar uma conta gratuita e gerar um token de acesso com permissão para usar a API de inferência.

<!-- source_page: 38 -->

Passos para gerar o token:

1. Acesse huggingface.co e crie uma conta gratuita, se ainda não tiver.

2. Após logar, vá até a página de tokens: https://huggingface.co/settings/tokens

3. Clique em 'New token" (Novo token)

4. Dê um nome para o token, como token-chatbot

5. Selecione a permissão Inference: Make calls to Inference Providers (necessária para gerar respostas com os modelos)

6. Clique em 'Create token" e copie o código gerado

7. Crie uma nova chave secreta na barra lateral à esquerda (colab) com nome "token" e o valor do token da HuggingFace

⚠️ Importante: guarde seu token com segurança e nunca o compartilhe publicamente.

from huggingface_hub import InferenceClient

o) 🔑 Configuração do token de acesso

Aqui você coloca o seu token de acesso (com permissão Access Inference API) para se conectar com os modelos da Hugging Face.

```text
from google.colab import userdata
token = userdata.get('token")
```

client = InferenceClient(token=token)

🤖 Modelo utilizado no chatbot Para garantir uma experiência de uso fluida, com boa qualidade de resposta e compatibilidade total com a API pública gratuita da Hugging Face, utilizaremos o modelo: HuggingFaceH4/zephyr-7b-beta: modelo de 7 bilhões de parâmetros, otimizado para seguir instruções de forma clara, consistente e adaptada a múltiplos idiomas, incluindo o português. Ele apresenta excelente equilíbrio entre velocidade, custo e

<!-- source_page: 39 -->

qualidade de geração de respostas, sendo ideal para aplicações educacionais e interativas como este chatbot. Escolhemos o zephyr-7b-beta por ser atualmente uma ótima opção gratuita disponível, combinando alta qualidade nas respostas com acesso estável pela API da Hugging Face, sem restrições de tamanho.

p) 🧠 Definindo o comportamento do chatbot

O system prompt é uma instrução especial enviada ao modelo antes de qualquer pergunta do usuário. Ele serve para definir o comportamento, o estilo de linguagem e o papel que o assistente deve desempenhar ao longo da conversa. No nosso caso, estamos configurando o modelo para agir como um professor de biologia simpático, que responde de forma didática, clara e acessível, sempre em português. Isso ajuda o modelo a manter coerência no tom, usar exemplos simples e ajustar o vocabulário para alunos do ensino médio. 📌 O system prompt funciona como uma "personalidade base" para o chatbot — é como se estivéssemos dizendo: "Finja que você é esse personagem e se comunique da forma mais apropriada para esse papel."

system_prompt = ( "Você é um assistente de biologia simpático. Sempre que o usuário fizer uma pergunta relacionada à biologia, responda de forma clara, objetiva e em português. Se a pergunta não estiver relacionada a biologia, responda normalmente de forma simpática." )

q) 💬 Função do chatbot interativo

Esta função inicia um bate-papo com o modelo escolhido. Parâmetros da geração de texto:

- prompt: o texto enviado ao modelo, incluindo o system prompt e a pergunta;

- model: o nome do modelo que será usado (ver opções abaixo);

- max_new_tokens=300: número máximo de tokens na resposta. Respostas mais longas usam valores maiores;

- temperature=0.3: controla a criatividade. Valores baixos = respostas mais objetivas; altos = mais criativas.

<!-- source_page: 40 -->

```text
# Histórico de conversas
historico = []
```

```text
# Função para formatar o histórico para exibição
def formatar_historico(hist):
texto = ""
for m in hist:
prefixo = "🧑 Usuário:" if m["role"] == "user" else "🤖 Assistente:"
texto += f"{prefixo} {m['content"]}\n\n"
return texto.strip()
```

# Função principal da IA def responder_pergunta(mensagem, reset=True): global historico

if reset: historico = []

```text
# Prompt formatado apenas com a pergunta atual, sem histórico
prompt_formatado = (
```

f"SYSTEM: {system_prompt}\n" f"USER: {mensagem}\nASSISTANT:" )

resposta_raw = client.text_generation(

```text
prompt=prompt_formatado,
model="HuggingFaceH4/zephyr-7b-beta",
max_new_tokens=300,
temperature=0.3
)
```

```text
# Processa o retorno
if isinstance(resposta_raw, str):
resposta = resposta_raw
elif isinstance(resposta_raw, list):
resposta = resposta_raw[0]["generated_text"]
elif isinstance(resposta_raw, dict):
resposta = resposta_raw.get("generated_text", "")
else:
resposta = str(resposta_raw)
```

resposta_limpa = resposta.split("ASSISTANT:")[-1].strip()

historico.append({"role": "user", "content": mensagem}) historico.append({"role": "assistant", "content": resposta_limpa})

return resposta_limpa, formatar_historico(historico)

<!-- source_page: 41 -->

r) 🧪 Testes diretos com o modelo no notebook

Antes de criarmos uma interface visual, é importante testar se o modelo está funcionando corretamente por meio de entradas e saídas diretas no terminal do notebook. Nesta etapa, vamos digitar perguntas manualmente no terminal do Colab e ver as respostas geradas pelo modelo. Esse teste ajuda a:

- Validar se a conexão com o modelo está funcionando.

- Observar a qualidade das respostas.

- Avaliar o comportamento do system prompt em ação. 💡 Para encerrar o teste, digite sair ou exit no prompt.

# Teste interativo no Colab — digite perguntas diretamente

print("🔬 Chat de Biologia — digite 'sair" para encerrar\n")

while True: pergunta = input("Você: ") if pergunta.strip().lower() in ["sair", "exit", "quit"]:

```text
print("Encerrado.")
break
resposta, _ = responder_pergunta(pergunta)
print(f"Assistente: {resposta}\n")
```

🔬 Chat de Biologia — digite 'sair" para encerrar

Você: O que é DNA? Assistente: DNA (sigla de Deoxyribonucleic acid) é o material genético que carrega as instruções para a construção e funcionamento de todos os seres vivos. É composto por moléculas longas e estreitas, formadas por quatro tipos de bases (adenina, timina, guanina e citosina), que se combinam em pares específicos (adenina com timina e guanina com citosina) para formar as famosas "escalas de gel" características do DNA. O DNA se enrola em estruturas complexas, como o cromossomo, que são responsáveis pela transmissão hereditária de características de um organismo para outro.

Você: sair Encerrado.

<!-- source_page: 42 -->

s) 💬 Criando uma interface amigável com Gradio

Após testar o modelo manualmente, vamos criar uma interface interativa com Gradio, onde:

- O usuário digita sua pergunta em uma caixa de texto

- O chatbot responde de forma clara e visual

- Todo o histórico da conversa é mostrado abaixo Isso permite que qualquer pessoa interaja com o modelo de forma simples, como em um chat educacional. 🌿 O tema da interface é voltado para a Biologia, com um design amigável e educativo!

```text
# Interface Gradio com visual temático de biologia
import gradio as gr
```

with gr.Blocks(theme=gr.themes.Base()) as demo: gr.Markdown(

""" <h1 style="text-align: center; color: #2E8B57;">💬 Chat de Biologia</h1> <p style="text-align: center; font-size: 16px;">Tire suas dúvidas sobre biologia com um assistente inteligente 🌿</p>

""" )

with gr.Row(): entrada = gr.Textbox(label="Pergunta", placeholder="Ex: O que é fotossíntese?", lines=3, scale=1)

botao = gr.Button("Enviar", scale=0)

gr.Markdown("---") saida = gr.Textbox(label="Resposta do Assistente", lines=5, interactive=False)

with gr.Accordion("📚 Ver histórico completo da conversa", open=False): historico_area = gr.Textbox(label="", lines=10, interactive=False)

def responder_gradio(texto): resposta, hist = responder_pergunta(texto) return resposta, hist

botao.click(fn=responder_gradio, inputs=entrada, outputs=[saida, historico_ area])

demo.launch() continua continua

<!-- source_page: 43 -->

It looks like you are running Gradio on a hosted a Jupyter notebook. For the Gradio app to work, sharing must be enabled. Automatically setting `share=True` (you can turn this off by setting `share=False` in `launch()` explicitly).

Colab notebook detected. To show errors in colab notebook, set debug=True in launch() * Running on public URL: https://841f071c3887812580.gradio.live

This share link expires in 1 week. For free permanent hosting and GPU upgrades, run `gradio deploy` from the terminal in the working directory to deploy to Hugging Face Spaces (https://huggingface.co/spaces)

✅Conclusão – Modelos de Linguagem e Chatbots Nesta etapa, utilizamos um modelo de linguagem para construir um chatbot simples, capaz de gerar respostas a perguntas de forma automatizada. Exploramos os seguintes conceitos:

- Como configurar o acesso à API da Hugging Face;

- Como definir um system prompt para orientar o comportamento do assistente;

- Como testar o modelo diretamente no notebook;

- E como criar uma interface amigável com Gradio para interação. Essa abordagem mostra como é possível integrar modelos de linguagem em aplicações práticas, mesmo em cenários educacionais e experimentais. Com isso, finalizamos a parte de uso de modelos de linguagem para construção de um chatbot simples.

<!-- source_page: 44 -->

t) Referências Bibliográficas

BIRD, Steven; KLEIN, Ewan; LOPER, Edward. Natural Language Processing with Python: Analyzing Text with the Natural Language Toolkit. Sebastopol: O"Reilly Media, 2009. LECUN, Yann; BENGIO, Yoshua; HINTON, Geoffrey. Deep learning. Nature, v. 521, n. 7553, p. 436–444, 2015. DOI: https://doi.org/10.1038/nature14539. VASWANI, Ashish et al. Attention is all you need. In: ADVANCES in Neural Information Processing Systems 30 (NeurIPS 2017). p. 5998–6008, 2017. RUSSELL, Stuart J; NORVIG, Peter. Inteligência artificial: uma abordagem moderna. 4. ed. Rio de Janeiro: GEN LTC, 2022. 1080 p.

<!-- source_page: 45 -->

### 2.2.1 Conceitos e fundamentos

Ao longo do desenvolvimento do NLP, diferentes estratégias foram criadas para tornar possível que computadores lidem com a linguagem humana de forma eficaz. Três formas principais se destacam nesse processo. A primeira foi baseada em regras gramaticais fixas. Nela, os sistemas eram programados com instruções sobre como a língua funciona, como identificar sujeitos, verbos ou objetos. Esse método funcionava bem com frases simples, mas falhava quando apareciam gírias, metáforas ou variações de linguagem. É como seguir um manual de receitas: funciona quando tudo está dentro do esperado, mas não quando os ingredientes mudam. Com a internet e os textos digitais, surgiu uma segunda forma: o uso de estatísticas. Os computadores passaram a aprender com grandes quantidades de texto, observando quais palavras aparecem juntas com mais frequência. Por exemplo, perceberam que "bom" aparece muito com "dia" ou "filme". Esse tipo de aprendizado permite que o sistema reconheça padrões, mesmo sem entender o significado das palavras, como alguém que aprende uma nova língua apenas ouvindo muito outras pessoas. A abordagem mais recente, e mais avançada, usa redes neurais artificiais, inspiradas no funcionamento do cérebro humano. Nela, as palavras são transformadas em números e organizadas em um espaço matemático, onde palavras parecidas ficam próximas. Por exemplo, "rei" e "rainha" aparecem em contextos semelhantes e, por isso, ficam próximas nesse espaço. Assim, o sistema consegue entender relações de sentido, como "feliz" estar mais perto de "alegre" do que de "triste". Essa forma de aprendizagem permite que a máquina compreenda melhor o contexto e até crie textos novos com base no que aprendeu. Para que isso funcione, o NLP segue algumas etapas. Primeiro, a frase é dividida em partes menores, como palavras – essa etapa se chama tokenização. Depois, cada palavra é analisada: que tipo de palavra é (verbo, substantivo, advérbio, etc.) e qual sua função na frase. Em seguida, o sistema tenta entender o significado das palavras e da frase como um todo. Finalmente, ele interpreta o contexto e a intenção por trás do que foi dito. Por exemplo, na frase "está chovendo canivete", o sistema precisa entender que isso é uma metáfora para uma chuva muito forte, e não algo literal. Além disso, os computadores precisam transformar as palavras em números, já que é assim que eles operam. Cada palavra vira um vetor, uma sequência de valores que indica como ela se relaciona com outras. Palavras usadas em contextos parecidos terão vetores parecidos. Isso permite que o sistema reconheça, por exemplo, que "amor" e "carinho" têm sentidos próximos, mesmo que sejam palavras diferentes.

<!-- source_page: 46 -->

Esses fundamentos mostram como o NLP tenta ensinar as máquinas a compreenderem a linguagem de forma cada vez mais próxima da nossa. Ainda que os computadores não "entendam" como nós, eles conseguem identificar padrões, interpretar frases e produzir textos úteis e coerentes.

#### 2.2.1.1 Como os computadores aprendem a escrever como humanos?

Nos últimos anos, o NLP deu um salto enorme com o surgimento dos Modelos de Linguagem de Grande Escala, conhecidos como LLMs (Large Language Models). Esses modelos são sistemas de inteligência artificial treinados para compreender e gerar linguagem humana com um nível impressionante de naturalidade e coerência. Um dos marcos desse avanço foi a introdução da arquitetura Transformer, proposta por Vaswani et al. (2017). Baseada em um mecanismo de atenção, ela permite que o modelo identifique quais partes do texto são mais relevantes para entender o significado geral. Embora tenha sido criada para tradução automática, a arquitetura se tornou a base dos principais modelos de linguagem atuais e é considerada um dos pilares da inteligência artificial moderna. Mas afinal, o que é um modelo de linguagem? De forma simples, é um tipo de sistema que aprende a prever qual será a próxima palavra de uma frase com base nas palavras anteriores. Essa tarefa, aparentemente simples, exige que o modelo compreenda estruturas linguísticas, relações de significado, contexto e até intenções do falante. Por exemplo, ao ler a frase "Hoje eu vou à...", o modelo pode prever que a próxima palavra será "escola", "praia" ou "igreja", pois são termos que costumam completar esse tipo de construção de maneira coerente. Por outro lado, dificilmente ele sugeriria uma palavra como "geladeira", que quebraria o sentido da frase. Durante o processo de treinamento, o modelo passa por milhões de exemplos desse tipo, ajustando seus parâmetros internos – que podem ser imaginados como pequenos botões que controlam suas decisões – até conseguir gerar textos que façam sentido em diferentes contextos. Quanto maior o modelo, maior sua capacidade de lidar com situações linguísticas complexas, como metáforas, ambiguidade ou perguntas abertas. Esses modelos se tornam ainda mais impressionantes quando ganham uma capacidade chamada "generativa". Um modelo generativo não apenas entende padrões, mas também é capaz de criar novos conteúdos com base nesses padrões. Isso significa que, em vez de simplesmente classificar um texto ou identificar uma informação, ele pode escrever uma redação, responder a uma pergunta aberta, resu-

<!-- source_page: 47 -->

mir um artigo ou até inventar uma história. Em outras palavras, modelos generativos transformam a IA em uma ferramenta criativa, que consegue produzir novas respostas a partir do que aprendeu. Na Figura 14, tem-se uma representação simplificada do aprendizado de modelos de linguagem.

**Figura 14 - Representação simplificada do aprendizado de modelos de linguagem**

Fonte: Autoria própria.

O funcionamento de um modelo generativo baseado em linguagem pode ser comparado ao de uma pessoa que leu milhares de livros e artigos, e que usa esse conhecimento para produzir um texto novo cada vez que alguém lhe faz uma pergunta. Por exemplo, se o modelo recebe a instrução "Explique o que é um modelo de linguagem para estudantes do ensino médio", ele irá recuperar, entre os padrões que aprendeu, formas comuns de explicar esse conceito, vocabulário apropriado para o público e estruturas típicas de uma explicação. A resposta gerada será construída palavra por palavra, de forma inédita, considerando o conteúdo da pergunta e a forma como as pessoas costumam abordar esse tema. Assim, os LLMs representam uma das mais impressionantes conquistas recentes da inteligência artificial, trazendo o uso da linguagem para o centro das interações entre humanos e máquinas. Sua capacidade de compreender e produzir texto de forma fluente está transformando a maneira como aprendemos, trabalhamos e nos comunicamos com a tecnologia.

<!-- source_page: 48 -->

#### 2.2.1.2 Aplicações de Processamento de Linguagem Natural

As tecnologias baseadas em NLP estão presentes em diversas situações do cotidiano, muitas vezes sem que o usuário perceba. Um exemplo clássico são os assistentes virtuais. Quando alguém pergunta "Vai chover hoje?", o sistema identifica que se trata de uma solicitação relacionada à previsão do tempo, busca a informação adequada e responde de forma clara e compreensível. Outro exemplo são os teclados de smartphones, que sugerem palavras ou completam frases automaticamente. Isso acontece porque o modelo reconhece padrões comuns de escrita. Se alguém começa a digitar "eu te amo...", é provável que o sistema sugira palavras como "muito" ou "demais" com base em seu aprendizado prévio. Os chatbots utilizados em sites de compras ou serviços também utilizam NLP para compreender e responder a perguntas frequentes, como "Qual o valor do frete?" ou "Como faço para trocar um produto?". Além disso, plataformas de redes sociais utilizam NLP para traduzir publicações, filtrar conteúdos ofensivos ou identificar opiniões positivas e negativas em comentários. Outras aplicações incluem sistemas de filtragem de spam, que analisam o conteúdo de e-mails para separar mensagens indesejadas, e ferramentas de correção automática de textos, que ajudam o usuário a escrever de forma mais clara e correta. Tradutores automáticos também se tornaram mais eficazes, conseguindo adaptar frases completas ao contexto e até interpretar expressões idiomáticas corretamente. Um exemplo bastante conhecido do uso de NLP em conjunto com LLMs é o ChatGPT®. Desenvolvido pela empresa OpenAI®, o ChatGPT® é um modelo de linguagem capaz de conversar com usuários por meio de textos. Ele analisa a mensagem recebida, interpreta o que está sendo pedido e gera uma resposta nova, elaborada em tempo real. Por meio dessa ferramenta, é possível tirar dúvidas, pedir explicações, receber sugestões de escrita, resolver problemas de linguagem e até criar histórias ou textos acadêmicos. O ChatGPT® mostra como os avanços no NLP e nos modelos generativos estão sendo aplicados diretamente no cotidiano das pessoas, facilitando a comunicação com a tecnologia. Esses exemplos mostram como o NLP já faz parte da vida cotidiana e como ele contribui para tornar a interação com a tecnologia mais fluida, natural e inteligente.

<!-- source_page: 49 -->

Agora é sua vez!

Acesse o notebook para colocar em prática o conteúdo adquirido e sintetizar o tópico de Processamento de Linguagem Natural.

Saiba mais...

Para aprofundar os conhecimentos sobre NLP, é possível explorar recursos que apresentam tanto os fundamentos quanto aplicações práticas da área.

Blog do Hugging Face®: O Hugging Face® é uma das principais plataformas de desenvolvimento em NLP atualmente. Seu blog reúne textos atualizados sobre o funcionamento de modelos de linguagem, avanços na área e tutoriais com exemplos reais. É indicado para quem deseja entender como as tecnologias de NLP são desenvolvidas e aplicadas em diferentes contextos. Disponível em: https://huggingface.co/blog.

Documentação do NLTK (Natural Language Toolkit): O NLTK é uma biblioteca de Python voltada ao ensino e experimentação em NLP. Sua documentação oferece explicações de conceitos fundamentais, como tokenização e análise gramatical, além de exemplos práticos e exercícios. É indicada para iniciantes que desejam explorar como os computadores processam textos. A biblioteca é utilizada como base no livro Natural Language Processing with Python (Bird; Klein; Loper, 2009), escrito pelos próprios autores do NLTK. Disponível em: https://www.nltk.org.

ChatGPT®: O ChatGPT® é um exemplo direto da aplicação de modelos de linguagem em interfaces interativas. Por meio da interação com a ferramenta, é possível observar como o sistema interpreta perguntas, gera textos e se adapta a diferentes contextos comunicativos. A experiência permite compreender, na prática, como funcionam os modelos generativos. Disponível em: https://chat.openai.com.

<!-- source_page: 50 -->

## 2.3 Visão computacional

De acordo com a IBM® (2021), a visão computacional é uma área da IA dedicada a fazer com que máquinas sejam capazes de interpretar, analisar e compreender informações visuais extraídas de imagens ou vídeos, fazendo com que as máquinas possam "enxergar" e compreender o mundo real. Inspirada nos processos de percepção humana, essa área integra técnicas de processamento de imagem, análise de padrões e aprendizado de máquina para extrair detalhes relevantes das imagens, objetos e/ou formas. Ao converter em dados o que a máquina "lê", os algoritmos conseguem detectar características, reconhecer elementos e, a partir disso, executar ações ou recomendações de maneira autônoma. O desenvolvimento dessa área começou a ganhar força nos anos 1960, quando pesquisadores tentavam compreender como o cérebro humano reconhece imagens para replicar esse processo em computadores. Foi em 1989 que Yann LeCun introduziu a LeNet® (capaz de reconhecer manuscritos), uma arquitetura que impactou diretamente no que hoje é conhecida como rede neural convolucional, uma técnica extremamente importante e que abriu as portas para diversos outros pesquisadores descobrirem novas aplicações com ela (Lecun et al., 1989). Esse campo tem experimentado crescimento acelerado graças aos avanços do hardware e ao surgimento de redes neurais profundas mais complexas, além de técnicas complementares, que permitem uma compreensão mais sofisticada de contextos visuais e tornam a interação entre computadores e ambientes físicos cada vez mais precisa.

Acesse o notebook no Google Colab®

<!-- source_page: 51 -->

Notebook Colab

a) Objetivos de Aprendizagem

Nesse notebook vamos aprender na prática (em código) os conceitos que foram ensinados nas aulas teóricas. Vamos explorar um pouco mais sobre as imagens digitais e operações comuns em projetos de Visão Computacional, além de treinarmos nossos primeiros modelos e por fim, aprender a importar e usar um modelo muito famoso conhecido como YOLO.

b) 🧠 Mini Curso de Visão Computacional para Iniciantes

😎 Introdução Bem-vindos ao nosso mini curso prático de Visão Computacional! 👀 Uma área fascinante da inteligência artificial que busca dar aos computadores a capacidade de "enxergar" e interpretar imagens e vídeos, semelhante ao sistema visual humano. Esta é uma das áreas mais impactantes da IA! Você já usou um filtro no Instagram? Ou desbloqueou o celular com o rosto? Talvez tenha visto carros que dirigem sozinhos? Tudo isso é possível graças à Visão Computacional! 👀 Nesta aula prática, vamos explorar os fundamentos da visão computacional através de exemplos práticos utilizando a biblioteca OpenCV, que é a mais popular para processamento de imagens e vídeos. Não se preocupe se você não tem experiência com programação avançada - nosso foco será em experimentar e entender os conceitos através da prática.

📚 O que você vai aprender hoje: ✅Utilizar a biblioteca OpenCV para manipular imagens. ✅Aplicar diferentes filtros e transformações em imagens. ✅Entender como as imagens são representadas digitalmente. ✅Experimentar um modelo simples de reconhecimento com o dataset MNIST.

<!-- source_page: 52 -->

c) 📚 Introdução ao OpenCV

d) 🤔 O que é o OpenCV?

OpenCV (Open Source Computer Vision Library) é uma biblioteca de código aberto focada em visão computacional e aprendizado de máquina. Ela foi criada pela empresa Intel em 1999 e, desde então, evoluiu com a ajuda de uma grande comunidade global. Hoje, é uma das ferramentas mais usadas no mundo quando se fala em visão computacional e inteligência artificial.

🛠️ Por que o OpenCV é tão popular? Veja algumas das principais características do OpenCV:

- ✅ Gratuito e de código aberto – pode ser usado tanto por estudantes quanto por empresas sem custos (licença BSD).

- 💻 Compatível com várias linguagens, como Python (que usaremos aqui), C++ e Java.

- 🚀 Possui mais de 2500 algoritmos otimizados para tarefas como detecção de rostos, movimento, bordas, filtros de imagem e muito mais!

- 🌍 Está presente em milhões de aplicações pelo mundo — de projetos escolares a grandes empresas de tecnologia.

e) ⚙️ Instalando e Configurando o OpenCV

💡 Boa notícia!

Como estamos usando o Google Colab, o OpenCV já vem instalado por padrão, o

que torna tudo fácil e rápido! 🙌

No Colab, você pode seguir em frente sem se preocupar com isso. Vamos começar a usar! 🖼📸

import cv2

```text
import numpy as np
import matplotlib.pyplot as plt
from IPython.display import display, Image
```

<!-- source_page: 53 -->

```text
# Verificando a versão do OpenCV
print(f"Versão do OpenCV: {cv2.__version__}")
```

Versão do OpenCV: 4.11.0

```text
# Configurando o matplotlib para exibir imagens em linha
%matplotlib inline
plt.rcParams['figure.figsize'] = [10, 8] # Tamanho padrão das figuras
```

f) 🖼️ Primeira Imagem com OpenCV

# Baixando uma imagem de exemplo (caso esteja no Colab ou em um ambiente online) !wget -O exemplo.jpg https://raw.githubusercontent.com/opencv/opencv/ master/samples/data/lena.jpg 2>/dev/null || curl -s -o exemplo.jpg https:// raw.githubusercontent.com/opencv/opencv/master/samples/data/lena.jpg

```text
# Carregando a imagem
imagem = cv2.imread('exemplo.jpg')
```

```text
# Exibindo a imagem
plt.figure(figsize=(8, 6))
plt.imshow(imagem)
plt.title('Imagem em BGR')
plt.axis('off') # Remover os eixos para uma visualização mais limpa
plt.show()
```

<!-- source_page: 54 -->

**Figura 15 - Lena em BGR**

Fonte: autoria própria

e) 💡Curiosidade:

O OpenCV carrega as imagens em um formato de cores diferente do que estamos acostumados (BGR em vez de RGB). Por isso, fazemos uma conversão simples para que as cores apareçam corretamente.

```text
# Para exibir corretamente com matplotlib, precisamos converter para RGB
imagem_rgb = cv2.cvtColor(imagem, cv2.COLOR_BGR2RGB)
```

```text
# Exibindo a imagem
plt.figure(figsize=(8, 6))
plt.imshow(imagem_rgb)
plt.title('Imagem em RGB')
plt.axis('off') # Remover os eixos para uma visualização mais limpa
plt.show()
```

<!-- source_page: 55 -->

**Figura 16 - Lena em RGB**

Fonte: autoria própria

f) 🧩 Entendendo a Estrutura de uma Imagem Digital

Antes de aplicar filtros ou treinar modelos, é importante entender: como o computador "enxerga" uma imagem?

📷 Como as imagens são representadas? Para nós, uma imagem pode parecer apenas uma fotografia. Mas para o computador, ela é uma matriz de números — ou seja, uma tabela cheia de valores.

- Cada imagem digital é composta por:

- Pixels: os pequenos pontos que formam a imagem. Cada pixel tem uma cor representada por números.

- Canais de cor: normalmente três — vermelho (R), verde (G) e azul (B). Cada canal também é uma matriz de números!

- Resolução: é o tamanho da imagem, dado pelo número de pixels na largura x altura (por exemplo, 640x480).

```text
# Vamos verificar a forma (shape) da nossa imagem
altura, largura, canais = imagem_rgb.shape
print(f"Dimensões da imagem: {largura} x {altura} pixels")
continua
continua
```

<!-- source_page: 56 -->

```text
print(f"Número de canais: {canais}")
print(f"Tipo de dados: {imagem_rgb.dtype}")
print(f"Tamanho total em memória: {imagem_rgb.nbytes / 1024:.2f} KB")
```

Dimensões da imagem: 512 x 512 pixels Número de canais: 3 Tipo de dados: uint8 Tamanho total em memória: 768.00 KB

```text
# Vamos ver os valores de pixel de uma pequena região
print("\nValores de pixels em uma pequena região (10x10):")
print(imagem_rgb[100:105, 100:105, 0]) # Canal vermelho (R) de uma pequena
região
```

Valores de pixels em uma pequena região (10x10): [[182 175 177 173 179] [180 175 177 173 175] [172 174 178 175 173] [167 173 177 172 180] [175 171 178 176 177]]

```text
print("\nValores de pixels em uma pequena região (5x5):")
print(imagem_rgb[100:105, 100:105, 1]) # Canal verde (G) de uma pequena região
```

Valores de pixels em uma pequena região (5x5): [[74 67 71 67 73] [74 69 71 67 68] [66 68 73 70 66] [61 67 72 67 73] [71 65 72 69 67]]

```text
print("\nValores de pixels em uma pequena região (5x5):")
print(imagem_rgb[100:105, 100:105, 2]) # Canal azul (B) de uma pequena região
```

Valores de pixels em uma pequena região (5x5): [[87 80 81 77 83] [84 79 81 77 76] [76 78 80 77 72] [71 77 79 74 79] [82 77 82 79 76]]

continua continua

<!-- source_page: 57 -->

🤔 Por que isso importa? Entender essa estrutura é essencial para:

- Aplicar efeitos e transformações;

- Analisar partes específicas da imagem;

- Treinar modelos de reconhecimento visual.

🌈 Acessando os Canais de Cor Agora que você já sabe que uma imagem colorida é composta por três canais de cor — vermelho (Red), verde (Green) e azul (Blue) — vamos dar um passo além:

g) 🎯 Objetivo:

Vamos visualizar cada canal separadamente, para entender como cada um contribui para formar a imagem final.

```text
# Separando os canais R, G, B
r = imagem_rgb[:, :, 0] # Canal vermelho
g = imagem_rgb[:, :, 1] # Canal verde
b = imagem_rgb[:, :, 2] # Canal azul
```

# Criando uma figura com 4 subplots (imagem original + 3 canais) fig, axs = plt.subplots(1, 4, figsize=(20, 5))

# Exibindo a imagem original e cada canal axs[0].imshow(imagem_rgb) axs[0].set_title('Imagem Original') axs[0].axis('off')

axs[1].imshow(r, cmap='Reds') axs[1].set_title('Canal Vermelho') axs[1].axis('off')

axs[2].imshow(g, cmap='Greens') axs[2].set_title('Canal Verde') axs[2].axis('off')

axs[3].imshow(b, cmap='Blues') axs[3].set_title('Canal Azul') axs[3].axis('off')

plt.tight_layout() plt.show() continua continua

<!-- source_page: 58 -->

**Figura 17 - Lena em canais RGB**

Fonte: autoria própria

🔍 O que acontece em cada canal?

- O canal azul mostra a intensidade da cor azul em cada pixel. 🟦

- O canal verde mostra onde a cor verde está mais forte. 🟩

- O canal vermelho mostra a presença da cor vermelha. 🟥

⚫⚪ Convertendo para Escala de Cinza Uma das operações mais simples e importantes em visão computacional é converter uma imagem colorida em escala de cinza.

h) 🎯 Mas o que é "escala de cinza"?

Em vez de três canais de cor (vermelho, verde e azul), a imagem passa a ter apenas um canal, com tons que vão do preto ao branco.

Cada pixel da imagem cinza representa um nível de brilho, onde:

- 0 = preto (sem luz)

- 255 = branco (máxima luz)

- Valores entre eles = tons de cinza

```text
# Convertendo a imagem para escala de cinza
imagem_cinza = cv2.cvtColor(imagem_rgb, cv2.COLOR_BGR2GRAY)
```

<!-- source_page: 59 -->

```text
# Exibindo a imagem em escala de cinza
plt.figure(figsize=(8, 6))
plt.imshow(imagem_cinza, cmap='gray')
plt.title('Imagem em Escala de Cinza')
plt.axis('off')
plt.show()
```

**Figura 18 - Lena em escalas de cinza**

Fonte: autoria própria

```text
# Visualizando as dimensões da imagem em escala de cinza
print(f"Dimensões da imagem em escala de cinza: {imagem_cinza.shape}")
```

Dimensões da imagem em escala de cinza: (512, 512)

i) 🤔 Por que usamos isso?

Converter para escala de cinza é útil porque:

- Simplifica o processamento: uma única matriz em vez de três;

- Reduz o tamanho da imagem (menos dados);

- É suficiente para muitas tarefas, como detecção de bordas e reconhecimento de padrões.

<!-- source_page: 60 -->

🌈 O que são "espaços de cor"? Um espaço de cor é um modo de organizar e representar as cores de uma imagem. Cada espaço de cor tem um jeito diferente de descrever os valores de cor de cada pixel.

j) 🧪 Vamos experimentar:

Você verá a mesma imagem sendo exibida em diferentes espaços de cor:

- RGB – O mais comum: Vermelho, Verde e Azul.

- HSV – Matiz (cor), Saturação (intensidade) e Valor (brilho).

- LAB – Baseado em como o olho humano percebe a luz e a cor.

```text
# Convertendo para diferentes espaços de cores
hsv = cv2.cvtColor(imagem, cv2.COLOR_BGR2HSV)
lab = cv2.cvtColor(imagem, cv2.COLOR_BGR2LAB)
```

```text
# Convertendo HSV e LAB para exibição (BGR para RGB)
hsv_display = cv2.cvtColor(hsv, cv2.COLOR_HSV2RGB)
lab_display = cv2.cvtColor(lab, cv2.COLOR_LAB2RGB)
```

# Exibindo os diferentes espaços de cores fig, axs = plt.subplots(1, 3, figsize=(18, 6))

axs[0].imshow(imagem_rgb) axs[0].set_title('RGB (padrão)') axs[0].axis('off')

axs[1].imshow(hsv) axs[1].set_title('HSV (Matiz, Saturação, Valor)') axs[1].axis('off')

axs[2].imshow(lab) axs[2].set_title('LAB (Luminosidade, A, B)') axs[2].axis('off')

```text
plt.tight_layout()
plt.show()
```

<!-- source_page: 61 -->

**Figura 19 - Lena em RGB, HSV e LAB**

Fonte: autoria própria

🎨 Diferentes Espaços de Cores e Suas Aplicações Cada espaço de cor tem suas vantagens específicas, dependendo da tarefa em visão computacional. Veja como usá-los da melhor forma:

- RGB (Vermelho, Verde, Azul): É a representação padrão das imagens digitais, a mais fácil de entender. Usada em visualizações e exibição de imagens.

- HSV (Matiz, Saturação, Valor): Muito útil para segmentar cores específicas e rastrear objetos, já que separa a cor da intensidade da luz.

- LAB (Luminância, A, B): Desenvolvido para ser mais próximo da percepção humana de cores. É ótimo para comparar cores com mais precisão.

k) 🧰 Manipulação Básica de Imagens

Agora que você já entende como as imagens funcionam, vamos aprender a carregá-las utilizando diferentes bibliotecas, redimensioná-las e transformá-las — passos essenciais em qualquer projeto de visão computacional!

l) 🖼️ Carregando e Exibindo Imagens de Diferentes Formas

Existem várias bibliotecas no Python que permitem trabalhar com imagens. Vamos conhecer duas bem populares:

<!-- source_page: 62 -->

m) 1⃣ Usando PIL (Python Imaging Library) / Pillow

Pillow é uma biblioteca simples e poderosa para abrir, editar e salvar imagens. Ela é especialmente útil para:

- Carregar imagens de forma rápida;

- Redimensionar e recortar imagens;

- Aplicar efeitos e filtros simples.

from PIL import Image

```text
# Carregando a imagem com PIL
imagem_pil = Image.open('exemplo.jpg')
```

```text
# Exibindo a imagem
plt.figure(figsize=(8, 6))
plt.imshow(imagem_pil)
plt.title('Imagem carregada com PIL/Pillow')
plt.axis('off')
plt.show()
```

**Figura 20 - Imagem carregada com PIL/Pillow - Lena**

Fonte: autoria própria

<!-- source_page: 63 -->

```text
# Verificando o formato e modo da imagem
print(f"Tamanho da imagem PIL: {imagem_pil.size}")
print(f"Modo de cor: {imagem_pil.mode}")
```

Tamanho da imagem PIL: (512, 512) Modo de cor: RGB

print(imagem_pil.shape)

--------------------------------------------------------------------------- AttributeError Traceback (most recent call last) <ipython-input-36-bbfe9d9f434b> in <cell line: 0>() ----> 1 print(imagem_pil.shape)

/usr/local/lib/python3.11/dist-packages/PIL/JpegImagePlugin.py in __getattr__(self, name) 398 deprecate(name, 12) 399 return getattr(self, "_" + name) --> 400 raise AttributeError(name) 401 402 def __getstate__(self) -> list[Any]:

AttributeError: shape

```text
imagem_array = np.array(imagem_pil)
print(f"Formato do array numpy: {imagem_array.shape}")
```

Formato do array numpy: (512, 512, 3)

n) 2⃣ Usando Matplotlib Diretamente

```text
# Matplotlib pode carregar imagens diretamente
imagem_plt = plt.imread('exemplo.jpg')
```

<!-- source_page: 64 -->

```text
plt.figure(figsize=(8, 6))
plt.imshow(imagem_plt)
plt.title('Imagem carregada diretamente com Matplotlib')
plt.axis('off')
plt.show()
```

**Figura 21 - Imagem carregada com Matplotlib - Lena**

Fonte: autoria própria

print(f"Formato da imagem matplotlib: {imagem_plt.shape}")

Formato da imagem matplotlib: (512, 512, 3)

o) 📏 Redimensionamento de Imagens

O redimensionamento é uma das operações mais comuns e importantes em visão computacional.

p) 🤔 O que significa redimensionar?

Redimensionar uma imagem é alterar seu tamanho — diminuindo ou aumentando sua largura e altura (em pixels).

<!-- source_page: 65 -->

q) 🧠 Por que isso é importante?

- 🔍 Padronizar imagens antes de treinar um modelo de IA;

- 🚀 Acelerar o processamento, reduzindo o número de pixels;

- 🎯 Ajustar a imagem ao tamanho esperado por um algoritmo ou interface. 💡 Dica: Sempre que for comparar ou usar várias imagens juntas (por exemplo, em um modelo de reconhecimento), elas precisam ter o mesmo tamanho.

```text
# Vamos usar a imagem carregada com OpenCV
original = cv2.imread('exemplo.jpg')
original_rgb = cv2.cvtColor(original, cv2.COLOR_BGR2RGB)
```

```text
# 1. Redimensionamento com OpenCV
altura, largura = original.shape[:2]
print(f"Dimensões originais: {largura}x{altura}")
```

Dimensões originais: 512x512

```text
# Redimensionar para metade do tamanho
metade = cv2.resize(original_rgb, (largura//2, altura//2))
```

```text
# Redimensionar para o dobro do tamanho
dobro = cv2.resize(original_rgb, (largura*2, altura*2))
```

```text
# Redimensionar mantendo a proporção
proporcao = 0.3 # 30% do tamanho original
nova_largura = int(largura * proporcao)
nova_altura = int(altura * proporcao)
proporcional = cv2.resize(original_rgb, (nova_largura, nova_altura))
```

# Exibindo as imagens redimensionadas fig, axs = plt.subplots(2, 2, figsize=(15, 10))

axs[0, 0].imshow(original_rgb) axs[0, 0].set_title(f'Original ({largura}x{altura})') axs[0, 0].axis('off') continua continua

<!-- source_page: 66 -->

axs[0, 1].imshow(metade) axs[0, 1].set_title(f'Metade do tamanho ({largura//2}x{altura//2})') axs[0, 1].axis('off')

axs[1, 0].imshow(dobro) axs[1, 0].set_title(f'Dobro do tamanho ({largura*2}x{altura*2})') axs[1, 0].axis('off')

axs[1, 1].imshow(proporcional) axs[1, 1].set_title(f'Proporcional ({nova_largura}x{nova_altura})') axs[1, 1].axis('off')

```text
plt.tight_layout()
plt.show()
```

**Figura 22 - Redimensionamentos de Imagem - Lena**

Fonte: autoria própria

<!-- source_page: 67 -->

r) 🔄 Métodos de Interpolação no Redimensionamento

Quando redimensionamos uma imagem, o computador precisa "preencher" ou "adivinhar" os novos pixels com base nos que já existem. Isso é feito por meio de métodos de interpolação.

```text
# Vamos ver a diferença entre métodos de interpolação
# Redimensionando para um tamanho muito pequeno e depois aumentando
tiny = cv2.resize(original_rgb, (50, 50))
```

```text
plt.figure(figsize=(8, 6))
plt.imshow(tiny)
plt.title('Imagem muito reduzida')
plt.axis('off')
plt.show()
```

**Figura 23 - Imagem muito reduzida - Lena**

Fonte: autoria própria

print(largura, altura)

512 512

<!-- source_page: 68 -->

```text
# Aumentando de volta com diferentes métodos de interpolação
nearest = cv2.resize(tiny, (largura, altura), interpolation=cv2.INTER_NEAREST)
linear = cv2.resize(tiny, (largura, altura), interpolation=cv2.INTER_LINEAR)
cubic = cv2.resize(tiny, (largura, altura), interpolation=cv2.INTER_CUBIC)
```

# Exibindo os resultados fig, axs = plt.subplots(2, 2, figsize=(15, 10))

axs[0, 0].imshow(original_rgb) axs[0, 0].set_title('Original') axs[0, 0].axis('off')

axs[0, 1].imshow(nearest) axs[0, 1].set_title('Interpolação Nearest Neighbor (mais pixelado)') axs[0, 1].axis('off')

axs[1, 0].imshow(linear) axs[1, 0].set_title('Interpolação Linear (padrão)') axs[1, 0].axis('off')

axs[1, 1].imshow(cubic) axs[1, 1].set_title('Interpolação Cúbica (mais suave)') axs[1, 1].axis('off')

```text
plt.tight_layout()
plt.show()
```

<!-- source_page: 69 -->

**Figura 24 - Interpolações de Imagens - Lena**

Fonte: autoria própria

🧪 Principais Métodos:

- 🔹 Nearest Neighbor (Vizinho mais próximo):

- Mais simples e mais rápido;

- Pode deixar a imagem pixelada ou com "degraus".

- 🔸 Interpolação Linear:

- Faz uma média simples entre os pixels vizinhos;

- Oferece um bom equilíbrio entre qualidade e desempenho.

- 🔶 Interpolação Cúbica:

- Usa mais vizinhos e cálculos mais complexos;

- Produz a melhor qualidade, mas é mais lenta.

<!-- source_page: 70 -->

⚠️ Nota importante: Redimensionar uma imagem para um tamanho muito pequeno e depois aumentá-la novamente sempre causará perda de qualidade! O processo descarta informações que não podem ser recuperadas.

s) 🔁 Efeitos de Espelhamento e Rotação

Além de redimensionar, também podemos girar ou espelhar imagens. Essas transformações são chamadas de transformações geométricas e são muito úteis em várias situações.

t) ✨ Exemplos de uso:

- Aumentar a variedade de imagens em um dataset (técnica chamada data augmentation);

- Corrigir a orientação de uma imagem;

- Criar efeitos visuais ou simular movimento. 🧠 Dica: Espelhar uma imagem é como olhar no espelho — a direita vira esquerda, e vice-versa. Rotacionar pode ser útil quando as imagens não estão na posição correta para análise.

img = original_rgb.copy()

--------------------------------------------------------------------------- NameError Traceback (most recent call last) <ipython-input-1-cc5977bfc110> in <cell line: 0>() ----> 1 img = original_rgb.copy()

NameError: name 'original_rgb' is not defined

```text
# Espelhamento horizontal
espelho_h = cv2.flip(img, 1) # 1 = flip horizontal
```

```text
# Espelhamento vertical
espelho_v = cv2.flip(img, 0) # 0 = flip vertical
```

continua continua

<!-- source_page: 71 -->

```text
# Espelhamento em ambos os eixos
espelho_ambos = cv2.flip(img, -1) # -1 = flip nos dois eixos
```

```text
# Rotação de 45 graus
# Primeiro, definimos o centro da rotação e a matriz de rotação
centro = (largura // 2, altura // 2)
matriz_rotacao = cv2.getRotationMatrix2D(centro, 45, 1.0)
rotacao_45 = cv2.warpAffine(img, matriz_rotacao, (largura, altura))
```

# Exibindo os resultados fig, axs = plt.subplots(2, 3, figsize=(15, 10))

axs[0, 0].imshow(img) axs[0, 0].set_title('Original') axs[0, 0].axis('off')

axs[0, 1].imshow(espelho_h) axs[0, 1].set_title('Espelhamento Horizontal') axs[0, 1].axis('off')

axs[0, 2].imshow(espelho_v) axs[0, 2].set_title('Espelhamento Vertical') axs[0, 2].axis('off')

axs[1, 0].imshow(espelho_ambos) axs[1, 0].set_title('Espelhamento Ambos Eixos') axs[1, 0].axis('off')

axs[1, 1].imshow(rotacao_45) axs[1, 1].set_title('Rotação 45 Graus') axs[1, 1].axis('off')

# Deixamos um quadro vazio axs[1, 2].axis('off')

```text
plt.tight_layout()
plt.show()
```

<!-- source_page: 72 -->

**Figura 25 - Espelhamento e Rotação de Imagens - Lena**

Fonte: autoria própria

u) 🧪 Aplicando Filtros

🎯 O que são filtros? Filtros são operações aplicadas sobre os pixels da imagem para destacar informações importantes ou remover partes indesejadas (como ruídos ou detalhes irrelevantes).

🧠 Para que servem?

- 🧹 Remover ruídos (pontos ou manchas indesejadas);

- 🔍 Realçar bordas e contornos importantes;

- 💡 Suavizar ou intensificar certas regiões. Vamos começar com alguns dos filtros mais básicos e úteis — e ver como eles transformam a imagem de formas surpreendentes! 🎨🔬

<!-- source_page: 73 -->

**Figura 26 - 2D Convolution Animation**

Kernel (image processing)

🧹 Filtros Básicos Os filtros são ferramentas fundamentais no processamento de imagens. Eles nos ajudam a:

- Suavizar imagens (remover ruídos);

- Destacar detalhes importantes;

- Separar objetos do fundo. Vamos conhecer os principais:

v) 🌫️ Filtro de Suavização (Blur)

Este filtro "borram" a imagem levemente, o que ajuda a reduzir ruídos (pequenos pontos ou falhas). ✅Ideal quando queremos "limpar" a imagem antes de aplicar outras técnicas, como detecção de bordas. Caracteristicas:

- Reduzem o ruído visual da imagem (pontos aleatórios que atrapalham);

- Deixam a imagem mais suave, mas menos nítida;

- Muito usados como pré-processamento antes de aplicar outras técnicas (como detecção de bordas).

<!-- source_page: 74 -->

```text
# Aplicando diferentes filtros de suavização
# 1. Filtro de Média (Average Blur)
blur_media = cv2.blur(img, (5, 5)) # Kernel 5x5
```

```text
# 2. Filtro Gaussiano
blur_gaussiano = cv2.GaussianBlur(img, (5, 5), 0)
```

```text
# 3. Filtro Mediana
blur_mediana = cv2.medianBlur(img, 5)
```

```text
# 4. Filtro Bilateral (preserva bordas)
blur_bilateral = cv2.bilateralFilter(img, 9, 75, 75)
```

# Exibindo os resultados fig, axs = plt.subplots(2, 3, figsize=(18, 12))

axs[0, 0].imshow(img) axs[0, 0].set_title('Original') axs[0, 0].axis('off')

axs[0, 1].imshow(blur_media) axs[0, 1].set_title('Filtro de Média') axs[0, 1].axis('off')

axs[0, 2].imshow(blur_gaussiano) axs[0, 2].set_title('Filtro Gaussiano') axs[0, 2].axis('off')

axs[1, 0].imshow(blur_mediana) axs[1, 0].set_title('Filtro Mediana') axs[1, 0].axis('off')

axs[1, 1].imshow(blur_bilateral) axs[1, 1].set_title('Filtro Bilateral') axs[1, 1].axis('off')

# Deixando um espaço vazio axs[1, 2].axis('off')

```text
plt.tight_layout()
plt.show()
```

<!-- source_page: 75 -->

**Figura 27 - Filtros de Suavização de Imagem - Lena**

Fonte: autoria própria

w) 🔍 Filtro de Nitidez (Sharpen)

link: https://setosa.io/ev/image-kernels/ Ao contrário do blur, esse filtro realça os contornos e detalhes, deixando a imagem mais nítida.

🧪 Técnica: Unsharp Mask

- Primeiro, aplica-se um blur leve na imagem;

- Depois, subtrai-se a versão borrada da imagem original;

- Isso destaca as partes que mudam rapidamente — ou seja, os detalhes e bordas.

```text
# Filtro de nitidez com kernel personalizado
kernel_nitidez = np.array([[-1, -1, -1],
```

[-1, 9, -1], [-1, -1, -1]])

<!-- source_page: 76 -->

```text
# Aplicando o kernel de nitidez
img_nitidez = cv2.filter2D(img, -1, kernel_nitidez)
```

```text
# Também podemos aplicar o filtro Unsharp Mask
# Primeiro borramos a imagem
gaussian = cv2.GaussianBlur(img, (5, 5), 0)
```

```text
# Depois subtraímos da imagem original e somamos de volta
unsharp_mask = cv2.addWeighted(img, 1.5, gaussian, -0.5, 0)
```

# Exibindo os resultados fig, axs = plt.subplots(1, 3, figsize=(18, 6))

axs[0].imshow(img) axs[0].set_title('Original') axs[0].axis('off')

axs[1].imshow(img_nitidez) axs[1].set_title('Filtro de Nitidez') axs[1].axis('off')

axs[2].imshow(unsharp_mask) axs[2].set_title('Unsharp Mask') axs[2].axis('off')

```text
plt.tight_layout()
plt.show()
```

**Figura 28 - Filtragem por Unsharp Mask - Lena**

Fonte: autoria própria

<!-- source_page: 77 -->

- O filtro de nitidez destaca bordas e detalhes.

- Unsharp Mask é uma técnica que primeiro borra a imagem e depois a subtrai da original.

x) 🔳 Detecção de Bordas

Essa técnica encontra os pontos de transição forte na imagem — onde a cor ou brilho muda de forma brusca.

🔍 Muito usada para reconhecimento de formas, detecção de objetos ou para transformar uma imagem em um esboço.

Alguns algoritmos famosos: Sobel, Canny.

```text
# Primeiro vamos converter a imagem para escala de cinza
img_cinza = cv2.cvtColor(img, cv2.COLOR_RGB2GRAY)
```

```text
# 1. Detector de bordas Sobel
sobelx = cv2.Sobel(img_cinza, cv2.CV_64F, 1, 0, ksize=3) # Bordas horizontais
sobely = cv2.Sobel(img_cinza, cv2.CV_64F, 0, 1, ksize=3) # Bordas verticais
sobel_combinado = cv2.magnitude(sobelx, sobely) # Magnitude do gradiente
```

```text
# Normalizando para exibição
sobelx = cv2.normalize(sobelx, None, 0, 255, cv2.NORM_MINMAX)
sobely = cv2.normalize(sobely, None, 0, 255, cv2.NORM_MINMAX)
sobel_combinado = cv2.normalize(sobel_combinado, None, 0, 255, cv2.NORM_MINMAX)
```

```text
plt.figure(figsize=(8, 6))
plt.imshow(sobel_combinado)
plt.title('Detector de bordas Sobel')
plt.axis('off')
plt.show()
```

<!-- source_page: 78 -->

**Figura 29 - Detector de Bordas Sobel - Lena**

Fonte: autoria própria

```text
# 2. Detector de bordas Canny
canny = cv2.Canny(img_cinza, 100, 200) # Parâmetros: limiar mínimo e máximo
```

```text
plt.figure(figsize=(8, 6))
plt.imshow(canny)
plt.title('Detector de bordas Canny')
plt.axis('off')
plt.show()
```

**Figura 30 - Detector de Bordas Canny - Lena**

Fonte: autoria própria

<!-- source_page: 79 -->

```text
# 3. Detector de bordas Laplaciano
laplaciano = cv2.Laplacian(img_cinza, cv2.CV_64F)
laplaciano = np.uint8(np.absolute(laplaciano))
```

```text
plt.figure(figsize=(8, 6))
plt.imshow(laplaciano)
plt.title('Detector de bordas Laplaciano')
plt.axis('off')
plt.show()
```

**Figura 31 - Detector de Bordas Laplaciano - Lena**

Fonte: autoria própria

# Exibindo os resultados fig, axs = plt.subplots(2, 3, figsize=(18, 12))

axs[0, 0].imshow(img_cinza, cmap='gray') axs[0, 0].set_title('Original em Escala de Cinza') axs[0, 0].axis('off')

axs[0, 1].imshow(sobelx, cmap='gray') axs[0, 1].set_title('Sobel X (Bordas Horizontais)') axs[0, 1].axis('off')

axs[0, 2].imshow(sobely, cmap='gray') axs[0, 2].set_title('Sobel Y (Bordas Verticais)') axs[0, 2].axis('off')

axs[1, 0].imshow(sobel_combinado, cmap='gray') axs[1, 0].set_title('Sobel Combinado') continua continua

<!-- source_page: 80 -->

axs[1, 0].axis('off')

axs[1, 1].imshow(canny, cmap='gray') axs[1, 1].set_title('Canny') axs[1, 1].axis('off')

axs[1, 2].imshow(laplaciano, cmap='gray') axs[1, 2].set_title('Laplaciano') axs[1, 2].axis('off')

```text
plt.tight_layout()
plt.show()
```

**Figura 32 - Detector de Bordas Laplaciano - Lena**

Fonte: autoria própria

y) 🚪 Threshold (Limiarização)

A limiarização é usada para transformar uma imagem em preto e branco (binária), separando o que é objeto e o que é fundo.

- Se o pixel for mais claro que o valor limite (threshold), fica branco;

- Se for mais escuro, fica preto.

<!-- source_page: 81 -->

🎯 Simples e poderoso para segmentar imagens, por exemplo, separar texto de fundo em documentos digitalizados.

# 1. Threshold Binário _, thresh_binario = cv2.threshold(img_cinza, 127, 255, cv2.THRESH_BINARY)

```text
plt.figure(figsize=(4, 3))
plt.imshow(thresh_binario)
plt.title('thresh_binario')
plt.axis('off')
plt.show()
```

**Figura 33 - Threshold Binário de Imagem - Lena**

Fonte: autoria própria

# 2. Threshold Binário Invertido _, thresh_binario_inv = cv2.threshold(img_cinza, 127, 255, cv2.THRESH_BINARY_ INV)

```text
plt.figure(figsize=(4, 3))
plt.imshow(thresh_binario_inv)
plt.title('thresh_binario_inv')
plt.axis('off')
plt.show()
```

<!-- source_page: 82 -->

**Figura 34 - Threshold Binário Invertido de Imagem - Lena**

Fonte: autoria própria

```text
# 3. Threshold Adaptativo
thresh_adapt = cv2.adaptiveThreshold(img_cinza, 255, cv2.ADAPTIVE_THRESH_
GAUSSIAN_C,
cv2.THRESH_BINARY, 11, 2)
```

```text
plt.figure(figsize=(4, 3))
plt.imshow(thresh_adapt)
plt.title('thresh_adapt')
plt.axis('off')
plt.show()
```

**Figura 35 - Threshold Adaptativo de Imagem - Lena**

Fonte: autoria própria

# 4. Threshold Otsu _, thresh_otsu = cv2.threshold(img_cinza, 0, 255, cv2.THRESH_BINARY + cv2. THRESH_OTSU)

<!-- source_page: 83 -->

```text
plt.figure(figsize=(4, 3))
plt.imshow(thresh_otsu)
plt.title('thresh_otsu')
plt.axis('off')
plt.show()
```

**Figura 36 - Threshold Otsu de Imagem - Lena**

Fonte: autoria própria

# Exibindo os resultados fig, axs = plt.subplots(2, 3, figsize=(18, 12))

axs[0, 0].imshow(img_cinza, cmap='gray') axs[0, 0].set_title('Original em Escala de Cinza') axs[0, 0].axis('off')

axs[0, 1].imshow(thresh_binario, cmap='gray') axs[0, 1].set_title('Threshold Binário (127)') axs[0, 1].axis('off')

axs[0, 2].imshow(thresh_binario_inv, cmap='gray') axs[0, 2].set_title('Threshold Binário Invertido') axs[0, 2].axis('off')

axs[1, 0].imshow(thresh_adapt, cmap='gray') axs[1, 0].set_title('Threshold Adaptativo') axs[1, 0].axis('off')

axs[1, 1].imshow(thresh_otsu, cmap='gray') axs[1, 1].set_title('Threshold Otsu') axs[1, 1].axis('off') # Deixando um espaço vazio axs[1, 2].axis('off')

```text
plt.tight_layout()
plt.show()
```

<!-- source_page: 84 -->

**Figura 37 - Threshold em Escala de Cinza - Lena**

Fonte: autoria própria

z) 🧪 Exercício Prático: Combinando Técnicas

Agora é hora de colocar em prática o que aprendemos! 🙌 Neste exercício, vamos combinar várias técnicas de visão computacional para criar um efeito visual interessante chamado:

🎨"Detecção de Contornos Coloridos"

aa) 🔧 O que vamos usar:

- Conversão para escala de cinza: para simplificar a imagem;

- Filtro de suavização (blur): para reduzir ruídos;

- Detecção de bordas (Canny): para encontrar os contornos;

- Cor original da imagem: para aplicar os contornos sobre ela, criando um efeito colorido.

<!-- source_page: 85 -->

🤔 Por que isso é legal?

- É uma forma criativa de visualizar apenas as bordas, sem perder o estilo original da imagem;

- Mostra como várias técnicas simples, quando combinadas, criam algo mais poderoso;

- Nos ajuda a entender como os processos se conectam em visão computacional.

```text
# Carregando a imagem original
img = cv2.imread('exemplo.jpg')
img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
```

```text
# Passo 1: Converter para escala de cinza
img_gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
```

```text
# Passo 2: Aplicar blur para reduzir ruído
img_blur = cv2.GaussianBlur(img_gray, (5, 5), 0)
```

```text
# Passo 3: Detectar bordas com Canny
bordas = cv2.Canny(img_blur, 50, 150)
```

```text
# Passo 4: Dilatar as bordas para torná-las mais visíveis
kernel = np.ones((2, 2), np.uint8)
bordas_dilatadas = cv2.dilate(bordas, kernel, iterations=1)
```

```text
# Passo 5: Inverter as bordas para usar como máscara
mascara = cv2.bitwise_not(bordas_dilatadas)
```

```text
# Passo 6: Aplicar efeito de aquarela à imagem original
img_aquarela = cv2.stylization(img, sigma_s=60, sigma_r=0.6)
img_aquarela_rgb = cv2.cvtColor(img_aquarela, cv2.COLOR_BGR2RGB)
```

```text
# Passo 7: Converter a máscara para 3 canais para aplicar à imagem colorida
mascara_3_canais = cv2.cvtColor(mascara, cv2.COLOR_GRAY2BGR)
```

```text
# Passo 8: Aplicar a máscara à imagem com efeito de aquarela
resultado = cv2.bitwise_and(img_aquarela, mascara_3_canais)
resultado_rgb = cv2.cvtColor(resultado, cv2.COLOR_BGR2RGB)
```

```text
# Passo 9: Adicionar as bordas de volta à imagem resultante
# Primeiro, convertemos as bordas dilatadas para 3 canais
bordas_3_canais = cv2.cvtColor(bordas_dilatadas, cv2.COLOR_GRAY2BGR)
# Depois, somamos com a imagem original
img_final = cv2.add(resultado, bordas_3_canais)
img_final_rgb = cv2.cvtColor(img_final, cv2.COLOR_BGR2RGB)
```

<!-- source_page: 86 -->

# Exibindo os resultados fig, axs = plt.subplots(2, 2, figsize=(15, 10))

axs[0, 0].imshow(img_rgb) axs[0, 0].set_title('Imagem Original') axs[0, 0].axis('off')

axs[0, 1].imshow(bordas, cmap='gray') axs[0, 1].set_title('Bordas Detectadas') axs[0, 1].axis('off')

axs[1, 0].imshow(img_aquarela_rgb) axs[1, 0].set_title('Efeito Aquarela') axs[1, 0].axis('off')

axs[1, 1].imshow(img_final_rgb) axs[1, 1].set_title('Resultado Final') axs[1, 1].axis('off')

```text
plt.tight_layout()
plt.show()
```

**Figura 38 - Detecção de Contornos Coloridos - Lena**

Fonte: autoria própria

<!-- source_page: 87 -->

ab) 🔍 Reconhecimento Simples com Imagens

Chegamos a uma das partes mais empolgantes da visão computacional: o reconhecimento de padrões! Neste módulo, vamos aprender como um computador pode identificar o conteúdo de uma imagem, mesmo quando ela foi desenhada à mão.

🧠 O que é reconhecimento de padrões? É a capacidade de um sistema computacional reconhecer formas, objetos ou símbolos, mesmo com pequenas variações. É isso que permite, por exemplo:

- Seu celular desbloquear com reconhecimento facial;

- Um robô ler placas de trânsito;

- Um app identificar o número que você escreveu com o dedo na tela.

ac) 🔢 Introdução ao Dataset MNIST

Para esse experimento, vamos usar um conjunto de dados muito famoso chamado MNIST.

📚 O que é o MNIST?

- MNIST significa Modified National Institute of Standards and Technology;

- É um dataset com 70.000 imagens de dígitos escritos à mão(de 0 a 9); - 
60.000 imagens são usadas para treinar o modelo; - 
10.000 são usadas para testar se ele aprendeu corretamente;

- Cada imagem é pequena: tem apenas 28x28 pixels e está em escala de cinza.

ad) 🤔 Por que ele é importante?

- É considerado o "Olá, Mundo" do reconhecimento de imagens;

- É simples, mas poderoso para entender como os algoritmos aprendem;

- É usado em muitos cursos e projetos iniciais de inteligência artificial.

<!-- source_page: 88 -->

💡 Curiosidade: Mesmo com tamanho pequeno, essas imagens ensinam o computador a diferenciar milhares de variações do número 7, por exemplo — algo fácil para humanos, mas desafiador para máquinas!

```text
# Importando as bibliotecas
import tensorflow as tf
from tensorflow.keras.datasets import mnist
from sklearn.neighbors import KNeighborsClassifier
import time
```

# Carregando o dataset MNIST (X_train, y_train), (X_test, y_test) = mnist.load_data()

Downloading data from https://storage.googleapis.com/tensorflow/tf-kerasdatasets/mnist.npz 11490434/11490434 ━━━━━━━━━━━━━━━━━━━━ 1s 0us/step

```text
print(f"Formato dos dados de treinamento: {X_train.shape}")
print(f"Formato dos rótulos de treinamento: {y_train.shape}")
print(f"Formato dos dados de teste: {X_test.shape}")
print(f"Formato dos rótulos de teste: {y_test.shape}")
```

Formato dos dados de treinamento: (60000, 28, 28) Formato dos rótulos de treinamento: (60000,) Formato dos dados de teste: (10000, 28, 28) Formato dos rótulos de teste: (10000,)

ae) 👀 Visualizando Exemplos do MNIST

Antes de usarmos o MNIST para treinar um modelo, vamos dar uma olhada nas imagens para entender melhor o tipo de dado com que estamos trabalhando.

<!-- source_page: 89 -->

🧠 O que vamos observar? Cada imagem do MNIST é:

- 📏 Pequena: apenas 28x28 pixels;

- ⚫ Em escala de cinza (sem cores);

- ✍️ Um dígito (0 a 9) escrito à mão por diferentes pessoas.

🎯 Por que visualizar os dados?

- Nos ajuda a entender o desafio: os números têm estilos diferentes, tamanhos variados e até traços tortos;

- É uma etapa importante do processo chamado exploração dos dados;

- Facilita perceber por que um modelo precisa aprender padrões, e não apenas memorizar imagens.

💬 Dica: Ao treinar modelos de IA, ver os dados com os próprios olhos é tão importante quanto escrever o código.

```text
plt.figure(figsize=(10, 5))
for i in range(10):
plt.subplot(2, 5, i+1)
plt.imshow(X_train[i], cmap='gray')
plt.title(f"Dígito: {y_train[i]}")
plt.axis('off')
plt.tight_layout()
plt.show()
```

<!-- source_page: 90 -->

**Figura 39 - Amostra 1 do dataset MNIST**

Fonte: dataset Modified National Institute of Standards and Technology

```text
# Vamos ver a distribuição dos dígitos no conjunto de treinamento
unique, counts = np.unique(y_train, return_counts=True)
plt.figure(figsize=(10, 4))
plt.bar(unique, counts)
plt.xlabel('Dígito')
plt.ylabel('Quantidade')
plt.title('Distribuição dos dígitos no conjunto de treinamento')
plt.xticks(unique)
plt.grid(axis='y', alpha=0.75)
plt.show()
```

**Figura 40 - Distribuição dos Dígitos no Conjunto**

Fonte: autoria própria

<!-- source_page: 91 -->

print(f"Total de exemplos para cada dígito:\n{dict(zip(unique, counts))}")

Total de exemplos para cada dígito: {np.uint8(0): np.int64(5923), np.uint8(1): np.int64(6742), np.uint8(2): np.int64(5958), np.uint8(3): np.int64(6131), np.uint8(4): np.int64(5842), np.uint8(5): np.int64(5421), np.uint8(6): np.int64(5918), np.uint8(7): np.int64(6265), np.uint8(8): np.int64(5851), np.uint8(9): np.int64(5949)}

af) 🧼 Pré-processamento das Imagens

Antes de ensinar um modelo a reconhecer números, precisamos preparar as imagens para que ele consiga entender os dados corretamente. Esse processo é chamado de pré-processamento.

🤔 Por que precisamos pré-processar? Imagine tentar resolver uma prova com a folha toda amassada ou manchada — seria mais difícil, certo? O mesmo vale para os computadores. Se os dados estiverem "bagunçados", o modelo não aprende bem.

ag) 🧰 Etapas comuns de pré-processamento:

1. Normalização dos pixels

- Os valores dos pixels vão de 0 a 255 (quanto mais alto, mais claro).

- A gente divide tudo por 255 para que os valores fiquem entre 0 e 1. Isso ajuda o modelo a aprender mais rápido e com mais precisão.

2. Ajuste de formato (reshape)

- O modelo espera as imagens com um formato específico.

- Precisamos garantir que cada imagem tenha o formato certo (por exemplo, 28x28 pixels com 1 canal de cor).

📌 Resumo: Pré-processar é como organizar a mesa antes de estudar: limpa, clara e pronta para receber o conteúdo!

<!-- source_page: 92 -->

```text
# 1. Normalização: converter valores de pixels de 0-255 para 0-1
X_train_norm = X_train / 255.0
X_test_norm = X_test / 255.0
```

```text
# 2. Reshape: transformar imagens 28x28 em vetores 784-dimensionais
X_train_flat = X_train_norm.reshape(X_train.shape[0], -1)
X_test_flat = X_test_norm.reshape(X_test.shape[0], -1)
```

```text
print(f"Formato dos dados de treinamento após reshape: {X_train_flat.shape}")
print(f"Formato dos dados de teste após reshape: {X_test_flat.shape}")
```

Formato dos dados de treinamento após reshape: (60000, 784) Formato dos dados de teste após reshape: (10000, 784)

ah) 🧠 Treinando um Modelo Simples: k-Nearest Neighbors (k-NN)

Agora que nossos dados estão prontos, vamos treinar um modelo de classificação para reconhecer os dígitos escritos à mão. Vamos usar um dos algoritmos mais simples e fáceis de entender: o k-Nearest Neighbors (k-NN). 👥 O que é o k-NN? Imagine que você encontra um número novo e quer saber qual é. O que você faz? Compara com outros números que já conhece! O k-NN faz algo parecido:

1. Ele procura os "k" exemplos mais parecidos com a imagem nova (por isso "k vizinhos mais próximos");

2. Depois, vê qual número aparece com mais frequência entre esses vizinhos;

3. E então classifica a imagem como esse número.

<!-- source_page: 93 -->

🎯 Por que usar o k-NN?

- É simples de entender;

- Funciona bem em muitos casos iniciais;

- Não precisa de um processo de treinamento muito complexo.

ai) ⚠️ Limitações:

- Pode ser lento quando há muitos dados, pois precisa comparar cada nova imagem com todas as outras;

- Nem sempre é o melhor modelo para grandes projetos, mas é ótimo para aprender os conceitos básicos.

💡 Dica: O k-NN é como pedir a opinião de amigos próximos — se a maioria acha que o número é um 3, você confia neles! 😉

```text
# Criando e treinando o modelo k-NN
start_time = time.time()
knn = KNeighborsClassifier(n_neighbors=5)
knn.fit(X_train_flat, y_train)
end_time = time.time()
```

print(f"Tempo de treinamento: {end_time - start_time:.2f} segundos")

Tempo de treinamento: 0.06 segundos

aj) 📈 Avaliando o Modelo

Depois de treinar nosso modelo com os dados de treino, é hora de descobrir se ele aprendeu bem. Para isso, vamos usar o conjunto de teste — um grupo de imagens que o modelo nunca viu antes.

🎯 Por que avaliar? Avaliar o modelo é como fazer uma prova final: queremos ver se ele realmente aprendeu a reconhecer os números, ou se apenas memorizou os exemplos do treino.

<!-- source_page: 94 -->

🔍 O que vamos medir? A principal medida que usaremos é a acurácia:

- Acurácia = Quantas previsões o modelo acertou / Total de previsões

- Se a acurácia for alta, significa que o modelo está classificando corretamente a maioria dos dígitos.

🧠 Importância da avaliação:

- Evita que a gente confie num modelo que só decorou os exemplos;

- Mostra se o modelo está generalizando bem para novos dados;

- Ajuda a decidir se precisamos melhorar o modelo ou ajustar os dados.

💡 Dica: Avaliar um modelo é como testar um aluno com perguntas novas. Se ele entendeu o conteúdo, vai se sair bem mesmo com questões diferentes!

```text
# Fazendo previsões
start_time = time.time()
y_pred = knn.predict(X_test_flat)
end_time = time.time()
```

print(f"Tempo para prever as amostras: {end_time - start_time:.2f} segundos")

Tempo para prever as amostras: 41.34 segundos

```text
# Calculando a acurácia
accuracy = (y_pred == y_test).mean()
print(f'Acurácia do modelo: {accuracy * 100:.2f}%')
```

Acurácia do modelo: 96.88%

# Visualizando algumas previsões fig, axs = plt.subplots(5, 2, figsize=(10, 12)) for i, ax in enumerate(axs.flat): # Escolhendo aleatoriamente um exemplo do conjunto de teste continua continua

<!-- source_page: 95 -->

```text
idx = np.random.randint(0, len(X_test))
# Exibindo a imagem
ax.imshow(X_test[idx], cmap='gray')
# Adicionando a previsão ao título
ax.set_title(f'Predição: {y_pred[idx]}, Real: {y_test[idx]}')
ax.axis('off')
plt.tight_layout()
plt.show()
```

**Figura 41 - Amostra 2 do dataset MNIST**

Fonte: dataset Modified National Institute of Standards and Technology

<!-- source_page: 96 -->

ak) 🤖 Explorando Redes Neurais Convolucionais (CNNs)

Agora que vimos como funciona um modelo simples (k-NN), vamos conhecer uma ferramenta mais poderosa: as Redes Neurais Convolucionais, ou CNNs. Elas são muito usadas em visão computacional — e por um bom motivo: funcionam muito bem com imagens! 📸💡

al) 🧱 Entendendo a Arquitetura de uma CNN

Vamos ver, passo a passo, como uma CNN "olha" para uma imagem:

1. 🔍 Conv2D — Camada de Convolução

- Aplica filtros 3x3 sobre a imagem para detectar padrões locais (como bordas e curvas);

- Funciona como uma "lente" que destaca diferentes partes da imagem.

2. ◻ MaxPooling2D — Camada de Redução

- Diminui o tamanho da imagem mantendo os detalhes mais importantes;

- Ajuda a tornar o modelo mais rápido e eficiente, sem perder informação útil.

3. 📏 Flatten — Camada de Achatamento

- Transforma os mapas de ativação (a imagem processada) em uma linha de números;

- Prepara os dados para as camadas finais de decisão.

4. 🧮 Dense— Camada Densa

- São neurônios totalmente conectados que combinam tudo o que foi aprendido até agora;

- É onde o "raciocínio final" do modelo acontece.

<!-- source_page: 97 -->

5. 🎯 Softmax — Saída Final

- Gera uma probabilidade para cada número (0 a 9);

- O número com maior probabilidade é escolhido como a resposta final do modelo.

💡 Resumo: As CNNs veem uma imagem em camadas: primeiro enxergam os detalhes, depois agrupam informações e, por fim, decidem o que a imagem representa!

import tensorflow as tf

```text
from tensorflow.keras.utils import plot_model
from IPython.display import Image, display
```

```text
# Para CNN: converte para shape (N,28,28,1)
X_train_cnn = X_train_norm.reshape(-1, 28, 28, 1)
X_test_cnn = X_test_norm.reshape(-1, 28, 28, 1)
```

```text
modelo_cnn = tf.keras.Sequential([
# 1ª camada convolucional: 32 filtros 3×3 + ReLU
tf.keras.layers.Conv2D(32, (3, 3),
activation='relu',
input_shape=(28, 28, 1)),
# Pooling 2×2 (meta: diminuir dimensão e manter feature dominante)
tf.keras.layers.MaxPooling2D((2, 2)),
```

# 2ª camada convolucional: 64 filtros 3×3 + ReLU tf.keras.layers.Conv2D(64, (3, 3), activation='relu'), tf.keras.layers.MaxPooling2D((2, 2)),

# Achata para vetor tf.keras.layers.Flatten(),

# Camada densa intermediária (64 neurônios) + ReLU tf.keras.layers.Dense(64, activation='relu'),

# Saída: 10 neurônios (uma para cada dígito) + Softmax tf.keras.layers.Dense(10, activation='softmax') ])

<!-- source_page: 98 -->

/usr/local/lib/python3.11/dist-packages/keras/src/layers/convolutional/ base_conv.py:107: UserWarning: Do not pass an `input_shape`/`input_ dim` argument to a layer. When using Sequential models, prefer using an `Input(shape)` object as the first layer in the model instead. super().__init__(activity_regularizer=activity_regularizer, **kwargs)

```text
# Mostrar o resumo do modelo
print("RESUMO DO MODELO")
modelo_cnn.summary()
```

RESUMO DO MODELO Model: "sequential" ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━┓ ┃ Layer (type) ┃ Output Shape ┃ Param # ┃ ┡━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╇━━━━━━━━━━━━━━━━━━━━━━━━╇━━━━━━━━━━━━━━━┩ │ conv2d (Conv2D) │ (None, 26, 26, 32) │ 320 │ ├─────────────────────────────────┼────────────────────────┼───────────────┤ │ max_pooling2d (MaxPooling2D) │ (None, 13, 13, 32) │ 0 │ ├─────────────────────────────────┼────────────────────────┼───────────────┤ │ conv2d_1 (Conv2D) │ (None, 11, 11, 64) │ 18,496 │ ├─────────────────────────────────┼────────────────────────┼───────────────┤ │ max_pooling2d_1 (MaxPooling2D) │ (None, 5, 5, 64) │ 0 │ ├─────────────────────────────────┼────────────────────────┼───────────────┤ │ flatten (Flatten) │ (None, 1600) │ 0 │ ├─────────────────────────────────┼────────────────────────┼───────────────┤ │ dense (Dense) │ (None, 64) │ 102,464 │ ├─────────────────────────────────┼────────────────────────┼───────────────┤ │ dense_1 (Dense) │ (None, 10) │ 650 │ └─────────────────────────────────┴────────────────────────┴───────────────┘ Total params: 121,930 (476.29 KB) Trainable params: 121,930 (476.29 KB) Non-trainable params: 0 (0.00 B)

```text
# Visualizar a arquitetura usando plot_model
print("VISUALIZAÇÃO GRÁFICA DA ARQUITETURA")
```

```text
# Criar o gráfico e salvar em um arquivo
plot_model(modelo_cnn, to_file='modelo_cnn_arquitetura.png',
show_shapes=True,
show_layer_names=True,
show_dtype=True,
dpi=96)
```

VISUALIZAÇÃO GRÁFICA DA ARQUITETURA

<!-- source_page: 99 -->

**Figura 42 - Visualização Gráfica da Arquitetura**

Fonte: autoria própria

```text
modelo_cnn.compile(optimizer='adam',
loss='sparse_categorical_crossentropy',
metrics=['accuracy'])
```

<!-- source_page: 100 -->

modelo_cnn.fit(X_train_cnn, y_train, epochs=1, validation_split=0.2, verbose=1)

1500/1500 ━━━━━━━━━━━━━━━━━━━━ 53s 33ms/step - accuracy: 0.8866 - loss: 0.3627 - val_accuracy: 0.9822 - val_loss: 0.0608 <keras.src.callbacks.history.History at 0x7f80043ad3d0>

```text
# Avaliação no conjunto completo de teste
loss_cnn, acc_cnn = modelo_cnn.evaluate(X_test_cnn, y_test, verbose=0)
print(f"\nAcurácia CNN no conjunto de teste: {acc_cnn*100:.2f}%\n")
```

Acurácia CNN no conjunto de teste: 98.46%

# --- 4) Comparação visual de algumas previsões fig, axs = plt.subplots(5, 3, figsize=(10, 14))

```text
for i in range(5):
idx = np.random.randint(0, len(X_test))
img = X_test_norm[idx]# já normalizada
true = y_test[idx]
```

```text
# Previsão k-NN
img_flat = img.reshape(1, -1)
p_knn = knn.predict(img_flat)[0]
```

```text
# Previsão CNN
img_cnn = img.reshape(1, 28, 28, 1)
p_cnn = np.argmax(modelo_cnn.predict(img_cnn, verbose=0)[0])
```

# Exibe axs[i,0].imshow(img, cmap='gray') axs[i,0].set_title(f"Real: {true}") axs[i,0].axis('off')

axs[i,1].imshow(img, cmap='gray') axs[i,1].set_title(f"k-NN: {p_knn}", color=('green' if p_knn==true else 'red')) axs[i,1].axis('off')

axs[i,2].imshow(img, cmap='gray') axs[i,2].set_title(f"CNN: {p_cnn}", color=('green' if p_cnn==true else 'red')) axs[i,2].axis('off')

continua continua

<!-- source_page: 101 -->

```text
plt.tight_layout()
plt.show()
```

**Figura 43 - Amostra 3 do dataset MNIST**

Fonte: dataset Modified National Institute of Standards and Technology

```text
print(f"- k-NN (não-paramétrico): acurácia = {accuracy*100:.2f}%.")
print(f"- CNN (rede neural convolucional): acurácia = {acc_cnn*100:.2f}%.")
```

- k-NN (não-paramétrico): acurácia = 96.88%. - CNN (rede neural convolucional): acurácia = 98.46%.

<!-- source_page: 102 -->

am) 🎯 YOLO: You Only Look Once

Vamos agora conhecer um dos modelos mais famosos e eficientes para detecção de objetos em tempo real: o YOLO!

👀 O que é o YOLO? YOLO (You Only Look Once) é um modelo de visão computacional que consegue encontrar e identificar objetos em imagens ou vídeos de uma só vez — de forma rápida e precisa! Enquanto outros modelos "olham" para a imagem por partes, o YOLO analisa tudo de uma vez só (por isso o nome).

🧠 O que o YOLO faz?

- Localiza objetos (como carros, pessoas, animais);

- Desenha caixas ao redor deles;

- Diz o que é cada objeto, com um grau de confiança (ex: "pessoa: 92%").

⚙️ Como funciona (simplificando):

1. Divide a imagem em grades (tipo um tabuleiro);

2. Cada grade tenta detectar objetos dentro dela;

3. O modelo retorna:

- As coordenadas da caixa (bounding box);

- A classe do objeto (ex: cachorro, bicicleta);

- A confiança na detecção.

an) 🧪 Onde o YOLO é usado?

- 🚗 Carros autônomos (detecção de pedestres e placas);

- 📷 Câmeras de segurança;

- 🛍️ Monitoramento de estoque em lojas;

- 🏀 Esportes (rastreamento de jogadores e bola).

<!-- source_page: 103 -->

!pip install ultralytics

Collecting ultralytics Downloading ultralytics-8.3.142-py3-none-any.whl.metadata (37 kB) Requirement already satisfied: numpy>=1.23.0 in /usr/local/lib/python3.11/distpackages (from ultralytics) (2.0.2) Requirement already satisfied: matplotlib>=3.3.0 in /usr/local/lib/python3.11/ dist-packages (from ultralytics) (3.10.0) Requirement already satisfied: opencv-python>=4.6.0 in /usr/local/lib/python3.11/ dist-packages (from ultralytics) (4.11.0.86) Requirement already satisfied: pillow>=7.1.2 in /usr/local/lib/python3.11/distpackages (from ultralytics) (11.2.1) Requirement already satisfied: pyyaml>=5.3.1 in /usr/local/lib/python3.11/distpackages (from ultralytics) (6.0.2) Requirement already satisfied: requests>=2.23.0 in /usr/local/lib/python3.11/distpackages (from ultralytics) (2.32.3) Requirement already satisfied: scipy>=1.4.1 in /usr/local/lib/python3.11/distpackages (from ultralytics) (1.15.3) Requirement already satisfied: torch>=1.8.0 in /usr/local/lib/python3.11/distpackages (from ultralytics) (2.6.0+cu124) Requirement already satisfied: torchvision>=0.9.0 in /usr/local/lib/python3.11/ dist-packages (from ultralytics) (0.21.0+cu124) Requirement already satisfied: tqdm>=4.64.0 in /usr/local/lib/python3.11/distpackages (from ultralytics) (4.67.1) Requirement already satisfied: psutil in /usr/local/lib/python3.11/dist-packages (from ultralytics) (5.9.5) Requirement already satisfied: py-cpuinfo in /usr/local/lib/python3.11/distpackages (from ultralytics) (9.0.0) Requirement already satisfied: pandas>=1.1.4 in /usr/local/lib/python3.11/distpackages (from ultralytics) (2.2.2) Collecting ultralytics-thop>=2.0.0 (from ultralytics) Downloading ultralytics_thop-2.0.14-py3-none-any.whl.metadata (9.4 kB) Requirement already satisfied: contourpy>=1.0.1 in /usr/local/lib/python3.11/distpackages (from matplotlib>=3.3.0->ultralytics) (1.3.2) Requirement already satisfied: cycler>=0.10 in /usr/local/lib/python3.11/distpackages (from matplotlib>=3.3.0->ultralytics) (0.12.1) Requirement already satisfied: fonttools>=4.22.0 in /usr/local/lib/python3.11/ dist-packages (from matplotlib>=3.3.0->ultralytics) (4.58.0) Requirement already satisfied: kiwisolver>=1.3.1 in /usr/local/lib/python3.11/ dist-packages (from matplotlib>=3.3.0->ultralytics) (1.4.8) Requirement already satisfied: packaging>=20.0 in /usr/local/lib/python3.11/distpackages (from matplotlib>=3.3.0->ultralytics) (24.2) Requirement already satisfied: pyparsing>=2.3.1 in /usr/local/lib/python3.11/distpackages (from matplotlib>=3.3.0->ultralytics) (3.2.3) Requirement already satisfied: python-dateutil>=2.7 in /usr/local/lib/python3.11/ dist-packages (from matplotlib>=3.3.0->ultralytics) (2.9.0.post0) Requirement already satisfied: pytz>=2020.1 in /usr/local/lib/python3.11/distpackages (from pandas>=1.1.4->ultralytics) (2025.2) Requirement already satisfied: tzdata>=2022.7 in /usr/local/lib/python3.11/distpackages (from pandas>=1.1.4->ultralytics) (2025.2) Requirement already satisfied: charset-normalizer<4,>=2 in /usr/local/lib/ python3.11/dist-packages (from requests>=2.23.0->ultralytics) (3.4.2) Requirement already satisfied: idna<4,>=2.5 in /usr/local/lib/python3.11/distpackages (from requests>=2.23.0->ultralytics) (3.10) Requirement already satisfied: urllib3<3,>=1.21.1 in /usr/local/lib/python3.11/ dist-packages (from requests>=2.23.0->ultralytics) (2.4.0) Requirement already satisfied: certifi>=2017.4.17 in /usr/local/lib/python3.11/ dist-packages (from requests>=2.23.0->ultralytics) (2025.4.26) Requirement already satisfied: filelock in /usr/local/lib/python3.11/dist-packages (from torch>=1.8.0->ultralytics) (3.18.0) Requirement already satisfied: typing-extensions>=4.10.0 in /usr/local/lib/ python3.11/dist-packages (from torch>=1.8.0->ultralytics) (4.13.2) Requirement already satisfied: networkx in /usr/local/lib/python3.11/dist-packages (from torch>=1.8.0->ultralytics) (3.4.2) Requirement already satisfied: jinja2 in /usr/local/lib/python3.11/dist-packages (from torch>=1.8.0->ultralytics) (3.1.6) continua continua

<!-- source_page: 104 -->

Requirement already satisfied: fsspec in /usr/local/lib/python3.11/dist-packages (from torch>=1.8.0->ultralytics) (2025.3.2) Collecting nvidia-cuda-nvrtc-cu12==12.4.127 (from torch>=1.8.0->ultralytics) Downloading nvidia_cuda_nvrtc_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl. metadata (1.5 kB) Collecting nvidia-cuda-runtime-cu12==12.4.127 (from torch>=1.8.0->ultralytics) Downloading nvidia_cuda_runtime_cu12-12.4.127-py3-none-manylinux2014_x86_64. whl.metadata (1.5 kB) Collecting nvidia-cuda-cupti-cu12==12.4.127 (from torch>=1.8.0->ultralytics) Downloading nvidia_cuda_cupti_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl. metadata (1.6 kB) Collecting nvidia-cudnn-cu12==9.1.0.70 (from torch>=1.8.0->ultralytics) Downloading nvidia_cudnn_cu12-9.1.0.70-py3-none-manylinux2014_x86_64.whl. metadata (1.6 kB) Collecting nvidia-cublas-cu12==12.4.5.8 (from torch>=1.8.0->ultralytics) Downloading nvidia_cublas_cu12-12.4.5.8-py3-none-manylinux2014_x86_64.whl. metadata (1.5 kB) Collecting nvidia-cufft-cu12==11.2.1.3 (from torch>=1.8.0->ultralytics) Downloading nvidia_cufft_cu12-11.2.1.3-py3-none-manylinux2014_x86_64.whl. metadata (1.5 kB) Collecting nvidia-curand-cu12==10.3.5.147 (from torch>=1.8.0->ultralytics) Downloading nvidia_curand_cu12-10.3.5.147-py3-none-manylinux2014_x86_64.whl. metadata (1.5 kB) Collecting nvidia-cusolver-cu12==11.6.1.9 (from torch>=1.8.0->ultralytics) Downloading nvidia_cusolver_cu12-11.6.1.9-py3-none-manylinux2014_x86_64.whl. metadata (1.6 kB) Collecting nvidia-cusparse-cu12==12.3.1.170 (from torch>=1.8.0->ultralytics) Downloading nvidia_cusparse_cu12-12.3.1.170-py3-none-manylinux2014_x86_64.whl. metadata (1.6 kB) Requirement already satisfied: nvidia-cusparselt-cu12==0.6.2 in /usr/local/lib/ python3.11/dist-packages (from torch>=1.8.0->ultralytics) (0.6.2) Requirement already satisfied: nvidia-nccl-cu12==2.21.5 in /usr/local/lib/ python3.11/dist-packages (from torch>=1.8.0->ultralytics) (2.21.5) Requirement already satisfied: nvidia-nvtx-cu12==12.4.127 in /usr/local/lib/ python3.11/dist-packages (from torch>=1.8.0->ultralytics) (12.4.127) Collecting nvidia-nvjitlink-cu12==12.4.127 (from torch>=1.8.0->ultralytics) Downloading nvidia_nvjitlink_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl. metadata (1.5 kB) Requirement already satisfied: triton==3.2.0 in /usr/local/lib/python3.11/distpackages (from torch>=1.8.0->ultralytics) (3.2.0) Requirement already satisfied: sympy==1.13.1 in /usr/local/lib/python3.11/distpackages (from torch>=1.8.0->ultralytics) (1.13.1) Requirement already satisfied: mpmath<1.4,>=1.1.0 in /usr/local/lib/python3.11/ dist-packages (from sympy==1.13.1->torch>=1.8.0->ultralytics) (1.3.0) Requirement already satisfied: six>=1.5 in /usr/local/lib/python3.11/dist-packages (from python-dateutil>=2.7->matplotlib>=3.3.0->ultralytics) (1.17.0) Requirement already satisfied: MarkupSafe>=2.0 in /usr/local/lib/python3.11/distpackages (from jinja2->torch>=1.8.0->ultralytics) (3.0.2) Downloading ultralytics-8.3.142-py3-none-any.whl (1.0 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 1.0/1.0 MB 28.2 MB/s eta 0:00:00 Downloading nvidia_cublas_cu12-12.4.5.8-py3-none-manylinux2014_x86_64.whl (363.4 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 363.4/363.4 MB 2.8 MB/s eta 0:00:00 Downloading nvidia_cuda_cupti_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl (13.8 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 13.8/13.8 MB 76.9 MB/s eta 0:00:00 Downloading nvidia_cuda_nvrtc_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl (24.6 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 24.6/24.6 MB 61.6 MB/s eta 0:00:00 Downloading nvidia_cuda_runtime_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl (883 kB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 883.7/883.7 kB 28.1 MB/s eta 0:00:00 Downloading nvidia_cudnn_cu12-9.1.0.70-py3-none-manylinux2014_x86_64.whl (664.8 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 664.8/664.8 MB 3.0 MB/s eta 0:00:00 Downloading nvidia_cufft_cu12-11.2.1.3-py3-none-manylinux2014_x86_64.whl (211.5 MB) continua continua

<!-- source_page: 105 -->

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 211.5/211.5 MB 6.1 MB/s eta 0:00:00 Downloading nvidia_curand_cu12-10.3.5.147-py3-none-manylinux2014_x86_64.whl (56.3 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 56.3/56.3 MB 17.0 MB/s eta 0:00:00 Downloading nvidia_cusolver_cu12-11.6.1.9-py3-none-manylinux2014_x86_64.whl (127.9 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 127.9/127.9 MB 7.7 MB/s eta 0:00:00 Downloading nvidia_cusparse_cu12-12.3.1.170-py3-none-manylinux2014_x86_64.whl (207.5 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 207.5/207.5 MB 6.9 MB/s eta 0:00:00 Downloading nvidia_nvjitlink_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl (21.1 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 21.1/21.1 MB 38.7 MB/s eta 0:00:00 Downloading ultralytics_thop-2.0.14-py3-none-any.whl (26 kB) Installing collected packages: nvidia-nvjitlink-cu12, nvidia-curand-cu12, nvidiacufft-cu12, nvidia-cuda-runtime-cu12, nvidia-cuda-nvrtc-cu12, nvidia-cuda-cupti- cu12, nvidia-cublas-cu12, nvidia-cusparse-cu12, nvidia-cudnn-cu12, nvidiacusolver-cu12, ultralytics-thop, ultralytics Attempting uninstall: nvidia-nvjitlink-cu12 Found existing installation: nvidia-nvjitlink-cu12 12.5.82 Uninstalling nvidia-nvjitlink-cu12-12.5.82: Successfully uninstalled nvidia-nvjitlink-cu12-12.5.82 Attempting uninstall: nvidia-curand-cu12 Found existing installation: nvidia-curand-cu12 10.3.6.82 Uninstalling nvidia-curand-cu12-10.3.6.82: Successfully uninstalled nvidia-curand-cu12-10.3.6.82 Attempting uninstall: nvidia-cufft-cu12 Found existing installation: nvidia-cufft-cu12 11.2.3.61 Uninstalling nvidia-cufft-cu12-11.2.3.61: Successfully uninstalled nvidia-cufft-cu12-11.2.3.61 Attempting uninstall: nvidia-cuda-runtime-cu12 Found existing installation: nvidia-cuda-runtime-cu12 12.5.82 Uninstalling nvidia-cuda-runtime-cu12-12.5.82: Successfully uninstalled nvidia-cuda-runtime-cu12-12.5.82 Attempting uninstall: nvidia-cuda-nvrtc-cu12 Found existing installation: nvidia-cuda-nvrtc-cu12 12.5.82 Uninstalling nvidia-cuda-nvrtc-cu12-12.5.82: Successfully uninstalled nvidia-cuda-nvrtc-cu12-12.5.82 Attempting uninstall: nvidia-cuda-cupti-cu12 Found existing installation: nvidia-cuda-cupti-cu12 12.5.82 Uninstalling nvidia-cuda-cupti-cu12-12.5.82: Successfully uninstalled nvidia-cuda-cupti-cu12-12.5.82 Attempting uninstall: nvidia-cublas-cu12 Found existing installation: nvidia-cublas-cu12 12.5.3.2 Uninstalling nvidia-cublas-cu12-12.5.3.2: Successfully uninstalled nvidia-cublas-cu12-12.5.3.2 Attempting uninstall: nvidia-cusparse-cu12 Found existing installation: nvidia-cusparse-cu12 12.5.1.3 Uninstalling nvidia-cusparse-cu12-12.5.1.3: Successfully uninstalled nvidia-cusparse-cu12-12.5.1.3 Attempting uninstall: nvidia-cudnn-cu12 Found existing installation: nvidia-cudnn-cu12 9.3.0.75 Uninstalling nvidia-cudnn-cu12-9.3.0.75: Successfully uninstalled nvidia-cudnn-cu12-9.3.0.75 Attempting uninstall: nvidia-cusolver-cu12 Found existing installation: nvidia-cusolver-cu12 11.6.3.83 Uninstalling nvidia-cusolver-cu12-11.6.3.83: Successfully uninstalled nvidia-cusolver-cu12-11.6.3.83 Successfully installed nvidia-cublas-cu12-12.4.5.8 nvidia-cuda-cupti-cu12-12.4.127 nvidia-cuda-nvrtc-cu12-12.4.127 nvidia-cuda-runtime-cu12-12.4.127 nvidia-cudnncu12-9.1.0.70 nvidia-cufft-cu12-11.2.1.3 nvidia-curand-cu12-10.3.5.147 nvidiacusolver-cu12-11.6.1.9 nvidia-cusparse-cu12-12.3.1.170 nvidia-nvjitlink-cu12-12.4.127 ultralytics-8.3.142 ultralytics-thop-2.0.14

<!-- source_page: 106 -->

```text
# 2. Importação das bibliotecas
import cv2
import matplotlib.pyplot as plt
import numpy as np
from ultralytics import YOLO
from google.colab.patches import cv2_imshow
import os
```

ao) 🚀 YOLOv8n: Detecção de Objetos Rápida e Leve

Agora vamos conhecer uma das versões mais modernas e eficientes da família YOLO: o YOLOv8n (versão "Nano"). Desenvolvido pela Ultralytics, ele é um modelo de detecção de objetos em tempo real, ideal para dispositivos com poucos recursos, como celulares ou microcontroladores.

**Figura 44 - Arquitetura YOLOv8**

Fonte: Ultralytics

<!-- source_page: 107 -->

ap) 🧠 O que torna o YOLOv8n especial?

- 📦 Tamanho compacto: apenas 6.2 MB — cabe em praticamente qualquer dispositivo!

- ⚡ Muito rápido: processa imagens em milissegundos, mesmo sem GPU.

- 🎯 Boa precisão para seu tamanho: detecta objetos com mAP de ~37.3% no benchmark COCO.

⚙️ Como funciona o YOLOv8n? Ele é dividido em três partes principais:

1. Backbone – extrai características importantes da imagem (como formas e texturas);

- Usa uma arquitetura moderna chamada CSPDarknet, com blocos especiais chamados C2f e SPPF.

2. Neck – combina informações de diferentes tamanhos da imagem;

- Usa FPN (Feature Pyramid Network), que ajuda a detectar objetos pequenos e grandes.

3. Head – faz a previsão final: onde estão os objetos e o que eles são;

- Não usa "âncoras" fixas, o que deixa tudo mais rápido e direto.

📋 Especificações técnicas (em linguagem simples):

- 🧠 Parâmetros: ~3.2 milhões (medida de "quantas coisas o modelo aprendeu")

- ⏱️ Velocidade:

- GPU: 2-6 milissegundos por imagem

- CPU: 15-30 milissegundos (varia com o computador)

- 📐 Tamanho da imagem: 640x640 pixels (entrada padrão)

- 🧪 Treinado no dataset COCO (80 classes como: pessoas, carros, cães, bicicletas…)

<!-- source_page: 108 -->

🐶 O que o YOLOv8n consegue detectar?

- Pessoas 👤

- Animais (gatos, cachorros, cavalos…)

- Veículos (carros, bicicletas, aviões…)

- Objetos comuns (celulares, xícaras, cadeiras…)

💡 Por que usar o YOLOv8n?

- ✅ Rápido

- ✅ Leve

- ✅ Boa precisão

- ✅ Ideal para projetos reais com poucos recursos

💡 Dica: Se você quer um modelo de detecção para usar em tempo real no seu celular, webcam ou microcontrolador, o YOLOv8n é um excelente ponto de partida!

Comparação entre os modelos YOLO V8 📊

**Figura 45 - Comparação entre os modelos YOLOv8**

Fonte: Ultralytics

<!-- source_page: 109 -->

```text
# 3. Carregar o modelo YOLOv8
# YOLOv8n é o modelo "nano" - pequeno e rápido
print("Carregando o modelo YOLOv8...")
modelo = YOLO('yolov8n.pt') # Baixa automaticamente o modelo se ele não
existir localmente
print("\nModelo carregado com sucesso!")
```

Carregando o modelo YOLOv8...

Modelo carregado com sucesso!

```text
# 4. Função para baixar uma imagem usando wget
def baixar_imagem(url, nome_arquivo='imagem_temp.jpg'):
print(f"Baixando imagem de {url}...")
# Usar wget para baixar a imagem
```

!wget -q {url} -O {nome_arquivo}

```text
# Ler a imagem com OpenCV
img = cv2.imread(nome_arquivo)
```

```text
# Verificar se a imagem foi carregada corretamente
if img is None:
print(f"Erro ao carregar a imagem {nome_arquivo}")
return None
```

```text
print(f"Imagem baixada e carregada com sucesso!")
return img
```

```text
# 5. Função para desenhar caixas delimitadoras (bounding boxes)
def desenhar_bbox(img, resultados):
# Criar uma cópia da imagem para não alterar a original
img_bbox = img.copy()
```

```text
# Obter as previsões
boxes = resultados[0].boxes
```

# Para cada detecção for box in boxes:

# Coordenadas da caixa x1, y1, x2, y2 = map(int, box.xyxy[0])

```text
# ID da classe e confiança
cls_id = int(box.cls[0])
conf = float(box.conf[0])
```

# Nome da classe (obtida a partir do modelo) continua continua

<!-- source_page: 110 -->

cls_name = resultados[0].names[cls_id]

# Definir cor com base no ID da classe (para ter cores diferentes para classes diferentes)

color = (int(hash(cls_name) % 255), int(hash(cls_name[::-1]) % 255), int(hash(cls_name + "color") % 255))

# Desenhar retângulo cv2.rectangle(img_bbox, (x1, y1), (x2, y2), color, 4)

```text
# Adicionar texto com nome da classe e confiança
text = f"{cls_name}: {conf:.2f}"
cv2.putText(img_bbox, text, (x1, y1 - 10), cv2.FONT_HERSHEY_SIMPLEX,
1.0, color, 3)
```

return img_bbox

```text
# 6. Função para mostrar os resultados
def mostrar_resultados(img_original, img_com_bbox):
# Converter de BGR para RGB para exibição correta
img_rgb = cv2.cvtColor(img_original, cv2.COLOR_BGR2RGB)
img_bbox_rgb = cv2.cvtColor(img_com_bbox, cv2.COLOR_BGR2RGB)
```

```text
# Exibir as imagens lado a lado
plt.figure(figsize=(16, 8))
```

```text
plt.subplot(1, 2, 1)
plt.imshow(img_rgb)
plt.title('Imagem Original')
plt.axis('off')
```

```text
plt.subplot(1, 2, 2)
plt.imshow(img_bbox_rgb)
plt.title('Detecções YOLO')
plt.axis('off')
```

```text
plt.tight_layout()
plt.show()
```

```text
# 7. Usando o modelo em imagens de exemplo
# Exemplos de imagens para detecção
urls_exemplos = [
"https://raw.githubusercontent.com/ultralytics/yolov5/master/data/images/
zidane.jpg", # Pessoa
"https://raw.githubusercontent.com/ultralytics/yolov5/master/data/images/
bus.jpg", # Ônibus
```

"https://cdn.pixabay.com/photo/2017/12/17/12/45/football-3024154_1280.jpg" # Esporte ]

<!-- source_page: 111 -->

```text
# Primeiro vamos visualizar as imagens que vamos usar para teste
print("\n# Visualização das imagens de teste")
plt.figure(figsize=(15, 10))
```

```text
for i, url in enumerate(urls_exemplos):
# Nome do arquivo temporário com índice para evitar sobrescrever
nome_arquivo = f"imagem_exemplo_{i}.jpg"
```

```text
# Baixar a imagem
img = baixar_imagem(url, nome_arquivo)
```

if img is not None:

```text
# Converter de BGR para RGB para exibição correta
img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
```

```text
# Mostrar a imagem
plt.subplot(1, len(urls_exemplos), i+1)
plt.imshow(img_rgb)
plt.title(f'Imagem de teste {i+1}')
plt.axis('off')
```

```text
plt.tight_layout()
plt.show()
```

# Visualização das imagens de teste Baixando imagem de https://raw.githubusercontent.com/ultralytics/yolov5/master/ data/images/zidane.jpg... Imagem baixada e carregada com sucesso! Baixando imagem de https://raw.githubusercontent.com/ultralytics/yolov5/master/ data/images/bus.jpg... Imagem baixada e carregada com sucesso! Baixando imagem de https://cdn.pixabay.com/photo/2017/12/17/12/45/ football-3024154_1280.jpg... Imagem baixada e carregada com sucesso!

**Figura 46 - Imagem de Teste 1**

Fonte: Ultralytics

<!-- source_page: 112 -->

**Figura 47 - Imagem de Teste 2**

Fonte: Ultralytics

**Figura 48 - Imagem de Teste 2**

Fonte: Ultralytics

```text
# Agora vamos processar cada imagem
print("\n# Detecção de objetos com YOLO")
for i, url in enumerate(urls_exemplos):
# Nome do arquivo temporário com índice para evitar sobrescrever
nome_arquivo = f"imagem_exemplo_{i}.jpg"
```

# Baixar a imagem (ou usar a já baixada) continua continua

<!-- source_page: 113 -->

```text
if not os.path.exists(nome_arquivo):
img = baixar_imagem(url, nome_arquivo)
else:
img = cv2.imread(nome_arquivo)
```

if img is not None:

```text
print(f"\nRealizando detecção de objetos na imagem {i+1}...")
# Realizar a detecção com o modelo YOLO
resultados = modelo(img)
```

```text
# Desenhar as bounding boxes na imagem
img_com_bbox = desenhar_bbox(img, resultados)
```

# Mostrar os resultados mostrar_resultados(img, img_com_bbox)

```text
# Exibir classes detectadas
classes_detectadas = set([resultados[0].names[int(box.cls[0])] for box
in resultados[0].boxes])
```

print(f"Classes detectadas: {', '.join(classes_detectadas)}")

# Limpar arquivo temporário if os.path.exists(nome_arquivo):

!rm {nome_arquivo}

# Detecção de objetos com YOLO

Realizando detecção de objetos na imagem 1...

0: 384x640 2 persons, 1 tie, 363.0ms Speed: 5.0ms preprocess, 363.0ms inference, 41.1ms postprocess per image at shape (1, 3, 384, 640)

**Figura 49 - Detecção Yolo 1**

Fonte: autoria própria

<!-- source_page: 114 -->

Classes detectadas: tie, person

Realizando detecção de objetos na imagem 2...

0: 640x480 4 persons, 1 bus, 1 stop sign, 236.0ms Speed: 6.0ms preprocess, 236.0ms inference, 1.7ms postprocess per image at shape (1, 3, 640, 480)

**Figura 50 - Detecção Yolo 2**

Fonte: autoria própria

Classes detectadas: stop sign, person, bus

Realizando detecção de objetos na imagem 3...

0: 352x640 (no detections), 247.4ms Speed: 5.1ms preprocess, 247.4ms inference, 1.5ms postprocess per image at shape (1, 3, 352, 640)

<!-- source_page: 115 -->

**Figura 51 - Detecção Yolo 3**

Fonte: autoria própria

Classes detectadas:

```text
from google.colab import files
print("Faça o upload de uma imagem:")
uploaded = files.upload()
```

```text
for filename in uploaded.keys():
# Ler imagem enviada
img = cv2.imread(filename)
```

if img is not None:

```text
# Realizar detecção
resultados = modelo(img)
```

```text
# Desenhar bounding boxes
img_com_bbox = desenhar_bbox(img, resultados)
```

# Mostrar resultados mostrar_resultados(img, img_com_bbox)

# Limpar arquivo temporário !rm {filename}

Faça o upload de uma imagem: Nenhum arquivo escolhido Upload widget is only available when the cell has been executed in the current browser session. Please rerun this cell to enable. Saving Flux_Schnell_highresolution_stock_photo_of_a_confident_busines_1.jpg to Flux_Schnell_highresolution_stock_photo_of_a_confident_busines_1.jpg

0: 640x640 2 persons, 2 chairs, 1 dining table, 1 tv, 312.0ms Speed: 7.8ms preprocess, 312.0ms inference, 3.9ms postprocess per image at shape (1, 3, 640, 640)

<!-- source_page: 116 -->

**Figura 52 - Detecção Yolo 3**

Fonte: autoria própria

**[Conteúdo visual da página - Figura 52: Detecção Yolo 3]**

A figura compara a imagem original com as detecções YOLO. As caixas exibidas incluem: `person: 0.90`, `person: 0.91`, `tv: 0.32`, `chair: 0.81`, `chair: 0.30` e `dining table: 0.31`. Fonte indicada na figura: autoria própria.

<!-- source_page: 117 -->

### 2.3.1 Conceitos e fundamentos

A base da Visão Computacional é transformar informações visuais (imagem ou vídeo) em dados que possam ser processados por algoritmos. Lembrando que um vídeo nada mais é que uma sequência de imagens sendo exibidas para você a uma taxa de quadros por segundo (FPS), ou seja, um vídeo de 30 FPS são 30 imagens passando por segundo na sua tela. A imagem, antes de ser entendida pelo computador, é decomposta em pixels, que é a menor unidade de uma imagem. Esses pixels possuem informações cruciais para os modelos, relacionados à cor e intensidade de luz de cada um. Um aspecto fundamental para a área é o entendimento de como as cores são representadas digitalmente. Em um computador, as imagens costumam ser codificadas em canais de cor, que nada mais são do que componentes que descrevem a intensidade de cada cor primária em cada pixel. Existem diversos tipos de modelos de cor, porém focaremos no mais comum que é o RGB (Red, Green, Blue). Cada cor primária aqui representa um canal que recebe um valor numérico que indica o quanto daquela cor específica há no pixel. Esse valor numérico varia de 0 a 255, sendo 0 a ausência da cor e 255 o valor máximo de intensidade dessa cor no pixel. Na prática, podemos pensar em uma imagem na resolução de 1280 x 720 pixels que é o padrão conhecido como resolução HD (High-Definition). Isso significa que essa imagem possui 1280 pixels de altura e 720 pixels de comprimento (ou vice e versa) totalizando 921.600 pixels. Cada pixel, por sua vez, é descrito pelos valores dos três canais (Red, Green e Blue). Por exemplo, se o canal Red de um pixel tem o valor 255, o canal Green tem o valor 0 e o canal Blue também 0, esse pixel específico na imagem representa a cor vermelha intensa. Se todos os canais tiverem valores elevados, o pixel aparecerá branco; se todos forem baixos, aparecerá preto. Dessa forma, quando se mistura uma quantidade adequada de vermelho, verde e azul em cada ponto, é possível representar uma ampla gama de cores, de forma semelhante à percepção humana. A intensidade de cada canal misturado aos outros dois cria um total de 16,7 milhões de combinações possíveis. A partir daí, os algoritmos aplicam métodos matemáticos e estatísticos para detectar linhas, formas, cores e padrões. Na Figura 53 é apresentada a formação de uma imagem digital a partir dos canais RGB.

<!-- source_page: 118 -->

**Figura 53 - Formação da imagem digital a partir dos canais de cor RGB por pixel**

Fonte: Adaptada de Vale (2021).

A questão de reconhecimento de padrões é outro conceito importante, permitindo que computadores aprendam a identificar características visuais (como formas, texturas ou cores) e classifiquem imagens em categorias específicas, por exemplo, aprendendo quais características classificam um gato e que o diferenciam de um cachorro. Esse processo é feito com a ajuda de grandes bases de dados e algoritmos de aprendizado de máquina envolvendo as redes neurais convolucionais (do inglês, Convolutional Neural Networks – CNNs), que "aprendem" a associar características visuais a determinados objetos detectando padrões em diversos locais da imagem (NNMOC Book). Essas redes fazem operações chamadas de convolução aliadas a aplicações de filtros que varrem uma imagem, analisando pequenos blocos de pixels por vez e destacando traços importantes daquele objeto. Conforme a rede se aprofunda, camadas sucessivas aprendem padrões cada vez mais complexos, combinando as características detectadas em níveis anteriores. Para exemplificar esse reconhecimento de padrões vamos utilizar a imagem apresentada na Figura 54 em que temos o número 8 em uma resolução de 22x16 pixels. A imagem está em uma escala de cor dita como escala de cinza, que possui apenas um canal, diferentemente do RGB que são 3. Esse único canal recebe também valores de 0 a 255 (0 sendo a ausência e 255 a intensidade máxima da cor), porém a única cor presente é o cinza em diversas tonalidades.

<!-- source_page: 119 -->

**Figura 54 - Imagem digital representada por matrizes de pixels e padrões numéricos**

Fonte: Adaptada de Mota (2018).

Na imagem é possível ver a análise por blocos de pixels. Os valores contidos nesses blocos são os que são passados para que a rede aprenda as características desse número. Dessa forma, ao receber uma outra imagem com o dígito 8, todas as operações são feitas novamente para extrair as características dessa nova imagem e, então, a rede compara com o conhecimento que ela possui e reconhece o padrão, conseguindo classificá-lo de forma correta. Apesar dos avanços, a visão computacional ainda enfrenta desafios. Iluminação inadequada, variações de tamanho, posição dos objetos e a presença de ruídos (interferências na imagem) podem comprometer o desempenho dos algoritmos. Logo, quanto maior a variedade de dados fornecidos para a rede, melhor ela performa em um ambiente de testes reais, ou seja, fora de um ambiente controlado.

<!-- source_page: 120 -->

### 2.3.2 Aplicações de visão computacional

A visão computacional engloba diversas tarefas (tasks) que permitem aos computadores interpretarem informações visuais de modo cada vez mais próximo ao olhar humano. Entre essas tarefas, destacam-se a segmentação, que consiste em separar a imagem em regiões com características semelhantes; a classificação, onde cada imagem ou parte dela recebe um rótulo específico; e a detecção, que localiza a posição de determinados objetos ou padrões dentro da cena. Cada uma delas possui áreas derivadas, mas não cabe aqui aprofundar em cada uma. A imagem, apresentada na Figura 55, traz exemplos práticos dessas tarefas.

**Figura 55 - Exemplos de tarefas em visão computacional: classificação, detecção e segmentação de objetos**

Fonte: Adaptada de Christian (2022).

A visão computacional possui aplicações em uma ampla variedade de campos, por exemplo: na indústria do entretenimento, a realidade aumentada insere elementos digitais no ambiente real, aprimorando a experiência do usuário em jogos e aplicativos educacionais; o upscaling de imagens melhora a qualidade e a resolução de fotos ou vídeos antigos; em tarefas de automação, como em veículos autônomos, os algoritmos precisam identificar pedestres, obstáculos e faixas de trânsito para navegação segura; em contextos de Reconhecimento Óptico de Caracteres (OCR), em inglês Optical Character Recognition, converte-se texto impresso ou manuscrito em formato digital; na saúde, a análise de imagens médicas auxilia médicos na detecção precoce de doenças, enquanto o reconhecimento facial oferece soluções de segurança e verificação de identidade. Em síntese, a visão computacional é fundamental para que máquinas e sistemas inteligentes sejam capazes de interpretar o mundo visual ao seu redor. A evolução das técnicas de processamento de imagens e aprendizado de máquina possibilitou a

<!-- source_page: 121 -->

criação de soluções cada vez mais precisas e eficientes, influenciando áreas que vão da medicina ao varejo. Com o contínuo avanço da IA, espera-se que o campo da visão computacional traga inovações ainda mais impactantes, ampliando suas aplicações e benefícios para a sociedade.

Agora é sua vez!

Acesse o notebook para colocar em prática o conteúdo adquirido e sintetizar o tópico de Visão Computacional.

Saiba mais...

Confira algumas aplicações surpreendentes para despertar ainda mais o seu interesse:

Início inusitado: Em 1966, no MIT, o pesquisador Seymour Papert propôs um projeto de verão para que estudantes "resolvessem a visão computacional". Acreditava-se que o problema pudesse ser resolvido em poucos meses, mas a complexidade mostrou que precisaria de décadas de pesquisa.

Mars Rovers da NASA: Robôs que exploram Marte usam câmeras e algoritmos de Visão Computacional para analisar rochas, mapear o terreno e navegar pelo planeta vermelho de forma autônoma.

Kinect® e consoles de Jogo: O Kinect®, lançado pela Microsoft® para Xbox®, foi um marco na interação homem-máquina, pois utiliza sensores de profundidade para capturar movimentos do corpo em tempo real, abrindo caminho para novas formas de entretenimento e pesquisas em robótica.

Face ID® em Smartphones: A tecnologia de reconhecimento facial em celulares é fruto de algoritmos de visão computacional que mapeiam e analisam pontos do rosto do usuário, oferecendo um método de segurança prático e eficiente.

Detecção de emoções: Alguns sistemas são capazes de estimar estados emocionais de uma pessoa a partir de expressões faciais e microexpressões, mostrando o quanto a Visão Computacional pode se aproximar da percepção humana. continua continua

<!-- source_page: 122 -->

Carros autônomos: Além de lidar com radares e sensores de proximidade, veículos autônomos têm câmeras que "enxergam" semáforos, placas de trânsito e pedestres, demonstrando como a visão computacional se torna essencial para a mobilidade do futuro.

Já pensou em criar seu próprio modelo de visão computacional em minutos? A ferramenta do Google® chamada de Teachable Machine permite treinar modelos de classificação de imagens diretamente no navegador, sem a necessidade de programação avançada. É uma ótima forma de entender como a máquina "aprende" a identificar objetos. Confira no endereço a seguir: https://teachablemachine.withgoogle.com/

Quer entender de forma prática como os filtros de imagem atuam na visão computacional? Visite o site Setosa e explore uma demonstração interativa incrível! Essa ferramenta permite experimentar com diversos filtros (kernels) e visualizar, em tempo real, como cada filtro transforma uma imagem. Ideal para entusiastas que desejam aprofundar seus conhecimentos de maneira visual e dinâmica.

4.4 Processamento de áudio e voz

A voz é uma das formas mais naturais e poderosas de comunicação humana. Através dela, transmitimos não apenas informações objetivas, mas também emoções, intenções e até aspectos culturais. Seja em uma conversa, em uma música, ou até mesmo em uma simples interjeição como "ai" ao nos machucarmos, há uma enorme quantidade de significados embutidos nos sons que emitimos. É justamente por essa riqueza comunicativa que o processamento de áudio e voz se tornou uma área essencial dentro da IA. Cada vez mais, convivemos com sistemas que interpretam, traduzem ou geram voz de maneira automática. Esses sistemas já fazem parte do nosso cotidiano – criando legendas automáticas em vídeos, transformando nossa fala em texto, e até sintetizando vozes digitais para criar efeitos, dublagens ou auxiliar pessoas com deficiência.

<!-- source_page: 123 -->

Notebook Colab

a) Objetivos de Aprendizagem

- Este notebook tem como objetivo demonstrar na prática como utilizar o Whisper, um modelo avançado de reconhecimento de fala da OpenAI, para

transcrever áudios automaticamente.

🔹 O que você vai aprender?

- Instalar e configurar o Whisper e suas dependências.

- Gravar ou carregar um arquivo de áudio (formato WAV recomendado).

- Escolher o modelo ideal (entre tiny, base, small, medium e large) de acordo com a necessidade (velocidade vs. precisão).

- Realizar a transcrição automática, com detecção de idioma e geração do texto correspondente.

- Ajustes opcionais, como forçar o idioma português ou otimizar para CPU/GPU.

🎯 Por que isso é útil?

- Automatizar a conversão de fala para texto (ideal para entrevistas, reuniões, aulas, etc.).

- Aplicar em projetos de processamento de linguagem natural (NLP) ou análise de conteúdo.

b) Instalar dependências

Primeiro, precisamos trazer para o ambiente do Colab duas bibliotecas essenciais:

- Whisper (repositório oficial da OpenAI) – responsável por carregar o modelo pré-treinado de transcrição.

- ffmpeg-python – interface em Python para o FFmpeg, que converte e prepara o áudio para o modelo. Ao executaro Colab baixa e instala essas ferramentas, garantindo que tenhamos tudo pronto para processar o arquivo de áudio.

<!-- source_page: 124 -->

```text
!pip install -q --upgrade git+https://github.com/openai/whisper.git
!pip install -q --upgrade ffmpeg-python
```

Installing build dependencies ... done Getting requirements to build wheel ... done Preparing metadata (pyproject.toml) ... done ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 363.4/363.4 MB 4.0 MB/s eta 0:00:00

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 13.8/13.8 MB 66.9 MB/s eta 0:00:00 ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 24.6/24.6 MB 56.1 MB/s eta 0:00:00 ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 883.7/883.7 kB 36.7 MB/s eta 0:00:00

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 664.8/664.8 MB 1.5 MB/s eta 0:00:00

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 211.5/211.5 MB 5.8 MB/s eta 0:00:00

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 56.3/56.3 MB 13.9 MB/s eta 0:00:00 ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 127.9/127.9 MB 7.5 MB/s eta 0:00:00 ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 207.5/207.5 MB 5.9 MB/s eta 0:00:00

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 21.1/21.1 MB 67.0 MB/s eta 0:00:00 Building wheel for openai-whisper (pyproject.toml) ... done

c) Grave um audio e faça o upload aqui

Use o site https://products.aspose.app/audio/pt/voice-recorder/wav para gravar um audio em formato wav. Depois, basta executar a célula abaixo e fazer o upload do arquivo.

O código abaixo abre uma janela para você selecionar o arquivo. Depois de concluído, uploaded é um dicionário onde a chave é o nome do arquivo; atribuímos essa chave a audio_path para usar nas etapas seguintes.

```text
from google.colab import files
uploaded = files.upload() # vai abrir diálogo para selecionar o arquivo
audio_path = list(uploaded.keys())[0]
print("Arquivo carregado:", audio_path)
```

<!-- source_page: 125 -->

Nenhum arquivo escolhido Upload widget is only available when the cell has been executed in the current browser session. Please rerun this cell to enable. Saving record_out.wav to record_out.wav Arquivo carregado: record_out.wav

d) Carregamento do modelo Whisper

Com o áudio disponível, carregamos o modelo pré-treinado:

```text
import whisper
# modelos disponíveis: tiny, base, small, medium, large
model = whisper.load_model("small")
```

100%|███████████████████████████████████████| 461M/461M [00:05<00:00, 91.4MiB/s]

Aqui escolhemos o modelo "small" por equilibrar precisão e velocidade. Se quiser mais rapidez (mas menor acurácia), pode usar "tiny" ou "base"; para máxima qualidade, "medium" ou "large".

e) Transcrição do áudio

Finalmente, enviamos o caminho do arquivo para o modelo:

```text
result = model.transcribe(audio_path, fp16=False) # fp16=False se estiver em
CPU
print("Idioma detectado:", result["language"])
print("Transcrição:\n", result["text"])
```

Idioma detectado: pt Transcrição: Um, dois, três, quatro.

<!-- source_page: 126 -->

- fp16=False força precisão simples (float32) no CPU. Se você estiver em GPU e preferir meio-precisão, pode remover esse parâmetro.

O dicionário result traz:

- result["language"]: idioma detectado automaticamente.

- result["text"]: o texto transcrito do seu áudio.

f) Ajustes opcionais

Para forçar o português, adicione language="pt" em transcribe(). Se for trabalhar sem GPU, prefira modelos menores (tiny, base) para economizar tempo. Em GPU, use fp16=True (ou omita fp16) para aproveitar a aceleração por meio-precisão.

<!-- source_page: 127 -->

### 2.4.1 Conceitos e fundamentos

Diferentemente dos humanos, computadores não têm a capacidade inata de compreender o som. Para uma máquina, o áudio é apenas uma sequência de variações de pressão no ar, capturada por sensores como microfones. Para que esse sinal seja compreendido por um sistema de IA, ele precisa ser convertido para uma forma numérica – um processo conhecido como representação digital do som. O som é convertido em uma série de amostras (amostragem), resultando em uma sequência de números que representam a intensidade do som ao longo do tempo. Em seguida, utilizamos técnicas como a Transformada de Fourier ou o Mel-Frequency Cepstral Coefficients (MFCCs) para extrair características relevantes do áudio. Essas representações mostram "como" o som se comporta no tempo e nas frequências – informações cruciais para que um modelo de IA entenda a estrutura da fala. Uma vez que temos essa representação vetorial, podemos alimentar redes neurais que aprendem padrões entre os sons e suas correspondências textuais. Para tarefas de Speech to Text (STT), o modelo aprende a mapear sequências de áudio para transcrições escritas. Já no caso de Text to Speech (TTS), o modelo aprende o caminho inverso: gerar um sinal sonoro a partir de um texto, sintetizando fala artificial com entonações realistas. Na Figura 56 são apresentadas as etapas de processamento de áudio para conversão fala-texto e texto-fala

**Figura 56 - Pipeline de processamento de áudio para conversão fala-texto e texto-fala**

Fonte: Autoria própria.

#### 2.4.1.1 Aplicações de processamento de áudio e voz

Hoje, os sistemas de processamento de áudio estão embutidos em uma ampla variedade de aplicações. Os assistentes virtuais, como a Alexa®, a Siri® e o Google Assistant®, são exemplos diretos de uso de tecnologias STT e TTS. Quando dizemos "Qual a previsão do tempo?", o sistema precisa

<!-- source_page: 128 -->

primeiro transcrever nossa fala em texto, interpretar o significado da frase (processamento de linguagem natural) e, então, responder com uma voz artificial. Outro exemplo relevante está nos serviços de acessibilidade: pessoas com deficiência auditiva se beneficiam de legendas geradas automaticamente, enquanto pessoas com deficiência na fala podem usar sintetizadores de voz para se comunicar. Com o crescimento das redes sociais baseadas em vídeo, como TikTok e Instagram Reels, recursos como geração automática de legendas, vozes narrativas e dublagens também passaram a ser ferramentas criativas indispensáveis. Nesses contextos, o áudio não é apenas um canal de comunicação, mas um componente artístico e expressivo. Além disso, áreas como tradução simultânea, atendimento automatizado, educação de idiomas e até biometria de voz (autenticação por características vocais) utilizam essas tecnologias para melhorar a experiência do usuário.

Agora é sua vez!

Acesse o notebook para colocar em prática o conteúdo adquirido e sintetizar o tópico de Processamento de áudio e voz.

Saiba mais…

Explorando como a IA vê o áudio

Para aprofundar os conceitos apresentados, disponibilizamos um notebook interativo no Google Colab® que permite visualizar o espectro de uma nota musical tocada por diferentes instrumentos. No exemplo, exploramos a nota C6 (dó na sexta oitava) e comparamos sua representação espectral quando tocada por um oboé e uma clarineta. continua continua

<!-- source_page: 129 -->

Apesar de ambos os instrumentos tocarem exatamente a mesma nota em termos de altura (frequência fundamental), o som que ouvimos é claramente distinto. Isso acontece por causa do timbre, uma característica sonora que depende das frequências harmônicas que acompanham a nota principal. O oboé, por exemplo, tende a produzir um som mais penetrante e "nasal", com harmônicos fortes e próximos, enquanto a clarineta tem um som mais aveludado e suave, com uma distribuição diferente de harmônicos – inclusive enfatizando os ímpares. Essas diferenças são visíveis quando analisamos a forma de onda e o espectro de frequência dos dois instrumentos. Por meio dessa visualização, conseguimos entender melhor como a IA pode aprender a identificar, classificar ou até sintetizar diferentes sons, mesmo que eles tenham a mesma frequência base.

Acesse o notebook no Google Colab®

<!-- source_page: 130 -->

## 2.5 Computação em nuvem, computação de borda e IoT

Atualmente, estamos cercados por dispositivos inteligentes que fazem parte do nosso dia a dia, desde assistentes nos celulares até eletrodomésticos conectados, os famosos dispositivos "smart". Por trás dessas tecnologias, três conceitos se destacam: computação em nuvem, computação de borda e a Internet das Coisas (IoT). Compreender essas tecnologias e como elas se integram é essencial para perceber como a inovação tecnológica transforma nossas vidas, facilitando tarefas e abrindo caminho para novas possibilidades no futuro.

### 2.5.1 Computação em nuvem

A computação em nuvem é como um grande depósito digital acessado pela internet, onde estão guardados arquivos, aplicativos e até sistemas de inteligência artificial. Ao invés de usarmos programas instalados diretamente em nossos celulares ou computadores, utilizamos recursos disponíveis remotamente. A IA, nesse contexto, aproveita a enorme capacidade desses servidores para processar grandes quantidades de dados rapidamente. Por exemplo, quando você usa um assistente virtual no seu celular, suas perguntas são processadas pelo dispositivo e então enviadas à nuvem, onde algoritmos de IA analisam a voz, buscam informações e lhe devolvem respostas em tempo real. Assim, a computação em nuvem permite que sistemas inteligentes fiquem disponíveis a qualquer momento, em qualquer lugar, sem exigir equipamentos caros ou potentes do usuário. O processamento em nuvem permite que pessoas possam desenvolver seus projetos em máquinas que, devido a seu preço, são inacessíveis para a grande maioria das pessoas.

### 2.5.2 Computação de borda

A computação de borda pode ser vista como uma parente próxima da computação em nuvem, só que mais rápida (em alguns casos) e muito mais perto do usuário. Está presente nos dispositivos que usamos diariamente, como celulares, câmeras de segurança e smartwatches. Em vez de mandar todos os dados para servidores muito distantes, as informações são analisadas localmente, o que acelera a resposta e diminui a dependência da internet. Com o avanço da tecnologia, temos dispositivos cada vez mais poderosos computacionalmente, permitindo, em alguns casos, a utilização de algoritmos inteligentes diretamente neles.

<!-- source_page: 131 -->

Imagine que você tenha uma câmera de segurança em casa que reconhece automaticamente o rosto das pessoas. Ao invés de enviar os vídeos completos para a nuvem, a IA na própria câmera processa as imagens na hora, economizando tempo e protegendo a privacidade das pessoas envolvidas.

### 2.5.3 Internet das Coisas (IoT)

Internet das Coisas refere-se à rede de dispositivos do cotidiano de pessoas e empresas conectados à internet, capazes de coletar, enviar e receber informações automaticamente. Esses objetos inteligentes vão desde geladeiras que avisam quando um produto está acabando até sensores em fazendas que monitoram temperatura e umidade do solo para melhorar a produção agrícola. Basicamente são dispositivos implementados com o intuito de coletar um grande número de informações, podendo, também, manipular esses dados e realizar ações. Combinada com IA, a IoT permite soluções inteligentes de baixo custo muitas vezes. Na Figura 57, é apresentada a arquitetura de processamento em borda para a Internet das Coisas.

**Figura 57 - Arquitetura de processamento em borda para a Internet das Coisas (IoT)**

Fonte: Autoria própria

<!-- source_page: 132 -->

Saiba mais…

Curiosidades:

- Sabia que a computação em nuvem começou a ganhar força com a popularização dos serviços de armazenamento como Dropbox e Google Drive®?

- O conceito de computação de borda ficou famoso com os carros autônomos, que precisam processar informações rapidamente para tomar decisões em tempo real.

- A IoT já está presente até mesmo em roupas inteligentes, capazes de monitorar sua saúde ou desempenho esportivo!

Por onde começar?

- IoT: kits de Arduino e ESP32 são excelentes para iniciantes devido à facilidade e custo acessível, oferecendo diversos miniprojetos que podem ser realizados sem muita experiência técnica.

- Computação em nuvem: o Google Cloud Platform® (GCP) oferece descontos e bônus especiais para estudantes, facilitando o acesso inicial, porém demanda um pouco mais de conhecimento.

- CLOUD TREINAMENTOS. O que é Cloud Computing? Uma explicação simples para iniciantes. [S.l.]: YouTube, 8 de jan. de 2023. Disponível em: https:// youtu.be/aYIVId5FzX4. Acesso em: 13 fev. 2025.

- Computação de borda: explorar projetos simples utilizando Raspberry Pi, que é como um mini PC, pode ser um ótimo ponto de partida, afinal esses hardwares conseguem suportar alguns modelos locais e realizar projetos bem legais.

- DICIONARIOTEC. Entenda o que é computação de borda em menos de 2 minutos. [S.l.]: YouTube, 27 de dez. de 2022. Disponível em: https://www.youtube.com/watch?v=XpviTycY1jY. Acesso em: 13 fev. 2025.

<!-- source_page: 133 -->

## 2.6 Robótica

Quando pensamos em inteligência artificial, é comum que a imagem de um robô venha imediatamente à mente. Essa associação não é por acaso: a robótica é uma das áreas onde a IA ganha uma forma tangível, quase "humana". Ao longo dos anos, desenvolvemos máquinas que não apenas executam tarefas automatizadas, mas também são capazes de perceber, decidir e interagir com o mundo ao seu redor – capacidades centrais da inteligência artificial. A IA aplicada à robótica transforma robôs em agentes inteligentes. Eles deixam de ser apenas braços mecânicos repetitivos para se tornarem sistemas adaptativos capazes de navegar por ambientes complexos, reagir a estímulos inesperados e até mesmo aprender com a experiência. Esse avanço é o que torna possível termos robôs que limpam nossa casa, entregam encomendas, realizam cirurgias ou exploram outros planetas.

Acesse o simulador de garra mecânica

### 2.6.1 Conceitos e fundamentos

Para entender como a inteligência artificial é usada na robótica, primeiro precisamos entender o que define um robô. De maneira geral, um robô é qualquer sistema físico capaz de perceber o ambiente ao seu redor e agir sobre ele com algum nível de autonomia. Isso significa que os robôs combinam sensores (para percepção) e atuadores (para ação), controlados por algoritmos que determinam seu comportamento. É aí que a IA entra: ela fornece a "inteligência" que conecta percepção e ação. Por exemplo, um robô que precisa se locomover em um ambiente desconhecido usa sensores como câmeras ou radares para mapear o local (percepção), algoritmos de IA para decidir o melhor caminho (planejamento) e motores para se mover de fato (ação). Essa cadeia de decisões é o que permite ao robô lidar com obstáculos, recalcular rotas e até prever comportamentos humanos ao seu redor. Além das funções básicas, a IA também possibilita que os robôs interajam de maneira mais natural com as pessoas. Sistemas de visão computacional permitem reconhecer rostos, gestos e objetos. Processamento de linguagem natural permite entender comandos de voz ou responder a perguntas. Combinadas, essas tecnologias tornam os robôs mais acessíveis, úteis e até simpáticos – transformando-os em verdadeiros assistentes inteligentes. Na Figura 58 estão apresentadas as capacidades da IA aplicadas à robótica

<!-- source_page: 134 -->

**Figura 58 - Capacidades da inteligência artificial aplicadas à robótica**

Fonte: Autoria própria

Saiba mais…

Exemplo de robôs

- Imagine um pequeno robô sobre rodas que se move sozinho pelas calçadas de uma cidade. Esses robôs, já utilizados em universidades e bairros nos Estados Unidos, são projetados para entregar comida ou encomendas de forma autônoma. Eles usam visão computacional para identificar obstáculos, mapas e GPS para saber onde estão e para onde ir e modelos de IA para tomar decisões em tempo real – como desviar de pedestres ou esperar para atravessar uma rua.

- Além de sensores e motores, esses robôs são conectados a sistemas inteligentes que os ajudam a reagir a imprevistos do ambiente. Em alguns casos, eles também se comunicam com centrais de monitoramento humano, caso encontrem alguma dificuldade. Esse tipo de robô é um exemplo claro de como percepção, decisão e ação se unem com a ajuda da IA para criar máquinas que podem interagir com o mundo de forma inteligente.

Quer saber mais?

- A empresa Synkar é brasileira, e nasceu no meio de Goiás.

<!-- source_page: 135 -->

Agora que entendemos as principais abordagens de IA e como elas são aplicadas para resolver problemas reais, é essencial entender como usar essas técnicas na prática para implementar soluções efetivas. Para isso, é necessário seguir um fluxo de trabalho estruturado, que começa com a definição clara do problema e passa por etapas como coleta de dados, pré-processamento, treinamento de modelos, avaliação e implementação. Na Unidade V, esse processo será detalhado, explicando como trabalhar com IA desde a concepção até a aplicação, abordando as etapas cruciais e as ferramentas necessárias para um projeto bem-sucedido. Dessa forma, podemos conectar as abordagens teóricas que vimos até agora com a execução prática de soluções inovadoras.
