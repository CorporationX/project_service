package faang.school.projectservice.filter;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationFilter {
    boolean isApplicable(ProjectFilterDto filters);

    Specification<Project> apply (Specification<Project> spec, ProjectFilterDto filters);
}
