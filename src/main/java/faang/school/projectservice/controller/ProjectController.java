package faang.school.projectservice.controller;

import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("{project-id}/upload")
    public void uploadFileToProject(@PathVariable("project-id") Long projectId,
                                    @RequestParam("user-id") Long userId,
                                    @RequestBody MultipartFile file) {
        projectService.uploadFileToProject(projectId, userId, file);
    }
}
