package faang.school.projectservice.controller;

import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Validated
public class ProjectController {
    private final ProjectService projectService;

    @PutMapping("/{projectId}/cover-image")
    public ProjectDto uploadCoverImage(@PathVariable long projectId, @RequestParam @NotNull MultipartFile file) {
        return projectService.uploadCoverImage(projectId, file);
    }

    @GetMapping("/{projectId}/cover-image")
    public InputStream downloadCoverImageBy(@PathVariable long projectId) {
        return projectService.downloadCoverImage(projectId);
    }

    @DeleteMapping("/{projectId}/cover-image")
    public ProjectDto deleteCoverImage(@PathVariable @NotBlank long projectId) {
        return projectService.deleteCoverImage(projectId);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProject(@PathVariable long projectId) {
        return projectService.getProject(projectId);
    }
}