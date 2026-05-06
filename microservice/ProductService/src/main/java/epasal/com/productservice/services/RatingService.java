package epasal.com.productservice.services;

import epasal.com.productservice.dto.request.DeleteUserReview;
import epasal.com.productservice.dto.request.ReviewRequest;
import epasal.com.productservice.dto.response.ProductReviewResponse;
import epasal.com.productservice.dto.response.ReviewResponse;

public interface RatingService {
    ReviewResponse addRating(ReviewRequest reviewRequest);

    ProductReviewResponse getProductReviews(String productId);

    void deleteReview(String reviewId);

    void deleteUserReview(DeleteUserReview deleteUserReview);

}
