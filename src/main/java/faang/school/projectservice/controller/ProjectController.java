package faang.school.projectservice.controller;

import faang.school.projectservice.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Validated
@Tag(name = "Project", description = "Operations related to project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/{projectId}/cover")
    @Operation(summary = "Upload project cover", description = "Uploads a cover image for the specified project")
    public ResponseEntity<String> uploadCover(@PathVariable @Min(value = 1, message = "Project ID must be more than 0") Long projectId,
                                              @RequestParam("cover") @NotNull MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        projectService.uploadFile(multipartFile, projectId, "cover");
        return ResponseEntity.ok("Image uploaded: " + fileName);
    }
}
