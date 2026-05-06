package epasal.com.userservice.mapper;

import epasal.com.userservice.dtos.requests.CreateAddressRequest;
import epasal.com.userservice.dtos.response.AddressResponse;
import epasal.com.userservice.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressResponse toResponse(Address address);

    Address toEntity(CreateAddressRequest createAddressRequest);

}
