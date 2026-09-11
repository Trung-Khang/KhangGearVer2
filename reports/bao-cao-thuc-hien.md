# Bao cao Giai doan 2

- Da them Entity `Category` va `User`, enum `ADMIN`, `MANAGER`, `CUSTOMER`, timestamp lifecycle callback va Jakarta Validation.
- Da them repository tim kiem/phan trang, kiem tra trung va dem ADMIN active.
- Service transaction trim/normalize, chan trung, giu password cu khi sua rong, BCrypt password moi va bao ve tu xoa/tu khoa/ADMIN active cuoi cung.
- Script SQL idempotent trong `sql/` tao database, schema, index va seed hash BCrypt; khong co plaintext password hay secret.
- Test repository dung H2 profile `test`; service dung Mockito/BCrypt. `mvn clean test` pass 7/7 test va `mvn clean package` pass.
- Chua kiem thu SQL Server that do chua co credential qua bien moi truong. Chua co Security, controller CRUD, JSP CRUD hay SiteMesh decorator.

# Báo cáo Giai đoạn 3 - Spring Security

## Mục tiêu và phạm vi

Triển khai đăng nhập bằng dữ liệu bảng `users` và phân quyền riêng cho khu vực `/admin/**`. Không triển khai CRUD Category/User, không tích hợp SiteMesh/Bootstrap layout hoàn chỉnh và không thay đổi cấu trúc database SQL Server.

## Thành phần đã thêm

- `SecurityConfig`: `SecurityFilterChain` theo Spring Security hiện đại; tài nguyên công khai gồm `/assets/**`, `/login`, `/403`, `/error`; `/admin/**` yêu cầu `ROLE_ADMIN`; các URL còn lại yêu cầu đăng nhập.
- `CustomUserDetailsService`: tìm `username` không phân biệt hoa thường từ `UserRepository`, kiểm tra `active` và `emailVerified` trước khi trả `UserDetails`, sau đó ánh xạ enum `ADMIN`, `MANAGER`, `CUSTOMER` thành authority `ROLE_*`.
- `AuthController`, `AdminController` và các JSP `auth/login.jsp`, `admin/dashboard.jsp`, `error/403.jsp`. POST `/login` do Spring Security xử lý; logout chỉ POST `/logout` và có CSRF token.
- `application-security-test.properties` cùng `data-security-test.sql`: profile H2 trong bộ nhớ dùng riêng để kiểm tra WAR, không cần SQL Server. Seed chỉ dùng BCrypt hash, không có mật khẩu rõ hoặc secret.

## Kiểm thử

- `mvn clean test`: 11 test pass. Security integration test kiểm tra `/login`, redirect anonymous, ADMIN vào dashboard, MANAGER/CUSTOMER bị 403, login đúng/sai, tài khoản inactive/unverified bị từ chối và logout cần CSRF. Test profile xác nhận seed H2 có ADMIN/MANAGER/CUSTOMER active, verified và BCrypt hợp lệ.
- Đã phát hiện và sửa redirect loop khi JSP được forward vào `/WEB-INF`: Security cho phép riêng dispatcher `FORWARD` và `ERROR`, không mở thêm URL request.
- `mvn clean package`: pass, tạo `target/khanggear-ver2.war`.
- HTTP smoke test WAR với `security-test` trên cổng 8182: `/login` trả HTML có form/CSRF; ADMIN đăng nhập và nhận `/admin` 200 có nội dung; MANAGER đăng nhập nhưng nhận 403 có nội dung; anonymous `/admin` redirect về login; logout POST có CSRF redirect về `/login?logout`.

## Giới hạn còn lại

Chưa có CRUD controller/JSP, SiteMesh decorator/Bootstrap admin template đầy đủ, SQL Server thật và browser test. Các phần này để giai đoạn sau.

# Báo cáo Giai đoạn 3B - Đăng ký và OTP email

