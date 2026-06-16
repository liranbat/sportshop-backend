-- Single-size products (ONE_SIZE). JOIN drops rows whose product wasn't seeded;
-- ON CONFLICT keeps re-runs safe against an already-seeded DB.
INSERT INTO product_stock (product_id, size, quantity, low_stock_threshold)
SELECT p.id, v.size, v.quantity, v.threshold
FROM (VALUES
    -- Basketball balls
    ('Spalding NBA Basketball',                     'ONE_SIZE', 25, 5),
    ('Spalding Street Basketball -- Black Edition', 'ONE_SIZE', 18, 5),
    ('Rainbow Multi-Color Basketball',              'ONE_SIZE',  0, 5), -- OUT_OF_STOCK
    ('Maccabi Tel Aviv Souvenir Basketball',        'ONE_SIZE',  0, 5), -- OUT_OF_STOCK

    -- Soccer balls
    ('Nike Academy Soccer Ball',                    'ONE_SIZE', 30, 5),
    ('UCL 2025/26 Official Match Ball',             'ONE_SIZE',  2, 5), -- LOW_STOCK
    ('Classic Red Match Soccer Ball',               'ONE_SIZE', 22, 5),
    ('Manchester United Club Ball',                 'ONE_SIZE',  4, 5), -- LOW_STOCK
    ('Spain National Team Supporters Ball',         'ONE_SIZE',  3, 5), -- LOW_STOCK
    ('adidas Tango River Plate Ball',               'ONE_SIZE', 20, 5),

    -- Volleyball balls
    ('Mikasa V200W Official Match Volleyball',      'ONE_SIZE',  0, 5), -- OUT_OF_STOCK
    ('Vermont Beach Volleyball',                    'ONE_SIZE', 16, 5),

    -- Tennis rackets
    ('Pro Carbon Tennis Racket',                    'ONE_SIZE', 11, 5),
    ('Lightweight Composite Tennis Racket',         'ONE_SIZE',  6, 5),
    ('Dunlop CX 200 Tour',                          'ONE_SIZE',  4, 5), -- LOW_STOCK

    -- Padel rackets
    ('Wilson Bela Pro Padel Racket',                'ONE_SIZE',  2, 5), -- LOW_STOCK
    ('Wilson Tour Padel Racket',                    'ONE_SIZE', 14, 5),

    -- Ping-pong
    ('Retractable Table Tennis Net',                'ONE_SIZE', 35, 5),
    ('Beginner Table Tennis Racket',                'ONE_SIZE',  5, 5), -- LOW_STOCK
    ('King Feel Carbon Pro Racket',                 'ONE_SIZE',  9, 5)
) AS v(product_name, size, quantity, threshold)
JOIN products p ON p.name = v.product_name
ON CONFLICT (product_id, size) DO NOTHING;

