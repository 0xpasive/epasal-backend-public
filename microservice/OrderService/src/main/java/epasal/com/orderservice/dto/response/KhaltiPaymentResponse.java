package epasal.com.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KhaltiPaymentResponse {
    private String pidx;
    private String payment_url;
    private String expires_at;
    private int expires_in;
}
