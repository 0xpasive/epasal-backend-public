package epasal.com.inventoryservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InventoryOperation {
    private String productId;
    private int quantity;
}
