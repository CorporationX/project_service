package faang.school.projectservice.filter.project;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;

@Component
public class ProjectNameFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getName() != null && !filterDto.getName().isEmpty();
    }

    @Override
    public Stream<ProjectDto> apply(Stream<ProjectDto> projects, ProjectFilterDto filterDto) {
        return projects.filter(project -> project.getName().equals(filterDto.getName()));
    }

}
