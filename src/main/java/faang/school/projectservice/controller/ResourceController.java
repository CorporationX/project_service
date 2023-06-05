package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/v1/resources")
@Slf4j
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @GetMapping("/{projectId}/all")
    public List<ResourceDto> getAllAvailableResources(@PathVariable Long projectId) {
        return resourceService.getAvailableResources(projectId, userContext.getUserId());
    }

    @GetMapping(path = "/{resourceId}", produces = "application/octet-stream")
    public InputStream downloadResource(@PathVariable Long resourceId) {
        return resourceService.downloadResource(resourceId, userContext.getUserId());
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<String> deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId, userContext.getUserId());
        return ResponseEntity.ok("Resource deleted successfully");
    }

    @PostMapping("/{resourceId}")
    public ResourceDto updateResource(@PathVariable Long resourceId, @RequestBody MultipartFile file) {
        return resourceService.updateResource(resourceId, userContext.getUserId(), file);
    }

    @PutMapping("/{projectId}/add")
    public ResourceDto addResource(@PathVariable Long projectId, @RequestBody MultipartFile file) {
        return resourceService.addResource(projectId, userContext.getUserId(), file);
    }
}
