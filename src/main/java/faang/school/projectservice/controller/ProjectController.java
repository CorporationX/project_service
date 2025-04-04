package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectCoverService;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
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
    private final ProjectCoverService projectCoverService;

    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable long id) {
        log.info("Endpoint <getProjectById> called with URI='/api/v1/projects/{id}'");
        return projectService.getProjectById(id);
    }

    @PostMapping("/by-ids")
    List<ProjectDto> getProjectsByIds(@RequestBody List<Long> ids) {
        return projectService.getProjectsByIds(ids);
    }

    @PostMapping
    public ProjectDto createProject(@RequestBody @Valid ProjectDto projectDto) {
        log.info("Endpoint <createProject> called with URI='/api/v1/projects'");
        return projectService.createProject(projectDto);
    }

    @PutMapping
    public ProjectDto updateProject(@RequestBody @Valid ProjectDto projectDto) {
        log.info("Endpoint <updateProject> called with URI='/api/v1/projects'");
        return projectService.updateProject(projectDto);
    }

    @PostMapping("/user/{userId}/filter")
    public List<ProjectDto> getAllAvailableProjectsForUserWithFilter(@RequestBody ProjectFilterDto filter,
                                                                     @PathVariable Long userId) {
        log.info("Endpoint <getAllAvailableProjectsForUserWithFilter> called with URI='/api/v1/projects/user/{userId}/filter'");
        return projectService.getAllAvailableProjectsForUserWithFilter(filter, userId);
    }

    @GetMapping("/user/{userId}")
    public List<ProjectDto> getAllAvailableProjectsForUser(@PathVariable Long userId) {
        log.info("Endpoint <getAllAvailableProjectsForUser> called with URI='/api/v1/projects/user/{userId}'");
        return projectService.getAllAvailableProjectsForUser(userId);
    }

    @PostMapping("/{id}/cover")
    public ResponseEntity<String> addProjectCover(@PathVariable long id, @RequestParam MultipartFile file) {
        log.info("Endpoint <addProjectCover> called with URI='/api/v1/projects/{id}/cover'");
        return ResponseEntity.ok(projectCoverService.addProjectCover(id, file));
    }

    @DeleteMapping("/{id}/cover")
    public ResponseEntity<String> deleteProjectCover(@PathVariable long id) {
        log.info("Endpoint <deleteProjectCover> called with URI='/api/v1/projects/{id}/cover'");
        return ResponseEntity.ok(projectCoverService.deleteProjectCover(id));
    }

    @GetMapping("/{id}/cover")
    public ResponseEntity<InputStreamResource> getProjectCover(@PathVariable long id) {
        log.info("Endpoint <getProjectCover> called with URI='/api/v1/projects/{id}/cover'");

        InputStream inputStream = projectCoverService.getProjectCover(id);
        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
