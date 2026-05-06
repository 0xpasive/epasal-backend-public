package epasal.com.userservice.services;


import epasal.com.userservice.dtos.requests.AuthRequest;
import epasal.com.userservice.dtos.requests.CreateUserRequest;
import epasal.com.userservice.dtos.requests.UpdatePasswordRequest;
import epasal.com.userservice.dtos.requests.UpdateUserRequest;
import epasal.com.userservice.dtos.response.*;


public interface UserService {
    RegisterResponse register(CreateUserRequest createUserRequest);

    AuthResponse login(AuthRequest authRequest);

    UserResponse myDetails();

    void updatePassword(UpdatePasswordRequest updatePasswordRequest);

    UserResponse updateUser(UpdateUserRequest updateUserRequest);

    AuthResponse refreshToken(String tokenValue);

    void logout(String tokenValue);


    userFullNameResponse getUserFullName(String userId);
    UserEmailResponse getUserEmail(String userId);


}
