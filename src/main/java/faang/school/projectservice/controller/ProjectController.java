package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectForUpdateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectForCreationDto;
import faang.school.projectservice.dto.project.ProjectOutputDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectOutputDto create(@Valid @RequestBody ProjectForCreationDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping
    public ProjectOutputDto update(@Valid @RequestBody ProjectForUpdateDto projectDto) {
        return projectService.update(projectDto);
    }

    @GetMapping("/filtered")
    public List<ProjectOutputDto> getFilteredProjects(@Valid @RequestBody ProjectFilterDto projectDto) {
        return projectService.getFilteredProjects(projectDto);
    }

    @GetMapping("/all")
    public List<ProjectOutputDto> getAllProjects() {
        ProjectFilterDto emptyDto = ProjectFilterDto.builder().build();
        return projectService.getFilteredProjects(emptyDto);
    }

    @GetMapping("/{projectId}")
    public ProjectOutputDto getProjectById(@PathVariable long projectId) {
        return projectService.getProjectById(projectId);
    }
}
