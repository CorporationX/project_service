package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.SubProjectsFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;

import java.util.stream.Stream;

public interface SubProjectFilter {

    boolean isApplicable(SubProjectsFilterDto subProjectsFilterDto);

    Stream<Project> apply(Stream<Project> projects, SubProjectsFilterDto subProjectsFilterDto);

    default boolean isPublic(Project project) {
        return project.getVisibility() == ProjectVisibility.PUBLIC;
    }
}
