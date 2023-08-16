package faang.school.projectservice.filter.project_filter;

import faang.school.projectservice.dto.project.ProjectByFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectByFilterDto projectByFilterDto) {
        return projectByFilterDto.getName() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectByFilterDto projectByFilterDto) {
        return projectStream.filter(project -> project.getName().contains(projectByFilterDto.getName()));
    }
}
