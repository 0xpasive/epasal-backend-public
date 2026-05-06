package epasal.com.userservice.dtos.requests;

import lombok.Data;

@Data
public class AdminLogin {
    private String email;
    private String code;
}
