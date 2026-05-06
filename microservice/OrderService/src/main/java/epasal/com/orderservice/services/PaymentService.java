package epasal.com.orderservice.services;

import epasal.com.orderservice.Entity.Order;
import epasal.com.orderservice.dto.requests.KhaltiLookup;
import epasal.com.orderservice.dto.requests.KhaltiPaymentPayload;
import epasal.com.orderservice.dto.response.DecodedResponse;
import epasal.com.orderservice.dto.response.EsewaResponse;
import epasal.com.orderservice.dto.response.KhaltiLookupResponse;
import epasal.com.orderservice.dto.response.KhaltiPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.utils.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final KhaltiService khaltiService;
    @Value("${payment.service.esewa.secret_key}")
    private String secret;
    @Value("${khalti.secret.key}")
    private String KhaltiSecretKey;
    String token = "Key " + KhaltiSecretKey;
    @Value("${khalti.website_url}")
    private String return_url;
    @Value("${khalti.website_url}")
    private String website_url;

    public String generateHash(String secret, String message) throws InvalidKeyException, NoSuchAlgorithmException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
    }

    public EsewaResponse esewaResponse(Order order) throws NoSuchAlgorithmException, InvalidKeyException {
        String message = "total_amount=" + order.getTotalAmount() + ",transaction_uuid=" + order.getTransactionId() + ",product_code=" + "EPAYTEST";
        String signature = generateHash(secret, message);
        return EsewaResponse.builder()
                .amount(String.valueOf(order.getTotalAmount()))
                .signature(signature)
                .signed_field_names("total_amount,transaction_uuid,product_code")
                .total_amount(String.valueOf(order.getTotalAmount()))
                .transaction_uuid(order.getTransactionId())
                .build();
    }

    public DecodedResponse decodeMessage(String data) {
        byte[] decodedData = Base64.decodeBase64(data);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(decodedData, DecodedResponse.class);
    }

    public KhaltiPaymentResponse initiateKhaltiPayment(Order order) {
        int amountInPaisa = (int) (order.getTotalAmount() * 100);
        KhaltiPaymentPayload payload = KhaltiPaymentPayload.builder()
                .return_url(return_url)
                .website_url(website_url)
                .amount(amountInPaisa)
                .purchase_order_id(order.getId())
                .purchase_order_name("Epasal")
                .build();
        log.info("token: {}", "key " + KhaltiSecretKey);
        return khaltiService.initiateKhaltiPayment(payload, "Key " + KhaltiSecretKey);
    }

    public KhaltiLookupResponse khaltiLookup(String pidx) {
        KhaltiLookup data = KhaltiLookup.builder()
                .pidx(pidx)
                .build();
        return khaltiService.khaltiLookUp(data, "Key " + KhaltiSecretKey);
    }


}
