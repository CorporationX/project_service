package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectStatusFilter implements ProjectFilter<Project, ProjectFilterDto> {
    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getStatus() != null;
    }

    @Override
    public void apply(List<Project> projects, ProjectFilterDto projectFilterDto) {
        projects.removeIf(project -> !project.getStatus().equals(projectFilterDto.getStatus()));
    }
}
