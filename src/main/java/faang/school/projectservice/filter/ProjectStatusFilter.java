package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectStatusFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto filter) {
        return filter.status() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filter) {
        return projects
                .filter(project -> filter.status().equals(project.getStatus()));
    }
}
