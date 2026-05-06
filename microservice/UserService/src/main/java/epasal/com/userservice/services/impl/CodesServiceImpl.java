package epasal.com.userservice.services.impl;

import epasal.com.userservice.dtos.requests.CodeRequest;
import epasal.com.userservice.dtos.requests.VerifyCode;
import epasal.com.userservice.entity.Codes;
import epasal.com.userservice.entity.User;
import epasal.com.userservice.event.UserCodeEvent;
import epasal.com.userservice.eventPublisher.RabbitEventPublisher;
import epasal.com.userservice.exception.BadCodeRequestException;
import epasal.com.userservice.exception.UnauthorizedException;
import epasal.com.userservice.repo.CodesRepository;
import epasal.com.userservice.repo.UserRepository;
import epasal.com.userservice.services.CodesService;
import epasal.com.userservice.services.RateLimitingService;
import epasal.com.userservice.services.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class CodesServiceImpl implements CodesService {

    private final UserRepository userRepository;
    private final CodesRepository codesRepository;
    private final TokenService tokenService;
    private final RabbitTemplate rabbitTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RateLimitingService rateLimitingService;
    private final RabbitEventPublisher rabbitEventPublisher;


    @Override
    public void getCode(CodeRequest codeRequest) {
        log.info("Received code request for email: {} and type: {}", codeRequest.getEmail(), codeRequest.getType());
        rateLimitingService.requestTimeout(codeRequest.getEmail());
        if (codeRequest.getType().equals(CodeRequest.Type.VERIFICATION)) {
            log.debug("Processing verification code request for email: {}", codeRequest.getEmail());
            generateVerificationCode(codeRequest);
        } else if (codeRequest.getType().equals(CodeRequest.Type.PASSWORD_RESET)) {
            log.debug("Processing password reset code request for email: {}", codeRequest.getEmail());
            generateResetCode(codeRequest);
        } else {
            log.warn("Invalid code type received: {} for email: {}", codeRequest.getType(), codeRequest.getEmail());
            throw new BadCodeRequestException("Invalid code type. Must be either 'verification' or 'password_reset'.");
        }
    }

    public void generateVerificationCode(CodeRequest codeRequest) {
        User user = findUser(codeRequest.getEmail());
        log.info("Generating verification code for email: {}", codeRequest.getEmail());

        if (user != null) {

            if (user.isVerified()) {
                log.warn("User is already verified.");
                return;
            }

            codesRepository.findByUserIdAndType(String.valueOf(user.getId()), Codes.Type.VERIFICATION).ifPresent(codesRepository::delete);

            log.debug("Generating code.");
            Codes newCode = Codes.builder()
                    .userId(String.valueOf(user.getId()))
                    .code(generateCode())
                    .generatedAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .type(Codes.Type.VERIFICATION)
                    .build();
            codesRepository.save(newCode);

            UserCodeEvent event = new UserCodeEvent(
                    user.getEmail(),
                    newCode.getCode(),
                    newCode.getType()
            );
            log.debug("Publishing user code event for email: {} with code: {} and type: {}", user.getEmail(), newCode.getCode(), newCode.getType());
            rabbitEventPublisher.publishUserCodeEvent(event);
        }
    }


    public void generateResetCode(CodeRequest codeRequest) {
        log.info("Generating password reset code for email: {}", codeRequest.getEmail());
        User user = findUser(codeRequest.getEmail());

        if (user != null) {

            codesRepository.findByUserIdAndType(String.valueOf(user.getId()), Codes.Type.PASSWORD_RESET).ifPresent(codesRepository::delete);

            log.debug("Generating code.");
            Codes code = Codes.builder()
                    .userId(String.valueOf(user.getId()))
                    .code(generateCode())
                    .generatedAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .type(Codes.Type.PASSWORD_RESET)
                    .build();

            codesRepository.save(code);
            tokenService.revokeTokensByUser(user);

            UserCodeEvent event = new UserCodeEvent(
                    user.getEmail(),
                    code.getCode(),
                    code.getType()
            );
            log.debug("Publishing user code event for email: {} with code: {} and type: {}", user.getEmail(), code.getCode(), code.getType());
            rabbitEventPublisher.publishUserCodeEvent(event);

        }
        log.info("No user found for email: {}. No code generated.", codeRequest.getEmail());
    }

    @Override
    public void verifyCode(VerifyCode verifyCode) {
        log.info("Receiver Code verification request for email: {} and type: {}", verifyCode.getEmail(), verifyCode.getType());
        rateLimitingService.checkRateLimit(verifyCode.getEmail());

        if (verifyCode.getType() == Codes.Type.VERIFICATION) {
            log.debug("Processing verification code for email: {}", verifyCode.getEmail());
            verifyVerificationCode(verifyCode);
        } else if (verifyCode.getType() == Codes.Type.PASSWORD_RESET) {
            log.debug("Processing password reset code for email: {}", verifyCode.getEmail());
            verifyResetCode(verifyCode);
        } else {
            throw new BadCodeRequestException("Invalid code type. Must be either 'verification' or 'password_reset'.");
        }

    }

    public void verifyVerificationCode(VerifyCode verifyCode) {
        log.info("Verifying code for email: {}", verifyCode.getEmail());
        User user = findUserForCode(verifyCode.getEmail());

        Codes code = codesRepository.findByUserIdAndType(String.valueOf(user.getId()), Codes.Type.VERIFICATION)
                .orElseThrow(() -> new BadCodeRequestException("Invalid verification code. Please try again."));

        if (code != null) {
            log.info("code: {}", code.getCode());
            if (code.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.warn("The verification code has expired for email: {}", verifyCode.getEmail());
                throw new BadCodeRequestException("The verification code has expired. Please request a new one.");
            }
            if (verifyCode.getCode().equals(code.getCode())) {
                log.info("Verification code is valid for email: {}", verifyCode.getEmail());
                user.setVerified(true);
                userRepository.save(user);
                codesRepository.delete(code);
            } else {
                log.warn("Invalid verification code provided for email: {}", verifyCode.getEmail());
                throw new BadCodeRequestException("Invalid verification code. Please try again.");
            }
        }
    }

    public void verifyResetCode(VerifyCode verifyCode) {
        log.info("Verifying password reset code for email: {}", verifyCode.getEmail());
        User user = findUserForCode(verifyCode.getEmail());
        Codes code = codesRepository.findByUserIdAndType(String.valueOf(user.getId()), verifyCode.getType()).orElseThrow(() -> new UnauthorizedException("Invalid Code"));

        if (code.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("The password reset code has expired for email: {}", verifyCode.getEmail());
            throw new BadCodeRequestException("The password reset code has expired. Please request a new one.");
        }

        if (!code.getCode().equals(verifyCode.getCode())) {
            log.warn("Invalid password reset code provided for email: {}", verifyCode.getEmail());
            throw new BadCodeRequestException("Invalid password reset code. Please try again.");
        }

        if (verifyCode.getNewPassword() == null) {
            log.warn("New password cannot be blank for email: {}", verifyCode.getEmail());
            throw new BadCodeRequestException("New password cannot be blank.");
        }

        codesRepository.delete(code);
        log.debug("Setting password for user with email:{}", verifyCode.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(verifyCode.getNewPassword()));
        userRepository.save(user);
        log.info("Password reset successful for email: {}", verifyCode.getEmail());

        rateLimitingService.resetLimit(user.getEmail());
    }

    public String generateCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }

    public User findUser(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findUserForCode(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("Invalid Code"));

    }


}
