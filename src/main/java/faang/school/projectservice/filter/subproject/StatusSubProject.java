package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StatusSubProject implements SubProjectFilter {

    @Override
    public boolean isApplicable(SubProjectFilterDto filters) {
        return filters.name() != null;
    }

    @Override
    public Specification<Project> apply(SubProjectFilterDto filters) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), filters.status()));
    }
}
