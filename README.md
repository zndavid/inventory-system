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

## 🚀 Running the App

### Build and Run Locally

```bash
mvn clean package
java -jar target/inventory-service-0.0.1-SNAPSHOT.jar
```
### Build images and start containers
```bash
docker compose up --build -d
```

### Stop and remove containers
```bash
docker compose down
```


## 📡 Example cURL Requests

### Create a new product
```bash
curl -X POST "http://localhost:8080/products" \
-H "Content-Type: application/json" \
-d '{"name":"Sample","quantity":5,"price":19.99}'
```

### Get all products
```bash
curl "http://localhost:8080/products"
```

### Search products by name
```bash
curl "http://localhost:8080/products/search?name=sample"
```
### Update product quantity
```bash
curl -X PATCH "http://localhost:8080/products/{id}?quantity=10"
```

### Delete a product
```bash
curl -X DELETE "http://localhost:8080/products/{id}"
```
### Get inventory summary
```bash
curl "http://localhost:8080/products/summary"
```

## 📝 Swagger UI
http://localhost:8080/swagger-ui/index.html