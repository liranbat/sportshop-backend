INSERT INTO users (first_name, last_name, email, phone, password_hash, is_admin) VALUES
    ('Liran', 'Batson', 'liran.bat5@gmail.com',   '0501234567', '$2a$10$6N0f.QtVAK2Imb3P3QbqEus6ge8a/ssBv2pcWp0Nyx3fZJZ79avoS', TRUE),
    ('Test',  'User',   'test.user@example.com',  '0502345678', '$2a$10$agQrNyGfVlk.bCyKG8DSZeGWuczLLB72.wroV3hVL4ECGK46w.W9i', FALSE),
    ('Admin', 'User',   'admin.user@example.com', '0503456789', '$2a$10$agQrNyGfVlk.bCyKG8DSZeGWuczLLB72.wroV3hVL4ECGK46w.W9i', TRUE)
ON CONFLICT (email) DO NOTHING;
