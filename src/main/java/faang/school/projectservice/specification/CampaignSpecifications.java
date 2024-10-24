package faang.school.projectservice.specification;

import faang.school.projectservice.model.entity.Campaign;
import faang.school.projectservice.model.enums.CampaignStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class CampaignSpecifications {

    public static Specification<Campaign> createdAtAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Campaign> createdAtBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Campaign> statusEquals(CampaignStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Campaign> createdByEquals(Long creatorId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("createdBy"), creatorId);
    }
}

