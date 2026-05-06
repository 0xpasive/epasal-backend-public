package epasal.com.cartservice.service;

import epasal.com.cartservice.dto.request.AddItemToCart;
import epasal.com.cartservice.dto.response.CartResponse;

public interface CartService {
    CartResponse addItemToCart(AddItemToCart addItemToCart);

    CartResponse getCart();

    CartResponse removeCartItem(String productId);

    void removeAllCartItems();
}
