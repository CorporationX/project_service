package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto projectFilterDto);

    Stream<Project> apply(Stream<Project> projects, ProjectFilterDto projectFilterDto);
}
