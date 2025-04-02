package faang.school.projectservice.filter;

import faang.school.projectservice.dto.presentation.PresentationFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationFilter {

    boolean isApplicable(PresentationFilterDto filters);

    Specification<Project> apply(PresentationFilterDto filters);
}
