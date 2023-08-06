package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public abstract class ProjectFilter {

    public Stream<Project> applyFilter(Stream<Project> stream, ProjectFilterDto projectFilterDto){
        return stream.filter(project -> applyFilter(project, projectFilterDto));
    }
    protected abstract boolean applyFilter(Project project, ProjectFilterDto projectFilterDto);

    public abstract boolean isApplicable(ProjectFilterDto projectFilterDto);
}
