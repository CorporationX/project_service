package faang.school.projectservice.filter.projecfilters;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;

import java.util.stream.Stream;

public class TestProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectDto dto) {
        return dto.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectDto dto) {
        return projects.filter(project ->
                project.getStatus() == ProjectStatus.CREATED);
    }
}
