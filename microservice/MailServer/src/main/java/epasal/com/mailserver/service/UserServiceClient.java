package epasal.com.mailserver.service;


import epasal.com.mailserver.dto.ApiResponse;
import epasal.com.mailserver.dto.UserEmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient(name = "USERSERVICE")
public interface UserServiceClient {
    @GetMapping("/api/v1/auth/email/{id}")
    ApiResponse<UserEmailResponse> getUserFullName(@PathVariable String id);
}
