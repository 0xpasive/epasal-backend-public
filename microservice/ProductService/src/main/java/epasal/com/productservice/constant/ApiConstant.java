package epasal.com.productservice.constant;

public class ApiConstant {
    public static final String BASE_API = "/api/v1/products";
    public static final String GET_PRODUCT_BY_ID = "/{id}";

    public static final String UPDATE_PRODUCT = "/update/{id}";
    public static final String DELETE_PRODUCT = "/delete/{id}";
    public static final String PRODUCT_EXISTS = "/exists/{id}";

    public static final String ADD_REVIEW = "/review";
    public static final String GET_REVIEWS = "/review/all/{productId}";
    public static final String DELETE = "/review/{productId}";
    public static final String SEARCH = "/search";
    public static final String SIMILAR = "/similar/{id}";
    public static final String DELETE_USER_REVIEW = "/admin/delete/review";

    public static final String CATEGORY = "/category";
    public static final String BRAND = "/brand";

    public static final String FEATURED = "/featured";

    public static final String UNPUBLISH = "/unpublish";
    public static final String REPUBLISH = "/republish";

    public static final String BATCH_PRODUCTS = "/api/v1/products/batch";
    public static final String BATCH_UNPUBLISH = "/unpublish";
    public static final String BATCH_PUBLISH = "/republish";
    public static final String BATCH_DELETE = "/delete";

}
