package vn.edu.hcmute.khanggearver2.service.impl;

import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hcmute.khanggearver2.domain.Role;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.exception.BusinessRuleException;
import vn.edu.hcmute.khanggearver2.exception.DuplicateResourceException;
import vn.edu.hcmute.khanggearver2.exception.ResourceNotFoundException;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;
import vn.edu.hcmute.khanggearver2.service.UserService;

@Service
@Profile("!foundation")
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) { this.repository = repository; this.passwordEncoder = passwordEncoder; }
    @Override @Transactional public User create(User user) { normalize(user); rejectDuplicates(user, null); user.setPassword(passwordEncoder.encode(required(user.getPassword(), "Mat khau la bat buoc"))); return repository.save(user); }
    @Override @Transactional(readOnly = true) public User getById(Long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Khong tim thay nguoi dung")); }
    @Override @Transactional public User update(Long id, User input) { User current = getById(id); normalize(input); rejectDuplicates(input, id); current.setUsername(input.getUsername()); current.setEmail(input.getEmail()); current.setFullName(input.getFullName()); current.setPhone(input.getPhone()); current.setRole(input.getRole()); current.setActive(input.getActive()); current.setEmailVerified(input.getEmailVerified()); if (input.getPassword() != null && !input.getPassword().isBlank()) current.setPassword(passwordEncoder.encode(input.getPassword())); return repository.save(current); }
    @Override @Transactional public void delete(Long id, Long actorId) { User target = getById(id); guardSelf(target, actorId, "Tu xoa tai khoan"); guardLastActiveAdmin(target, false); repository.delete(target); }
    @Override @Transactional public User changeActive(Long id, boolean active, Long actorId) { User target = getById(id); if (!active) { guardSelf(target, actorId, "Tu khoa tai khoan"); guardLastActiveAdmin(target, true); } target.setActive(active); return repository.save(target); }
    @Override @Transactional(readOnly = true) public Page<User> search(String keyword, Pageable pageable) { return repository.search(keyword == null ? "" : keyword.trim(), pageable); }
    private void normalize(User user) { user.setUsername(required(user.getUsername(), "Username la bat buoc").toLowerCase(Locale.ROOT)); user.setEmail(required(user.getEmail(), "Email la bat buoc").toLowerCase(Locale.ROOT)); user.setFullName(blankToNull(user.getFullName())); user.setPhone(blankToNull(user.getPhone())); if (user.getRole() == null) user.setRole(Role.CUSTOMER); if (user.getActive() == null) user.setActive(true); if (user.getEmailVerified() == null) user.setEmailVerified(false); }
    private void rejectDuplicates(User input, Long currentId) { repository.findByUsernameIgnoreCase(input.getUsername()).filter(u -> !u.getId().equals(currentId)).ifPresent(u -> { throw new DuplicateResourceException("Username da ton tai"); }); repository.findByEmailIgnoreCase(input.getEmail()).filter(u -> !u.getId().equals(currentId)).ifPresent(u -> { throw new DuplicateResourceException("Email da ton tai"); }); }
    private void guardSelf(User target, Long actorId, String action) { if (target.getId().equals(actorId)) throw new BusinessRuleException("Khong duoc " + action); }
    private void guardLastActiveAdmin(User target, boolean locking) { if (target.getRole() == Role.ADMIN && Boolean.TRUE.equals(target.getActive()) && repository.countByRoleAndActiveTrue(Role.ADMIN) <= 1) throw new BusinessRuleException(locking ? "Khong the khoa ADMIN active cuoi cung" : "Khong the xoa ADMIN active cuoi cung"); }
    private String required(String value, String message) { String result = blankToNull(value); if (result == null) throw new IllegalArgumentException(message); return result; }
    private String blankToNull(String value) { if (value == null) return null; String result = value.trim(); return result.isEmpty() ? null : result; }
}
