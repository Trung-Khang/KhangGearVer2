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

Giai doan 1 chi dung skeleton va JSP kiem tra tai `/`. Profile mac dinh `foundation` khong ket noi SQL Server va chua bat Spring Security de khoi dong duoc khong can cau hinh may ca nhan. Chua trien khai entity, repository, CRUD Category/User, Security, upload, SiteMesh decorator, SQL Server that hay storefront.
