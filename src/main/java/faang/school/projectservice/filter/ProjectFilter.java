package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;

import java.util.stream.Stream;

public interface ProjectFilter {

    boolean isApplicable(ProjectFilterDto filterDto);

    Stream<ProjectDto> apply(Stream<ProjectDto> projects, ProjectFilterDto filterDto);

}
