package vn.edu.hcmute.khanggearver2.repository;
import org.springframework.data.jpa.repository.JpaRepository; import vn.edu.hcmute.khanggearver2.domain.OrderItem;
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> { boolean existsByProductId(Long productId); }
