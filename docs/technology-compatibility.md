# Tuong thich cong nghe Giai doan 1

| Cong nghe | Phien ban chon | Ly do va nguon da kiem tra |
| --- | --- | --- |
| Spring Boot | 4.1.1 | Ban on dinh moi duoc tai lieu Spring Boot khuyen nghi. [System Requirements](https://docs.spring.io/spring-boot/system-requirements.html). |
| Java | 17 | Spring Boot 4.1.1 yeu cau toi thieu Java 17. |
| Servlet container | Embedded Tomcat 11.0.x, Servlet 6.1 | Duoc Spring Boot 4.1.1 ho tro chinh thuc. |
| JSP | Jakarta Server Pages qua `tomcat-embed-jasper` | JSP can Jasper trong Tomcat embedded. [Jakarta Pages 3.0](https://jakarta.ee/specifications/pages/3.0/). |
| JSTL | `org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1` | Dung implementation Jakarta, khong dung `javax.*`. [Jakarta Tags](https://jakarta.ee/specifications/tags/3.0/jakarta-tags-spec-3.0.pdf). |
| SiteMesh Decorator 3 | `org.sitemesh:sitemesh:3.3.0-RC1` | Day la artifact 3.3.x duoc SiteMesh phat hanh qua Maven; nhanh 3.3.x ho tro Jakarta EE 11, Tomcat 11 va Spring Boot 4. [SiteMesh 3](https://github.com/sitemesh/sitemesh3). |
| SQL Server | Microsoft JDBC Driver do Spring Boot BOM quan ly | Khai bao runtime-only; URL/credential dat trong file local bi ignore. |

## Rui ro can xu ly o giai doan sau

- JSP voi Spring Boot WAR can kiem tra tren embedded Tomcat va khi deploy WAR ngoai Tomcat.
- SiteMesh `3.3.0-RC1` la release candidate truoc ban final 3.3.0. Decorator/filter chua duoc bat o Giai doan 1; khi tich hop phai dung API `jakarta.*`, mapping hep, chon dung integration filter/view-resolver va test response body.
- Spring Security moi chi la dependency. Profile `foundation` vo hieu hoa Security auto-configuration de JSP kiem tra truy cap duoc khi chua co login; chua co `SecurityFilterChain`, role policy hoac trang login trong Giai doan 1.
