package epasal.com.orderservice.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
@Builder
public class Order {
    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<OrderItems> orderItems = new HashSet<>();

    @Column
    private double totalAmount;

    @Column
    private String status;

    @Column
    private String addressId;

    @Column
    private LocalDateTime orderDate;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column
    private String transactionId;

    @Column
    private String pidx;

    public void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public void addOrderItem(OrderItems item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public enum PaymentMethod {
        ESEWA,
        KHALTI
    }


}
