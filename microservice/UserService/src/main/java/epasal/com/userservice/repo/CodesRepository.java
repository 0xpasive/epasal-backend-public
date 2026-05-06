package epasal.com.userservice.repo;

import epasal.com.userservice.entity.Codes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodesRepository extends JpaRepository<Codes, String> {
    Optional<Codes> findByUserIdAndType(String userId, Codes.Type type);

    Optional<Codes> findByUserId(String userId);
}
