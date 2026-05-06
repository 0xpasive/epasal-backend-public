package epasal.com.userservice.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminResponse {
    private String email;
    private String token;
    private String refreshToken;
}
