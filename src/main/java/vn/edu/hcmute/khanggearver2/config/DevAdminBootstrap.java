package vn.edu.hcmute.khanggearver2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.hcmute.khanggearver2.domain.Role;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;

@Configuration
@Profile("dev-bootstrap")
public class DevAdminBootstrap {
    @Bean
    CommandLineRunner createDevelopmentAdmin(UserRepository users, PasswordEncoder encoder,
            @Value("${APP_ADMIN_USERNAME:}") String username,
            @Value("${APP_ADMIN_PASSWORD:}") String password,
            @Value("${APP_ADMIN_EMAIL:}") String email) {
        return arguments -> {
            if (blank(username) || blank(password) || blank(email)
                    || users.existsByUsernameIgnoreCase(username.trim())
                    || users.existsByEmailIgnoreCase(email.trim())) return;
            User admin = new User();
            admin.setUsername(username.trim().toLowerCase(java.util.Locale.ROOT));
            admin.setEmail(email.trim().toLowerCase(java.util.Locale.ROOT));
            admin.setPassword(encoder.encode(password));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            admin.setEmailVerified(true);
            users.save(admin);
        };
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }
}
