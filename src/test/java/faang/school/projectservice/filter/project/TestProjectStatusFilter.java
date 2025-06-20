package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;

import java.util.stream.Stream;

public class TestProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto) {
        return projects.filter(project ->
                project.getStatus() == ProjectStatus.CREATED);
    }
}
