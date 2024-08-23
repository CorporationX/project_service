package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Component
public class ProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getStatusPattern() != null;
    }

    @Override
    public Stream<Project> apply(List<Project> projects, ProjectFilterDto filters) {
        return projects.stream().filter(project ->
                project.getStatus().equals(filters.getStatusPattern()));
    }
}