package vn.edu.hcmute.khanggearver2.repository;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable; import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; import org.springframework.data.repository.query.Param; import vn.edu.hcmute.khanggearver2.domain.Product;
import java.util.Optional; import java.math.BigDecimal;
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    @Query("select p from Product p where lower(p.name) like lower(concat('%', :keyword, '%')) and (:categoryId is null or p.category.id = :categoryId) and (:active is null or p.active = :active)")
    Page<Product> search(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("active") Boolean active, Pageable pageable);
    @Query("select p from Product p where p.active = true and (:keyword = '' or lower(p.name) like lower(concat('%', :keyword, '%')) or lower(coalesce(p.description, '')) like lower(concat('%', :keyword, '%'))) and (:categoryId is null or p.category.id = :categoryId) and (:minPrice is null or p.price >= :minPrice) and (:maxPrice is null or p.price <= :maxPrice) and (:inStock = false or p.stock > 0)")
    Page<Product> searchStorefront(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, @Param("inStock") boolean inStock, Pageable pageable);
    long countByActiveTrue(); long countByStockLessThanEqual(Integer stock);
    Optional<Product> findByIdAndActiveTrue(Long id);
}
