package vn.edu.hcmute.khanggearver2.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(name = "UQ_users_username", columnNames = "username"), @UniqueConstraint(name = "UQ_users_email", columnNames = "email")})
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank @Size(min = 3, max = 50) @Column(nullable = false, length = 50) private String username;
    @NotBlank @Size(max = 100) @Column(nullable = false, length = 100) private String password;
    @Size(max = 150) @Column(length = 150) private String fullName;
    @NotBlank @Email @Size(max = 254) @Column(nullable = false, length = 254) private String email;
    @Pattern(regexp = "^$|^[0-9+() .-]{8,20}$", message = "So dien thoai khong hop le") @Column(length = 20) private String phone;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Role role = Role.CUSTOMER;
    @Column(nullable = false) private Boolean active = true;
    @Column(nullable = false) private Boolean emailVerified = false;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;
    @PrePersist void onCreate() { LocalDateTime now = LocalDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
    public Long getId(){return id;} public void setId(Long id){this.id=id;} public String getUsername(){return username;} public void setUsername(String username){this.username=username;} public String getPassword(){return password;} public void setPassword(String password){this.password=password;} public String getFullName(){return fullName;} public void setFullName(String fullName){this.fullName=fullName;} public String getEmail(){return email;} public void setEmail(String email){this.email=email;} public String getPhone(){return phone;} public void setPhone(String phone){this.phone=phone;} public Role getRole(){return role;} public void setRole(Role role){this.role=role;} public Boolean getActive(){return active;} public void setActive(Boolean active){this.active=active;} public Boolean getEmailVerified(){return emailVerified;} public void setEmailVerified(Boolean emailVerified){this.emailVerified=emailVerified;} public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
