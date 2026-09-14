package vn.edu.hcmute.khanggearver2.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration @Profile("!foundation")
public class SecurityConfig {
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
   http.authorizeHttpRequests(a -> a.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
       .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**", "/media/**", "/login", "/register/**", "/verify-email/**", "/forgot-password/**", "/reset-password/**", "/403", "/error").permitAll()
       .requestMatchers("/admin/user/**").hasRole("ADMIN")
       .requestMatchers("/admin/category/**", "/admin/product/**", "/admin/order/**", "/admin/statistics", "/admin").hasAnyRole("ADMIN", "MANAGER")
       .requestMatchers("/admin/**").hasRole("ADMIN")
       .anyRequest().authenticated())
       .formLogin(f -> f.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/admin", true).failureUrl("/login?error").permitAll())
       .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/login?logout"))
       .exceptionHandling(e -> e.accessDeniedPage("/403"));
   return http.build();
 }
}
