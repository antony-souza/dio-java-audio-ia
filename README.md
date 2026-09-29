# API de orçamento com voz e Spring AI

Projeto de estudo baseado no módulo [05-spring-ai da trilha DIO](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/tree/main/05-spring-ai). A API registra e consulta gastos por REST ou por comando de voz. No fluxo de voz, o Spring AI transcreve o áudio, o modelo escolhe uma ferramenta da aplicação, o caso de uso consulta ou grava os dados, e a resposta é convertida em MP3.

## Minha evolução da base

- Validação de descrição, valor em centavos e categoria no domínio `Transaction`. A mesma regra protege a rota REST e o Tool Calling.
- Erros de validação da rota REST retornam HTTP 400 com `{"error":"..."}`.
- Conversão correta de centavos para reais na resposta, usando `BigDecimal` (por exemplo, `1250` centavos vira `12.50`).
- Testes unitários de validação, persistência e conversão monetária sem depender de chave de IA ou banco.
- GitHub Actions executa os testes unitários a cada envio ou pull request.
- Projeto configurado para Java 17.

## Tecnologias

Java 17, Spring Boot 4, Spring AI 2, Gradle, Spring Data JPA, MySQL e Docker Compose. O provedor OpenAI é usado para chat, transcrição e geração de voz.

## Como executar

Pré-requisitos: JDK 17, Docker com Compose e uma chave de API da OpenAI com acesso aos modelos configurados em `src/main/resources/application.properties`. O uso da API do provedor pode gerar custos. Não coloque a chave no Git.

No PowerShell, dentro da pasta do projeto:

```powershell
$env:OPENAI_API_KEY = "sua-chave-aqui"
docker compose up -d
.\gradlew.bat bootRun
```

O `compose.yml` cria o MySQL na porta local `3307`. O Spring Boot Docker Compose detecta o serviço e configura a conexão para a aplicação. Para rodar os testes unitários, não é necessário subir o banco nem informar a chave:

```powershell
.\gradlew.bat test
```

Em Linux/macOS, use `./gradlew` no lugar de `.\gradlew.bat` e exporte `OPENAI_API_KEY` no shell.

## Como testar a API

As categorias disponíveis na base são `GROCERIES`, `PHARMA` e `AUTO`. O campo `amount` da entrada é **em centavos**. A resposta apresenta o valor **em reais**.

Criar uma transação no PowerShell:

```powershell
$body = @{ description = "Mercado"; category = "GROCERIES"; amount = 1250 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/transactions -ContentType application/json -Body $body
```

Resposta esperada: HTTP 201, com `description` igual a `Mercado` e `amount` igual a `12.50`.

Consultar uma categoria:

```powershell
Invoke-RestMethod http://localhost:8080/transactions/GROCERIES
```

Testar a validação:

```powershell
$body = @{ description = "Mercado"; category = "GROCERIES"; amount = 0 } | ConvertTo-Json
Invoke-WebRequest -Method Post -Uri http://localhost:8080/transactions -ContentType application/json -Body $body -SkipHttpErrorCheck
```

Resposta esperada: HTTP 400 e mensagem `O valor deve ser maior que zero (em centavos)`.

Enviar um comando de voz (substitua o caminho por um áudio seu):

```powershell
curl.exe -X POST -F "file=@comando.m4a" http://localhost:8080/transactions/ai -o resposta.mp3
```

Exemplo de fala: “Registre uma compra de doze reais e cinquenta centavos no mercado”. A transcrição, a escolha da ferramenta e a voz exigem conexão com o provedor de IA. Revise o resultado criado com `GET /transactions/GROCERIES`.

## Organização e aprendizado

- `domain`: regras e modelo de transação; valores são armazenados em centavos.
- `application`: casos de uso compartilhados entre REST e IA.
- `infrastructure`: rotas HTTP, tratamento de erro e persistência JPA.

Este exercício mostra por que a regra de negócio deve ficar no domínio: uma validação só protege todos os caminhos de entrada. Também mostra que o modelo de IA escolhe a ação, mas quem grava ou consulta de fato é um caso de uso Java.

Os testes automatizados cobrem regras locais. O fluxo completo de voz depende de MySQL, da chave e dos serviços externos, então deve ser testado manualmente no ambiente de execução.

## Origem

Base educacional: [digitalinnovationone/dio-spring-boot-learning-track, módulo 05-spring-ai](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/tree/main/05-spring-ai), commit `9add4345f9f0f75e1a02dcd557c627fdf6187ff7`. As alterações descritas acima são a evolução deste repositório.
