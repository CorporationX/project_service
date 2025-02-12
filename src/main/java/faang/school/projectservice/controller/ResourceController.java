package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.resource.ResourceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("api/v1/projects/resources")
@RestController
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;
    private final ResourceMapper resourceMapper;

    @PostMapping("/{projectId}")
    public ResponseEntity<ResourceDto> addResource(
            @PathVariable @Positive Long projectId, @RequestBody MultipartFile file) {
        Long userId = userContext.getUserId();
        Resource resource = resourceService.addResource(projectId, userId, file);
        ResourceDto resourceDto = resourceMapper.toDto(resource);
        return ResponseEntity.ok(resourceDto);
    }

    @PatchMapping("/{projectId}/{resourceId}")
    public ResponseEntity<ResourceDto> updateResource(
            @PathVariable @Positive Long projectId,
            @PathVariable @Positive Long resourceId, @RequestBody MultipartFile file) {
        Long userId = userContext.getUserId();
        Resource resource = resourceService.updateResource(resourceId, projectId, userId, file);
        ResourceDto resourceDto = resourceMapper.toDto(resource);
        return ResponseEntity.ok(resourceDto);
    }

    @DeleteMapping("/{projectId}/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable @Positive Long projectId,
                                               @PathVariable @Positive Long resourceId) {
        Long userId = userContext.getUserId();
        resourceService.deleteResource(resourceId, projectId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{projectId}/{resourceId}")
    public ResponseEntity<InputStreamResource> downloadResource(
            @PathVariable @Positive Long projectId,
            @PathVariable @Positive Long resourceId) {
        Long userId = userContext.getUserId();
        S3FileDto file = resourceService.downloadResource(resourceId, projectId, userId);
        return ResponseEntity.ok()
                .contentLength(file.getContentLength())
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName())
                .body(file.getInputStreamResource());
    }

}
