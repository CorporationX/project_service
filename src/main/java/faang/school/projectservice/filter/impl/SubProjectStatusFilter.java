package faang.school.projectservice.filter.impl;

import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.model.entity.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectStatusFilter implements SubProjectFilter {

    @Override
    public boolean isApplicable(ProjectDto projectDto) {
        return projectDto.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectDto projectDto) {
        return projects
                .filter(project -> project.getStatus() == projectDto.getStatus());
    }
}
