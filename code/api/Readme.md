# Bluejay Spring Boot API

A production-ready Spring Boot 4.1 backend built with Java 21, Spring Security, Spring Data JPA, Flyway, and MySQL 8.4.

---

## 🛠️ Tech Stack

* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 4.1 (`web`, `data-jpa`, `actuator`, `security`)
* **Database:** MySQL 8.4
* **Database Migrations:** Flyway
* **Security:** JWT (Stateless)
* **Build Tool:** Maven

---

## 🏛️ Architecture & Domain-Driven Design (DDD)

The application follows Domain-Driven Design principles organized by distinct bounded contexts under `edu.bu.metcs673.bluejay`. Each domain package encapsulates its own web layer, application/service layer, and persistence components:

```text
edu.bu.metcs673.bluejay/
├── auth/                      # Authentication & Identity Domain
│   ├── controller/            # REST controllers (auth endpoints)
│   ├── dto/                   # Request/Response Data Transfer Objects
│   ├── entity/                # Domain entities (User, Role)
│   ├── repository/            # JPA repositories (UserRepository)
│   ├── security/              # Security filters & JWT handling
│   └── service/               # Authentication domain logic & UserDetailsService
├── common/                      # Cross-cutting concerns & shared infrastructure
│   ├── config/                # Global SecurityFilterChain & App configurations
│   ├── dto/                   # Unified response wrappers (ApiResponse<T>)
│   └── exception/             # Global error handlers
└── [domain]/                  # Future domains (e.g., sales, products)
```

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

---

## 🔑 Environment Variables

The application relies on the following environment variables (with defaults configured in `application.yaml`):

| Variable                     | Description                 | Default Value                            |
|------------------------------|-----------------------------|------------------------------------------|
| `SPRING_DATASOURCE_URL`      | JDBC Connection String      | `jdbc:mysql://localhost:3306/bluejay_db` |
| `SPRING_DATASOURCE_USERNAME` | Database User               | `bluejay_user`                           |
| `SPRING_DATASOURCE_PASSWORD` | Database Password           | `bluejay_password`                       |
| `JWT_SECRET`                 | Secret key for signing JWTs | *(Required in Production)*               |
| `JWT_EXPIRATION_MS`          | Token validity duration     | `86400000` (24 Hours)                    |

---

## 🏥 Observability & Health Check

Application and database health are exposed via **Spring Boot Actuator**.

**Localhost URL:** http://localhost:8080/actuator/health

### Health Endpoint

```http
GET /actuator/health
```

### Sample of Healthy Response (`200 OK`)

```json
{
  "components": {
    "db": {
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      },
      "status": "UP"
    }
  },
  "status": "UP"
}
```

---

## 🗄️ Database Migrations (Flyway)

Flyway handles versioned database migrations located in `src/main/resources/db/migration/`.

* **File Naming Convention:** `V<VERSION>__<description>.sql` (e.g., `V1__init_schema.sql`).
* **Auto-Execution:** Migrations run automatically on application startup.
* **Manual Execution via Maven:**
```bash
./mvnw flyway:migrate
```

---

## 🧪 Testing

Run unit and integration tests using Maven:

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=AuthServiceTest
```

---
## 🎨 Code Style & Standards

To maintain consistency across the entire codebase, the project follows standard Java formatting rules backed by our shared code style scheme in `api/docs/`.

### 1. IntelliJ Code Style Setup
1. Go to **Settings/Preferences** > **Editor** > **Code Style** > **Java**.
2. Click the **Gear Icon** next to *Scheme* > **Import Scheme** > **IntelliJ IDEA code style XML**.
3. Select the code style XML file located at:
```text
api/docs/bu-code-style.xml
```
4. Click **Apply** to ensure line wrapping, indentation (4 spaces), and import ordering match team standards.

### 2. Auto-Formatting Rules
Before committing code, run the native formatter:
* **Mac:** `Cmd + Option + L` (Reformat Code) + `Control + Option + O` (Optimize Imports)
* **Windows/Linux:** `Ctrl + Alt + L` + `Ctrl + Alt + O`

### 3. Key Design Conventions
* **Imports:** Do not use wildcard imports (`import java.util.*`). If wildcard imports are still occurring after importing the code style scheme, manually set the threshold: 
  * Go to **Settings/Preferences** > **Editor** > **Code Style** > **Java** > **Imports** tab. 
  * Set **Class count to use import with '\*'** and **Names count to use static import with '\*'** to `99`.
* **Lombok Usage:** Use `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, and `@Builder` to minimize boilerplate. Avoid `@Data` on JPA entities to prevent unexpected LazyInitialization exceptions in `equals()` and `hashCode()`.
* **Standardized Responses:** All REST controllers must wrap payloads in the generic `ApiResponse<T>` wrapper located in `common.dto`.
* **Explicit Annotations:** Always annotate Spring components explicitly (`@RestController`, `@Service`, `@Repository`).
* **Dependency Injection:** Use constructor injection via `@RequiredArgsConstructor` instead of field `@Autowired`.

---

## 🛠️ Docker Troubleshooting & Resets

### Access Denied (SQL State 28000 / Error 1045)

MySQL stores credentials in its initial volume creation. If database credentials or host permissions change in `docker-compose.yml`, reset the volume to initialize a fresh database:

```bash
docker compose down -v
docker compose up -d db
```

### Running Full Stack in Docker

To validate end-to-end containerized execution (app + DB):

```bash
docker compose up -d --build
```

