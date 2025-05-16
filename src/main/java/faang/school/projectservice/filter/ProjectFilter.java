package faang.school.projectservice.filter;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {

    boolean isApplicable (ProjectDto dto);

    Stream<Project> apply(Stream<Project> projects, ProjectDto dto);
}