-- Multi-size products (shoes), EU 39..46 each.
INSERT INTO product_stock (product_id, size, quantity, low_stock_threshold)
SELECT p.id, v.size, v.quantity, v.threshold
FROM (VALUES
    -- Nike Ja 3 Basketball Shoes
    ('Nike Ja 3 Basketball Shoes', '39',  0, 5), -- OUT_OF_STOCK
    ('Nike Ja 3 Basketball Shoes', '40',  2, 5), -- LOW_STOCK
    ('Nike Ja 3 Basketball Shoes', '41', 12, 5),
    ('Nike Ja 3 Basketball Shoes', '42', 18, 5),
    ('Nike Ja 3 Basketball Shoes', '43', 22, 5),
    ('Nike Ja 3 Basketball Shoes', '44', 15, 5),
    ('Nike Ja 3 Basketball Shoes', '45',  8, 5),
    ('Nike Ja 3 Basketball Shoes', '46',  0, 5), -- OUT_OF_STOCK

    -- Nike LeBron Witness 8
    ('Nike LeBron Witness 8',      '39', 10, 5),
    ('Nike LeBron Witness 8',      '40',  4, 5), -- LOW_STOCK
    ('Nike LeBron Witness 8',      '41',  0, 5), -- OUT_OF_STOCK
    ('Nike LeBron Witness 8',      '42', 20, 5),
    ('Nike LeBron Witness 8',      '43', 18, 5),
    ('Nike LeBron Witness 8',      '44', 25, 5),
    ('Nike LeBron Witness 8',      '45', 12, 5),
    ('Nike LeBron Witness 8',      '46',  3, 5), -- LOW_STOCK

    -- Nike Mercurial Superfly 9 "Believe" (fully OUT_OF_STOCK)
    ('Nike Mercurial Superfly 9 "Believe"', '39',  0, 5),
    ('Nike Mercurial Superfly 9 "Believe"', '40',  0, 5),
    ('Nike Mercurial Superfly 9 "Believe"', '41',  0, 5),
    ('Nike Mercurial Superfly 9 "Believe"', '42',  0, 5),
    ('Nike Mercurial Superfly 9 "Believe"', '43',  0, 5),
    ('Nike Mercurial Superfly 9 "Believe"', '44',  0, 5),
    ('Nike Mercurial Superfly 9 "Believe"', '45',  0, 5),
    ('Nike Mercurial Superfly 9 "Believe"', '46',  0, 5),

    -- adidas F50 Elite FG
    ('adidas F50 Elite FG', '39', 12, 5),
    ('adidas F50 Elite FG', '40', 14, 5),
    ('adidas F50 Elite FG', '41', 18, 5),
    ('adidas F50 Elite FG', '42', 20, 5),
    ('adidas F50 Elite FG', '43', 16, 5),
    ('adidas F50 Elite FG', '44', 11, 5),
    ('adidas F50 Elite FG', '45',  2, 5), -- LOW_STOCK
    ('adidas F50 Elite FG', '46',  0, 5), -- OUT_OF_STOCK

    -- adidas Predator Elite FG
    ('adidas Predator Elite FG', '39',  8, 5),
    ('adidas Predator Elite FG', '40',  0, 5), -- OUT_OF_STOCK
    ('adidas Predator Elite FG', '41', 15, 5),
    ('adidas Predator Elite FG', '42', 20, 5),
    ('adidas Predator Elite FG', '43', 18, 5),
    ('adidas Predator Elite FG', '44', 22, 5),
    ('adidas Predator Elite FG', '45', 10, 5),
    ('adidas Predator Elite FG', '46',  5, 5), -- LOW_STOCK

    -- adidas Predator Accuracy.1 "Solar Energy"
    ('adidas Predator Accuracy.1 "Solar Energy"', '39', 14, 5),
    ('adidas Predator Accuracy.1 "Solar Energy"', '40', 16, 5),
    ('adidas Predator Accuracy.1 "Solar Energy"', '41', 12, 5),
    ('adidas Predator Accuracy.1 "Solar Energy"', '42', 20, 5),
    ('adidas Predator Accuracy.1 "Solar Energy"', '43', 18, 5),
    ('adidas Predator Accuracy.1 "Solar Energy"', '44', 25, 5),
    ('adidas Predator Accuracy.1 "Solar Energy"', '45',  8, 5),
    ('adidas Predator Accuracy.1 "Solar Energy"', '46',  6, 5),

    -- adidas Copa Mundial
    ('adidas Copa Mundial', '39',  0, 5), -- OUT_OF_STOCK
    ('adidas Copa Mundial', '40',  3, 5), -- LOW_STOCK
    ('adidas Copa Mundial', '41', 10, 5),
    ('adidas Copa Mundial', '42', 15, 5),
    ('adidas Copa Mundial', '43', 18, 5),
    ('adidas Copa Mundial', '44', 22, 5),
    ('adidas Copa Mundial', '45', 14, 5),
    ('adidas Copa Mundial', '46',  4, 5) -- LOW_STOCK
) AS v(product_name, size, quantity, threshold)
JOIN products p ON p.name = v.product_name
ON CONFLICT (product_id, size) DO NOTHING;
