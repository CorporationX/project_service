package faang.school.projectservice.fillters.project.impl;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.fillters.project.ProjectFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;


@Component
public class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public boolean filterEntity(Project project, ProjectFilterDto filters) {
        return project.getName().contains(filters.getNamePattern());
    }
}
