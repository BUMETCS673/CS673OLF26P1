
# 🪶 BlueJay POS – Web & AI Point of Sale System

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Angular](https://img.shields.io/badge/Angular-16%2B-red)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-blue)

**BlueJay POS** is an enterprise-grade, multi-tenant web application designed for small-to-medium grocery and retail operations. Evolving from a legacy desktop terminal prototype, BlueJay delivers an intuitive Angular web interface, a robust Spring Boot RESTful API, role-based access security, and cutting-edge **Spring AI** integration for automated supplier receipt scanning and inventory forecasting.

---

## 📌 Project Overview & Vision

Small-to-medium retail businesses frequently struggle with fragmented software, manual stock entries, and desktop-locked POS terminals. **BlueJay POS** bridges this gap by offering a secure, centralized web platform that streamlines:
* **Point-of-Sale Checkout:** Fast cashier web interface with automated stock updates and real-time low-stock counter warnings.
* **AI-Driven Restock Ingestion:** OCR receipt and invoice parsing via Spring AI to automatically update wholesale inventory stock and pricing.
* **Multi-User Concurrency:** Concurrency-safe relational database design preventing transaction collisions across multiple counter registers.
* **Store Analytics:** Executive dashboard reporting revenue, cost of goods sold (COGS), profit margins, and remaining stock valuation over custom date ranges.

---

## 🛠️ Technology Stack

| Layer | Technology / Framework |
| :--- | :--- |
| **Frontend** | Angular, TypeScript, HTML5/SCSS, Bootstrap / Tailwind CSS |
| **Backend** | Java 17+, Spring Boot (REST API, Spring Security, Spring Data JPA) |
| **AI Integration** | Spring AI (OpenAI / Ollama API for OCR receipt parsing & demand forecasting) |
| **Database** | TBD |
| **Build & CI/CD** | Apache Maven, GitHub Actions, JaCoCo, JUnit 5, Mockito |
| **IDEs** | IntelliJ IDEA (Backend preference) / VS Code (Frontend preference) |

---

## 🔒 Security Highlights

* **Stateless JWT Authentication:** Secure session management for Angular SPA and Spring Boot REST API communication.
* **Password Hashing:** Strong cryptographic password encryption utilizing **BCrypt**.
* **Role-Based Access Control (RBAC):** Strict method- and endpoint-level authorization separating `ROLE_ADMIN`, `ROLE_MANAGER`, and `ROLE_CASHIER`.
* **API Validation:** Payload sanitization, CORS protection, and secure file-upload handling for AI receipt uploads.

---

## 👥 Team & Roles

* **Sara Orion** – *Team Leader & Security Leader* (`srorion@bu.edu`)
* **Krizma Nagi** – *Requirement Leader* (`nagikriz@bu.edu`)
* **Kimleng Lim** – *Design & Implementation Leader* (`kimleng@bu.edu`)
* **Andy Chang** – *Configuration Leader* (`achang3@bu.edu`)
* **Italia Tran** – *QA Leader* (`intran@bu.edu`)

---

## 📁 Repository Structure

```text
TBD
```

---

## 🚀 Getting Started (Development Setup)

### Prerequisites

* **Java Development Kit (JDK):** Version 17 or higher
* **Node.js & NPM:** Node LTS (v18+) and NPM
* **Angular CLI:** `npm install -g @angular/cli`
* **PostgreSQL / H2:** Local relational database instance

### Backend Setup (Spring Boot)

TBD

### Frontend Setup (Angular)

TBD

---

## 🔄 Branching Strategy & Workflow

* **`main`**: Protected branch reserved for production-ready, verified releases.
* **`develop`**: Primary integration branch for active sprint development.
* **`feature/<feature-name>`**: Topic branches for isolated user story implementations.
* **Pull Requests (PRs):** Direct commits to `main` and `develop` are restricted. All feature code must be submitted via PR, pass automated CI test suites, and receive peer review approval prior to merging.

---

## 📝 License & Acknowledgments

This project is developed as part of the BU MET CS673 Software Engineering course. Built upon foundational domain concepts from the BU MET CS622 term project prototype baseline.

