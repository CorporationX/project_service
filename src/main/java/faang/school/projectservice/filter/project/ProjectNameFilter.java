package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ProjectNameFilter implements ProjectFilter<Project, ProjectFilterDto> {
    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getName() != null;
    }
    @Override
    public void apply(List<Project> projects, ProjectFilterDto filter) {
        projects.removeIf(project -> !project.getName().contains(filter.getName()));
    }
}