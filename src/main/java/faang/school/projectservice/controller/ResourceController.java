package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/api/v1")
public class ResourceController {
    private final ResourceService resourceService;

    @PutMapping("/{projectId}/add")
    public ResourceDto addResource(@PathVariable Long projectId, @RequestBody MultipartFile file) {
        return resourceService.addResource(projectId, file);
    }

//    @PostMapping("/{resourceId}")
//    public ResourceDto updateResource(@PathVariable Long resourceId, @RequestBody MultipartFile file) {
//        return resourceService.updateResource();
//    }
//
//    @DeleteMapping("{/resourceId}")
//    public ResponseEntity<String> deleteResource(@PathVariable Long resourceId) {
//        resourceService.deleteResource();
//        return ResponseEntity.ok("Resource deleted");
//    }
}
