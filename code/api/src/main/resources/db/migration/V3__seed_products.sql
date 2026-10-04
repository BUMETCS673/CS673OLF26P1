-- V3__seed_products

-- 1. Add some inital categories
INSERT INTO `categories` VALUES (1, 'Pharmacy', 'Medicines');

-- 2. Add some products
INSERT INTO `products` VALUES ('a3c8b819-9883-459f-9a7c-0dba22fe2e1a', '6935951301705', 'Amoxicillin Capsules', 1, 3.00, 5.00, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Add sale_transactions
INSERT INTO `sale_transactions` VALUES ('bb7a401d-2f6d-473e-916a-113147748a0c', '123456', '33333333-3333-3333-3333-333333333333', 10.00, 'CASH', CURRENT_TIMESTAMP);

-- 4. Add sale_item
INSERT INTO `sale_items` VALUES (1, 'bb7a401d-2f6d-473e-916a-113147748a0c', 'a3c8b819-9883-459f-9a7c-0dba22fe2e1a', 2, 3.00, 5.00, 10.00);
