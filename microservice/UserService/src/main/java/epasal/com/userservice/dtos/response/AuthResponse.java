package epasal.com.userservice.dtos.response;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    public UserResponse userResponse;

    public String refreshToken;
}
