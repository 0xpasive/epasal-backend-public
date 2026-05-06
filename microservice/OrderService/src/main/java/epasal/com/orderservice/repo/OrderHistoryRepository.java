package epasal.com.orderservice.repo;

import epasal.com.orderservice.Entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Integer> {
    List<OrderHistory> findByOrderId(String orderId);
}
