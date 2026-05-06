package epasal.com.orderservice.dto.requests;

import epasal.com.orderservice.Entity.Order;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {


    @NotNull
    private List<OrderItemsRequest> items;

    @NotEmpty
    private String addressId;

    @NotNull
    private Order.PaymentMethod paymentMethod;

}
