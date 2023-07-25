package faang.school.projectservice.filter.project_filter;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class ProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectDto projectDto) {
        return projectDto.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectDto projectDto) {
        return projectStream.filter(project -> project.getStatus().equals(projectDto.getStatus()));
    }
}
