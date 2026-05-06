package epasal.com.inventoryservice.services.Impl;

import epasal.com.inventoryservice.dto.request.CreateInventory;
import epasal.com.inventoryservice.dto.request.InventoryOperation;
import epasal.com.inventoryservice.dto.response.InventoryResponse;
import epasal.com.inventoryservice.dto.response.PageResponse;
import epasal.com.inventoryservice.entity.Inventory;
import epasal.com.inventoryservice.exception.InventoryAlreadyExistsException;
import epasal.com.inventoryservice.exception.InventoryNotFoundException;
import epasal.com.inventoryservice.exception.StockNotAvailableException;
import epasal.com.inventoryservice.mapper.Mapper;
import epasal.com.inventoryservice.repo.InventoryRepository;
import epasal.com.inventoryservice.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final Mapper mapper;

    @Override
    public InventoryResponse createInventory(CreateInventory createInventory) {
        Inventory inventory = inventoryRepository.findByProductId(createInventory.getProductId());
        if (inventory != null) {
            throw new InventoryAlreadyExistsException("Inventory already exists for productId: " + createInventory.getProductId());
        }
        Inventory newInventory = Inventory.builder()
                .productId(createInventory.getProductId())
                .totalQuantity(createInventory.getQuantity())
                .availableQuantity(0)
                .reservedQuantity(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Inventory savedInventory = inventoryRepository.save(newInventory);
        return mapper.toResponse(savedInventory);
    }

    @Override
    public InventoryResponse getInventoryByProductId(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if (inventory == null) {
            throw new InventoryNotFoundException("Inventory not found for productId: " + productId);
        }
        return mapper.toResponse(inventory);
    }

    @Override
    public InventoryResponse setTotalQuantity(InventoryOperation inventoryOperation) {
        Inventory inventory = inventoryRepository.findByProductId(inventoryOperation.getProductId());
        inventory.setTotalQuantity(inventoryOperation.getQuantity());
        inventory.calculateAvailableQuantity();
        inventory.setUpdatedAt(LocalDateTime.now());
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return mapper.toResponse(updatedInventory);
    }

    @Override
    public InventoryResponse reserveQuantity(InventoryOperation inventoryOperation) {
        Inventory inventory = inventoryRepository.findByProductId(inventoryOperation.getProductId());
        if (inventory.getAvailableQuantity() < inventoryOperation.getQuantity()) {
            throw new StockNotAvailableException("Insufficient stock to reserve");
        }
        inventory.setReservedQuantity(inventoryOperation.getQuantity());
        inventory.calculateAvailableQuantity();
        inventory.setUpdatedAt(LocalDateTime.now());
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return mapper.toResponse(updatedInventory);
    }

    @Override
    public InventoryResponse releaseReservedQuantity(InventoryOperation inventoryOperation) {
        Inventory inventory = inventoryRepository.findByProductId(inventoryOperation.getProductId());
        if (inventory.getReservedQuantity() < inventoryOperation.getQuantity()) {
            throw new StockNotAvailableException("Insufficient reserved quantity to release");
        }
        inventory.setReservedQuantity(inventory.getReservedQuantity() - inventoryOperation.getQuantity());
        inventory.calculateAvailableQuantity();
        inventory.setUpdatedAt(LocalDateTime.now());
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return mapper.toResponse(updatedInventory);
    }

    @Override
    public PageResponse getAllInventories(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Inventory> inventoryPage = inventoryRepository.findAll(pageable);
            return createPageResponse(inventoryPage);
        } catch (Exception e) {
            throw new ResourceAccessException("Failed to retrieve inventories");
        }
    }

    @Override
    public void deleteInventory(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if (inventory == null) {
            throw new InventoryNotFoundException("Inventory not found for productId: " + productId);
        }
        inventoryRepository.delete(inventory);
    }

    @Override
    public void resetInventory(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if (inventory == null) {
            throw new InventoryNotFoundException("Inventory not found for productId: " + productId);
        }
        inventory.setTotalQuantity(0);
        inventory.setAvailableQuantity(0);
        inventory.setReservedQuantity(0);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);
    }

    private PageResponse createPageResponse(Page<Inventory> productPage) {
        return new PageResponse(
                productPage.getContent()
                        .stream()
                        .map(mapper::toResponse)
                        .toList(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );

    }


}
