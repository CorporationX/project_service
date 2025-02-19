package faang.school.projectservice.repository;

import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
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
            WHERE (COALESCE(:projectId, c.project.id) = c.project.id)
            AND (:namePattern IS NULL OR c.title LIKE CONCAT('%', :namePattern, '%'))
            AND (COALESCE(:minGoal, c.goal) <= c.goal)
            AND (COALESCE(:maxGoal, c.goal) >= c.goal)
            AND (COALESCE(:status, c.status) = c.status)
            AND (COALESCE(:createdBy, c.createdBy) = c.createdBy)
            AND (COALESCE(:startDate, c.createdAt) <= c.createdAt)
            AND (COALESCE(:endDate, c.createdAt) >= c.createdAt)
            ORDER BY c.createdAt DESC
            """
    )
    List<Campaign> findAllByFilters(@Param("projectId") Long projectId,
                                    @Param("namePattern") String namePattern,
                                    @Param("minGoal") BigDecimal minGoal,
                                    @Param("maxGoal") BigDecimal maxGoal,
                                    @Param("status") CampaignStatus status,
                                    @Param("createdBy") Long createdBy,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
}
