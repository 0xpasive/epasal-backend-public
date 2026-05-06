package epasal.com.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KhaltiLookupResponse {
    private String pidx;
    private int total_amount;
    private String status;
    private String transaction_id;
    private int fee;
    private boolean refunded;
    private String error_key;
}
