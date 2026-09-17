# Database Schema Changelog & Migration Guide

## Executive Summary

This document logs the schema migration path from the legacy SQLite prototype (`jpos`) to our standardized, production-ready MySQL 8.4 database managed via Flyway.

he structural changes below transition the application from isolated text-based entities to a fully relational, ACID-compliant backend with explicit auditing and scalable Role-Based Access Control (RBAC). **All core functional requirements and reporting capabilities from the original SQLite prototype—including sales analysis, real-time stock deduction, historical inventory auditing, and analytical reporting—are fully preserved and modernized.**

---

## Architectural Mapping to System Requirements

To ensure complete alignment with project requirements, the table below maps each system functional requirement directly to its underlying database schema implementation:

| Functional Requirement | Legacy SQLite Approach | MySQL 8.4 Migration Design | Architectural Advantage |
| :--- | :--- | :--- | :--- |
| **Real-Time Stock Deduction** | Manual calculations on `inventory` records | Direct `products.current_stock` column with `INT` precision | Enables $O(1)$ stock checks during scanning and supports MySQL InnoDB pessimistic row-locking (`SELECT ... FOR UPDATE`) during checkout to prevent concurrent race conditions. |
| **Product-Level Sale Analysis**<br>*(Quantity, Total Cost, Profit)* | Derived from `sale_items` + `products` | Immutable `sale_items` snapshot (`unit_cost`, `unit_price`, `subtotal`) joined with `sale_transactions` | Freezes historical product costs at the moment of sale. Store price or cost updates tomorrow will never skew past profit and cost calculations for historical reports. |
| **Historical Inventory Reporting by Date Range** | Unstructured `inventory` table entries | Dedicated `inventory_movements` audit log (`quantity_change`, `movement_type`, `reference_id`, `created_at`) | Captures exact historical stock state and movement trajectory (sales, restocks, damage, returns) over any target timeframe without slowing down real-time sales operations. |
| **Advanced Analytics & AI Forecasting** *(Nice-to-Have)* | Non-standardized data formats | Time-series structured `inventory_movements` + `sale_items` | Provides clean, indexed time-series data suitable for export to CSV/Excel or training ML/AI restock demand forecasting models. |

---

## Key Schema Changes & Rationale

### 1. Primary Keys & Data Types
* **UUID Implementation:** Replaced non-standard `TEXT` identifiers with standard `VARCHAR(36)` UUIDs for top-level domain entities (`users`, `products`, `sale_transactions`) to enable decentralized ID generation across services and prevent sequential ID enumeration.
* **Numeric Primary Keys:** Adopted `BIGINT AUTO_INCREMENT` for metadata and reference tables (`roles`, `permissions`, `categories`, `sale_items`, `inventory_movements`) to maximize indexing efficiency.
* **Exact Financial Precision:** Upgraded all monetary fields (`cost_price`, `sale_price`, `grand_total`, `unit_cost`, `unit_price`, `subtotal`) from floating-point `REAL` to fixed-point `DECIMAL(12,2)` to eliminate floating-point rounding errors during currency calculations.

### 2. User Authorization & Security (RBAC)
* **Normalized Roles & Permissions:** Replaced the legacy `CHECK (role IN ('Admin', 'Manager', 'Cashier'))` constraint on `users` with a multi-tenant RBAC design:
    * `roles`: System roles (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_CASHIER`) with custom `description` attributes.
    * `permissions`: Action rights (e.g., `INVENTORY_WRITE`, `SALE_EXECUTE`).
    * `user_roles` & `role_permissions`: Association tables enabling Spring Security method-level protection.

### 3. Dynamic Catalog & Dual-Tier Inventory Design
* **Category Management:** Replaced the hardcoded `CHECK` constraint on categories (`food`, `beverage`, etc.) with a relational `categories` table (`name`, `description`). Store managers can dynamically add, edit, or remove categories via API/UI.
* **Dual-Tier Stock Tracking:** Split stock management into two complementary layers:
    1. **Hot State (`products.current_stock`):** Holds live count for rapid checkout verification.
    2. **Audit State (`inventory_movements`):** Tracks point-in-time stock deltas (`'SALE'`, `'RESTOCK'`, `'ADJUSTMENT'`, `'RETURN'`) for historical audit reporting and demand forecasting.

### 4. Sales Auditing & Transaction Integrity
* **Cashier Attribution:** Added `cashier_id` (FK to `users`) to `sale_transactions` to track user accountability per transaction.
* **Payment Method Tracking:** Introduced `payment_method` (`CASH`, `CARD`, `DIGITAL_WALLET`) on receipts for drawer balancing.
* **Audit Timestamps:** Upgraded soft `DATE` fields to `DATETIME` with `DEFAULT CURRENT_TIMESTAMP` and `ON UPDATE CURRENT_TIMESTAMP` triggers for precise audit logging.

---

## Detailed Entity Mapping Matrix

| Entity | SQLite Baseline (`jpos`) | MySQL 8.4 Target (`V1__initial_schema.sql`) | Primary Reason for Modification |
| :--- | :--- | :--- | :--- |
| **`users`** | `id` (TEXT), `username`, `role` (CHECK), `password` | `id` (VARCHAR 36), `username`, `password_hash`, `enabled`, `created_at` + `user_roles` mapping | Supports secure password hashing, multi-role assignment, and user account status management. |
| **`roles` / `permissions`** | Implicit strings in CHECK constraints | `roles` (`id`, `name`, `description`), `permissions` (`id`, `name`, `description`) | Enables fine-grained Spring Security access controls and administrative role management. |
| **`categories`** | Hardcoded `CHECK` list on `products` table | `id` (BIGINT PK), `name` (UNIQUE), `description` | Decouples categorization into a user-configurable entity without requiring DDL migration scripts. |
| **`products`** | `id` (TEXT), `barcode`, `name`, `product_category` (CHECK) | `id` (VARCHAR 36), `barcode`, `name`, `category_id` (FK), `cost_price`, `sale_price`, `current_stock` (INT), timestamps | Normalizes catalog lookup, enforces decimal accuracy, and supports real-time stock reads. |
| **`inventory`** | `id` (TEXT), `number_in_stock` (REAL), `cost`, `product_id`, `created_at` | Transformed into **`inventory_movements`**: `id` (BIGINT), `product_id` (FK), `quantity_change` (INT), `movement_type`, `reference_id`, `created_at` | Provides structured time-series event logging for stock movements while maintaining $O(1)$ real-time stock lookups. |
| **`sale_transactions`** | `transaction_id` (TEXT), `receipt_number`, `grand_total` (REAL), `transaction_date` (DATE) | `id` (VARCHAR 36 PK), `receipt_number`, `cashier_id` (FK), `grand_total` (DECIMAL), `payment_method`, `transaction_date` (DATETIME) | Binds transaction receipts to cashier accounts, payment types, and exact timestamp audits. |
| **`sale_items`** | Composite PK (`product_id`, `transaction_id`), `quantity` (REAL), `cost` (REAL), `price` (REAL) | `id` (BIGINT PK), `transaction_id` (FK), `product_id` (FK), `quantity` (INT), `unit_cost`, `unit_price`, `subtotal` (DECIMAL) | Guarantees exact historical cost/price snapshots and improves surrogate key indexing performance. |

---

## Migration Verification Checklist

* [ ] Verify Flyway applies `V1__initial_schema.sql` cleanly on a fresh MySQL 8.4 Docker container.
* [ ] Confirm foreign key constraints prevent orphaned records in `sale_items` and `user_roles`.
* [ ] Validate that decimal values preserve exact 2-decimal precision on insert (`19.99`).