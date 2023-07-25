package faang.school.projectservice.filter.project_filter;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectDto projectDto) {
        return projectDto.getName() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectDto projectDto) {
        return projectStream.filter(project -> project.getName().contains(projectDto.getName()));
    }
}
