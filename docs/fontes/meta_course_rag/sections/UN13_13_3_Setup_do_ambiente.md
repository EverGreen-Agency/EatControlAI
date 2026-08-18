---
unit: 13
unit_title: "Curso do Meta SDK (DAT)"
section: "13.3"
section_title: "Setup do ambiente"
source_file: "Un13_Material_de_apoio_Meta.pdf"
source_markdown: "units/UN13_Curso_do_Meta_SDK_DAT.md"
source_pages: [18, 19, 20, 21, 22, 23, 24, 25, 26]
language: "pt-BR"
---

## 13.3 Setup do ambiente

### 13.3.1 Objetivos de aprendizagem

Ao final, você será capaz de:

- Explicar como o Meta Wearables Device Access Toolkit (DAT) é distribuído via GitHub Packages e por que o Gradle precisa de autenticação para baixá-lo.

- Criar e configurar um personal access token (classic) do GitHub com o escopo read:packages, guardando-o de forma segura no projeto.

- Configurar o projeto Android para o DAT: repositório Maven no settings.gradle.kts, artefatos no libs.versions.toml e dependências no build.gradle.kts.

- Configurar o AndroidManifest.xml com as permissões exigidas, os metadados APPLICATION_ID e CLIENT_TOKEN (atestação) e o intent filter com URI scheme.

- Verificar os requisitos mínimos de ambiente (Android, Android Studio, óculos e app Meta AI) antes de começar a desenvolver.

### 13.3.2 Pré-requisitos

- Tópico 2.1 (Hardware e SDK) e 2.2 (Arquitetura) — você já conhece o papel do app companion e do DAT na arquitetura óculos ⇄ Bluetooth ⇄ Android.

- Tópico 1.3 (Fundamentos de Android) — projeto Android com Gradle (Kotlin DSL) e estrutura de módulos.

- Android Studio Flamingo ou mais recente instalado, e um dispositivo/emulador com Android 10 (API 29) ou superior. Verificar: requisitos de versão na página oficial de Setup, pois o DAT está em developer preview e eles podem mudar: https://wearables.developer.meta.com/docs/getting-started-toolkit/

- Uma conta no GitHub (necessária para gerar o token de acesso aos pacotes).

### 13.3.3 Conceito / fundamentação

#### 13.3.3.1 O que "setup" significa aqui

Diferente de uma biblioteca comum, o DAT envolve três partes que precisam estar alinhadas antes da primeira linha de código útil: o seu projeto Android (que baixa e referencia o SDK), o GitHub Packages (de onde o SDK é baixado, com autenticação) e o app Meta AI no celular (que faz a ponte com os óculos e valida a identidade do seu app) (Figura 20). Este tópico cobre a configuração do projeto; o registro do app junto ao Meta AI e a criação de sessão são assunto do tópico 2.4.

**Figura 20 – As três partes que o setup precisa alinhar**

Fonte: autoria própria.

#### 13.3.3.2 Requisitos mínimos do ambiente

Segundo a documentação oficial (na data de escrita):

- Android: 10 (API 29) ou superior — o mesmo requisito do app Meta AI.

- IDE: Android Studio Flamingo ou mais recente.

- Óculos suportados: Ray-Ban Meta (Gen 1 e Gen 2) e Meta Ray-Ban Display. Também é possível testar sem hardware usando o Mock Device Kit (tópico 2.7).

- App Meta AI: versão v254 ou superior, com developer mode habilitado.

- Firmware dos óculos: v20+ (Ray-Ban Meta) ou v21+ (Meta Ray-Ban Display).

Verificar: todos esses números mudam com frequência em developer preview. Confira a página de Setup e a matriz "Version Dependencies" antes do hackathon: https://wearables.developer.meta.com/docs/version-dependencies/ Nota 24: para habilitar o developer mode, abra o app Meta AI, vá em Settings > App Info e toque 5 vezes no número da versão do app — o toggle "Developer Mode" aparece. Sem ele, seu app em desenvolvimento não consegue conversar com os óculos.

#### 13.3.3.3 Distribuição via GitHub Packages

O DAT não está no Maven Central nem no Google Maven: ele é publicado no GitHub Packages, o registro de pacotes do GitHub, dentro do repositório oficial facebook/meta-wearables-dat-android. Para o Gradle, isso é só mais um repositório Maven — com uma diferença importante: o GitHub Packages exige autenticação para baixar pacotes, mesmo os públicos. É como um armário de materiais do laboratório: qualquer aluno pode pegar, mas precisa passar o crachá na porta.

Esse "crachá" é um personal access token (classic) — um PAT — da sua conta GitHub, com pelo menos o escopo read:packages (permissão de leitura de pacotes, nada mais). O token entra como senha nas credenciais do repositório Maven; o username não é necessário.

