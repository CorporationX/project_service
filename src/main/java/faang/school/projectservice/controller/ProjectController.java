package faang.school.projectservice.controller;

import faang.school.projectservice.service.ProjectService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PutMapping("/projects/{projectId}/cover")
    @ResponseStatus(HttpStatus.CREATED)
    String uploadCover(@PathVariable @NotBlank Long projectId, @RequestParam @NotNull MultipartFile cover) {
        return projectService.uploadCover(projectId, cover);
    }

    @DeleteMapping("/projects/{projectId}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeResourceCover(@PathVariable @NotBlank Long projectId) {
        projectService.removeResourceCover(projectId);
    }
}
