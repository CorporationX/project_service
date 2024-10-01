package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public Stream<Project> apply(List<Project> projects, ProjectFilterDto filters) {
        return projects.stream().filter(project -> project.getName().contains(filters.getNamePattern()));
    }
}
