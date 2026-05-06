package epasal.com.orderservice.services.impl;

import epasal.com.orderservice.Entity.Order;
import epasal.com.orderservice.Entity.OrderHistory;
import epasal.com.orderservice.Entity.OrderItems;
import epasal.com.orderservice.Entity.Payment;
import epasal.com.orderservice.annotations.LogExecutionTime;
import epasal.com.orderservice.components.CurrentUser;
import epasal.com.orderservice.dto.requests.OrderItemsRequest;
import epasal.com.orderservice.dto.requests.OrderRequest;
import epasal.com.orderservice.dto.response.*;
import epasal.com.orderservice.event.OrderEvent;
import epasal.com.orderservice.eventPublisher.RabbitEventPublisher;
import epasal.com.orderservice.exception.AccessDeniedException;
import epasal.com.orderservice.exception.ExternalServiceException;
import epasal.com.orderservice.exception.OrderNotCompletedException;
import epasal.com.orderservice.exception.OrderNotFoundException;
import epasal.com.orderservice.mapper.Mapper;
import epasal.com.orderservice.otherservices.AddressServiceClient;
import epasal.com.orderservice.otherservices.InventoryServiceClient;
import epasal.com.orderservice.otherservices.ProductServiceClient;
import epasal.com.orderservice.otherservices.dto.AddressExists;
import epasal.com.orderservice.otherservices.dto.BatchProductResponse;
import epasal.com.orderservice.otherservices.dto.BatchProducts;
import epasal.com.orderservice.otherservices.dto.InventoryOperation;
import epasal.com.orderservice.repo.OrderHistoryRepository;
import epasal.com.orderservice.repo.OrderRepository;
import epasal.com.orderservice.repo.PaymentRepository;
import epasal.com.orderservice.services.OrderService;
import epasal.com.orderservice.services.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

