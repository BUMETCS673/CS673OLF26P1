# Bluejay Spring Boot API

A production-ready Spring Boot 4.1 backend built with Java 21, Spring Security, Spring Data JPA, Flyway, and MySQL 8.4.

---

## 🛠️ Tech Stack

* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 4.1 (`web`, `data-jpa`, `actuator`, `security`)
* **Database:** MySQL 8.4
* **Database Migrations:** Flyway
* **Security:** JWT with Stateless Blacklisting
* **Build Tool:** Maven

---

## 🚀 Final Release API & Feature Summary

The final release expands the API across vertical feature slices to deliver a complete Point-of-Sale (POS) system with integrated inventory tracking, checkout processing, reporting analytics, and role-based access control (RBAC):

### 1. Endpoint & Controller Summary

* **Authentication & User Management (`/auth`, `/users`)**
    * `POST /auth/login` — Authenticates credentials and returns a signed JWT.
    * `POST /auth/logout` — Revokes the active caller's JWT using the token blacklist.
    * `GET /users` — Retrieves system users (Restricted to `ADMIN`).
    * `POST /users` — Creates new user accounts with role assignments (Restricted to `ADMIN`).
    * `PATCH /users/{userId}` — Updates user status and role assignments (Restricted to `ADMIN`).

* **Product Catalog (`/product`)**
    * `POST /product` — Creates products with assigned categories (`ADMIN`, `MANAGER`).
    * `GET /product` — Filtered product lookup with pagination via `ProductQueryDto` (`ADMIN`, `MANAGER`, `CASHIER`).
    * `GET /product/categories` — Retrieves available product categories (`ADMIN`, `MANAGER`).
    * `GET /product/{barcode}` — Barcode lookup for catalog items (`ADMIN`, `MANAGER`, `CASHIER`).
    * `PATCH /product/{id}/low-stock` — Updates product low-stock threshold triggers.

* **Inventory Management (`/inventory`)**
    * `GET /inventory` — Returns catalog inventory details and costs (`ADMIN`, `MANAGER`).
    * `GET /inventory/stock` — Retrieves current stock levels and low-stock alerts (`ADMIN`, `MANAGER`).
    * `POST /inventory/stock-entry` — Processes stock restock entries and records audit movements (`ADMIN`, `MANAGER`).
    * `PATCH /inventory/{id}/price` — Updates product sale prices inline (`ADMIN`, `MANAGER`).

* **Sales & Point of Sale (`/sales`)**
    * `POST /sales` — Processes checkout transactions, deducts stock, calculates change breakdown, and logs audit movements (`ADMIN`, `MANAGER`, `CASHIER`).
    * `POST /sales/printReceipt` — Retrieves detailed receipt data for past transactions (`ADMIN`, `MANAGER`, `CASHIER`).

* **Reporting & Analytics (`/reports`)**
    * `GET /reports/inventory` — Generates a total inventory catalog report with current stock levels and cost baselines (`ADMIN`, `MANAGER`).
    * `GET /reports/sales` — Generates a consolidated sales report (revenue, costs, net profit, unit counts) for a date range (`ADMIN`).
    * `GET /reports/sales/products` — Generates a per-product sales performance and profitability report for a date range (`ADMIN`).

---

### 2. Standardized API Response Contract

All controllers wrap returned payloads in `common.dto.ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Sale completed successfully",
  "data": {
    "transactionId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
    "receiptNumber": "BJ-9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
    "grandTotal": 15.50,
    "cashReceived": 20.00,
    "changeDue": 4.50,
    "paymentMethod": "CASH"
  },
  "errorCode": null
}

```

---

## 🏛️ Architecture & Domain-Driven Design (DDD)

Our backend follows a **Clean Architecture (Ports & Adapters)** pattern organized around **Vertical Feature Slices**. Each business module isolates its core domain from framework and persistence infrastructure.

```text
edu.bu.metcs673.bluejay/
├── auth/                      # Authentication & Identity Vertical Slice
│   └── security/              # Web Security, JWT Filters, Blacklist & Auth Infrastructure
├── inventory/                 # Stock, Restock & Audit Movement Vertical Slice
├── product/                   # Catalog & Category Management Vertical Slice
├── report/                    # Analytics, Profitability & Reporting Vertical Slice
├── sales/                     # Checkout & POS Transaction Vertical Slice
└── common/                    # Cross-Cutting Concerns & Shared Infrastructure
    ├── config/                # Framework Configuration Beans
    ├── dto/                   # Unified API Response Wrappers (ApiResponse<T>)
    ├── exception/             # Global RestControllerAdvice Exception Handlers
    └── security/              # Security Context Helpers (SecurityUtils)

```

### 📐 Domain Guidelines & Architectural Rules

To maintain consistency across all feature slices, every domain adheres to the following Clean Architecture boundaries:

#### 1. Clean Architecture Package Mapping

```text
    +-------------------------------------------------------------+
    |                  OUTER LAYER (Infrastructure)               |
    |                                                             |
    |   [ DRIVING ADAPTERS ]           [ DRIVEN ADAPTERS ]        |
    |   ├── controller/                ├── repository/            |
    |   └── security/                  └── entity/                |
    |                                                             |
    |    +---------------------------------------------------+    |
    |    |            MIDDLE LAYER (Application Use Cases)   |    |
    |    |                                                   |    |
    |    |    ├── service/                                   |    |
    |    |    └── dto/                                       |    |
    |    |                                                   |    |
    |    |    +-----------------------------------------+    |    |
    |    |    |      INNER CORE (Pure Domain)           |    |    |
    |    |    |                                         |    |    |
    |    |    |      └── domain/                        |    |    |
    |    |    |                                         |    |    |
    |    |    +-----------------------------------------+    |    |
    |    +---------------------------------------------------+    |
    +-------------------------------------------------------------+

```

