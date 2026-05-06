package epasal.com.userservice.repo;

import epasal.com.userservice.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    Address findByUserIdAndIsPrimary(UUID userId, boolean isPrimary);

    boolean existsByUserIdAndId(UUID userId, UUID id);

    List<Address> findByUserId(UUID userId);
}
