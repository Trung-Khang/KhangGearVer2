# Commerce API

- `GET /api/cart`, `POST /api/cart/items`, `PUT /api/cart/items/{productId}`, `DELETE /api/cart/items/{productId}`.
- `POST /api/cart/checkout` voi `receiverName`, `phone`, `email`, `shippingAddress`, `note`, `paymentMethod` (`COD` hoac `BANK_TRANSFER`).
- `GET /api/account/profile`, `GET /api/account/orders`, `POST /api/account/orders/{id}/cancel`.
- Request thay doi du lieu can dang nhap va CSRF header `X-CSRF-TOKEN`; token lay tu `GET /api/csrf`.
