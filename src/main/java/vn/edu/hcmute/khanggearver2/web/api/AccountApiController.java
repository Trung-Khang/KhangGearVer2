package vn.edu.hcmute.khanggearver2.web.api;

import java.security.Principal;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hcmute.khanggearver2.domain.Order;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.repository.OrderRepository;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;

@RestController
@Profile("!foundation")
@RequestMapping("/api/account")
public class AccountApiController {
    private final UserRepository users;
    private final OrderRepository orders;
    public AccountApiController(UserRepository users, OrderRepository orders) { this.users = users; this.orders = orders; }
    @GetMapping("/profile") public ResponseEntity<?> profile(Principal principal) { return users.findByUsernameIgnoreCase(principal.getName()).map(AccountApiController::profileDto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }
    @GetMapping("/orders") public List<OrderSummaryDto> orders(Principal principal) { User user = user(principal); return orders.findByCustomerIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 50, Sort.by("createdAt").descending())).getContent().stream().map(this::orderDto).toList(); }
    @PostMapping("/orders/{id}/cancel") public ResponseEntity<OrderSummaryDto> cancel(@PathVariable Long id, Principal principal) { User user = user(principal); Order order = orders.findById(id).orElseThrow(); if (!order.getCustomer().getId().equals(user.getId()) || !(order.getOrderStatus().name().equals("PENDING") || order.getOrderStatus().name().equals("CONFIRMED"))) return ResponseEntity.badRequest().build(); order.setOrderStatus(vn.edu.hcmute.khanggearver2.domain.OrderStatus.CANCELLED); return ResponseEntity.ok(orderDto(orders.save(order))); }
    private User user(Principal principal) { return users.findByUsernameIgnoreCase(principal.getName()).orElseThrow(); }
    private static java.util.Map<String, Object> profileDto(User user) { return java.util.Map.of("id", user.getId(), "username", user.getUsername(), "fullName", user.getFullName() == null ? "" : user.getFullName(), "email", user.getEmail(), "phone", user.getPhone() == null ? "" : user.getPhone(), "role", user.getRole().name()); }
    private OrderSummaryDto orderDto(Order order) { return new OrderSummaryDto(order.getId(), order.getOrderStatus().name(), order.getPaymentMethod().name(), order.getTotalAmount(), order.getCreatedAt(), order.getItems().stream().map(i -> i.getProductName() + " x " + i.getQuantity()).toList()); }
}
