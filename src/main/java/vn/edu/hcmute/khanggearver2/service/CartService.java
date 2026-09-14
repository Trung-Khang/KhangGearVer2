package vn.edu.hcmute.khanggearver2.service;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import vn.edu.hcmute.khanggearver2.web.api.CartDto;
import vn.edu.hcmute.khanggearver2.web.api.CheckoutRequest;

public interface CartService {
    CartDto get(HttpSession session);
    CartDto add(HttpSession session, Long productId, int quantity);
    CartDto update(HttpSession session, Long productId, int quantity);
    CartDto remove(HttpSession session, Long productId);
    void clear(HttpSession session);
    Long checkout(HttpSession session, Principal principal, CheckoutRequest request);
}
