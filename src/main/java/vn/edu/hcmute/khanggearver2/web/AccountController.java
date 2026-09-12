package vn.edu.hcmute.khanggearver2.web;

import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.hcmute.khanggearver2.domain.OtpPurpose;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.service.AccountOtpService;
import vn.edu.hcmute.khanggearver2.service.AccountService;
import vn.edu.hcmute.khanggearver2.service.OtpChallengeResult;
import vn.edu.hcmute.khanggearver2.service.OtpVerificationResult;

@Controller
@Profile("!foundation")
public class AccountController {
    private static final String VERIFY_USER_ID = "verifyUserId";
    private static final String RESET_REQUESTED = "resetRequested";
    private static final String RESET_USER_ID = "resetUserId";
    private static final String RESET_OTP_ID = "resetOtpId";
    private static final String RESET_VERIFIED_UNTIL = "resetVerifiedUntil";
    private final AccountService accounts;
    private final AccountOtpService otps;

    public AccountController(AccountService accounts, AccountOtpService otps) {
        this.accounts = accounts;
        this.otps = otps;
    }

    @GetMapping("/register")
    String register() { return "auth/register"; }

    @PostMapping("/register")
    String register(@RequestParam(required = false) String username, @RequestParam(required = false) String email,
            @RequestParam(required = false) String password, @RequestParam(required = false) String confirmPassword,
            @RequestParam(required = false) String fullName, @RequestParam(required = false) String phone,
            HttpSession session, Model model, RedirectAttributes flash) {
        Map<String, String> errors = registrationErrors(username, email, password, confirmPassword, phone);
        if (!errors.isEmpty()) return registrationForm(model, errors, username, email, fullName, phone);
        User existing = accounts.findByEmail(email);
        if (existing != null && !Boolean.TRUE.equals(existing.getEmailVerified())) {
            session.setAttribute(VERIFY_USER_ID, existing.getId());
            issueVerification(existing, flash);
            return "redirect:/verify-email";
        }
        try {
            User user = accounts.registerCustomer(username, email, password, fullName, phone);
            session.setAttribute(VERIFY_USER_ID, user.getId());
            issueVerification(user, flash);
            return "redirect:/verify-email";
        } catch (RuntimeException exception) {
            errors.put("general", safeMessage(exception, "Không thể tạo tài khoản. Vui lòng kiểm tra lại dữ liệu."));
            return registrationForm(model, errors, username, email, fullName, phone);
        }
    }

    @GetMapping("/verify-email")
    String verifyEmail(HttpSession session) {
        return session.getAttribute(VERIFY_USER_ID) == null ? "redirect:/login" : "auth/verify-email";
    }

    @PostMapping("/verify-email")
    String verifyEmail(@RequestParam(required = false) String otp, HttpSession session, Model model,
            RedirectAttributes flash) {
        Long userId = sessionId(session, VERIFY_USER_ID);
        if (userId == null) return "redirect:/login";
        if (!validOtp(otp)) {
            model.addAttribute("fieldErrors", Map.of("otp", "OTP phải gồm 6 chữ số."));
            return "auth/verify-email";
        }
        User user = accounts.get(userId);
        OtpVerificationResult result = otps.verify(user, OtpPurpose.VERIFY_EMAIL, otp);
        if (result == OtpVerificationResult.VERIFIED) {
            accounts.verifyEmail(userId);
            session.removeAttribute(VERIFY_USER_ID);
            flash.addFlashAttribute("message", "Xác minh tài khoản thành công. Bạn có thể đăng nhập.");
            return "redirect:/login";
        }
        model.addAttribute("error", otpMessage(result));
        return "auth/verify-email";
    }

    @PostMapping("/verify-email/resend")
    String resendVerify(HttpSession session, RedirectAttributes flash) {
        Long userId = sessionId(session, VERIFY_USER_ID);
        if (userId == null) return "redirect:/login";
        issueVerification(accounts.get(userId), flash);
        return "redirect:/verify-email";
    }

    @GetMapping("/forgot-password")
    String forgotPassword() { return "auth/forgot-password"; }

    @PostMapping("/forgot-password")
    String forgotPassword(@RequestParam(required = false) String email, HttpSession session, Model model,
            RedirectAttributes flash) {
        if (!validEmail(email)) {
            model.addAttribute("fieldErrors", Map.of("email", "Email không hợp lệ."));
            model.addAttribute("email", safe(email));
            return "auth/forgot-password";
        }
        clearReset(session);
        session.setAttribute(RESET_REQUESTED, Boolean.TRUE);
        User user = accounts.findByEmail(email);
        if (user != null && Boolean.TRUE.equals(user.getActive())) {
            session.setAttribute(RESET_USER_ID, user.getId());
            try { otps.issue(user, OtpPurpose.RESET_PASSWORD); } catch (RuntimeException ignored) {
                // Keep the response identical for known and unknown email addresses.
            }
        }
        flash.addFlashAttribute("message", "Nếu email tồn tại trong hệ thống, mã OTP đã được gửi.");
        return "redirect:/reset-password/verify";
    }

