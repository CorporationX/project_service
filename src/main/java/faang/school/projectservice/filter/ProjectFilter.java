package faang.school.projectservice.filter;

import faang.school.projectservice.dto.ProjectDto;

import java.util.List;

public interface ProjectFilter {

    boolean isApplicable (ProjectDto dto);

    List<ProjectDto> apply(ProjectDto dto);
}
