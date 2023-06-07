package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StageInvitationSpecification {
    public Specification<StageInvitation> findByFilter(StageInvitationFilterDto filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filter.getId()));
            }
            if (filter.getStageId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("stage"), filter.getStageId()));
            }
            if (filter.getAuthorId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("author"), filter.getAuthorId()));
            }
            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
