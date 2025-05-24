package faang.school.projectservice.filter.projecfilters;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import java.util.stream.Stream;

public class TestProjectVisibilityFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.getVisibility() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto) {
        return projects.filter(project ->
                project.getVisibility() == ProjectVisibility.PRIVATE);
    }
}
