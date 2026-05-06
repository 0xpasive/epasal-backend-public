package epasal.com.productservice.controller;


import epasal.com.productservice.dto.request.*;
import epasal.com.productservice.dto.response.ApiResponse;
import epasal.com.productservice.dto.response.PageResponse;
import epasal.com.productservice.dto.response.ProductResponse;
import epasal.com.productservice.entity.Brand;
import epasal.com.productservice.entity.Category;
import epasal.com.productservice.services.ProductService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static epasal.com.productservice.constant.ApiConstant.*;

@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
@EnableMethodSecurity
@Slf4j
public class ProductController {
    private final ProductService productService;

    @GetMapping()
    public ApiResponse<PageResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        PageResponse productsPage = productService.getAllProducts(page, size, sortBy, direction);
        return ApiResponse.success("Products retrieved successfully", productsPage);
    }

    @GetMapping(GET_PRODUCT_BY_ID)
    public ApiResponse<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse product = productService.getProductById(id);
        return ApiResponse.success("Product retrieved successfully", product);
    }

    @GetMapping(SEARCH)
    public ApiResponse<PageResponse> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable
    ) {
        PageResponse productsPage = productService.searchProducts(keyword, category, brand, minPrice, maxPrice, pageable);
        return ApiResponse.success("Search results retrieved successfully", productsPage);
    }

    @GetMapping(SIMILAR)
    public ApiResponse<PageResponse> findSimilarProducts(@PathVariable String id, Pageable pageable) {
        PageResponse response = productService.getSimilarProducts(id, pageable);
        return ApiResponse.success("Similar products retrieved successfully", response);
    }

    @GetMapping(CATEGORY)
    public ApiResponse<List<Category>> getAllCategory() {
        List<Category> categories = productService.getAllCategory();
        return ApiResponse.success("Categories retrieved successfully", categories);
    }

    @GetMapping(BRAND)
    public ApiResponse<List<Brand>> getAllBrands() {
        List<Brand> brands = productService.getAllBrands();
        return ApiResponse.success("Brands retrieved successfully", brands);
    }

    @GetMapping(FEATURED)
    public ApiResponse<List<ProductResponse>> getFeaturedProduct() {
        return ApiResponse.success("Featured products retrieved successfully", productService.getFeaturedProducts());
    }

    //For Service to Service Communication

    @Hidden
    @GetMapping(PRODUCT_EXISTS)
    public Boolean productExists(@PathVariable String id) {
        return productService.productExists(id);
    }



}
