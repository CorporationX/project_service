package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/{projectId}/resources")
    public ResourceReadDto uploadResource(@PathVariable long projectId, @RequestBody MultipartFile file) {
        return projectService.uploadResourceToGallery(projectId, file);
    }

    @GetMapping("/{projectId}/resources/list")
    public List<ResourceReadDto> getAllResources(@PathVariable long projectId) {
        return projectService.getAllProjectResources(projectId);
    }

    @DeleteMapping("/{projectId}/resources/list/{resourceId}")
    public String deleteResource(@PathVariable long projectId, @PathVariable long resourceId) {
        projectService.deleteResourceFromGallery(projectId, resourceId);
        return String.format("Ресурс с id %d удален из проекта с id %d", resourceId, projectId);
    }
}
