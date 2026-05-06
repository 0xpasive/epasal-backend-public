package epasal.com.userservice.controller;

import epasal.com.userservice.dtos.requests.AdminCode;
import epasal.com.userservice.dtos.requests.AdminLogin;
import epasal.com.userservice.dtos.response.AddressResponse;
import epasal.com.userservice.dtos.response.AdminResponse;
import epasal.com.userservice.dtos.response.ApiResponse;
import epasal.com.userservice.dtos.response.UserResponse;
import epasal.com.userservice.services.AdminService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static epasal.com.userservice.constant.ApiConstant.*;

@RestController
@RequestMapping(ADMIN)
@RequiredArgsConstructor
class AdminController {
    private final AdminService adminService;


    @PostMapping(ADMIN_CODE)
    public ApiResponse<Void> getAdminCode(@RequestBody AdminCode adminCode) {
        adminService.generateAdminCode(adminCode);
        return ApiResponse.success("Code Generated Successfully", null);
    }


    @PostMapping(ADMIN_lOGIN)
    public ApiResponse<AdminResponse> adminLogin(@RequestBody AdminLogin adminLogin, HttpServletResponse httpServletResponse) {
        AdminResponse response = adminService.adminLogin(adminLogin);

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", response.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        response.setRefreshToken(null);

        return ApiResponse.success("Login success", response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(GET_USER_DETAILS)
    public ApiResponse<UserResponse> getUserDetails(@PathVariable String id) {
        UserResponse userResponse = adminService.getUserDetails(id);
        return ApiResponse.success("User details retrieved successfully", userResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(GET_ADDRESS)
    public ApiResponse<AddressResponse> getAddress(@PathVariable String id) {
        AddressResponse addressResponse = adminService.getAddressDetails(id);
        return ApiResponse.success("Address details retrieved successfully", addressResponse);
    }

}
