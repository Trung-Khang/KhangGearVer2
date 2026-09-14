package vn.edu.hcmute.khanggearver2.web.api;

import org.springframework.context.annotation.Profile;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("!foundation")
public class CsrfController {
    @GetMapping("/api/csrf")
    public CsrfToken csrf(CsrfToken token) { return token; }
}
