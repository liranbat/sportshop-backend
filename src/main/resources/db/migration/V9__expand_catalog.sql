INSERT INTO categories (name, icon_filename) VALUES
    ('Basketball', 'basketball.svg'),
    ('Soccer',     'soccer.svg'),
    ('Volleyball', 'volleyball.svg'),
    ('Tennis',     'tennis.svg'),
    ('Padel',      'padel.svg'),
    ('Ping Pong',  'ping-pong.svg');

UPDATE products
SET category_id = (SELECT id FROM categories WHERE name = 'Basketball')
WHERE category_id IN (SELECT id FROM categories WHERE name LIKE 'Basketball %');

UPDATE products
SET category_id = (SELECT id FROM categories WHERE name = 'Soccer')
WHERE category_id IN (SELECT id FROM categories WHERE name LIKE 'Soccer %');

DELETE FROM categories WHERE name LIKE 'Basketball %' OR name LIKE 'Soccer %';

INSERT INTO products (name, description, category_id, is_multi_size, image_filename, price)
VALUES
    -- Basketball
    (
        'Rainbow Multi-Color Basketball',
        'Vibrant 8-panel rubber basketball. Great grip for outdoor play and skill drills.',
        (SELECT id FROM categories WHERE name = 'Basketball'),
        FALSE, 'basketball-ball-multi-color.jpg', 24.99
    ),
    (
        'Maccabi Tel Aviv Souvenir Basketball',
        'Yellow-and-blue souvenir basketball featuring the Maccabi Tel Aviv crest.',
        (SELECT id FROM categories WHERE name = 'Basketball'),
        FALSE, 'basketball-ball-maccabi-tlv.jpg', 34.99
    ),
    (
        'Nike Ja 3 Basketball Shoes',
        'Signature Ja Morant low-top with responsive Cushlon foam and a grippy herringbone outsole.',
        (SELECT id FROM categories WHERE name = 'Basketball'),
        TRUE, 'nike-ja-3-shoes-basketball.jpg', 139.99
    ),
    (
        'Nike LeBron Witness 8',
        'LeBron-line everyday performance basketball shoe with a Max Air heel unit.',
        (SELECT id FROM categories WHERE name = 'Basketball'),
        TRUE, 'NIKE-LeBron-Witness-8-basketball-shoes-FB2239-005-Black-University-Red-White-1.jpg', 109.99
    ),

    -- Soccer
    (
        'Classic Red Match Soccer Ball',
        'All-red 32-panel match ball with a butyl bladder and machine-stitched seams.',
        (SELECT id FROM categories WHERE name = 'Soccer'),
        FALSE, 'soccer-ball-red.jpg', 29.99
    ),
    (
        'Manchester United Club Ball',
        'Manchester United-branded training ball, regulation size 5, with the club crest front and back.',
        (SELECT id FROM categories WHERE name = 'Soccer'),
        FALSE, 'soccer-ball-manchester-united.jpg', 39.99
    ),
    (
        'Spain National Team Supporters Ball',
        'Spain flag-styled supporters ball, regulation size 5, soft-touch surface.',
        (SELECT id FROM categories WHERE name = 'Soccer'),
        FALSE, 'soccer-ball-spain.png', 34.99
    ),
    (
        'adidas Tango River Plate Ball',
        'Retro reissue of the iconic Tango pentagon design from the 1978 World Cup era.',
        (SELECT id FROM categories WHERE name = 'Soccer'),
        FALSE, 'soccer-ball-tango.jpg', 49.99
    ),
    (
        'Nike Mercurial Superfly 9 "Believe"',
        'Speed-tier FG cleat with a Vaporposite+ upper and an Air Zoom unit underfoot.',
        (SELECT id FROM categories WHERE name = 'Soccer'),
        TRUE, 'nike-superfly-9-white-soccer-shoe.jpg', 249.99
    ),
    (
        'adidas F50 Elite FG',
        'Lightweight Sprintframe sole plate built for explosive acceleration in open play.',
        (SELECT id FROM categories WHERE name = 'Soccer'),
        TRUE, 'adidas-f50-soccer-shoe.jpg', 229.99
    ),
    (
        'adidas Predator Elite FG',
        'Fold-over tongue Predator with FacetFrame rubber strike zones for swerve and control.',
        (SELECT id FROM categories WHERE name = 'Soccer'),
        TRUE, 'adidas-predetor-shoe.jpg', 269.99
    ),
    (
        'adidas Predator Accuracy.1 "Solar Energy"',
        'Solar-green Predator with HybridTouch upper and a zone-skin strike pad.',
        (SELECT id FROM categories WHERE name = 'Soccer'),
        TRUE, 'adidas-predetor-shoe-green.jpg', 239.99
    ),
    (
        'adidas Copa Mundial',
        'The iconic K-leather cleat, hand-made in Germany since 1979.',
        (SELECT id FROM categories WHERE name = 'Soccer'),
        TRUE, 'adidas_copa_mundial_soccer_shoe.jpg', 159.99
    ),

    -- Volleyball
    (
        'Mikasa V200W Official Match Volleyball',
        'FIVB-approved official match ball used at major international tournaments.',
        (SELECT id FROM categories WHERE name = 'Volleyball'),
        FALSE, 'Mikasa-volleyball-ball.png', 89.99
    ),
    (
        'Vermont Beach Volleyball',
        'Soft-touch beach volleyball with a weather-resistant synthetic cover.',
        (SELECT id FROM categories WHERE name = 'Volleyball'),
        FALSE, 'vermont-volleyball-ball.jpg', 34.99
    ),

    -- Tennis
    (
        'Pro Carbon Tennis Racket',
        'Carbon-frame intermediate racket with a hybrid string pattern for spin and control.',
        (SELECT id FROM categories WHERE name = 'Tennis'),
        TRUE, 'tennis-racket-silver.jpg', 129.99
    ),
    (
        'Lightweight Composite Tennis Racket',
        'Composite frame with an oversized head — ideal for improving recreational players.',
        (SELECT id FROM categories WHERE name = 'Tennis'),
        TRUE, 'tennis-racket-white-black-green.jpg', 99.99
    ),
    (
        'Dunlop CX 200 Tour',
        'Tour-spec control racket favoured by competitive baseliners and all-court players.',
        (SELECT id FROM categories WHERE name = 'Tennis'),
        TRUE, 'dunlop-tennis-racket.jpg', 229.99
    ),

    -- Padel
    (
        'Wilson Bela Pro Padel Racket',
        'Carbon-fibre teardrop padel racket with an EVA core for power and feel.',
        (SELECT id FROM categories WHERE name = 'Padel'),
        FALSE, 'padel-racket-green-black.jpg', 199.99
    ),
    (
        'Wilson Tour Padel Racket',
        'All-court padel racket with a rough surface that adds bite and spin on every strike.',
        (SELECT id FROM categories WHERE name = 'Padel'),
        FALSE, 'padel-racket-red.jpg', 179.99
    ),

    -- Ping Pong
    (
        'Retractable Table Tennis Net',
        'Clip-on retractable net fitting tables up to 180cm wide. Tension-adjustable.',
        (SELECT id FROM categories WHERE name = 'Ping Pong'),
        FALSE, 'ping-pong-net.jpg', 19.99
    ),
    (
        'Beginner Table Tennis Paddle',
        'Inverted-rubber paddle ideal for casual play and first-time players.',
        (SELECT id FROM categories WHERE name = 'Ping Pong'),
        FALSE, 'ping-pog-racket-begginer.jpg', 24.99
    ),
    (
        'King Feel Carbon Pro Paddle',
        '7-ply wood blade with 2 carbon layers and ITTF-approved rubber. Tournament-ready.',
        (SELECT id FROM categories WHERE name = 'Ping Pong'),
        FALSE, 'ping-pong-racket-advanced.jpg', 79.99
    );
