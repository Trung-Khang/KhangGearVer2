package vn.edu.hcmute.khanggearver2.web.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.hcmute.khanggearver2.domain.Category;
import vn.edu.hcmute.khanggearver2.domain.Product;
import vn.edu.hcmute.khanggearver2.repository.CategoryRepository;
import vn.edu.hcmute.khanggearver2.repository.ProductRepository;

@RestController
@Profile("!foundation")
@RequestMapping("/api/storefront")
public class StorefrontController {
    private final CategoryRepository categories;
    private final ProductRepository products;

    public StorefrontController(CategoryRepository categories, ProductRepository products) {
        this.categories = categories;
        this.products = products;
    }

    @GetMapping("/categories")
    public List<StorefrontCategoryDto> categories() {
        return categories.findByActiveTrueOrderByNameAsc().stream().map(this::categoryDto).toList();
    }

    @GetMapping("/products")
    public StorefrontPageDto products(@RequestParam(defaultValue = "") String keyword,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) BigDecimal minPrice,
                                      @RequestParam(required = false) BigDecimal maxPrice,
                                      @RequestParam(defaultValue = "false") boolean inStock,
                                      @RequestParam(defaultValue = "newest") String sort,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "12") int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 48);
        Page<Product> result = products.searchStorefront(keyword.trim(), categoryId, minPrice, maxPrice, inStock,
                PageRequest.of(safePage, safeSize, sortFor(sort)));
        return new StorefrontPageDto(result.getContent().stream().map(this::productDto).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<StorefrontProductDto> product(@PathVariable Long id) {
        return products.findByIdAndActiveTrue(id).map(product -> ResponseEntity.ok(productDto(product)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/featured")
    public StorefrontPageDto featured(@RequestParam(defaultValue = "8") int size) {
        return products("", null, null, null, true, "newest", 0, size);
    }

    @GetMapping("/best-selling")
    public StorefrontPageDto bestSelling(@RequestParam(defaultValue = "8") int size) {
        return products("", null, null, null, true, "price-desc", 0, size);
    }

    @GetMapping("/newest")
    public StorefrontPageDto newest(@RequestParam(defaultValue = "8") int size) {
        return products("", null, null, null, false, "newest", 0, size);
    }

    private Sort sortFor(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "price-asc" -> Sort.by("price").ascending();
            case "price-desc" -> Sort.by("price").descending();
            case "name" -> Sort.by("name").ascending();
            default -> Sort.by("createdAt").descending();
        };
    }

    private StorefrontCategoryDto categoryDto(Category category) {
        return new StorefrontCategoryDto(category.getId(), category.getName(), category.getDescription(),
                category.getIcon() == null ? null : "/media/categories/" + category.getIcon());
    }

    private StorefrontProductDto productDto(Product product) {
        return new StorefrontProductDto(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
                product.getStock(), product.getImage() == null ? null : "/media/products/" + product.getImage(),
                product.getCategory().getId(), product.getCategory().getName());
    }
}
