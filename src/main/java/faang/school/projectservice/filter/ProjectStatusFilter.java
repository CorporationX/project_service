package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;
@Component
public class ProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getStatusPattern() != null;
    }
    @Override
    public Stream<Project> apply (Stream <Project> projects, ProjectFilterDto filters) {
        {projects.filter(project ->
                            project.getStatus().equals(filters.getStatusPattern()))
                    .collect(Collectors.toList());
            return projects;
        }
    }
}