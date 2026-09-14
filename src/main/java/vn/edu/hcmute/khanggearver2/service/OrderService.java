package vn.edu.hcmute.khanggearver2.service;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable; import vn.edu.hcmute.khanggearver2.domain.Order; import vn.edu.hcmute.khanggearver2.domain.OrderStatus;
public interface OrderService { Page<Order> search(String keyword,OrderStatus status,Pageable pageable); Order getById(Long id); Order updateStatus(Long id,OrderStatus status); }
