package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.ProjectResource;
import faang.school.projectservice.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ResourceMapper resourceMapper;

    @Operation(summary = "Upload cover to project", description = "Upload cover's file to project using multipart/form-data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cover's file uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid project ID or cover's file format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("{project-id}/cover")
    String uploadCover(@PathVariable("project-id") @NotBlank Long projectId,
                       @RequestParam("user-id") Long userId,
                       @RequestBody @NotNull MultipartFile cover) {
        return projectService.uploadCover(projectId, userId, cover);
    }

//    @Operation(summary = "Delete cover from project", description = "Delete a cover's file from a project by project ID, user ID")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Cover's file deleted successfully"),
//            @ApiResponse(responseCode = "400", description = "Invalid project"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    @DeleteMapping("{project-id}/cover")
//    void removeCover(@PathVariable @NotBlank Long projectId,
//                     @RequestParam("user-id") Long userId) {
//        projectService.removeCover(userId, projectId);
//    }

    @Operation(summary = "Upload file to project", description = "Upload file to project using multipart/form-data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid project ID or file format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(path = "{project-id}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResourceDto uploadFileToProject(
            @PathVariable("project-id") @Positive Long projectId,
            @RequestParam("user-id") Long userId,
            @RequestBody MultipartFile file) {
        ProjectResource resource = projectService.uploadFileToProject(projectId, userId, file);
        return resourceMapper.toResourceDto(resource);
    }


    @Operation(summary = "Delete file from project", description = "Delete a file from a project by project ID, user ID, and resource ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid project or resource ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("{project-id}/files")
    public void deleteFileFromProject(
            @PathVariable("project-id") Long projectId,
            @RequestParam("user-id") Long userId,
            @RequestParam("resource-id") Long resourceId) {
        projectService.deleteFileFromProject(projectId, userId, resourceId);
    }


    @Operation(summary = "Get file from project", description = "Retrieve a file from a project by project ID, user ID, and resource ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid project or resource ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("{project-id}/files")
    public ResponseEntity<Resource> getFileFromProject(
            @PathVariable("project-id") Long projectId,
            @RequestParam("user-id") Long userId,
            @RequestParam("resource-id") Long resourceId) {
        Pair<Resource, ProjectResource> resourceInfo = projectService.getFileFromProject(projectId, userId, resourceId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceInfo.getSecond().getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resourceInfo.getSecond().getName() + "\"")
                .body(resourceInfo.getFirst());
    }

}