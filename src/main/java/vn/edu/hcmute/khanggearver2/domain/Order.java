package vn.edu.hcmute.khanggearver2.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User customer;
    @Column(nullable = false, length = 150) private String receiverName;
    @Column(nullable = false, length = 20) private String phone;
    @Column(nullable = false, length = 254) private String email;
    @Column(nullable = false, length = 500) private String shippingAddress;
    @Column(length = 1000) private String note;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private PaymentMethod paymentMethod = PaymentMethod.COD;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private PaymentStatus paymentStatus = PaymentStatus.UNPAID;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private OrderStatus orderStatus = OrderStatus.PENDING;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal totalAmount = BigDecimal.ZERO;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) @OrderBy("id ASC") private List<OrderItem> items = new ArrayList<>();
    @PrePersist void createDates(){LocalDateTime now=LocalDateTime.now();createdAt=now;updatedAt=now;}
    @PreUpdate void updateDate(){updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public void setId(Long value){id=value;}
    public User getCustomer(){return customer;} public void setCustomer(User value){customer=value;}
    public String getReceiverName(){return receiverName;} public void setReceiverName(String value){receiverName=value;}
    public String getPhone(){return phone;} public void setPhone(String value){phone=value;}
    public String getEmail(){return email;} public void setEmail(String value){email=value;}
    public String getShippingAddress(){return shippingAddress;} public void setShippingAddress(String value){shippingAddress=value;}
    public String getNote(){return note;} public void setNote(String value){note=value;}
    public PaymentMethod getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(PaymentMethod value){paymentMethod=value;}
    public PaymentStatus getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(PaymentStatus value){paymentStatus=value;}
    public OrderStatus getOrderStatus(){return orderStatus;} public void setOrderStatus(OrderStatus value){orderStatus=value;}
    public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal value){totalAmount=value;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
    public List<OrderItem> getItems(){return items;} public void setItems(List<OrderItem> value){items=value;}
}
