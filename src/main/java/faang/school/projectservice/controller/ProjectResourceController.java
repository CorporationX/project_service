package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ProjectResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Project Resources")
public class ProjectResourceController {

    private final ProjectResourceService projectResourceService;
    private final UserContext userContext;

    @Operation(
            summary = "Save new file to project",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}
    )
    @PostMapping("/projects/{projectId}/resources")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto saveFile(@Positive @PathVariable long projectId,
                                @NotNull @RequestParam("file") MultipartFile file) {
        long currentUserId = userContext.getUserId();
        return projectResourceService.saveFile(currentUserId, projectId, file);
    }

    @Operation(
            summary = "Get project file by id",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}
    )
    @GetMapping("/projects/{projectId}/resources/{resourceId}")
    public InputStreamResource getFile(@Positive @PathVariable long projectId,
                                       @Positive @PathVariable long resourceId) {
        long currentUserId = userContext.getUserId();
        return projectResourceService.getFile(currentUserId, projectId, resourceId);
    }

    @Operation(
            summary = "Delete project file",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}
    )
    @DeleteMapping("/projects/{projectId}/resources/{resourceId}")
    public void deleteFile(@Positive @PathVariable long projectId,
                           @Positive @PathVariable long resourceId) {
        long currentUserId = userContext.getUserId();
        projectResourceService.deleteFile(currentUserId, projectId, resourceId);
    }
}
