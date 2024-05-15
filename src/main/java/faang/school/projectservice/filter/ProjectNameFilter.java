package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getName() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto projectFilterDto) {
        return projects.filter(project -> project.getName().equals(projectFilterDto.getName()));
    }
}
