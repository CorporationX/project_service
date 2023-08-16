package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.filter.subproject.SubprojectFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final ProjectService projectService;
    private final List<SubprojectFilter> subprojectFilters;
    private final ProjectMapper projectMapper;

    public List<ProjectDto> getAllSubProject(SubprojectFilterDto filters) {
        Project project = projectService.getProjectById(filters.getId());
        Stream<Project> subprojects = project.getChildren().stream();

        return subprojectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(subprojects, filters))
                .map(projectMapper::toProjectDto)
                .toList();
    }
}
