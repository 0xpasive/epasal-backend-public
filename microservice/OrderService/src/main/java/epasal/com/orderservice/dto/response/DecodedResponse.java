package epasal.com.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecodedResponse {
    private String transaction_code;
    private String status;
    private String total_amount;
    private String transaction_uuid;
    private String product_code;
    private String signed_field_names;
    private String signature;
}
