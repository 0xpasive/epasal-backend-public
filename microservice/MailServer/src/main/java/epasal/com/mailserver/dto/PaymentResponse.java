package epasal.com.mailserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PaymentResponse {
    private String transactionId;
    private double amount;
    private String status;
    private String gateway;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
