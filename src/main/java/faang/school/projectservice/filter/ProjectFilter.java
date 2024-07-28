package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable (ProjectFilterDto filters);
    Stream<Project> apply (Stream <Project> events, ProjectFilterDto filters);
}
