---
unit: 2
unit_title: "Principais abordagens e aplicações"
section: "2.3"
section_title: "Visão computacional"
source_file: "Un2_curso_meta.pdf"
source_markdown: "units/UN02_Principais_abordagens_e_aplica_es.md"
source_pages: [50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129]
language: "pt-BR"
---

## 2.3 Visão computacional

De acordo com a IBM® (2021), a visão computacional é uma área da IA dedicada a fazer com que máquinas sejam capazes de interpretar, analisar e compreender informações visuais extraídas de imagens ou vídeos, fazendo com que as máquinas possam "enxergar" e compreender o mundo real. Inspirada nos processos de percepção humana, essa área integra técnicas de processamento de imagem, análise de padrões e aprendizado de máquina para extrair detalhes relevantes das imagens, objetos e/ou formas. Ao converter em dados o que a máquina "lê", os algoritmos conseguem detectar características, reconhecer elementos e, a partir disso, executar ações ou recomendações de maneira autônoma. O desenvolvimento dessa área começou a ganhar força nos anos 1960, quando pesquisadores tentavam compreender como o cérebro humano reconhece imagens para replicar esse processo em computadores. Foi em 1989 que Yann LeCun introduziu a LeNet® (capaz de reconhecer manuscritos), uma arquitetura que impactou diretamente no que hoje é conhecida como rede neural convolucional, uma técnica extremamente importante e que abriu as portas para diversos outros pesquisadores descobrirem novas aplicações com ela (Lecun et al., 1989). Esse campo tem experimentado crescimento acelerado graças aos avanços do hardware e ao surgimento de redes neurais profundas mais complexas, além de técnicas complementares, que permitem uma compreensão mais sofisticada de contextos visuais e tornam a interação entre computadores e ambientes físicos cada vez mais precisa.

Acesse o notebook no Google Colab®

Notebook Colab

a) Objetivos de Aprendizagem

Nesse notebook vamos aprender na prática (em código) os conceitos que foram ensinados nas aulas teóricas. Vamos explorar um pouco mais sobre as imagens digitais e operações comuns em projetos de Visão Computacional, além de treinarmos nossos primeiros modelos e por fim, aprender a importar e usar um modelo muito famoso conhecido como YOLO.

b) 🧠 Mini Curso de Visão Computacional para Iniciantes

😎 Introdução Bem-vindos ao nosso mini curso prático de Visão Computacional! 👀 Uma área fascinante da inteligência artificial que busca dar aos computadores a capacidade de "enxergar" e interpretar imagens e vídeos, semelhante ao sistema visual humano. Esta é uma das áreas mais impactantes da IA! Você já usou um filtro no Instagram? Ou desbloqueou o celular com o rosto? Talvez tenha visto carros que dirigem sozinhos? Tudo isso é possível graças à Visão Computacional! 👀 Nesta aula prática, vamos explorar os fundamentos da visão computacional através de exemplos práticos utilizando a biblioteca OpenCV, que é a mais popular para processamento de imagens e vídeos. Não se preocupe se você não tem experiência com programação avançada - nosso foco será em experimentar e entender os conceitos através da prática.

📚 O que você vai aprender hoje: ✅Utilizar a biblioteca OpenCV para manipular imagens. ✅Aplicar diferentes filtros e transformações em imagens. ✅Entender como as imagens são representadas digitalmente. ✅Experimentar um modelo simples de reconhecimento com o dataset MNIST.

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

axs[0, 1].imshow(metade) axs[0, 1].set_title(f'Metade do tamanho ({largura//2}x{altura//2})') axs[0, 1].axis('off')

axs[1, 0].imshow(dobro) axs[1, 0].set_title(f'Dobro do tamanho ({largura*2}x{altura*2})') axs[1, 0].axis('off')

axs[1, 1].imshow(proporcional) axs[1, 1].set_title(f'Proporcional ({nova_largura}x{nova_altura})') axs[1, 1].axis('off')

```text
plt.tight_layout()
plt.show()
```

**Figura 22 - Redimensionamentos de Imagem - Lena**

Fonte: autoria própria

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

**Figura 25 - Espelhamento e Rotação de Imagens - Lena**

Fonte: autoria própria

u) 🧪 Aplicando Filtros

