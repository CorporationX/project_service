package faang.school.projectservice.repository;

import faang.school.projectservice.model.Campaign;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Optional<Campaign> findByTitleAndProjectId(String title, Long projectId);

    @Query(
            "SELECT c FROM Campaign c " +
            "WHERE (:namePattern IS NULL OR c.title LIKE %:namePattern%) " +
            "AND (:minGoal IS NULL OR c.goal >= :minGoal) " +
            "AND (:maxGoal IS NULL OR c.goal <= :maxGoal) " +
            "AND (:status IS NULL OR c.status = :status)"
    )
    List<Campaign> findAllByFilters(@Param("namePattern") String namePattern,
                                    @Param("minGoal") BigDecimal minGoal,
                                    @Param("maxGoal") BigDecimal maxGoal,
                                    @Param("status") String status,
                                    Pageable pageable);
}
