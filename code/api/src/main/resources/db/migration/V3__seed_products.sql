-- Seed categories and products from src/main/resources/data/products.csv.
-- The CSV description column is not persisted because the products table
-- does not currently include a description field.

INSERT INTO categories (name) VALUES
    ('beverage'),
    ('dairy'),
    ('fruit'),
    ('household'),
    ('soft drink');

INSERT INTO products (
    id,
    barcode,
    name,
    category_id,
    cost_price,
    sale_price,
    current_stock
) VALUES
    (
        'fc0d434e-5301-4f19-b8c5-6b4638c6475c',
        '045678901231',
        'Lemons',
        (SELECT id FROM categories WHERE name = 'fruit'),
        0.00,
        0.00,
        0
    ),
    (
        'fdb859c8-d5bd-4450-b5c4-ab7ac112d826',
        '001234567890',
        'Nature''s Best Organic Whole Milk (1 Gallon)',
        (SELECT id FROM categories WHERE name = 'dairy'),
        0.00,
        0.00,
        0
    ),
    (
        'b261027f-e10d-4a22-9a60-69a6e4e97301',
        '051000012347',
        'Raspberries',
        (SELECT id FROM categories WHERE name = 'fruit'),
        0.00,
        0.00,
        0
    ),
    (
        '557bf027-1b43-47b3-8222-a3459c8581e7',
        '012345123455',
        'Spring Water',
        (SELECT id FROM categories WHERE name = 'beverage'),
        0.00,
        0.00,
        0
    ),
    (
        'a0508bf4-0a8e-4a9f-897a-2c09ed7075d4',
        '036000291452',
        'Zero Calories Coca Cola',
        (SELECT id FROM categories WHERE name = 'soft drink'),
        0.00,
        0.00,
        0
    ),
    (
        'e5457146-26de-49e8-a70c-5b0b9c0350ef',
        '078901234563',
        'Canada Dry Ginger Ale',
        (SELECT id FROM categories WHERE name = 'soft drink'),
        0.00,
        0.00,
        0
    ),
    (
        '1682d2a6-6949-4883-b239-c91a99f21f29',
        '081297010115',
        'HEX Performance Stain & Stink Remover',
        (SELECT id FROM categories WHERE name = 'household'),
        0.00,
        0.00,
        0
    ),
    (
        'cee1f79b-5d10-4079-89bf-56217d01642c',
        '091827364502',
        'Paper Towels',
        (SELECT id FROM categories WHERE name = 'household'),
        0.00,
        0.00,
        0
    ),
    (
        'fb6844cb-30ae-4ecf-abf4-278aa1736b43',
        '021000010104',
        'Bounty Paper Towels',
        (SELECT id FROM categories WHERE name = 'household'),
        0.00,
        0.00,
        0
    ),
    (
        '43595f60-f7c4-4523-af75-90d58ec54fa2',
        '045678901234',
        'Cascade Platinum Dishwasher Pods Fresh',
        (SELECT id FROM categories WHERE name = 'household'),
        0.00,
        0.00,
        0
    );
