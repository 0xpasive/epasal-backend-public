package epasal.com.userservice.services.impl;

import epasal.com.userservice.dtos.requests.AdminCode;
import epasal.com.userservice.dtos.requests.AdminLogin;
import epasal.com.userservice.dtos.response.AddressResponse;
import epasal.com.userservice.dtos.response.AdminResponse;
import epasal.com.userservice.dtos.response.UserResponse;
import epasal.com.userservice.entity.Address;
import epasal.com.userservice.entity.Admin;
import epasal.com.userservice.entity.RefreshTokens;
import epasal.com.userservice.entity.User;
import epasal.com.userservice.exception.UnauthorizedException;
import epasal.com.userservice.mapper.AddressMapper;
import epasal.com.userservice.mapper.UserResponseMapper;
import epasal.com.userservice.repo.AddressRepository;
import epasal.com.userservice.repo.AdminRepository;
import epasal.com.userservice.repo.TokenRepository;
import epasal.com.userservice.repo.UserRepository;
import epasal.com.userservice.services.AdminService;
import epasal.com.userservice.services.JwtService;
import epasal.com.userservice.services.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserResponseMapper userResponseMapper;
    private final AddressMapper addressMapper;

    @Override
    public void generateAdminCode(AdminCode adminCode) {
        log.info("Request Received to generate admin code for admin with email: {}", adminCode.getEmail());
        Admin admin = adminRepository.findByEmail(adminCode.getEmail()).orElse(null);
        if (admin == null) {
            log.warn("Admin with email: {} not found. Cannot generate code.", adminCode.getEmail());
            return;
        }
        String code = generateCode();
        admin.setCode(code);
        adminRepository.save(admin);

        log.info("Admin code generated for admin with email: {}. Code: {}", adminCode.getEmail(), code);
        log.debug("Admin code generation process completed for admin with email: {}", adminCode.getEmail());
    }

    @Override
    @Transactional
    public AdminResponse adminLogin(AdminLogin adminlogin) {
        log.info("Admin login Attempt for email={}", adminlogin.getEmail());
        Admin admin = adminRepository.findByEmail(adminlogin.getEmail()).orElseThrow(() -> {
            log.warn("Admin login failed for email={}. Admin not found.", adminlogin.getEmail());
            return new UnauthorizedException("Invalid Credentials");});
        if (!admin.getCode().equals(adminlogin.getCode())) {
            log.warn("Admin login failed for email={}. Invalid code provided.", adminlogin.getEmail());
            throw new UnauthorizedException("Invalid Credentials");
        }
        log.info("Credentials Verified Successfully");
        User user = userRepository.findByEmail(admin.getEmail()).orElseGet(() -> newAdminUser(admin.getEmail()));

        RefreshTokens refreshToken = tokenRepository.findByUser(user)
                .orElseGet(() -> tokenService.generateToken(user));

        log.info("Generating token for admin");
        String token = jwtService.jwtCreate(user);

        return AdminResponse.builder()
                .email(admin.getEmail())
                .token(token)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    public UserResponse getUserDetails(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElse(null);
        if (user == null) {
            log.warn("User with email: {} not found.", userId);
            return null;
        }
        return userResponseMapper.entityToResponse(user);
    }

    @Override
    public AddressResponse getAddressDetails(String addressId) {
        Address address = addressRepository.findById(UUID.fromString(addressId)).orElse(null);
        if (address == null) {
            log.warn("Address with id: {} not found.", addressId);
            return null;
        }
        return addressMapper.toResponse(address);
    }

    public User newAdminUser(String email) {
        log.info("Creating new Admin user with email={}", email);
        User user = new User();
        user.setEmail(email);
        user.setFullName("admin epasal");
        user.setRoles(Set.of(User.Role.ROLE_ADMIN));
        userRepository.save(user);
        log.info("Admin created Successfully with email: {}", email);
        return user;
    }

    public String generateCode() {
        log.info("Generating Code.");
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }

}
