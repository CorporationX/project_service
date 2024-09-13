package faang.school.projectservice.service.subproject.filters;

import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface SubProjectFilter {
    boolean isApplicable(ProjectDto projectDto);

    Stream<Project> apply(Stream<Project> projects, ProjectDto previousDto);
}
