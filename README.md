# Inventory Service

This is a simple Spring Boot-based REST API for managing product inventory.

It includes the following features:

- Create new products
- List products (with optional pagination)
- Search products by name (case-insensitive)
- Update only the quantity of a product
- Delete a product
- Get a summary of the inventory (total products, total quantity, average price, and list of out-of-stock products)
- OpenAPI documentation (Swagger UI)

---

## 🔧 Tech Stack

- Java 21
- Spring Boot 3.5.4
- Spring Data JPA
- PostgreSQL
- Spring Validation
- Springdoc OpenAPI (Swagger UI)
- Docker & Docker Compose
- Testcontainers for integration testing
- Maven

---

## 🚀 Running the App (with Docker)

### 1. Build and start the app with Docker Compose

```bash
docker compose up --build