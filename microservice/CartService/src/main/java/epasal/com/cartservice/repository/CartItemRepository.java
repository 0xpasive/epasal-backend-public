package epasal.com.cartservice.repository;

import epasal.com.cartservice.Entity.Cart;
import epasal.com.cartservice.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByProductIdAndCart(String productId, Cart cart);

    List<CartItem> findByCart(Cart cart);
}
