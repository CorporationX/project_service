package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Resource", description = "Endpoint for managing resources")
@RequestMapping("${api.base-path}")
public class ResourceController {
    private final ResourceService resourceService;
    @Value("${services.s3.max_size}")
    private long maxSizeFile;

    @Operation(summary = "uploading file to project ")
    @PostMapping("/{userId}/files")
    public ResourceDto uploadFileToProject(@PathVariable Long userId, @RequestBody MultipartFile file) {
        return resourceService.uploadFileToProject(userId, file);
    }

    @Operation(summary = "updating file in project")
    @PutMapping("/{userId}/{resourceId}/files")
    public ResourceDto updateFileFromProject(@PathVariable Long userId, @PathVariable Long resourceId, @RequestBody MultipartFile file) {
        return resourceService.updateFileFromProject(userId, resourceId, file);
    }

    @Operation(summary = "deleting file from project")
    @DeleteMapping("{userId}/{resourceId}/files")
    public void deleteFileFromProject(@PathVariable Long userId, @PathVariable Long resourceId) {
        resourceService.deleteFileFromProject(userId, resourceId);
    }
}
