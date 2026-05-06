package epasal.com.userservice.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private UUID id;

    private UUID userId;

    private String street;

    private String city;

    private String province;

    private String country;

    private String postalCode;

    private String phoneNumber;

    private boolean isPrimary;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
