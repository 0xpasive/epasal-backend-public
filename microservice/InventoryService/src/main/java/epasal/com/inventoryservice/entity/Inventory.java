package epasal.com.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String productId;

    @Column
    private Integer totalQuantity;

    @Column
    private Integer availableQuantity;

    @Column
    private Integer reservedQuantity;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public void calculateAvailableQuantity() {
        this.availableQuantity = this.totalQuantity - this.reservedQuantity;
    }

}
