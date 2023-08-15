package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectFilterStatus extends ProjectFilter {
    @Override
    protected boolean applyFilter(Project project, ProjectFilterDto projectFilterDto) {
        return project.getStatus().equals(projectFilterDto.getStatus());
    }

    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getStatus() != null;
    }
}
