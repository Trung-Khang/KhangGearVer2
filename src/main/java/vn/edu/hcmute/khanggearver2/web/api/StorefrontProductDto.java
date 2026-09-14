package vn.edu.hcmute.khanggearver2.web.api;

import java.math.BigDecimal;

public record StorefrontProductDto(Long id, String name, String description, BigDecimal price,
                                   Integer stock, String imageUrl, Long categoryId, String categoryName) {}
