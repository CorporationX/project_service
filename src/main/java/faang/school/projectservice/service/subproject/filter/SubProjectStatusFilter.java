package faang.school.projectservice.service.subproject.filter;

import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectStatusFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(FilterSubProjectDto filters) {
        return filters.getStatusPattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, FilterSubProjectDto filters) {
        return projects
                .filter(project -> project.getStatus() != null)
                .filter(project -> filters.getStatusPattern().contains(project.getStatus().toString()));
    }
}