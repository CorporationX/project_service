package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ProjectResourceType;
import faang.school.projectservice.service.resource.ResourceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/resources/teamMember")
public class TeamMemberResourceController {
    private final ResourceService resourceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto uploadResource(
            @RequestParam
            @Positive(message = "TeamMemberId can't be negative")
            Long teamMemberId,
            @RequestParam
            @Positive(message = "ProjectId can't be negative")
            Long projectId,
            @RequestBody
            MultipartFile file) {
        return resourceService.uploadResource(projectId, teamMemberId, file, ProjectResourceType.PROJECT_RESOURCE);
    }

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> getResources(
            @RequestParam
            @Positive(message = "TeamMemberId can't be negative")
            Long teamMemberId,
            @RequestParam
            @Positive(message = "ProjectId can't be negative")
            Long resourceId) {
        return resourceService.getResources(teamMemberId, resourceId);
    }

    @DeleteMapping
    public void deleteFile(
            @RequestParam
            @Positive(message = "ProjectId can't be negative")
            Long teamMemberId,
            @RequestParam
            @Positive(message = "TeamMemberId can't be negative")
            Long resourceId) {
        resourceService.deleteResource(teamMemberId, resourceId);
    }
}
