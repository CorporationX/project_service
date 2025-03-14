package faang.school.projectservice.filter;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto filter) {
        return filter.name() != null && !filter.name().isBlank();
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filter) {
        return projects
                .filter(project -> filter.name().equalsIgnoreCase(project.getName()));
    }
}
