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
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_users_username' AND object_id = OBJECT_ID(N'dbo.users')) CREATE INDEX IX_users_username ON dbo.users(username);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_users_email' AND object_id = OBJECT_ID(N'dbo.users')) CREATE INDEX IX_users_email ON dbo.users(email);
GO
