package faang.school.projectservice.repository;

import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {
    Optional<Campaign> findByTitleAndProjectId(String title, Long projectId);

    Page<Campaign> findAllByProjectId(Long projectId, Pageable pageable);

    @Query("""
    FROM Campaign c
    WHERE (c.project.id = :projectId)
    AND (:createdById IS NULL OR c.createdBy = :createdById)
    AND (:titlePattern IS NULL OR c.title LIKE %:titlePattern%)
    AND (:minGoal IS NULL OR c.goal >= :minGoal)
    AND (:maxGoal IS NULL OR c.goal <= :maxGoal)
    AND (:status IS NULL OR c.status = :status)
    AND (c.createdAt >= :createdAfter)""")
    Page<Campaign> findAllByFilters(Long projectId,
                                    Long createdById,
                                    String titlePattern,
                                    BigDecimal minGoal,
                                    BigDecimal maxGoal,
                                    CampaignStatus status,
                                    LocalDateTime createdAfter,
                                    Pageable pageable);
}
