USE KhangGearVer2DB;
GO
IF NOT EXISTS (SELECT 1 FROM dbo.categories WHERE name = N'Phu kien') INSERT INTO dbo.categories (name, description, active) VALUES (N'Phu kien', N'Danh muc mau cho phu kien cong nghe.', 1);
IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = 'admin') INSERT INTO dbo.users (username, password, full_name, email, phone, role, active, email_verified) VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', N'Quan tri vien mau', 'admin@khanggear.local', '0900000001', 'ADMIN', 1, 1);
GO
