package epasal.com.orderservice.scheduler;

import epasal.com.orderservice.Entity.Order;
import epasal.com.orderservice.Entity.OrderItems;
import epasal.com.orderservice.exception.ExternalServiceException;
import epasal.com.orderservice.otherservices.InventoryServiceClient;
import epasal.com.orderservice.otherservices.dto.InventoryOperation;
import epasal.com.orderservice.repo.OrderRepository;
import epasal.com.orderservice.services.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static epasal.com.orderservice.constants.OrderStatusConstant.CANCELLED;

@Service
@RequiredArgsConstructor
public class OrderScheduler {
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final InventoryServiceClient inventoryServiceClient;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelInitializedOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        List<Order> orders = orderRepository.findByStatusAndOrderDateBefore("INITIALIZED", threshold);

        for (Order order : orders) {
            Set<OrderItems> orderItems = order.getOrderItems();
            orderItems.forEach(orderItem -> {
                InventoryOperation inventoryOperation = InventoryOperation.builder()
                        .productId(orderItem.getProductId())
                        .quantity(orderItem.getQuantity())
                        .build();
                try {
                    inventoryServiceClient.releaseReservedQuantity(inventoryOperation);
                } catch (Exception e) {
                    throw new ExternalServiceException("Cannot release product in inventory: " + orderItem.getProductName());
                }
            });
            order.setStatus(CANCELLED);
            orderRepository.save(order);
        }

    }
}
