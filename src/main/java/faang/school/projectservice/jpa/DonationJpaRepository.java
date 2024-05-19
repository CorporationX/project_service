package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationJpaRepository extends JpaRepository<Donation, Long> {
    Optional<Donation> findByIdAndUserId(Long id, Long userId);

    List<Donation> findAllByUserId(Long userId);
}
