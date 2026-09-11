package vn.edu.hcmute.khanggearver2.service;

import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hcmute.khanggearver2.domain.Role;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.exception.DuplicateResourceException;
import vn.edu.hcmute.khanggearver2.exception.ResourceNotFoundException;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;

@Service
@Profile("!foundation")
public class AccountService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    public AccountService(UserRepository users, PasswordEncoder passwordEncoder) { this.users = users; this.passwordEncoder = passwordEncoder; }
    @Transactional public User registerCustomer(String username, String email, String password, String fullName, String phone) {
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT); String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (users.existsByUsernameIgnoreCase(normalizedUsername)) throw new DuplicateResourceException("Tên đăng nhập đã tồn tại.");
        if (users.existsByEmailIgnoreCase(normalizedEmail)) throw new DuplicateResourceException("Email đã tồn tại.");
        User user = new User(); user.setUsername(normalizedUsername); user.setEmail(normalizedEmail); user.setPassword(passwordEncoder.encode(password)); user.setFullName(blankToNull(fullName)); user.setPhone(blankToNull(phone)); user.setRole(Role.CUSTOMER); user.setActive(true); user.setEmailVerified(false);
        return users.save(user);
    }
    @Transactional public void verifyEmail(Long userId) { User user = get(userId); user.setEmailVerified(true); users.save(user); }
    @Transactional public void resetPassword(Long userId, String password) { User user = get(userId); user.setPassword(passwordEncoder.encode(password)); users.save(user); }
    @Transactional(readOnly = true) public User get(Long id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản.")); }
    @Transactional(readOnly = true) public User findByEmail(String email) { return email == null ? null : users.findByEmailIgnoreCase(email.trim()).orElse(null); }
    private String blankToNull(String value) { if (value == null || value.trim().isEmpty()) return null; return value.trim(); }
}
