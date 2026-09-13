# KhangGearVer2

## Giai doan 1: Nen tang Spring Boot 4

KhangGearVer2 la phien ban chuyen doi co kiem soat tu he thong KhangGear cu sang Spring Boot 4, giu lai cac quy tac nghiep vu can thiet truoc khi trien khai chuc nang quan tri.

### Cong nghe nen

- Java 17, Maven, Spring Boot 4.1.1, WAR packaging.
- Spring MVC, Spring Data JPA, Spring Security va Jakarta Validation.
- SQL Server JDBC Driver, JSP/JSTL Jakarta, Embedded Tomcat Jasper va SiteMesh Decorator `3.3.0-RC1` (Jakarta-compatible).

### Cau truc

- `src/main/java`: ung dung Spring Boot, cau hinh MVC va controller kiem tra JSP.
- `src/main/webapp/WEB-INF/views`: JSP noi bo.
- `src/main/resources`: cau hinh chung, profile foundation khong dung database va file mau SQL Server khong co bi mat.
- `docs/legacy-mapping.md`: mapping nghiep vu tu du an cu.
- `docs/technology-compatibility.md`: quyet dinh phien ban va rui ro tuong thich.

### Kiem thu va dong goi

```bash
mvn clean test
mvn clean package
```

WAR duoc tao tai `target/khanggear-ver2.war`.

### Trang thai

Giai doan 2 da co Entity `Category`, `User`, enum role, Spring Data repository va service co quy tac nghiep vu. Database rieng la `KhangGearVer2DB`; cau hinh production doc `SQLSERVER_URL`, `SQLSERVER_USERNAME`, `SQLSERVER_PASSWORD`, va dung `ddl-auto=validate`.

Chay `sql/create-database.sql`, sau do `sql/schema.sql` va `sql/seed.sql` bang SQL Server. Profile `foundation` chi de kiem tra JSP khong ket noi database; profile `test` dung H2 trong bo nho, khong dai dien hoan toan cho SQL Server.

## Giai đoạn 3: Đăng nhập và phân quyền

- Spring Security dùng `CustomUserDetailsService` truy vấn `users.username` không phân biệt hoa thường và kiểm tra mật khẩu BCrypt.
- Chỉ tài khoản `active=true` và `emailVerified=true` mới đăng nhập được. Role được ánh xạ thành `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_CUSTOMER`; riêng `/admin/**` chỉ cho ADMIN.
- Có JSP `/login`, dashboard `/admin`, trang `/403`, CSRF mặc định và logout bằng POST. CRUD Category/User, SiteMesh Bootstrap và storefront vẫn chưa được triển khai trong repository mới.
- Profile `security-test` dùng H2 trong bộ nhớ để chạy WAR kiểm tra độc lập; chỉ có dữ liệu demo với BCrypt hash, không chứa mật khẩu hoặc SQL Server secret.

Chạy kiểm tra Security không cần SQL Server:

```bash
mvn clean test
mvn clean package
java -jar target/khanggear-ver2.war --spring.profiles.active=security-test --server.port=8182
```

## Giai đoạn 3B: OTP email

- Đăng ký tạo tài khoản `CUSTOMER` active nhưng chưa xác minh email; đăng nhập chỉ được phép sau khi OTP hợp lệ.
- Quên mật khẩu dùng OTP một lần, có thời hạn 5 phút, tối đa 5 lần sai và cooldown gửi lại 60 giây. Reset được xác nhận bằng session ngắn hạn, không dùng OTP trong URL hoặc localStorage.
- Cấu hình mail qua biến môi trường: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM_NAME`, `MAIL_STARTTLS`. Không commit các giá trị này.
- External Tomcat chỉ nạp biến trong `setenv.bat` khi Tomcat khởi động. Với `mvn spring-boot:run` hoặc `java -jar`, hãy khai báo các biến trên trong PowerShell/IDE trước khi chạy.

## Chay local bang VS Code

1. Tao file local: `Copy-Item .env.local.example .env.local`.
2. Tu dien `SQLSERVER_URL`, `SQLSERVER_USERNAME`, `SQLSERVER_PASSWORD` cho database rieng `KhangGearVer2DB`, va cac bien `MAIL_*` tren may. Khong commit `.env.local`.
3. Mo Run and Debug trong VS Code, chon `Run KhangGearVer2 (local SQL + Gmail)`, sau do bam nut chay.
4. Embedded Tomcat chay doc lap tai `http://localhost:8081/khanggear-ver2/login`. Ung dung cu external Tomcat van dung cong `8080`.

