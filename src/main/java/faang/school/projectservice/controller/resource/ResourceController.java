package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceResultDto;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResourceResultDto> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectId") Long projectId,
            @RequestParam("teamMemberId") Long teamMemberId
    ) {
        return ResponseEntity.ok(resourceService.uploadResource(file, projectId, teamMemberId));
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long resourceId,
            @RequestParam("teamMemberId") Long teamMemberId
    ) {
        resourceService.deleteResource(resourceId, teamMemberId);
        return ResponseEntity.noContent().build();
    }
}
