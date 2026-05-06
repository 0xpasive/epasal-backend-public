package epasal.com.orderservice.services;

import epasal.com.orderservice.dto.requests.OrderRequest;
import epasal.com.orderservice.dto.response.OrderHistoryResponse;
import epasal.com.orderservice.dto.response.OrderResponse;
import epasal.com.orderservice.dto.response.PageResponse;
import epasal.com.orderservice.dto.response.PaymentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest orderRequest);

    PageResponse<OrderResponse> getMyOrders(int page, int size, String sortBy, String direction);

    PageResponse<OrderResponse> getOrderByStatus(String status, Pageable pageable);

    PaymentResponse getPayment(String orderId);

    PageResponse<OrderResponse> getAllOrders(int page, int size);

    PageResponse<PaymentResponse> getAllPayments(int page, int size);

    OrderResponse getOrderById(String orderId);

    OrderResponse getOrderByIdAdmin(String orderId);

    OrderResponse cancelOrder(String orderId);

    List<OrderHistoryResponse> getOrderHistory(String orderId);

    void handlePaymentSuccess(String data);

    void handlePaymentFailure(String orderId);

    void handleKhaltiPayment(String pidx);

    OrderResponse rePay(String orderId);

    void reportConflict(String transactionId);

    PaymentResponse getPaymentByIdAdmin(String transactionId);

    void deleteMyOrder(String orderId);


}
