package vn.edu.hcmute.khanggearver2.web.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryDto(Long id, String status, String paymentMethod, BigDecimal totalAmount,
                              LocalDateTime createdAt, List<String> products) {}
