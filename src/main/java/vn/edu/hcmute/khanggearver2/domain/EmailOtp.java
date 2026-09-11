package vn.edu.hcmute.khanggearver2.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_otps", indexes = @Index(name = "IX_email_otps_user_purpose", columnList = "user_id,purpose"))
public class EmailOtp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(nullable = false, length = 254) private String email;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private OtpPurpose purpose;
    @Column(name = "code_hash", nullable = false, length = 100) private String codeHash;
    @Column(name = "expires_at", nullable = false) private LocalDateTime expiresAt;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @Column(name = "consumed_at") private LocalDateTime consumedAt;
    @Column(name = "attempt_count", nullable = false) private int attemptCount;
    @Column(name = "last_sent_at", nullable = false) private LocalDateTime lastSentAt;
    public Long getId() { return id; }
    public User getUser() { return user; } public void setUser(User user) { this.user = user; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public OtpPurpose getPurpose() { return purpose; } public void setPurpose(OtpPurpose purpose) { this.purpose = purpose; }
    public String getCodeHash() { return codeHash; } public void setCodeHash(String codeHash) { this.codeHash = codeHash; }
    public LocalDateTime getExpiresAt() { return expiresAt; } public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getConsumedAt() { return consumedAt; } public void setConsumedAt(LocalDateTime consumedAt) { this.consumedAt = consumedAt; }
    public int getAttemptCount() { return attemptCount; } public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }
    public LocalDateTime getLastSentAt() { return lastSentAt; } public void setLastSentAt(LocalDateTime lastSentAt) { this.lastSentAt = lastSentAt; }
}
