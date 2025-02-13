package faang.school.projectservice.controller;


import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.dto.project.ProjectDtoResponse;
import faang.school.projectservice.dto.resource.S3ObjectDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/{projectId}/presentation")
    public ProjectDtoResponse generatePdf(@PathVariable Long projectId) {

        return projectService.creatingPresentation(projectId);
    }

    @GetMapping("/download/{projectId}/presentation")
    public ResponseEntity<InputStreamResource> downloadFile(@Valid @NotNull @PathVariable Long projectId) {

        S3ObjectDto obj = projectService.downloadPdf(projectId);
        S3ObjectInputStream objectContent = obj.s3Object().getObjectContent();
        InputStreamResource body = new InputStreamResource(objectContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + obj.fileName() + "\"." + obj.contentType())
                .contentType(MediaType.APPLICATION_PDF)
                .body(body);
    }
}
