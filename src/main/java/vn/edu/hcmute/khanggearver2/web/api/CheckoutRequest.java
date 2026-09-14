package vn.edu.hcmute.khanggearver2.web.api;

public record CheckoutRequest(String receiverName, String phone, String email, String shippingAddress,
                              String note, String paymentMethod) {}
