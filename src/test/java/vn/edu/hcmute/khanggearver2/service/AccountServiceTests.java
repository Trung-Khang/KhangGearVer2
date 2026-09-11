package vn.edu.hcmute.khanggearver2.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.edu.hcmute.khanggearver2.domain.Role;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.exception.DuplicateResourceException;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceTests {
    @Mock private UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Test void registrationAlwaysCreatesUnverifiedCustomerWithBcryptPassword() {
        when(users.existsByUsernameIgnoreCase(any())).thenReturn(false); when(users.existsByEmailIgnoreCase(any())).thenReturn(false); when(users.save(any())).thenAnswer(i -> i.getArgument(0));
        User created = new AccountService(users, encoder).registerCustomer("New.User", "NEW@TEST.LOCAL", "test-password", "Test", "0900000000");
        assertThat(created.getRole()).isEqualTo(Role.CUSTOMER); assertThat(created.getActive()).isTrue(); assertThat(created.getEmailVerified()).isFalse(); assertThat(encoder.matches("test-password", created.getPassword())).isTrue();
    }
    @Test void rejectsDuplicateUsernameOrEmail() {
        when(users.existsByUsernameIgnoreCase(any())).thenReturn(true);
        assertThatThrownBy(() -> new AccountService(users, encoder).registerCustomer("taken", "new@test.local", "test-password", null, null)).isInstanceOf(DuplicateResourceException.class);
        reset(users); when(users.existsByUsernameIgnoreCase(any())).thenReturn(false); when(users.existsByEmailIgnoreCase(any())).thenReturn(true);
        assertThatThrownBy(() -> new AccountService(users, encoder).registerCustomer("new", "taken@test.local", "test-password", null, null)).isInstanceOf(DuplicateResourceException.class);
    }
}
