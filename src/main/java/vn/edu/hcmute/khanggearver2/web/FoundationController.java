package vn.edu.hcmute.khanggearver2.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FoundationController {
    @GetMapping("/")
    String index(Model model) {
        model.addAttribute("applicationName", "KhangGearVer2");
        return "index";
    }
}
