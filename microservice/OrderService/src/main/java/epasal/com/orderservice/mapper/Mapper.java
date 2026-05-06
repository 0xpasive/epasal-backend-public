package epasal.com.orderservice.mapper;

import epasal.com.orderservice.Entity.Order;
import epasal.com.orderservice.Entity.OrderHistory;
import epasal.com.orderservice.Entity.OrderItems;
import epasal.com.orderservice.Entity.Payment;
import epasal.com.orderservice.dto.response.OrderHistoryResponse;
import epasal.com.orderservice.dto.response.OrderItemsResponse;
import epasal.com.orderservice.dto.response.OrderResponse;
import epasal.com.orderservice.dto.response.PaymentResponse;
import org.mapstruct.Mapping;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "orderItems", target = "orderItems")
    @Mapping(source = "totalAmount", target = "totalAmount")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "addressId", target = "addressId")
    @Mapping(source = "orderDate", target = "orderDate")
    OrderResponse toResponse(Order order);

    @Mapping(source = "productName", target = "productName")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "price")
    OrderItemsResponse toItemResponse(OrderItems orderItems);

    OrderHistoryResponse toHistoryResponse(OrderHistory orderHistory);

    PaymentResponse toPaymentResponse(Payment payment);
}