- Tạo Entity `EmailOtp`/bảng `email_otps` với user, email, purpose, BCrypt code hash, expiry, consumed time, attempt count và last sent time. Migration SQL Server idempotent, không xóa dữ liệu cũ.
- Route: `/register`, `/verify-email`, `/verify-email/resend`, `/forgot-password`, `/reset-password/verify`, `/reset-password`. Tất cả POST có CSRF qua Spring Security.
- Rule: OTP 6 chữ số từ `SecureRandom`, hết hạn 5 phút, tối đa 5 lần sai, cooldown 60 giây, OTP mới vô hiệu OTP cũ và OTP đã dùng không dùng lại được.
- Mail dùng Spring Mail/JavaMailSender, HTML UTF-8, nhận diện KHANGGEAR và hai nội dung riêng cho xác minh email/đặt lại mật khẩu. Không ghi OTP, mật khẩu hay SMTP secret vào log.
- Đã đọc các service/controller/JSP OTP của dự án cũ để mapping; không sửa dự án cũ. `setenv.bat` Tomcat được kiểm tra theo tên biến, không đọc hoặc ghi giá trị nhạy cảm.
- Kiểm thử Gmail/SQL Server/Tomcat thật sẽ chỉ được ghi nhận sau khi có cấu hình runtime cho dự án mới; test tự động dùng H2 và mock mail, không gửi email thật.

# Bao cao Giai doan 3C - Kiem thu SQL Server, Gmail va Tomcat

## Ket qua SQL Server

- SQL Server Express dang chay va ket noi Windows Authentication chi-doc thanh cong.
- Da tao idempotent database rieng `KhangGearVer2DB` bang `sql/create-database.sql`, sau do chay `sql/schema.sql`. Khong drop database, bang hay du lieu va khong ghi vao database cua ung dung cu.
- Da xac nhan schema co `users`, `categories`, `email_otps`; `users` co `active`, `email_verified`, `role`; OTP co user, purpose, hash, expiry, consumed, attempts va last-sent.

## Tomcat va HTTP

- External Tomcat dung tai `C:\\apache-tomcat-11.0.25`; context cu `/dangnhap` da duoc kiem tra HTTP 200 truoc va sau deploy.
- Da deploy rieng `khanggear-ver2.war`, khong thay the `dangnhap.war`.
- Phat hien WAR Spring Boot thieu `SpringBootServletInitializer.configure`, lam context moi tra 404. Da sua trong commit `12e5d23` va them regression test cho bootstrap external Tomcat.
- Sau sua, log xac nhan Spring Web initializer da chay. Context moi chua khoi dong vi datasource production chua nhan duoc JDBC URL/credential, nen `/khanggear-ver2/login` chua the tra HTML.

## SMTP va phan con thieu

- `setenv.bat` ton tai va co nhom bien `SMTP_*`; khong co `MAIL_*` hay `SQLSERVER_*` duoc phat hien. Gia tri nhay cam khong duoc doc, ghi log hay dua vao repository.
- Gmail OTP that chua the kiem thu vi context moi bi chan o datasource truoc khi vao form. Khong gui email, khong tao OTP that va khong xu ly email ca nhan.
- Can cau hinh runtime cho Tomcat bang `SQLSERVER_URL`, `SQLSERVER_USERNAME`, `SQLSERVER_PASSWORD` cua database `KhangGearVer2DB`, sau do restart Tomcat. `MAIL_*` hoac `SMTP_*` da duoc code ho tro de kiem thu Gmail.

## Build

- `mvn clean test`: pass 17/17.
- `mvn clean package`: pass, tao `target/khanggear-ver2.war`.

## Bo sung 3C - Cau hinh runtime local va chan doan Tomcat

### Nguyen nhan external Tomcat khong phuc vu duoc ung dung moi

