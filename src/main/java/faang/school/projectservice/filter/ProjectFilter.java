package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.List;
import java.util.stream.Stream;

public interface ProjectFilter {

    boolean isApplicable(ProjectFilterDto projectByFilterDto);

    Stream<Project> apply(List<Project> projectStream, ProjectFilterDto projectByFilterDto);
}
