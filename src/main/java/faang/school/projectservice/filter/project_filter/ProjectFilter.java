package faang.school.projectservice.filter.project_filter;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {

    boolean isApplicable(ProjectDto projectDto);

    Stream<Project> apply(Stream<Project> projectStream, ProjectDto projectDto);
}
