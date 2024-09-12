package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getName() != null && !filterDto.getName().isBlank();
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filterDto) {
        return projects.filter(project -> project.getName().equals(filterDto.getName()));
    }
}
