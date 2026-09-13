package vn.edu.hcmute.khanggearver2.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.edu.hcmute.khanggearver2.domain.Category;
import vn.edu.hcmute.khanggearver2.domain.Role;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.exception.BusinessRuleException;
import vn.edu.hcmute.khanggearver2.exception.DuplicateResourceException;
import vn.edu.hcmute.khanggearver2.exception.ResourceNotFoundException;
import vn.edu.hcmute.khanggearver2.repository.CategoryRepository;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;
import vn.edu.hcmute.khanggearver2.service.impl.CategoryServiceImpl;
import vn.edu.hcmute.khanggearver2.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class ServiceTests {
    @Mock CategoryRepository categories;
    @Mock UserRepository users;

    @Test void categoryCreatesTrimmedAndRejectsDuplicates() {
        CategoryService service = new CategoryServiceImpl(categories); Category input = new Category(); input.setName("  Laptop  ");
        when(categories.existsByNameIgnoreCase("Laptop")).thenReturn(false); when(categories.save(any())).thenAnswer(i -> i.getArgument(0));
        assertThat(service.create(input).getName()).isEqualTo("Laptop");
        when(categories.existsByNameIgnoreCase("Laptop")).thenReturn(true);
        assertThatThrownBy(() -> service.create(input)).isInstanceOf(DuplicateResourceException.class);
    }
    @Test void categoryMissingUpdateThrows() { CategoryService service = new CategoryServiceImpl(categories); when(categories.findById(99L)).thenReturn(Optional.empty()); assertThatThrownBy(() -> service.update(99L, new Category())).isInstanceOf(ResourceNotFoundException.class); }
    @Test void userHashesOnCreateAndKeepsHashOnBlankUpdate() { BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); UserService service = new UserServiceImpl(users, encoder); User input = user("  Lan ", " LAN@EXAMPLE.COM "); input.setPassword("secret123"); when(users.findByUsernameIgnoreCase(any())).thenReturn(Optional.empty()); when(users.findByEmailIgnoreCase(any())).thenReturn(Optional.empty()); when(users.save(any())).thenAnswer(i -> i.getArgument(0)); User created = service.create(input); assertThat(created.getUsername()).isEqualTo("lan"); assertThat(encoder.matches("secret123", created.getPassword())).isTrue(); User stored = user("lan", "lan@example.com"); stored.setId(3L); stored.setPassword(created.getPassword()); when(users.findById(3L)).thenReturn(Optional.of(stored)); User edit = user("lan", "lan@example.com"); edit.setPassword(" "); service.update(3L, edit); assertThat(stored.getPassword()).isEqualTo(created.getPassword()); }
    @Test void userRejectsDuplicateAndProtectsSelfAndLastAdmin() { UserService service = new UserServiceImpl(users, new BCryptPasswordEncoder()); User duplicate = user("lan", "lan@example.com"); duplicate.setPassword("secret123"); User existing = user("lan", "other@example.com"); existing.setId(4L); when(users.findByUsernameIgnoreCase("lan")).thenReturn(Optional.of(existing)); assertThatThrownBy(() -> service.create(duplicate)).isInstanceOf(DuplicateResourceException.class); User admin = user("admin", "admin@example.com"); admin.setId(1L); admin.setRole(Role.ADMIN); when(users.findById(1L)).thenReturn(Optional.of(admin)); assertThatThrownBy(() -> service.delete(1L, 1L)).isInstanceOf(BusinessRuleException.class); assertThatThrownBy(() -> service.changeActive(1L, false, 1L)).isInstanceOf(BusinessRuleException.class); when(users.countByRoleAndActiveTrue(Role.ADMIN)).thenReturn(1L); assertThatThrownBy(() -> service.delete(1L, 2L)).isInstanceOf(BusinessRuleException.class); }
    private User user(String username, String email) { User user = new User(); user.setUsername(username); user.setEmail(email); user.setRole(Role.CUSTOMER); user.setActive(true); user.setEmailVerified(false); return user; }
}
