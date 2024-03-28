package faang.school.projectservice.service.subproject.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class SubProjectNameFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getNamePattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filterDto) {
        return projects.filter(project -> project.getName().contains(filterDto.getNamePattern()));
    }
}
