package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto filters);

    Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filters);
}
