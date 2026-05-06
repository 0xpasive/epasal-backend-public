package epasal.com.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductReviewResponse {
    private String productId;
    private List<ReviewResponse> reviews;
    private int totalReviews;
    private double averageRating;

}
