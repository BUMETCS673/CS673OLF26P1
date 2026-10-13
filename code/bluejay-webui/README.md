# Bluejay Web UI

Bluejay is a modern Angular 21 web application for retail point-of-sale and grocery operations. 
It provides an application shell and complete feature modules for dashboard metrics, product catalog management, 
inventory stock tracking, checkout processing, reporting analytics, user administration, and session security.

---

## 🚀 Final Release Features & Highlights

The final release expands the frontend beyond initial scaffolds into a fully connected, reactive single-page application (SPA) 
integrated with the backend API endpoints:

### 1. Role-Based Access Control & Route Security
* **Functional Route Guards (`app.routes.ts`):** Enforces client-side security via `authGuard`, `adminGuard`, and `adminAndManagerGuard`.
  * `/products`, `/inventory`: Accessible to **Admin** and **Manager** roles.
  * `/sales`: Accessible to **Admin**, **Manager**, and **Cashier** roles for checkout operations.
  * `/reports`: Accessible to **Admin** (sales & product profitability reports) and **Manager** (inventory reports).
  * `/users`: Strictly restricted to **Admin** users.

### 2. Feature Module Breakdown

* **Authentication & Session Security (`features/auth`, `core/auth`):**
  * Login page with reactive forms and JWT bearer token acquisition.
  * In-memory token storage with automatic request interception via HTTP interceptors.
  * Idle-session monitoring and safe logout handling (revoking active JWTs via `/api/v1/auth/logout`).

* **User Management (`features/users`):**
  * Live searchable directory with column sorting (username, ID, creation date, status) and 401/403 error states.
  * Interactive user creation and patch modals supporting role assignment (`ROLE_CASHIER`, `ROLE_MANAGER`, `ROLE_ADMIN`).

* **Product Catalog (`features/products`):**
  * Filtered product listing with pagination, search terms, and category selection.
  * Product barcode lookup interface and quick creation modal with category assignment.
  * Inline low-stock threshold configuration.

* **Inventory & Restock Operations (`features/inventory`):**
  * Real-time stock health metrics, low-stock alerts, and stock quantity overview.
  * Restock entry form supporting cost/price updates and audit movement tracking.
  * Inline product sale price modification.

* **Point-of-Sale & Checkout (`features/sales`):**
  * Fast barcode scanning and cart aggregation with line-item totals.
  * Cash payment processing with real-time cash received validation and change calculation.
  * Itemized change breakdown by denomination ($100, $50, $20, $10, $5, $1, quarters, dimes, nickels, pennies).
  * Transaction completion modal and receipt printing view.

* **Reports & Analytics (`features/reports`):**
  * **Inventory Report:** Stock levels, unit costs, and total valuation using Angular Signals (`items`, `state`, `totalUnits`).
  * **Sales Performance Report:** Revenue, total cost, net profit, transaction count, and units sold over configurable date ranges.
  * **Product Profitability Report:** Per-product breakdown of units sold, total revenue, stock cost, and profit margin sorted by performance.

---

## 🛠️ Get Started

### Requirements
* **Node.js** (v20+ recommended) and **npm**.
* **Bluejay Backend API** running at `http://localhost:8080`.

### Local Installation & Startup

1. Install dependencies:
```sh
npm install
```

2. Start the development server:
```sh
npm start
```

3. Open `http://localhost:4200` in your browser.

> **Proxy Note:** The development server forwards all `/api` requests to `http://localhost:8080` using `proxy.conf.json`. Ensure the backend API is running. Default seed login credentials: `admin` / `Password123!`.

### Useful Commands

```sh
# Run unit tests
npm test -- --watch=false

# Production build
npm run build
```

---

## 📁 Project Structure

```text
src/app/
├── core/
│   ├── auth/           # Authentication, JWT handling, and idle-session services
│   ├── guards/         # Functional route guards (authGuard, adminGuard, adminAndManagerGuard)
│   ├── layout/         # Application shell, sidebar, and global header navigation
│   └── services/       # Global infrastructure services
├── features/
│   ├── auth/           # Login page and auth routes
│   ├── dashboard/      # Dashboard metrics & summary widgets
│   ├── inventory/      # Stock tracking, health metrics, & restock entry forms
│   ├── products/       # Product search, catalog lists, & creation forms
│   ├── reports/        # Inventory, sales, and product profitability reports
│   ├── sales/          # Point-of-sale cart, cash checkout, change breakdown, & receipts
│   └── users/          # User directory (search/sort/edit) & user creation forms
└── shared/
    ├── components/     # Reusable UI components (modals, alert banners, tables)
    ├── directives/     # Shared Angular directives
    ├── models/         # Global API contracts (ApiResponse<T>, Page<T>)
    └── pipes/          # Currency and date formatting pipes
```

---

## 📐 Architecture & Development Guidelines

### Feature-Based Module Design

Each domain feature owns its own `pages/`, `components/`, `services/`, `models/`, and local `<feature-name>.routes.ts` file. All feature routes are lazily loaded by the root router (`app.routes.ts`).

### Shared Code Constraints

Only genuinely reusable components, directives, pipes, and data types reside in `shared/`. If logic or types are specific to a single domain (e.g., checkout cart items in `sales`), keep them inside that feature package.

### Standard CLI Generation Commands

```sh
# Standalone Page Component
ng generate component features/<feature-name>/pages/<page-name> --standalone --style=scss

# Feature UI Component
ng generate component features/<feature-name>/components/<component-name> --standalone --style=scss

# Feature Service
ng generate service features/<feature-name>/services/<service-name>
```