Cau hinh `.vscode/launch.json` dung `spring.config.additional-location` de Spring Boot doc file `.env.local` theo dinh dang properties. Cach nay khong can dua credential vao VS Code launch configuration.

Neu VS Code bao khong tim thay main class, cau hinh chay khong gan `projectName`: Java Debugger tu chon Maven module chua `KhangGearVer2Application`. Mo dung thu muc goc `KhangGearVer2`, chay `Java: Clean Java Language Server Workspace` neu can, cho Maven load xong roi chon cau hinh Run and Debug.

Tren Windows, Maven launcher co the mat classpath neu repository nam trong duong dan co ky tu Unicode. Da tao junction local `D:\KhangGearVer2-local` tro den repository nay de kiem tra. Neu gap lai `ClassNotFoundException`, mo `D:\KhangGearVer2-local` bang VS Code, reload Maven/Java Language Server, roi dung cung cau hinh Run and Debug. Junction nay la cau hinh may local, khong nam trong Git va khong thay doi source repository.

## External Tomcat

## Bo sung: dang ky va khoi phuc mat khau

- Dang ky tao tai khoan `CUSTOMER` o trang thai chua xac minh; OTP xac minh duoc gui qua Spring Mail va co the gui lai theo cooldown.
- Quen mat khau dung OTP 6 chu so, luu hash, het han 5 phut, gioi han so lan thu va chi dung mot lan. Mat khau moi duoc BCrypt trong giao dich cap nhat.
- Cac bien moi truong mail duoc ho tro: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM_NAME`, `MAIL_STARTTLS`. Chi dung placeholder trong repository, khong commit secret.
- Cac route JSP: `/register`, `/verify-email`, `/forgot-password`, `/reset-password/verify`, `/reset-password`.

## Giai doan 4A: CRUD Category

- ADMIN co the xem, tim kiem, them, sua va xoa Category tai `/admin/category/list`; cac POST deu co CSRF va dung Post/Redirect/Get.
- Ten Category duoc trim, bat buoc va khong trung khong phan biet hoa thuong. Icon nhan PNG/JPG/JPEG/WebP toi da 2 MB, ten file UUID va luu ngoai WAR trong `uploads/categories`.
- Migration `sql/schema.sql` bo sung cot `icon` theo cach idempotent. Product/User/Order va cac luong account/OTP khong thay doi.

WAR `target/khanggear-ver2.war` chi deploy voi context `/khanggear-ver2`, khong doi ten thanh `dangnhap.war`. External Tomcat can co `SQLSERVER_URL`, `SQLSERVER_USERNAME`, `SQLSERVER_PASSWORD` trong runtime; code van tuong thich fallback `SMTP_*` cu cho mail.

## Xac minh Category tren SQL Server

- Da doi chieu `Category` JPA voi `dbo.categories`: cac cot `id`, `name`, `description`, `active`, `created_at`, `updated_at` va `icon` khop schema hien tai.
- Migration `sql/schema.sql` cho `icon` la idempotent; khong drop bang va khong xoa du lieu.
- Runtime local co the ket noi `KhangGearVer2DB` qua cau hinh `.env.local` duoc nap bang `optional:file:./.env.local[.properties]`. Khong dua gia tri nhay cam vao Git.
- Category CRUD su dung PRG, CSRF, validation server-side va upload icon an toan.
