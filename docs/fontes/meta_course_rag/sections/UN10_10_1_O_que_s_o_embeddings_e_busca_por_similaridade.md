---
unit: 10
unit_title: "Bases de dados vetoriais"
section: "10.1"
section_title: "O que são embeddings e busca por similaridade"
source_file: "Un10_curso_meta(1).pdf"
source_markdown: "units/UN10_Bases_de_dados_vetoriais.md"
source_pages: [2, 3, 4]
language: "pt-BR"
---

## 10.1 O que são embeddings e busca por similaridade

Embeddings vetoriais são representações numéricas (vetores) de dados como palavras, frases, imagens ou outros itens, de forma que semântica e contexto sejam capturados nos números (Elastic, 2025). Em outras palavras, um embedding mapeia informação não estruturada (texto, imagem etc.) para um ponto em um espaço multidimensional, de modo que itens com significados semelhantes fiquem próximos nesse espaço. Isso permite que máquinas comparem e processem conteúdo baseado em significado, e não apenas por correspondência exata de texto. Por exemplo, frases diferentes que querem dizer a mesma coisa gerarão embeddings próximos; imagens visualmente parecidas terão vetores similares, e assim por diante. Uma vez que dados foram convertidos em embeddings, podemos utilizar uma busca por similaridade vetorial para encontrar itens “próximos” uns dos outros. Mas como medir a similaridade ou distância entre vetores? Existem métricas consagradas para isso:

- Distância Euclidiana (L2): É a distância geométrica “reta” entre dois pontos no espaço vetorial (Pinecone, 2025). Matematicamente, é a raiz quadrada da soma das diferenças quadráticas entre cada componente (coordenada) dos vetores. Um vetor com todas coordenadas muito próximas às de outro vetor terá uma L2 pequena, indicando alta similaridade literal. Entretanto, a L2 é sensível à escala dos vetores: vetores de grande magnitude tendem a ter distâncias maiores, mesmo que a direção (padrão semântico) seja parecida. Por isso, a L2 costuma ser útil quando a magnitude dos vetores também carrega informação relevante (por exemplo, contagens ou medidas absolutas).

- Similaridade do cosseno: Mede o ângulo entre dois vetores, ignorando suas magnitudes (Pinecone, 2025). É calculada pelo cosseno do ângulo (theta) entre os vetores a e b: , onde é o produto interno (soma dos produtos dos componentes) e é a norma (comprimento) do vetor. O valor varia de 1 (vetores com mesmo sentido, a -1 (sentidos opostos, ), sendo 0 para vetores ortogonais (sem relação). Como a divisão

pelas normas normaliza os vetores, o cosseno reflete apenas a direção, não importando o tamanho (Odazie, 2024). Isso o torna muito usado em NLP e busca semântica, pois embeddings de textos, geralmente, são normalizados, e queremos comparar conceito em vez de quantidade. Por exemplo, duas frases com palavras diferentes mas significado parecido podem ter alto (~próximo de 1) mesmo que uma seja mais longa que a outra.

- Produto interno (ou produto escalar): É a soma das multiplicações componente a componente dos vetores (formalmente $a \cdot b = \sum_i a_i b_i$). Se os vetores estiverem normalizados (comprimento 1), o produto interno equivale à similaridade do cosseno (Pinecone, 2025). Caso contrário, o produto interno incorpora também as magnitudes: vetores maiores produzem dot products maiores, mesmo que o ângulo seja igual. Em muitos sistemas, especialmente modelos de linguagem treinados com dot product como métrica, utiliza-se diretamente o produto interno como medida de afinidade. Ele produz um escalar que cresce quanto mais alinhados (e maiores) forem os vetores. O dot product é simples e eficiente de computar, mas pode exigir normalização prévia dos embeddings para não enviesar por magnitude

Cada métrica tem usos específicos. Em busca vetorial, em geral normalizamos os embeddings e usamos similaridade de cosseno ou produto interno, pois queremos medir alinhamento sem penalizar comprimentos diferentes. A L2 pode ser preferida em dados nos quais a diferença absoluta é relevante, por exemplo, comparar características mensuráveis ou contagens. Muitas bibliotecas e bancos vetoriais permitem escolher a métrica adequada, conforme o caso de uso (Pinecone, 2025). Exemplo prático: suponha dois vetores de embedding simples a = [1, 2] e b = [2, 4]. Vamos calcular as métricas:

```text
import numpy as np
a = np.array([1, 2])
b = np.array([2, 4])
# Distância Euclidiana
l2 = np.linalg.norm(a - b)
# Produto Interno
dot = float(np.dot(a, b))
# Similaridade do Cosseno (produto interno dividido pelas normas)
cos = dot / (np.linalg.norm(a) * np.linalg.norm(b))
print(f”L2 = {l2:.3f}, Produto interno = {dot:.3f}, Cosseno = {cos:.3f}”)
```

No exemplo acima, b é exatamente o dobro de a, então a distância L2 será >0 (vetores distintos), o produto interno será positivo e grande (8.0) e a similaridade de cosseno será 1.0, indicando alinhamento perfeito (mesma direção). Esse experimento ilustra que o cosseno foca na orientação (aqui, idêntica), enquanto L2 e dot refletem também a magnitude. Em contextos de busca semântica, geralmente nos preocupamos mais com a orientação do vetor no espaço de embedding (por exemplo, o significado subjacente) do que com seu comprimento. Por isso, similaridade de cosseno ou produto interno de vetores normalizados são as escolhas padrão para comparar embeddings textuais ou de imagens (Odazie, 2024). Em contrapartida, se estivéssemos comparando embeddings onde a escala importa (por exemplo, vetores de características com contagens), poderíamos optar pela L2 ou produto interno sem normalizar os vetores. Em todos os casos, o importante é usar uma métrica coerente com a forma como os embeddings foram gerados e treinados (muitos modelos de embedding “preferem” uma métrica específica) (Pinecone, 2025).
