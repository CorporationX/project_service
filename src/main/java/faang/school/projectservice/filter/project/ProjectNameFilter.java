package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
class ProjectNameFilter implements Filter<ProjectFilterDto, Project> {

    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getName() != null && !filterDto.getName().isBlank();
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filterDto) {
        return projects.filter(project -> project.getName().contains(filterDto.getName()));
    }
}
