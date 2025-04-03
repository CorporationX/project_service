package faang.school.projectservice.controller;

import faang.school.projectservice.service.ProjectService;
import jakarta.validation.constraints.NotNull;
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
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/cover")
    public ResponseEntity<Void> uploadCover(@NotNull @PathVariable Long projectId,
                                            @NotNull @RequestParam("cover") MultipartFile image) {
        projectService.uploadCover(projectId, image);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cover")
    public ResponseEntity<Void> deleteCover(@NotNull @PathVariable Long projectId) {
        projectService.deleteCover(projectId);
        return ResponseEntity.noContent().build();
    }
}
