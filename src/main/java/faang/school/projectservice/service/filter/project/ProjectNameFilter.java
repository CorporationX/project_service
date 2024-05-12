package faang.school.projectservice.service.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getName() != null;
    }

    @Override
    public Stream<Project> apply(List<Project> projects, ProjectFilterDto filters) {
        return projects.stream().filter(project -> project.getName().equals(filters.getName()));
    }
}
