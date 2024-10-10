package faang.school.projectservice.controller.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.dto.resource.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
public class ResourceController {

    private final ResourceService resourceService;
    private final UserContext userContext;

    @PostMapping("/{projectId}/resources")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto addResources(@Positive @PathVariable long projectId,
                                    @RequestParam("file") MultipartFile file) {
        long userId = userContext.getUserId();

        return resourceService.saveResource(projectId, file, userId);
    }

    @GetMapping("/{projectId}/resources/{resourceId}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable @Positive long projectId,
                                                       @PathVariable @Positive long resourceId) {
        long userId = userContext.getUserId();

        InputStreamResource file = resourceService.getFile(projectId, userId, resourceId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }

    @PutMapping("/{projectId}/resources/{resourceId}")
    public void deleteFile(@PathVariable @Positive long projectId,
                           @PathVariable @Positive long resourceId) {
        long userId = userContext.getUserId();

        resourceService.deleteResource(projectId, userId, resourceId);
    }
}
