package epasal.com.productservice.repo;

import epasal.com.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomProductRepository {
    Page<Product> searchProducts(
            String keyword,
            String category,
            String brand,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );

    Page<Product> findSimilarProduct(
            String productId,
            Pageable pageable
    );
}
