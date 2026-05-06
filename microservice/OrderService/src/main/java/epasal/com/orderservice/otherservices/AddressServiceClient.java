package epasal.com.orderservice.otherservices;

import epasal.com.orderservice.otherservices.dto.AddressExists;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "USERSERVICE")
public interface AddressServiceClient {
    @PostMapping("/api/v1/auth/address/exists")
    boolean addressExists(@RequestBody AddressExists addressExists);
}
