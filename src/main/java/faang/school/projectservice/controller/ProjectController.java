package faang.school.projectservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectDto create(@RequestBody ProjectDto projectDto) {
        validate(projectDto);
        return projectService.create(projectDto);
    }

    @PatchMapping("/{id}")
    public ProjectDto update(@PathVariable long id, @RequestBody ProjectDto projectDto) {
        if (id != projectDto.getId()) {
            throw new IllegalArgumentException("Project id does not match.");
        }
        return projectService.update(projectDto);
    }

    @GetMapping("/all/filtered")
    public List<ProjectDto> getAll(@ModelAttribute ProjectFilterDto filter) {
        return projectService.getAll(filter);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAll() {
        return projectService.getAll();
    }

    @GetMapping("/{projectId}")
    public ProjectDto getById(@PathVariable long projectId) {
        return projectService.getById(projectId);
    }

    @PostMapping
    public List<ProjectDto> getProjectsByIds(@RequestBody List<Long> ids) {
        return projectService.getProjectsByIds(ids);
    }

    private void validate(ProjectDto projectDto) {
        boolean isNameValid = projectDto.getName() != null && !projectDto.getName().isEmpty();
        boolean isDescriptionValid = projectDto.getDescription() != null && !projectDto.getDescription().isEmpty();
        if (!isDescriptionValid || !isNameValid) {
            throw new IllegalArgumentException("Name and description must not be empty.");
        }
    }
}