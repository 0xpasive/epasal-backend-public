package epasal.com.productservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProduct {

    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private Double price;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private Double discountPrice;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Size(min = 10, max = 100, message = "Category must be between 10 and 100 characters")
    private String category;

    @Size(min = 10, max = 100, message = "Brand must be between 10 and 100 characters")
    private String brand;

    @NotEmpty(message = "Tags are required")
    private Set<String> tags;

    @NotBlank(message = "Return policy is required")
    private String returnPolicy;

    @NotBlank(message = "Warranty is required")
    private String warranty;

}
