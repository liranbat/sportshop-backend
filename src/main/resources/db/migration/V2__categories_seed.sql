-- Seed; ignore if already existing.
INSERT INTO categories (name, icon_filename)
SELECT v.name, v.icon_filename
FROM (VALUES
    ('Basketball', 'basketball.svg'),
    ('Soccer',     'soccer.svg'),
    ('Volleyball', 'volleyball.svg'),
    ('Tennis',     'tennis.svg'),
    ('Padel',      'padel.svg'),
    ('Ping Pong',  'ping-pong.svg')
) AS v(name, icon_filename)
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.name = v.name);
