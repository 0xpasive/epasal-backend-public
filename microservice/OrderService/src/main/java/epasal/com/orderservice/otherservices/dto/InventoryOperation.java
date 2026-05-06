package epasal.com.orderservice.otherservices.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class InventoryOperation {
    private String productId;
    private int quantity;
}
