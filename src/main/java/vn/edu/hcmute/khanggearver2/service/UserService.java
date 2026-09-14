package vn.edu.hcmute.khanggearver2.service;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable; import vn.edu.hcmute.khanggearver2.domain.User;
public interface UserService { User create(User user); User getById(Long id); User update(Long id, User user, Long actorId); void delete(Long id, Long actorId); User changeActive(Long id, boolean active, Long actorId); Page<User> search(String keyword, Pageable pageable); }
