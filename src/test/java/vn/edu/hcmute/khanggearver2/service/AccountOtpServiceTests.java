package vn.edu.hcmute.khanggearver2.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.edu.hcmute.khanggearver2.domain.EmailOtp;
import vn.edu.hcmute.khanggearver2.domain.OtpPurpose;
import vn.edu.hcmute.khanggearver2.domain.User;
import vn.edu.hcmute.khanggearver2.exception.BusinessRuleException;
import vn.edu.hcmute.khanggearver2.repository.EmailOtpRepository;
import vn.edu.hcmute.khanggearver2.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AccountOtpServiceTests {
    @Mock private EmailOtpRepository repository;
    @Mock private UserRepository users;
    @Mock private OtpMailService mail;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Test void issuesHashedOtpAndSendsMail() {
        User user = user(); when(repository.findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(1L, OtpPurpose.VERIFY_EMAIL)).thenReturn(Optional.empty()); when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        new AccountOtpService(repository, encoder, mail).issue(user, OtpPurpose.VERIFY_EMAIL);
        ArgumentCaptor<EmailOtp> captor = ArgumentCaptor.forClass(EmailOtp.class); verify(repository).save(captor.capture()); verify(mail).sendOtp(eq(user), eq(OtpPurpose.VERIFY_EMAIL), anyString());
        assertThat(captor.getValue().getCodeHash()).doesNotMatch("\\d{6}"); assertThat(captor.getValue().getExpiresAt()).isAfter(captor.getValue().getCreatedAt());
    }
    @Test void blocksResendInsideCooldown() {
        EmailOtp existing = otp("123456"); existing.setLastSentAt(LocalDateTime.now()); when(repository.findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(1L, OtpPurpose.VERIFY_EMAIL)).thenReturn(Optional.of(existing));
        assertThatThrownBy(() -> new AccountOtpService(repository, encoder, mail).issue(user(), OtpPurpose.VERIFY_EMAIL)).isInstanceOf(BusinessRuleException.class); verifyNoInteractions(mail);
    }
    @Test void verifiesOnceAndRejectsExpiredAndTooManyAttempts() {
        EmailOtp valid = otp("123456"); when(repository.findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(1L, OtpPurpose.RESET_PASSWORD)).thenReturn(Optional.of(valid)); when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        assertThat(new AccountOtpService(repository, encoder, mail).verify(user(), OtpPurpose.RESET_PASSWORD, "123456")).isEqualTo(OtpVerificationResult.VERIFIED); assertThat(valid.getConsumedAt()).isNotNull();
        EmailOtp expired = otp("123456"); expired.setExpiresAt(LocalDateTime.now().minusSeconds(1)); when(repository.findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(1L, OtpPurpose.RESET_PASSWORD)).thenReturn(Optional.of(expired));
        assertThat(new AccountOtpService(repository, encoder, mail).verify(user(), OtpPurpose.RESET_PASSWORD, "123456")).isEqualTo(OtpVerificationResult.EXPIRED);
        EmailOtp exhausted = otp("123456"); exhausted.setAttemptCount(5); when(repository.findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(1L, OtpPurpose.RESET_PASSWORD)).thenReturn(Optional.of(exhausted));
        assertThat(new AccountOtpService(repository, encoder, mail).verify(user(), OtpPurpose.RESET_PASSWORD, "123456")).isEqualTo(OtpVerificationResult.TOO_MANY_ATTEMPTS);
    }
    @Test void resetChallengeKeepsOtpUntilPasswordUpdateCommits() {
        User user = user();
        EmailOtp pending = otp("123456");
        pending.setId(7L);
        when(repository.findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(1L, OtpPurpose.RESET_PASSWORD)).thenReturn(Optional.of(pending));
        when(repository.findById(7L)).thenReturn(Optional.of(pending));
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(users.save(any())).thenAnswer(i -> i.getArgument(0));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        AccountOtpService service = new AccountOtpService(repository, encoder, mail, users);

        OtpChallengeResult challenge = service.prepareReset(user, "123456");
        assertThat(challenge.result()).isEqualTo(OtpVerificationResult.VERIFIED);
        assertThat(challenge.otpId()).isEqualTo(7L);
        assertThat(pending.getConsumedAt()).isNull();

        service.completePasswordReset(1L, challenge.otpId(), "new-password");
        assertThat(pending.getConsumedAt()).isNotNull();
        assertThat(encoder.matches("new-password", user.getPassword())).isTrue();
    }
    private User user() { User user = new User(); user.setId(1L); user.setEmail("user@test.local"); return user; }
    private EmailOtp otp(String code) { EmailOtp otp = new EmailOtp(); otp.setUser(user()); otp.setPurpose(OtpPurpose.RESET_PASSWORD); otp.setCodeHash(encoder.encode(code)); otp.setCreatedAt(LocalDateTime.now()); otp.setLastSentAt(LocalDateTime.now().minusMinutes(2)); otp.setExpiresAt(LocalDateTime.now().plusMinutes(5)); return otp; }
}
