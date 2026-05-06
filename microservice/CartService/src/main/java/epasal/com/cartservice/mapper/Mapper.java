package epasal.com.cartservice.mapper;

import epasal.com.cartservice.Entity.Cart;
import epasal.com.cartservice.Entity.CartItem;
import epasal.com.cartservice.dto.response.CartItemResponse;
import epasal.com.cartservice.dto.response.CartResponse;
import org.mapstruct.Mapping;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "items", target = "items")
    CartResponse toResponse(Cart cart);

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "quantity", target = "quantity")
    CartItemResponse toItemResponse(CartItem cartItem);

}
