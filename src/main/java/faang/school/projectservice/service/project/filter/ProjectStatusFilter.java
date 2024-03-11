package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectStatusFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getStatusPattern() != null;
    }

    @Override
    public void apply(List<Project> projects, ProjectFilterDto filters) {
        projects.removeIf(project -> !project.getStatus().equals(filters.getStatusPattern()));
    }
}
