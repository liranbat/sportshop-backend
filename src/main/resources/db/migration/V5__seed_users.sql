-- Both seeded users share password "PassChange1706" (change after first login).
-- Hashes were generated with BCryptPasswordEncoder(strength=10, $2a) — regenerate the same way if the password changes.
INSERT INTO users (first_name, last_name, email, phone, password_hash, is_admin) VALUES
    ('Liran', 'Batson',  'liran.bat5@gmail.com',  '0501234567', '$2a$10$6N0f.QtVAK2Imb3P3QbqEus6ge8a/ssBv2pcWp0Nyx3fZJZ79avoS', TRUE),
    ('Test',  'User', 'test.user@example.com', '0502345678', '$2a$10$agQrNyGfVlk.bCyKG8DSZeGWuczLLB72.wroV3hVL4ECGK46w.W9i', FALSE);
