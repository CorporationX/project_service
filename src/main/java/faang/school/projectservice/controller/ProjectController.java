package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.*;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.S3ObjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponse createProject(@Valid @RequestBody CreateProjectRequest createProjectRequest) {
        return projectService.createProject(createProjectRequest);
    }

    @PutMapping
    public ProjectResponse updateProject(@Valid @RequestBody UpdateProjectRequest updateProjectRequest) {
        return projectService.updateProject(updateProjectRequest);
    }

    @PostMapping("/{userId}/search")
    public List<ProjectResponse> filterProjects(@Valid @Positive @PathVariable Long userId,
                                                @RequestBody FilterProjectRequest filterProjectRequest) {
        return projectService.filterProjects(userId, filterProjectRequest);
    }

    @GetMapping("/{userId}/all")
    public List<ProjectResponse> getProjects(@Valid @Positive @PathVariable Long userId) {
        return projectService.getAllProjects(userId);
    }

    @DeleteMapping
    public void deleteProject(@Valid @RequestBody DeleteProjectRequest deleteProjectRequest) {
        projectService.deleteProject(deleteProjectRequest);
    }

    @GetMapping("/generate/pdf/{projectId}")
    public ResponseEntity<ResourceDto> createPdf(@Valid @NotNull @PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.createPdfFromProject(projectId));
    }

    @GetMapping("/download/pdf/{resourceId}")
    public ResponseEntity<InputStreamResource> downloadFile(@Valid @NotNull @PathVariable Long resourceId) {
        S3ObjectDto obj = projectService.downloadPdf(resourceId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + obj.fileName() + "\"." +obj.contentType())
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(obj.s3Object().getObjectContent()));
    }

}

