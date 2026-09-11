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
