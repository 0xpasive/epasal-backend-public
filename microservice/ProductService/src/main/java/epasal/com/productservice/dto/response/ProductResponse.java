package epasal.com.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private Double price;
    private Double discountPrice;
    private String description;
    private String category;
    private String brand;
    private String imageUrl;
    private Set<String> tags;
    private String returnPolicy;
    private String warranty;
    private String inventoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
