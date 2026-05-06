package epasal.com.productservice.repo.impl;

import epasal.com.productservice.entity.Product;
import epasal.com.productservice.repo.CustomProductRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Product> searchProducts(String keyword, String category, String brand, Double minPrice, Double maxPrice, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("published").is(true));
        if (keyword != null && !keyword.trim().isEmpty()) {
            Criteria keywordCriteria = new Criteria().orOperator(
                    Criteria.where("name").regex(keyword, "i"),
                    Criteria.where("description").regex(keyword, "i"),
                    Criteria.where("tags").regex(keyword, "i")
            );
            criteriaList.add(keywordCriteria);
        }
        if (category != null && !category.trim().isEmpty()) {
            criteriaList.add(Criteria.where("category").is(category));
        }
        if (brand != null && !brand.trim().isEmpty()) {
            criteriaList.add(Criteria.where("brand").is(brand));
        }

        if (minPrice != null) {
            criteriaList.add(Criteria.where("price").gte(minPrice));
        }
        if (maxPrice != null) {
            criteriaList.add(Criteria.where("price").lte(maxPrice));
        }

        return getProducts(pageable, criteriaList);

    }

    @NonNull
    private Page<Product> getProducts(Pageable pageable, List<Criteria> criteriaList) {
        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);

        long total = mongoTemplate.count(query, Product.class);
        query.with(pageable);

        List<Product> products = mongoTemplate.find(query, Product.class);

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Page<Product> findSimilarProduct(String productId, Pageable pageable) {
        Product currentProduct = mongoTemplate.findById(productId, Product.class);
        if (currentProduct == null) {
            return Page.empty();
        }
        List<Criteria> criteriaList = new ArrayList<>();

        criteriaList.add(Criteria.where("published").is(true));

        criteriaList.add(Criteria.where("_id").ne(productId));

        if (currentProduct.getBrand() != null) {
            criteriaList.add(
                    new Criteria().orOperator(
                            Criteria.where("brand").regex("^" + currentProduct.getBrand() + "$", "i"),
                            new Criteria()
                    )
            );
        }
        criteriaList.add(Criteria.where("category").regex("^" + currentProduct.getCategory() + "$", "i"));

        if (currentProduct.getPrice() != null) {
            double price = currentProduct.getPrice();
            double min = price * 0.8;
            double max = price * 1.2;

            criteriaList.add(
                    Criteria.where("price").gte(min).lte(max)
            );
        }
        return getProducts(pageable, criteriaList);

    }
}
