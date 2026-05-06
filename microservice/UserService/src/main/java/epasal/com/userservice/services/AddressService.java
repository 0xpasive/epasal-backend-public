package epasal.com.userservice.services;

import epasal.com.userservice.dtos.requests.AddressExists;
import epasal.com.userservice.dtos.requests.CreateAddressRequest;
import epasal.com.userservice.dtos.requests.UpdateAddressRequest;
import epasal.com.userservice.dtos.response.AddressResponse;

import java.util.List;

public interface AddressService {
    AddressResponse addAddress(CreateAddressRequest createAddressRequest);

    List<AddressResponse> getAddress();

    AddressResponse getAddressById(String addressId);

    AddressResponse editAddress(String addressId, UpdateAddressRequest updateAddressRequest);

    Boolean addressExists(AddressExists addressExists);

    void deleteAddress(String addressId);

}
