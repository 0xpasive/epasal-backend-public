package epasal.com.orderservice.otherservices.dto;

import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class BatchProductResponse {
    private List<ProductResponse> products;
    private List<String> notFoundProductIds;
    private Integer totalProductsFound;
    private Integer totalProductsNotFound;
}
