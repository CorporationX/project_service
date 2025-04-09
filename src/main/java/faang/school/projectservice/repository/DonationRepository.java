package faang.school.projectservice.repository;

import faang.school.projectservice.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long>, JpaSpecificationExecutor<Donation> {
    Optional<Donation> findByIdAndUserId(Long id, Long userId);

    List<Donation> findAllByUserId(Long userId);
}
