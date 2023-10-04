package faang.school.projectservice.repository;

import faang.school.projectservice.model.Donation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    Optional<Donation> findByIdAndUserId(Long id, Long userId);
    List<Donation> findAllByUserId(Long userId);

    @Query(
            nativeQuery = true, value = """
            SELECT d.* FROM donation d
            WHERE (:currency IS NULL OR d.currency LIKE :currency)
            AND (:minAmount IS NULL OR d.amount >= :minAmount)
            AND (:maxAmount IS NULL OR d.amount <= :maxAmount)
            AND (CAST(:createdAt AS date) IS NULL OR d.donation_time = CAST(:createdAt AS date))
            ORDER by d.donation_time
            """
    )
    List<Donation> findAllByFilters(@Param("currency") Currency currency,
                                    @Param("minAmount") BigDecimal minAmount,
                                    @Param("maxAmount") BigDecimal maxAmount,
                                    @Param("createdAt") LocalDateTime createdAt,
                                    Pageable pageable);
}
