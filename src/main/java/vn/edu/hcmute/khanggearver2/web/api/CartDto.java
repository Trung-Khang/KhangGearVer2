package vn.edu.hcmute.khanggearver2.web.api;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(List<CartLineDto> items, BigDecimal total, int itemCount) {}
