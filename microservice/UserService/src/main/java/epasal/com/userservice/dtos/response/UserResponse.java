package epasal.com.userservice.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private String token;

    private UUID id;

    @Getter(AccessLevel.NONE)
    private String email;

    private String fullName;

    private boolean verified;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public String getEmail() {
        return censorEmail(this.email);
    }

    private String censorEmail(String email) {
        if (email == null || !email.contains("@")) return email;

        String[] parts = email.split("@");
        String censoredLocal = parts[0].substring(0, 2) + "****";

        return censoredLocal + "@" + parts[1];
    }


}
