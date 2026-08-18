---
unit: 2
unit_title: "Principais abordagens e aplicações"
section: "2.1"
section_title: "Ciência de Dados e Mineração de Dados"
source_file: "Un2_curso_meta.pdf"
source_markdown: "units/UN02_Principais_abordagens_e_aplica_es.md"
source_pages: [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29]
language: "pt-BR"
---

## 2.1 Ciência de Dados e Mineração de Dados

A Ciência e a Mineração de Dados são áreas cruciais no mundo da IA, permitindo que transformemos grandes volumes de informações brutas em conhecimento útil e estratégico. Imagine que você tem um vasto oceano de informações – cada transação, cada clique, cada interação do cliente é como uma gota nesse oceano. Por meio da Ciência de Dados e da Mineração de Dados, podemos identificar padrões e extrair conhecimento que, à primeira vista, parecem ser dados aleatórios. Na realidade, os dados coletados e armazenados corretamente podem refletir comportamentos ou tendências humanas que, com certeza, serão úteis para alguém. Um exemplo palpável disso foi relatado no livro O Poder do Hábito: uma rede de supermercados norte-americana analisou os hábitos de compra de seus clientes e percebeu que determinadas combinações de produtos (como itens de cuidado pessoal, vitaminas e outros produtos relacionados) indicavam, com alta probabilidade, que uma cliente estava grávida. Com essa informação, a rede de supermercados podia direcionar conteúdos específicos para esse público.

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

<class 'pandas.core.frame.DataFrame'> RangeIndex: 2111 entries, 0 to 2110 Data columns (total 17 columns): # Column Non-Null Count Dtype --- ------ -------------- ----- 0 Gender 2111 non-null object 1 Age 2111 non-null int64 2 Height 2111 non-null float64 3 Weight 2111 non-null float64 4 family_history_with_overweight 2111 non-null object 5 FAVC 2111 non-null object 6 FCVC 2111 non-null float64 7 NCP 2111 non-null float64 8 CAEC 2111 non-null object 9 SMOKE 2111 non-null object 10 CH2O 2111 non-null float64 11 SCC 2111 non-null object 12 FAF 2111 non-null float64 13 TUE 2111 non-null float64 14 CALC 2111 non-null object 15 MTRANS 2111 non-null object 16 NObeyesdad 2111 non-null object dtypes: float64(7), int64(1), object(9) memory usage: 280.5+ KB

f) 💦 Limpeza e tratamento dos dados

df.isnull().sum()

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

<ipython-input-47-40a60a9aa827>:11: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['Gender'] = df['Gender'].replace({'Male': 0, 'Female': 1}) # Masculno agora é 0 e Feminino 1 <ipython-input-47-40a60a9aa827>:12: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['family_history_with_overweight'] = df['family_history_with_overweight']. replace({'no': 0, <ipython-input-47-40a60a9aa827>:15: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['FAVC'] = df['FAVC'].replace({'no': 0, 'yes': 1}) # Se consome alimento com alto teor de caloriadas: 0 para não e 1 para sim <ipython-input-47-40a60a9aa827>:16: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['SMOKE'] = df['SMOKE'].replace({'no': 0, 'yes': 1}) # Se fuma: 0 para não e 1 para sim <ipython-input-47-40a60a9aa827>:17: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['SCC'] = df['SCC'].replace({'no': 0, 'yes': 1}) # Se monitora as calórias ingeridas: 0 para não e 1 para sim <ipython-input-47-40a60a9aa827>:18: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['CAEC'] = df['CAEC'].replace({'no': 0, 'Sometimes': 1, 'Frequently': 2, 'Always': 3}) # Se belisca: 0 para não e 1 para sim <ipython-input-47-40a60a9aa827>:19: FutureWarning: Downcasting behavior in 'replace' is deprecated and will be removed in a future version. To retain the old behavior, explicitly call 'result.infer_objects(copy=False)'. To opt-in to the future behavior, set 'pd.set_option('future.no_silent_downcasting', True)' df['CALC'] = df['CALC'].replace({'no': 0, 'Sometimes': 1, 'Frequently': 2, 'Always': 3}) # Se consome alcool: 0 para não e 1 para sim

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

**Figura 8 - Importância das Features**

Fonte: autoria própria

Agora que temos um modelo com 93% de acurácia, sabemos quais são as colunas/ features importantes que determinam, com maior certeza, os fatores da obesidade. Isso parece interessante para apresentar em uma reunião, certo? Lembram-se das nossas perguntas iniciais?

- Quais são os principais fatores de estilo de vida que contribuem para a obesidade em Goiás? 🤔

