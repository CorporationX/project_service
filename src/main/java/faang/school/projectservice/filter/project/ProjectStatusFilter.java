package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getProjectStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filters) {
        return projects.filter(project -> filters.getProjectStatus().equals(project.getStatus()));
    }
}
