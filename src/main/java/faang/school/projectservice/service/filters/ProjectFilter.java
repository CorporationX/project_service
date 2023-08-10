package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto projectFilterDto);

    Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto filterDto);
}