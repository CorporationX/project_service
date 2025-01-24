package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto filter);
    Stream<Project> applyFilter(Stream<Project> projectStream, ProjectFilterDto filter);
}
