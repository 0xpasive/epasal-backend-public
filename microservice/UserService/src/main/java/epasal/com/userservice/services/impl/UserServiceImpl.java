package epasal.com.userservice.services.impl;


import epasal.com.userservice.components.CurrentUser;
import epasal.com.userservice.dtos.requests.AuthRequest;
import epasal.com.userservice.dtos.requests.CreateUserRequest;
import epasal.com.userservice.dtos.requests.UpdatePasswordRequest;
import epasal.com.userservice.dtos.requests.UpdateUserRequest;
import epasal.com.userservice.dtos.response.*;
import epasal.com.userservice.entity.RefreshTokens;
import epasal.com.userservice.entity.User;
import epasal.com.userservice.event.UserRegisteredEvent;
import epasal.com.userservice.eventPublisher.RabbitEventPublisher;
import epasal.com.userservice.exception.ResourceNotFoundException;
import epasal.com.userservice.exception.UnVerifiedException;
import epasal.com.userservice.exception.UnauthorizedException;
import epasal.com.userservice.exception.UserAlreadyExistsException;
import epasal.com.userservice.mapper.Mapper;
import epasal.com.userservice.mapper.UserResponseMapper;
import epasal.com.userservice.repo.TokenRepository;
import epasal.com.userservice.repo.UserRepository;
import epasal.com.userservice.services.JwtService;
import epasal.com.userservice.services.RateLimitingService;
import epasal.com.userservice.services.TokenService;
import epasal.com.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final Mapper mapper;
    private final UserResponseMapper userResponseMapper;
    private final TokenService tokenService;
    private final CurrentUser currentUser;
    private final TokenRepository tokenRepository;
    private final RabbitEventPublisher rabbitEventPublisher;
    private final RateLimitingService rateLimitingService;


    private boolean verifyPassword(String password, String encodedPassword) {
        log.debug("Comparing passwords");
        return bCryptPasswordEncoder.matches(password, encodedPassword);
    }


    @Override
    public RegisterResponse register(CreateUserRequest createUserRequest) {
        log.info("Attempting to register user with email: {}", createUserRequest.getEmail());
        User user = mapper.requestToEntity(createUserRequest);

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Registration attempt failed: User with email {} already exists", user.getEmail());
            throw new UserAlreadyExistsException("Cannot Register User");
        }

        log.debug("Creating new user entity for email: {}", user.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        user.setCreatedAt(Date.from(Instant.now()));
        user.setUpdatedAt(Date.from(Instant.now()));
        user.setVerified(false);
        user.setRoles(Set.of(User.Role.ROLE_USER));
        userRepository.save(user);

        log.info("User entity saved successfully for email: {}", user.getEmail());

        log.debug("Publishing UserRegisteredEvent for email: {}", user.getEmail());
        UserRegisteredEvent event = new UserRegisteredEvent(
                user.getEmail(),
                user.getFullName()
        );

        rabbitEventPublisher.publishUserRegistrationEvent(event);

        log.info("User registered successfully with email: {}", user.getEmail());
        return mapper.entityToResponse(user);

    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        log.info("Login attempt for email: {}", authRequest.getEmail());
        rateLimitingService.checkRateLimit(authRequest.getEmail());
        User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow(
                () -> {
                    log.warn("Login attempt failed: User with email {} not found", authRequest.getEmail());
                    return new ResourceNotFoundException("Invalid Request");
                }
        );

        if (!verifyPassword(authRequest.getPassword(), user.getPassword())) {
            log.warn("Login attempt failed: Invalid credentials for email {}", authRequest.getEmail());
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!user.isVerified()) {
            log.warn("Login attempt failed: Unverified email for user with email {}", authRequest.getEmail());
            throw new UnVerifiedException("Please verify your email to continue shopping");
        }

        log.debug("Generating token for user with email : {}", user.getEmail());
        String token = jwtService.jwtCreate(user);
        log.debug("Generating refresh token for user with email : {}", user.getEmail());
        RefreshTokens refreshToken = tokenRepository.findByUser(user).orElseGet(() -> tokenService.generateToken(user));
        tokenRepository.save(refreshToken);
        UserResponse response = userResponseMapper.entityToResponse(user);
        response.setToken(token);
        rateLimitingService.resetLimit(authRequest.getEmail());

        log.info("Login successful for email: {}", authRequest.getEmail());
        return AuthResponse.builder()
                .refreshToken(refreshToken.getToken())
                .userResponse(response)
                .build();
    }

    @Override
    public UserResponse myDetails() {
        log.info("Fetching details for authenticated user with ID: {}", currentUser.getUserId());
        return userResponseMapper.entityToResponse(getAuthenticatedUser());
    }

    @Override
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        log.info("Password update attempt for authenticated user with ID: {}", currentUser.getUserId());
        User user = getAuthenticatedUser();

        if (!verifyPassword(updatePasswordRequest.getCurrentPassword(), user.getPassword())) {
            log.warn("Password update attempt failed: Invalid current password for user with ID: {}", currentUser.getUserId());
            throw new UnauthorizedException("Invalid Credentials");
        }

        log.debug("Updating password for user with ID: {}", currentUser.getUserId());
        user.setPassword(bCryptPasswordEncoder.encode(updatePasswordRequest.getNewPassword()));
        user.setUpdatedAt(Date.from(Instant.now()));
        userRepository.save(user);
        log.info("Password updated successfully for user with ID: {}", currentUser.getUserId());
    }

    @Override
    public UserResponse updateUser(UpdateUserRequest updateUserRequest) {
        log.info("User update attempt for authenticated user with ID: {}", currentUser.getUserId());
        User user = getAuthenticatedUser();

        log.debug("Updating user details for user with ID: {}", currentUser.getUserId());
        user.setFullName(updateUserRequest.getFullName());
        user.setUpdatedAt(Date.from(Instant.now()));
        userRepository.save(user);
        log.info("User details updated successfully for user with ID: {}", currentUser.getUserId());
        return userResponseMapper.entityToResponse(user);
    }


    public AuthResponse refreshToken(String tokenValue) {
        log.info("Refresh token attempt with token: {}", tokenValue);
        if (!tokenService.validateToken(tokenValue)) {
            log.warn("Refresh token attempt failed: Invalid token {}", tokenValue);
            throw new ResourceNotFoundException("Invalid token");
        } else {
            RefreshTokens refreshToken = tokenRepository.findByToken(tokenValue).orElseThrow(
                    () -> {
                        log.warn("Refresh token attempt failed: Token not found {}", tokenValue);
                        return new ResourceNotFoundException("Token not found");
                    }
            );

            User user = refreshToken.getUser();

            log.debug("Generating new JWT token for user with email: {}", user.getEmail());
            String token = jwtService.jwtCreate(user);

            UserResponse response = userResponseMapper.entityToResponse(user);
            response.setToken(token);

            log.debug("Revoking old refresh token: {}", tokenValue);
            tokenService.revokeToken(tokenValue);

            log.debug("Generating new refresh token for user with email: {}", user.getEmail());
            RefreshTokens newRefreshToken = tokenService.generateToken(user);
            tokenRepository.save(newRefreshToken);

            log.info("Refresh token successful for user with email: {}", user.getEmail());

            return AuthResponse.builder()
                    .refreshToken(newRefreshToken.getToken())
                    .userResponse(response)
                    .build();
        }
    }

    public void logout(String tokenValue) {
        log.info("Logout attempt with refresh token: {}", tokenValue);
        if (!tokenService.validateToken(tokenValue)) {
            log.warn("Logout attempt failed: Invalid refresh token {}", tokenValue);
            throw new ResourceNotFoundException("Invalid refresh token please login again");
        } else {
            log.debug("Revoking refresh token: {}", tokenValue);
            tokenService.revokeToken(tokenValue);
        }
    }

    @Override
    public userFullNameResponse getUserFullName(String userId) {
        log.debug("Fetching full name for user with ID: {}", userId);
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new ResourceNotFoundException("User not Found"));
        userFullNameResponse response = new userFullNameResponse();
        response.setFullName(user.getFullName());
        return response;
    }

    @Override
    public UserEmailResponse getUserEmail(String userId) {
        log.debug("Fetching email for user with ID: {}", userId);
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new ResourceNotFoundException("User not Found"));
        UserEmailResponse response = new UserEmailResponse();
        response.setEmail(user.getEmail());
        return response;
    }


    private User getAuthenticatedUser() {
        log.debug("Retrieving authenticated user with ID: {}", currentUser.getUserId());
        UUID userId = currentUser.getUserId();
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
    }


}
