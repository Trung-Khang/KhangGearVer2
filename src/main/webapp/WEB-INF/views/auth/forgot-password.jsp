<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Quên mật khẩu | KhangGear</title><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"></head>
<body class="bg-light"><main class="container py-5"><div class="card mx-auto shadow-sm" style="max-width:520px"><div class="card-body p-4"><h1 class="h3">Quên mật khẩu</h1><p class="text-muted">Nhập email để nhận mã OTP đặt lại mật khẩu.</p>
<form method="post" action="${pageContext.request.contextPath}/forgot-password"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><label class="form-label" for="email">Email</label><input class="form-control" id="email" name="email" type="email" required maxlength="254" value="${email}"><div class="text-danger small">${fieldErrors.email}</div><button class="btn btn-primary mt-3" type="submit">Gửi OTP</button></form><a class="d-inline-block mt-3" href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
</div></div></main></body></html>
