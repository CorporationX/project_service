package faang.school.projectservice.filter;

import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.model.entity.Project;

import java.util.stream.Stream;

public interface SubProjectFilter {
    boolean isApplicable(ProjectDto projectDto);

    Stream<Project> apply(Stream<Project> projects, ProjectDto previousDto);
}
