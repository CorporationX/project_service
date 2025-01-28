package faang.school.projectservice.filter;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StatusSpecification implements SpecificationFilter{

    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.name() != null;
    }

    @Override
    public Specification<Project> apply(Specification<Project> spec, ProjectFilterDto filters) {
        return spec.and(((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), filters.status())));
    }
}
