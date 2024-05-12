package faang.school.projectservice.service.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.List;
import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto filters);

    Stream<Project> apply(List<Project> projects, ProjectFilterDto filters);
}
