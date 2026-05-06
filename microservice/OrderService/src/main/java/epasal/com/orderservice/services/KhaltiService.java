package epasal.com.orderservice.services;


import epasal.com.orderservice.dto.requests.KhaltiLookup;
import epasal.com.orderservice.dto.requests.KhaltiPaymentPayload;
import epasal.com.orderservice.dto.response.KhaltiLookupResponse;
import epasal.com.orderservice.dto.response.KhaltiPaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
@FeignClient(url = "${khalti.url}", name = "khalti-service")
public interface KhaltiService {


    @PostMapping("/epayment/lookup/")
    KhaltiLookupResponse khaltiLookUp(@RequestBody KhaltiLookup khaltiLookup,
                                      @RequestHeader("Authorization") String token);

    @PostMapping("/epayment/initiate/")
    KhaltiPaymentResponse initiateKhaltiPayment(@RequestBody KhaltiPaymentPayload khaltiPaymentPayload,
                                                @RequestHeader("Authorization") String token);
}
