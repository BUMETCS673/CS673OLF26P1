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

### 🔍 Troubleshooting: Flyway "Unknown database 'bluejay_db'" (Error Code 1049)

If the application crashes during deployment with `SQL State : 42000 / Error Code : 1049 (Unknown database 'bluejay_db')`, it means a stale Docker volume exists on your machine from a previous run.

MySQL **only** triggers the automatic database creation (`MYSQL_DATABASE`) on its **very first boot**. If an old data volume is present, MySQL skips initialization entirely, causing Flyway to fail.

#### The Fix:
Do not just restart the containers. You must explicitly purge the persistent data state using our standard tear-down command:

```bash
docker compose down -v --rmi local
```

**Why this fixes it:**
The **`-v` flag** is the critical piece here—it destroys the cached local MySQL volumes. On the next `docker compose up`, Docker will be forced to create a brand new database instance and automatically provision `bluejay_db` for Flyway.

*(Alternatively, you can manually delete the volume via the **Volumes** menu bar inside **Docker Desktop** before restarting the services).*
---

## 🛠️ IntelliJ Docker Run Configuration

To prevent stale code caching and enforce smooth local builds directly inside IntelliJ:

1. Open **Run/Debug Configurations** $\rightarrow$ **Docker** $\rightarrow$ **Compose**.
2. Set **Compose files** to `./docker-compose.yml`.
3. Under **Modify options** (top right):
   * Enable **Remove orphans on 'down'**.
   * Enable **Remove volumes on 'down'**.
4. Set **Remove images on 'down'** to **`Local`** *(preserves the official MySQL 8.4 image while forcing local code image rebuilds)*.
5. Under **Before launch**, click **`+`** $\rightarrow$ **Run Maven Goal** and set:
```text
clean package -DskipTests
```
---

## 🔒 Authentication & API Testing

Testing the system health & api endpoints can be done via **cURL** or using **Postman**.

### Postman Setup
* Open Postman $\rightarrow$ **Import** both `Bluejay-API.postman_collection.json` and `Bluejay-Local.postman_environment.json` from `api/docs/postman/`.
* Select the **Bluejay-Local** environment.


### Health Check

Verify system status and database connectivity.

#### cURL:
```bash
curl -X GET http://localhost:8080/actuator/health
```

#### Postman: 
Run `Health -> GET Actuator Health` in the `Bluejay-API` collection.

#### Expected Response (`200 OK`):
```json
{ "status": "UP" }
```

### User Login (Obtain JWT Token)

Authenticate against the service to generate a Bearer token.

#### cURL:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Password123!"
  }'
```

#### Postman
Open the `Auth -> POST Login` request. Under the **Body** tab, select `raw` and add the following JSON:

```json
{
  "username": "admin",
  "password": "Password123!"
}
```

Under the **Scripts** tab, select `post-response` add the following javascript:

```javascript
if (pm.response.code === 200) {
    var response = pm.response.json();
    if (response.success && response.data && response.data.token) {
        pm.environment.set("jwtToken", response.data.token);
        console.log("JWT token successfully set in environment.");
    }
}
```

Send the `POST` request.

#### Expected Response (`200 OK`):
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

### Access Protected Endpoints

Pass the retrieved JWT in the `Authorization` header to access RBAC-protected routes.

#### cURL:
```bash
curl -X GET http://localhost:8080/api/v1/protected-resource \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN_HERE>"
```

#### Postman: 
Any request executed under the `Bluejay-API` collection will automatically inherit the token stored in `{{jwtToken}}` via collection-level Bearer Auth.

---

## 🧪 Junit Testing

Run unit and integration tests using Maven:

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=AuthServiceTest
```
---

## 🔑 Environment Variables

The application relies on the following environment variables (with defaults configured in `application.yaml`):

| Variable                     | Description                 | Default Value                                |
|------------------------------|-----------------------------|----------------------------------------------|
| `SPRING_DATASOURCE_URL`      | JDBC Connection String      | `jdbc:mysql://bluejay_mysql:3306/bluejay_db` |
| `SPRING_DATASOURCE_USERNAME` | Database User               | `bluejay_user`                               |
| `SPRING_DATASOURCE_PASSWORD` | Database Password           | `bluejay_password`                           |
| `JWT_SECRET`                 | Secret key for signing JWTs | *(Required in Production)*                   |
| `JWT_EXPIRATION_MS`          | Token validity duration     | `86400000` (24 Hours)                        |

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
## 🎨 Code Style & Standards

To maintain consistency across the entire codebase, the project follows standard Java formatting rules backed by our shared code style scheme in `api/docs/`.

### IntelliJ Code Style Setup
1. Go to **Settings/Preferences** > **Editor** > **Code Style** > **Java**.
2. Click the **Gear Icon** next to *Scheme* > **Import Scheme** > **IntelliJ IDEA code style XML**.
3. Select the code style XML file located at:
```text
api/docs/bu-code-style.xml
```
4. Click **Apply** to ensure line wrapping, indentation (4 spaces), and import ordering match team standards.

### Auto-Formatting Rules
Before committing code, run the native formatter:
* **Mac:** `Cmd + Option + L` (Reformat Code) + `Control + Option + O` (Optimize Imports)
* **Windows/Linux:** `Ctrl + Alt + L` + `Ctrl + Alt + O`

### Key Design Conventions
* **Imports:** Do not use wildcard imports (`import java.util.*`). If wildcard imports are still occurring after importing the code style scheme, manually set the threshold: 
  * Go to **Settings/Preferences** > **Editor** > **Code Style** > **Java** > **Imports** tab. 
  * Set **Class count to use import with '\*'** and **Names count to use static import with '\*'** to `99`.
* **Lombok Usage:** Use `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, and `@Builder` to minimize boilerplate. Avoid `@Data` on JPA entities to prevent unexpected LazyInitialization exceptions in `equals()` and `hashCode()`.
* **Standardized Responses:** All REST controllers must wrap payloads in the generic `ApiResponse<T>` wrapper located in `common.dto`.
* **Explicit Annotations:** Always annotate Spring components explicitly (`@RestController`, `@Service`, `@Repository`).
* **Dependency Injection:** Use constructor injection via `@RequiredArgsConstructor` instead of field `@Autowired`.

---

## 🛠️ Troubleshooting & Environment Resets

### Lombok & Compilation Errors (`variable not initialized`)

If running `./mvnw clean package` throws errors about uninitialized `final` fields:

* **IDE Setup:** Verify that **Enable annotation processing** is checked under **Settings $\rightarrow$ Build, Execution, Deployment $\rightarrow$ Compiler $\rightarrow$ Annotation Processors** in IntelliJ.

### Database Access Denied / Stale Credentials (`SQLState: 28000`)

MySQL stores credentials during initial volume creation. If database passwords or seed scripts change, reset the volume to initialize a fresh database:

```bash
docker compose down -v
docker compose up -d db
```

### Full Stack Docker Validation

To test end-to-end containerized execution (app + database) with a completely fresh build:

```bash
./mvnw clean package -DskipTests
docker compose up -d --build
```