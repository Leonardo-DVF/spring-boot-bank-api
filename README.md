# Bank API

[![Java](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-blue)](https://www.docker.com/)
[![Tests](https://img.shields.io/badge/tests-JUnit%205-green)](https://junit.org/junit5/)

REST API para gerenciamento bancário desenvolvida com Java e Spring Boot. O projeto cobre cadastro e autenticação de usuários, gerenciamento de clientes, contas bancárias e operações financeiras como depósito, saque, transferência e consulta de histórico.

## Objetivo

Este projeto foi desenvolvido para praticar conceitos de backend com Spring Boot, incluindo:

- autenticação e autorização com Spring Security e JWT
- modelagem de entidades e regras de negócio
- validações de entrada
- tratamento global de exceções
- documentação de API com Swagger/OpenAPI
- migrations com Flyway
- integração com PostgreSQL
- containerização com Docker e Docker Compose
- testes unitários com JUnit 5 e Mockito

## Funcionalidades

- Cadastro de usuário
- Login com token JWT
- Cadastro de cliente vinculado ao usuário autenticado
- Consulta e atualização de dados do cliente
- Atualização de status do cliente
- Criação de conta bancária
- Consulta de conta por id
- Listagem de contas do cliente autenticado
- Atualização de status da conta
- Depósito em conta
- Saque em conta
- Transferência entre contas
- Histórico paginado de transações por conta
- Tratamento global de erros
- Versionamento do banco com Flyway

## Tecnologias

- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Swagger/OpenAPI
- Docker
- Docker Compose
- Maven
- JUnit 5
- Mockito

## Estrutura

O projeto está organizado por domínio e responsabilidade:

- `auth`: autenticação, segurança e geração/validação de token JWT
- `user`: gerenciamento de usuários
- `customer`: gerenciamento de clientes
- `account`: gerenciamento de contas bancárias
- `transaction`: operações financeiras e histórico de transações
- `exception`: tratamento global de erros e exceções customizadas
- `config`: configurações de documentação e infraestrutura da aplicação

## Variáveis de Ambiente

O projeto usa variáveis de ambiente para dados sensíveis e configurações locais. Existe um arquivo de exemplo:

```bash
.env.example
```

Para rodar localmente com Docker, crie um arquivo `.env` na raiz do projeto:

```env
POSTGRES_DB=bank_api
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
JWT_SECRET=minha-chave-secreta-local
```

O arquivo `.env` não deve ser enviado para o GitHub. Ele já está ignorado pelo `.gitignore`.

## Como Executar com Docker

Com Docker e Docker Compose instalados, execute na raiz do projeto:

```bash
docker compose up -d --build
```

A API ficará disponível em:

```text
http://localhost:8080
```

Para acompanhar os logs:

```bash
docker compose logs -f api
```

Para parar os containers:

```bash
docker compose down
```

## Swagger

Com a aplicação em execução, acesse a documentação interativa:

```text
http://localhost:8080/swagger-ui.html
```

Também é possível acessar a especificação OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

## Endpoints Principais

### Autenticação

- `POST /auth/register`: cadastra um usuário
- `POST /auth/login`: autentica um usuário e retorna um token JWT

### Clientes

- `POST /customers`: cria um cliente para o usuário autenticado
- `GET /customers/{id}`: consulta um cliente por id
- `PATCH /customers/me`: atualiza os dados do cliente autenticado
- `PATCH /customers/{id}/status`: atualiza o status de um cliente

### Contas

- `POST /accounts`: cria uma conta bancária
- `GET /accounts/{id}`: consulta uma conta por id
- `GET /accounts`: lista as contas do cliente autenticado
- `PATCH /accounts/{id}/status`: atualiza o status de uma conta

### Transações

- `POST /transactions/deposit`: realiza depósito em uma conta
- `POST /transactions/withdraw`: realiza saque em uma conta
- `POST /transactions/transfer`: realiza transferência entre contas
- `GET /transactions/{id}`: consulta uma transação por id
- `GET /transactions/accounts/{accountId}`: lista o histórico paginado de uma conta

## Exemplos de Requisições

### Cadastro de Usuário

```json
{
  "username": "leonardo",
  "email": "leonardo@email.com",
  "password": "123456",
  "role": "ROLE_CLIENT"
}
```

### Login

```json
{
  "username": "leonardo",
  "password": "123456"
}
```

### Cadastro de Cliente

```json
{
  "fullName": "Leonardo Ferreira",
  "document": "21872010075"
}
```

### Criação de Conta

```json
{
  "agency": "0001",
  "number": "123456-7",
  "type": "CHECKING"
}
```

### Depósito

```json
{
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 150.00,
  "description": "Depósito em dinheiro"
}
```

### Saque

```json
{
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 100.00,
  "description": "Saque em caixa eletrônico"
}
```

### Transferência

```json
{
  "sourceAccountId": "550e8400-e29b-41d4-a716-446655440000",
  "destinationAgency": "0001",
  "destinationAccountNumber": "765432-1",
  "amount": 250.00,
  "description": "Transferência entre contas"
}
```

## Collection

O projeto inclui uma collection do Postman em:

```text
docs/Bank API.postman_collection.json
```

Configure a variável `baseUrl` como `http://localhost:8080` e, após o login, preencha a variável `token` com o JWT retornado.

## Regras de Negócio

- Apenas usuários autenticados podem acessar endpoints protegidos.
- Cada cliente pertence ao usuário autenticado que o criou.
- Cada usuário pode ter apenas um cliente.
- O documento do cliente deve ser único.
- Uma conta pertence a um cliente.
- Não é permitido acessar ou operar contas de outro cliente.
- Não é permitido operar contas inativas ou bloqueadas.
- Depósitos, saques e transferências exigem valor maior que zero.
- Saques e transferências exigem saldo suficiente.
- Não é permitido transferir para a mesma conta de origem.
- Transferências registram transações para a conta de origem e para a conta de destino.
- O histórico de transações é paginado e ordenado por data de criação.
- O schema do banco é controlado por migrations do Flyway.

## Testes

Para executar os testes:

```bash
./mvnw test
```

No Windows:

```powershell
cmd /c mvnw.cmd test
```
