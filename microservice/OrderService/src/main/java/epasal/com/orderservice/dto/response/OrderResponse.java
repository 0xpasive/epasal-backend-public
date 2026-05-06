package epasal.com.orderservice.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import epasal.com.orderservice.Entity.Order;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private String id;

    private String userId;

    private Set<OrderItemsResponse> orderItems;

    private double totalAmount;

    private String status;

    private String addressId;

    private LocalDateTime orderDate;

    private EsewaResponse esewaResponse;

    private KhaltiPaymentResponse khaltiPaymentResponse;

    private Order.PaymentMethod paymentMethod;

    private String transactionId;

    private String pidx;
}
