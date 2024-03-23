package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDtoFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectDtoFilter projectDtoFilter) {
        return projectDtoFilter.getTitlePattern() != null;
    }
    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getNamePattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filterDto) {
        return projects.filter(project -> project.getName().contains(filterDto.getNamePattern()));
    }
    @Override
    public void apply(List<Project> projects, ProjectDtoFilter goalFilterDto) {
        projects.removeIf(project -> project.getName().contains(goalFilterDto.getTitlePattern()));
    }
}
