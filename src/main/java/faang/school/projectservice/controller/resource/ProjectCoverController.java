package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectCoverController {

    private final ProjectService projectService;

    @PutMapping("/{projectId}/cover")
    public ResponseEntity<ProjectDto> addProjectCover(@PathVariable("projectId") Long projectId,
                                                      @RequestPart("file") MultipartFile file) {
        ProjectDto projectDto = projectService.addCoverImage(projectId, file);
        return ResponseEntity.ok(projectDto);
    }

    /*private final ResourceService resourceService;

    @PutMapping("/{projectId}/add")
    public ResponseEntity<ResourceDto> addProjectCover(@PathVariable("projectId") Long projectId,
                                                       @RequestBody MultipartFile file) {
        ResourceDto resourceDto = resourceService.addResource(projectId, file);
        return ResponseEntity.ok().body(resourceDto);
    }

    @PostMapping("/{resourceId}")
    public ResponseEntity<ResourceDto> updateProjectCover(@PathVariable("resourceId") Long resourceId,
                                                          @RequestBody MultipartFile file) {
        ResourceDto resourceDto = resourceService.updateResource(resourceId, file);
        return ResponseEntity.ok().body(resourceDto);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<String> deleteProjectCover(@PathVariable("resourceId") Long resourceId) {
        resourceService.deleteResource(resourceId);
        return ResponseEntity.ok("Resource deleted successfully");
    }

    @GetMapping(path = "/{resourceId}", produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadProjectCover(@PathVariable("resourceId") Long resourceId) {
        byte[] imageBytes = null;
        try {
            imageBytes = resourceService.downloadResource(resourceId).readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }*/
}
