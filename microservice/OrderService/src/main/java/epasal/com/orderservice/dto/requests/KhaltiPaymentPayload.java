package epasal.com.orderservice.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KhaltiPaymentPayload {
    private String return_url;
    private String website_url;
    private int amount;
    private String purchase_order_id;
    private String purchase_order_name;
}