    @GetMapping("/reset-password/verify")
    String verifyReset(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(RESET_REQUESTED))
                ? "auth/reset-password-verify" : "redirect:/forgot-password";
    }

    @PostMapping("/reset-password/verify")
    String verifyReset(@RequestParam(required = false) String otp, HttpSession session, Model model) {
        if (!Boolean.TRUE.equals(session.getAttribute(RESET_REQUESTED))) return "redirect:/forgot-password";
        Long userId = sessionId(session, RESET_USER_ID);
        if (userId == null || !validOtp(otp)) {
            model.addAttribute("error", "Mã OTP không đúng hoặc đã hết hạn.");
            return "auth/reset-password-verify";
        }
        OtpChallengeResult challenge = otps.prepareReset(accounts.get(userId), otp);
        if (challenge.result() != OtpVerificationResult.VERIFIED) {
            model.addAttribute("error", otpMessage(challenge.result()));
            return "auth/reset-password-verify";
        }
        session.setAttribute(RESET_OTP_ID, challenge.otpId());
        session.setAttribute(RESET_VERIFIED_UNTIL, Instant.now().plusSeconds(600).toEpochMilli());
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    String resetPassword(HttpSession session) {
        return resetIsVerified(session) ? "auth/reset-password" : "redirect:/forgot-password";
    }

    @PostMapping("/reset-password")
    String resetPassword(@RequestParam(required = false) String password,
            @RequestParam(required = false) String confirmPassword, HttpSession session, Model model,
            RedirectAttributes flash) {
        if (!resetIsVerified(session)) return "redirect:/forgot-password";
        Map<String, String> errors = passwordErrors(password, confirmPassword);
        if (!errors.isEmpty()) {
            model.addAttribute("fieldErrors", errors);
            return "auth/reset-password";
        }
        try {
            otps.completePasswordReset(sessionId(session, RESET_USER_ID), sessionId(session, RESET_OTP_ID), password);
            clearReset(session);
            flash.addFlashAttribute("message", "Đặt lại mật khẩu thành công. Bạn có thể đăng nhập.");
            return "redirect:/login";
        } catch (RuntimeException exception) {
            model.addAttribute("error", "Phiên đặt lại mật khẩu đã hết hạn. Vui lòng thực hiện lại.");
            clearReset(session);
            return "auth/reset-password";
        }
    }

    private String registrationForm(Model model, Map<String, String> errors, String username, String email,
            String fullName, String phone) {
        model.addAttribute("fieldErrors", errors);
        Map<String, String> form = new LinkedHashMap<>();
        form.put("username", safe(username)); form.put("email", safe(email));
        form.put("fullName", safe(fullName)); form.put("phone", safe(phone));
        model.addAttribute("form", form);
        return "auth/register";
    }

    private void issueVerification(User user, RedirectAttributes flash) {
        try {
            otps.issue(user, OtpPurpose.VERIFY_EMAIL);
            flash.addFlashAttribute("message", "Mã xác nhận đã được gửi đến email của bạn.");
        } catch (RuntimeException exception) {
            flash.addFlashAttribute("error", "Tài khoản đã được tạo nhưng chưa thể gửi email xác nhận. Vui lòng thử gửi lại OTP sau.");
        }
    }

    private Map<String, String> registrationErrors(String username, String email, String password,
            String confirmation, String phone) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (username == null || !username.trim().matches("[A-Za-z0-9_.-]{3,50}"))
            errors.put("username", "Tên đăng nhập từ 3-50 ký tự, chỉ gồm chữ, số, dấu . _ -.");
        if (!validEmail(email)) errors.put("email", "Email không hợp lệ.");
        errors.putAll(passwordErrors(password, confirmation));
        if (phone != null && !phone.isBlank() && !phone.trim().matches("[0-9+() .-]{8,20}"))
            errors.put("phone", "Số điện thoại không hợp lệ.");
        return errors;
    }

    private Map<String, String> passwordErrors(String password, String confirmation) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (password == null || password.length() < 8) errors.put("password", "Mật khẩu cần ít nhất 8 ký tự.");
        if (confirmation == null || !confirmation.equals(password))
            errors.put("confirmPassword", "Xác nhận mật khẩu không khớp.");
        return errors;
    }

    private boolean validEmail(String value) {
        return value != null && value.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
    private boolean validOtp(String value) { return value != null && value.matches("\\d{6}"); }
    private Long sessionId(HttpSession session, String name) {
        Object value = session.getAttribute(name);
        return value instanceof Long id ? id : null;
    }
    private boolean resetIsVerified(HttpSession session) {
        Object until = session.getAttribute(RESET_VERIFIED_UNTIL);
        return sessionId(session, RESET_USER_ID) != null && sessionId(session, RESET_OTP_ID) != null
                && until instanceof Long value && value > Instant.now().toEpochMilli();
    }
    private void clearReset(HttpSession session) {
        session.removeAttribute(RESET_REQUESTED); session.removeAttribute(RESET_USER_ID);
        session.removeAttribute(RESET_OTP_ID); session.removeAttribute(RESET_VERIFIED_UNTIL);
    }
    private String otpMessage(OtpVerificationResult result) {
        return switch (result) {
            case EXPIRED -> "Mã OTP đã hết hạn. Vui lòng gửi lại mã mới.";
            case TOO_MANY_ATTEMPTS -> "Bạn đã nhập sai quá nhiều lần. Vui lòng gửi lại mã mới.";
            default -> "Mã OTP không đúng hoặc đã được sử dụng.";
        };
    }
    private String safe(String value) { return value == null ? "" : value.trim(); }
    private String safeMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }
}
