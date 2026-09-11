package vn.edu.hcmute.khanggearver2.web;
import org.springframework.stereotype.Controller; import org.springframework.web.bind.annotation.GetMapping;
@Controller public class AuthController { @GetMapping("/login") String login(){return "auth/login";} @GetMapping("/403") String forbidden(){return "error/403";} }
