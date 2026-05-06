package epasal.com.userservice.controller;

import epasal.com.userservice.dtos.requests.AddressExists;
import epasal.com.userservice.dtos.requests.CreateAddressRequest;
import epasal.com.userservice.dtos.requests.UpdateAddressRequest;
import epasal.com.userservice.dtos.response.AddressResponse;
import epasal.com.userservice.dtos.response.ApiResponse;
import epasal.com.userservice.services.AddressService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static epasal.com.userservice.constant.ApiConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_API)
public class AddressController {
    private final AddressService addressService;


    @PreAuthorize("hasRole('USER')")
    @PostMapping(ADDRESS)
    public ApiResponse<AddressResponse> addAddress(@RequestBody CreateAddressRequest createAddressRequest) {
        AddressResponse response = addressService.addAddress(createAddressRequest);
        return ApiResponse.success("Address added successfully", response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(ADDRESS)
    public ApiResponse<List<AddressResponse>> getAddress() {
        List<AddressResponse> response = addressService.getAddress();
        return ApiResponse.success("Address fetched successfully", response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(ADDRESS_ID)
    public ApiResponse<AddressResponse> getAddressById(@PathVariable String id) {
        AddressResponse response = addressService.getAddressById(id);
        return ApiResponse.success("Address fetched successfully", response);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping(ADDRESS_ID)
    public ApiResponse<AddressResponse> editAddress(@PathVariable String id, @RequestBody UpdateAddressRequest updateAddressRequest) {
        AddressResponse response = addressService.editAddress(id, updateAddressRequest);
        return ApiResponse.success("Address updated successfully", response);
    }

    @Hidden
    @PostMapping(ADDRESS_EXISTS)
    public Boolean addressExists(@RequestBody AddressExists addressExists) {
        return addressService.addressExists(addressExists);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping(DELETE)
    public ApiResponse<Void> deleteAddress(@PathVariable String id) {
        addressService.deleteAddress(id);
        return ApiResponse.success("Address deleted successfully", null);
    }


}
