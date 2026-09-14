USE KhangGearVer2DB;
GO
IF OBJECT_ID(N'dbo.categories', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.categories (
        id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_categories PRIMARY KEY,
        name NVARCHAR(150) NOT NULL,
        description NVARCHAR(1000) NULL,
        active BIT NOT NULL CONSTRAINT DF_categories_active DEFAULT 1,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_categories_created_at DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL CONSTRAINT DF_categories_updated_at DEFAULT SYSUTCDATETIME(),
        CONSTRAINT UQ_categories_name UNIQUE (name)
    );
END;
GO
IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_users PRIMARY KEY,
        username VARCHAR(50) NOT NULL,
        password VARCHAR(100) NOT NULL,
        full_name NVARCHAR(150) NULL,
        email VARCHAR(254) NOT NULL,
        phone VARCHAR(20) NULL,
        role VARCHAR(20) NOT NULL,
        active BIT NOT NULL CONSTRAINT DF_users_active DEFAULT 1,
        email_verified BIT NOT NULL CONSTRAINT DF_users_email_verified DEFAULT 0,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_users_created_at DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL CONSTRAINT DF_users_updated_at DEFAULT SYSUTCDATETIME(),
        CONSTRAINT UQ_users_username UNIQUE (username),
        CONSTRAINT UQ_users_email UNIQUE (email),
        CONSTRAINT CK_users_role CHECK (role IN ('ADMIN', 'MANAGER', 'CUSTOMER'))
    );
END;
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_categories_name' AND object_id = OBJECT_ID(N'dbo.categories')) CREATE INDEX IX_categories_name ON dbo.categories(name);
IF COL_LENGTH(N'dbo.categories', N'icon') IS NULL ALTER TABLE dbo.categories ADD icon NVARCHAR(255) NULL;
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_users_username' AND object_id = OBJECT_ID(N'dbo.users')) CREATE INDEX IX_users_username ON dbo.users(username);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_users_email' AND object_id = OBJECT_ID(N'dbo.users')) CREATE INDEX IX_users_email ON dbo.users(email);
GO
IF OBJECT_ID(N'dbo.email_otps', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.email_otps (
        id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_email_otps PRIMARY KEY,
        user_id BIGINT NOT NULL,
        email VARCHAR(254) NOT NULL,
        purpose VARCHAR(20) NOT NULL,
        code_hash VARCHAR(100) NOT NULL,
        expires_at DATETIME2 NOT NULL,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_email_otps_created_at DEFAULT SYSUTCDATETIME(),
        consumed_at DATETIME2 NULL,
        attempt_count INT NOT NULL CONSTRAINT DF_email_otps_attempt_count DEFAULT 0,
        last_sent_at DATETIME2 NOT NULL,
        CONSTRAINT FK_email_otps_users FOREIGN KEY (user_id) REFERENCES dbo.users(id),
        CONSTRAINT CK_email_otps_purpose CHECK (purpose IN ('VERIFY_EMAIL', 'RESET_PASSWORD'))
    );
END;
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_email_otps_user_purpose' AND object_id = OBJECT_ID(N'dbo.email_otps')) CREATE INDEX IX_email_otps_user_purpose ON dbo.email_otps(user_id, purpose, created_at DESC);
GO
IF OBJECT_ID(N'dbo.products', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.products (
        id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_products PRIMARY KEY,
        name NVARCHAR(200) NOT NULL,
        description NVARCHAR(2000) NULL,
        price DECIMAL(19,2) NOT NULL CONSTRAINT DF_products_price DEFAULT 0,
        stock INT NOT NULL CONSTRAINT DF_products_stock DEFAULT 0,
        image NVARCHAR(255) NULL,
        active BIT NOT NULL CONSTRAINT DF_products_active DEFAULT 1,
        category_id BIGINT NOT NULL,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_products_created_at DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL CONSTRAINT DF_products_updated_at DEFAULT SYSUTCDATETIME(),
        CONSTRAINT FK_products_categories FOREIGN KEY (category_id) REFERENCES dbo.categories(id),
        CONSTRAINT CK_products_price CHECK (price >= 0),
        CONSTRAINT CK_products_stock CHECK (stock >= 0)
    );
END;
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_products_category_active' AND object_id = OBJECT_ID(N'dbo.products')) CREATE INDEX IX_products_category_active ON dbo.products(category_id, active);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_products_name' AND object_id = OBJECT_ID(N'dbo.products')) CREATE INDEX IX_products_name ON dbo.products(name);
GO
IF OBJECT_ID(N'dbo.orders', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.orders (
        id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_orders PRIMARY KEY,
        user_id BIGINT NOT NULL,
        receiver_name NVARCHAR(150) NOT NULL,
        phone VARCHAR(20) NOT NULL,
        email VARCHAR(254) NOT NULL,
        shipping_address NVARCHAR(500) NOT NULL,
        note NVARCHAR(1000) NULL,
        payment_method VARCHAR(30) NOT NULL CONSTRAINT DF_orders_payment_method DEFAULT 'COD',
        payment_status VARCHAR(30) NOT NULL CONSTRAINT DF_orders_payment_status DEFAULT 'UNPAID',
        order_status VARCHAR(30) NOT NULL CONSTRAINT DF_orders_order_status DEFAULT 'PENDING',
        total_amount DECIMAL(19,2) NOT NULL CONSTRAINT DF_orders_total_amount DEFAULT 0,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_orders_created_at DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL CONSTRAINT DF_orders_updated_at DEFAULT SYSUTCDATETIME(),
        CONSTRAINT FK_orders_users FOREIGN KEY (user_id) REFERENCES dbo.users(id),
        CONSTRAINT CK_orders_payment_method CHECK (payment_method IN ('COD', 'BANK_TRANSFER')),
        CONSTRAINT CK_orders_payment_status CHECK (payment_status IN ('UNPAID', 'PAID', 'REFUNDED')),
        CONSTRAINT CK_orders_status CHECK (order_status IN ('PENDING', 'CONFIRMED', 'SHIPPING', 'COMPLETED', 'CANCELLED'))
    );
END;
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_orders_user_created' AND object_id = OBJECT_ID(N'dbo.orders')) CREATE INDEX IX_orders_user_created ON dbo.orders(user_id, created_at DESC);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_orders_status' AND object_id = OBJECT_ID(N'dbo.orders')) CREATE INDEX IX_orders_status ON dbo.orders(order_status);
GO
IF OBJECT_ID(N'dbo.order_items', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_items (
        id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_order_items PRIMARY KEY,
        order_id BIGINT NOT NULL,
        product_id BIGINT NOT NULL,
        product_name NVARCHAR(200) NOT NULL,
        unit_price DECIMAL(19,2) NOT NULL,
        quantity INT NOT NULL,
        line_total DECIMAL(19,2) NOT NULL,
        CONSTRAINT FK_order_items_orders FOREIGN KEY (order_id) REFERENCES dbo.orders(id),
        CONSTRAINT FK_order_items_products FOREIGN KEY (product_id) REFERENCES dbo.products(id),
        CONSTRAINT CK_order_items_quantity CHECK (quantity > 0),
        CONSTRAINT CK_order_items_prices CHECK (unit_price >= 0 AND line_total >= 0)
    );
END;
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_order_items_order' AND object_id = OBJECT_ID(N'dbo.order_items')) CREATE INDEX IX_order_items_order ON dbo.order_items(order_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_order_items_product' AND object_id = OBJECT_ID(N'dbo.order_items')) CREATE INDEX IX_order_items_product ON dbo.order_items(product_id);
GO
