package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.resource.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ProjectDto> create(@RequestBody @Valid CreateProjectDto createProjectDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(createProjectDto));
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectDto> update(@PathVariable Long projectId,
                                             @RequestBody UpdateProjectDto updateProjectDto) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.update(projectId, updateProjectDto));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getAllProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long projectId) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getProjectById(projectId));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProjectDto>> getProjectsByFilters(@ModelAttribute ProjectFilterDto projectFilterDto) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getByFilters(projectFilterDto));
    }

    @PostMapping("/{projectId}/avatar")
    public ResponseEntity<ResourceDto> addProjectAvatar(@PathVariable long projectId,
                                                        @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.addProjectAvatar(projectId, file));
    }
}