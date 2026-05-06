package epasal.com.cartservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    public UUID id;
    public List<CartItemResponse> items;
}
