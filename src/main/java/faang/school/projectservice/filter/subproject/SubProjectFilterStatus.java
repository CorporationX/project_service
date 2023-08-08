package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;

import java.util.stream.Stream;

public class SubProjectFilterStatus implements SubprojectFilter {
    @Override
    public boolean isApplicable(SubprojectFilterDto filters) {
        return filters.getStatusFilter() != null;
    }

    @Override
    public void apply(Stream<Project> projects, SubprojectFilterDto filters) {
        projects.filter(project -> project.getStatus().equals(filters.getStatusFilter()))
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC ||
                        project.getOwnerId() == filters.getRequesterId());
    }
}
