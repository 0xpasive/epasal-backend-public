package epasal.com.orderservice.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Payment {
    @Id
    private String transactionId;

    @Column
    private double amount;

    @Column
    private String status;

    @Column
    private String gateway;

    @Column
    private LocalDateTime created_at;

    @Column
    private LocalDateTime updated_at;
}
