package faang.school.projectservice.service.filter.event;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import java.util.stream.Stream;

public abstract class ProjectFilter {

    public Stream<Project> applyFilter(Stream<Project> projects, ProjectFilterDto filter) {
        return projects.filter(event -> applyFilter(event, filter));
    }

    protected abstract boolean applyFilter(Project event, ProjectFilterDto filter);

    public abstract boolean isApplicable(ProjectFilterDto filter);
}