USE KhangGearVer2DB;
GO
IF NOT EXISTS (SELECT 1 FROM dbo.categories WHERE name = N'Phu kien') INSERT INTO dbo.categories (name, description, active) VALUES (N'Phu kien', N'Danh muc mau cho phu kien cong nghe.', 1);
IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = 'admin') INSERT INTO dbo.users (username, password, full_name, email, phone, role, active, email_verified) VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', N'Quan tri vien mau', 'admin@khanggear.local', '0900000001', 'ADMIN', 1, 1);
GO
DECLARE @categoryId BIGINT = (SELECT TOP 1 id FROM dbo.categories WHERE name = N'Phu kien');
IF @categoryId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.products)
BEGIN
    INSERT INTO dbo.products (name, description, price, stock, active, category_id)
    VALUES
      (N'KhangGear USB-C Hub 7 in 1', N'Hub USB-C da cong cho laptop.', 690000, 20, 1, @categoryId),
      (N'KhangGear Wireless Mouse', N'Chuot khong day cho van phong va gaming.', 490000, 35, 1, @categoryId),
      (N'KhangGear Mechanical Keyboard', N'Ban phim co day RGB.', 1490000, 12, 1, @categoryId),
      (N'KhangGear Gaming Headset', N'Tai nghe gaming am thanh vong.', 1290000, 8, 1, @categoryId),
      (N'KhangGear Laptop Stand', N'Gia do laptop hop kim nhom.', 590000, 16, 1, @categoryId);
END;
GO
