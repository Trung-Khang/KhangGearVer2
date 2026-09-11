INSERT INTO users (username, password, full_name, email, role, active, email_verified, created_at, updated_at)
VALUES ('admin-demo', '$2a$10$XBJnm0lGtikUnbyggjM5aeATwgKvLIF.KZ5dQPI1HhF7CHklmIXq.', 'Admin Demo', 'admin-demo@khanggear.test', 'ADMIN', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (username, password, full_name, email, role, active, email_verified, created_at, updated_at)
VALUES ('manager-demo', '$2a$10$XBJnm0lGtikUnbyggjM5aeATwgKvLIF.KZ5dQPI1HhF7CHklmIXq.', 'Manager Demo', 'manager-demo@khanggear.test', 'MANAGER', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (username, password, full_name, email, role, active, email_verified, created_at, updated_at)
VALUES ('customer-demo', '$2a$10$XBJnm0lGtikUnbyggjM5aeATwgKvLIF.KZ5dQPI1HhF7CHklmIXq.', 'Customer Demo', 'customer-demo@khanggear.test', 'CUSTOMER', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
