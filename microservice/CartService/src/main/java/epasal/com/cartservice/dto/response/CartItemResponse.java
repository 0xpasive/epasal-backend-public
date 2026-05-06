package epasal.com.cartservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse implements Serializable {
    public String productId;
    public int quantity;
}
