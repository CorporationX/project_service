package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceFileDto;
import faang.school.projectservice.exception.ResourceHandlingException;
import faang.school.projectservice.service.projectresource.ProjectResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/resources")
@Tag(name = "Project Resources", description = "Manage project files (upload, download, delete)")
public class ProjectResourceController {

    private final ProjectResourceService projectResourceService;

    @Operation(summary = "Upload a file to a project", description = "Uploads a file and associates it with a given project ID")
    @PostMapping("/upload/{projectId}")
    public ResponseEntity<ResourceFileDto> uploadFile(
            @Parameter(description = "Project ID", example = "2")
            @PathVariable("projectId") Long projectId,

            @Parameter(description = "File to upload")
            @RequestParam("file") MultipartFile file) throws IOException {
        ResourceFileDto uploadedFile = projectResourceService.uploadFile(projectId, file);
        return ResponseEntity.ok(uploadedFile);
    }

    @Operation(summary = "Download a file", description = "Downloads a file by resource ID")
    @GetMapping("/download/{resourceId}")
    public ResponseEntity<InputStreamResource> downloadFile(
            @Parameter(description = "Resource ID", example = "10")
            @PathVariable("resourceId") Long resourceId) throws ResourceHandlingException {
        InputStream fileStream = projectResourceService.downloadFile(resourceId);
        ResourceFileDto resourceInfo = projectResourceService.getResourceInfo(resourceId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resourceInfo.getName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(new InputStreamResource(fileStream));
    }

    @Operation(summary = "Delete a file", description = "Deletes a file by resource ID")
    @DeleteMapping("/delete/{resourceId}")
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "Resource ID", example = "10")
            @PathVariable Long resourceId) throws AccessDeniedException, ResourceHandlingException {
        projectResourceService.deleteFile(resourceId);
        return ResponseEntity.noContent().build();
    }
}
