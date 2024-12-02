package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @PostMapping("/project/{projectId}/resource")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto uploadResource(@PathVariable @Positive long projectId, @RequestPart MultipartFile file) {
        long userId = userContext.getUserId();
        validateUserId(userId);
        return resourceService.uploadResource(projectId, userId, file);
    }

    @DeleteMapping("/resource/{resourceId}")
    public ResourceDto deleteResource(@PathVariable @Positive long resourceId) {
        long userId = userContext.getUserId();
        validateUserId(userId);
        return resourceService.deleteResource(resourceId, userId);
    }

    @PostMapping("/resource/{resourceId}")
    public ResourceDto updateResource(@PathVariable @Positive long resourceId, @RequestPart MultipartFile file) {
        long userId = userContext.getUserId();
        validateUserId(userId);
        return resourceService.updateResource(resourceId, userId, file);
    }

    private void validateUserId(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User id must be more 0");
        }
    }
}