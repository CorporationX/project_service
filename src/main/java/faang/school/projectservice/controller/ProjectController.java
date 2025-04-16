package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.service.ProjectService;
import lombok.Data;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("gallery")
@Data
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/project/{projectId}/upload")
    public ResponseEntity<ResourceReadDto> uploadResource(
            @PathVariable long projectId,
            @RequestParam("files") MultipartFile file)  {
        System.out.println();
        return ResponseEntity.ok(projectService.uploadResourceToGallery(projectId, file));
    }
}
