package epasal.com.productservice.services;

import epasal.com.productservice.dto.request.BatchProducts;
import epasal.com.productservice.dto.response.BatchProductResponse;

public interface BatchProductService {
    BatchProductResponse getBatchProducts(BatchProducts batchProducts);

    void unpublishBatchProducts(BatchProducts batchProducts);
    void publishBatchProducts(BatchProducts batchProducts);

    void deleteBatchProducts(BatchProducts batchProducts);
}
