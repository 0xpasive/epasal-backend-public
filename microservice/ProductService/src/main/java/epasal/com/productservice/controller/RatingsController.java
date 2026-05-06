package epasal.com.productservice.controller;

import epasal.com.productservice.dto.request.ReviewRequest;
import epasal.com.productservice.dto.response.ApiResponse;
import epasal.com.productservice.dto.response.ProductReviewResponse;
import epasal.com.productservice.dto.response.ReviewResponse;
import epasal.com.productservice.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static epasal.com.productservice.constant.ApiConstant.*;

@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
public class RatingsController {
    private final RatingService ratingService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping(ADD_REVIEW)
    public ApiResponse<ReviewResponse> addReview(@RequestBody ReviewRequest reviewRequest) {
        ReviewResponse reviewResponse = ratingService.addRating(reviewRequest);
        return ApiResponse.success("Review added successfully", reviewResponse);
    }

    @GetMapping(GET_REVIEWS)
    public ApiResponse<ProductReviewResponse> getProductReviews(@PathVariable String productId) {
        ProductReviewResponse productReviewResponse = ratingService.getProductReviews(productId);
        return ApiResponse.success("Product reviews retrieved successfully", productReviewResponse);

    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping(DELETE)
    public ApiResponse<Void> deleteReview(@PathVariable String productId) {
        ratingService.deleteReview(productId);
        return ApiResponse.success("Review deleted successfully", null);
    }

}
