package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectDtoFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectDtoFilter goalFilterDto) {
        return goalFilterDto.getStatusPattern() != null;
    }

    @Override
    public void apply(List<Project> projects, ProjectDtoFilter goalFilterDto) {
        projects.removeIf(project -> !project.getStatus().equals(goalFilterDto.getStatusPattern()));
    }
}
