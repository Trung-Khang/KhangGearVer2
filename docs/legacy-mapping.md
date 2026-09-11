# Khao sat KhangGear cu

Tai lieu nay chi ghi nhan nghiep vu tu du an Servlet/JSP cu. Khong sao chep source, du lieu, cau hinh may ca nhan hay bi mat.

## Mapping du lieu can bao toan

| Thanh phan cu | Bang/cot cu | Huong Spring Boot 4 |
| --- | --- | --- |
| Category | `Category`: `cate_id`, `cate_name`, `icons` | Entity JPA moi voi `id`, `name`, `icon`; giu dung ten bang/cot bang `@Table` va `@Column`. |
| User | `dbo.[User]`: `id`, `email`, `username`, `fullname`, `password`, `avatar`, `roleid`, `phone`, `createddate`, `active`, `email_verified` | Entity JPA moi se giu cac field/cot nay; ten bang duoc quote phu hop SQL Server. |

## Quy tac nghiep vu can giu

- Category: ten bat buoc, toi da 150 ky tu, khong trung; icon tuy chon va chi nhan anh hop le. Form cu co tim kiem, them, sua, xoa va thong bao tieng Viet.
- User: email va username la duy nhat. Role hien co: `ADMIN=1`, `MANAGER=2`, `CUSTOMER=3`.
- User dang nhap phai `active=true` va `email_verified=true`. Tai khoan dang ky moi la CUSTOMER, hoat dong nhung chua xac minh email; tai khoan chua xac minh khong duoc dang nhap.
- Chi ADMIN quan ly User. Khong tu khoa/xoa tai khoan dang dang nhap va luon phai con it nhat mot ADMIN hoat dong.
- Admin cu dung sidebar xanh KhangGear, logo dau sidebar, header trang co tieu de/loi chao/Dang xuat, cac muc Danh muc, San pham, Don hang, Nguoi dung va Thong ke.

## Thay the kien truc

| Cu | Moi |
| --- | --- |
| Servlet Controller | Spring MVC `@Controller` |
| DAO/Service cu | Spring Data JPA + Service |
| `AdminAuthFilter` | Spring Security |
| JSP admin cu | JSP/JSTL moi |
| SiteMesh cu | Cau hinh SiteMesh rieng cho Spring Boot 4 |

## Giai đoạn 3B - OTP email

- Đã đọc: `OtpService`, `AccountOtpDao`, `RegisterController`, `VerifyEmailController`, `ForgotPasswordController` và các JSP OTP của dự án cũ. Luồng cũ dùng OTP 6 chữ số từ `SecureRandom`, hết hạn sau 5 phút, cooldown gửi lại 60 giây, giới hạn 5 lần sai và OTP chỉ dùng một lần.
- `account_otps` JDBC cũ được thay bằng Entity JPA `EmailOtp`, `EmailOtpRepository`, `AccountOtpService` và enum `OtpPurpose` (`VERIFY_EMAIL`, `RESET_PASSWORD`). Mã OTP ở dự án mới được BCrypt trước khi lưu.
- SMTP JavaMail trực tiếp cũ được thay bằng `SpringOtpMailService` dùng Spring Mail/`JavaMailSender`. Tên người gửi là `KHANGGEAR`; cấu hình lấy từ biến môi trường, không sao chép cấu hình Tomcat hoặc secret cũ.
- Tomcat cũ có `setenv.bat` tại đường dẫn đã cung cấp và khai báo nhóm `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, `SMTP_FROM`, `SMTP_STARTTLS`. Dự án mới chuẩn hóa sang `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM_NAME`, `MAIL_STARTTLS`; khi chạy ngoài Tomcat phải export các biến này vào shell/IDE.

## Dieu chinh o du an moi

- Giai doan 1 chua tao entity, repository, CRUD, Security hoac decorator.
- SQL Server se dung cau hinh ngoai Git qua `application-local.properties`; khong mang password/cau hinh cu sang.
- Du an cu tung gap Category HTTP 200 nhung body rong khi SiteMesh resolve decorator sai. Giai doan sau phai co mapping hep va test body that.
