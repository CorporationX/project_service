package faang.school.projectservice.filter.project;

import java.util.stream.Stream;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;

public interface ProjectFilter {

    boolean isApplicable(ProjectFilterDto filterDto);
    
    Stream<ProjectDto> apply(Stream<ProjectDto> projects, ProjectFilterDto filterDto);
}
