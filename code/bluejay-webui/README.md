# Bluejay Web UI

Bluejay is an Angular 21 web application for grocery operations. It provides the application shell and feature areas for dashboard, products, inventory, sales, reports, users, and authentication.

---

## 🚀 Iteration 2 Features & Highlights

During **Iteration 2**, the frontend moved beyond placeholder routes to deliver active feature modules and route security:

* **Route Security & Access Control (`app.routes.ts`):** Integrated functional `canActivate` route guards (`authGuard`, `adminGuard`, `adminAndManagerGuard`).
  * `/products`, `/inventory`, `/reports`: Protected for **Admin** and **Manager** roles.
  * `/users`: Strictly protected for **Admin** users.
* **User Management Feature (`features/users`):**
  * Active directory page featuring live search filtering, column sorting (username, ID, creation date, enabled status), and 403/401 error messaging.
  * User creation form component supporting role selection (`ROLE_CASHIER`, `ROLE_MANAGER`, `ROLE_ADMIN`) and reactive validation.
* **Inventory Reporting Feature (`features/reports`):**
  * Replaced placeholders with a functional inventory report page using modern Angular Signals (`items`, `state`, `totalUnits`).
  * Features loading, forbidden (403), and error states, with formatted tables for product stock counts and unit costs.
* **Inventory & Product Management Pages:**
  * `InventoryPage`: Assembled standalone layout composing inventory health, stock list, and stock entry components.
  * `ProductListPage`: Built composed view containing product search, listing, and product entry forms.

---

## Get Started

Requirements: Node.js and npm. The app expects the Bluejay backend to be available at `http://localhost:8080` for API requests.

```sh
npm install
npm start
```

Open `http://localhost:4200`. The development server forwards `/api` requests to `http://localhost:8080` using `proxy.conf.json`. Start the backend separately. For local sign-in, the backend should provide the `admin` / `admin123` account. Authentication requests use `/api/v1/auth`.

## Next Steps

1. Start the backend, then run the frontend with `npm start`.
2. Choose the feature area for your work and keep feature-specific pages, components, services, models, and routes together.
3. Put code in `shared` only when it is genuinely reused across features. Coordinate changes to the app shell, global navigation, or root routes with the infrastructure owner.
4. Run the relevant tests and build before submitting your changes.

Useful commands:

```sh
npm test -- --watch=false
npm run build
```

## Project Structure

```text
src/app/
├── core/
│   ├── auth/           # Authentication and idle-session services
│   ├── guards/         # Route guards (authGuard, adminGuard, adminAndManagerGuard)
│   ├── layout/         # App shell, sidebar, and global navigation
│   └── services/       # Application-wide infrastructure services
├── features/
│   ├── auth/           # Login page and auth routes
│   ├── dashboard/      # Dashboard metrics & summary routes
│   ├── inventory/      # Inventory health, stock lists, & stock entry forms
│   ├── products/       # Product search, catalog lists, & creation forms
│   ├── reports/        # Inventory reports with signals & error state handling
│   ├── sales/          # Point-of-sale checkout feature
│   └── users/          # User directory (search/sort) & user creation form
└── shared/
  	├── components/     # Reusable UI
  	├── directives/     # Reusable directives
  	├── models/         # Reusable types (e.g. ApiResponse)
  	└── pipes/          # Reusable pipes
```

Each business feature owns its route file and `pages/`, `components/`, `models/`, and `services/` folders. Some folders are currently empty and are kept in the repository with `.gitkeep` files. Feature routes are lazy-loaded by `src/app/app.routes.ts`; the login feature keeps the `/login` path. Application-wide authentication and idle-session behavior is implemented under `src/app/core/auth/`.

## Development Guidelines

### Application Shell

The application/infrastructure owner maintains `core/layout/app-shell`, the sidebar, global navigation, and global layout. Feature developers should normally not edit these files. The shell contains no business-specific behavior.

### Feature Ownership

Each developer should work primarily within one feature:

| Developer | Area |
| --- | --- |
| 1 | Dashboard |
| 2 | Products |
| 3 | Inventory |
| 4 | Sales |
| 5 | Users |
| 6 | Shared infrastructure, API integration, or coordination |

A feature owns its pages, feature-specific components, services, models, and route configuration. Keep feature routes in that feature's `<feature-name>.routes.ts` file.

### Shared Code

Put only genuinely reusable components, directives, pipes, and models in `shared`. If code is used by one feature, keep it inside that feature. Avoid generic abstractions without a concrete reuse need.

### Feature Development

Use this structure for new features:

```text
features/<feature-name>/
├── components/       # Reusable UI used only by this feature
├── pages/            # Routed, page-level standalone components
├── services/         # Feature API and orchestration services
├── models/           # Feature-specific types and interfaces
└── <feature-name>.routes.ts
```

Use the feature's route file to map local paths to page components. The root router lazy-loads that route configuration. Put API calls in feature services, feature data types in `models`, and route definitions in `<feature-name>.routes.ts`.

There is no custom feature schematic. Create the standard folders and route file as needed. Angular CLI commands for common files:

```sh
ng generate component features/<feature-name>/pages/<page-name> --standalone --style=scss
ng generate component features/<feature-name>/components/<component-name> --standalone --style=scss
ng generate service features/<feature-name>/services/<service-name>
```

### Git Workflow

Use one feature branch per work item and submit changes through pull requests. Keep changes within the owned feature, avoid unrelated edits to another developer's feature, and coordinate changes to the shell or root routes with the infrastructure owner. This keeps most parallel work in separate directories and reduces merge conflicts.

### Current Scope

This scaffold does not add new authentication or authorization rules, route guards, backend API integrations for the placeholder features, business functionality, or deployment configuration. Existing login, logout, and idle-session behavior is retained. Route guards must not be considered a substitute for backend authorization.
