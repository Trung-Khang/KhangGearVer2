<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Đăng nhập | KhangGear</title><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"></head>
<body class="bg-light"><main class="container py-5"><div class="card mx-auto shadow-sm" style="max-width:440px"><div class="card-body p-4">
<h1 class="h3">Đăng nhập KhangGear</h1><p class="text-muted">Sử dụng tài khoản đã được kích hoạt.</p>
<div class="alert alert-danger" role="alert" ${param.error == null ? 'hidden' : ''}>Thông tin đăng nhập không hợp lệ, tài khoản bị khóa hoặc email chưa xác minh.</div>
<div class="alert alert-success" role="alert" ${param.logout == null && message == null ? 'hidden' : ''}>${message != null ? message : 'Đăng xuất thành công.'}</div>
<form method="post" action="${pageContext.request.contextPath}/login"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<div class="mb-3"><label class="form-label" for="username">Tên đăng nhập</label><input class="form-control" id="username" name="username" required autocomplete="username"></div>
<div class="mb-3"><label class="form-label" for="password">Mật khẩu</label><input class="form-control" id="password" name="password" type="password" required autocomplete="current-password"></div>
<button class="btn btn-primary w-100" type="submit">Đăng nhập</button></form>
<div class="d-flex justify-content-between mt-3"><a href="${pageContext.request.contextPath}/register">Đăng ký</a><a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a></div>
</div></div></main></body></html>
