package epasal.com.userservice.services;

import epasal.com.userservice.config.JwtConfiguration;
import epasal.com.userservice.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtConfiguration jwtConfiguration;

    public String jwtCreate(User user) {
        log.debug("Creating JWT for user with ID: {}", user.getId());
        var now = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer("com.epasal")
                .subject(user.getId().toString())
                .claim("roles", user.getRoles())
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .build();
        log.info("JWT claims created for user ID: {} with roles: {}", user.getId(), user.getRoles());
        return jwtConfiguration.jwtEncoder().encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
