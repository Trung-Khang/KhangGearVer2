# Storefront API

## Public GET endpoints

- `GET /api/storefront/categories`: danh muc dang hoat dong.
- `GET /api/storefront/products`: danh sach phan trang. Ho tro `keyword`, `categoryId`, `minPrice`, `maxPrice`, `inStock`, `sort`, `page`, `size`.
- `GET /api/storefront/products/{id}`: chi tiet san pham dang ban.
- `GET /api/storefront/featured`, `/best-selling`, `/newest`: cac danh sach nhanh cho trang chu.

Response san pham dung DTO, gia va ton kho doc tu database; anh chi tra URL media public, khong tra duong dan file tren may chu.
