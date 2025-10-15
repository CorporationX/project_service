package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
public class ProjectController {
    private final ProjectService projectService;

    public void create(@Valid ProjectCreateDto projectDto) {
       projectService.create(projectDto);
    }

    public void update(@Valid ProjectUpdateDto projectDto,
                       @Positive(message = "Project id must be positive") long projectId) {
        projectService.update(projectDto, projectId);
    }

    public ProjectDto getById(@Positive(message = "Project id must be positive") long projectId) {
       return projectService.getById(projectId);
    }

    public List<ProjectDto> getAll() {
        return projectService.getAll();
    }

    public List<ProjectDto> getByFilter(@Valid ProjectFilterDto filterDto) {
        return projectService.getByFilter(filterDto);
    }
    public void delete(@Positive(message = "Project id must be positive") long projectId) {
        projectService.delete(projectId);
    }
}
