package epasal.com.userservice.repo;

import epasal.com.userservice.entity.RefreshTokens;
import epasal.com.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshTokens, Long> {
    Optional<RefreshTokens> findById(long id);

    Optional<RefreshTokens> findByToken(String token);

    Optional<RefreshTokens> findByUser(User user);
}
