package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.S3ObjectDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/{projectId}/presentation")
    public void generatePdf(@PathVariable Long projectId) {
        projectService.createPresentation(projectId);
    }

    @GetMapping("/{projectId}/presentation/download")
    public ResponseEntity<InputStreamResource> downloadFile(@Valid @NotNull @PathVariable Long projectId) {
        S3ObjectDto obj = projectService.downloadPdf(projectId);
        InputStreamResource body = projectService.getPresentation(obj);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + obj.fileName() + "\"." + obj.contentType())
                .contentType(MediaType.APPLICATION_PDF)
                .body(body);
    }
}
