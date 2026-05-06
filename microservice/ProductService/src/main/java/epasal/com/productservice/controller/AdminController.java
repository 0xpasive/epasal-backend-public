package epasal.com.productservice.controller;

import epasal.com.productservice.dto.request.*;
import epasal.com.productservice.dto.response.ApiResponse;
import epasal.com.productservice.dto.response.PageResponse;
import epasal.com.productservice.dto.response.ProductResponse;
import epasal.com.productservice.services.ProductService;
import epasal.com.productservice.services.RatingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static epasal.com.productservice.constant.ApiConstant.*;

@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
@EnableMethodSecurity
@Slf4j
class AdminController {
    private final ProductService productService;
    private final RatingService ratingService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> addProduct(@ModelAttribute CreateProduct createProduct, @RequestPart("image") MultipartFile image) {
        log.info("Adding product: {}", createProduct.getName());
        ProductResponse response = productService.addProduct(createProduct, image);
        return ApiResponse.success("Product added successfully", response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(path = UPDATE_PRODUCT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> updateProduct(@PathVariable String id, @ModelAttribute UpdateProduct updateProduct, @RequestPart(value = "image", required = false) MultipartFile image) {
        ProductResponse response = productService.updateProduct(id, updateProduct, image);
        return ApiResponse.success("Product updated successfully", response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(DELETE_PRODUCT)
    public ApiResponse<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ApiResponse.success("Product deleted successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(CATEGORY)
    public ApiResponse<Void> addCategory(@RequestParam String name) {
        productService.addCategory(name);
        return ApiResponse.success("Category added successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(BRAND)
    public ApiResponse<Void> addBrand(@RequestParam String name) {
        productService.addBrand(name);
        return ApiResponse.success("Brand added successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(BRAND)
    public ApiResponse<Void> deleteBrand(@RequestParam String name) {
        productService.deleteBrand(name);
        return ApiResponse.success("Brand deleted successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(FEATURED)
    public ApiResponse<Void> addFeaturedProduct(@RequestBody AddFeaturedProduct addFeaturedProduct) {
        productService.addFeaturedProduct(addFeaturedProduct.getProductId());
        return ApiResponse.success("Product added to featured list successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(FEATURED)
    public ApiResponse<Void> deleteFeaturedProduct(@RequestBody DeleteFeaturedProduct deleteFeaturedProduct) {
        productService.deleteFeaturedProduct(deleteFeaturedProduct.getProductId());
        return ApiResponse.success("Product removed from featured list successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(UNPUBLISH)
    public ApiResponse<Void> unpublishProduct(@RequestParam String productId){
        productService.unPublishProduct(productId);
        return ApiResponse.success("Product unpublished successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UNPUBLISH)
    public ApiResponse<PageResponse> getUnpublishedProducts(Pageable pageable) {
        PageResponse products = productService.getUnPublishedProducts(pageable);
        return ApiResponse.success("Products Fetched Successfully", products);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(REPUBLISH)
    public ApiResponse<Void> republishProduct(@RequestParam String productId){
        productService.rePublish(productId);
        return ApiResponse.success("Product republished successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(CATEGORY)
    public ApiResponse<Void> deleteCategory(@RequestParam String name) {
        productService.deleteCategory(name);
        return ApiResponse.success("Category deleted successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(DELETE_USER_REVIEW)
    public ApiResponse<Void> deleteUserReview(@RequestBody DeleteUserReview deleteUserReview){
        ratingService.deleteUserReview(deleteUserReview);
        return ApiResponse.success("User review deleted successfully", null);
    }

}
