package epasal.com.orderservice.otherservices.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressExists {
    private String addressId;
    private String userId;
}
