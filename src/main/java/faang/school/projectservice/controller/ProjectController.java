package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.project.ProjectService;
import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ResourceMapper resourceMapper;

//////////////////////////////////////////////////////////////////////////////////////////////////
    @Operation(summary = "Upload file to project", description = "Upload file to project using multipart/form-data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid project ID or file format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(path = "{project-id}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResourceDto uploadFileToProject(
            @Parameter(description = "ID of the project", required = true)
            @PathVariable("project-id") @Positive Long projectId,

            @Parameter(description = "ID of the user", required = true)
            @RequestParam("user-id") Long userId,

            @Parameter(description = "File to upload", required = true, content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary")))
            @RequestBody MultipartFile file) {
        Resource resource = projectService.uploadFileToProject(projectId, userId, file);
        return resourceMapper.toResourceDto(resource);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////
    @Operation(summary = "Delete file from project", description = "Delete a file from a project by project ID, user ID, and resource ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid project or resource ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("{project-id}/files")
    public void deleteFileFromProject(
            @Parameter(description = "ID of the project", required = true)
            @PathVariable("project-id") Long projectId,

            @Parameter(description = "ID of the user", required = true)
            @RequestParam("user-id") Long userId,

            @Parameter(description = "ID of the resource to delete", required = true)
            @RequestParam("resource-id") Long resourceId) {
        projectService.deleteFileFromProject(projectId, userId, resourceId);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////
    @Operation(summary = "Get file from project", description = "Retrieve a file from a project by project ID, user ID, and resource ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid project or resource ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("{project-id}/files")
    public ResponseEntity<InputStreamResource> getFileFromProject(
            @Parameter(description = "ID of the project", required = true)
            @PathVariable("project-id") Long projectId,

            @Parameter(description = "ID of the user", required = true)
            @RequestParam("user-id") Long userId,

            @Parameter(description = "ID of the resource to retrieve", required = true)
            @RequestParam("resource-id") Long resourceId) {

        Map<Resource, InputStream> resourceWithFileStream = projectService.getFileFromProject(projectId, userId, resourceId);
        Resource resource = resourceWithFileStream.keySet().iterator().next();
        InputStream fileStream = resourceWithFileStream.get(resource);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getName() + "\"")
                .body(new InputStreamResource(fileStream));
    }

//////////////////////////////////////////////////////////////////////////////////////////////////
}
