package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.service.ResourceService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @PostMapping("/")
    public ResourceResponseDto addResource(@RequestParam Long projectId,
                                           @RequestBody MultipartFile file) {
        log.info("Uploading image {} to project {}", file.getName(), projectId);
        Long userId = userContext.getUserId();
        return resourceService.addResource(userId, projectId, file);
    }

    @GetMapping(path = "/{resourceId}", produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadResource(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                   @NotBlank @PathVariable Long resourceId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        byte[] imageBytes = null;
        try {
            imageBytes = resourceService.downloadResource(userId, resourceId).readAllBytes();
        } catch (IOException e) {
            log.error("Error downloading file resource {}, error: {}", resourceId, e.toString());
            return new ResponseEntity<>(imageBytes, httpHeaders, HttpStatus.NOT_FOUND);
        }
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/{resourceId}")
    public void deleteResource(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                               @NotBlank @PathVariable Long resourceId) {
        resourceService.deleteResource(userId, resourceId);
        log.info("Image with id {} was successfully deleted", resourceId);
    }
}
