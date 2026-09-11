package vn.edu.hcmute.khanggearver2.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hcmute.khanggearver2.domain.EmailOtp;
import vn.edu.hcmute.khanggearver2.domain.OtpPurpose;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.exception.BusinessRuleException;
import vn.edu.hcmute.khanggearver2.repository.EmailOtpRepository;

@Service
@Profile("!foundation")
public class AccountOtpService {
    private static final int MAX_ATTEMPTS = 5;
    private final EmailOtpRepository otps;
    private final PasswordEncoder passwordEncoder;
    private final OtpMailService mailService;
    private final SecureRandom random = new SecureRandom();
    public AccountOtpService(EmailOtpRepository otps, PasswordEncoder passwordEncoder, OtpMailService mailService) { this.otps = otps; this.passwordEncoder = passwordEncoder; this.mailService = mailService; }
    @Transactional public void issue(User user, OtpPurpose purpose) {
        LocalDateTime now = LocalDateTime.now();
        otps.findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(user.getId(), purpose).ifPresent(previous -> {
            if (previous.getLastSentAt().isAfter(now.minusSeconds(60))) throw new BusinessRuleException("Vui lòng chờ 60 giây trước khi gửi lại OTP.");
            previous.setConsumedAt(now);
            otps.save(previous);
        });
        String code = String.format("%06d", random.nextInt(1_000_000));
        EmailOtp otp = new EmailOtp();
        otp.setUser(user); otp.setEmail(user.getEmail()); otp.setPurpose(purpose); otp.setCodeHash(passwordEncoder.encode(code));
        otp.setCreatedAt(now); otp.setLastSentAt(now); otp.setExpiresAt(now.plusMinutes(5)); otp.setAttemptCount(0);
        otps.save(otp);
        mailService.sendOtp(user, purpose, code);
    }
    @Transactional public OtpVerificationResult verify(User user, OtpPurpose purpose, String code) {
        if (code == null || !code.matches("\\d{6}")) return OtpVerificationResult.INVALID;
        EmailOtp otp = otps.findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(user.getId(), purpose).orElse(null);
        if (otp == null) return OtpVerificationResult.INVALID;
        LocalDateTime now = LocalDateTime.now();
        if (!otp.getExpiresAt().isAfter(now)) { otp.setConsumedAt(now); otps.save(otp); return OtpVerificationResult.EXPIRED; }
        if (otp.getAttemptCount() >= MAX_ATTEMPTS) { otp.setConsumedAt(now); otps.save(otp); return OtpVerificationResult.TOO_MANY_ATTEMPTS; }
        if (!passwordEncoder.matches(code, otp.getCodeHash())) {
            otp.setAttemptCount(otp.getAttemptCount() + 1);
            if (otp.getAttemptCount() >= MAX_ATTEMPTS) otp.setConsumedAt(now);
            otps.save(otp);
            return otp.getAttemptCount() >= MAX_ATTEMPTS ? OtpVerificationResult.TOO_MANY_ATTEMPTS : OtpVerificationResult.INVALID;
        }
        otp.setConsumedAt(now); otps.save(otp);
        return OtpVerificationResult.VERIFIED;
    }
}
