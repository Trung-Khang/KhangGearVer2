<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/jakarta-tags-core.tld" %>
<!doctype html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"><title>KhangGearVer2 Admin</title></head>
<body class="bg-light">
<main class="container py-5"><div class="card shadow-sm"><div class="card-body">
<h1 class="h3">KhangGearVer2 Admin</h1><p>Xin chào <strong>${username}</strong>. Quyền: <strong>${role}</strong>.</p><p>Chào mừng đến khu vực quản trị KhangGear.</p>
<div class="d-flex flex-wrap gap-2"><a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/category/list">Quản lý danh mục</a><a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/product/list">Quản lý sản phẩm</a><a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/order/list">Quản lý đơn hàng</a><a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/statistics">Thống kê</a><c:if test="${role == 'ROLE_ADMIN'}"><a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/user/list">Quản lý người dùng</a></c:if></div>
<form class="mt-4" method="post" action="${pageContext.request.contextPath}/logout"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><button class="btn btn-danger" type="submit">Đăng xuất</button></form>
</div></div></main></body></html>
