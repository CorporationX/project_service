package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.List;
import java.util.stream.Stream;

public interface ProjectFilter {
    boolean iaApplicable(ProjectFilterDto filterDto);

    List<Project> apply(Stream<Project> projectStream, ProjectFilterDto filterDto);
}
