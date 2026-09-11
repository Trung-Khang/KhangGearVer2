package vn.edu.hcmute.khanggearver2.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.data.domain.PageRequest; import org.springframework.test.context.ActiveProfiles; import vn.edu.hcmute.khanggearver2.domain.Category; import vn.edu.hcmute.khanggearver2.domain.Role; import vn.edu.hcmute.khanggearver2.domain.User;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:khanggearver2;MODE=MSSQLServer;DB_CLOSE_DELAY=-1", "spring.datasource.username=sa", "spring.datasource.password=", "spring.datasource.driver-class-name=org.h2.Driver", "spring.jpa.hibernate.ddl-auto=create-drop"}) @ActiveProfiles("test") class RepositoryTests {
 @Autowired CategoryRepository categories; @Autowired UserRepository users;
 @BeforeEach void clear(){ users.deleteAll(); categories.deleteAll(); }
 @Test void categoryFindsIgnoringCaseAndPagesSearch(){ Category c=new Category(); c.setName("Laptop"); categories.saveAndFlush(c); assertThat(categories.existsByNameIgnoreCase("lApToP")).isTrue(); assertThat(categories.findByNameContainingIgnoreCase("TOP", PageRequest.of(0,1)).getTotalElements()).isEqualTo(1); }
 @Test void userFindsSearchesAndCountsActiveAdmins(){ User admin=user("root","root@example.com",Role.ADMIN); users.save(admin); User customer=user("lan","lan@example.com",Role.CUSTOMER); customer.setFullName("Nguyen Van Lan"); users.saveAndFlush(customer); assertThat(users.findByUsernameIgnoreCase("ROOT")).isPresent(); assertThat(users.findByEmailIgnoreCase("LAN@EXAMPLE.COM")).isPresent(); assertThat(users.search("van",PageRequest.of(0,5)).getTotalElements()).isEqualTo(1); assertThat(users.countByRoleAndActiveTrue(Role.ADMIN)).isEqualTo(1); }
 private User user(String username,String email,Role role){User u=new User();u.setUsername(username);u.setEmail(email);u.setPassword("hash");u.setRole(role);return u;}
}
