package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/project/")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PutMapping("{projectId}/image")
    public ResponseEntity<ProjectDto> addImage(@PathVariable Long projectId, @RequestBody MultipartFile file){
        return ResponseEntity.status(HttpStatus.OK).body(projectService.addImage(projectId, file));
    }

    @GetMapping("{projectId}")
    public ResponseEntity<?> getImage(@PathVariable long projectId) {
        val body = projectService.getImage(projectId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(body.toByteArray());
    }

    @DeleteMapping("{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteImage(@PathVariable Long projectId) {
        projectService.deleteImage(projectId);
    }

    @PostMapping("{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@PathVariable Long userId, @Validated @RequestBody ProjectDto projectDto) {
        return projectService.create(userId, projectDto);
    }

    @PutMapping("")
    public ProjectDto update(@Validated @RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @PostMapping("{userId}/accessible/filtered")
    public List<ProjectDto> findProjectsWithFilter(@PathVariable Long userId,
                                                   @Validated @NotNull @RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.findProjectsWithFilter(userId, projectFilterDto);
    }

    @GetMapping("{userId}/accessible/projects")
    public List<ProjectDto> findAllProjects(@PathVariable Long userId) {
        return projectService.findAllProjects(userId);
    }

    @PostMapping("{userId}/accessible/project")
    public  ProjectDto findById(@PathVariable Long userId,
                                @Validated @RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.findById(userId, projectFilterDto);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProject(@PathVariable Long projectId) {
        return projectService.getProject(projectId);
    }
}
