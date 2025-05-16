package faang.school.projectservice.filter.projecfilters;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectVisibilityFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectDto dto) {
        return dto.getName() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectDto dto) {
        return projects
                .filter(project -> project.getName().equals(dto.getName()));
    }
}
