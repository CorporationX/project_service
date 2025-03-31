package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/{projectId}/cover")
    public ResponseEntity<ProjectDto> addProjectCover(@PathVariable("projectId") Long projectId,
                                                      @RequestPart("file") MultipartFile file) {
        ProjectDto projectDto = projectService.addCoverImage(projectId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectDto);
    }

    @PutMapping("/{projectId}/cover")
    public ResponseEntity<ProjectDto> updateProjectCover(@PathVariable("projectId") Long projectId,
                                                         @RequestPart("file") MultipartFile file) {
        ProjectDto projectDto = projectService.updateCoverImage(projectId, file);
        return ResponseEntity.ok(projectDto);
    }

    @DeleteMapping("/{projectId}/cover/soft")
    public ResponseEntity<ProjectDto> softDeleteProjectCover(@PathVariable("projectId") Long projectId) {
        ProjectDto projectDto = projectService.softDeleteCoverImage(projectId);
        return ResponseEntity.ok(projectDto);
    }

    @DeleteMapping("/{projectId}/cover/hard")
    public ResponseEntity<?> hardDeleteProjectCover(@PathVariable("projectId") Long projectId) {
        projectService.hardDeleteCoverImage(projectId);
        return ResponseEntity.noContent().build();
    }
}
