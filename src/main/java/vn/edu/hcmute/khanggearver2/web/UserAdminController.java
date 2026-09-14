package vn.edu.hcmute.khanggearver2.web;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.hcmute.khanggearver2.domain.Role;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.exception.BusinessRuleException;
import vn.edu.hcmute.khanggearver2.exception.DuplicateResourceException;
import vn.edu.hcmute.khanggearver2.exception.ResourceNotFoundException;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;
import vn.edu.hcmute.khanggearver2.service.UserService;

@Controller
@Profile("!foundation")
@RequestMapping("/admin/user")
public class UserAdminController {
    private static final int PAGE_SIZE = 10;
    private final UserService users;
    private final UserRepository userRepository;

    public UserAdminController(UserService users, UserRepository userRepository) {
        this.users = users;
        this.userRepository = userRepository;
    }

    @GetMapping({"", "/list"})
    String list(@RequestParam(defaultValue = "") String keyword,
                @RequestParam(defaultValue = "0") int page, Model model) {
        int safePage = Math.max(0, page);
        model.addAttribute("users", users.search(keyword, PageRequest.of(safePage, PAGE_SIZE, Sort.by("username").ascending())));
        model.addAttribute("keyword", keyword == null ? "" : keyword.trim());
        return "admin/user/list";
    }

    @GetMapping("/add")
    String add(Model model) {
        if (!model.containsAttribute("form")) model.addAttribute("form", defaultUser());
        model.addAttribute("roles", Role.values());
        return "admin/user/add";
    }

    @PostMapping("/add")
    String create(@RequestParam(required = false) String username,
                  @RequestParam(required = false) String email,
                  @RequestParam(required = false) String fullName,
                  @RequestParam(required = false) String phone,
                  @RequestParam(required = false) String role,
                  @RequestParam(required = false) String password,
                  @RequestParam(required = false) String confirmPassword,
                  @RequestParam(required = false) Boolean active,
                  @RequestParam(required = false) Boolean emailVerified,
                  Model model, RedirectAttributes flash) {
        User form = form(username, email, fullName, phone, role, password, active, emailVerified);
        Map<String, String> errors = validate(form, password, confirmPassword, true);
        if (!errors.isEmpty()) return addError(model, form, errors);
        try {
            users.create(form);
            flash.addFlashAttribute("message", "Đã thêm người dùng.");
            return "redirect:/admin/user/list";
        } catch (RuntimeException exception) {
            return addError(model, form, exceptionErrors(exception));
        }
    }

    @GetMapping("/edit")
    String edit(@RequestParam Long id, Model model) {
        model.addAttribute("form", users.getById(id));
        model.addAttribute("roles", Role.values());
        return "admin/user/edit";
    }

    @PostMapping("/edit")
    String update(@RequestParam Long id,
                  @RequestParam(required = false) String username,
                  @RequestParam(required = false) String email,
                  @RequestParam(required = false) String fullName,
                  @RequestParam(required = false) String phone,
                  @RequestParam(required = false) String role,
                  @RequestParam(required = false) String password,
                  @RequestParam(required = false) String confirmPassword,
                  @RequestParam(required = false) Boolean active,
                  @RequestParam(required = false) Boolean emailVerified,
                  Principal principal, Model model, RedirectAttributes flash) {
        User form = form(username, email, fullName, phone, role, password, active, emailVerified);
        form.setId(id);
        Map<String, String> errors = validate(form, password, confirmPassword, false);
        if (!errors.isEmpty()) return editError(model, form, errors);
        try {
            users.update(id, form, actorId(principal));
            flash.addFlashAttribute("message", "Đã cập nhật người dùng.");
            return "redirect:/admin/user/list";
        } catch (RuntimeException exception) {
            return editError(model, form, exceptionErrors(exception));
        }
    }

    @PostMapping("/toggle-status")
    String toggleStatus(@RequestParam Long id, @RequestParam boolean active,
                        Principal principal, RedirectAttributes flash) {
        try {
            users.changeActive(id, active, actorId(principal));
            flash.addFlashAttribute("message", active ? "Đã mở khóa tài khoản." : "Đã khóa tài khoản.");
        } catch (RuntimeException exception) {
            flash.addFlashAttribute("error", message(exception, "Không thể đổi trạng thái tài khoản."));
        }
        return "redirect:/admin/user/list";
    }

