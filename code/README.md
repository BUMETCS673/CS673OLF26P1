# Software Engineering Project — Monorepo Architecture

Welcome to the main code repository. This project is structured as a monorepo containing our **Spring Boot REST API** backend, **Angular** frontend web application, and a **Docker Compose** orchestration setup for local development, testing, and deployment.

---

## 📁 Repository Structure

```text
code/
├── docker-compose.yml         # Multi-container orchestrator (MySQL, API, Web)
├── README.md                  # Project setup and developer instructions
├── api/                       # Spring Boot 4 + Java 21 REST API
│   ├── Dockerfile             # Multi-stage JDK 21 build definition
│   ├── pom.xml                # Maven dependencies (Spring Boot, MySQL, Flyway)
│   └── src/                   # Backend Java source code & Flyway migrations
└── web/                       # Angular Web Application (To Be Setup)
    ├── Dockerfile             # Multi-stage build (Node.js compile -> NGINX host)
    ├── nginx.conf             # NGINX reverse proxy setup (routes /api to backend)
    ├── package.json           # Frontend dependencies & scripts
    └── src/                   # Frontend TypeScript, HTML, & CSS source
```

### Subfolder Overview

* **`api/` (Spring Boot Backend):**
  * **Framework:** Spring Boot 4.1+ running on **Java 21**.
  * **Database Migrations:** Managed via **Flyway**. Database schema scripts are located in `api/src/main/resources/db/migration/` (e.g., `V1__init.sql`).
  * **Database Driver:** MySQL Connector/J connected to the MySQL 8.4 container.

* **`web/` (Angular Frontend):** *To be setup still*
  * **Framework:** Angular running on Node.js 18+.
  * **Production Server:** **NGINX** (Alpine). In Docker mode, NGINX serves compiled static assets and reverse-proxies API requests (`/api/*`) to the backend container, preventing CORS issues.

---

## 🛠️ Prerequisites

Before getting started, ensure you have the following installed on your machine:

* **[Docker Desktop](https://www.docker.com/products/docker-desktop/)** (Must be running before starting services)
* **[Git](https://git-scm.com/)**
* *(Optional for local runtime)* **Java 21 JDK** & **Node.js 18+**

---

## 🚀 Quick Start (Docker Compose)

The fastest way to spin up the entire application stack (MySQL 8.4 + Spring Boot API + Angular Web) is using Docker Compose from the root `code/` directory.

### Command Line Interface (CLI)

1. Open your terminal in the `code/` root directory.
2. Build and start all services:
   ```bash
   docker compose up --build
   ```
3. Access running services:
   * **Web Application (Angular):** [http://localhost](http://localhost)
   * **REST API (Spring Boot):** [http://localhost:8080](http://localhost:8080) (or via proxy at [http://localhost/api](http://localhost/api))
   * **Database (MySQL 8.4):** `localhost:3306` (`user: root`, `password: rootpassword`, `database: app_db`)

4. Stop all services:
   ```bash
   docker compose down
   ```

---

## 💻 IDE Setup & Docker Integration

Developers are free to use either **IntelliJ IDEA** or **Visual Studio Code**. You should open the **root `code/` directory** as your project workspace in your editor of choice.

### Option A: IntelliJ IDEA (Community or Ultimate)

1. **Open Project:** Select **File > Open** and choose the root `code/` folder. IntelliJ will automatically detect the Maven module inside `api/`.
2. **Enable Docker Plugin:** Ensure the native **Docker** plugin is enabled under **Settings/Preferences > Plugins**.
3. **Configure Docker Connection:**
   * Go to **Settings/Preferences > Build, Execution, Deployment > Docker**.
   * Click **`+`** to add a new connection for Docker Desktop and click **OK**.
4. **Run Docker Compose:**
   * Open `docker-compose.yml` in the editor.
   * Click the **double green play icon (`▶▶`)** at the top line of the file (or right-click `docker-compose.yml` in the Project Explorer tree and choose **Run 'docker-compose.yml'**).
   * Manage logs, inspect ports, and stop/restart containers using the **Services** panel (`Alt+8` / `Cmd+8`).

---

### Option B: Visual Studio Code

1. **Open Workspace:** Select **File > Open Folder** and choose the root `code/` folder.
2. **Install Recommended Extensions:**
   * **Docker** (by Microsoft)
   * **Extension Pack for Java** (by Red Hat)
   * **Angular Language Service**
3. **Run Docker Compose:**
   * Open `docker-compose.yml`.
   * Right-click inside the editor window and select **Compose Up** (or **Compose Up - Build** to rebuild images).
4. **Manage Containers:**
   * Click the **Docker icon** on the left Activity Bar.
   * Expand the **CONTAINERS** pane to view running logs, inspect ports, or stop services.

---

## 🔄 Development Workflows

### 1. Full-Stack Docker Mode (Recommended for Demos & PR Testing)
Run all three services inside Docker containers using `docker compose up --build`. This mirrors production conditions and ensures zero local configuration conflicts across the team.

### 2. Hybrid Development Mode (Recommended for Active Coding)
Rebuilding containers on every code change can slow down rapid iteration. For day-to-day coding:

1. Spin up **only** the MySQL database container:
   ```bash
   docker compose up db -d
   ```
2. **Backend Devs:** Open `api/` in your IDE and launch Spring Boot using your IDE's runner or `./mvnw spring-boot:run`. Flyway will automatically run migrations against `localhost:3306`.
3. **Frontend Devs:** Open `web/` in terminal and run `npm start` (`ng serve`) for instant hot-reloading at `http://localhost:4200`.

---

## 📝 Flyway Database Migrations

Database schema changes are automatically applied when the Spring Boot application boots up.

* **Migration Folder:** `api/src/main/resources/db/migration/`
* **Naming Convention:** `V<Version>__<Description>.sql` (Note: **Two** underscores).
  * *Example:* `V1__create_initial_schema.sql`
  * *Example:* `V2__add_users_table.sql`

---

## 🛑 Git & Environment Rules

* **Do NOT commit IDE files:** Local settings (`.idea/`, `.vscode/`, `*.iml`) are managed in `.gitignore`.
* **Do NOT commit build artifacts:** Target directories (`api/target/`, `web/dist/`, `web/node_modules/`) are excluded.
* Keep `docker-compose.yml` environment configurations updated if new variables or secrets are added.