🎯 O que são filtros? Filtros são operações aplicadas sobre os pixels da imagem para destacar informações importantes ou remover partes indesejadas (como ruídos ou detalhes irrelevantes).

🧠 Para que servem?

- 🧹 Remover ruídos (pontos ou manchas indesejadas);

- 🔍 Realçar bordas e contornos importantes;

- 💡 Suavizar ou intensificar certas regiões. Vamos começar com alguns dos filtros mais básicos e úteis — e ver como eles transformam a imagem de formas surpreendentes! 🎨🔬

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

**Figura 42 - Visualização Gráfica da Arquitetura**

Fonte: autoria própria

```text
modelo_cnn.compile(optimizer='adam',
loss='sparse_categorical_crossentropy',
metrics=['accuracy'])
```

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

!pip install ultralytics

Collecting ultralytics Downloading ultralytics-8.3.142-py3-none-any.whl.metadata (37 kB) Requirement already satisfied: numpy>=1.23.0 in /usr/local/lib/python3.11/distpackages (from ultralytics) (2.0.2) Requirement already satisfied: matplotlib>=3.3.0 in /usr/local/lib/python3.11/ dist-packages (from ultralytics) (3.10.0) Requirement already satisfied: opencv-python>=4.6.0 in /usr/local/lib/python3.11/ dist-packages (from ultralytics) (4.11.0.86) Requirement already satisfied: pillow>=7.1.2 in /usr/local/lib/python3.11/distpackages (from ultralytics) (11.2.1) Requirement already satisfied: pyyaml>=5.3.1 in /usr/local/lib/python3.11/distpackages (from ultralytics) (6.0.2) Requirement already satisfied: requests>=2.23.0 in /usr/local/lib/python3.11/distpackages (from ultralytics) (2.32.3) Requirement already satisfied: scipy>=1.4.1 in /usr/local/lib/python3.11/distpackages (from ultralytics) (1.15.3) Requirement already satisfied: torch>=1.8.0 in /usr/local/lib/python3.11/distpackages (from ultralytics) (2.6.0+cu124) Requirement already satisfied: torchvision>=0.9.0 in /usr/local/lib/python3.11/ dist-packages (from ultralytics) (0.21.0+cu124) Requirement already satisfied: tqdm>=4.64.0 in /usr/local/lib/python3.11/distpackages (from ultralytics) (4.67.1) Requirement already satisfied: psutil in /usr/local/lib/python3.11/dist-packages (from ultralytics) (5.9.5) Requirement already satisfied: py-cpuinfo in /usr/local/lib/python3.11/distpackages (from ultralytics) (9.0.0) Requirement already satisfied: pandas>=1.1.4 in /usr/local/lib/python3.11/distpackages (from ultralytics) (2.2.2) Collecting ultralytics-thop>=2.0.0 (from ultralytics) Downloading ultralytics_thop-2.0.14-py3-none-any.whl.metadata (9.4 kB) Requirement already satisfied: contourpy>=1.0.1 in /usr/local/lib/python3.11/distpackages (from matplotlib>=3.3.0->ultralytics) (1.3.2) Requirement already satisfied: cycler>=0.10 in /usr/local/lib/python3.11/distpackages (from matplotlib>=3.3.0->ultralytics) (0.12.1) Requirement already satisfied: fonttools>=4.22.0 in /usr/local/lib/python3.11/ dist-packages (from matplotlib>=3.3.0->ultralytics) (4.58.0) Requirement already satisfied: kiwisolver>=1.3.1 in /usr/local/lib/python3.11/ dist-packages (from matplotlib>=3.3.0->ultralytics) (1.4.8) Requirement already satisfied: packaging>=20.0 in /usr/local/lib/python3.11/distpackages (from matplotlib>=3.3.0->ultralytics) (24.2) Requirement already satisfied: pyparsing>=2.3.1 in /usr/local/lib/python3.11/distpackages (from matplotlib>=3.3.0->ultralytics) (3.2.3) Requirement already satisfied: python-dateutil>=2.7 in /usr/local/lib/python3.11/ dist-packages (from matplotlib>=3.3.0->ultralytics) (2.9.0.post0) Requirement already satisfied: pytz>=2020.1 in /usr/local/lib/python3.11/distpackages (from pandas>=1.1.4->ultralytics) (2025.2) Requirement already satisfied: tzdata>=2022.7 in /usr/local/lib/python3.11/distpackages (from pandas>=1.1.4->ultralytics) (2025.2) Requirement already satisfied: charset-normalizer<4,>=2 in /usr/local/lib/ python3.11/dist-packages (from requests>=2.23.0->ultralytics) (3.4.2) Requirement already satisfied: idna<4,>=2.5 in /usr/local/lib/python3.11/distpackages (from requests>=2.23.0->ultralytics) (3.10) Requirement already satisfied: urllib3<3,>=1.21.1 in /usr/local/lib/python3.11/ dist-packages (from requests>=2.23.0->ultralytics) (2.4.0) Requirement already satisfied: certifi>=2017.4.17 in /usr/local/lib/python3.11/ dist-packages (from requests>=2.23.0->ultralytics) (2025.4.26) Requirement already satisfied: filelock in /usr/local/lib/python3.11/dist-packages (from torch>=1.8.0->ultralytics) (3.18.0) Requirement already satisfied: typing-extensions>=4.10.0 in /usr/local/lib/ python3.11/dist-packages (from torch>=1.8.0->ultralytics) (4.13.2) Requirement already satisfied: networkx in /usr/local/lib/python3.11/dist-packages (from torch>=1.8.0->ultralytics) (3.4.2) Requirement already satisfied: jinja2 in /usr/local/lib/python3.11/dist-packages (from torch>=1.8.0->ultralytics) (3.1.6) continua continua

