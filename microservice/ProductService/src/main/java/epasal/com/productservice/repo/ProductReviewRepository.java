package epasal.com.productservice.repo;

import epasal.com.productservice.entity.ProductReview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductReviewRepository extends MongoRepository<ProductReview, String> {
    Optional<ProductReview> findByProductId(String productId);
}
