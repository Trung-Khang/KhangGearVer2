package vn.edu.hcmute.khanggearver2.service.impl;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.khanggearver2.domain.OtpPurpose;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.service.OtpMailService;

@Service
@Profile("!foundation")
public class SpringOtpMailService implements OtpMailService {
    private final JavaMailSender mailSender;
    private final String fromName;
    private final String fromAddress;
    public SpringOtpMailService(JavaMailSender mailSender, @Value("${app.mail.from-name:KHANGGEAR}") String fromName,
            @Value("${spring.mail.username:}") String fromAddress) { this.mailSender = mailSender; this.fromName = fromName; this.fromAddress = fromAddress; }
    @Override public void sendOtp(User user, OtpPurpose purpose, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setFrom(new InternetAddress(fromAddress, fromName, "UTF-8"));
            String subject = purpose == OtpPurpose.VERIFY_EMAIL ? "[KHANGGEAR] Mã xác nhận tạo tài khoản" : "[KHANGGEAR] Mã xác nhận đặt lại mật khẩu";
            String action = purpose == OtpPurpose.VERIFY_EMAIL ? "xác minh tài khoản" : "đặt lại mật khẩu";
            helper.setSubject(subject);
            helper.setText("<div style='font-family:Arial,sans-serif;color:#10213b'><h2>KHANGGEAR</h2><p>Chào " + escape(user.getFullName()) + ",</p><p>Mã OTP để " + action + " của bạn là:</p><p style='font-size:30px;font-weight:bold;letter-spacing:6px;color:#1677ff'>" + code + "</p><p>Mã có hiệu lực trong 5 phút và chỉ dùng một lần.</p><p>Không chia sẻ mã này với bất kỳ ai.</p></div>", true);
            mailSender.send(message);
        } catch (Exception exception) { throw new IllegalStateException("Không thể gửi email OTP. Vui lòng thử lại sau.", exception); }
    }
    private String escape(String value) { return value == null || value.isBlank() ? "bạn" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"); }
}
