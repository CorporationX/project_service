package faang.school.projectservice.service.subproject.filters;

import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectNameFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(ProjectDto projectDto) {
        return projectDto.getName() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectDto projectDto) {
        return projects
                .filter(project -> project.getName().toLowerCase().contains(projectDto.getName().toLowerCase()));
    }
}
