package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ProjectResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Tag(name = "Project Resources")
public class ProjectResourceController {

    private final ProjectResourceService projectResourceService;

    @PostMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save new file to project")
    public ResourceDto saveFile(@Positive @PathVariable long projectId,
                                 @NotNull @RequestParam("file") MultipartFile file) {
        return projectResourceService.saveFile(projectId, file);
    }

    @GetMapping("/{resourceID}")
    @Operation(summary = "Get project file by id")
    public InputStream getFile(@Positive @PathVariable long resourceId) {
        return projectResourceService.getFile(resourceId);
    }

    @DeleteMapping("/{resourceId}")
    @Operation(summary = "Delete project file")
    public void deleteFile(@Positive @PathVariable long resourceId) {
        projectResourceService.deleteFile(resourceId);
    }
}
