package epasal.com.productservice.services.impl;

import epasal.com.productservice.dto.request.BatchProducts;
import epasal.com.productservice.dto.request.CreateProduct;
import epasal.com.productservice.dto.request.UpdateProduct;
import epasal.com.productservice.dto.response.*;
import epasal.com.productservice.entity.Brand;
import epasal.com.productservice.entity.Category;
import epasal.com.productservice.entity.Product;
import epasal.com.productservice.exception.ProductExistsException;
import epasal.com.productservice.exception.ResourceNotFoundException;
import epasal.com.productservice.mapper.Mapper;
import epasal.com.productservice.otherservices.InventoryServiceClient;
import epasal.com.productservice.otherservices.dto.CreateInventory;
import epasal.com.productservice.repo.BrandRepository;
import epasal.com.productservice.repo.CategoryRepository;
import epasal.com.productservice.repo.ProductRepository;
import epasal.com.productservice.services.CloudinaryService;
import epasal.com.productservice.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_ID_PREFIX = "ep-product-";
    private static final int PRODUCT_ID_SUFFIX_LENGTH = 8;

    private final ProductRepository productRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final CloudinaryService cloudinaryService;
    private final Mapper mapper;

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public ProductResponse addProduct(CreateProduct createProduct, MultipartFile file) {
        log.info("Adding product with name: {}", createProduct.getName());

        validateImageFile(file);
        validateProductNameUniqueness(createProduct.getName());

        Product product = mapper.toEntity(createProduct);
        product.setId(generateProductId());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setImageUrl(uploadProductImage(file, createProduct.getName()));
        product.setPublished(true);

        productRepository.save(product);
        log.debug("Product persisted with id: {}", product.getId());

        CreateInventory createInventory = CreateInventory.builder()
                .productId(product.getId())
                .quantity(createProduct.getQuantity())
                .build();

        try {
            ApiResponse<InventoryResponse> response = inventoryServiceClient.createInventory(createInventory);
            log.info("Response got from Inventory: {}", response);
            product.setInventoryId(String.valueOf(response.getData().getId()));
            log.info("Product created successfully with id: {}", product.getId());
            productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mapper.toResponse(product);
    }

    @Override
    public PageResponse getAllProducts(int page, int size, String sortBy, String direction) {
        log.debug("Fetching products - page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.findAllByPublishedTrue(true, pageable);
        return createPageResponse(productPage);
    }

    @Override
    public ProductResponse getProductById(String id) {
        log.debug("Fetching product by id: {}", id);

        Product product = findProductOrThrow(id);
        return mapper.toResponse(product);
    }

    @Override
    public ProductResponse updateProduct(String id, UpdateProduct updateProduct, MultipartFile file) {
        log.info("Updating product with id: {}", id);

        Product product = findProductOrThrow(id);

        Optional.ofNullable(updateProduct.getName())
                .ifPresent(name -> {
                    if (productRepository.existsByName(name) && !name.equals(product.getName())) {
                        throw new ProductExistsException("Product with the same name already exists");
                    }
                    product.setName(name);
                });

        Optional.ofNullable(updateProduct.getPrice()).ifPresent(product::setPrice);
        Optional.ofNullable(updateProduct.getDiscountPrice()).ifPresent(product::setDiscountPrice);
        Optional.ofNullable(updateProduct.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(updateProduct.getCategory()).ifPresent(product::setCategory);
        Optional.ofNullable(updateProduct.getBrand()).ifPresent(product::setBrand);
        Optional.ofNullable(updateProduct.getTags()).ifPresent(product::setTags);
        Optional.ofNullable(updateProduct.getWarranty()).ifPresent(product::setWarranty);
        Optional.ofNullable(updateProduct.getReturnPolicy()).ifPresent(product::setReturnPolicy);

        if (file != null && !file.isEmpty()) {
            replaceProductImage(product, file);
        }

        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        log.info("Product updated successfully with id: {}", product.getId());
        return mapper.toResponse(product);
    }


    @Override
    public void deleteProduct(String id) {
        log.info("Deleting product with id: {}", id);

        Product product = findProductOrThrow(id);

        try {
            cloudinaryService.deleteImage(product.getName());
        } catch (Exception e) {
            log.error("Failed to delete image for product id: {}", id, e);
            throw new ResourceNotFoundException("Image deletion failed: " + e.getMessage());
        }

        productRepository.delete(product);
        inventoryServiceClient.deleteInventory(id);

        log.info("Product deleted successfully with id: {}", id);
    }

    @Override
    public Boolean productExists(String id) {
        log.debug("Checking existence for product id: {}", id);
        return productRepository.existsById(id);
    }



    @Override
    public void addCategory(String category) {
        Category newCategory = new Category();
        newCategory.setName(category);
        categoryRepository.save(newCategory);
    }

    @Override
    public void deleteCategory(String category) {
        Category existingCategory = categoryRepository.findByName(category);
        categoryRepository.delete(existingCategory);
    }

    @Override
    public void addBrand(String brand) {
        Brand newBrand = new Brand();
        newBrand.setName(brand);
        brandRepository.save(newBrand);
    }

    @Override
    public void deleteBrand(String brand) {
        Brand existingBrand = brandRepository.findByName(brand);
        brandRepository.delete(existingBrand);
    }

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public PageResponse searchProducts(String keyword, String category, String brand, Double minPrice, Double maxPrice, Pageable pageable) {
        Page<Product> searchProducts = productRepository.searchProducts(keyword, category, brand, minPrice, maxPrice, pageable);
        return createPageResponse(searchProducts);
    }

    @Override
    public PageResponse getSimilarProducts(String productId, Pageable pageable) {
        Pageable limitedPageable = PageRequest.of(pageable.getPageNumber(), 4);
        Page<Product> similarProducts = productRepository.findSimilarProduct(productId, limitedPageable);
        return createPageResponse(similarProducts);
    }

    @Override
    public List<ProductResponse> getFeaturedProducts() {
        List<Product> products = productRepository.findAllByFeatured(true);
        return products.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void addFeaturedProduct(String productId) {
        List<Product> products = productRepository.findAllByFeatured(true);
        if (products.size() == 4) {
            throw new ResourceNotFoundException("Featured products limit reached.");
        }
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("No product found"));
        product.setFeatured(true);
        productRepository.save(product);
    }

    @Override
    public void deleteFeaturedProduct(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("No product found"));
        product.setFeatured(false);
        productRepository.save(product);
    }

    @Override
    public void unPublishProduct(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("No product found"));
        if (!product.isPublished()) {
            throw new ResourceNotFoundException("Product is already unpublished");
        }
        product.setPublished(false);
        productRepository.save(product);
    }

    @Override
    public void rePublish(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("No product found"));
        if (product.isPublished()) {
            throw new ResourceNotFoundException("Product is already published");
        }
        product.setPublished(true);
        productRepository.save(product);
    }

    @Override
    public PageResponse getUnPublishedProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAllByPublished(false, pageable);
        return createPageResponse(products);
    }

    //HELPER FUNCTIONS
    private Product findProductOrThrow(String id) {
        return productRepository.findByIdAndPublished(id, true)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResourceNotFoundException("Image file is required");
        }
    }


    private void validateProductNameUniqueness(String name) {
        if (productRepository.existsByName(name)) {
            throw new ProductExistsException("Product with the same name already exists");
        }
    }


    private String uploadProductImage(MultipartFile file, String name) {
        try {
            Map<String, Object> uploadResult = cloudinaryService.uploadImage(file, name);
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            log.error("Image upload failed for product name: {}", name, e);
            throw new ResourceNotFoundException("Image upload failed: " + e.getMessage());
        }
    }

    private void replaceProductImage(Product product, MultipartFile file) {
        try {
            cloudinaryService.deleteImage(product.getName());
            Map<String, Object> uploadResult = cloudinaryService.uploadImage(file, product.getName());
            product.setImageUrl(uploadResult.get("secure_url").toString());
        } catch (Exception e) {
            log.error("Image replacement failed for product id: {}", product.getId(), e);
            throw new ResourceNotFoundException("Image upload failed: " + e.getMessage());
        }
    }


    private String generateProductId() {
        return PRODUCT_ID_PREFIX + UUID.randomUUID().toString().substring(0, PRODUCT_ID_SUFFIX_LENGTH);
    }

    private PageResponse createPageResponse(Page<Product> productPage) {
        return new PageResponse(
                productPage.getContent()
                        .stream()
                        .map(mapper::toResponse)
                        .toList(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );

    }
}
