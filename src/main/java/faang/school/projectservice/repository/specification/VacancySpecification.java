package faang.school.projectservice.repository.specification;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.springframework.data.jpa.domain.Specification;

public class VacancySpecification {

    public static Specification<Vacancy> getByPosition(TeamRole position) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("position"), position);
    }

    public static Specification<Vacancy> getByName(String namePart) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), String.format("%%%s%%", namePart));
    }
}
