package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectVisibilityFilter implements ProjectFilters {
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getVisibility() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto filters) {
        return projectStream.filter(project -> project.getVisibility().equals(filters.getVisibility()));
    }
}
