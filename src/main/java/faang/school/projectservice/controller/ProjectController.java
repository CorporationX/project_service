package faang.school.projectservice.controller;

import faang.school.projectservice.service.ProjectService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/projects/{projectId}/cover")
    public void uploadCover(@NotNull @PathVariable Long projectId,
                            @NotNull @RequestParam("cover") MultipartFile image) {
        projectService.uploadCover(projectId, image);
    }

    @DeleteMapping ("/projects/{projectId}/cover")
    public void deleteCover(@NotNull @PathVariable Long projectId) {
        projectService.deleteCover(projectId);
    }
}
