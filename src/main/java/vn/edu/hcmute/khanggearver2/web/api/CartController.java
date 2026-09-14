package vn.edu.hcmute.khanggearver2.web.api;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hcmute.khanggearver2.service.CartService;

@RestController
@Profile("!foundation")
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cart;
    public CartController(CartService cart) { this.cart = cart; }
    @GetMapping public CartDto get(HttpSession session) { return cart.get(session); }
    @PostMapping("/items") public CartDto add(@RequestBody CartItemRequest request, HttpSession session) { return cart.add(session, request.productId(), request.quantity()); }
    @PutMapping("/items/{productId}") public CartDto update(@PathVariable Long productId, @RequestBody CartItemRequest request, HttpSession session) { return cart.update(session, productId, request.quantity()); }
    @DeleteMapping("/items/{productId}") public CartDto remove(@PathVariable Long productId, HttpSession session) { return cart.remove(session, productId); }
    @PostMapping("/checkout") public Long checkout(@RequestBody CheckoutRequest request, HttpSession session, Principal principal) { return cart.checkout(session, principal, request); }
}
