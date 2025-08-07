package faang.school.projectservice.controller.project.presentation;

import faang.school.projectservice.dto.resource.S3FileResponse;
import faang.school.projectservice.service.presentation.ProjectPresentationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/presentation")
@Tag(name = "Project Presentation", description = "PDF generation and download")
public class ProjectPresentationController {

    private final ProjectPresentationService projectPresentationService;

    @PostMapping
    @Operation(
            summary = "Generate project presentation",
            description = "Generates a PDF presentation for the specified project"
    )
    public ResponseEntity<Void> create(@PathVariable @Positive Long projectId) {
        projectPresentationService.create(projectId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    @Operation(
            summary = "Download project presentation",
            description = "Returns the generated PDF presentation for the specified project"
    )
    public ResponseEntity<InputStreamResource> getPresentation(@PathVariable @Positive Long projectId) {
        S3FileResponse file = projectPresentationService.downloadPresentation(projectId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.fileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file.inputStream());
    }
}
