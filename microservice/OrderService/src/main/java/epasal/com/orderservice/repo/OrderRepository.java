package epasal.com.orderservice.repo;

import epasal.com.orderservice.Entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByUserId(String userId, Pageable pageable);

    Page<Order> findByUserIdAndStatus(String userId, String status, Pageable pageable);

    Optional<Order> findByTransactionId(String transactionId);

    Page<Order> findByStatusIn(List<String> statuses, Pageable pageable);

    Order findByPidx(String pidx);

    List<Order> findByStatusAndOrderDateBefore(String status, LocalDateTime orderDateBefore);
}
