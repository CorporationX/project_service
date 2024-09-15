package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.List;
import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto filters);

    Stream<Project> apply(List<Project> projects, ProjectFilterDto filters);
}
