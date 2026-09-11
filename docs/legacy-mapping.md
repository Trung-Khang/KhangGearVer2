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

## Dieu chinh o du an moi

- Giai doan 1 chua tao entity, repository, CRUD, Security hoac decorator.
- SQL Server se dung cau hinh ngoai Git qua `application-local.properties`; khong mang password/cau hinh cu sang.
- Du an cu tung gap Category HTTP 200 nhung body rong khi SiteMesh resolve decorator sai. Giai doan sau phai co mapping hep va test body that.
