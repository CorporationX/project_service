package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.domain.Specification;

public interface SubProjectFilter {

    boolean isApplicable(SubProjectFilterDto filters);

    Specification<Project> apply(SubProjectFilterDto filters);
}
