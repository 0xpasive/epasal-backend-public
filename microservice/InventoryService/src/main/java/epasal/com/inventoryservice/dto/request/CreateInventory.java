package epasal.com.inventoryservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInventory {
    private String productId;
    private int quantity;
}
