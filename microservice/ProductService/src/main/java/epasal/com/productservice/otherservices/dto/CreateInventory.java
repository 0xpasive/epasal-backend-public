package epasal.com.productservice.otherservices.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateInventory {
    private String productId;
    private int quantity;
}
