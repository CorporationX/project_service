package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectDtoRequest;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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


@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@RequestBody @Valid ProjectDtoRequest projectDtoRequest) {
        return projectService.create(projectDtoRequest);
    }

    @PutMapping("/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto update(@PathVariable Long projectId, @RequestBody @Valid ProjectDto projectDto) {
        return projectService.update(projectId, projectDto);
    }

    @GetMapping("/filtered")
    public List<ProjectDto> getFilteredProject(@RequestBody ProjectFilterDto filters) {
        return projectService.getFilteredProject(filters);
    }

    @GetMapping()
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProject();
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable long projectId) {
        return projectService.findById(projectId);
    }

    @DeleteMapping("/{projectId}")
    public void delete(@PathVariable long projectId) {
        projectService.delete(projectId);
    }

    @PostMapping("/{projectId}/cover")
    public ResponseEntity<String> uploadCover(@PathVariable @Min(value = 1, message = "Project ID must be more than 0") Long projectId,
                                              @RequestParam("cover") @NotNull MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        projectService.uploadFile(multipartFile, projectId, "cover");
        return ResponseEntity.ok("Image uploaded: " + fileName);

    }
}
