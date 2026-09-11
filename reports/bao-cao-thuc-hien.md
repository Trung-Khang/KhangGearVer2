# Bao cao Giai doan 2

- Da them Entity `Category` va `User`, enum `ADMIN`, `MANAGER`, `CUSTOMER`, timestamp lifecycle callback va Jakarta Validation.
- Da them repository tim kiem/phan trang, kiem tra trung va dem ADMIN active.
- Service transaction trim/normalize, chan trung, giu password cu khi sua rong, BCrypt password moi va bao ve tu xoa/tu khoa/ADMIN active cuoi cung.
- Script SQL idempotent trong `sql/` tao database, schema, index va seed hash BCrypt; khong co plaintext password hay secret.
- Test repository dung H2 profile `test`; service dung Mockito/BCrypt. `mvn clean test` pass 7/7 test va `mvn clean package` pass.
- Chua kiem thu SQL Server that do chua co credential qua bien moi truong. Chua co Security, controller CRUD, JSP CRUD hay SiteMesh decorator.
