package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public Stream<ProjectDto> apply(Stream<ProjectDto> projects, ProjectFilterDto filterDto) {
        return projects.filter(projectDto -> filterDto.getStatus().equals(projectDto.getStatus()));
    }

}
