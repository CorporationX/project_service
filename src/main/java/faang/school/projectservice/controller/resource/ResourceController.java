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
@RequestMapping("/resources")
@RequiredArgsConstructor
@Validated
@Tag(name = "Resources", description = "Resource handler")
public class ResourceController {
    private final ResourceService resourceService;

    @Operation(summary = "Create new resource")
    @PostMapping("/project/{projectId}/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto add(@PathVariable @Min(value = 1L, message = "Project id cannot be less than 1") Long projectId,
                           @RequestHeader(value = "x-user-id") String userid,
                           @RequestParam(value = "allowedTeamRoles", required = false) List<TeamRole> allowedTeamRoles,
                           @RequestBody MultipartFile file) {

        return resourceService.add(file, allowedTeamRoles, projectId);
    }

    @Operation(summary = "Update resource")
    @PutMapping("/project/{projectId}/update/{resourceId}")
    @ResponseStatus(HttpStatus.OK)
    public ResourceDto update(@PathVariable @Min(value = 1L, message = "Project id cannot be less than 1") Long projectId,
                              @PathVariable @Min(value = 1L, message = "Resource id cannot be less than 1") Long resourceId,
                              @RequestHeader(value = "x-user-id") String userid,
                              @RequestParam(value = "allowedTeamRoles", required = false) List<TeamRole> allowedTeamRoles,
                              @RequestParam(value = "resourceStatus", required = false) ResourceStatus resourceStatus,
                              @RequestBody(required = false) MultipartFile file) {

        return resourceService.update(file, allowedTeamRoles, projectId, resourceId, resourceStatus);
    }

    @Operation(summary = "Get resource")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<InputStreamResource> get(@PathVariable @Min(value = 1L, message = "Project id cannot be less than 1") Long projectId,
                                                   @RequestHeader(value = "x-user-id") String userid,
                                                   @RequestParam(value = "resourceId") @Min(value = 1L, message = "Resource id cannot be less than 1") Long resourceId) {
        return resourceService.get(projectId, resourceId);
    }

    @Operation(summary = "Delete resource")
    @DeleteMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ResourceDto remove(@PathVariable @Min(value = 1L, message = "Project id cannot be less than 1") Long projectId,
                              @RequestHeader(value = "x-user-id") String userid,
                              @RequestParam(value = "resourceId") @Min(value = 1L, message = "Resource id cannot be less than 1") Long resourceId) {
        return resourceService.remove(projectId, resourceId);
    }
}
