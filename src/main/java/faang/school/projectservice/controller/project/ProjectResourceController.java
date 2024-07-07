package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ProjectResourceType;
import faang.school.projectservice.service.resource.ResourceService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("resources")
public class ProjectResourceController {
    private final ResourceService resourceService;
    
    @PostMapping("{projectId}")
    public ResourceDto uploadResource(
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
        return resourceService.uploadResource(projectId, teamMemberId, file, ProjectResourceType.PROJECT_RESOURCE);
    }
    
    @PostMapping("{projectId}/cover")
    public ResourceDto uploadProjectCover(
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
        return resourceService.uploadProjectCover(projectId, teamMemberId, file);
    }
    
    @DeleteMapping("{projectId}/cover")
    public void deleteProjectCover(
        @PathVariable @NotNull(message = "Project id can't be null") @PositiveOrZero(message = "Project id can't be negative") Long projectId
    ) {
        resourceService.deleteProjectCover(projectId);
    }
}
