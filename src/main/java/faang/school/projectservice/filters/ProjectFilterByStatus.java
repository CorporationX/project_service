package faang.school.projectservice.filters;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class ProjectFilterByStatus implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto projectFilterDto) {
        return projectStream
                .filter(project -> project.getStatus().equals(projectFilterDto.getStatus()));
    }
}
