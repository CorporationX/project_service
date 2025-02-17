package faang.school.projectservice.service.filter.moment;

import faang.school.projectservice.model.Moment;
import org.springframework.data.jpa.domain.Specification;

public final class MomentSpecification {

    private MomentSpecification() {
        throw new UnsupportedOperationException();
    }

    public static Specification<Moment> projectEqual(Long projectId) {
        return projectId == null
                ? Specification.where(null)
                : ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("moment_project").get("id"),  projectId));
    }

    public static Specification<Moment> monthEqual(Integer month) {
        return month == null
                ? Specification.where(null)
                : ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, root.get("date")), month));
    }


    public static Specification<Moment> yearEqual(Integer year) {
        return (year == null)
                ? Specification.where(null)
                : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("date")), year);
    }
}
