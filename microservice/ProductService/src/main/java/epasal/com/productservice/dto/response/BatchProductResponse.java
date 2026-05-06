package epasal.com.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class BatchProductResponse {
    private List<ProductResponse> products;
    private List<String> notFoundProductIds;
    private Integer totalProductsFound;
    private Integer totalProductsNotFound;
}
