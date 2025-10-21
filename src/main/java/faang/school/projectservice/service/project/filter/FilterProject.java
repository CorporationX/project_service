package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface FilterProject {
    boolean isApplication(ProjectFilterDto projectFilterDto);

    Stream<Project> apply(Stream<Project> projects , ProjectFilterDto projectFilterDto);
}
