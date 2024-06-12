package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectDtoRequest;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Validated
@Tag(name = "Project", description = "The Project API")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Create project", tags = "Project")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@RequestBody @Valid ProjectDtoRequest projectDtoRequest) {
        return projectService.create(projectDtoRequest);
    }

    @Operation(summary = "Update project", tags = "Project")
    @PutMapping("/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto update(@PathVariable Long projectId, @RequestBody @Valid ProjectDto projectDto) {
        return projectService.update(projectId, projectDto);
    }

    @Operation(summary = "Get filtered project. Filter conditions are passed in the request body.", tags = "Project")
    @GetMapping("/filtered")
    public List<ProjectDto> getFilteredProject(@RequestBody ProjectFilterDto filters) {
        return projectService.getFilteredProject(filters);
    }

    @Operation(summary = "Get all project", tags = "Project")
    @GetMapping()
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProject();
    }

    @Operation(summary = "Get project by id", tags = "Project")
    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable long projectId) {
        return projectService.findProjectById(projectId);
    }

    @Operation(summary = "Delete project by id", tags = "Project")
    @DeleteMapping("/{projectId}")
    public void delete(@PathVariable long projectId) {
        projectService.delete(projectId);
    }

    @Operation(summary = "Upload cover for Project", tags = "Project")
    @PostMapping("/{projectId}/cover")
    public ResponseEntity<String> uploadCover(@PathVariable @Min(value = 1, message = "Project ID must be more than 0") Long projectId,
                                              @RequestParam("cover") @NotNull MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        projectService.uploadFile(multipartFile, projectId, "cover");
        return ResponseEntity.ok("Image uploaded: " + fileName);
    }
}
