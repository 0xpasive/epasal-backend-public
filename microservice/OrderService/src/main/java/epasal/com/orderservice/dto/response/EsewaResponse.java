package epasal.com.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EsewaResponse {
    private String amount;
    private String signature;
    private String signed_field_names;
    private String total_amount;
    private String transaction_uuid;
}