Requirement already satisfied: fsspec in /usr/local/lib/python3.11/dist-packages (from torch>=1.8.0->ultralytics) (2025.3.2) Collecting nvidia-cuda-nvrtc-cu12==12.4.127 (from torch>=1.8.0->ultralytics) Downloading nvidia_cuda_nvrtc_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl. metadata (1.5 kB) Collecting nvidia-cuda-runtime-cu12==12.4.127 (from torch>=1.8.0->ultralytics) Downloading nvidia_cuda_runtime_cu12-12.4.127-py3-none-manylinux2014_x86_64. whl.metadata (1.5 kB) Collecting nvidia-cuda-cupti-cu12==12.4.127 (from torch>=1.8.0->ultralytics) Downloading nvidia_cuda_cupti_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl. metadata (1.6 kB) Collecting nvidia-cudnn-cu12==9.1.0.70 (from torch>=1.8.0->ultralytics) Downloading nvidia_cudnn_cu12-9.1.0.70-py3-none-manylinux2014_x86_64.whl. metadata (1.6 kB) Collecting nvidia-cublas-cu12==12.4.5.8 (from torch>=1.8.0->ultralytics) Downloading nvidia_cublas_cu12-12.4.5.8-py3-none-manylinux2014_x86_64.whl. metadata (1.5 kB) Collecting nvidia-cufft-cu12==11.2.1.3 (from torch>=1.8.0->ultralytics) Downloading nvidia_cufft_cu12-11.2.1.3-py3-none-manylinux2014_x86_64.whl. metadata (1.5 kB) Collecting nvidia-curand-cu12==10.3.5.147 (from torch>=1.8.0->ultralytics) Downloading nvidia_curand_cu12-10.3.5.147-py3-none-manylinux2014_x86_64.whl. metadata (1.5 kB) Collecting nvidia-cusolver-cu12==11.6.1.9 (from torch>=1.8.0->ultralytics) Downloading nvidia_cusolver_cu12-11.6.1.9-py3-none-manylinux2014_x86_64.whl. metadata (1.6 kB) Collecting nvidia-cusparse-cu12==12.3.1.170 (from torch>=1.8.0->ultralytics) Downloading nvidia_cusparse_cu12-12.3.1.170-py3-none-manylinux2014_x86_64.whl. metadata (1.6 kB) Requirement already satisfied: nvidia-cusparselt-cu12==0.6.2 in /usr/local/lib/ python3.11/dist-packages (from torch>=1.8.0->ultralytics) (0.6.2) Requirement already satisfied: nvidia-nccl-cu12==2.21.5 in /usr/local/lib/ python3.11/dist-packages (from torch>=1.8.0->ultralytics) (2.21.5) Requirement already satisfied: nvidia-nvtx-cu12==12.4.127 in /usr/local/lib/ python3.11/dist-packages (from torch>=1.8.0->ultralytics) (12.4.127) Collecting nvidia-nvjitlink-cu12==12.4.127 (from torch>=1.8.0->ultralytics) Downloading nvidia_nvjitlink_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl. metadata (1.5 kB) Requirement already satisfied: triton==3.2.0 in /usr/local/lib/python3.11/distpackages (from torch>=1.8.0->ultralytics) (3.2.0) Requirement already satisfied: sympy==1.13.1 in /usr/local/lib/python3.11/distpackages (from torch>=1.8.0->ultralytics) (1.13.1) Requirement already satisfied: mpmath<1.4,>=1.1.0 in /usr/local/lib/python3.11/ dist-packages (from sympy==1.13.1->torch>=1.8.0->ultralytics) (1.3.0) Requirement already satisfied: six>=1.5 in /usr/local/lib/python3.11/dist-packages (from python-dateutil>=2.7->matplotlib>=3.3.0->ultralytics) (1.17.0) Requirement already satisfied: MarkupSafe>=2.0 in /usr/local/lib/python3.11/distpackages (from jinja2->torch>=1.8.0->ultralytics) (3.0.2) Downloading ultralytics-8.3.142-py3-none-any.whl (1.0 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 1.0/1.0 MB 28.2 MB/s eta 0:00:00 Downloading nvidia_cublas_cu12-12.4.5.8-py3-none-manylinux2014_x86_64.whl (363.4 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 363.4/363.4 MB 2.8 MB/s eta 0:00:00 Downloading nvidia_cuda_cupti_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl (13.8 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 13.8/13.8 MB 76.9 MB/s eta 0:00:00 Downloading nvidia_cuda_nvrtc_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl (24.6 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 24.6/24.6 MB 61.6 MB/s eta 0:00:00 Downloading nvidia_cuda_runtime_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl (883 kB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 883.7/883.7 kB 28.1 MB/s eta 0:00:00 Downloading nvidia_cudnn_cu12-9.1.0.70-py3-none-manylinux2014_x86_64.whl (664.8 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 664.8/664.8 MB 3.0 MB/s eta 0:00:00 Downloading nvidia_cufft_cu12-11.2.1.3-py3-none-manylinux2014_x86_64.whl (211.5 MB) continua continua

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 211.5/211.5 MB 6.1 MB/s eta 0:00:00 Downloading nvidia_curand_cu12-10.3.5.147-py3-none-manylinux2014_x86_64.whl (56.3 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 56.3/56.3 MB 17.0 MB/s eta 0:00:00 Downloading nvidia_cusolver_cu12-11.6.1.9-py3-none-manylinux2014_x86_64.whl (127.9 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 127.9/127.9 MB 7.7 MB/s eta 0:00:00 Downloading nvidia_cusparse_cu12-12.3.1.170-py3-none-manylinux2014_x86_64.whl (207.5 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 207.5/207.5 MB 6.9 MB/s eta 0:00:00 Downloading nvidia_nvjitlink_cu12-12.4.127-py3-none-manylinux2014_x86_64.whl (21.1 MB) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 21.1/21.1 MB 38.7 MB/s eta 0:00:00 Downloading ultralytics_thop-2.0.14-py3-none-any.whl (26 kB) Installing collected packages: nvidia-nvjitlink-cu12, nvidia-curand-cu12, nvidiacufft-cu12, nvidia-cuda-runtime-cu12, nvidia-cuda-nvrtc-cu12, nvidia-cuda-cupti- cu12, nvidia-cublas-cu12, nvidia-cusparse-cu12, nvidia-cudnn-cu12, nvidiacusolver-cu12, ultralytics-thop, ultralytics Attempting uninstall: nvidia-nvjitlink-cu12 Found existing installation: nvidia-nvjitlink-cu12 12.5.82 Uninstalling nvidia-nvjitlink-cu12-12.5.82: Successfully uninstalled nvidia-nvjitlink-cu12-12.5.82 Attempting uninstall: nvidia-curand-cu12 Found existing installation: nvidia-curand-cu12 10.3.6.82 Uninstalling nvidia-curand-cu12-10.3.6.82: Successfully uninstalled nvidia-curand-cu12-10.3.6.82 Attempting uninstall: nvidia-cufft-cu12 Found existing installation: nvidia-cufft-cu12 11.2.3.61 Uninstalling nvidia-cufft-cu12-11.2.3.61: Successfully uninstalled nvidia-cufft-cu12-11.2.3.61 Attempting uninstall: nvidia-cuda-runtime-cu12 Found existing installation: nvidia-cuda-runtime-cu12 12.5.82 Uninstalling nvidia-cuda-runtime-cu12-12.5.82: Successfully uninstalled nvidia-cuda-runtime-cu12-12.5.82 Attempting uninstall: nvidia-cuda-nvrtc-cu12 Found existing installation: nvidia-cuda-nvrtc-cu12 12.5.82 Uninstalling nvidia-cuda-nvrtc-cu12-12.5.82: Successfully uninstalled nvidia-cuda-nvrtc-cu12-12.5.82 Attempting uninstall: nvidia-cuda-cupti-cu12 Found existing installation: nvidia-cuda-cupti-cu12 12.5.82 Uninstalling nvidia-cuda-cupti-cu12-12.5.82: Successfully uninstalled nvidia-cuda-cupti-cu12-12.5.82 Attempting uninstall: nvidia-cublas-cu12 Found existing installation: nvidia-cublas-cu12 12.5.3.2 Uninstalling nvidia-cublas-cu12-12.5.3.2: Successfully uninstalled nvidia-cublas-cu12-12.5.3.2 Attempting uninstall: nvidia-cusparse-cu12 Found existing installation: nvidia-cusparse-cu12 12.5.1.3 Uninstalling nvidia-cusparse-cu12-12.5.1.3: Successfully uninstalled nvidia-cusparse-cu12-12.5.1.3 Attempting uninstall: nvidia-cudnn-cu12 Found existing installation: nvidia-cudnn-cu12 9.3.0.75 Uninstalling nvidia-cudnn-cu12-9.3.0.75: Successfully uninstalled nvidia-cudnn-cu12-9.3.0.75 Attempting uninstall: nvidia-cusolver-cu12 Found existing installation: nvidia-cusolver-cu12 11.6.3.83 Uninstalling nvidia-cusolver-cu12-11.6.3.83: Successfully uninstalled nvidia-cusolver-cu12-11.6.3.83 Successfully installed nvidia-cublas-cu12-12.4.5.8 nvidia-cuda-cupti-cu12-12.4.127 nvidia-cuda-nvrtc-cu12-12.4.127 nvidia-cuda-runtime-cu12-12.4.127 nvidia-cudnncu12-9.1.0.70 nvidia-cufft-cu12-11.2.1.3 nvidia-curand-cu12-10.3.5.147 nvidiacusolver-cu12-11.6.1.9 nvidia-cusparse-cu12-12.3.1.170 nvidia-nvjitlink-cu12-12.4.127 ultralytics-8.3.142 ultralytics-thop-2.0.14

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

Classes detectadas: tie, person

Realizando detecção de objetos na imagem 2...

0: 640x480 4 persons, 1 bus, 1 stop sign, 236.0ms Speed: 6.0ms preprocess, 236.0ms inference, 1.7ms postprocess per image at shape (1, 3, 640, 480)

**Figura 50 - Detecção Yolo 2**

Fonte: autoria própria

Classes detectadas: stop sign, person, bus

Realizando detecção de objetos na imagem 3...

0: 352x640 (no detections), 247.4ms Speed: 5.1ms preprocess, 247.4ms inference, 1.5ms postprocess per image at shape (1, 3, 352, 640)

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

**Figura 52 - Detecção Yolo 3**

Fonte: autoria própria

**[Conteúdo visual da página - Figura 52: Detecção Yolo 3]**

A figura compara a imagem original com as detecções YOLO. As caixas exibidas incluem: `person: 0.90`, `person: 0.91`, `tv: 0.32`, `chair: 0.81`, `chair: 0.30` e `dining table: 0.31`. Fonte indicada na figura: autoria própria.

### 2.3.1 Conceitos e fundamentos

A base da Visão Computacional é transformar informações visuais (imagem ou vídeo) em dados que possam ser processados por algoritmos. Lembrando que um vídeo nada mais é que uma sequência de imagens sendo exibidas para você a uma taxa de quadros por segundo (FPS), ou seja, um vídeo de 30 FPS são 30 imagens passando por segundo na sua tela. A imagem, antes de ser entendida pelo computador, é decomposta em pixels, que é a menor unidade de uma imagem. Esses pixels possuem informações cruciais para os modelos, relacionados à cor e intensidade de luz de cada um. Um aspecto fundamental para a área é o entendimento de como as cores são representadas digitalmente. Em um computador, as imagens costumam ser codificadas em canais de cor, que nada mais são do que componentes que descrevem a intensidade de cada cor primária em cada pixel. Existem diversos tipos de modelos de cor, porém focaremos no mais comum que é o RGB (Red, Green, Blue). Cada cor primária aqui representa um canal que recebe um valor numérico que indica o quanto daquela cor específica há no pixel. Esse valor numérico varia de 0 a 255, sendo 0 a ausência da cor e 255 o valor máximo de intensidade dessa cor no pixel. Na prática, podemos pensar em uma imagem na resolução de 1280 x 720 pixels que é o padrão conhecido como resolução HD (High-Definition). Isso significa que essa imagem possui 1280 pixels de altura e 720 pixels de comprimento (ou vice e versa) totalizando 921.600 pixels. Cada pixel, por sua vez, é descrito pelos valores dos três canais (Red, Green e Blue). Por exemplo, se o canal Red de um pixel tem o valor 255, o canal Green tem o valor 0 e o canal Blue também 0, esse pixel específico na imagem representa a cor vermelha intensa. Se todos os canais tiverem valores elevados, o pixel aparecerá branco; se todos forem baixos, aparecerá preto. Dessa forma, quando se mistura uma quantidade adequada de vermelho, verde e azul em cada ponto, é possível representar uma ampla gama de cores, de forma semelhante à percepção humana. A intensidade de cada canal misturado aos outros dois cria um total de 16,7 milhões de combinações possíveis. A partir daí, os algoritmos aplicam métodos matemáticos e estatísticos para detectar linhas, formas, cores e padrões. Na Figura 53 é apresentada a formação de uma imagem digital a partir dos canais RGB.

**Figura 53 - Formação da imagem digital a partir dos canais de cor RGB por pixel**

Fonte: Adaptada de Vale (2021).

A questão de reconhecimento de padrões é outro conceito importante, permitindo que computadores aprendam a identificar características visuais (como formas, texturas ou cores) e classifiquem imagens em categorias específicas, por exemplo, aprendendo quais características classificam um gato e que o diferenciam de um cachorro. Esse processo é feito com a ajuda de grandes bases de dados e algoritmos de aprendizado de máquina envolvendo as redes neurais convolucionais (do inglês, Convolutional Neural Networks – CNNs), que "aprendem" a associar características visuais a determinados objetos detectando padrões em diversos locais da imagem (NNMOC Book). Essas redes fazem operações chamadas de convolução aliadas a aplicações de filtros que varrem uma imagem, analisando pequenos blocos de pixels por vez e destacando traços importantes daquele objeto. Conforme a rede se aprofunda, camadas sucessivas aprendem padrões cada vez mais complexos, combinando as características detectadas em níveis anteriores. Para exemplificar esse reconhecimento de padrões vamos utilizar a imagem apresentada na Figura 54 em que temos o número 8 em uma resolução de 22x16 pixels. A imagem está em uma escala de cor dita como escala de cinza, que possui apenas um canal, diferentemente do RGB que são 3. Esse único canal recebe também valores de 0 a 255 (0 sendo a ausência e 255 a intensidade máxima da cor), porém a única cor presente é o cinza em diversas tonalidades.

**Figura 54 - Imagem digital representada por matrizes de pixels e padrões numéricos**

Fonte: Adaptada de Mota (2018).

Na imagem é possível ver a análise por blocos de pixels. Os valores contidos nesses blocos são os que são passados para que a rede aprenda as características desse número. Dessa forma, ao receber uma outra imagem com o dígito 8, todas as operações são feitas novamente para extrair as características dessa nova imagem e, então, a rede compara com o conhecimento que ela possui e reconhece o padrão, conseguindo classificá-lo de forma correta. Apesar dos avanços, a visão computacional ainda enfrenta desafios. Iluminação inadequada, variações de tamanho, posição dos objetos e a presença de ruídos (interferências na imagem) podem comprometer o desempenho dos algoritmos. Logo, quanto maior a variedade de dados fornecidos para a rede, melhor ela performa em um ambiente de testes reais, ou seja, fora de um ambiente controlado.

### 2.3.2 Aplicações de visão computacional

A visão computacional engloba diversas tarefas (tasks) que permitem aos computadores interpretarem informações visuais de modo cada vez mais próximo ao olhar humano. Entre essas tarefas, destacam-se a segmentação, que consiste em separar a imagem em regiões com características semelhantes; a classificação, onde cada imagem ou parte dela recebe um rótulo específico; e a detecção, que localiza a posição de determinados objetos ou padrões dentro da cena. Cada uma delas possui áreas derivadas, mas não cabe aqui aprofundar em cada uma. A imagem, apresentada na Figura 55, traz exemplos práticos dessas tarefas.

**Figura 55 - Exemplos de tarefas em visão computacional: classificação, detecção e segmentação de objetos**

Fonte: Adaptada de Christian (2022).

A visão computacional possui aplicações em uma ampla variedade de campos, por exemplo: na indústria do entretenimento, a realidade aumentada insere elementos digitais no ambiente real, aprimorando a experiência do usuário em jogos e aplicativos educacionais; o upscaling de imagens melhora a qualidade e a resolução de fotos ou vídeos antigos; em tarefas de automação, como em veículos autônomos, os algoritmos precisam identificar pedestres, obstáculos e faixas de trânsito para navegação segura; em contextos de Reconhecimento Óptico de Caracteres (OCR), em inglês Optical Character Recognition, converte-se texto impresso ou manuscrito em formato digital; na saúde, a análise de imagens médicas auxilia médicos na detecção precoce de doenças, enquanto o reconhecimento facial oferece soluções de segurança e verificação de identidade. Em síntese, a visão computacional é fundamental para que máquinas e sistemas inteligentes sejam capazes de interpretar o mundo visual ao seu redor. A evolução das técnicas de processamento de imagens e aprendizado de máquina possibilitou a

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

Carros autônomos: Além de lidar com radares e sensores de proximidade, veículos autônomos têm câmeras que "enxergam" semáforos, placas de trânsito e pedestres, demonstrando como a visão computacional se torna essencial para a mobilidade do futuro.

Já pensou em criar seu próprio modelo de visão computacional em minutos? A ferramenta do Google® chamada de Teachable Machine permite treinar modelos de classificação de imagens diretamente no navegador, sem a necessidade de programação avançada. É uma ótima forma de entender como a máquina "aprende" a identificar objetos. Confira no endereço a seguir: https://teachablemachine.withgoogle.com/

Quer entender de forma prática como os filtros de imagem atuam na visão computacional? Visite o site Setosa e explore uma demonstração interativa incrível! Essa ferramenta permite experimentar com diversos filtros (kernels) e visualizar, em tempo real, como cada filtro transforma uma imagem. Ideal para entusiastas que desejam aprofundar seus conhecimentos de maneira visual e dinâmica.

4.4 Processamento de áudio e voz

A voz é uma das formas mais naturais e poderosas de comunicação humana. Através dela, transmitimos não apenas informações objetivas, mas também emoções, intenções e até aspectos culturais. Seja em uma conversa, em uma música, ou até mesmo em uma simples interjeição como "ai" ao nos machucarmos, há uma enorme quantidade de significados embutidos nos sons que emitimos. É justamente por essa riqueza comunicativa que o processamento de áudio e voz se tornou uma área essencial dentro da IA. Cada vez mais, convivemos com sistemas que interpretam, traduzem ou geram voz de maneira automática. Esses sistemas já fazem parte do nosso cotidiano – criando legendas automáticas em vídeos, transformando nossa fala em texto, e até sintetizando vozes digitais para criar efeitos, dublagens ou auxiliar pessoas com deficiência.

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

- fp16=False força precisão simples (float32) no CPU. Se você estiver em GPU e preferir meio-precisão, pode remover esse parâmetro.

O dicionário result traz:

- result["language"]: idioma detectado automaticamente.

- result["text"]: o texto transcrito do seu áudio.

f) Ajustes opcionais

Para forçar o português, adicione language="pt" em transcribe(). Se for trabalhar sem GPU, prefira modelos menores (tiny, base) para economizar tempo. Em GPU, use fp16=True (ou omita fp16) para aproveitar a aceleração por meio-precisão.

### 2.4.1 Conceitos e fundamentos

Diferentemente dos humanos, computadores não têm a capacidade inata de compreender o som. Para uma máquina, o áudio é apenas uma sequência de variações de pressão no ar, capturada por sensores como microfones. Para que esse sinal seja compreendido por um sistema de IA, ele precisa ser convertido para uma forma numérica – um processo conhecido como representação digital do som. O som é convertido em uma série de amostras (amostragem), resultando em uma sequência de números que representam a intensidade do som ao longo do tempo. Em seguida, utilizamos técnicas como a Transformada de Fourier ou o Mel-Frequency Cepstral Coefficients (MFCCs) para extrair características relevantes do áudio. Essas representações mostram "como" o som se comporta no tempo e nas frequências – informações cruciais para que um modelo de IA entenda a estrutura da fala. Uma vez que temos essa representação vetorial, podemos alimentar redes neurais que aprendem padrões entre os sons e suas correspondências textuais. Para tarefas de Speech to Text (STT), o modelo aprende a mapear sequências de áudio para transcrições escritas. Já no caso de Text to Speech (TTS), o modelo aprende o caminho inverso: gerar um sinal sonoro a partir de um texto, sintetizando fala artificial com entonações realistas. Na Figura 56 são apresentadas as etapas de processamento de áudio para conversão fala-texto e texto-fala

**Figura 56 - Pipeline de processamento de áudio para conversão fala-texto e texto-fala**

Fonte: Autoria própria.

#### 2.4.1.1 Aplicações de processamento de áudio e voz

Hoje, os sistemas de processamento de áudio estão embutidos em uma ampla variedade de aplicações. Os assistentes virtuais, como a Alexa®, a Siri® e o Google Assistant®, são exemplos diretos de uso de tecnologias STT e TTS. Quando dizemos "Qual a previsão do tempo?", o sistema precisa

primeiro transcrever nossa fala em texto, interpretar o significado da frase (processamento de linguagem natural) e, então, responder com uma voz artificial. Outro exemplo relevante está nos serviços de acessibilidade: pessoas com deficiência auditiva se beneficiam de legendas geradas automaticamente, enquanto pessoas com deficiência na fala podem usar sintetizadores de voz para se comunicar. Com o crescimento das redes sociais baseadas em vídeo, como TikTok e Instagram Reels, recursos como geração automática de legendas, vozes narrativas e dublagens também passaram a ser ferramentas criativas indispensáveis. Nesses contextos, o áudio não é apenas um canal de comunicação, mas um componente artístico e expressivo. Além disso, áreas como tradução simultânea, atendimento automatizado, educação de idiomas e até biometria de voz (autenticação por características vocais) utilizam essas tecnologias para melhorar a experiência do usuário.

Agora é sua vez!

Acesse o notebook para colocar em prática o conteúdo adquirido e sintetizar o tópico de Processamento de áudio e voz.

Saiba mais…

Explorando como a IA vê o áudio

Para aprofundar os conceitos apresentados, disponibilizamos um notebook interativo no Google Colab® que permite visualizar o espectro de uma nota musical tocada por diferentes instrumentos. No exemplo, exploramos a nota C6 (dó na sexta oitava) e comparamos sua representação espectral quando tocada por um oboé e uma clarineta. continua continua

Apesar de ambos os instrumentos tocarem exatamente a mesma nota em termos de altura (frequência fundamental), o som que ouvimos é claramente distinto. Isso acontece por causa do timbre, uma característica sonora que depende das frequências harmônicas que acompanham a nota principal. O oboé, por exemplo, tende a produzir um som mais penetrante e "nasal", com harmônicos fortes e próximos, enquanto a clarineta tem um som mais aveludado e suave, com uma distribuição diferente de harmônicos – inclusive enfatizando os ímpares. Essas diferenças são visíveis quando analisamos a forma de onda e o espectro de frequência dos dois instrumentos. Por meio dessa visualização, conseguimos entender melhor como a IA pode aprender a identificar, classificar ou até sintetizar diferentes sons, mesmo que eles tenham a mesma frequência base.

Acesse o notebook no Google Colab®
