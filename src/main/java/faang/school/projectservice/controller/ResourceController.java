package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @PutMapping("/{projectId}/add")
    public ResourceDto addResource(@PathVariable Long projectId, @RequestBody MultipartFile file) {
        return resourceService.addResource(projectId, file);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<String> deleteResource(@PathVariable Long resourceId) {
        long userId = userContext.getUserId();
        resourceService.deleteResource(resourceId, userId);
        return ResponseEntity.ok("Resource deleted");
    }

    @GetMapping("/{resourceId}")
    public InputStream getResource(@PathVariable Long resourceId) {
        return resourceService.downloadResource(resourceId);
    }
}