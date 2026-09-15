-- V2__seed_test_users.sql

-- 1. Insert Base Permissions
INSERT INTO permissions (name, description) VALUES
    ('INVENTORY_READ', 'View products and stock levels'),
    ('INVENTORY_WRITE', 'Add, update, or remove products and adjust stock'),
    ('SALE_EXECUTE', 'Process sales transactions at the POS terminal'),
    ('REPORTS_VIEW', 'Access sales analytics and inventory financial reports'),
    ('USER_MANAGEMENT', 'Create, update, and manage system user accounts');

-- 2. Map Permissions to Roles
-- Admin gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ROLE_ADMIN';

-- Manager gets inventory, sales, and reporting permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_MANAGER'
  AND p.name IN ('INVENTORY_READ', 'INVENTORY_WRITE', 'SALE_EXECUTE', 'REPORTS_VIEW');

-- Cashier gets sales execution and basic inventory read
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_CASHIER'
  AND p.name IN ('INVENTORY_READ', 'SALE_EXECUTE');

-- 3. Seed Default Test Accounts (Password: Password123!)
-- Hash: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a
INSERT INTO users (id, username, password_hash, enabled) VALUES
    ('11111111-1111-1111-1111-111111111111', 'admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', TRUE),
    ('22222222-2222-2222-2222-222222222222', 'manager', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', TRUE),
    ('33333333-3333-3333-3333-333333333333', 'cashier', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', TRUE);

-- 4. Map Users to Roles
INSERT INTO user_roles (user_id, role_id)
SELECT '11111111-1111-1111-1111-111111111111', id FROM roles WHERE name = 'ROLE_ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT '22222222-2222-2222-2222-222222222222', id FROM roles WHERE name = 'ROLE_MANAGER';

INSERT INTO user_roles (user_id, role_id)
SELECT '33333333-3333-3333-3333-333333333333', id FROM roles WHERE name = 'ROLE_CASHIER';