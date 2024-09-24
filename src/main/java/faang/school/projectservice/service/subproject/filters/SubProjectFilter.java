package faang.school.projectservice.service.subproject.filters;

import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface SubProjectFilter {
    boolean isApplicable(ProjectDto projectDto);

    Stream<Project> apply(Stream<Project> projects, ProjectDto previousDto);
}
