package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.List;
import java.util.stream.Stream;

public interface ProjectFilter {

    boolean isApplicable(ProjectFilterDto filters);
    List<Project> apply(Stream<Project> projects, ProjectFilterDto filters);
}
