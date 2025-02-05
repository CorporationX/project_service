package faang.school.projectservice.repository;

import faang.school.projectservice.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    Optional<Donation> findByIdAndUserId(Long id, Long userId);

    List<Donation> findAllByUserId(Long userId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM donation
            WHERE user_id = :userId
            ORDER BY donation_time DESC
            """)
    List<Donation> findAllByUserIdAndSortedByDate(Long userId);
}
