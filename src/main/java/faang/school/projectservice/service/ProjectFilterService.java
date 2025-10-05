package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectFilterService {
    private final List<ProjectFilter> projectFilters;

    public Stream<Project> applyFilters(Stream<Project> projectStream, ProjectFilterDto projectFilterDto) {
        for (ProjectFilter projectFilter : projectFilters) {
            if (projectFilter.isApplicable(projectFilterDto)) {
                projectStream = projectFilter.apply(projectStream, projectFilterDto);
            }
        }
        return projectStream;
    }
}
