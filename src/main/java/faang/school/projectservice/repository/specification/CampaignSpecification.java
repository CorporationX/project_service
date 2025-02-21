package faang.school.projectservice.repository.specification;

import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CampaignSpecification {
    public Specification<Campaign> getByCreatedAt(LocalDate createdAt) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("createdAt"), createdAt, createdAt.plusDays(1));
    }

    public Specification<Campaign> getByStatus(CampaignStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public Specification<Campaign> getByCreatorId(long id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("createdBy"), id);
    }

    public Specification<Campaign> getOrderedByDate() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            return criteriaBuilder.conjunction();
        };
    }
}
