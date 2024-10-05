package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import jakarta.validation.constraints.Positive;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@Validated
public class ProjectController {
    private final ProjectService projectService;
    private final UserContext userContext;

    @GetMapping("/{projectId}")
    public Project getProject(@Positive @PathVariable long projectId) {
        return projectService.getProject(projectId);
    }

    @PatchMapping("/{projectId}/cover")
    public ProjectDto uploadCover(@Positive @PathVariable long projectId, @RequestParam MultipartFile file){
        long userId = userContext.getUserId();

        return projectService.uploadCover(projectId, file, userId);
    }
}
