# Guia de identidade de assinatura release

Status: `PENDENTE DE EXECUÇÃO HUMANA`
Data: 18/08/2026
Escopo: criar, proteger, testar e custodiar a chave de assinatura de produção do Eat Control AI.

> Este guia não contém senhas, caminhos privados nem material de chave. Nada disso deve ser escrito
> neste arquivo, em outro arquivo do repositório, em issue, print ou chat.

## 1. Por que isto não foi gerado automaticamente

Gerar a chave é irreversível na prática. Quem controla o keystore controla a identidade do app:

- perder o arquivo ou as senhas impede publicar atualizações com a mesma identidade;
- se você distribui APK fora da Play Store, uma identidade nova exige que todos desinstalem e
  reinstalem o app;
- a assinatura precisa ser cadastrada no Meta Wearables Developer Center para o DAT autorizar.

Por isso as senhas devem ser digitadas por você, no seu terminal, e a decisão de custódia precisa
existir **antes** da criação.

## 2. As três identidades que não devem ser confundidas

| Identidade | Onde é usada hoje | Consequência de perder |
|---|---|---|
| Debug keystore | build debug atual, já cadastrado no Developer Center | baixa: pode ser recriado, mas a assinatura cadastrada muda |
| Upload key | assina o que você envia ao Google Play | média: o Google pode redefinir se você usa Play App Signing |
| App signing key | assina o que chega ao usuário final | alta: sem ela, e sem Play App Signing, não há atualização possível |

