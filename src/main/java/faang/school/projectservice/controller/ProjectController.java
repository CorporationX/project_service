package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping()
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping()
    public ProjectDto updateProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }

    @GetMapping("/{id}")
    public ProjectDto findProjectById(@PathVariable Long id) {
        return projectService.findProjectById(id);
    }

    @GetMapping("/filter")
    public List<ProjectDto> getProjectByFilter(@RequestBody ProjectFilterDto filters) {
        return projectService.getProjectByFilter(filters);
    }

    @GetMapping()
    public List<ProjectDto> getAllProject() {
        return projectService.getAllProject();
    }

    @PutMapping("/{projectId}/addCover")
    public String addCoverToProject(@PathVariable Long projectId, @RequestPart(value = "cover") MultipartFile cover) {
        log.debug("Received request to upload cover for project with id: {} from file: {}",
                projectId, cover.getName());
        return projectService.addCoverProject(projectId, cover);
    }

    @GetMapping(value = "/{projectId}/getCover", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> getProjectCover(@PathVariable Long projectId) {
        log.debug("Received request to get cover for project with id: {}", projectId);
        InputStream responseBody = projectService.getProjectCover(projectId);
        return ResponseEntity.ok().body(new InputStreamResource(responseBody));
    }

    @DeleteMapping("/{projectId}/deleteCover")
    public ResponseEntity<String> deleteProjectCover(@PathVariable Long projectId) {
        log.debug("Received request to delete cover for project with id: {}", projectId);
        projectService.deleteCover(projectId);
        return ResponseEntity.ok().body("Project cover has been successfully deleted");
    }

    @GetMapping("/{projectId}/viewCover")
    public ResponseEntity<byte[]> viewProjectCover(@PathVariable Long projectId) {
        log.debug("Received request to get cover for project with id: {}", projectId);
        byte[] responseBody;
        try {
            responseBody = projectService.getProjectCover(projectId).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
    }
}