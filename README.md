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

Chua trien khai Spring Security thuc te, CRUD controller/JSP, SiteMesh decorator, upload hay storefront.
