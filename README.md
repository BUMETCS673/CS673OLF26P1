# 🪶 BlueJay POS – Web Point of Sale System

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Java](https://img.shields.io/badge/Java-21%2B-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1%2B-green)
![Angular](https://img.shields.io/badge/Angular-21%2B-red)
![MySQL](https://img.shields.io/badge/MySQL-8.4%2B-blue)

**BlueJay POS** is an enterprise-grade web application designed for small-to-medium grocery and retail operations. 
Evolving from a desktop terminal prototype, BlueJay delivers a modern Angular web interface, a robust Spring Boot RESTful API, 
role-based access security (stateless JWT with token revocation blacklisting), Flyway database migrations, 
and interactive **Scalar** API documentation.

---

## 📌 Project Overview & Vision

Small-to-medium retail businesses frequently struggle with fragmented software, manual stock entries, and desktop-locked POS terminals. **BlueJay POS** bridges this gap by offering a secure, centralized web platform that streamlines:

* **Point-of-Sale Checkout:** Fast cashier web interface with automated stock deduction, line-item subtotals, cash payment validation, and change breakdown by denomination.
* **Inventory & Restock Tracking:** Automated inventory updates, restock entry forms, inline price updates, low-stock threshold triggers, and audit movement logs.
* **Multi-User Concurrency & Security:** Concurrency-safe relational database design preventing transaction collisions across registers, backed by role-based access control (RBAC).
* **Store Analytics & Reporting:** Comprehensive executive reports detailing gross revenue, stock cost baselines, net profit margins, unit movement, and per-product profitability.

---

## 🚀 Final Release Features & Highlights

For the final release, the team delivered full end-to-end integration across all feature modules:

* **Role-Based Access Control (RBAC) & Route Security:**
  * Backend endpoints are secured using `@PreAuthorize` annotations based on user roles (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_CASHIER`).
  * Token revocation blacklist service (`TokenBlacklistService`) revokes active JWTs upon logout.
  * Frontend navigation routes are guarded (`adminGuard`, `adminAndManagerGuard`, `authGuard`) to restrict unauthorized UI access.
* **Standardized API Response Pipeline:** All Spring Boot REST API responses across domain controllers are wrapped inside a consistent `ApiResponse<T>` envelope for uniform data and error contracts.
* **User Management & Directory:** Administrators can create and update system user accounts, assign roles (`ADMIN`, `MANAGER`, `CASHIER`), and search/sort active system users.
* **Product Catalog & Inventory Management:** Full product catalog management with category mapping, barcode lookup, inventory health metrics, low-stock threshold alerts, and restock entries with audit logging.
* **Point-of-Sale Checkout:** Cashier interface for itemized sales, grand total calculations, cash validation, change due calculations, denomination breakdowns ($100 down to penny), and receipt printing.
* **Reporting & Analytics Suite:**
  * **Inventory Report:** Stock counts, unit costs, and total stock valuation.
  * **Consolidated Sales Report:** Overall revenue, cost of goods sold (COGS), net profit, unit count, and transaction count over configurable date ranges.
  * **Product Sales Analysis:** Per-product breakdown of units sold, revenue, stock cost, and profit margins sorted by performance.

---

## 🛠️ Technology Stack

| Layer | Technology / Framework |
| --- | --- |
| **Frontend** | Angular 21, TypeScript, HTML5/SCSS, Angular Signals |
| **Backend** | Java 21 (LTS), Spring Boot 4.1 (`web`, `data-jpa`, `actuator`, `security`) |
| **API Docs & Testing** | SpringDoc OpenAPI 3.1, Scalar UI, Postman, JUnit 5 |
| **Database & Migrations** | MySQL 8.4 (InnoDB), Flyway Database Migrations |
| **Security** | Stateless JWT (BCrypt Password Hashing, Token Blacklist, RBAC) |
| **Containerization** | Docker, Docker Compose, NGINX Reverse Proxy |
| **Build Tools** | Apache Maven, npm / Angular CLI |

---

## 👥 Team & Roles

* **Sara Orion** – *Team Leader & Security Leader*
* **Krizma Nagi** – *Requirement Leader*
* **Kimleng Lim** – *Design & Implementation Leader*
* **Andy Chang** – *Configuration Leader*
* **Italia Tran** – *QA Leader*

---

## 📁 Monorepo Structure

```text
code/
├── docker-compose.yml         # Multi-container orchestrator (MySQL, API, Web)
├── api/                       # Spring Boot 4 + Java 21 REST API
│   ├── README.md              # Backend API README documentation
│   ├── Dockerfile             # Multi-stage JDK 21 build definition
│   ├── pom.xml                # Maven dependencies (Spring Boot, MySQL, Flyway, Scalar)
│   ├── docs/                  # Postman collections, environments, PlantUML diagrams & IDE code styles
│   └── src/                   # Backend Java source code & Flyway migrations
└── bluejay-webui/             # Angular Web Application
    ├── README.md              # Frontend Web UI README documentation
    ├── Dockerfile             # Multi-stage build (Node.js compile -> NGINX host)
    ├── nginx.conf             # NGINX reverse proxy setup (routes /api to backend)
    ├── package.json           # Frontend dependencies & scripts
    └── src/                   # Frontend TypeScript, HTML, & CSS source
```

---

## 🚀 Quick Start (Development Setup)

### System Prerequisites

* **Java Development Kit (JDK):** Version 21
* **Node.js & npm:** Node.js `v20.19.0+` or `>= v24.0.0` with npm `>= 8.0.0`
* **Angular CLI:** `npm install -g @angular/cli@21`
* **Docker Desktop & Docker Compose**

### 1. Launch Containerized Stack (Full Docker)

To spin up MySQL 8.4, the Spring Boot API, and the Angular Web UI simultaneously:

```bash
docker compose up -d --build
```

Access services:

* **Web UI (Angular):** [http://localhost:4200/](http://localhost:4200/)
* **API Health Actuator:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
* **OpenAPI Spec:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
* **Scalar API Reference:** [http://localhost:8080/scalar](http://localhost:8080/scalar)
* **Web UI (Deployed):** [http://3.145.103.249:4200](https://www.google.com/search?q=http://3.145.103.249:4200)


### 2. Launch Hybrid Development Mode (Rapid Iteration)

* **Start Database:** `docker compose up -d db`
* **Backend API:** Run `BluejayApplication.java` from IntelliJ or execute `./mvnw spring-boot:run` inside `api/`.
* **Frontend Web UI:** Run `npm start` inside `bluejay-webui/` to launch at `http://localhost:4200` with local proxy configuration.

---

## 🔄 Branching Strategy & Workflow

* **`main`**: Protected branch reserved for production-ready, verified releases.
* **`develop`**: Primary integration branch for active sprint development.
* **`feature/<feature-name>`**: Topic branches for isolated user story implementations.
* **Pull Requests (PRs):** Direct commits to `main` and `develop` are restricted. All feature code must be submitted via PR, pass automated CI test suites, and receive peer review approval prior to merging.


---

## 📝 License & Acknowledgments

This project is developed as part of the BU MET CS673 Software Engineering course. Built upon foundational domain concepts from the BU MET CS622 term project prototype baseline [JPOS](https://www.google.com/search?q=https://github.com/kim-0x/jpos).
