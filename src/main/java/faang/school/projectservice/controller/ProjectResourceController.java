package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ProjectResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Tag(name = "Project Resources")
public class ProjectResourceController {

    private final ProjectResourceService projectResourceService;
    private final UserContext userContext;

    @PostMapping("/{projectId}/resource")
    public ResourceDto saveFile(@Positive @PathVariable long projectId,
                                @NotNull @RequestParam("file") MultipartFile file) {
        long currentUserId = userContext.getUserId();
        return projectResourceService.saveFile(projectId, currentUserId, file);
    }

    @GetMapping("/{projectId}/resource/{resourceId}")
    public InputStreamResource getFile(@Positive @PathVariable long projectId,
                                       @Positive @PathVariable long resourceId) {
        long currentUserId = userContext.getUserId();
        return projectResourceService.getFile(currentUserId, projectId, resourceId);
    }

    @DeleteMapping("/{projectId}/resource/{resourceId}")
    public void deleteFile(@Positive @PathVariable long projectId,
                           @Positive @PathVariable long resourceId) {
        long currentUserId = userContext.getUserId();
        projectResourceService.deleteFile(currentUserId, projectId, resourceId);
    }
}
