package epasal.com.mailserver.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String id;

    private String userId;

    private Set<OrderItemsResponse> orderItems;

    private double totalAmount;

    private String status;

    private String addressId;

    private LocalDateTime orderDate;

    private PaymentMethod paymentMethod;

    private String transactionId;

    private String pidx;

    public enum PaymentMethod {
        ESEWA,
        KHALTI
    }
}
