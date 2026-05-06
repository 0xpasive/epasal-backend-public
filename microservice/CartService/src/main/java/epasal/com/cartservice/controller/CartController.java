package epasal.com.cartservice.controller;

import epasal.com.cartservice.dto.request.AddItemToCart;
import epasal.com.cartservice.dto.response.ApiResponse;
import epasal.com.cartservice.dto.response.CartResponse;
import epasal.com.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static epasal.com.cartservice.constants.ApiConstants.*;


@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    public ApiResponse<CartResponse> addItemToCart(@Valid @RequestBody AddItemToCart addItemToCart) {
        CartResponse CartItemResponse = cartService.addItemToCart(addItemToCart);
        return ApiResponse.success("Item added to cart successfully", CartItemResponse);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    public ApiResponse<CartResponse> getCartItems() {
        CartResponse response = cartService.getCart();
        return ApiResponse.success("Cart fetched successfully", response);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(REMOVE_ITEM)
    public ApiResponse<CartResponse> removeCartItem(@PathVariable String productId) {
        CartResponse response = cartService.removeCartItem(productId);
        return ApiResponse.success("Item removed from cart successfully", response);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping(REMOVE_ALL_ITEMS)
    public ApiResponse<CartResponse> removeAllCartItems() {
        cartService.removeAllCartItems();
        return ApiResponse.success("All items removed from cart successfully", null);
    }

}
