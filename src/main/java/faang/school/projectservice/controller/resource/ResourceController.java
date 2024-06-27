package faang.school.projectservice.controller.resource;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @PostMapping(value = "/{projectId}/files")
    public ResourceDto saveFile(@PathVariable @Positive Long projectId,
                                @RequestParam("file") MultipartFile file) {
        Long currentUserId = userContext.getUserId();
        return resourceService.saveFile(currentUserId, projectId, file);
    }

    @GetMapping("/{projectId}/files/{resourceId}")
    public S3ObjectInputStream getFile(@PathVariable @Positive Long projectId,
                                       @PathVariable @Positive Long resourceId) {
        Long currentUserId = userContext.getUserId();
        return resourceService.getFile(currentUserId, projectId, resourceId);
    }

    @DeleteMapping("/{projectId}/files/{resourceId}")
    public void deleteFile(@PathVariable @Positive Long projectId,
                           @PathVariable @Positive Long resourceId) {
        long currentUserId = userContext.getUserId();
        resourceService.deleteFile(currentUserId, projectId, resourceId);
    }
}
