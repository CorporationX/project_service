package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
@Tag(name = "Resource API", description = "Endpoints for managing project files")
@Slf4j
@Validated
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/{projectId}")
    @Operation(summary = "Add project file to storage")
    public ResponseEntity<Map<String, Object>> uploadResource(
            @PathVariable
                @Positive(message = "Project id must be a positive integer") Long projectId,
            @RequestParam(name = "userId")
                @Positive(message = "User id must be a positive integer") Long userId,
            @RequestParam(name = "file")
            @NotNull(message = "You should add a file") MultipartFile file
            ) {
        log.info("Request to upload of {} file in the project id: {} received", file.getName(), projectId);
        ResourceResponseDto resourceResponseDto = resourceService.uploadResource(projectId, userId, file);
        Map<String, Object> response = new HashMap<>();
        response.put("data", resourceResponseDto);
        response.put("message", String.format("File %s uploaded successfully", file.getName()));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{projectId}/{resourceId}")
    @Operation(summary = "Delete project file from storage")
    public ResponseEntity<Map<String, String>> deleteResource(
            @PathVariable
            @Positive(message = "Project id must be a positive integer") Long projectId,
            @PathVariable
            @Positive(message = "File id must be a positive integer") Long resourceId,
            @RequestParam(name = "userId")
            @Positive(message = "User id must be a positive integer") Long userId
    ) {
        log.info("Request to delete of file id: {} in the project id: {} received", resourceId, projectId);
        resourceService.deleteResource(projectId, resourceId, userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", String.format("File id: %d was deleted successfully", resourceId));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{projectId}/{resourceId}")
    @Operation(summary = "Update project file in storage")
    public ResponseEntity<Map<String, Object>> updateResource(
            @PathVariable
            @Positive(message = "Project id must be a positive integer") Long projectId,
            @PathVariable
            @Positive(message = "File id must be a positive integer") Long resourceId,
            @RequestParam(name = "userId")
            @Positive(message = "User id must be a positive integer") Long userId,
            @RequestParam(name = "file")
            @NotNull(message = "You should add a file") MultipartFile file
    ) {
        log.info("Request to update file id {} in the project id: {} received", resourceId, projectId);
        ResourceResponseDto resourceResponseDto = resourceService.updateResource(projectId, resourceId, userId, file);
        Map<String, Object> response = new HashMap<>();
        response.put("data", resourceResponseDto);
        response.put("message", String.format("File id: %d updated successfully", resourceResponseDto.getId()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}/{resourceId}")
    @Operation(summary = "Get project file from storage")
    public ResponseEntity<byte[]> getResource(
            @PathVariable
            @Positive(message = "Project id must be a positive integer") Long projectId,
            @PathVariable
            @Positive(message = "File id must be a positive integer") Long resourceId,
            @RequestParam(name = "userId")
            @Positive(message = "User id must be a positive integer") Long userId
    ) {
        log.info("Request to get file id {} in the project id: {} received", resourceId, projectId);

        byte[] resource = resourceService.downloadResource(projectId, resourceId, userId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; resourceId=\"" + resourceId + "\"");

        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