- Log cho thay context `khanggear-ver2` dung o khoi tao datasource do runtime cua external Tomcat chua co `SQLSERVER_URL`, `SQLSERVER_USERNAME`, `SQLSERVER_PASSWORD`. Hibernate khong lay duoc JDBC metadata nen khong the khoi tao dialect; day la blocker cua ung dung moi.
- Mot lan khoi dong Tomcat khac that bai do cong `8080` va shutdown port `8005` dang duoc Tomcat dang chay su dung. Log sau do ghi nhan lenh shutdown hop le; khong co bang chung Community Server Connector lam Tomcat crash.
- Cac canh bao `--add-opens` va canh bao cleanup JDBC xuat hien luc Tomcat dung, khong phai nguyen nhan goc. Deployment chi co `khanggear-ver2.war`; ung dung cu `dangnhap` khong bi thay the.

### Chay local an toan

- Da them `.vscode/launch.json` voi cau hinh `Run KhangGearVer2 (local SQL + Gmail)`. Cau hinh chay main class Spring Boot tren cong `8081`, context `/khanggear-ver2`, va doc properties tu `.env.local` qua `spring.config.additional-location`.
- Da them `.env.local.example` chi chua placeholder cho `SQLSERVER_URL`, `SQLSERVER_USERNAME`, `SQLSERVER_PASSWORD` va `MAIL_*`. File `.env.local` bi Git ignore; credential khong nam trong launch configuration, README hay bao cao.
- Smoke test WAR voi profile `security-test` tren embedded Tomcat da tra HTTP 200 va HTML khac rong cho `/khanggear-ver2/login`, `/khanggear-ver2/register` va `/khanggear-ver2/forgot-password`. Profile nay dung H2 de kiem tra JSP/runtime, khong gui Gmail that.
- SQL Server/Gmail that qua external Tomcat chua the kiem thu tiep cho den khi nguoi van hanh khai bao ba bien `SQLSERVER_*` trong runtime cua Tomcat cho database rieng `KhangGearVer2DB` va restart dung instance Tomcat.

## Bo sung 3C - Sua cau hinh VS Code Run and Debug

- `KhangGearVer2Application` dung package dung theo source layout, co `public static void main(String[] args)` va Maven da compile thanh `target/classes/vn/edu/hcmute/khanggearver2/KhangGearVer2Application.class`.
- Da bo `projectName` vi truong nay co the khong trung ten module ma Java Language Server nhan dien; Java Debugger se tu chon Maven project theo `mainClass`. Nguyen nhan da tai hien ben ngoai VS Code la classpath cua process fork bi loi encoding o duong dan Unicode tren Windows.
- `args` duoc doi sang mang tham so hop le cua Java Debugger; van giu port `8081`, context `/khanggear-ver2` va nap `.env.local` qua Spring Boot. Khong co credential trong launch configuration.
- Da kiem tra `.env.local` chi theo ten bien: cac bien `SQLSERVER_*` va `MAIL_*` can thiet deu co va file dang bi Git ignore. Khong doc hay ghi gia tri nhay cam.
- `mvn spring-boot:run` tai duong dan repository goc da tai hien `ClassNotFoundException`; debug classpath cho thay doan duong dan Unicode bi bien dang trong process fork. Chay cung lenh qua junction ASCII local `D:\KhangGearVer2-local` da vao duoc `KhangGearVer2Application.main`, nen loi main class da duoc phan loai la classpath/encoding cua launcher tren Windows, khong phai loi package hay compile.
- Sau khi main class chay duoc, runtime dung o loi moi `Unable to determine Dialect without JDBC metadata`. `.env.local` da duoc nap va co dung ten bien, nhung ket noi SQL Server chua thanh cong hoac URL khong hop le. Khong doc, sua hay log gia tri credential; can kiem tra gia tri SQL Server tren may local truoc khi co the smoke test HTTP production.
- Smoke test tách biệt voi `security-test` qua junction da khoi dong embedded Tomcat tren `8081`; `/khanggear-ver2/login`, `/register` va `/forgot-password` deu tra HTTP 200 voi HTML khac rong. H2 chi duoc dung de xac minh main class/JSP sau sua launcher, khong thay the kiem thu SQL Server production.
