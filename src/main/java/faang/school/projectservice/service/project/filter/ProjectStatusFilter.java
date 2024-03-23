package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDtoFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectDtoFilter goalFilterDto) {
        return goalFilterDto.getStatusPattern() != null;
    }
    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getStatusPattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filterDto) {
        return projects.filter(project -> project.getStatus().equals(filterDto.getStatusPattern()));
    }
    @Override
    public void apply(List<Project> projects, ProjectDtoFilter goalFilterDto) {
        projects.removeIf(project -> !project.getStatus().equals(goalFilterDto.getStatusPattern()));
    }
}
