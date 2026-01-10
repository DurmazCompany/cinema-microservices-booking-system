-- Password is 'password'
INSERT INTO users (email, password_hash, role)
VALUES ('admin@cinema.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ADMIN')
ON CONFLICT (email) DO NOTHING;
