package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/resources")
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @PutMapping("{projectId}/add")
    public ResourceDto addResource(@PathVariable Long projectId, @RequestBody MultipartFile multipartFile) {
        return resourceService.addResource(projectId, multipartFile, userContext.getUserId());
    }

    @PostMapping("/{resourceId}")
    public ResourceDto updateResource(@PathVariable Long resourceId, @RequestBody MultipartFile multipartFile) {
        return resourceService.updateResource(resourceId, multipartFile, userContext.getUserId());
    }

    @DeleteMapping("/{resourceId}")
    public void deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId, userContext.getUserId());
    }

    @GetMapping("/{resourceId}")
    public InputStream downloadResource(@PathVariable Long resourceId) {
        return resourceService.downloadResource(resourceId);
    }

}
