<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/jakarta-tags-core.tld" %>
<!doctype html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Thống kê | KhangGear</title><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"></head>
<body class="bg-light">
<main class="container py-4">
  <h1 class="h3 mb-4">Thống kê</h1>
  <div class="row g-3">
    <div class="col-md-4"><div class="card"><div class="card-body"><div>Sản phẩm</div><div class="display-6">${summary.products}</div></div></div></div>
    <div class="col-md-4"><div class="card"><div class="card-body"><div>Đang bán</div><div class="display-6">${summary.activeProducts}</div></div></div></div>
    <div class="col-md-4"><div class="card"><div class="card-body"><div>Sắp hết</div><div class="display-6">${summary.lowStockProducts}</div></div></div></div>
    <div class="col-md-4"><div class="card"><div class="card-body"><div>Danh mục</div><div class="display-6">${summary.categories}</div></div></div></div>
    <div class="col-md-4"><div class="card"><div class="card-body"><div>Người dùng</div><div class="display-6">${summary.users}</div></div></div></div>
    <div class="col-md-4"><div class="card"><div class="card-body"><div>Đơn hàng</div><div class="display-6">${summary.orders}</div></div></div></div>
  </div>
  <h2 class="h5 mt-4">Đơn hàng theo trạng thái</h2>
  <ul><c:forEach var="entry" items="${summary.ordersByStatus}"><li>${entry.key}: ${entry.value}</li></c:forEach></ul>
</main>
</body>
</html>
