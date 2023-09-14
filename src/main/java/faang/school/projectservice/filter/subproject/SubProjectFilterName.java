package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectFilterName implements SubprojectFilter {
    @Override
    public boolean isApplicable(SubprojectFilterDto filters) {
        return filters.getNameProject() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, SubprojectFilterDto filters) {
        return projects.filter(project -> project.getName().equals(filters.getNameProject()))
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC ||
                        project.getOwnerId() == filters.getRequesterId());
    }
}
