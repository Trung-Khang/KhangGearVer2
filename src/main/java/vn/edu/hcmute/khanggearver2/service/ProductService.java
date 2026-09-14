package vn.edu.hcmute.khanggearver2.service;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable; import vn.edu.hcmute.khanggearver2.domain.Product;
public interface ProductService { Product create(Product product); Product getById(Long id); Product update(Long id, Product input); void deleteOrDeactivate(Long id); Product toggleActive(Long id, boolean active); Page<Product> search(String keyword, Long categoryId, Boolean active, Pageable pageable); }
