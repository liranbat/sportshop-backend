-- Bootstrap admin + generic test user. Both share the password "PassChange1706"
-- (change after first login). The password_hash literals below were generated
-- once with Spring Security's BCryptPasswordEncoder (strength 10, $2a) — the
-- exact encoder Phase 3.3's AuthService.register() / .login() will use, so
-- passwordEncoder.matches("PassChange1706", password_hash) returns true.
-- Regenerate via a throwaway main using BCryptPasswordEncoder if either
-- password ever changes.
INSERT INTO users (first_name, last_name, email, phone, password_hash, is_admin) VALUES
    ('Liran', 'Batson',  'liran.bat5@gmail.com',  '0501234567', '$2a$10$6N0f.QtVAK2Imb3P3QbqEus6ge8a/ssBv2pcWp0Nyx3fZJZ79avoS', TRUE),
    ('Test',  'User', 'test.user@example.com', '0502345678', '$2a$10$agQrNyGfVlk.bCyKG8DSZeGWuczLLB72.wroV3hVL4ECGK46w.W9i', FALSE);
