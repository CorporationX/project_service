package faang.school.projectservice.repository;

import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Optional<Campaign> findByTitleAndProjectId(String title, Long projectId);

    @Query("""
            SELECT c FROM Campaign c
            WHERE c.project = :project
            AND(:namePattern IS NULL OR c.title LIKE %:namePattern%)
            AND (:minGoal IS NULL OR c.goal >= :minGoal)
            AND (:maxGoal IS NULL OR c.goal <= :maxGoal)
            AND (:status IS NULL OR c.status = :status)
            AND (:createdBy IS NULL OR c.createdBy = :createdBy)
            AND (:startDate IS NULL OR c.createdAt >= :startDate)
            AND (:endDate IS NULL OR c.createdAt <= :endDate)
            ORDER BY c.createdAt DESC
            """
    )
    List<Campaign> findAllByFilters(@Param("project") Project project,
                                    @Param("namePattern") String namePattern,
                                    @Param("minGoal") BigDecimal minGoal,
                                    @Param("maxGoal") BigDecimal maxGoal,
                                    @Param("status") CampaignStatus status,
                                    @Param("createdBy") Long createdBy,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
}
