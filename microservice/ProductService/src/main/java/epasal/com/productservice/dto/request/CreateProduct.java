package epasal.com.productservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProduct {

    @NotBlank(message = "Product name is required")
    @Size(min = 10, max = 100, message = "Product name must be between 10 and 100 characters")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private double price;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private double discountPrice;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Category is required")
    @Size(min = 10, max = 100, message = "Category must be between 10 and 100 characters")
    private String category;

    @NotBlank(message = "Brand is required")
    @Size(min = 10, max = 100, message = "Brand must be between 10 and 100 characters")
    private String brand;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity must not exceed 100")
    private int quantity;

    @NotEmpty(message = "Tags are required")
    private Set<String> tags;

    @NotBlank(message = "Return policy is required")
    private String returnPolicy;

    @NotBlank(message = "Warranty is required")
    private String warranty;

}
