package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/project")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectDto create(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping("/update")
    public ProjectDto update(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @PostMapping("/{userId}")
    public List<ProjectDto> getProjectWithFilters(@Valid @RequestBody ProjectFilterDto projectFilterDto, @PathVariable long userId) {
        return projectService.getProjectsWithFilter(projectFilterDto, userId);
    }

    @GetMapping("/project/{userId}")
    public List<ProjectDto> getAllProjects(@PathVariable long userId) {
        return projectService.getAllProjects(userId);
    }

    @GetMapping("/project")
    public ProjectDto getProjectById(@RequestParam("projectId") long projectId, @RequestParam("userId") long userId) {
        return projectService.getProjectById(projectId, userId);
    }

    @PutMapping("/{projectId}/coverImage/")
    public String addCoverImage(@PathVariable long projectId, @RequestBody @NotNull MultipartFile file) {
        log.info("Received request to add a cover image to the project with ID: {}", projectId);
        return projectService.addCoverImage(projectId, file);
    }

    @GetMapping("/coverImage/{projectId}")
    public String getCoverImageBy(@PathVariable long projectId) {
        log.info("Received request to get a cover image from project with ID: {}", projectId);
        return projectService.getCoverImageBy(projectId);
    }

    @DeleteMapping("/coverImage/{projectId}")
    public void deleteCoverImage(@PathVariable @NotBlank long projectId) {
        log.info("Received request to delete a cover image from project with ID: {}", projectId);
        projectService.deleteCoverImageBy(projectId);
    }
}
