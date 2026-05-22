-- Renames "Paddle"/"paddle" to "Racket"/"racket" in the Ping Pong product names and descriptions
-- (V9 seeded them as "Paddle", but locally we refer to them as rackets).

UPDATE products
SET name        = REPLACE(name, 'Paddle', 'Racket'),
    description = REPLACE(description, 'paddle', 'racket')
WHERE category_id = (SELECT id FROM categories WHERE name = 'Ping Pong');
