package faang.school.projectservice.controller.project;

import faang.school.projectservice.api.ProjectApi;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class ProjectController implements ProjectApi {
    private final ProjectService projectService;

    @Override
    public ProjectDto create(@Valid ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @Override
    public ProjectDto update(@Valid ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @Override
    public List<ProjectDto> getAll() {
        return projectService.getAll();
    }

    @Override
    public ProjectDto getById(long projectId) {
        return projectService.getById(projectId);
    }

    @Override
    public List<ProjectDto> getByFilters(ProjectFilterDto filterDto) {
        return projectService.getAll(filterDto);
    }

    @GetMapping("{projectId}/exist")
    public boolean existProjectById(@PathVariable("projectId") long projectsId) {
        return projectService.existProjectById(projectsId);
    }
}