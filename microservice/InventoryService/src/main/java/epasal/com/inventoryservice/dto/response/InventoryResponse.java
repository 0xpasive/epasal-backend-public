package epasal.com.inventoryservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    private UUID id;
    private String productId;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer reservedQuantity;
}
