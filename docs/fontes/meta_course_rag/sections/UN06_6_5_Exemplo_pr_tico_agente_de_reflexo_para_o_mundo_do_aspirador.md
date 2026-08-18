---
unit: 6
unit_title: "Fundamentos de sistemas de agentes"
section: "6.5"
section_title: "Exemplo prático: agente de reflexo para o mundo do aspirador"
source_file: "Un6_curso_meta.pdf"
source_markdown: "units/UN06_Fundamentos_de_sistemas_de_agentes.md"
source_pages: [11, 12]
language: "pt-BR"
---

## 6.5 Exemplo prático: agente de reflexo para o mundo do aspirador

Para ilustrar alguns conceitos vistos na seção anterior, vamos considerar um exemplo clássico de agente e implementar uma versão simplificada em Python®. O cenário é o mundo do aspirador de pó: imagine um ambiente com duas salas (A e B), que podem estar limpas ou sujas, e um aspirador robô que pode se mover entre as salas e limpar sujeira. Esse exemplo é frequentemente usado em IA introdutória para demonstrar agentes reflexivos e ambientes parcialmente observáveis. Suponha um agente de reflexo simples para esse mundo. Ele percebe duas coisas: sua localização atual (A ou B) e o estado daquela sala (“limpo” ou “sujo”). Com base apenas nessa percepção imediata, ele deve decidir entre três ações possíveis: mover para a esquerda, mover para a direita ou aspirar. Uma estratégia simples (e racional, nesse caso) é:

- Se a sala atual estiver suja, a ação escolhida deve ser aspirar (limpar) imediatamente;

- Se a sala atual estiver limpa, então vá para a próxima sala (por exemplo, da A vá para B, ou da B volte para a sala A) para verificar se há sujeira lá.

Podemos codificar esse comportamento em uma função Python bem simples:

```text
def agente_reflexo_simples(local, situacao):
“””Decide a ação com base na localização e situação atual (limpo/sujo).”””
if situacao == ‘sujo’:
return ‘aspirar’
elif local == ‘A’:
return ‘direita’ # sala A está limpa, mover para B
else:
return ‘esquerda’ # sala B está limpa, mover para A
# Exemplos de uso da função:
print(agente_reflexo_simples(‘A’, ‘sujo’)) # Saída esperada: ‘aspirar’ (se em A
e está sujo, limpa)
print(agente_reflexo_simples(‘A’, ‘limpo’)) # Saída esperada: ‘direita’ (se em A
e limpo, vai para B)
print(agente_reflexo_simples(‘B’, ‘limpo’)) # Saída esperada:
‘esquerda’ (se em B e limpo, volta para A)
```

Esse agente é “míope” no sentido de que ele não guarda memória do que já ocorreu nem planeja adiante – ele simplesmente reage: limpe se sujo, senão, vá para o outro lado. Ainda assim, essa regra simples é suficiente para eventualmente limpar as duas salas, se o mundo for estático. No entanto, podemos pensar em melhorias: e se ao chegar na sala B o agente descobri-la limpa também? No código acima, ele voltaria para A imediatamente. Ficaria então em um loop A→B→A mesmo que tudo esteja limpo (nosso código não prevê uma condição de parada). Poderíamos introduzir alguma regra adicional, como “se ambas as salas estiverem limpas, então parar”, mas note que para saber que ambas estão limpas o agente já precisaria ter memória (“lembrar” que a outra sala estava limpa quando saiu de lá). Essa exigência o tiraria da categoria de reflexo simples e o colocaria na categoria baseado em modelo (pois precisaria armazenar o fato observado anteriormente). Apesar de simples, o exemplo do aspirador ilustra bem as diferenças de arquiteturas de agentes. Um agente reflexivo simples funciona com regras fixas e sem estado interno, adequado para ambientes pequenos, totalmente observáveis e estáticos. Já um agente com modelo interno poderia, por exemplo, “memorizar” quais salas já foram limpas para evitar trabalho repetido. Um agente baseado em objetivos poderia ter como objetivo “limpar ambas as salas” e então planejar uma sequência de ações que garanta isso, talvez evitando voltar desnecessariamente a uma sala já limpa. Poderíamos até incorporar o aprendizado, fazendo o agente perceber padrões (por exemplo, se uma sala suja tende a ficar suja novamente após certo tempo, talvez por um fator externo, o agente poderia aprender a revisitar periodicamente em vez de parar completamente).
