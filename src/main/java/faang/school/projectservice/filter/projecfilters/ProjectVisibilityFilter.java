package faang.school.projectservice.filter.projecfilters;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectVisibilityFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.getVisibility() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto) {
        return projects
                .filter(project -> project.getVisibility().equals(dto.getVisibility()));
    }
}
