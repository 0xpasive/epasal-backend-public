package epasal.com.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;


@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private Double price;
    private Double discountPrice;
    private Double discountedPrice;
    private String description;
    @Indexed
    private String category;
    private String brand;
    private String imageUrl;
    private Set<String> tags;
    private String returnPolicy;
    private String warranty;
    private String inventoryId;
    private boolean featured;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
