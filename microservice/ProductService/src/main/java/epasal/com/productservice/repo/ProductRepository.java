package epasal.com.productservice.repo;

import epasal.com.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String>, CustomProductRepository {

    Optional<Product> findByIdAndPublished(String id, boolean published);

    Page<Product> findAllByPublishedTrue(boolean published, Pageable pageable);

    boolean existsByName(String name);


    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);

    List<Product> findAllByFeatured(boolean featured);

    Page<Product> findAllByPublished(boolean published, Pageable pageable);


}
