-- Story #29 manual QA seed data (NOT a Flyway migration; run by hand against a local DB).
-- AI-ASSISTED: YES (Claude) - sample sales for checking GET /api/v1/reports/sales.
--
-- Run (from the code/ folder, with docker compose up):
--   Get-Content api\docs\qa\story-29-sales-seed.sql | docker exec -i bluejay_mysql mysql -uroot -prootpassword bluejay_db
--
-- Safe to run more than once (INSERT IGNORE on fixed ids).
-- IDs are valid UUIDs (prefix 29000000-) because the Product entity reads products.id as a UUID;
-- non-UUID ids would break the Products and Inventory pages.
-- Uses its own QA products: the V3-seeded products all have 0.00 prices.
--
-- Expected results:
--   2026-10-01 .. 2026-10-31 -> revenue 62.48, cost 40.48, net profit 22.00, 3 transactions, 7 units
--     (includes a sale at 23:59 on Oct 31, which checks that the end date is inclusive)
--   2026-10-05 .. 2026-10-05 -> revenue 29.98, cost 19.98, net profit 10.00, 1 transaction,  2 units
--   2026-09-01 .. 2026-09-30 -> revenue 14.99, cost  9.99, net profit  5.00, 1 transaction,  1 unit
--   2026-11-01 .. 2026-11-30 -> all zeros (empty range)

INSERT IGNORE INTO products (id, barcode, name, cost_price, sale_price, current_stock) VALUES
    ('29000000-0000-0000-0000-00000000a001', 'QA29-1001', 'QA Premium Rice 5kg', 9.99, 14.99, 50),
    ('29000000-0000-0000-0000-00000000a002',  'QA29-1002', 'QA Cooking Oil 2L',   4.25,  6.50, 50);

-- Cashier from V2__seed_test_users.sql
INSERT IGNORE INTO sale_transactions (id, receipt_number, cashier_id, grand_total, payment_method, transaction_date) VALUES
    ('29000000-0000-0000-0000-00000000b001', 'QA29-R-0001', '33333333-3333-3333-3333-333333333333', 14.99, 'CASH', '2026-09-20 10:00:00'),
    ('29000000-0000-0000-0000-00000000b002', 'QA29-R-0002', '33333333-3333-3333-3333-333333333333', 29.98, 'CARD', '2026-10-05 09:15:00'),
    ('29000000-0000-0000-0000-00000000b003', 'QA29-R-0003', '33333333-3333-3333-3333-333333333333', 13.00, 'CASH', '2026-10-17 14:30:00'),
    ('29000000-0000-0000-0000-00000000b004', 'QA29-R-0004', '33333333-3333-3333-3333-333333333333', 19.50, 'CASH', '2026-10-31 23:59:00');

-- unit_cost / unit_price are the snapshots at the time of sale
INSERT IGNORE INTO sale_items (id, transaction_id, product_id, quantity, unit_cost, unit_price, subtotal) VALUES
    (29001, '29000000-0000-0000-0000-00000000b001',  '29000000-0000-0000-0000-00000000a001', 1, 9.99, 14.99, 14.99),
    (29002, '29000000-0000-0000-0000-00000000b002', '29000000-0000-0000-0000-00000000a001', 2, 9.99, 14.99, 29.98),
    (29003, '29000000-0000-0000-0000-00000000b003', '29000000-0000-0000-0000-00000000a002',  2, 4.25,  6.50, 13.00),
    (29004, '29000000-0000-0000-0000-00000000b004', '29000000-0000-0000-0000-00000000a002',  3, 4.00,  6.50, 19.50);

-- To remove the QA data afterwards:
--   DELETE FROM sale_transactions WHERE id LIKE '29000000-%';   -- sale_items cascade
--   DELETE FROM products WHERE id LIKE '29000000-%';