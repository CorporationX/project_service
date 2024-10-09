package faang.school.projectservice.model.filter.subproject;

import faang.school.projectservice.model.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.entity.Project;
import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto projectFilterDto);

    Stream<Project> filter(Stream<Project> projectStream, ProjectFilterDto projectFilterDto);
}
