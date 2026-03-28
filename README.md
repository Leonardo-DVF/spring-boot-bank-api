# Bank API

REST API para gerenciamento bancário, permitindo cadastro e autenticação de usuários, gerenciamento de clientes, criação de contas e operações financeiras como depósito e saque.

## Objetivo

Este projeto foi desenvolvido com o objetivo de praticar conceitos de desenvolvimento backend com Java e Spring Boot, incluindo:

- autenticação e autorização com Spring Security e JWT
- modelagem de entidades e regras de negócio
- validações de entrada
- tratamento global de exceções
- migrations com Flyway
- integração com PostgreSQL
- containerização com Docker
- testes unitários

## Funcionalidades

- Cadastro de usuário
- Login com autenticação JWT
- Cadastro de cliente vinculado ao usuário autenticado
- Criação de conta bancária
- Consulta de conta por id
- Listagem de contas do cliente autenticado
- Atualização de status da conta
- Depósito em conta
- Saque em conta
- Tratamento global de exceções
- Versionamento de banco com Flyway

## Tecnologias utilizadas

- Java
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker
- Docker Compose
- Maven
- JUnit 5
- Mockito

## Estrutura do projeto

O projeto está organizado por domínio e responsabilidade:

- `auth` → autenticação, segurança e geração/validação de token JWT
- `user` → gerenciamento de usuários
- `customer` → gerenciamento de clientes
- `account` → gerenciamento de contas bancárias
- `exception` → tratamento global de erros e exceções customizadas

## Regras de negócio

- Apenas usuários autenticados podem acessar endpoints protegidos
- Cada cliente está vinculado ao usuário autenticado
- A conta criada pertence ao cliente do usuário autenticado
- Não é permitido acessar contas de outro cliente
- Não é permitido operar em conta inativa
- Não é permitido sacar valor maior que o saldo disponível
- O schema do banco é controlado por migrations com Flyway

## Como executar o projeto

### Pré-requisitos

- Java instalado
- Maven instalado
- Docker e Docker Compose

### Rodando com Docker

Execute o comando abaixo na raiz do projeto:

```bash
docker compose up -d --build