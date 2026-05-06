package epasal.com.productservice.services.impl;

import epasal.com.productservice.components.CurrentUser;
import epasal.com.productservice.dto.request.DeleteUserReview;
import epasal.com.productservice.dto.request.ReviewRequest;
import epasal.com.productservice.dto.response.ProductReviewResponse;
import epasal.com.productservice.dto.response.ReviewResponse;
import epasal.com.productservice.entity.ProductReview;
import epasal.com.productservice.exception.ResourceNotFoundException;
import epasal.com.productservice.mapper.Mapper;
import epasal.com.productservice.otherservices.UserServiceClient;
import epasal.com.productservice.repo.ProductReviewRepository;
import epasal.com.productservice.services.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final ProductReviewRepository productReviewRepository;
    private final CurrentUser currentUser;
    private final Mapper mapper;
    private final UserServiceClient userServiceClient;

    @Override
    public ReviewResponse addRating(ReviewRequest reviewRequest) {
        String authenticatedUser = requireAuthenticatedUser();
        String fullName = userServiceClient.getUserFullName(authenticatedUser).getData().getFullName();

        String productId = reviewRequest.getProductId();

        log.info("Adding review for product: {} by user: {}", productId, authenticatedUser);

        ProductReview productReview = findOrCreateProductReview(productId);

        productReview.getReviews().stream()
                .filter(r -> r.getUserId().equals(authenticatedUser))
                .findFirst().ifPresent(existingReview -> productReview.getReviews().remove(existingReview));

        log.debug("Creating review object for product: {} by user: {}", productId, authenticatedUser);
        ProductReview.Review review = ProductReview.Review.builder()
                .userId(authenticatedUser)
                .fullName(fullName)
                .rating(reviewRequest.getRating())
                .comment(reviewRequest.getComment())
                .build();

        log.debug("Adding review to product review list for product: {}", productId);
        productReview.getReviews().add(review);
        productReview.calculateTotalReviews();
        productReview.calculateAverageRating();
        productReviewRepository.save(productReview);

        log.info("Review added successfully for product: {}", productId);
        return mapper.toReviewResponse(review);
    }

    @Override
    public ProductReviewResponse getProductReviews(String productId) {
        log.debug("Fetching reviews for product: {}", productId);

        ProductReview productReview = findOrCreateProductReview(productId);
        return mapper.toProductReviewResponse(productReview);
    }

    @Override
    public void deleteReview(String productId) {
        log.info("Deleting review for product: {} by authenticated user", productId);
        ProductReview productReview = productReviewRepository.findByProductId(productId).orElseThrow(() -> new ResourceNotFoundException("Product Review Not Found"));
        String authenticatedUser = requireAuthenticatedUser();
        removeReview(authenticatedUser, productReview);
        log.info("Review deleted successfully for product: {} by user: {}", productId, authenticatedUser);
    }

    @Override
    public void deleteUserReview(DeleteUserReview deleteUserReview) {
        log.info("Deleting review for product: {} by user: {}", deleteUserReview.getProductId(), deleteUserReview.getUserId());
        ProductReview productReview = productReviewRepository.findByProductId(deleteUserReview.getProductId()).orElseThrow(()-> new ResourceNotFoundException("Product Review not found."));
        removeReview(deleteUserReview.getUserId(), productReview);
    }

    private void removeReview(String userId, ProductReview productReview) {
        log.info("Removing review for product: {} by user: {}", productReview.getProductId(), userId);
        ProductReview.Review review = productReview.getReviews().stream()
                .filter(r -> r.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Review Not Found for User"));
        productReview.getReviews().remove(review);
        productReview.calculateTotalReviews();
        productReview.calculateAverageRating();
        productReviewRepository.save(productReview);
    }


    private ProductReview findOrCreateProductReview(String productId) {
        log.info("Finding or creating product review for product: {}", productId);
        return productReviewRepository.findByProductId(productId)
                .orElseGet(() -> createEmptyProductReview(productId));
    }

    private ProductReview createEmptyProductReview(String productId) {
        log.info("Creating empty product review for product: {}", productId);
        return ProductReview.builder()
                .productId(productId)
                .reviews(new ArrayList<>())
                .build();
    }

    private String requireAuthenticatedUser() {
        try {
            log.info("Retrieving authenticated user from CurrentUser component");
            return String.valueOf(currentUser.getUserId());
        } catch (Exception e) {
            log.error("Failed to retrieve authenticated user: {}", e.getMessage());
            throw new ResourceNotFoundException("User is not authenticated");
        }
    }

}

