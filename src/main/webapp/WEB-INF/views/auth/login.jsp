<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <title>&#272;&#259;ng nh&#7853;p | KhangGearVer2</title>
</head>
<body class="bg-light">
<main class="container py-5">
    <div class="card mx-auto shadow-sm" style="max-width:420px">
        <div class="card-body p-4">
            <h1 class="h3">&#272;&#259;ng nh&#7853;p KhangGearVer2</h1>
            <p class="text-muted">S&#7917; d&#7909;ng t&#224;i kho&#7843;n &#273;&#227; &#273;&#432;&#7907;c k&#237;ch ho&#7841;t.</p>
            <%-- Spring Security reads these parameter names. --%>
            <div class="alert alert-danger" role="alert" ${param.error == null ? 'hidden' : ''}>Th&#244;ng tin &#273;&#259;ng nh&#7853;p kh&#244;ng h&#7907;p l&#7879;, t&#224;i kho&#7843;n b&#7883; kh&#243;a ho&#7863;c email ch&#432;a x&#225;c minh.</div>
            <div class="alert alert-success" role="alert" ${param.logout == null ? 'hidden' : ''}>&#272;&#259;ng xu&#7845;t th&#224;nh c&#244;ng.</div>
            <div class="alert alert-success" role="alert" ${message == null ? 'hidden' : ''}>${message}</div>
            <form method="post" action="${pageContext.request.contextPath}/login">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                <div class="mb-3">
                    <label class="form-label" for="username">T&#234;n &#273;&#259;ng nh&#7853;p</label>
                    <input class="form-control" id="username" name="username" required autocomplete="username">
                </div>
                <div class="mb-3">
                    <label class="form-label" for="password">M&#7853;t kh&#7849;u</label>
                    <input class="form-control" id="password" name="password" type="password" required autocomplete="current-password">
                </div>
                <button class="btn btn-primary w-100" type="submit">&#272;&#259;ng nh&#7853;p</button>
            </form>
        </div>
    </div>
</main>
</body>
</html>
