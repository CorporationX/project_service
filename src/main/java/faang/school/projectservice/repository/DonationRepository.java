package faang.school.projectservice.repository;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    Optional<Donation> findByIdAndUserId(Long id, Long userId);

    List<Donation> findAllByUserId(Long userId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM donation
            WHERE user_id = :userId
            AND(:startDate IS NULL OR donation_time > :startDate)
            AND(:endDate IS NULL OR donation_time < :endDate)
            AND(:currency IS NULL OR currency = :currency)
            AND(:maxAmount IS NULL OR amount <= :maxAmount)
            AND(:minAmount IS NULL OR amount >= :minAmount)
            ORDER BY donation_time DESC
            """)
    List<Donation> findAllByUserIdFilteredAndThenSortedByDate(@Param("userId") Long userId,
                                                              @Param("startDate") LocalDateTime startDate,
                                                              @Param("endDate") LocalDateTime endDate,
                                                              @Param("currency") Currency currency,
                                                              @Param("maxAmount") BigDecimal maxAmount,
                                                              @Param("minAmount") BigDecimal minAmount);
}
