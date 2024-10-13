package faang.school.projectservice.specification;

import faang.school.projectservice.model.entity.Meet;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MeetSpecifications {

    public static Specification<Meet> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Meet> startDateAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), date);
    }

    public static Specification<Meet> startDateBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), date);
    }

    public static Specification<Meet> endDateAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), date);
    }

    public static Specification<Meet> endDateBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), date);
    }

    public static Specification<Meet> createdAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Meet> createdBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
    }
}

