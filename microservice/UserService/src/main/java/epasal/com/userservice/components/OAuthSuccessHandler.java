package epasal.com.userservice.components;

import epasal.com.userservice.entity.RefreshTokens;
import epasal.com.userservice.entity.User;
import epasal.com.userservice.repo.TokenRepository;
import epasal.com.userservice.repo.UserRepository;
import epasal.com.userservice.services.JwtService;
import epasal.com.userservice.services.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name);
            newUser.setRoles(Set.of(User.Role.ROLE_USER));
            newUser.setVerified(true);
            newUser.setCreatedAt(Date.from(Instant.now()));
            newUser.setUpdatedAt(Date.from(Instant.now()));
            return userRepository.save(newUser);
        });
        String token = jwtService.jwtCreate(user);
        RefreshTokens refreshToken = tokenRepository.findByUser(user).orElseGet(() -> tokenService.generateToken(user));
        tokenRepository.save(refreshToken);

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        response.sendRedirect("https://epasal-frontend.vercel.app/login-success?token=" + token);


    }
}
