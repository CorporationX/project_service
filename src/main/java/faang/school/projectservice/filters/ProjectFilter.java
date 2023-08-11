package faang.school.projectservice.filters;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto filterDto);
    Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto projectFilterDto);
}
