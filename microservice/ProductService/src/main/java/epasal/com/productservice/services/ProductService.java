package epasal.com.productservice.services;

import epasal.com.productservice.dto.request.BatchProducts;
import epasal.com.productservice.dto.request.CreateProduct;
import epasal.com.productservice.dto.request.UpdateProduct;
import epasal.com.productservice.dto.response.BatchProductResponse;
import epasal.com.productservice.dto.response.PageResponse;
import epasal.com.productservice.dto.response.ProductResponse;
import epasal.com.productservice.entity.Brand;
import epasal.com.productservice.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse addProduct(CreateProduct createProduct, MultipartFile file);

    PageResponse getAllProducts(int page, int size, String sortBy, String direction);

    ProductResponse getProductById(String id);

    ProductResponse updateProduct(String id, UpdateProduct updateProduct, MultipartFile file);

    void deleteProduct(String id);


    Boolean productExists(String id);




    void addCategory(String category);

    void deleteCategory(String category);

    void addBrand(String brand);

    void deleteBrand(String brand);

    List<Brand> getAllBrands();

    List<Category> getAllCategory();

    PageResponse searchProducts(
            String keyword,
            String category,
            String brand,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );

    PageResponse getSimilarProducts(String productId, Pageable pageable);


    List<ProductResponse> getFeaturedProducts();

    void addFeaturedProduct(String productId);

    void deleteFeaturedProduct(String productId);

    void unPublishProduct(String productId);

    void rePublish(String productId);

    PageResponse getUnPublishedProducts(Pageable pageable);

}
