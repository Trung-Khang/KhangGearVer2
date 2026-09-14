package vn.edu.hcmute.khanggearver2.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vn.edu.hcmute.khanggearver2.domain.Role;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:securitytests;MODE=MSSQLServer;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SecurityIntegrationTests {
    @Autowired private WebApplicationContext context;
    @Autowired private UserRepository users;
    @Autowired private PasswordEncoder passwordEncoder;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        users.deleteAll();
        users.save(testAccount("admin", "admin@test.local", Role.ADMIN, true, true));
        users.save(testAccount("manager", "manager@test.local", Role.MANAGER, true, true));
        users.save(testAccount("customer", "customer@test.local", Role.CUSTOMER, true, true));
        users.save(testAccount("inactive", "inactive@test.local", Role.ADMIN, false, true));
        users.save(testAccount("unverified", "unverified@test.local", Role.ADMIN, true, false));
    }

    @Test
    void loginAndAdminAuthorizationFollowTheExpectedPolicy() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
        mockMvc.perform(post("/register"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
        mockMvc.perform(get("/admin").with(user("manager").roles("MANAGER")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
        mockMvc.perform(get("/admin").with(user("customer").roles("CUSTOMER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/category/list").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(view().name("admin/category/list"));
        mockMvc.perform(get("/admin/product/list").with(user("manager").roles("MANAGER")))
                .andExpect(status().isOk()).andExpect(view().name("admin/product/list"));
        mockMvc.perform(get("/admin/order/list").with(user("manager").roles("MANAGER")))
                .andExpect(status().isOk()).andExpect(view().name("admin/order/list"));
        mockMvc.perform(get("/admin/statistics").with(user("manager").roles("MANAGER")))
                .andExpect(status().isOk()).andExpect(view().name("admin/statistics"));
    }

    @Test
    void databaseLoginRejectsInvalidInactiveAndUnverifiedAccounts() throws Exception {
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "admin")
                        .param("password", "test-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "admin")
                        .param("password", "not-the-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "inactive")
                        .param("password", "test-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "unverified")
                        .param("password", "test-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void logoutRequiresPostAndCsrf() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/logout").with(csrf()).with(user("admin").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void userAdministrationIsAdminOnlyAndCreatesBcryptAccounts() throws Exception {
        mockMvc.perform(get("/admin/user/list").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(view().name("admin/user/list"));
        mockMvc.perform(get("/admin/user/list").with(user("manager").roles("MANAGER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/user/list").with(user("customer").roles("CUSTOMER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/admin/user/add").with(user("admin").roles("ADMIN"))
                        .param("username", "missing-csrf"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/admin/user/add").with(user("admin").roles("ADMIN")).with(csrf())
                        .param("username", "new-admin-user").param("email", "new-admin-user@test.local")
                        .param("fullName", "New User").param("phone", "0900000011").param("role", "MANAGER")
                        .param("password", "new-password").param("confirmPassword", "new-password")
                        .param("active", "true").param("emailVerified", "true"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/user/list"));
        User created = users.findByUsernameIgnoreCase("new-admin-user").orElseThrow();
        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches("new-password", created.getPassword())).isTrue();
        org.assertj.core.api.Assertions.assertThat(created.getRole()).isEqualTo(Role.MANAGER);
    }

    private User testAccount(String username, String email, Role role, boolean active, boolean verified) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("test-password"));
        user.setRole(role);
        user.setActive(active);
        user.setEmailVerified(verified);
        return user;
    }
}
