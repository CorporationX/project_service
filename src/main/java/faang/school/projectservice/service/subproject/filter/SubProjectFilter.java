package faang.school.projectservice.service.subproject.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface SubProjectFilter {
    boolean isApplicable(ProjectFilterDto filterDto);

    Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filterDto);
}