Atenção! O token é uma credencial pessoal. Ele nunca deve ir para o repositório Git do time — por isso a configuração usa local.properties (que já fica no .gitignore dos projetos Android) ou uma variável de ambiente.

#### 13.3.3.4 Identidade do app: APPLICATION_ID, CLIENT_TOKEN e atestação

Quando seu app pede acesso aos óculos, o app Meta AI precisa confiar nele. Esse processo de verificação de autenticidade chama-se atestação (attestation) e

usa dois valores declarados como <meta-data> no manifest: APPLICATION_ID e CLIENT_TOKEN. Ambos são obtidos ao registrar seu app no Wearables Developer Center (seção "Manage projects").

A assinatura do app (App Signature) não é obrigatória para a atestação, mas o Meta AI a usa para verificar a autenticidade; identificadores incorretos fazem a conexão falhar com erro.

Nota 25: a atestação não é usada em Developer Mode — apps em modo de desenvolvimento rodam com lógica local, sem canal de release. Nesse caso, você pode omitir os valores ou usar 0. Para o hackathon, é assim que seu time vai trabalhar.

#### 13.3.3.5 O caminho de volta: intent filter com URI scheme

Fluxos como registro e permissões acontecem dentro do app Meta AI: seu app envia o usuário para lá e, ao final, o Meta AI precisa devolvê-lo ao seu app. Ele faz isso abrindo uma URI com um scheme customizado (ex.: myexampleapp://...). Para que o Android saiba qual Activity abrir, você declara um intent filter com action VIEW, categorias DEFAULT e BROWSABLE, e o seu scheme. Sem esse intent filter, o usuário fica "preso" no app Meta AI e o fluxo nunca conclui.

Na prática: no hackathon, cada time terá seu próprio app companion. Escolha um scheme único e específico do seu app (ex.: meuappoculos) — schemes genéricos podem colidir com outros apps instalados no mesmo celular de teste, e aí o callback abre o app errado.

### 13.3.4 Na prática

Passo a passo para deixar o Gradle sincronizando com o DAT em um projeto Android existente (Kotlin DSL). O resultado deste tópico é o sync verde; inicializar o SDK e registrar o app são o tópico 2.4.

#### 13.3.4.1 Passo 1 — Crie o personal access token no GitHub

1. No GitHub, acesse Settings > Developer settings > Personal access tokens > Tokens (classic) > Generate new token (classic).

2. Marque apenas o escopo read:packages.

3. Copie o token gerado (formato ghp_...) — ele só é exibido uma vez.

Verificar: o caminho exato da UI do GitHub muda com o tempo; instruções oficiais em https://docs.github.com/en/authentication/keeping-your-account-and-data-secure /managing-your-personal-access-tokens Atenção! Tokens fine-grained nem sempre funcionam com GitHub Packages — a documentação do DAT pede explicitamente um token classic.

#### 13.3.4.2 Passo 2 — Guarde o token fora do controle de versão

Duas opções (escolha uma):

# local.properties (raiz do projeto — já ignorado pelo Git)

github_token=ghp_seu_token_aqui

**Código 2.3-01 · também no notebook companion, seção 2.3.4.2**

Ou, via variável de ambiente no terminal:

export GITHUB_TOKEN=ghp_seu_token_aqui ./gradlew installDebug # rodando a partir da raiz do projeto

**Código 2.3-02 · também no notebook companion, seção 2.3.4.2**

Atenção! Erro comum: colocar o token no gradle.properties e commitá-lo. O GitHub detecta tokens vazados em pushes e os revoga automaticamente, e o build de todo o time quebra. Use local.properties ou variável de ambiente, sempre.

#### 13.3.4.3 Passo 3 — Adicione o repositório Maven no settings.gradle.kts

▶ Código 2.3-03 – settings.gradle.kts — código completo no notebook companion (seção 2.3.4.3).

Nota 26: o repositório vai no settings.gradle.kts (dentro de dependencyResolutionManagement), não no build.gradle.kts do módulo — esse é o padrão moderno de resolução centralizada de dependências.

#### 13.3.4.4 Passo 4 — Declare os artefatos no libs.versions.toml

▶ Código 2.3-04 – gradle/libs.versions.toml — código completo no notebook companion (seção 2.3.4.4).

Nota 27: o material usa mwdat 0.8.0. Como o SDK é versionado, confira versões mais recentes em https://github.com/orgs/facebook/packages?repo_name=meta-wearables-dat-an droid

Os três artefatos: mwdat-core (núcleo do SDK), mwdat-camera (streaming e captura — tópico 2.5) e mwdat-mockdevice (dispositivo simulado — tópico 2.7).

#### 13.3.4.5 Passo 5 — Adicione as dependências no build.gradle.kts do app

▶ Código 2.3-05 – app/build.gradle.kts — código completo no notebook companion (seção 2.3.4.5).

#### 13.3.4.6 Passo 6 — Configure o AndroidManifest.xml

▶ Código 2.3-06 – Permissões usadas pelo DAT: comunicação Bluetooth com os óculos + rede — código completo no notebook companion (seção 2.3.4.6).

Os ${...} são manifest placeholders, preenchidos pelo Gradle:

▶ Código 2.3-07 – app/build.gradle.kts — código completo no notebook companion (seção 2.3.4.6).

Nota 28: como visto no 1.4, BLUETOOTH_CONNECT é permissão de runtime (API 31+): declarar no manifest não basta, o usuário precisa conceder em tempo de execução. O fluxo completo de permissões do DAT será visto no tópico 2.4. Na prática: sem BLUETOOTH_CONNECT concedida, o app companion não fala com os óculos — nem câmera, nem microfone, nem áudio. Esse é o primeiro item a checar quando "nada funciona" no dia do hackathon.

#### 13.3.4.7 Passo 7 — Sync e validação

No Android Studio: File > Sync Project with Gradle Files. Se o sync concluir sem erros e os artefatos mwdat-* aparecerem em External Libraries, o setup está pronto. O passo a passo completo depende de conta GitHub e das versões atuais dos pacotes.

Erro comum: o sync falha com 401 Unauthorized (ou Could not GET 'https://maven.pkg.github.com/...'). Causas típicas, na ordem em que valem a checagem: token ausente (esqueceu o local.properties ou a variável de ambiente), token sem o escopo read:packages, token expirado/revogado, ou nome da chave errado (github_token no arquivo vs. o que o settings.gradle.kts lê). Corrigido o token, rode o sync de novo — o Gradle não tenta sozinho.

### 13.3.5 Resumo / cheatsheet

- Requisitos: Android 10+, Android Studio Flamingo+, óculos Ray-Ban Meta (Gen 1/2) ou Meta Ray-Ban Display, app Meta AI v254+ com developer mode — ou Mock Device Kit (2.7).

- O DAT é distribuído via GitHub Packages: repositório Maven https://maven.pkg.github.com/facebook/meta-wearables-da t-android.

- GitHub Packages exige autenticação mesmo para pacotes públicos: PAT classic com escopo read:packages, usado como password (username vazio).

- Token fica em local.properties (github_token=...) ou na variável de ambiente GITHUB_TOKEN — nunca no Git.

- Repositório vai no settings.gradle.kts (dependencyResolutionManagement); versões e artefatos (mwdat-core, mwdat-camera, mwdat-mockdevice) no libs.versions.toml; implementation(...) no build.gradle.kts.

- Manifest: permissões BLUETOOTH, BLUETOOTH_CONNECT, INTERNET (+ CAMERA para mock device com câmera do celular).

- APPLICATION_ID e CLIENT_TOKEN (meta-data) servem à atestação; em Developer Mode podem ser 0.

- Intent filter com URI scheme próprio permite ao app Meta AI devolver o usuário ao seu app.

### 13.3.6 Referências

- SETUP — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/getting-started-toolkit/ — requisitos de plataforma, óculos suportados, versões mínimas e habilitação do developer mode.

- INTEGRATE WEARABLES DEVICE ACCESS TOOLKIT INTO YOUR ANDROID APP — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/build-integration-android/ — passo a passo oficial: manifest, Gradle, token e dependências (fonte dos trechos de configuração deste tópico).

- META-WEARABLES-DAT-ANDROID — REPOSITÓRIO OFICIAL NO GITHUB. Disponível em: https://github.com/facebook/meta-wearables-dat-android — código-fonte, packages publicados e sample app completo para comparação.

- MANAGING YOUR PERSONAL ACCESS TOKENS — GITHUB DOCS. Disponível em: https://docs.github.com/en/authentication/keeping-your-account-and-data-s ecure/managing-your-personal-access-tokens — criação e gerenciamento do token classic com escopos.

#### 13.3.6.1 Para ir além

MANAGE PROJECTS — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/manage-projects — onde registrar seu app e obter APPLICATION_ID e CLIENT_TOKEN para builds fora do Developer Mode.

VERSION DEPENDENCIES — META WEARABLES DEVELOPER CENTER. Disponível em: https://wearables.developer.meta.com/docs/version-dependenci es/ — matriz de compatibilidade entre SDK, app Meta AI e firmware dos óculos.
