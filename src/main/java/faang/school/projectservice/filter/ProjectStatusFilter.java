package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class ProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filter) {
        return filter.getProjectStatusPattern() != null;
    }

    @Override
    public void applyFilter(Stream<Project> projectStream, ProjectFilterDto filter) {
        projectStream.filter(project -> Objects.equals(project.getStatus(), filter.getProjectStatusPattern()));
    }
}
