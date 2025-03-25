package faang.school.projectservice.controller.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @GetMapping(path = "/{resourceId}", produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadResource(@PathVariable("resourceId") Long resourceId) {
        byte[] imageBytes = null;
        try {
            imageBytes = resourceService.downloadResource(resourceId).readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<String> deleteResource(@PathVariable("resourceId") Long resourceId) {
        resourceService.deleteResource(resourceId, userContext.getUserId());
        return ResponseEntity.ok("Resource deleted successfully");
    }

    @PostMapping("/{resourceId}")
    public ResponseEntity<ResourceDto> updateResource(@PathVariable("resourceId") Long resourceId,
                                                      @RequestBody MultipartFile file) {
        ResourceDto resourceDto = resourceService.updateResource(resourceId,
                userContext.getUserId(), file);
        return ResponseEntity.ok().body(resourceDto);
    }

    @PutMapping("/{projectId}/add")
    public ResponseEntity<ResourceDto> addResource(@PathVariable("projectId") Long projectId,
                                                   @RequestBody MultipartFile file) {
        ResourceDto resourceDto = resourceService.addResource(projectId, file);
        return ResponseEntity.ok().body(resourceDto);
    }
}
