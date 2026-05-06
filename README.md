# ePasal Backend (Spring Boot Microservices)

This repo contains the backend for an e-commerce app built with Spring Boot using a microservice architecture.

## Services

- **API Gateway** (`APICloudGateway`) — Spring Cloud Gateway (WebFlux) + OAuth2 resource server
- **Service Discovery** (`eurekaServer`) — Netflix Eureka
- **User/Auth** (`UserService`) — auth, user management, OTP/code flows
- **Product** (`ProductService`) — product catalog (MongoDB) + Cloudinary integration
- **Cart** (`CartService`) — cart management
- **Order** (`OrderService`) — orders + payment integrations
- **Inventory** (`InventoryService`) — inventory management
- **Mail** (`MailServer`) — email sending + RabbitMQ listeners + Thymeleaf templates

## Tech Stack (from the Maven poms)

- Java **21**
- Spring Boot **4.0.5**
- Spring Cloud **2025.1.0**
- Eureka (service discovery) + Spring Cloud LoadBalancer
- Spring Cloud Gateway (WebFlux)
- Security: Spring Security + OAuth2 Resource Server
- Inter-service calls: OpenFeign
- Resilience: Resilience4j circuit breaker (dependency included in Cart/Product/Order)
- Datastores:
	- MySQL (JPA/Hibernate) — User/Cart/Order/Inventory
	- MongoDB — Product
	- Redis — used in UserService (rate limiting / attempt tracking)
- Messaging: RabbitMQ (Spring AMQP)
- OpenAPI/Swagger: springdoc-openapi (each service) + aggregated Swagger UI at the gateway
- Email: Spring Mail + Thymeleaf templates (MailServer)
- Mapping: MapStruct (DTO mapping)

## Spring Boot Topics Covered

- Microservices with service discovery (Eureka) and gateway routing
- JWT/OAuth2 resource server setup (service-side)
- Database per service pattern (MySQL + MongoDB)
- Async/event-driven communication with RabbitMQ
- Centralized API docs via SpringDoc + gateway aggregation
- Validation (spring-boot-starter-validation)
- Resilience patterns (circuit breaker via Resilience4j)
- DTO mapping (MapStruct)

## Backend Engineering Principles Implemented

- **API Gateway**: single entry point + route rewriting to internal services
- **Service discovery + client-side load balancing**: services register to Eureka and are called via `lb://...`
- **Event-driven email**: User/Order events published to RabbitMQ and consumed by MailServer
- **Rate limiting & abuse protection** (UserService): request cooldown + attempt window enforced using Redis
- **Password reset flow**: password reset codes emitted as events and emailed via MailServer templates
- **Separation of concerns**: separate services + separate databases where applicable
- **Fault tolerance**: circuit breaker around downstream calls (where configured)

## API Docs (Swagger)

- Public Swagger UI: https://backend.poudelsunil.info.np/swagger-ui/index.html

When running locally via Docker, the gateway Swagger UI should be at:

- http://localhost:8080/swagger-ui/index.html

## Running Locally (Docker Compose)

Prereqs:

- Docker + Docker Compose + maven

From the repo root:

```bash
mvn clean package -DskipTests
docker compose up --build
```

This starts:

- Gateway: `8080`
- UserService: `8081`
- ProductService: `8082`
- CartService: `8083`
- OrderService: `8084`
- InventoryService: `8085`
- MailServer: `8086`
- Eureka: `8761`
- MySQL: `3306`
- RabbitMQ: `5672` (AMQP), `15672` (management UI)
- Redis: `6379`

## Notes / Config

- **OAuth2 (Google)**: `UserService` has Google OAuth client config in `application.yml`. For local auth flows you’ll need to use your own client id/secret and correct redirect URI.
- **JWT keys**: services validate JWT using `classpath:keys/public.key`. `UserService` also has the private key for signing.
- **MongoDB**: `ProductService` expects a MongoDB URI (currently set in `application.yml`).
- **SMTP**: `MailServer` is configured for Gmail SMTP in `application.yml`.

## Repo Layout

- `microservice/` contains one Spring Boot project per service.
- Root `compose.yaml` runs the full stack for local development.
