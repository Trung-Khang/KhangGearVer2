package vn.edu.hcmute.khanggearver2.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank @Size(max = 200) @Column(nullable = false, length = 200) private String name;
    @Size(max = 2000) @Column(length = 2000) private String description;
    @DecimalMin(value = "0.00") @Column(nullable = false, precision = 19, scale = 2) private BigDecimal price = BigDecimal.ZERO;
    @PositiveOrZero @Column(nullable = false) private Integer stock = 0;
    @Column(length = 255) private String image;
    @Column(nullable = false) private Boolean active = true;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "category_id", nullable = false) private Category category;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;
    @PrePersist void createDates() { LocalDateTime now = LocalDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void updateDate() { updatedAt = LocalDateTime.now(); }
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String value){name=value;}
    public String getDescription(){return description;} public void setDescription(String value){description=value;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal value){price=value;}
    public Integer getStock(){return stock;} public void setStock(Integer value){stock=value;}
    public String getImage(){return image;} public void setImage(String value){image=value;}
    public Boolean getActive(){return active;} public void setActive(Boolean value){active=value;}
    public Category getCategory(){return category;} public void setCategory(Category value){category=value;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
