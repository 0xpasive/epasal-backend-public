package epasal.com.orderservice.controller;

import epasal.com.orderservice.dto.response.ApiResponse;
import epasal.com.orderservice.dto.response.OrderResponse;
import epasal.com.orderservice.dto.response.PageResponse;
import epasal.com.orderservice.dto.response.PaymentResponse;
import epasal.com.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static epasal.com.orderservice.constants.ApiConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_API)
class AdminController {

    private final OrderService orderService;

    @GetMapping(GET_ORDERS_BY_ID_ADMIN)
    public ApiResponse<OrderResponse> getOrderByIdAdmin(@PathVariable String id) {
        OrderResponse response = orderService.getOrderByIdAdmin(id);
        return ApiResponse.success("Order fetched successfully", response);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(GET_PAYMENT_BY_ID_ADMIN)
    public ApiResponse<PaymentResponse> getPaymentByIdAdmin(@PathVariable String id) {
        PaymentResponse response = orderService.getPaymentByIdAdmin(id);
        return ApiResponse.success("Payment details fetched successfully", response);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(GET_ALL_ORDERS)
    public ApiResponse<PageResponse> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderResponse> response = orderService.getAllOrders(page, size);
        return ApiResponse.success("Orders fetched successfully", response);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(ALL_PAYMENT)
    public ApiResponse<PageResponse> getAllPayments(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        PageResponse<PaymentResponse> response = orderService.getAllPayments(page, size);
        return ApiResponse.success("Payments fetched successfully", response);
    }
    @PreAuthorize("hasRole('Admin')")
    @PostMapping(CONFLICT)
    public ApiResponse<Void> conflictOrder(@RequestParam String transactionId) {
        orderService.reportConflict(transactionId);
        return ApiResponse.success("Conflict reported successfully", null);
    }


}
