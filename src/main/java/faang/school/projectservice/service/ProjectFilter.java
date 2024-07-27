package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto filters);

    Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto filters);
}
