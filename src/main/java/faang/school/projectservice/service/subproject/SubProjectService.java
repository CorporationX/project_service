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

    public List<ProjectDto> getAllSubProject(SubprojectFilterDto subprojectFilterDto) {
        Project project = projectService.getProjectById(subprojectFilterDto.getId());
        Stream<Project> subprojects = project.getChildren().stream();

        subprojectFilters.stream()
                .filter(filter -> filter.isApplicable(subprojectFilterDto))
                .forEach(filter -> filter.apply(subprojects, subprojectFilterDto));

        return projectMapper.toListProjectDto(subprojects.toList());
    }
}
