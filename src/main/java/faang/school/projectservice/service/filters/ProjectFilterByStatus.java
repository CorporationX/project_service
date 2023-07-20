package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class ProjectFilterByStatus implements ProjectFilter {
    @Override
    public boolean iaApplicable(ProjectFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public List<Project> apply(Stream<Project> projectStream, ProjectFilterDto filterDto) {
        return projectStream.filter(project ->
                project.getStatus().equals(filterDto.getStatus())).toList();
    }
}
