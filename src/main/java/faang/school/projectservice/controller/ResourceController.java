package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.service.ResourceService;
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
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping("/{projectId}")
    public ResourceReadDto uploadResource(@PathVariable long projectId, @RequestBody MultipartFile file) {
        return resourceService.uploadResource(projectId, file);
    }

    @GetMapping("/{projectId}/all")
    public List<ResourceReadDto> getAllResources(@PathVariable long projectId) {
        return resourceService.getAllProjectResources(projectId);
    }

    @DeleteMapping("/{projectId}/all/{resourceId}")
    public String deleteResource(@PathVariable long projectId, @PathVariable long resourceId) {
        resourceService.deleteResource(projectId, resourceId);
        return String.format("Ресурс с id %d удален из проекта с id %d", resourceId, projectId);
    }
}
