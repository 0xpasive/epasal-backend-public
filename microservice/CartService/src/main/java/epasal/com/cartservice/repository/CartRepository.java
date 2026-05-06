package epasal.com.cartservice.repository;

import epasal.com.cartservice.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(String user);
}
