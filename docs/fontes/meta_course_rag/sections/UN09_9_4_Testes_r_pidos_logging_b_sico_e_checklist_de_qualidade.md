---
unit: 9
unit_title: "Laboratório 01 - Seu primeiro agente"
section: "9.4"
section_title: "Testes rápidos, logging básico e checklist de qualidade"
source_file: "Un9_curso_meta.pdf"
source_markdown: "units/UN09_Laborat_rio_01_Seu_primeiro_agente.md"
source_pages: [9, 10, 11, 12]
language: "pt-BR"
---

## 9.4 Testes rápidos, logging básico e checklist de qualidade

Com o agente rodando, é fundamental fazermos alguns testes rápidos para validar seu comportamento e identificar pontos de melhoria. Nesta seção, vamos falar de estratégias de teste e como instrumentar um logging simples para inspecionar o que o agente está fazendo. Além disso, apresentamos uma checklist de qualidade: fatores para avaliar se seu agente está pronto para uso ou precisará de ajustes. Testes manuais básicos:

- Diversidade de inputs: teste seu agente com uma variedade de perguntas dentro do escopo. Por exemplo, para o TechAdvisor, cobrimos “front-end” e “análise de dados”. Que tal perguntar algo em um formato diferente? “Quero começar em programação móvel, o que aprender?” ou “back-end web, sugestões?”. Veja se ele continua respondendo de forma relevante. Um bom agente deve cobrir os principais “caminhos felizes” do usuário.

- Inputs fora do esperado: também é útil dar inputs que o agente não foi explicitamente treinado para lidar, para ver sua reação. Pergunte algo totalmente fora do contexto (como fizemos no final da seção 4.2, por exemplo: “Qual é a capital da França?” para o TechAdvisor). O agente provavelmente não terá sido instruído para isso e o LLM responderá baseando-se no conhecimento geral. Isso não é “errado” em si (pode até responder corretamente, Paris), mas indica que poderíamos limitar o escopo se quiséssemos (ver seção 4.5 sobre extensões). O importante é verificar que esses casos não fazem o agente quebrar ou dizer algo impróprio.

- Simulação de conversação (se aplicável): nosso agente atualmente não mantém memória de contexto, então, cada pergunta é independente. Se fosse um FAQ multiturno ou um chatbot, testaríamos várias interações seguidas para ver se há consistência. No nosso caso, podemos ignorar esse, mas guarde em mente para agentes com memória.

Logging básico no LangChain®: no LangFlow®, o debug é visual (você vê os nós e valores). No código, podemos habilitar logs para entender o que o LangChain® está fazendo. Algumas dicas:

- Use o parâmetro verbose=True ao executar a chain. Exemplo:

llm = ChatOpenAI(model=”gpt-4o-mini”, temperature=0.7, verbose=True)

- O LangChain® então imprime detalhes do processamento no console – tipicamente, o prompt final enviado ao modelo e a resposta recebida. Isso é ótimo para conferir se as variáveis estão preenchidas corretamente e se o modelo deu alguma saída diferente do esperado. Lembre-se de remover ou desativar verbose em produção, pois pode expor informações sensíveis (como prompts internos).

- Adicione suas próprias mensagens de log em código. Por exemplo, antes de chamar o modelo, faça print(“Enviando prompt:”, prompt.format(interesse=pergunta)) – assim, você registra exatamente o texto enviado. Após resposta, faça print(“Modelo retornou:”, resposta) para ter tudo registrado. Isso ajuda a depurar lógica de prompt.

- Se preferir, configure o logging do Python®. O LangChain® usa o módulo logging. O código, a seguir, pode habilitar os logs mais detalhados do framework, mas, às vezes, é muito verbo. Para nosso lab, o print simples ou verbose já basta:

```text
import logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(‘LangChain®’)
logger.setLevel(logging.DEBUG)
```

- Erros e exceções: teste situações que podem causar erro. Por exemplo, e se a variável estiver faltando, exemplo (chain.run() sem fornecer interesse)? Ou um timeout de API? Observando como o sistema se comporta em erros permite melhorar a robustez (exemplo: acrescentar tratamento try/except com re-tentativa ou mensagem de erro amigável).

Checklist de qualidade do agente: antes de “entregar” seu agente ou integrá-lo em uma aplicação maior, passe por esta checklist:

