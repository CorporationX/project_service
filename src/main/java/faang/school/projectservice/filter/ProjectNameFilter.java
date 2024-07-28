package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getNamePattern() != null && !filters.getNamePattern().isEmpty();
    }
    @Override
    public Stream<Project> apply(Stream <Project> events, ProjectFilterDto filters) {
        return events.filter(event -> event.getName().equals(filters.getNamePattern()));
    }
}
