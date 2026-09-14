package vn.edu.hcmute.khanggearver2.repository;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Query; import org.springframework.data.repository.query.Param; import vn.edu.hcmute.khanggearver2.domain.Order;
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select distinct o from Order o left join fetch o.items i left join fetch i.product where o.id = :id")
    java.util.Optional<Order> findDetailById(@Param("id") Long id);
    @Query("select o from Order o where (:status is null or o.orderStatus = :status) and (cast(o.id as string) like concat('%', :keyword, '%') or lower(o.receiverName) like lower(concat('%', :keyword, '%')) or lower(o.email) like lower(concat('%', :keyword, '%')) or o.phone like concat('%', :keyword, '%'))")
    Page<Order> search(@Param("keyword") String keyword, @Param("status") vn.edu.hcmute.khanggearver2.domain.OrderStatus status, Pageable pageable);
    Page<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    long countByOrderStatus(vn.edu.hcmute.khanggearver2.domain.OrderStatus status);
}
