package epasal.com.productservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "ratings")
public class ProductReview {
    @Id
    private String productId;
    private List<Review> reviews;
    private int totalReviews;
    private double averageRating;

    public void calculateTotalReviews() {
        this.totalReviews = reviews != null ? reviews.size() : 0;
    }

    public void calculateAverageRating() {
        if (reviews != null && !reviews.isEmpty()) {
            double avg = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            this.averageRating = Math.round(avg * 100.0) / 100.0;
        } else {
            this.averageRating = 0.0;
        }
    }

    @Setter
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Review {
        private String userId;
        private String fullName;
        private double rating;
        private String comment;
    }
}
