package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {

    boolean isApplicable(ProjectFilterDto filterDto);

    Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filterDto);
}
