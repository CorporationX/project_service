package faang.school.projectservice.service.project.filters;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto projectFilterDto);
    Stream<ProjectDto> filter(Stream<ProjectDto> projectDtoStream, ProjectFilterDto projectFilterDto);
}
