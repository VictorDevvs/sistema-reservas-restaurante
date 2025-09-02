# 🍽️ Sistema de Reserva de Restaurante

Sistema completo para gerenciamento de reservas em restaurantes. Permite que clientes se cadastrem, façam login com autenticação segura via JWT, e realizem reservas com regras de disponibilidade e capacidade de mesas. A API será publicada em **ambiente público** com banco de dados **PostgreSQL na nuvem** para produção.

---

## 🧩 Problema Resolvido

Este sistema resolve desafios comuns de gestão em restaurantes, como:

- Evitar **overbooking de mesas** e má gestão de horários
- Fornecer uma camada de **segurança com autenticação e controle de acesso**
- **Validar regras de negócio específicas**, como horário de funcionamento e capacidade de cada mesa
- Diferenciar funções de **usuário e administrador**, oferecendo rotas seguras e personalizadas

---

## 🚀 Funcionalidades

- Cadastro e login de usuários com validações robustas
- Geração de accessToken JWT e refreshToken opaco ao logar
- Controle de acesso baseado em perfis (`CLIENTE`, `ADMINISTRADOR`)
- Gerenciamento de mesas (criar, atualizar, remover — apenas ADMIN)
- Criação, visualização e cancelamento de reservas com regras específicas
- Verificações automáticas para concluir reservas expiradas
- API documentada com Swagger
- Senhas validadas com força mínima (números, maiúsculas, minúsculas, especiais, tamanho mínimo)

---

## 🧰 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security**
- **JWT (jjwt)**
- **MapStruct**
- **PostgreSQL (produção) / MySQL (testes)**
- **Maven**
- **Swagger/OpenAPI**
- **Jakarta Bean Validation**
- **Tarefas agendadas com `@Scheduled`**

---

## 🏗️ Arquitetura e Organização

Organizado em camadas:

```
src/main/java/com/br/sistema_reserva_restaurante
├── config
├── controllers
├── dtos
├── exceptions
├── handler
├── mapper
├── model
├── repositories
├── security
├── service
├── utils
└── validation
```

---

## 🗃️ Modelo de Banco de Dados (PostgreSQL)

- **usuario**
  - `id`, `nome`, `email`, `senha (bcrypt)`, `role`
- **mesa**
  - `id`, `numero`, `capacidade`, `status` (`DISPONIVEL`, `INDISPONIVEL`)
- **reserva**
  - `id`, `usuario_id`, `mesa_id`, `data_hora_reserva`, `numero_pessoas`, `status` (`ATIVA`, `CANCELADA`, `CONCLUIDA`)

---

## 📫 Endpoints

### 👤 Usuário

| Método | Endpoint              | Acesso       | Descrição                   |
|--------|-----------------------|--------------|-----------------------------|
| POST   | `/usuarios/registrar` | Público      | Cadastro de usuário         |
| POST   | `/usuarios/login`     | Público      | Login e obtenção de JWT     |
| POST   | `/usuarios/refresh`   | Público      | Atualiza o token de acesso  |
| POST   | `/usuarios/logout`    | Público      | Revoga o token de acesso    |



### 📅 Reserva

| Método | Endpoint                   | Acesso            | Descrição                                     |
|--------|----------------------------|-------------------|-----------------------------------------------|
| GET    | `/reservas`                | Autenticado       | Listar reservas do usuário logado             |
| POST   | `/reservas`                | Autenticado       | Criar uma nova reserva                        |
| PATCH  | `/reservas/{id}/cancelar`  | Autenticado/Admin | Cancelar reserva criada pelo usuário ou ADMIN |

### 🍽️ Mesa

| Método | Endpoint         | Acesso      | Descrição                              |
|--------|------------------|-------------|----------------------------------------|
| GET    | `/mesas`         | Autenticado | Lista todas as mesas                   |
| POST   | `/mesas`         | ADMIN       | Cadastra uma nova mesa                 |
| PATCH  | `/mesas/{id}`    | ADMIN       | Atualiza dados de uma mesa             |
| DELETE | `/mesas/{id}`    | ADMIN       | Remove mesa do sistema                 |

---

## 🔐 Segurança e Regras de Negócio

- Apenas usuários autenticados podem fazer reservas ou cancelamentos
- O login gera um accessToken JWT com duração de 15 minutos usado em requisições subsequentes e um refreshToken opaco
- Regras de reserva:
  - Horário permitido: **18:00 às 23:00**
  - Não permite reservar datas passadas
  - A capacidade da mesa deve suportar o número de pessoas
  - Mesas reservadas ficam com status `INDISPONIVEL`; ao cancelar, voltam para `DISPONIVEL`
- Cancelamento só pode ser feito pelo criador da reserva ou por um administrador
- Verificação automática de reservas expiradas por agendamento

---

## ⚙️ Execução e Deploy

### Pré-requisitos

- PostgreSQL configurado
- JDK 21
- Maven instalado

### Rodando localmente

```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```

Configure o `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://<host>:<port>/<database>
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
jwt.secret=CHAVE_SUPER_SECRETA
jwt.expiration=3600000
```

Execute o projeto:

```bash
./mvnw spring-boot:run
```

Acesse o Swagger:
```
http://localhost:8080/swagger-ui/index.html
```
