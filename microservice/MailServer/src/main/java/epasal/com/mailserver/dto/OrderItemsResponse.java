package epasal.com.mailserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class OrderItemsResponse {


    private String productName;

    private int quantity;

    private double price;

}
