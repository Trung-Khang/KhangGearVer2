package vn.edu.hcmute.khanggearver2.security;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;

@Service @Profile("!foundation")
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public CustomUserDetailsService(UserRepository users) { this.users = users; }
    @Override public UserDetails loadUserByUsername(String username) {
        User user = users.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("Đăng nhập không thành công"));
        if (!Boolean.TRUE.equals(user.getActive())) throw new DisabledException("Tài khoản đã bị khóa");
        if (!Boolean.TRUE.equals(user.getEmailVerified())) throw new CredentialsExpiredException("Email chưa được xác minh");
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), java.util.List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
