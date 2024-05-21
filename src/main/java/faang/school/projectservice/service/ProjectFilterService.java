package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectFilterService {
    private final List<ProjectFilter> projectFilters;

    public Stream<Project> applyFilters(Stream<Project> projects, @NonNull ProjectFilterDto projectFilterDto) {
            projectFilters.stream()
                    .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                    .forEach(projectFilter ->projectFilter.apply(projects, projectFilterDto));
        return projects;
    }
}
