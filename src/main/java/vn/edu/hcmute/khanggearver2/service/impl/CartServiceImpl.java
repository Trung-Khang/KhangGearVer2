package vn.edu.hcmute.khanggearver2.service.impl;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.hcmute.khanggearver2.domain.Order;
import vn.edu.hcmute.khanggearver2.domain.OrderItem;
import vn.edu.hcmute.khanggearver2.domain.PaymentMethod;
import vn.edu.hcmute.khanggearver2.domain.Product;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.exception.BusinessRuleException;
import vn.edu.hcmute.khanggearver2.repository.OrderRepository;
import vn.edu.hcmute.khanggearver2.repository.ProductRepository;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;
import vn.edu.hcmute.khanggearver2.service.CartService;
import vn.edu.hcmute.khanggearver2.web.api.CartDto;
import vn.edu.hcmute.khanggearver2.web.api.CartLineDto;
import vn.edu.hcmute.khanggearver2.web.api.CheckoutRequest;

@Service
@Profile("!foundation")
public class CartServiceImpl implements CartService {
    private static final String CART_KEY = "khanggear.cart";
    private final ProductRepository products;
    private final UserRepository users;
    private final OrderRepository orders;

    public CartServiceImpl(ProductRepository products, UserRepository users, OrderRepository orders) {
        this.products = products;
        this.users = users;
        this.orders = orders;
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto get(HttpSession session) {
        return snapshot(session, cart(session));
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto add(HttpSession session, Long productId, int quantity) {
        requirePositive(quantity);
        Product product = product(productId);
        Map<Long, Integer> cart = cart(session);
        int next = cart.getOrDefault(productId, 0) + quantity;
        ensureStock(product, next);
        cart.put(productId, next);
        session.setAttribute(CART_KEY, cart);
        return snapshot(session, cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto update(HttpSession session, Long productId, int quantity) {
        requirePositive(quantity);
        Product product = product(productId);
        ensureStock(product, quantity);
        Map<Long, Integer> cart = cart(session);
        cart.put(productId, quantity);
        session.setAttribute(CART_KEY, cart);
        return snapshot(session, cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto remove(HttpSession session, Long productId) {
        cart(session).remove(productId);
        return snapshot(session, cart(session));
    }

    @Override
    public void clear(HttpSession session) {
        session.removeAttribute(CART_KEY);
    }

    @Override
    @Transactional
    public Long checkout(HttpSession session, Principal principal, CheckoutRequest request) {
        User user = users.findByUsernameIgnoreCase(principal.getName())
                .orElseThrow(() -> new BusinessRuleException("Không tìm thấy tài khoản hiện tại."));
        String receiver = required(request.receiverName(), "Họ tên người nhận là bắt buộc.");
        String phone = required(request.phone(), "Số điện thoại là bắt buộc.");
        String email = required(request.email(), "Email là bắt buộc.");
        String address = required(request.shippingAddress(), "Địa chỉ giao hàng là bắt buộc.");
        PaymentMethod payment;
        try { payment = PaymentMethod.valueOf(required(request.paymentMethod(), "Phương thức thanh toán là bắt buộc.")); }
        catch (IllegalArgumentException ex) { throw new BusinessRuleException("Phương thức thanh toán không hợp lệ."); }
        Map<Long, Integer> cart = cart(session);
        if (cart.isEmpty()) throw new BusinessRuleException("Giỏ hàng đang trống.");
        Order order = new Order();
        order.setCustomer(user); order.setReceiverName(receiver); order.setPhone(phone);
        order.setEmail(email); order.setShippingAddress(address); order.setNote(request.note()); order.setPaymentMethod(payment);
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> lines = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product product = product(entry.getKey());
            ensureStock(product, entry.getValue());
            int quantity = entry.getValue();
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            OrderItem item = new OrderItem(); item.setOrder(order); item.setProduct(product);
            item.setProductName(product.getName()); item.setUnitPrice(product.getPrice()); item.setQuantity(quantity); item.setLineTotal(lineTotal);
            lines.add(item); total = total.add(lineTotal); product.setStock(product.getStock() - quantity); products.save(product);
        }
        order.setItems(lines); order.setTotalAmount(total);
        Order saved = orders.save(order);
        session.removeAttribute(CART_KEY);
        return saved.getId();
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> cart(HttpSession session) {
        Object value = session.getAttribute(CART_KEY);
        if (value instanceof Map<?, ?> map) {
            Map<Long, Integer> copy = new LinkedHashMap<>();
            map.forEach((key, item) -> { if (key instanceof Long id && item instanceof Integer quantity) copy.put(id, quantity); });
            return copy;
        }
        return new LinkedHashMap<>();
    }

    private CartDto snapshot(HttpSession session, Map<Long, Integer> source) {
        List<CartLineDto> lines = new ArrayList<>(); BigDecimal total = BigDecimal.ZERO; int count = 0;
        Map<Long, Integer> valid = new LinkedHashMap<>();
        for (Map.Entry<Long, Integer> entry : source.entrySet()) {
            Product product = products.findByIdAndActiveTrue(entry.getKey()).orElse(null);
            if (product == null || entry.getValue() == null || entry.getValue() < 1) continue;
            int quantity = Math.min(entry.getValue(), product.getStock()); if (quantity < 1) continue;
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            lines.add(new CartLineDto(product.getId(), product.getName(), product.getPrice(), quantity, product.getStock(), lineTotal,
                    product.getImage() == null ? null : "/media/products/" + product.getImage()));
            valid.put(product.getId(), quantity); total = total.add(lineTotal); count += quantity;
        }
        session.setAttribute(CART_KEY, valid);
        return new CartDto(lines, total, count);
    }

    private Product product(Long id) { return products.findByIdAndActiveTrue(id).orElseThrow(() -> new BusinessRuleException("Sản phẩm không còn kinh doanh.")); }
    private void ensureStock(Product product, int quantity) { if (quantity > product.getStock()) throw new BusinessRuleException("Sản phẩm " + product.getName() + " không đủ tồn kho."); }
    private void requirePositive(int quantity) { if (quantity < 1) throw new BusinessRuleException("Số lượng phải là số nguyên dương."); }
    private String required(String value, String message) { if (value == null || value.trim().isEmpty()) throw new BusinessRuleException(message); return value.trim(); }
}
