package epasal.com.inventoryservice.controller;

import epasal.com.inventoryservice.dto.request.CreateInventory;
import epasal.com.inventoryservice.dto.request.InventoryOperation;
import epasal.com.inventoryservice.dto.response.ApiResponse;
import epasal.com.inventoryservice.dto.response.InventoryResponse;
import epasal.com.inventoryservice.dto.response.PageResponse;
import epasal.com.inventoryservice.services.InventoryService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static epasal.com.inventoryservice.constants.ApiConstants.*;

@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
class InventoryController {

    private final InventoryService inventoryService;

    @Hidden
    @PostMapping(CREATE)
    public ApiResponse<InventoryResponse> createInventory(@RequestBody CreateInventory createInventory) {
        InventoryResponse response = inventoryService.createInventory(createInventory);
        return ApiResponse.success("Inventory created successfully", response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(SET_QUANTITY)
    public ApiResponse<InventoryResponse> setTotalQuantity(@RequestBody InventoryOperation inventoryOperation) {
        InventoryResponse response = inventoryService.setTotalQuantity(inventoryOperation);
        return ApiResponse.success("Total quantity set successfully", response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(GET_ALL)
    public ApiResponse<PageResponse> getAllInventories(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PageResponse responses = inventoryService.getAllInventories(page, size);
        return ApiResponse.success("Inventories fetched successfully", responses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(GET_BY_PRODUCT_ID)
    public ApiResponse<InventoryResponse> getInventoryByProductId(@PathVariable String productId) {
        InventoryResponse response = inventoryService.getInventoryByProductId(productId);
        return ApiResponse.success("Inventory fetched successfully", response);
    }


    //Service to Service
    //Order Service to Inventory Service
    @Hidden
    @PostMapping(RESERVE)
    public ApiResponse<InventoryResponse> reserveQuantity(@RequestBody InventoryOperation inventoryOperation) {
        InventoryResponse response = inventoryService.reserveQuantity(inventoryOperation);
        return ApiResponse.success("Quantity reserved successfully", response);
    }

    @Hidden
    @PostMapping(RELEASE)
    public ApiResponse<InventoryResponse> releaseReservedQuantity(@RequestBody InventoryOperation inventoryOperation) {
        InventoryResponse response = inventoryService.releaseReservedQuantity(inventoryOperation);
        return ApiResponse.success("Reserved quantity released successfully", response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(DELETE)
    public ApiResponse<Void> deleteInventory(@PathVariable String productId) {
        inventoryService.deleteInventory(productId);
        return ApiResponse.success("Inventory deleted successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(RESET)
    public ApiResponse<Void> resetInventory(@RequestParam String productId) {
        inventoryService.resetInventory(productId);
        return ApiResponse.success("Inventory reset successfully", null);
    }


}
