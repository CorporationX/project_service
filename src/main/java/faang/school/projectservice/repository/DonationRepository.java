package faang.school.projectservice.repository;

import faang.school.projectservice.model.Donation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    Optional<Donation> findByIdAndUserId(Long id, Long userId);

    List<Donation> findAllByUserId(Long userId, PageRequest of);

    @Query(nativeQuery = true, value = "SELECT d FROM Donation d WHERE DATE(d.donation_time) = :donationDate")
    List<Donation> findByDonationDate(LocalDate donationDate);

    @Query(nativeQuery = true, value = "SELECT d FROM Donation d WHERE d.currency = :currency")
    List<Donation> findByCurrency(String currency);

    List<Donation> findByAmount(BigDecimal amount);

    @Query("""
                SELECT d FROM Donation d
                    WHERE d.userId = :userId
                AND (:amount IS NULL OR d.amount = :amount)
                AND (:currency IS NULL OR d.currency = :currency)
                AND (:donationDate IS NULL OR FUNCTION('trunc', 'DAY', d.donationTime) = :donationDate)
            """)
    List<Donation> findAllByUserIdAndFilter(
            @Param("userId") Long userId,
            @Param("amount") BigDecimal amount,
            @Param("currency") String currency,
            @Param("donationDate") LocalDate donationDate);
}