import static epasal.com.orderservice.constants.OrderStatusConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final ProductServiceClient productServiceClient;
    private final OrderRepository orderRepository;
    private final Mapper mapper;
    private final CurrentUser currentUser;
    private final InventoryServiceClient inventoryServiceClient;
    private final AddressServiceClient addressServiceClient;
    private final OrderHistoryRepository orderHistoryRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final RabbitEventPublisher rabbitEventPublisher;

    @Override
    @Transactional
    @LogExecutionTime
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        log.info("Placing order for user: {}", currentUser.getUserId());
        UUID authenticatedUser = requireAuthenticatedUser();
        addressExists(orderRequest.getAddressId(), String.valueOf(authenticatedUser));

        log.debug("Creating order for user: {}, addressId: {}, paymentMethod: {}", authenticatedUser, orderRequest.getAddressId(), orderRequest.getPaymentMethod());
        Order order = Order.builder()
                .id(randomOrderID())
                .userId(String.valueOf(authenticatedUser))
                .addressId(orderRequest.getAddressId())
                .transactionId(randomTransactionId())
                .build();

        log.debug("Fetching product details for order items: {}", orderRequest.getItems().stream().map(OrderItemsRequest::getProductId).toList());
        ApiResponse<BatchProductResponse> response = getBatchProducts(orderRequest);
        response.getData().getProducts()
                .forEach(product -> {
                    OrderItemsRequest matchingItem = orderRequest.getItems().stream()
                            .filter(item -> item.getProductId().equals(product.getId()))
                            .findFirst()
                            .orElseThrow(() -> new OrderNotFoundException("Product not found in order request: " + product.getId()));
                    OrderItems orderItems = OrderItems.builder()
                            .order(order)
                            .productId(product.getId())
                            .productName(product.getName())
                            .price(product.getDiscountPrice() > 0 ? product.getDiscountPrice() : product.getPrice())
                            .quantity(matchingItem.getQuantity())
                            .build();
                    order.addOrderItem(orderItems);
                    InventoryOperation inventoryOperation = InventoryOperation.builder()
                            .productId(product.getId())
                            .quantity(matchingItem.getQuantity())
                            .build();
                    try {
                        inventoryServiceClient.reserveQuantity(inventoryOperation);
                    } catch (Exception e) {
                        throw new ExternalServiceException("Cannot reserve product in inventory: " + product.getId());
                    }
                });
        log.debug("Calculating total amount for order with {} items", order.getOrderItems().size());
        order.calculateTotalAmount();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(INITIALIZED);
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        orderRepository.save(order);

        CreateOrderHistory(order, order.getStatus());

        log.info("Order placed successfully for user); {}, orderId: {}", authenticatedUser, order.getId());
        return getOrderResponse(order, orderRequest.getPaymentMethod());
    }

    @Override
    public PageResponse getMyOrders(int page, int size, String sortBy, String direction) {
        log.info("Fetching orders for user: {}, page: {}, size: {}, sortBy: {}, direction: {}", currentUser.getUserId(), page, size, sortBy, direction);
        UUID authenticatedUser = requireAuthenticatedUser();
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderRepository.findByUserId(String.valueOf(authenticatedUser), pageable);
        return createPageResponse(orders);
    }

    @Override
    public PageResponse getOrderByStatus(String status, Pageable pageable) {
        log.info("Fetching orders for user: {}, status: {}, page: {}, size: {}", currentUser.getUserId(), status, pageable.getPageNumber(), pageable.getPageSize());
        UUID authenticatedUser = requireAuthenticatedUser();
        Page<Order> orders = orderRepository.findByUserIdAndStatus(String.valueOf(authenticatedUser), status, pageable);
        return createPageResponse(orders);
    }

    @Override
    public PaymentResponse getPayment(String orderId) {
        log.info("Fetching payment details for orderId: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));
        UUID authenticatedUser = requireAuthenticatedUser();
        if (!order.getUserId().equals(String.valueOf(authenticatedUser))) {
            log.warn("Unauthorized access attempt by user: {} for orderId: {}", authenticatedUser, orderId);
            throw new AccessDeniedException("Unauthorized");
        }
        Payment payment = paymentRepository.findByTransactionId(order.getTransactionId());
        return mapper.toPaymentResponse(payment);
    }

    @Override
    public PageResponse getAllOrders(int page, int size) {
        log.info("Fetching all orders for admin, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<String> statuses = Arrays.asList("CONFIRMED", "CONFLICT");
        Page<Order> orderPage = orderRepository.findByStatusIn(statuses, pageable);
        return createPageResponse(orderPage);
    }

    @Override
    public PageResponse getAllPayments(int page, int size) {
        log.info("Fetching all payments for admin, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = paymentRepository.findAll(pageable);
        return createPaymentPageResponse(paymentPage);
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        log.info("Fetching order details for user: {}, orderId: {}", currentUser.getUserId(), orderId);
        UUID authenticatedUser = requireAuthenticatedUser();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));
        if (order.getUserId().equals(String.valueOf(authenticatedUser))) {
            log.warn("Unauthorized access attempt by user: {} for orderId: {}", authenticatedUser, orderId);
            return mapper.toResponse(order);
        } else {
            throw new OrderNotFoundException("Order not found for this user");
        }
    }

    @Override
    public OrderResponse getOrderByIdAdmin(String orderId) {
        log.info("Fetching order details for admin, orderId: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return mapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String orderId) {
        log.info("Attempting to cancel order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));
        UUID authenticatedUser = requireAuthenticatedUser();
        if (!order.getUserId().equals(String.valueOf(authenticatedUser))) {
            log.warn("Unauthorized cancellation attempt by user: {} for orderId: {}", authenticatedUser, orderId);
            throw new AccessDeniedException("You are not authorized to cancel this order");
        }
        if (!order.getStatus().equals("INITIALIZED")) {
            log.warn("Attempt to cancel order with status: {} by user: {} for orderId: {}", order.getStatus(), authenticatedUser, orderId);
            log.debug("status: {}", order.getStatus());
            throw new AccessDeniedException("Cannot Cancel This Order.");
        }
        releaseInventory(order);
        order.setStatus(CANCELLED);
        orderRepository.save(order);
        CreateOrderHistory(order, order.getStatus());

        log.info("Order with ID: {} cancelled successfully by user: {}", orderId, authenticatedUser);
        return mapper.toResponse(order);
    }


    @Override
    public List<OrderHistoryResponse> getOrderHistory(String orderId) {
        log.info("Fetching order history for user: {}, orderId: {}", currentUser.getUserId(), orderId);
        UUID authenticatedUser = requireAuthenticatedUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order with ID: {} not found for user: {}", orderId, authenticatedUser);
                    return new OrderNotFoundException("Order not found");
                });

        if (!order.getUserId().equals(authenticatedUser.toString())) {
            log.warn("Unauthorized access attempt to order history by user: {}); for orderId: {}", authenticatedUser, orderId);
            throw new AccessDeniedException("Not Authorized to view this order history");
        }
        List<OrderHistory> orderHistory = orderHistoryRepository.findByOrderId(orderId);

        log.info("Order history fetched successfully for user: {}, orderId: {}, historyCount: {}", authenticatedUser, orderId, orderHistory.size());
        return orderHistory.stream()
                .map(mapper::toHistoryResponse)
                .toList();
    }

    @Override
    public void handlePaymentSuccess(String data) {
        log.info("Handling payment success with data: {}", data);
        DecodedResponse decodedResponse = paymentService.decodeMessage(data);

        Order order = orderRepository.findByTransactionId(decodedResponse.getTransaction_uuid()).orElseThrow(()-> new OrderNotFoundException("Order Not Found"));

        order.setStatus(CONFIRMED);
        orderRepository.save(order);

        createPayment(order, COMPLETED);
        CreateOrderHistory(order, COMPLETED);

        OrderEvent event = new OrderEvent();
        event.setStatus("CONFIRMED");
        event.setOrderId(order.getId());
        try {
            log.info("Publishing order placed event for orderId: {}, status: {}", order.getId(), event.getStatus());
            rabbitEventPublisher.publishOrderPlacedEvent(event);
        } catch (Exception e) {
            log.error("Cannot send email");
        }

    }

    @Override
    public void handlePaymentFailure(String orderId) {
        log.info("Handling payment failure for orderId: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not Found"));
        order.getOrderItems().forEach(orderItem -> {
            InventoryOperation inventoryOperation = InventoryOperation.builder()
                    .productId(orderItem.getProductId())
                    .quantity(orderItem.getQuantity())
                    .build();
            try {
                log.debug("Releasing reserved quantity for productId: {}, quantity: {} due to payment failure for orderId: {}", orderItem.getProductId(), orderItem.getQuantity(), orderId);
                inventoryServiceClient.releaseReservedQuantity(inventoryOperation);
            } catch (Exception e) {
                log.error("Cannot release product in inventory for productId: {} due to payment failure for orderId: {}", orderItem.getProductId(), orderId);
                throw new ExternalServiceException("Cannot release product in inventory: " + orderItem.getProductName());
            }
        });
        order.setStatus(FAILED);
        orderRepository.save(order);
        CreateOrderHistory(order, order.getStatus());
    }

    @Override
    public void handleKhaltiPayment(String pidx) {
        log.info("Handling Khalti payment for pidx: {}", pidx);
        Order order = orderRepository.findByPidx(pidx);
        if (!order.getStatus().equals(INITIALIZED)) {
            throw new AccessDeniedException("Order Already processed");
        }
        KhaltiLookupResponse response = null;
        try {
            response = paymentService.khaltiLookup(pidx);
        } catch (Exception e) {
            log.error("Error during Khalti lookup for pidx: {}", pidx, e);
            handlePaymentFailure(order.getId());
            throw new OrderNotCompletedException("Order Failed ");
        }
        if (!response.getStatus().equals("Completed")) {
            log.warn("Khalti payment not completed for pidx: {}, status: {}", pidx, response.getStatus());
            handlePaymentFailure(order.getId());
            throw new OrderNotCompletedException("Order not completed with status: " + response.getStatus());
        }

        order.setStatus(CONFIRMED);
        orderRepository.save(order);
        createPayment(order, response.getStatus().toUpperCase());
        CreateOrderHistory(order, order.getStatus().toUpperCase());
        OrderEvent event = new OrderEvent();
        event.setOrderId(order.getId());
        event.setStatus("CONFIRMED");
        try {
            log.debug("Publishing order placed event for orderId: {}, status: {}", order.getId(), event.getStatus());
            rabbitEventPublisher.publishOrderPlacedEvent(event);
        } catch (Exception e) {
            log.error("Cannot send email");
        }
    }

    @Override
    public OrderResponse rePay(String orderId) {
        log.info("orderId: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));
        order.setTransactionId(randomTransactionId());
        return getOrderResponse(order, order.getPaymentMethod());
    }

    @Override
    @Transactional
    public void reportConflict(String transactionId) {
        log.info("Reporting conflict for transactionId: {}", transactionId);
        Order order = orderRepository.findByTransactionId(transactionId).orElseThrow(() -> new OrderNotFoundException("Order Not Found"));
        order.setStatus(CONFLICT);
        orderRepository.save(order);

        log.debug("Releasing inventory for orderId: {} due to conflict report for transactionId: {}", order.getId(), transactionId);
        releaseInventory(order);

        Payment payment = paymentRepository.findByTransactionId(transactionId);
        log.info("Updating payment status to CONFLICT for transactionId: {}", transactionId);
        payment.setStatus(CONFLICT);
        paymentRepository.save(payment);
        CreateOrderHistory(order, CONFLICT);
        OrderEvent event = new OrderEvent();
        event.setOrderId(order.getId());
        event.setStatus("CONFLICT");
        try {
            log.debug("Publishing order conflict event for orderId: {}, status: {}", order.getId(), event.getStatus());
            rabbitEventPublisher.publishOrderPlacedEvent(event);
        } catch (Exception e) {
            log.error("Cannot send email");
        }
    }

    @Override
    public PaymentResponse getPaymentByIdAdmin(String transactionId) {
        log.info("Fetching payment details for admin, transactionId: {}", transactionId);
        Payment payment = paymentRepository.findByTransactionId(transactionId);
        if (payment == null) {
            log.warn("Payment not found for transactionId: {}", transactionId);
            throw new OrderNotFoundException("Payment not found for transaction ID: " + transactionId);
        }
        return mapper.toPaymentResponse(payment);
    }

    @Override
    public void deleteMyOrder(String orderId) {
        log.info("Attempting to delete order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));
        UUID authenticatedUser = requireAuthenticatedUser();
        if (!order.getUserId().equals(String.valueOf(authenticatedUser))) {
            log.warn("Unauthorized delete attempt by user: {} for orderId: {}", authenticatedUser, orderId);
            throw new AccessDeniedException("You are not authorized to delete this order");
        }
        if (order.getStatus().equals(CONFIRMED) || order.getStatus().equals(CONFLICT)) {
            log.warn("Attempt to delete order with status: {} by user: {} for orderId: {}", order.getStatus(), authenticatedUser, orderId);
            throw new AccessDeniedException("Cannot delete this order");
        }
        if (order.getStatus().equals(INITIALIZED)) {
            log.debug("Releasing inventory for orderId: {} due to delete attempt for order with status INITIALIZED by user: {}", orderId, authenticatedUser);
            releaseInventory(order);
        }
        orderRepository.delete(order);
    }

    private void releaseInventory(Order order) {
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
    }

    @NonNull
    private OrderResponse getOrderResponse(Order order, Order.PaymentMethod paymentMethod) {
        OrderResponse orderResponse = mapper.toResponse(order);

        if (paymentMethod.equals(Order.PaymentMethod.ESEWA)) {
            try {
                orderResponse.setEsewaResponse(paymentService.esewaResponse(order));
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        }
        KhaltiPaymentResponse khaltiPaymentResponse = paymentService.initiateKhaltiPayment(order);
        order.setPidx(khaltiPaymentResponse.getPidx());
        orderRepository.save(order);
        orderResponse.setKhaltiPaymentResponse(khaltiPaymentResponse);
        return orderResponse;
    }


    private UUID requireAuthenticatedUser() {
        try {
            return currentUser.getUserId();
        } catch (Exception e) {
            throw new AccessDeniedException("Unauthenticated access");
        }

    }

    public String randomOrderID() {
        return "ep-order-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public String randomTransactionId() {
        return "ep-tx-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public void addressExists(String addressId, String userId) {
        AddressExists addressExists = AddressExists.builder()
                .addressId(addressId)
                .userId(userId)
                .build();
        if (!addressServiceClient.addressExists(addressExists)) {
            throw new AccessDeniedException("User does not have this address");
        }
    }

    public ApiResponse<BatchProductResponse> getBatchProducts(OrderRequest orderRequest) {
        List<String> productIds = orderRequest.getItems().stream()
                .map(OrderItemsRequest::getProductId)
                .toList();
        log.info("products: {}", productIds);

        BatchProducts batchProducts = BatchProducts.builder()
                .productIds(productIds).build();

        log.info("Fetching product details for product IDs: {}", productIds);

        ApiResponse<BatchProductResponse> response = productServiceClient.getBatchProducts(batchProducts);
        log.info("Product service response: {}", response);
        if (response == null || response.getData().getProducts().isEmpty()) {
            throw new ExternalServiceException("Failed to fetch product details from product service");
        }
        return response;
    }

    public void createPayment(Order order, String status) {
        Payment payment = new Payment();
        payment.setTransactionId(order.getTransactionId());
        payment.setAmount(order.getTotalAmount());
        payment.setGateway(String.valueOf(order.getPaymentMethod()));
        payment.setStatus(status);
        payment.setCreated_at(LocalDateTime.now());
        payment.setUpdated_at(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    public void CreateOrderHistory(Order order, String status) {
        OrderHistory history = OrderHistory.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .status(status)
                .time(LocalDateTime.now())
                .build();
        orderHistoryRepository.save(history);

    }

    private PageResponse createPageResponse(Page<Order> productPage) {
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

    private PageResponse createPaymentPageResponse(Page<Payment> paymentPage) {
        return new PageResponse(
                paymentPage.getContent()
                        .stream()
                        .map(mapper::toPaymentResponse)
                        .toList(),
                paymentPage.getNumber(),
                paymentPage.getSize(),
                paymentPage.getTotalElements(),
                paymentPage.getTotalPages(),
                paymentPage.isLast()
        );
    }

}
