package faang.school.projectservice.filter;

import faang.school.projectservice.dto.presentation.PresentationFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class NameSpecification implements SpecificationFilter {

    @Override
    public boolean isApplicable(PresentationFilterDto filters) {
        return filters.name() != null;
    }

    @Override
    public Specification<Project> apply(PresentationFilterDto filters) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), String.format("%%%s%%", filters.name()));
    }
}
