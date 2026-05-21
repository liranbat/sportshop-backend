INSERT INTO products (name, description, category_id, is_multi_size, image_filename, price)
VALUES
    (
        'Spalding NBA Basketball',
        'Official-size indoor/outdoor basketball with composite leather grip.',
        (SELECT id FROM categories WHERE name = 'Basketball Balls'),
        FALSE, 'basketball-spalding.jpg', 29.99
    ),
    (
        'Spalding Street Basketball -- Black Edition',
        'Durable rubber street basketball, all-surface tested, matte black finish.',
        (SELECT id FROM categories WHERE name = 'Basketball Balls'),
        FALSE, 'basketball-spalding-black.jpg', 34.99
    ),
    (
        'Nike Academy Soccer Ball',
        'Match-grade training ball with reinforced TPU casing and butyl bladder.',
        (SELECT id FROM categories WHERE name = 'Soccer Balls'),
        FALSE, 'soccer-ball-nike.jpg', 39.99
    ),
    (
        'UCL 2025/26 Official Match Ball',
        'UEFA Champions League 2025/26 season official thermally-bonded match ball.',
        (SELECT id FROM categories WHERE name = 'Soccer Balls'),
        FALSE, 'soccer-ball-ucl-26.jpg', 149.99
    );
