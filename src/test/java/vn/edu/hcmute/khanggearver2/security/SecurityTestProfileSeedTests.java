package vn.edu.hcmute.khanggearver2.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import vn.edu.hcmute.khanggearver2.domain.Role;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("security-test")
class SecurityTestProfileSeedTests {
    @Autowired private UserRepository users;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void securityTestProfileLoadsAnActiveVerifiedAdminWithABcryptPassword() {
        User admin = users.findByUsernameIgnoreCase("admin-demo").orElseThrow();
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
        assertThat(admin.getActive()).isTrue();
        assertThat(admin.getEmailVerified()).isTrue();
        assertThat(passwordEncoder.matches("password", admin.getPassword())).isTrue();
        assertThat(users.findByUsernameIgnoreCase("manager-demo")).isPresent();
        assertThat(users.findByUsernameIgnoreCase("customer-demo")).isPresent();
    }
}
