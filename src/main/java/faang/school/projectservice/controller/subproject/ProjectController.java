package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.UpdateProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "main-methods")
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@RestController
public class ProjectController {
    private final UpdateProjectMapper updateProjectMapper;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;
    private final ProjectService projectService;

    @Operation(
            summary = "Creates a project",
            description = "Accepts Dto map into an entity and creates a project"

    )
    @PostMapping("/project")
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto dto) {
        log.info("Creating project '{}' by UserId #{}.", dto.getName(), dto.getOwnerId());
        Project project = projectMapper.toEntity(dto);
        ProjectDto createProjectDto = projectMapper.toDto(projectService.createProject(project));
        return ResponseEntity.status(HttpStatus.CREATED).body(createProjectDto);
    }

    @Operation(
            summary = "Updates the project",
            description = "Accepts Dto map into an entity and update a project"
    )
    @PatchMapping("/project")
    public ResponseEntity<ProjectUpdateDto> updateProject(@Valid @RequestBody ProjectUpdateDto dto) {
        log.info("Updating project {} by userId {} .", dto.getName(), dto.getOwnerId());
        Project project = updateProjectMapper.toEntity(dto);
        ProjectUpdateDto updateProject = updateProjectMapper.toDto(projectService.updateProject(project));
        return ResponseEntity.ok(updateProject);
    }

    @PostMapping("/filteredName/{currentUserId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByFilterName(
            @Valid @RequestBody ProjectFilterDto filterDto, @NotNull @PathVariable Long currentUserId) {
        log.info("Getting filtered projects by User #{}.", currentUserId);


        List<Project> projectsByFilterName = projectService.getProjectsByFilterName(filterDto, currentUserId);
        List<ProjectDto> projectDtoList = projectMapper.toDtoList(projectsByFilterName);

        return ResponseEntity.ok(projectDtoList);
    }

    @PostMapping("/filteredStatus/{currentUserId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByFilterStatus(
            @Valid @RequestBody ProjectFilterDto filterDto, @NotNull @PathVariable Long currentUserId) {
        log.info("Getting filtered projects by User #{}.", currentUserId);
        List<Project> projectsByFilterStatus = projectService.getProjectsByFilterStatus(filterDto, currentUserId);
        List<ProjectDto> projectDtoList = projectMapper.toDtoList(projectsByFilterStatus);
        return ResponseEntity.ok(projectDtoList);
    }

    @PostMapping("/allProjects/{currentUserId}")
    public ResponseEntity<List<Project>> getAllUserAvailableProjects(
            @Valid @RequestBody ProjectFilterDto filterDto, @PathVariable @NotNull Long currentUserId) {
        log.info("Getting available projects by User #{}.", currentUserId);
        return ResponseEntity.ok(projectService.getAllUserAvailableProjects(currentUserId));
    }

    @PostMapping("/filtered/{id}")
    public ResponseEntity<Project> findProjectById(
            @Valid @RequestBody ProjectFilterDto filterDto, @PathVariable @NotNull Long id) {
        log.info("Getting project with #{}.", id);
        return ResponseEntity.ok(projectService.findProjectById(id));
    }
}

