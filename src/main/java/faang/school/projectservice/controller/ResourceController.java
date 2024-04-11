package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @PostMapping("/{projectId}")
    public ResourceDto uploadResource(@PathVariable Long projectId, @RequestPart MultipartFile file) {
        return resourceService.uploadResource(projectId, file, userContext.getUserId());
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
