package faang.school.projectservice.controller;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RequiredArgsConstructor
@RequestMapping("/resource")
@RestController
@Tag(name = "ResourceController", description = "Controller for working with recources")
public class ResourceController {
    private final ResourceService resourceService;

    @Operation(description = "Upload file using multipart/form-data.")
    @PostMapping(path = "/{projectId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ResourceDto> uploadFile(
            @Parameter(description = "Auth header x-user-id, that will be TeamMemberId", required = true, name = "x-user-id")
            @RequestHeader(value = "x-user-id") Long userId,
            @Parameter(name = "projectId", description = "ID Project", required = true)
            @Positive @PathVariable Long projectId,
            @Parameter(description = "File to upload", required = true, content = @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(type = "string", format = "binary")
            ))
            @RequestPart("file") MultipartFile file) {
        ResourceDto dto = resourceService.addResource(projectId, file);
        return ResponseEntity.ok(dto);
    }

    @Operation(description = "Update file by id")
    @PutMapping(path = "/{resourceId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ResourceDto> updateFile(
            @Parameter(description = "Auth header x-user-id, that will be TeamMemberId", required = true, name = "x-user-id")
            @RequestHeader(value = "x-user-id") Long userId,
            @Positive @PathVariable Long resourceId,
            @Parameter(description = "File to upload", required = true, content = @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(type = "string", format = "binary")
            ))
            @RequestPart("file") MultipartFile file) {
        ResourceDto result = resourceService.updateResource(resourceId, file);
        return ResponseEntity.ok(result);
    }

    @Operation(description = "Delete file by id")
    @DeleteMapping(path = "/{resourceId}")
    public ResponseEntity<String> deleteFile(
            @Parameter(description = "Auth header x-user-id, that will be TeamMemberId", required = true, name = "x-user-id")
            @RequestHeader(value = "x-user-id") Long userId,
            @Positive @PathVariable Long resourceId
    ) {
        resourceService.deleteResource(resourceId);
        return ResponseEntity.ok("Successfully delete resource");
    }

    @Operation(summary = "Get file by Id")
    @GetMapping("/{resourceId}")
    public ResponseEntity<InputStreamResource> downloadFile(@Positive @PathVariable Long resourceId) {
        S3Object s3Object = resourceService.getResource(resourceId);
        InputStream fileStream = s3Object.getObjectContent();
        InputStreamResource resource = new InputStreamResource(fileStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + s3Object.getBucketName() + "\"")
                .contentType(MediaType.valueOf(s3Object.getObjectMetadata().getContentType()))
                .body(resource);
    }
}
