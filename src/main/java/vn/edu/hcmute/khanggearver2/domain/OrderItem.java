package vn.edu.hcmute.khanggearver2.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) private Order order;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @Column(nullable = false, length = 200) private String productName;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal unitPrice;
    @Column(nullable = false) private Integer quantity;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal lineTotal;
    public Long getId(){return id;} public void setId(Long value){id=value;}
    public Order getOrder(){return order;} public void setOrder(Order value){order=value;}
    public Product getProduct(){return product;} public void setProduct(Product value){product=value;}
    public String getProductName(){return productName;} public void setProductName(String value){productName=value;}
    public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal value){unitPrice=value;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer value){quantity=value;}
    public BigDecimal getLineTotal(){return lineTotal;} public void setLineTotal(BigDecimal value){lineTotal=value;}
}
