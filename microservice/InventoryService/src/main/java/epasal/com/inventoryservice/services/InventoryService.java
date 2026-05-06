package epasal.com.inventoryservice.services;

import epasal.com.inventoryservice.dto.request.CreateInventory;
import epasal.com.inventoryservice.dto.request.InventoryOperation;
import epasal.com.inventoryservice.dto.response.InventoryResponse;
import epasal.com.inventoryservice.dto.response.PageResponse;

public interface InventoryService {
    InventoryResponse createInventory(CreateInventory createInventory);

    InventoryResponse getInventoryByProductId(String productId);

    InventoryResponse setTotalQuantity(InventoryOperation inventoryOperation);

    InventoryResponse reserveQuantity(InventoryOperation inventoryOperation);

    InventoryResponse releaseReservedQuantity(InventoryOperation inventoryOperation);

    PageResponse getAllInventories(int page, int size);

    void deleteInventory(String productId);

    void resetInventory(String productId);
}
