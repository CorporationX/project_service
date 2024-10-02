package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto projectFilterDto);

    Stream<Project> filter(Stream<Project> projectStream, ProjectFilterDto projectFilterDto);
}
