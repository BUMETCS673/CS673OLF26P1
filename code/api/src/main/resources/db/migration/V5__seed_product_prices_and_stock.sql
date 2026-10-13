-- AI-USAGE SUMMARY
-- Tools: GitHub Copilot
-- Overall AI Contribution: 90%
-- AI-Assisted Areas: Generated products for sales testing
-- Human Contributions: Prompted what was needed for the products and how to generate the numbers
-- Notes: Generated products prices and quantity
-- Authors: Italia Tran

UPDATE products
SET sale_price = CASE barcode
    WHEN '045678901231' THEN 0.79
    WHEN '001234567890' THEN 6.49
    WHEN '051000012347' THEN 4.99
    WHEN '012345123455' THEN 1.29
    WHEN '036000291452' THEN 7.99
    WHEN '078901234563' THEN 2.49
    WHEN '081297010115' THEN 12.99
    WHEN '091827364502' THEN 8.99
    WHEN '021000010104' THEN 19.99
    WHEN '045678901234' THEN 14.99
    ELSE sale_price
END
WHERE sale_price = 0.00
  AND barcode IN (
      '045678901231',
      '001234567890',
      '051000012347',
      '012345123455',
      '036000291452',
      '078901234563',
      '081297010115',
      '091827364502',
      '021000010104',
      '045678901234'
  );

UPDATE products
SET current_stock = CASE barcode
    WHEN '045678901231' THEN 48
    WHEN '001234567890' THEN 24
    WHEN '051000012347' THEN 18
    WHEN '012345123455' THEN 48
    WHEN '036000291452' THEN 24
    WHEN '078901234563' THEN 24
    WHEN '081297010115' THEN 12
    WHEN '091827364502' THEN 18
    WHEN '021000010104' THEN 12
    WHEN '045678901234' THEN 12
    ELSE current_stock
END
WHERE current_stock = 0
  AND barcode IN (
      '045678901231',
      '001234567890',
      '051000012347',
      '012345123455',
      '036000291452',
      '078901234563',
      '081297010115',
      '091827364502',
      '021000010104',
      '045678901234'
  );