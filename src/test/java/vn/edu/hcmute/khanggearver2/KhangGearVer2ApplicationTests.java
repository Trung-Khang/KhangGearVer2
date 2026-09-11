package vn.edu.hcmute.khanggearver2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("foundation")
class KhangGearVer2ApplicationTests {
    @Test
    void contextLoads() {
    }

    @Test
    void externalTomcatBootstrapRegistersApplicationSource() {
        SpringApplicationBuilder builder = new TestApplication().configured(new SpringApplicationBuilder());

        assertThat(builder.build().getAllSources()).contains(KhangGearVer2Application.class);
    }

    private static final class TestApplication extends KhangGearVer2Application {
        private SpringApplicationBuilder configured(SpringApplicationBuilder builder) {
            return configure(builder);
        }
    }
}
