package epasal.com.userservice.services;

import epasal.com.userservice.entity.RefreshTokens;
import epasal.com.userservice.entity.User;
import epasal.com.userservice.repo.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public RefreshTokens generateToken(User user) {
        log.info("Generating refresh token for user with ID: {}", user.getId());
        RefreshTokens tokens = RefreshTokens.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .revoked(false)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();
        log.debug("Refresh token generated for user ID: {} with token value: {}", user.getId(), tokens.getToken());
        return tokenRepository.save(tokens);

    }

    public boolean validateToken(String tokenValue) {
        log.info("Validating refresh token: {}", tokenValue);
        RefreshTokens token = tokenRepository.findByToken(tokenValue).orElseThrow(() -> {
            log.warn("Invalid token: {}", tokenValue);
            return new  RuntimeException("Invalid token");
        });
        log.info("Token found for user ID: {}. Revoked: {}, Expires at: {}", token.getUser().getId(), token.isRevoked(), token.getExpiresAt());
        return !token.isRevoked() && !token.getExpiresAt().isBefore(LocalDateTime.now());
    }

    public void revokeToken(String tokenValue) {
        log.info("Revoking refresh token: {}", tokenValue);
        RefreshTokens token = tokenRepository.findByToken(tokenValue).orElseThrow(() -> new RuntimeException("Invalid token"));
        tokenRepository.delete(token);


    }

    public void revokeTokensByUser(User user) {
        log.debug("Revoking all refresh tokens for user with ID: {}", user.getId());
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
    }
}
