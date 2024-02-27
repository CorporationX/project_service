package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PutMapping("/{projectId}/add")
    public ResourceDto addResource(@PathVariable Long projectId, @RequestBody MultipartFile file) {
        return resourceService.addResource(projectId, file);
    }

    @PutMapping("/{projectId}/{resourceId}/delete")
    public ResourceDto deleteResource(@PathVariable Long projectId, @PathVariable Long resourceId) {
        return resourceService.deleteResource(projectId, resourceId);
    }

    @GetMapping("/{projectId}/{resourceId}")
    public ResourceDto getResource(@PathVariable Long projectId, @PathVariable Long resourceId) {
        return resourceService.getResource(projectId, resourceId);
    }
}
