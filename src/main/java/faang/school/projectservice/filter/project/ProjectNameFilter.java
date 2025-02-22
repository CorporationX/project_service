package faang.school.projectservice.filter.project;

import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectNameFilter implements Filter<Project, ProjectFilterDto> {

    @Override
    public boolean isApplicable(ProjectFilterDto filter) {
        return filter.name() != null;
    }

    @Override
    public List<Project> apply(List<Project> projects, ProjectFilterDto filters) {
        return projects.stream()
                .filter(project -> project.getName().contains(filters.name()))
                .toList();
    }
}
