package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter{
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return Objects.nonNull(filters.getName());
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filters) {
        return projects.filter(project -> project
                .getName()
                .toLowerCase()
                .contains(filters.getName().toLowerCase()));
    }
}
