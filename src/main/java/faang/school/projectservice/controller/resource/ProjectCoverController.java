package faang.school.projectservice.controller.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProjectCoverController {

    private final ProjectService projectService;

    @PutMapping("/{projectId}/cover")
    public ResponseEntity<ProjectDto> addProjectCover(@PathVariable("projectId") Long projectId,
                                                      @RequestPart("file") MultipartFile file) {
        Project updatedProject = projectService.addCoverImage(projectId, file);
        ProjectDto projectDto = mapToDto(updatedProject); // Предполагаем маппер
        return ResponseEntity.ok(projectDto);
    }

    private ProjectDto mapToDto(Project project) {
        // Здесь должен быть маппер (например, MapStruct), пока вручную
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setCoverImageId(project.getCoverImageId());
        // Другие поля...
        return dto;
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
