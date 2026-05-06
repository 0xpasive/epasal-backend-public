package epasal.com.productservice.controller;

import epasal.com.productservice.dto.request.BatchProducts;
import epasal.com.productservice.dto.response.ApiResponse;
import epasal.com.productservice.dto.response.BatchProductResponse;
import epasal.com.productservice.services.BatchProductService;
import epasal.com.productservice.services.ProductService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import static epasal.com.productservice.constant.ApiConstant.*;

@RestController
@RequestMapping(BATCH_PRODUCTS)
@Slf4j
@RequiredArgsConstructor
class BatchProductController {
    private final BatchProductService batchProductService;
    @Hidden
    @PostMapping()
    public ApiResponse<BatchProductResponse> getBatchProducts(@RequestBody BatchProducts batchProducts) {
        log.info("Batch products requested for IDs: {}", batchProducts.getProductIds());
        BatchProductResponse response = batchProductService.getBatchProducts(batchProducts);
        return ApiResponse.success("Batch products retrieved successfully", response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(BATCH_UNPUBLISH)
    public ApiResponse<Void> unpublishBatchProducts(@RequestBody BatchProducts batchProducts) {
        batchProductService.unpublishBatchProducts(batchProducts);
        return ApiResponse.success("Batch products unpublished successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(BATCH_PUBLISH)
    public ApiResponse<Void> publishBatchProducts(@RequestBody BatchProducts batchProducts) {
        batchProductService.publishBatchProducts(batchProducts);
        return ApiResponse.success("Batch products published successfully", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(BATCH_DELETE)
    public ApiResponse<Void> deleteBatchProducts(@RequestBody BatchProducts batchProducts) {
        batchProductService.deleteBatchProducts(batchProducts);
        return ApiResponse.success("Selected Products Deleted Successfully", null);
    }




}
