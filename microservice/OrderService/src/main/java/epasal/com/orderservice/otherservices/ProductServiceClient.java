package epasal.com.orderservice.otherservices;

import epasal.com.orderservice.dto.response.ApiResponse;
import epasal.com.orderservice.otherservices.dto.BatchProductResponse;
import epasal.com.orderservice.otherservices.dto.BatchProducts;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "PRODUCTSERVICE")
public interface ProductServiceClient {
    @GetMapping("/api/v1/products/exists/{id}")
    Boolean productExists(@PathVariable String id);

    @PostMapping("/api/v1/products/batch")
    ApiResponse<BatchProductResponse> getBatchProducts(@RequestBody BatchProducts batchProducts);

}
