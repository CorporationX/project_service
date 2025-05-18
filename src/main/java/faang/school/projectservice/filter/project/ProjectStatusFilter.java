package faang.school.projectservice.filter.project;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;

@Component
public class ProjectStatusFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public Stream<ProjectDto> apply(Stream<ProjectDto> projects, ProjectFilterDto filterDto) {
        return projects.filter(project -> project.getStatus().equals(filterDto.getStatus()));
    }

}