- Existe alguma relação entre obesidade e histórico familiar, consumo de alimentos calóricos, frequência de atividade física, etc.? 📊

- Podemos identificar grupos de pessoas com maior risco de obesidade? 🔍

- Como podemos usar essas informações para criar políticas públicas mais eficazes? 💡

k) Communicate (Comunicar): Apresentando os Resultados 🗣️ 🗣️ 🗣️

A análise dos dados ainda não terminou. Podemos querer aprofundar a interpretação de algumas informações. Por exemplo, a coluna "Gênero" aparece como a quinta variável mais importante no modelo, o que nos leva a questionar: existe um equilíbrio entre homens e mulheres ou algum dos gêneros apresenta uma tendência

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

### 2.1.1 Conceitos e fundamentos

A Ciência de Dados é um campo multidisciplinar que integra habilidades de probabilidade, estatística, algoritmo, dentre outros, para transformar dados em informações valiosas. Em sua prática, o profissional utiliza um método que envolve compreender o problema, identificar padrões e contar uma história com os dados. A sigla AGEMC (Ask, Get, Explore, Model, Communication) caracteriza o método cíclico de extração de informações a partir de dados. Na Figura 10, estão ilustradas as cinco etapas fundamentais desta metodologia.

**Figura 10 - Método AGEMC.**

Fonte: Adaptada de Wickham & Grolemund (2017).

A descrição destas cinco etapas do AGEMC está apresentada na Figura 11.

**Figura 11 - Descrição das etapas do método AGEMC.**

Fonte: Adaptada de Wickham & Grolemund (2017).

Imagine um grupo de estudantes que deseja descobrir se o tempo de estudo influencia as notas em uma prova. Primeiro, eles formulam a pergunta central: "Será que alunos que estudam mais horas obtêm melhores notas?", depois coletam os dados registrando as horas estudadas e as notas em uma planilha. Em seguida, exploram os dados com gráficos e estatísticas para identificar padrões, aplicam uma regressão linear para quantificar a influência das horas estudadas nas notas e, por fim, comunicam os resultados por meio de um relatório ou apresentação que destaca a correlação encontrada e ressalta que outros fatores também podem impactar o desempenho. Na Figura 12, é apresentado um gráfico em que cada ponto azul (X) representa um aluno, relacionando suas horas de estudo e a nota obtida na prova. O gráfico ilustra uma tendência geral de que, conforme aumentam as horas de estudo, as notas tendem a melhorar, embora haja variações individuais devido a outros fatores.

**Figura 12 - Relação entre horas de estudo e nota na prova.**

Fonte: Autoria própria.

Já a Mineração de Dados, também conhecida como "Data Mining", é uma das etapas dentro da Ciência de Dados. Ela se concentra na descoberta de padrões, tendências e informações relevantes em grandes conjuntos de dados, usando técnicas de IA e estatística.

#### 2.1.1.1 Tratamento dos dados é muito importante!

Para que a Ciência de Dados e a Mineração de Dados produzam resultados confiáveis e úteis, o tratamento dos dados é fundamental. Dados brutos geralmente são incompletos, inconsistentes e ruidosos (contêm erros ou informações irrelevantes). O tratamento de dados envolve a limpeza, transformação e organização dos dados para garantir sua qualidade e adequação para análise.

#### 2.1.1.2 Pensando os dados de maneira estratégica

A Ciência de Dados e a Mineração de Dados não são apenas sobre técnicas e algoritmos; elas também exigem uma mentalidade estratégica. É preciso entender o problema que se quer resolver, definir os objetivos da análise e escolher as técnicas mais adequadas para cada situação. Além disso, é importante interpretar os resultados de forma crítica e comunicar os insights de forma clara e eficaz.

Agora é sua vez!

Acesse o notebook para colocar em prática o conteúdo adquirido e sintetizar o tópico de Ciência de Dados.

Saiba mais…

Imagine que um grupo de estudantes deseja saber qual a causa dos ataques de tubarões. Conversam com moradores de uma determinada ilha para entender como o problema ocorre. Os moradores da ilha relatam informações como: horário dos ataques, principais alvos (turistas) e o que comeram antes de entrar no mar. Acontece que a ilha é famosa por seu maravilhoso sorvete de chocolate, então todos os turistas consomem esse sorvete. Então, se coletarmos os dados de ataques de tubarão e consumo de sorvete podemos chegar à conclusão de que existe uma correlação entre essas duas variáveis. A Figura 13 ilustra essa correlação. Isso não é interessante?

**Figura 13 - Venda de sorvete versus ataques de tubarão**

Fonte: Anderson (2023). continua continua

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
