# Role matrix

| Khu vuc | Anonymous | CUSTOMER | MANAGER | ADMIN |
|---|---|---|---|---|
| Catalog GET | Co | Co | Co | Co |
| Cart/checkout/account | Khong | Co | Co | Co |
| Category/Product/Order/Statistics admin | Khong | 403 | Co | Co |
| User admin | Khong | 403 | 403 | Co |

POST/PUT/DELETE thay doi du lieu can CSRF. Gia, ton kho va quyen duoc xac minh o backend.
