package vn.edu.hcmute.khanggearver2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class KhangGearVer2Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(KhangGearVer2Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(KhangGearVer2Application.class, args);
    }
}