Se a distribuição for pela Play Store, o caminho recomendado é ativar o **Play App Signing**: você
mantém a chave de upload e o Google custodia a chave de assinatura final. A documentação do Play
exige que a chave de upload seja RSA de 2048 bits ou mais
([Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756)).
Para certificados de APK Android, a prática consolidada é validade de pelo menos 25 anos
([referência de certificado autoassinado](https://www.sectigo.com/knowledge-base/detail/create-a-self-signed-certificate-for-apk)).

Conteúdo das fontes externas foi parafraseado para conformidade com restrições de licenciamento.

## 3. Decisão de custódia — preencher antes de gerar

Sem esta tabela preenchida, não gere a chave.

| Campo | Valor a definir | Preenchido |
|---|---|---|
| Responsável primário pela chave | | [ ] |
| Responsável secundário (redundância) | | [ ] |
| Onde o `.jks` fica em repouso | | [ ] |
| Onde as senhas ficam | | [ ] |
| Onde fica a cópia de backup criptografada | | [ ] |
| Quem pode assinar um release | | [ ] |
| Como um responsável é revogado ao sair do projeto | | [ ] |
| Data da próxima conferência do backup | | [ ] |

Regras mínimas recomendadas:

- duas pessoas com acesso, nunca uma;
- keystore e senhas guardados em lugares diferentes;
- o keystore nunca entra no repositório, em anexo de e-mail ou em mensageiro;
- as senhas ficam em gerenciador de senhas da equipe, não em arquivo de texto;
- a cópia de backup é criptografada e verificada por restauração real.

### Escolha entre os dois modelos

**Modelo A — Play App Signing (recomendado para distribuição pública).**
Você gera apenas a chave de upload. Se ela for perdida, o suporte do Play pode redefinir. O risco
catastrófico praticamente desaparece.

**Modelo B — Distribuição direta de APK, sem Play.**
Você gera e guarda a única chave que existe. É o cenário do beta com o parceiro e do hackathon. Aqui
o backup testado deixa de ser boa prática e passa a ser requisito, porque não há suporte para
recuperar nada.

Para o Ideathon e o teste com o Ray-Ban do parceiro, o Modelo B é suficiente. Se depois houver
publicação na Play Store, ative o Play App Signing e trate a chave atual como chave de upload.

## 4. Gerar a chave

Escolha um diretório fora de pastas sincronizadas automaticamente com nuvem pública. O `.gitignore`
já ignora `*.jks` e `*.keystore`, mas o diretório `keystores/` dentro do projeto é aceitável apenas
porque está ignorado; guardar fora do projeto é mais seguro.

Execute no **seu** PowerShell, não por automação:

```powershell
New-Item -ItemType Directory -Path "keystores" -Force

keytool -genkeypair -v `
  -keystore "keystores\eatcontrol-release.jks" `
  -storetype PKCS12 `
  -alias "eatcontrol-release" `
  -keyalg RSA `
  -keysize 4096 `
  -validity 10950
```

O `keytool` acompanha o JDK. Se o comando não for encontrado, use o JDK do Android Studio:

```powershell
& "$env:LOCALAPPDATA\Programs\Android Studio\jbr\bin\keytool.exe" -genkeypair -v `
  -keystore "keystores\eatcontrol-release.jks" `
  -storetype PKCS12 `
  -alias "eatcontrol-release" `
  -keyalg RSA -keysize 4096 -validity 10950
```

Explicação das escolhas:

- `PKCS12`: formato padrão atual; o formato JKS antigo é considerado proprietário e legado.
- `RSA 4096`: acima do mínimo exigido pelo Play, que é 2048.
- `validity 10950`: cerca de 30 anos, acima do mínimo prático de 25.
- `alias`: identifica a chave dentro do arquivo. Use algo estável e descritivo, como
  `eatcontrol-release`. Trocar de alias depois significa trocar de chave.

### O que responder nas perguntas do keytool

O `keytool` pedirá senha do keystore, senha da chave e dados do certificado.

- **Senha do keystore** e **senha da chave**: gere no gerenciador de senhas, com pelo menos 20
  caracteres aleatórios. Podem ser diferentes; se forem diferentes, ambas precisam ir para o
  `local.properties`.
- **CN (nome):** nome do app ou da equipe, por exemplo `Eat Control AI`.
- **OU / O:** unidade e organização, por exemplo `EverGreen Agency`.
- **L / ST / C:** cidade, estado e código do país, por exemplo `BR`.

Esses dados ficam públicos dentro do certificado do APK. Não coloque dados pessoais sensíveis,
telefone, CPF ou endereço residencial.

## 5. Registrar a chave para o Gradle

O `app/build.gradle.kts` já lê quatro propriedades. Adicione somente em `local.properties`, que é
ignorado pelo Git:

```properties
release_store_file=keystores/eatcontrol-release.jks
release_store_password=<senha do keystore>
release_key_alias=eatcontrol-release
release_key_password=<senha da chave>
```

Enquanto as quatro não existirem, o build release continua sem identidade de produção configurada.

## 6. Conferir a chave e extrair o fingerprint

```powershell
keytool -list -v -keystore "keystores\eatcontrol-release.jks" -alias "eatcontrol-release"
```

Anote o **SHA-256** do certificado. Ele é o valor que:

1. deve ser cadastrado no Meta Wearables Developer Center como App signature do release;
2. permite conferir depois se o APK instalado é realmente o seu.

O fingerprint do certificado não é segredo. A senha e o arquivo `.jks` são.

Depois de gerar um APK release, confirme que o certificado é o esperado:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\build-tools\36.1.0\apksigner.bat" verify --verbose --print-certs `
  "app\build\outputs\apk\release\app-release.apk"
```

## 7. Backup criptografado e testado

Um backup que nunca foi restaurado não é backup. O procedimento tem três partes.

### 7.1 Criar a cópia criptografada

Opção com 7-Zip, criptografando conteúdo e nomes de arquivo:

```powershell
& "C:\Program Files\7-Zip\7z.exe" a -tzip -mem=AES256 -mhe=on `
  "$env:USERPROFILE\Desktop\eatcontrol-keystore-backup.zip" `
  "keystores\eatcontrol-release.jks"
```

Opção com GnuPG, se preferir chave assimétrica ou senha simétrica:

```powershell
gpg --symmetric --cipher-algo AES256 --output "$env:USERPROFILE\Desktop\eatcontrol-release.jks.gpg" `
  "keystores\eatcontrol-release.jks"
```

Não use `Compress-Archive` do PowerShell: ele não criptografa.

A senha do backup deve ser **diferente** da senha do keystore e ficar no gerenciador de senhas.

### 7.2 Testar a restauração

O teste precisa provar que o arquivo restaurado ainda é uma chave utilizável:

```powershell
$temp = Join-Path $env:TEMP "keystore-restore-test"
New-Item -ItemType Directory -Path $temp -Force

& "C:\Program Files\7-Zip\7z.exe" x "$env:USERPROFILE\Desktop\eatcontrol-keystore-backup.zip" "-o$temp"

keytool -list -v -keystore "$temp\keystores\eatcontrol-release.jks" -alias "eatcontrol-release"
```

O teste passou quando o SHA-256 impresso é idêntico ao do item 6. Depois, remova a pasta temporária:

```powershell
Remove-Item -Recurse -Force $temp
```

### 7.3 Guardar em dois lugares distintos

- uma cópia em armazenamento controlado pela equipe, com acesso restrito e registro de quem acessa;
- uma cópia offline, por exemplo em mídia removível guardada fisicamente;
- as senhas em gerenciador de senhas, nunca junto do arquivo.

Registre no gerenciador de senhas, junto do item: alias, data de criação, validade e SHA-256. Assim
é possível auditar sem abrir o keystore.

## 8. Checklist de conclusão

- [ ] tabela de custódia da seção 3 preenchida e acordada;
- [ ] modelo A ou B escolhido explicitamente;
- [ ] keystore gerado com PKCS12, RSA de 2048 bits ou mais e validade de pelo menos 25 anos;
- [ ] alias definido e anotado;
- [ ] senhas geradas no gerenciador de senhas, com pelo menos 20 caracteres;
- [ ] quatro propriedades presentes apenas em `local.properties`;
- [ ] SHA-256 do certificado anotado fora do repositório;
- [ ] backup criptografado criado;
- [ ] restauração testada e fingerprint conferido;
- [ ] segunda cópia guardada em local separado;
- [ ] `git status` não mostra `.jks`, `.keystore` nem `local.properties`;
- [ ] fingerprint cadastrado no Developer Center antes de criar o canal de release;
- [ ] APK release gerado e verificado com `apksigner`;
- [ ] data da próxima conferência do backup agendada.

## 9. O que fazer se algo der errado

| Situação | Ação |
|---|---|
| Senha do keystore perdida | não há recuperação; a chave está perdida |
| Arquivo `.jks` perdido, backup íntegro | restaurar e conferir o SHA-256 |
| Arquivo e backup perdidos, distribuição direta | gerar nova identidade e reinstalar o app em todos os aparelhos |
| Arquivo perdido, publicando com Play App Signing | solicitar redefinição da chave de upload ao suporte do Play |
| Suspeita de vazamento do keystore | tratar como comprometido, rotacionar identidade e revisar quem tinha acesso |

## 10. Relação com o restante da entrega

- O beta atual com o parceiro usa **APK debug**, cuja assinatura já está cadastrada. Ele continua
  válido para testar Ray-Ban e DAT.
- Este guia destrava o item `EQ-03` do inventário de entrega.
- Após concluir, atualize `docs/hackathon-2026/INVENTARIO_ENTREGA_2026-08-22.md`: seção 9.3 e o bloco de release
  definitivo em 9.7, registrando apenas estados e fingerprint, nunca senhas.
