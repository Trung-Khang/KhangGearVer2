<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Đăng ký | KhangGear</title><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"></head>
<body class="bg-light"><main class="container py-5"><div class="card mx-auto shadow-sm" style="max-width:560px"><div class="card-body p-4"><h1 class="h3">Tạo tài khoản KhangGear</h1>
<div class="alert alert-danger" ${empty fieldErrors.general ? 'hidden' : ''}>${fieldErrors.general}</div>
<form method="post" action="${pageContext.request.contextPath}/register"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<label class="form-label" for="username">Tên đăng nhập</label><input class="form-control" id="username" name="username" required minlength="3" maxlength="50" pattern="[A-Za-z0-9_.-]{3,50}" value="${form.username}"><div class="text-danger small">${fieldErrors.username}</div>
<label class="form-label mt-2" for="email">Email</label><input class="form-control" id="email" name="email" type="email" required maxlength="254" value="${form.email}"><div class="text-danger small">${fieldErrors.email}</div>
<label class="form-label mt-2" for="fullName">Họ tên</label><input class="form-control" id="fullName" name="fullName" maxlength="150" value="${form.fullName}">
<label class="form-label mt-2" for="phone">Số điện thoại</label><input class="form-control" id="phone" name="phone" maxlength="20" value="${form.phone}"><div class="text-danger small">${fieldErrors.phone}</div>
<label class="form-label mt-2" for="password">Mật khẩu</label><input class="form-control" id="password" name="password" type="password" required minlength="8"><div class="text-danger small">${fieldErrors.password}</div>
<label class="form-label mt-2" for="confirmPassword">Xác nhận mật khẩu</label><input class="form-control" id="confirmPassword" name="confirmPassword" type="password" required minlength="8"><div class="text-danger small">${fieldErrors.confirmPassword}</div>
<button class="btn btn-primary mt-3" type="submit">Đăng ký</button></form><a class="d-inline-block mt-3" href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
</div></div></main></body></html>
