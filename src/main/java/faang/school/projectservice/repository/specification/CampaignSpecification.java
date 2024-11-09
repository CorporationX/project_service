package faang.school.projectservice.repository.specification;

import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class CampaignSpecification {
    public static Specification<Campaign> filteredCampaigns(Long projectId,
                                                            Long createdById,
                                                            String title,
                                                            BigDecimal minGoal,
                                                            BigDecimal maxGoal,
                                                            CampaignStatus status,
                                                            LocalDateTime createdAfter) {
        return (root, query, criteriaBuilder) -> {
            PredicateBuilder builder = new PredicateBuilder(criteriaBuilder);
            return builder.addCondition(() -> Objects.nonNull(projectId) ? criteriaBuilder.equal(root.get("project").get("id"), projectId) : null)
                    .addCondition(() -> Objects.nonNull(title) ? criteriaBuilder.like(root.get("title"), "%" + title + "%") : null)
                    .addCondition(() -> Objects.nonNull(createdById) ? criteriaBuilder.equal(root.get("createdBy"), createdById) : null)
                    .addCondition(() -> Objects.nonNull(minGoal) ? criteriaBuilder.greaterThanOrEqualTo(root.get("goal"), minGoal) : null)
                    .addCondition(() -> Objects.nonNull(maxGoal) ? criteriaBuilder.lessThanOrEqualTo(root.get("goal"), maxGoal) : null)
                    .addCondition(() -> Objects.nonNull(status) ? criteriaBuilder.equal(root.get("status"), status) : null)
                    .addCondition(() -> Objects.nonNull(createdAfter) ? criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdAfter) : null)
                    .build();
        };
    }
}
