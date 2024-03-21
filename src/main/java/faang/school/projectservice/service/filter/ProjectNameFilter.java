package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter{
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public List<Project> apply(Stream<Project> projects, ProjectFilterDto filters) {
        return projects
                .filter(project -> project.getName().contains(filters.getNamePattern())).toList();
    }
}
