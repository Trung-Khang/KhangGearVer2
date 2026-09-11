package vn.edu.hcmute.khanggearver2.web;
import java.security.Principal; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.GetMapping;
@Controller public class AdminController { @GetMapping("/admin") String dashboard(Principal principal, Model model){model.addAttribute("username",principal.getName());model.addAttribute("role",org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());return "admin/dashboard";} }
