package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.dto.resource.ResourceUpdateDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @PutMapping("/{projectId}/upload/{memberId}")
    public ResourceResponseDto upload(@RequestParam("file") MultipartFile file,
                                      @PathVariable Long projectId, @PathVariable Long memberId) {
        return resourceService.saveResource(file, projectId, memberId);
    }

    @PostMapping("/update")
    public ResourceResponseDto reload(@RequestBody ResourceUpdateDto resourceUpdateDto) {
        return resourceService.updateFileInfo(resourceUpdateDto);
    }

    @DeleteMapping("/{resourceId}/{teamMemberId}")
    public ResponseEntity<String> delete(@PathVariable Long resourceId, @PathVariable Long teamMemberId) {
        resourceService.deleteFile(resourceId, teamMemberId);
        return ResponseEntity.ok("File was successfully deleted!");
    }

    @GetMapping("/{resourceId}")
    public MultipartFile getResource(@PathVariable Long resourceId) {
        return resourceService.downloadFile(resourceId);
    }
}
