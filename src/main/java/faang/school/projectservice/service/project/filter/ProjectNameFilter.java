package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectDtoFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectDtoFilter projectDtoFilter) {
        return projectDtoFilter != null;
    }

    @Override
    public void apply(List<Project> projects, ProjectDtoFilter goalFilterDto) {
        projects.removeIf(project -> project.getName().contains(goalFilterDto.getTitlePattern()));
    }
}
