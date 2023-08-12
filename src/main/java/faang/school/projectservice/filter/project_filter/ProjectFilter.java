package faang.school.projectservice.filter.project_filter;

import faang.school.projectservice.dto.project.ProjectByFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {

    boolean isApplicable(ProjectByFilterDto projectByFilterDto);

    Stream<Project> apply(Stream<Project> projectStream, ProjectByFilterDto projectByFilterDto);
}
