package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectTitleFilter extends ProjectFilter {
    @Override
    protected boolean applyFilter(Project project, ProjectFilterDto projectFilterDto) {
        return project.getName().contains(projectFilterDto.getName());
    }

    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getName() != null;
    }
}
