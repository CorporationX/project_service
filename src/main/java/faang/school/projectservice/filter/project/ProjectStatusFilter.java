package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class ProjectStatusFilter implements ProjectFilter{
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getStatusPattern() != null;
    }

    @Override
    public List<Project> apply(Stream<Project> projects, ProjectFilterDto filters) {
        return projects
                .filter(project -> project.getStatus().equals(filters.getStatusPattern()))
                .toList();
    }
}
