package epasal.com.userservice.controller;

import epasal.com.userservice.dtos.requests.AuthRequest;
import epasal.com.userservice.dtos.requests.CreateUserRequest;
import epasal.com.userservice.dtos.requests.UpdatePasswordRequest;
import epasal.com.userservice.dtos.requests.UpdateUserRequest;
import epasal.com.userservice.dtos.response.*;
import epasal.com.userservice.services.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import static epasal.com.userservice.constant.ApiConstant.*;

@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
@EnableMethodSecurity
@Tag(name = "User", description = "Endpoints for user management.")
public class UserController {

    private final UserService userService;

    @PostMapping(REGISTER)
    @Operation(summary = "User registration")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody CreateUserRequest createUserRequest) {
        RegisterResponse response = userService.register(createUserRequest);
        return ApiResponse.success("User registered successfully", response);
    }

    @PostMapping(LOGIN)
    @Operation(summary = "User login")
    public ApiResponse<UserResponse> login(@Valid @RequestBody AuthRequest authRequest, HttpServletResponse httpServletResponse) {
        AuthResponse response = userService.login(authRequest);

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", response.refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ApiResponse.success("Login successful", response.userResponse);
    }

    @GetMapping(MY_DETAILS)
    @Operation(summary = "Get User Details")
    public ApiResponse<UserResponse> myDetails() {
        return ApiResponse.success("User Details Fetched Successfully.", userService.myDetails());
    }

    @PutMapping(UPDATE_PASSWORD)
    @Operation(summary = "Update User Password")
    public ApiResponse<Void> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        userService.updatePassword(updatePasswordRequest);
        return ApiResponse.success("Password Updated Successfully.", null);
    }

    @PatchMapping(MY_DETAILS)
    @Operation(summary = "Update User Information")
    public ApiResponse<UserResponse> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        return ApiResponse.success("User Updated Successfully.", userService.updateUser(updateUserRequest));
    }

    @PostMapping(REFRESH_TOKEN)
    @Operation(summary = "Refresh JWT Token")
    public ApiResponse<UserResponse> refreshToken(@CookieValue("refreshToken") String tokenValue, HttpServletResponse httpServletResponse) {
        AuthResponse response = userService.refreshToken(tokenValue);

        Cookie cookie = new Cookie("refreshToken", response.refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", response.refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ApiResponse.success("Token refreshed successfully", response.userResponse);
    }

    @PostMapping(LOGOUT)
    @Operation(summary = "User logout")
    public ApiResponse<Void> logout(@CookieValue("refreshToken") String tokenValue) {
        userService.logout(tokenValue);
        return ApiResponse.success("Logout successful", null);
    }

    @Hidden
    @GetMapping(FULLNAME)
    public ApiResponse<userFullNameResponse> getUserFullName(@PathVariable String id) {
        userFullNameResponse response = userService.getUserFullName(id);
        return ApiResponse.success("User full name fetched successfully", response);
    }

    @Hidden
    @GetMapping(EMAIL)
    public ApiResponse<UserEmailResponse> getUserEmail(@PathVariable String id) {
        UserEmailResponse response = userService.getUserEmail(id);
        return ApiResponse.success("User email fetched successfully", response);
    }


}
