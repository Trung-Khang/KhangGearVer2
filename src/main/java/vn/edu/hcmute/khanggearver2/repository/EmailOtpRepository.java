package vn.edu.hcmute.khanggearver2.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.khanggearver2.domain.EmailOtp;
import vn.edu.hcmute.khanggearver2.domain.OtpPurpose;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(Long userId, OtpPurpose purpose);
}
