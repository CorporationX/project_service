package faang.school.projectservice.controller.resource;

import faang.school.projectservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/resource")
@RestController
@Tag(name = "ResourceController", description = "Controller for working with recources")
public class ResourceController {
    private final ResourceService resourceService;

    @Operation(description = "Upload a file using multipart/form-data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file upload request")
    })
    @PostMapping(path = "/{projectId}", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadFile(
            @PathVariable Long projectId,
            @Parameter(description = "File to upload", required = true, content = @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(type = "string", format = "binary")
            ))
            @RequestPart("file") MultipartFile file) {
        resourceService.addResource(projectId, file);
        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
    }


}
