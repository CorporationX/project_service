package faang.school.projectservice.repository.specification;

import faang.school.projectservice.model.Meet;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MeetSpecification {

    public Specification<Meet> filterBy(long projectId, String title, LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (projectId > 0) {
                predicates.add(cb.equal(root.get("project").get("id"), projectId));
            }
            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(root.get("title"), "%" + title.toLowerCase() + "%"));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startsAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startsAt"), endDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