    @PostMapping("/delete")
    String delete(@RequestParam Long id, Principal principal, RedirectAttributes flash) {
        try {
            users.delete(id, actorId(principal));
            flash.addFlashAttribute("message", "Đã xóa người dùng.");
        } catch (RuntimeException exception) {
            flash.addFlashAttribute("error", message(exception, "Không thể xóa người dùng."));
        }
        return "redirect:/admin/user/list";
    }

    private Long actorId(Principal principal) {
        return userRepository.findByUsernameIgnoreCase(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản đang đăng nhập.")).getId();
    }

    private User defaultUser() {
        User user = new User();
        user.setActive(true);
        user.setEmailVerified(true);
        user.setRole(Role.CUSTOMER);
        return user;
    }

    private User form(String username, String email, String fullName, String phone, String role,
                      String password, Boolean active, Boolean emailVerified) {
        User user = defaultUser();
        user.setUsername(trim(username));
        user.setEmail(trim(email));
        user.setFullName(trim(fullName));
        user.setPhone(trim(phone));
        user.setPassword(password);
        user.setActive(Boolean.TRUE.equals(active));
        user.setEmailVerified(Boolean.TRUE.equals(emailVerified));
        try { user.setRole(Role.valueOf(trim(role).toUpperCase(Locale.ROOT))); }
        catch (RuntimeException exception) { user.setRole(null); }
        return user;
    }

    private Map<String, String> validate(User form, String password, String confirmPassword, boolean creating) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (form.getUsername() == null || !form.getUsername().matches("[A-Za-z0-9._-]{3,50}")) errors.put("username", "Username gồm 3-50 ký tự chữ, số, chấm, gạch dưới hoặc gạch ngang.");
        if (form.getEmail() == null || form.getEmail().length() > 254 || !form.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) errors.put("email", "Email không hợp lệ.");
        if (form.getFullName() != null && form.getFullName().length() > 150) errors.put("fullName", "Họ tên không được quá 150 ký tự.");
        if (form.getPhone() != null && !form.getPhone().matches("[0-9+() .-]{8,20}")) errors.put("phone", "Số điện thoại không hợp lệ.");
        if (form.getRole() == null) errors.put("role", "Role không hợp lệ.");
        boolean passwordProvided = password != null && !password.isBlank();
        if (creating && !passwordProvided) errors.put("password", "Mật khẩu là bắt buộc.");
        if (passwordProvided && (password.length() < 8 || password.length() > 100)) errors.put("password", "Mật khẩu phải có từ 8 đến 100 ký tự.");
        if ((creating || passwordProvided) && (confirmPassword == null || !password.equals(confirmPassword))) errors.put("confirmPassword", "Xác nhận mật khẩu chưa khớp.");
        return errors;
    }

    private String addError(Model model, User form, Map<String, String> errors) {
        model.addAttribute("form", form); model.addAttribute("fieldErrors", errors); model.addAttribute("roles", Role.values());
        return "admin/user/add";
    }

    private String editError(Model model, User form, Map<String, String> errors) {
        model.addAttribute("form", form); model.addAttribute("fieldErrors", errors); model.addAttribute("roles", Role.values());
        return "admin/user/edit";
    }

    private Map<String, String> exceptionErrors(RuntimeException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (exception instanceof DuplicateResourceException) {
            String message = exception.getMessage() == null ? "Dữ liệu đã tồn tại." : exception.getMessage();
            errors.put(message.toLowerCase(Locale.ROOT).contains("email") ? "email" : "username", message);
        } else errors.put("form", message(exception, "Không thể lưu người dùng."));
        return errors;
    }

    private String message(RuntimeException exception, String fallback) {
        if (exception instanceof BusinessRuleException || exception instanceof ResourceNotFoundException || exception instanceof DataIntegrityViolationException) return exception.getMessage() == null ? fallback : exception.getMessage();
        return fallback;
    }

    private String trim(String value) { return value == null ? null : value.trim(); }
}
