package faang.school.projectservice.controller.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping
@RestController
@Tag(name = "ProjectController", description = "Controller for working with projects")
public class ProjectController {

    @Operation(description = "Upload a file using multipart/form-data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file upload request")
    })
    @PostMapping(path = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "File to upload", required = true, content = @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(type = "string", format = "binary")
            ))
            @RequestPart("file") MultipartFile file) {

        // Логика обработки файла
        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
    }


}
