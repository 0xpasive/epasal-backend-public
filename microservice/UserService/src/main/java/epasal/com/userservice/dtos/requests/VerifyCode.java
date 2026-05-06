package epasal.com.userservice.dtos.requests;

import epasal.com.userservice.entity.Codes;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCode {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must be less than 255 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$", message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Type is required")
    private Codes.Type type;


    private String newPassword;
}