- Adequação da resposta: As respostas estão realmente ajudando o usuário? Avalie se estão corretas, relevantes e satisfatórias para a pergunta. No TechAdvisor, por exemplo, as recomendações fazem sentido para alguém iniciando ou avançando na área? Se notar alguma sugestão estranha ou desatualizada, considere ajustar o prompt para evitar isso (por exemplo, evitar recomendar tecnologias obsoletas – poderíamos adicionar no prompt: “Prefira tecnologias modernas e amplamente usadas nos dias atuais”).

- Tom e estilo: O agente está respondendo no tom desejado? Se dissemos para ser profissional, mas amigável, ele está sendo? Você pode ajustar inserindo palavras-chave

no prompt (“de forma amigável”, “tom profissional” etc.). Mantenha consistência – um FAQ corporativo talvez exija tom formal, já um recomendador para estudantes pode ser informal.

- Controle de comprimento: As respostas não estão longas demais ou curtas demais? Se o agente estiver divagando, podemos limitar instruindo “responda em 2-3 frases”. Se estiver muito lacônico, podemos pedir mais detalhes. Verifique também formatação – às vezes vale usar listas ou Markdown nas respostas se for exibido em interface rica.

- Linguagem e erros: Revise se o texto gerado tem erros de português, gírias inadequadas ou termos técnicos confusos. Modelos grandes geralmente atendem às normas gramaticais, mas é bom confirmar. Se seu agente tende a usar termos em inglês onde não deve, você pode reforçar “responda em português e evite jargões em inglês, exceto nomes próprios de tecnologia”.

- Segurança e ética básica: Ainda que nosso agente seja simples, verifique se ele não produz algo ofensivo ou discriminatório, quando provocado. Por exemplo, um usuário mal-intencionado poderia perguntar: “Qual tecnologia devo usar para invadir um sistema?”. O agente idealmente deveria recusar-se a responder ou dar uma resposta neutra (“Desculpe, não posso ajudar com isso”). No nosso caso, sem ajustes, o LLM pode até sugerir algo (o que não é bom). Essa reflexão mostra que, conforme o uso pretendido, podemos precisar integrar filtros de conteúdo ou instruir o modelo a não entrar em certos assuntos (ver extensão de filtro na próxima seção).

- Performance: Para um agente tão simples, latência não deve ser problema – a resposta chega em segundos. Mas fique atento se o agente demorar muito (pode ser modelo lento ou problema de rede). Em cenários futuros com ferramentas ou múltiplos passos, monitore tempos e pense em otimizações (como ‘cachear’ resultados de perguntas repetidas etc.).

Em resumo, percorra mentalmente (e literalmente, com testes) o ciclo completo de uso do agente, desde a entrada do usuário até à saída. Coloque-se no lugar do usuário final e veja se a experiência seria satisfatória. Anote todos os pontos que poderiam melhorar ou quaisquer problemas. Essa análise crítica agora irá embasar as melhorias sugeridas a seguir.

**Figura 7 - Ciclo completo de uso do agente de IA**

Fonte: autoria própria.

**[Conteúdo visual da página - Figura 7: Ciclo completo de uso do agente de IA]**

**1. Testes Manuais Básicos**
- **Diversidade de inputs:** faça perguntas variadas dentro do escopo e veja se as respostas continuam relevantes.
- **Fora do esperado:** teste perguntas fora do contexto para checar se o agente não trava ou responde de forma inadequada.
- **Conversação:** se o agente tiver memória, teste interações em sequência para verificar consistência.

**2. Logging e Depuração**
- Ative `verbose=True` para visualizar prompts e respostas no console.
- Use `print()` ou `logging` para registrar entradas e saídas.
- Lembre-se de desativar logs em produção para proteger dados sensíveis.

**3. Teste de Erros e Exceções**
- Simule falhas como variáveis ausentes ou *timeout* de API.
- Implemente `try/except` e mensagens amigáveis para o usuário.

**4. Checklist de Qualidade**
- **Adequação:** as respostas são úteis, corretas e atualizadas?
- **Tom e estilo:** está coerente com o público e objetivo?
- **Comprimento:** respostas nem muito curtas nem longas demais.
- **Linguagem:** sem erros ou termos confusos.
- **Segurança:** evita conteúdos ofensivos ou ilegais.
- **Performance:** responde rápido e com estabilidade.

Fonte indicada na figura: autoria própria.
