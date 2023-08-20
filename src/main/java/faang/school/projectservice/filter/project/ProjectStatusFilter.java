package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectByFilterDto;
import faang.school.projectservice.model.project.Project;

import java.util.stream.Stream;

public class ProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectByFilterDto projectByFilterDto) {
        return projectByFilterDto.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectByFilterDto projectByFilterDto) {
        return projectStream.filter(project -> project.getStatus().equals(projectByFilterDto.getStatus()));
    }
}
