package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/resources/v1")
@RequiredArgsConstructor
@Validated
@Tag(name = "Resources", description = "Resource handler")
public class ResourceController {
    private final ResourceService resourceService;

    @Operation(summary = "Create new resource")
    @PostMapping("/project/{projectId}/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto add(@PathVariable @Min(1L) Long projectId,
                           @RequestHeader(value = "x-user-id") String userid,
                           @RequestParam(value = "allowedTeamRoles", required = false) List<TeamRole> allowedTeamRoles,
                           @RequestBody MultipartFile file) {

        return resourceService.add(file, allowedTeamRoles, projectId);
    }

    @Operation(summary = "Update resource")
    @PutMapping("/project/{projectId}/update/{resourceId}")
    @ResponseStatus(HttpStatus.OK)
    public ResourceDto update(@PathVariable @Min(1L) Long projectId,
                              @PathVariable @Min(1L) Long resourceId,
                              @RequestHeader(value = "x-user-id") String userid,
                              @RequestParam(value = "allowedTeamRoles", required = false) List<TeamRole> allowedTeamRoles,
                              @RequestParam(value = "resourceStatus", required = false) ResourceStatus resourceStatus,
                              @RequestBody(required = false) MultipartFile file) {

        return resourceService.update(file, allowedTeamRoles, projectId, resourceId, resourceStatus);
    }

    @Operation(summary = "Get resource")
    @GetMapping("/project/{projectId}/get/{resourceId}")
    public ResponseEntity<InputStreamResource> get(@PathVariable @Min(1L) Long projectId,
                                                   @RequestHeader(value = "x-user-id") String userid,
                                                   @PathVariable @Min(1L) Long resourceId) {
        return resourceService.get(projectId, resourceId);
    }

    @Operation(summary = "Delete resource")
    @DeleteMapping("/project/{projectId}/remove/{resourceId}")
    @ResponseStatus(HttpStatus.OK)
    public ResourceDto remove(@PathVariable @Min(1L) Long projectId,
                              @RequestHeader(value = "x-user-id") String userid,
                              @PathVariable @Min(1L) Long resourceId) {
        return resourceService.remove(projectId, resourceId);
    }
}
