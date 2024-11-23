package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectStatusFilter implements Filter<Project, ProjectFilterDto> {

    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filters) {
        return projects.filter(project -> project.getStatus() == filters.getStatus());
    }
}
