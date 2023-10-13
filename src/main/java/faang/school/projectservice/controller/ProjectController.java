package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/")
    public ProjectDto create(@RequestBody ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping("/{projectId}")
    public ProjectDto changeStatus(@RequestBody ProjectDto projectDto, @PathVariable long projectId) {
        return projectService.update(projectDto, projectId);
    }

    @PostMapping("/{userId}/get-by-filters")
    public List<ProjectDto> getByFilters(@RequestBody ProjectFilterDto projectFilterDto, @PathVariable long userId) {
        return projectService.getByFilters(projectFilterDto, userId);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProject();
    }

    @DeleteMapping("/{userId}")
    public void deleteProjectById(@PathVariable long userId) {
        projectService.deleteProjectById(userId);
    }

    @GetMapping("/{userId}")
    public ProjectDto getProjectById(@PathVariable long userId) {
        return projectService.getProjectById(userId);
    }

    @PutMapping("/{projectId}/add")
    public String uploadImage(@PathVariable long projectId, @RequestBody MultipartFile file) {
        return projectService.uploadFile(projectId, file);
    }

    @DeleteMapping("/{projectId}/delete")
    public void deleteImage(@PathVariable long projectId) {
        projectService.deleteFile(projectId);
    }
}
