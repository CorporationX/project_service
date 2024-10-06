package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.resource.ResourceDto;
import faang.school.projectservice.dto.response.ResourceResponseObject;
import faang.school.projectservice.service.resource.ResourceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping("/{resourceId}")
    public ResponseEntity<Resource> getResource(@PathVariable @Positive Long resourceId) {
        ResourceResponseObject object = resourceService.getResourceById(resourceId);
        try (InputStream inputStream = object.inputStream()) {
            var resource = new ByteArrayResource(inputStream.readAllBytes());
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.valueOf(object.contentType()))
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/{resourceId}")
    public void deleteResource(@PathVariable @Positive Long resourceId) {
        resourceService.deleteResourceById(resourceId);
    }

    @PutMapping("/{resourceId}")
    public ResourceDto updateResource(@PathVariable @Positive Long resourceId,
                                      @RequestBody MultipartFile file) {
        return resourceService.updateResourceById(resourceId, file);

    }

    @PostMapping("/projects/{projectId}")
    public ResourceDto addResource(@PathVariable @Positive Long projectId,
                                   @RequestBody MultipartFile file) {
        return resourceService.addResourceToProject(projectId, file);
    }
}
