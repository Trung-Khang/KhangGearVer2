package vn.edu.hcmute.khanggearver2.web.api;

import java.util.List;

public record StorefrontPageDto(List<StorefrontProductDto> content, int page, int size, long totalElements, int totalPages) {}
