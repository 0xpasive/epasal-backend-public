package epasal.com.orderservice.otherservices;

import epasal.com.orderservice.dto.response.ApiResponse;
import epasal.com.orderservice.otherservices.dto.InventoryOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "INVENTORYSERVICE")
public interface InventoryServiceClient {
    @PostMapping("/api/v1/inventory/reserve")
    <T> ApiResponse<T> reserveQuantity(@RequestBody InventoryOperation inventoryOperation);

    @PostMapping("/api/v1/inventory/release")
    <T> ApiResponse<T> releaseReservedQuantity(@RequestBody InventoryOperation inventoryOperation);

}
