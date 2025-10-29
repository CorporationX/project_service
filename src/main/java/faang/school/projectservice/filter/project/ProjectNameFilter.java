package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class ProjectNameFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return !projectFilterDto.name().isBlank();
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto projectFilterDto) {
        return projectStream.filter(project -> project.getName().toLowerCase()
                .contains(projectFilterDto.name().toLowerCase()));
    }
}