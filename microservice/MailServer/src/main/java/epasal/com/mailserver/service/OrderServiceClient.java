package epasal.com.mailserver.service;

import epasal.com.mailserver.dto.ApiResponse;
import epasal.com.mailserver.dto.OrderResponse;
import epasal.com.mailserver.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@FeignClient(name = "ORDERSERVICE")
public interface OrderServiceClient {
    @GetMapping("/api/v1/orders/admin/{id}")
    ApiResponse<OrderResponse> getOrderById(@PathVariable String id);

    @GetMapping("/api/v1/orders/payment/admin/{id}")
    ApiResponse<PaymentResponse> getPaymentByIdAdmin(@PathVariable String id);
}