#### 2. Standard Sub-Package Responsibilities & Allowed Dependencies

```text
├── controller/            # Driving Adapter: REST Endpoints & Request Validation (@Valid)
├── domain/                # Inner Core: Business enums, core objects and domain model contracts
├── dto/                   # Middle Layer: API Transport Contracts (*Request.java, *Response.java)
├── entity/                # Driven Adapter: JPA Database Entities & Relational Schemas
├── repository/            # Driven Adapter: Spring Data JPA Interfaces / JDBC Queries
└── service/               # Middle Layer: Application Use Cases & Service Orchestration

```

| Clean Architecture Layer                        | Package Location                 | Architectural Role & Primary Purpose                                                                                                                                                       | Allowed Dependencies                         |
|-------------------------------------------------|----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------|
| **Inner Core** *(Pure Domain)*                  | `domain/`                        | Business enums, core value objects, and pure domain model contracts. **Must remain framework-agnostic** (no `@Entity`, `@RestController`, or `@Autowired`).                                | None (Java standard library only)            |
| **Middle Layer** *(Application Use Cases)*      | `service/`<br><br>`dto/`         | • `service/`: Business use-cases, orchestration, and domain rules.<br><br>• `dto/`: Request payload objects (`*Request.java`) and Response objects (`*Response.java`) for API transport.   | `domain/`, `dto/`, `repository/`<br><br>     |
| **Driving Adapters** *(Inbound Infrastructure)* | `controller/`<br><br>`security/` | • `controller/`: REST endpoints, HTTP request validation (`@Valid`), and response mapping.<br><br>• `security/`: Infrastructure for Web Security, JWT filtering, and blacklist mechanisms. | `service/`, `dto/`, `common/`<br><br>        |
| **Driven Adapters** *(Outbound Infrastructure)* | `repository/`<br><br>`entity/`   | • `entity/`: JPA/Hibernate entities mapping database tables and relational schemas.<br><br>• `repository/`: Spring Data JPA / JDBC repositories.                                           | `entity/`, `domain/` (for enums/value types) |

#### 3. Boundary & Dependency Rules

* **Fundamental Dependency Direction:** Dependencies must **always point inward**:

$$\text{controller / repository / entity / security} \longrightarrow \text{service / dto} \longrightarrow \text{domain}$$

* **No Direct Repository Access Across Domains:** A domain (e.g., `sales`) must **never** autowire or call another domain's repository (`inventory/repository`) directly. Communication between domains must occur through public interfaces in the target domain’s `service/` package.
* **Domain Autonomy:** A domain should function independently as a logical module. Soft boundaries ensure smooth future microservice extraction.

#### 4. Cross-Cutting Concerns & Common Utilities

* **Unified API Responses:** Every REST endpoint must wrap its output in `common.dto.ApiResponse<T>`.
* **Exception Handling:** Throw domain-specific exceptions (e.g., `InsufficientProductStockException`) and handle them globally using `@RestControllerAdvice` in `common.exception`.
* **Money & Precision:** Always use `BigDecimal` for monetary amounts (prices, unit costs, cash calculations) across DTOs, domain models, and entities.

---

## 🚀 Development Workflow

We use a **hybrid development model**: running the MySQL database in Docker while executing the Spring Boot application locally in IntelliJ for rapid iteration and interactive debugging.

### Prerequisites

* Java 21 JDK
* Docker Desktop & Docker Compose
* IntelliJ IDEA (or preferred IDE)

### 1. Start Database Infrastructure

To launch MySQL 8.4 without running the packaged application container:

```bash
docker compose up -d db
```

### 2. Run Application Locally

Execute `BluejayApplication.java` directly from IntelliJ. The application binds to `http://localhost:8080`.

**Port Conflict Notice:** If port `8080` is already in use, ensure the `bluejay_api` container is stopped:
```bash
docker stop bluejay_api
```


### 3. Containerized Deployment (Full Docker Stack)

To run both the Spring Boot API and MySQL database entirely within containerized environments:

```bash
# Build fresh Java binaries and launch all containers
./mvnw clean package -DskipTests
docker compose up -d --build
```

To tear down containers, wipe persistent database volumes (forces fresh Flyway migrations), and prune local images:

```bash
docker compose down -v --rmi local
```

---

## 🔒 Authentication & API Testing

Testing the system health & API endpoints can be done via **cURL** or using **Postman**.

### User Login (Obtain JWT Token)

Authenticate against the service to generate a Bearer token.

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Password123!"
  }'
```

### Expected Response (`200 OK`):

```json
{
  "success": true,
  "message": "Authentication successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "username": "admin",
    "expiresIn": 86400000
  }
}
```

---

## 🧪 JUnit Testing

Run unit and integration tests using Maven:

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=SaleServiceImplTest
```

---

## 📖 API Documentation (Scalar UI)

The application provides interactive, human-readable API documentation rendered by **Scalar**, powered by our live OpenAPI 3.0 specification.

* **Interactive Web Docs:** `http://localhost:8080/scalar`
* **Raw OpenAPI JSON Spec:** `http://localhost:8080/v3/api-docs`