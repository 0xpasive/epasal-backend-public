package epasal.com.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "codes")
public class Codes {
    @Id
    private String userId;

    private String code;

    @Enumerated(EnumType.STRING)
    private Type type;

    private LocalDateTime generatedAt;

    private LocalDateTime expiresAt;

    public enum Type {
        VERIFICATION,
        PASSWORD_RESET
    }
}
