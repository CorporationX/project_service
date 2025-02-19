package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createProject(@RequestBody @Valid ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping()
    public ProjectDto updateProject(@RequestBody @Valid ProjectDto projectDto) {
        return projectService.updatedProject(projectDto);
    }

    @PostMapping("/filter/{userId}")
    public List<ProjectDto> getAllAvailableProjectsForUserWithFilter(@NotBlank @RequestBody ProjectFilterDto filter,
                                                                     @PathVariable Long userId) {
        return projectService.getAllAvailableProjectsForUserWithFilter(filter, userId);
    }

    @GetMapping("/user/{userId}")
    public List<ProjectDto> getAllAvailableProjectsForUser(@PathVariable Long userId) {
        return projectService.getAllAvailableProjectsForUser(userId);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable Long projectId) {
        return projectService.getProjectById(projectId);
    }

    @PostMapping("/{projectId}/cover")
    public ResponseEntity<String> addProjectCover(@PathVariable long projectId,
                                                  @RequestHeader(name = "x-user-id") Long userId,
                                                  @RequestParam MultipartFile file) {
        log.info("Endpoint <addProjectCover> called with URI='/api/v1/projects/{projectId}/cover'");
        return ResponseEntity.ok(projectService.addProjectCover(projectId, file, userId));
    }

    @DeleteMapping("/{projectId}/cover")
    public ResponseEntity<String> deleteProjectCover(@PathVariable long projectId,
                                                     @RequestHeader(name = "x-user-id") Long userId) {
        log.info("Endpoint <deleteProjectCover> called with URI='/api/v1/projects/{projectId}/cover'");
        return ResponseEntity.ok(projectService.deleteProjectCover(projectId, userId));
    }

    @GetMapping("/{projectId}/cover")
    public ResponseEntity<InputStreamResource> getProjectCover(@PathVariable long projectId) {
        log.info("Endpoint <getProjectCover> called with URI='/api/v1/projects/{projectId}/cover'");

        InputStream inputStream = projectService.getProjectCover(projectId);
        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}

