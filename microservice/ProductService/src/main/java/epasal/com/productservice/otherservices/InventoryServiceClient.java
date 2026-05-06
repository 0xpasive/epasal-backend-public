package epasal.com.productservice.otherservices;

import epasal.com.productservice.dto.response.ApiResponse;
import epasal.com.productservice.dto.response.InventoryResponse;
import epasal.com.productservice.otherservices.dto.CreateInventory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@FeignClient(name = "INVENTORYSERVICE")
public interface InventoryServiceClient {
    @PostMapping("/api/v1/inventory/create")
    ApiResponse<InventoryResponse> createInventory(@RequestBody CreateInventory createInventory);

    @GetMapping("/api/v1/inventory/{productId}")
    <T> ApiResponse<T> getInventoryByProductId(@PathVariable String productId);

    @DeleteMapping("/api/v1/inventory/delete/{productId}")
    ApiResponse<Void> deleteInventory(@PathVariable String productId);

}
