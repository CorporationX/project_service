package faang.school.projectservice.service.filters.meet;

import faang.school.projectservice.model.Meet;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MeetSpecification {

    public static Specification<Meet> byFilters(Long projectId,
                                                String title,
                                                LocalDateTime startDate) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (projectId != null) {
                predicates.add(criteriaBuilder.equal(root.get("project").get("id"), projectId));
            }
            if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startsAt"), startDate));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
