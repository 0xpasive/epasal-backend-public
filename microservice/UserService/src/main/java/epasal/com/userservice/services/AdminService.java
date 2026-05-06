package epasal.com.userservice.services;

import epasal.com.userservice.dtos.requests.AdminCode;
import epasal.com.userservice.dtos.requests.AdminLogin;
import epasal.com.userservice.dtos.response.AddressResponse;
import epasal.com.userservice.dtos.response.AdminResponse;
import epasal.com.userservice.dtos.response.UserResponse;

public interface AdminService {
    void generateAdminCode(AdminCode adminCode);

    AdminResponse adminLogin(AdminLogin adminlogin);

    UserResponse getUserDetails(String userId);

    AddressResponse getAddressDetails(String addressId);

}
