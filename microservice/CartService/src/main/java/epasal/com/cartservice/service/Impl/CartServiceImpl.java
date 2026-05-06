package epasal.com.cartservice.service.Impl;

import epasal.com.cartservice.Entity.Cart;
import epasal.com.cartservice.Entity.CartItem;
import epasal.com.cartservice.components.CurrentUser;
import epasal.com.cartservice.dto.request.AddItemToCart;
import epasal.com.cartservice.dto.response.CartResponse;
import epasal.com.cartservice.exception.ProductNotFoundException;
import epasal.com.cartservice.mapper.Mapper;
import epasal.com.cartservice.otherservice.ProductServiceClient;
import epasal.com.cartservice.repository.CartItemRepository;
import epasal.com.cartservice.repository.CartRepository;
import epasal.com.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final CurrentUser currentUser;
    private final Mapper mapper;


    @Override
    public CartResponse addItemToCart(AddItemToCart addItemToCart) {
        String authenticatedUser = requireAuthenticatedUser();
        Boolean exists = productServiceClient.productExists(addItemToCart.getProductId());
        if (!exists) {
            throw new ProductNotFoundException("Product with id " + addItemToCart.getProductId() + " does not exist");
        }
        Cart cart = cartRepository.findByUser(authenticatedUser).orElseGet(() -> createNewCart(authenticatedUser));
        log.info("Cart before adding item: {}", cart.getId());
        CartItem cartItem = cartItemRepository
                .findByProductIdAndCart(addItemToCart.getProductId(), cart)
                .orElse(null);
        if (cart.getItems().size() == 5) {
            throw new AccessDeniedException("You cannot add more than 5 items to the cart. Please remove an existing item to add a new one.");
        }

        if (cartItem == null) {
            CartItem newItem = CartItem.builder()

                    .productId(addItemToCart.getProductId())
                    .quantity(addItemToCart.getQuantity())
                    .build();
            cart.addItem(newItem);
            cartItemRepository.save(newItem);


        } else {
            cartItem.setQuantity(cartItem.getQuantity() + addItemToCart.getQuantity());
            cartItemRepository.save(cartItem);
        }
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return mapper.toResponse(cart);

    }

    @Override
    public CartResponse getCart() {
        String authenticatedUser = requireAuthenticatedUser();
        Cart cart = cartRepository.findByUser(authenticatedUser).orElseGet(() -> createNewCart(authenticatedUser));
        return mapper.toResponse(cart);
    }

    //
    @Override
    public CartResponse removeCartItem(String productId) {
        String user = requireAuthenticatedUser();
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found for user: " + user));
        CartItem cartItem = cartItemRepository.findByProductIdAndCart(productId, cart)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found in cart"));
        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return mapper.toResponse(cart);

    }

    //
    @Override
    public void removeAllCartItems() {
        String user = requireAuthenticatedUser();
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found for user: " + user));
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.isEmpty()) {
            throw new ProductNotFoundException("No items found in cart to remove");
        }
        cartItems.forEach(cart::removeItem);
        cartItemRepository.deleteAll(cartItems);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    private Cart createNewCart(String user) {
        log.info("creating new cart for user: {}", user);
        Cart cart = new Cart();
        cart.setUser(user);

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);

    }

    private String requireAuthenticatedUser() {
        try {
            return String.valueOf(currentUser.getUserId());
        } catch (Exception e) {
            throw new AccessDeniedException("User is not authenticated");
        }

    }
}
