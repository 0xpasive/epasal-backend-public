package epasal.com.productservice.mapper;

import epasal.com.productservice.dto.request.CreateProduct;
import epasal.com.productservice.dto.response.ProductResponse;
import epasal.com.productservice.dto.response.ProductReviewResponse;
import epasal.com.productservice.dto.response.ReviewResponse;
import epasal.com.productservice.entity.Product;
import epasal.com.productservice.entity.ProductReview;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {
    Product toEntity(CreateProduct createProduct);

    ProductResponse toResponse(Product product);

    ReviewResponse toReviewResponse(ProductReview.Review review);

    ProductReviewResponse toProductReviewResponse(ProductReview productReview);
}
