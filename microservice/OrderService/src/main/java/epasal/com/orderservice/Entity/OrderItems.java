package epasal.com.orderservice.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
@Builder
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column
    private String productId;

    @Column
    private String productName;

    @Column
    private int quantity;

    @Column
    private double price;

}
