# E-commerce Microservices

Sistema de e-commerce baseado em microserviços com **Order Service** e **Notification Service**.

## 🏗️ Arquitetura

![Architecture](docs/architecture.svg)

### AWS Infrastructure

![AWS](docs/aws-infrastructure.svg)

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Security + OAuth2 (Keycloak)**
- **Apache Kafka**
- **MongoDB Atlas**
- **Docker**
- **Terraform (AWS ECS)**

## 📦 Serviços

| Serviço | Descrição |
|---------|-----------|
| `order-service` | API REST para gerenciamento de pedidos |
| `notification-service` | Consumer Kafka para processamento de notificações |

## 🚀 Quick Start

### Pré-requisitos

- Java 17+
- Docker
- MongoDB Atlas (ou local)
- Kafka (ou Confluent Cloud)
- Keycloak (ou outro OIDC provider)

### Variáveis de Ambiente

```bash
# Server
PORT=8080

# OAuth2 / Keycloak
OIDC_ISSUER_URI=https://your-keycloak/realms/ecommerce

# Kafka
KAFKA_BOOTSTRAP_SERVERS=your-kafka:9092
KAFKA_TOPIC=notification-topic
KAFKA_GROUP=notification-group

# MongoDB
SPRING_DATA_MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/ecommerce
```

### Rodando Localmente

```bash
# Order Service
cd order-service
./mvnw spring-boot:run

# Notification Service
cd notification-service
./mvnw spring-boot:run
```

### Rodando com Docker

```bash
# Build das imagens
docker build -t order-service ./order-service
docker build -t notification-service ./notification-service

# Run com variáveis de ambiente
docker run -d \
  -e PORT=8080 \
  -e OIDC_ISSUER_URI=https://your-keycloak/realms/ecommerce \
  -e KAFKA_BOOTSTRAP_SERVERS=your-kafka:9092 \
  -e SPRING_DATA_MONGODB_URI=mongodb+srv://... \
  -p 8080:8080 \
  order-service

docker run -d \
  -e PORT=8080 \
  -e OIDC_ISSUER_URI=https://your-keycloak/realms/ecommerce \
  -e KAFKA_BOOTSTRAP_SERVERS=your-kafka:9092 \
  -e KAFKA_GROUP=notification-group \
  -e SPRING_DATA_MONGODB_URI=mongodb+srv://... \
  notification-service
```

## 📡 API Endpoints

### Order Service

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/orders` | Criar pedido |
| `GET` | `/orders/{id}` | Buscar pedido |
| `PUT` | `/orders/{id}` | Atualizar pedido |

> Todas as rotas requerem autenticação via Bearer Token (JWT)

## 🔐 Autenticação

O projeto utiliza OAuth2 com Keycloak. Para obter um token:

```bash
curl -X POST \
  'https://your-keycloak/realms/ecommerce/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password&client_id=ecommerce-client&username=USER&password=PASS'
```

## ☁️ Deploy AWS ECS

A infraestrutura está definida em Terraform na pasta `/infra`:

```bash
cd infra
terraform init -backend-config=envs/dev/terraform.tfvars
terraform plan
terraform apply
```

## 📁 Estrutura do Projeto

```
.
├── order-service/           # Microserviço de pedidos
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── notification-service/    # Microserviço de notificações
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── infra/                   # Terraform (AWS)
├── docker/                  # Docker Compose (dev)
└── data/                    # Postman Collection
```

## 📝 Postman Collection

Importe o arquivo `data/postman_collection.json` no Postman para testar a API.

## 📄 License

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

Desenvolvido por **Nathan** 🚀
