package vn.edu.hcmute.khanggearver2.service;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable; import vn.edu.hcmute.khanggearver2.domain.Category;
public interface CategoryService { Category create(Category category); Category getById(Long id); Category update(Long id, Category category); void delete(Long id); Page<Category> search(String keyword, Pageable pageable); }
