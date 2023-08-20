package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectByFilterDto;
import faang.school.projectservice.model.project.Project;

import java.util.stream.Stream;

public interface ProjectFilter {

    boolean isApplicable(ProjectByFilterDto projectByFilterDto);

    Stream<Project> apply(Stream<Project> projectStream, ProjectByFilterDto projectByFilterDto);
}
