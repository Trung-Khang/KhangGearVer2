package vn.edu.hcmute.khanggearver2.web.api;

import java.math.BigDecimal;

public record CartLineDto(Long productId, String name, BigDecimal unitPrice, int quantity, int stock,
                          BigDecimal lineTotal, String imageUrl) {}
