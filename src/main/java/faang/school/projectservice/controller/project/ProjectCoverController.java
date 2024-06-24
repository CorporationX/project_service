package faang.school.projectservice.controller.project;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("projects")
public class ProjectCoverController {
    private final ProjectService projectService;
    
    @PostMapping("/{projectId}/cover")
    public ResourceDto uploadCover(
        @PathVariable @NotNull(message = "Project id can't be null")
        @PositiveOrZero(message = "Project id can't be negative")
        Long projectId,
        @RequestParam
        @NotNull(message = "Uploading team member Id Id can't be null")
        @PositiveOrZero(message = "Uploading team member Id can't be negative")
        Long teamMemberId,
        @NotNull
        @RequestPart
        MultipartFile file
    ) {
        return projectService.uploadCover(projectId, teamMemberId, file);
    }
    
    @DeleteMapping("/{projectId}/cover")
    public void deleteCover(
        @PathVariable @NotNull(message = "Project id can't be null") @PositiveOrZero(message = "Project id can't be negative") Long projectId
    ) {
        projectService.deleteCover(projectId);
    }
}
