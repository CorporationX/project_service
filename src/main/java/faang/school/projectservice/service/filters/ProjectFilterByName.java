package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectFilterByName implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getProjectNamePattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto filterDto) {
        return projectStream.filter(project ->
                project.getDescription().contains(filterDto.getProjectNamePattern()));
    }
}
