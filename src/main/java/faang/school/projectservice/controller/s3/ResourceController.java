package faang.school.projectservice.controller.s3;

import faang.school.projectservice.service.s3.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/v1/projects/{projectId}/resources")
@RestController
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> upload(@PathVariable Long projectId, @RequestParam MultipartFile file) {
        long resourceId = resourceService.upload(projectId, file);
        return ResponseEntity.ok(Map.of("resourceId", resourceId));
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId, @PathVariable Long resourceId) {
        resourceService.delete(projectId, resourceId);
        return ResponseEntity.noContent().build();
    }
}
