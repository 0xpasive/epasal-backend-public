package epasal.com.inventoryservice.mapper;

import epasal.com.inventoryservice.dto.response.InventoryResponse;
import epasal.com.inventoryservice.entity.Inventory;
import org.mapstruct.Mapping;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "totalQuantity", target = "totalQuantity")
    @Mapping(source = "availableQuantity", target = "availableQuantity")
    @Mapping(source = "reservedQuantity", target = "reservedQuantity")
    InventoryResponse toResponse(Inventory inventory);
}
