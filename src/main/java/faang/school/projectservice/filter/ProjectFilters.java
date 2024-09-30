package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface ProjectFilters {
    boolean isApplicable(ProjectFilterDto filters);
    Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto filters);
}
