package epasal.com.cartservice.otherservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient(name = "PRODUCTSERVICE")
public interface ProductServiceClient {
    @GetMapping("/api/v1/products/exists/{id}")
    Boolean productExists(@PathVariable String id);

}
