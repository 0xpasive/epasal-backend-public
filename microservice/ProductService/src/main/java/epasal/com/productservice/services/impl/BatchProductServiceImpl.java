package epasal.com.productservice.services.impl;

import epasal.com.productservice.dto.request.BatchProducts;
import epasal.com.productservice.dto.response.BatchProductResponse;
import epasal.com.productservice.dto.response.ProductResponse;
import epasal.com.productservice.entity.Product;
import epasal.com.productservice.mapper.Mapper;
import epasal.com.productservice.repo.ProductRepository;
import epasal.com.productservice.services.BatchProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchProductServiceImpl implements BatchProductService {

    private final ProductRepository productRepository;
    private final Mapper mapper;

    @Override
    public BatchProductResponse getBatchProducts(BatchProducts batchProducts) {
        List<String> productIds = batchProducts.getProductIds();
        log.info("Received batch product request for {} IDs", productIds.size());

        Set<String> uniqueIds = new HashSet<>(productIds);
        List<Product> products = productRepository.findAllById(uniqueIds);

        Set<String> foundIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        List<String> notFoundIds = uniqueIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            log.warn("Batch request: {} product(s) not found: {}", notFoundIds.size(), notFoundIds);
        }

        List<ProductResponse> productResponses = products.stream()
                .map(mapper::toResponse)
                .toList();

        return BatchProductResponse.builder()
                .products(productResponses)
                .notFoundProductIds(notFoundIds)
                .totalProductsFound(products.size())
                .totalProductsNotFound(notFoundIds.size())
                .build();
    }

    @Override
    public void unpublishBatchProducts(BatchProducts batchProducts) {
        log.info("Unpublishing Batch Proudcts with IDs: {}", batchProducts.getProductIds());
        List<Product> products = fetchBatchProducts(batchProducts);
        for (Product product: products){
            product.setPublished(false);
            productRepository.save(product);
            log.debug("Unpublished product with ID: {}", product.getId());
        }
    }

    @Override
    public void publishBatchProducts(BatchProducts batchProducts) {
        log.info("Publishing Batch Proudcts with IDs: {}", batchProducts.getProductIds());
        List<Product> products = fetchBatchProducts(batchProducts);
        for (Product product: products){
            product.setPublished(true);
            productRepository.save(product);
            log.debug("Published product with ID: {}", product.getId());
        }

    }

    @Override
    public void deleteBatchProducts(BatchProducts batchProducts) {
        log.info("Deleting Batch Proudcts with IDs: {}", batchProducts.getProductIds());
        List<Product> products = fetchBatchProducts(batchProducts);
        log.debug("Deleted {} products", batchProducts.getProductIds().size());
        productRepository.deleteAll(products);
    }

    public List<Product> fetchBatchProducts(BatchProducts batchProducts) {
        List<String> productIds = batchProducts.getProductIds();
        log.info("Received batch product request for {} IDs", productIds.size());

        Set<String> uniqueIds = new HashSet<>(productIds);
        return productRepository.findAllById(uniqueIds);
    }

}
