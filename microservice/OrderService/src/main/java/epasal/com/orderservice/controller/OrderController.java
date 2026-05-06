package epasal.com.orderservice.controller;

import epasal.com.orderservice.dto.requests.OrderRequest;
import epasal.com.orderservice.dto.response.*;
import epasal.com.orderservice.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static epasal.com.orderservice.constants.ApiConstants.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_API)
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('User')")
    @PostMapping(PLACE_ORDER)
    public ApiResponse<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse response = orderService.placeOrder(orderRequest);
        return ApiResponse.success("Order placed successfully", response);

    }

    @PreAuthorize("hasRole('User')")
    @GetMapping()
    public ApiResponse<PageResponse> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        PageResponse response = orderService.getMyOrders(page, size, sortBy, direction);
        return ApiResponse.success("Order fetched successfully", response);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping(GET_ORDERS_BY_STATUS)
    public ApiResponse<PageResponse> getOrderByStatus(@RequestParam String name, Pageable pageable) {
        PageResponse response = orderService.getOrderByStatus(name, pageable);
        return ApiResponse.success("Orders fetched successfully", response);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping(PAYMENT)
    public ApiResponse<PaymentResponse> getPayment(@PathVariable String orderId) {
        PaymentResponse response = orderService.getPayment(orderId);
        return ApiResponse.success("Payment details fetched successfully", response);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping(GET_ORDER_BY_ID)
    public ApiResponse<OrderResponse> getOrderById(@PathVariable String orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        return ApiResponse.success("Order fetched successfully", response);
    }


    @PreAuthorize("hasRole('User')")
    @PostMapping(CANCEL)
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable String orderId) {
        OrderResponse response = orderService.cancelOrder(orderId);
        return ApiResponse.success("Order cancelled successfully", response);
    }

    @PreAuthorize("hasRole('User')")
    @PostMapping(DELETE)
    public ApiResponse<Void> deleteOrder(@PathVariable String orderId){
        orderService.deleteMyOrder(orderId);
        return ApiResponse.success("Order deleted successfully", null);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping(REPAY)
    public ApiResponse<OrderResponse> rePay(@RequestParam String orderId) {
        OrderResponse response = orderService.rePay(orderId);
        return ApiResponse.success("Re-payment initiated successfully", response);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping(HISTORY)
    public ApiResponse<List<OrderHistoryResponse>> getOrderHistory(@PathVariable String id) {
        List<OrderHistoryResponse> response = orderService.getOrderHistory(id);
        return ApiResponse.success("Order history fetched successfully", response);
    }

    @GetMapping(PAYMENT_SUCCESS)
    public ApiResponse<Void> paymentSuccess(@RequestParam String data) {
        orderService.handlePaymentSuccess(data);
        return ApiResponse.success("Payment Successfull.", null);
    }

    @GetMapping(PAYMENT_FAILURE)
    public ApiResponse<Void> paymentFail(@RequestParam String orderId) {
        orderService.handlePaymentFailure(orderId);
        return ApiResponse.success("Payment Failed.", null);
    }

    @PostMapping(KHALTI)
    public ApiResponse<Void> khaltiPaymentHandler(@RequestParam String pidx) {
        orderService.handleKhaltiPayment(pidx);
        return ApiResponse.success("Khalti payment processed successfully", null);
    }



}
