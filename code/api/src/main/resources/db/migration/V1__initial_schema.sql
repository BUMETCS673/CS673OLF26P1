-- V1__initial_schema.sql

-- ==========================================
-- 1. AUTHENTICATION & RBAC SCHEMA
-- ==========================================
CREATE TABLE roles (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE permissions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE role_permissions (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE users (
    id            VARCHAR(36) PRIMARY KEY,
    username      VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE user_roles (
    user_id VARCHAR(36) NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================================
-- 2. CATALOG & INVENTORY SCHEMA
-- ==========================================
CREATE TABLE categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE products (
    id            VARCHAR(36) PRIMARY KEY,
    barcode       VARCHAR(100) UNIQUE,
    name          VARCHAR(255) NOT NULL,
    category_id   BIGINT,
    cost_price    DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    sale_price    DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    current_stock INT NOT NULL DEFAULT 0,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE movement_types (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB;

INSERT INTO movement_types (code, description) VALUES
    ('SALE', 'Stock deducted due to a customer purchase'),
    ('RESTOCK', 'Stock added via supplier purchase or inventory delivery'),
    ('ADJUSTMENT', 'Manual correction due to damage, theft, or audit reconciliation'),
    ('RETURN', 'Stock returned by customer and added back to active inventory');

CREATE TABLE inventory_movements (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id       VARCHAR(36) NOT NULL,
    quantity_change  INT NOT NULL,
    movement_type_id BIGINT NOT NULL,
    reference_id     VARCHAR(36),
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movement_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_movement_type FOREIGN KEY (movement_type_id) REFERENCES movement_types(id)
) ENGINE=InnoDB;

-- ==========================================
-- 3. SALES & TRANSACTION AUDIT SCHEMA
-- ==========================================
CREATE TABLE sale_transactions (
    id               VARCHAR(36) PRIMARY KEY,
    receipt_number   VARCHAR(100) NOT NULL UNIQUE,
    cashier_id       VARCHAR(36),
    grand_total      DECIMAL(12,2) NOT NULL,
    payment_method   VARCHAR(50) NOT NULL DEFAULT 'CASH',
    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sale_cashier FOREIGN KEY (cashier_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE sale_items (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL,
    product_id     VARCHAR(36) NOT NULL,
    quantity       INT NOT NULL DEFAULT 1,
    unit_cost      DECIMAL(12,2) NOT NULL,
    unit_price     DECIMAL(12,2) NOT NULL,
    subtotal       DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_item_transaction FOREIGN KEY (transaction_id) REFERENCES sale_transactions(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

-- ==========================================
-- 4. SEED INITIAL RBAC DATA
-- ==========================================
INSERT INTO roles (name, description) VALUES
    ('ROLE_ADMIN', 'Full system access including user management and system settings'),
    ('ROLE_MANAGER', 'Access to inventory management, pricing, and analytics reports'),
    ('ROLE_CASHIER', 'Access to POS terminal for executing sales and printing receipts');