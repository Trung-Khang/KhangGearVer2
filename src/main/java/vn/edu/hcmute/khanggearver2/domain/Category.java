package vn.edu.hcmute.khanggearver2.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(name = "UQ_categories_name", columnNames = "name"))
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank(message = "Ten danh muc la bat buoc") @Size(max = 150) @Column(nullable = false, length = 150) private String name;
    @Size(max = 1000) @Column(length = 1000) private String description;
    @Column(nullable = false) private Boolean active = true;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;
    @PrePersist void onCreate() { LocalDateTime now = LocalDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public Boolean getActive() { return active; } public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; } public LocalDateTime getUpdatedAt() { return updatedAt; }
}
