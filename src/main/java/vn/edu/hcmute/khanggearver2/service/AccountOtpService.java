package vn.edu.hcmute.khanggearver2.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hcmute.khanggearver2.domain.EmailOtp;
import vn.edu.hcmute.khanggearver2.domain.OtpPurpose;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.exception.BusinessRuleException;
import vn.edu.hcmute.khanggearver2.repository.EmailOtpRepository;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;

@Service
@Profile("!foundation")
public class AccountOtpService {
    private static final int MAX_ATTEMPTS = 5;
    private final EmailOtpRepository otps;
    private final PasswordEncoder passwordEncoder;
    private final OtpMailService mailService;
    private final UserRepository users;
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public AccountOtpService(EmailOtpRepository otps, PasswordEncoder passwordEncoder,
            OtpMailService mailService, UserRepository users) {
        this.otps = otps;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.users = users;
    }

    AccountOtpService(EmailOtpRepository otps, PasswordEncoder passwordEncoder, OtpMailService mailService) {
        this(otps, passwordEncoder, mailService, null);
    }

    @Transactional
    public void issue(User user, OtpPurpose purpose) {
        LocalDateTime now = LocalDateTime.now();
        otps.findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(user.getId(), purpose).ifPresent(previous -> {
            if (previous.getLastSentAt().isAfter(now.minusSeconds(60))) {
                throw new BusinessRuleException("Vui lòng chờ 60 giây trước khi gửi lại OTP.");
            }
            previous.setConsumedAt(now);
            otps.save(previous);
        });

        String code = String.format("%06d", random.nextInt(1_000_000));
        EmailOtp otp = new EmailOtp();
        otp.setUser(user);
        otp.setEmail(user.getEmail());
        otp.setPurpose(purpose);
        otp.setCodeHash(passwordEncoder.encode(code));
        otp.setCreatedAt(now);
        otp.setLastSentAt(now);
        otp.setExpiresAt(now.plusMinutes(5));
        otp.setAttemptCount(0);
        otps.save(otp);
        mailService.sendOtp(user, purpose, code);
    }

    @Transactional
    public OtpVerificationResult verify(User user, OtpPurpose purpose, String code) {
        return verifyCode(latest(user, purpose), code, true);
    }

    @Transactional
    public OtpChallengeResult prepareReset(User user, String code) {
        EmailOtp otp = latest(user, OtpPurpose.RESET_PASSWORD);
        OtpVerificationResult result = verifyCode(otp, code, false);
        return new OtpChallengeResult(result, result == OtpVerificationResult.VERIFIED ? otp.getId() : null);
    }

    @Transactional
    public void completePasswordReset(Long userId, Long otpId, String newPassword) {
        if (users == null || userId == null || otpId == null) {
            throw new BusinessRuleException("Phiên đặt lại mật khẩu không hợp lệ.");
        }
        EmailOtp otp = otps.findById(otpId)
                .orElseThrow(() -> new BusinessRuleException("OTP không còn hiệu lực."));
        LocalDateTime now = LocalDateTime.now();
        if (otp.getUser() == null || !userId.equals(otp.getUser().getId())
                || otp.getPurpose() != OtpPurpose.RESET_PASSWORD || otp.getConsumedAt() != null
                || !otp.getExpiresAt().isAfter(now) || otp.getAttemptCount() >= MAX_ATTEMPTS) {
            throw new BusinessRuleException("OTP không còn hiệu lực.");
        }
        User user = users.findById(userId)
                .orElseThrow(() -> new BusinessRuleException("Tài khoản không còn tồn tại."));
        user.setPassword(passwordEncoder.encode(newPassword));
        users.save(user);
        otp.setConsumedAt(now);
        otps.save(otp);
    }

    private EmailOtp latest(User user, OtpPurpose purpose) {
        return otps.findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(user.getId(), purpose)
                .orElse(null);
    }

    private OtpVerificationResult verifyCode(EmailOtp otp, String code, boolean consumeOnSuccess) {
        if (code == null || !code.matches("\\d{6}") || otp == null) return OtpVerificationResult.INVALID;
        LocalDateTime now = LocalDateTime.now();
        if (!otp.getExpiresAt().isAfter(now)) {
            otp.setConsumedAt(now);
            otps.save(otp);
            return OtpVerificationResult.EXPIRED;
        }
        if (otp.getAttemptCount() >= MAX_ATTEMPTS) {
            otp.setConsumedAt(now);
            otps.save(otp);
            return OtpVerificationResult.TOO_MANY_ATTEMPTS;
        }
        if (!passwordEncoder.matches(code, otp.getCodeHash())) {
            otp.setAttemptCount(otp.getAttemptCount() + 1);
            if (otp.getAttemptCount() >= MAX_ATTEMPTS) otp.setConsumedAt(now);
            otps.save(otp);
            return otp.getAttemptCount() >= MAX_ATTEMPTS
                    ? OtpVerificationResult.TOO_MANY_ATTEMPTS : OtpVerificationResult.INVALID;
        }
        if (consumeOnSuccess) {
            otp.setConsumedAt(now);
            otps.save(otp);
        }
        return OtpVerificationResult.VERIFIED;
    }
}
