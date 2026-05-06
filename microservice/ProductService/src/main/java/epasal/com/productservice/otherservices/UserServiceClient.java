package epasal.com.productservice.otherservices;


import epasal.com.productservice.dto.response.ApiResponse;
import epasal.com.productservice.otherservices.dto.userFullNameResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient(name = "USERSERVICE")
public interface UserServiceClient {
    @GetMapping("/api/v1/auth/{id}")
    ApiResponse<userFullNameResponse> getUserFullName(@PathVariable String id);
}
