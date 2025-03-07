package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateProjectRequestDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.config.context.user.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@RestController
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @Valid @RequestBody CreateProjectRequestDto projectRequestDto) {
        Long userId = userContext.getUserId();
        Project project = projectMapper.toEntity(projectRequestDto);
        Project createdProject = projectService.createProject(project, userId);
        return ResponseEntity.ok(projectMapper.toDto(createdProject));
    }

    @PostMapping("/subprojects")
    public ResponseEntity<ProjectResponseDto> createSubProject(
            @Valid @RequestBody CreateSubProjectDto subProjectDto,
            @RequestHeader("x-user-id") Long ownerId) {
        Project subProject = projectMapper.toEntity(subProjectDto);
        Project createdSubProject = projectService.createSubProject(subProject, ownerId);
        return ResponseEntity.ok(projectMapper.toDto(createdSubProject));
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateProjectRequestDto projectRequestDto) {
        Project updatedProject = projectMapper.toEntity(projectRequestDto);
        updatedProject.setId(projectId);
        Project result = projectService.updateProject(updatedProject);
        return ResponseEntity.ok(projectMapper.toDto(result));
    }

    @PatchMapping("/subprojects/{subProjectId}")
    public ResponseEntity<ProjectResponseDto> updateSubProject(
            @PathVariable Long subProjectId,
            @Valid @RequestBody CreateSubProjectDto subProjectDto) {
        Project updatedSubProject = projectMapper.toEntity(subProjectDto);
        updatedSubProject.setId(subProjectId);
        Project result = projectService.updateSubProject(updatedSubProject);
        return ResponseEntity.ok(projectMapper.toDto(result));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponseDto>> getProjects(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status,
            Pageable pageable) {
        Long userId = userContext.getUserId();
        Page<Project> projects = projectService.getProjects(name, status, userId, pageable);
        return ResponseEntity.ok(projects.map(projectMapper::toDto));
    }

    @GetMapping("/{parentProjectId}/subprojects")
    public ResponseEntity<Page<ProjectResponseDto>> getSubProjects(
            @PathVariable Long parentProjectId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status,
            Pageable pageable) {
        Page<Project> subProjects = projectService.getSubProjects(parentProjectId, name, status, pageable);
        return ResponseEntity.ok(subProjects.map(projectMapper::toDto));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable Long projectId) {
        Long userId = userContext.getUserId();
        Project project = projectService.getProjectById(projectId);
        return ResponseEntity.ok(projectMapper.toDto(project));
    }

    @PostMapping(value = "/{projectId}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCover(@PathVariable Long projectId,
                                              @RequestParam("file") MultipartFile file) {
        projectService.uploadProjectCover(projectId, file);
        return ResponseEntity.ok("Cover uploaded successfully");
    }

    @DeleteMapping("/{projectId}/cover")
    public ResponseEntity<String> deleteCover(@PathVariable Long projectId) {
        projectService.deleteCover(projectId);
        return ResponseEntity.ok("Cover deleted successfully");
    }
}