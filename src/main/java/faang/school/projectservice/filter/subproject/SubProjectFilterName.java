package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;

import java.util.stream.Stream;

public class SubProjectFilterName implements SubprojectFilter {
    @Override
    public boolean isApplicable(SubprojectFilterDto filters) {
        return filters.getNameFilter() != null;
    }

    @Override
    public void apply(Stream<Project> projects, SubprojectFilterDto filters) {
        projects.filter(project -> project.getName().equals(filters.getNameFilter()))
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC ||
                        project.getOwnerId() == filters.getRequesterId());
    }
}
