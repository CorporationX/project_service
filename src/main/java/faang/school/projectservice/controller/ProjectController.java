package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectForUpdateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectForCreationDto;
import faang.school.projectservice.dto.project.ProjectOutputDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectOutputDto create(@Valid @RequestBody ProjectForCreationDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping
    public ProjectOutputDto update(@Valid @RequestBody ProjectForUpdateDto projectDto) {
        return projectService.update(projectDto);
    }

    @GetMapping("/filtered")
    public List<ProjectOutputDto> getFilteredProjects(@Valid @RequestBody ProjectFilterDto projectDto) {
        return projectService.getFilteredProjects(projectDto);
    }

    @GetMapping("/all")
    public List<ProjectOutputDto> getAllProjects() {
        ProjectFilterDto emptyDto = ProjectFilterDto.builder().build();
        return projectService.getFilteredProjects(emptyDto);
    }

    @GetMapping("/{projectId}")
    public ProjectOutputDto getProjectById(@PathVariable long projectId) {
        return projectService.getProjectById(projectId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{projectId}/cover-image")
    public ProjectOutputDto uploadCoverImage(@PathVariable Long projectId, @RequestParam("file") MultipartFile file) {
        log.debug("Uploading cover image for project with id {} - Started", projectId);
        ProjectOutputDto projectWithCoverImage = projectService.uploadCoverImage(projectId, file);
        log.info("Uploading cover image for project with id {} - Finished", projectId);
        return projectWithCoverImage;
    }

    @DeleteMapping("/{projectId}/cover-image")
    public ProjectOutputDto deleteCoverImage(@PathVariable Long projectId) {
        log.debug("Deleting cover image for project with id {} - Started", projectId);
        ProjectOutputDto projectWithoutCoverImage = projectService.deleteCoverImage(projectId);
        log.info("Deleting cover image for project with id {} - Finished", projectId);
        return projectWithoutCoverImage;
    }

    @GetMapping("/{projectId}/cover-image")
    public ResponseEntity<byte[]> downloadCoverImage(@PathVariable Long projectId) {
        log.debug("Getting cover image for project with id {} - Started", projectId);
        byte[] coverImage = projectService.getCoverImage(projectId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        log.info("Getting cover image for project with id {} - Finished", projectId);
        return new ResponseEntity<>(coverImage, headers, HttpStatus.OK);
    }
}
