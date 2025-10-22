package faang.school.projectservice.controller;

import faang.school.projectservice.controller.facade.project.ProjectFacade;
import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.model.ProjectStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Endpoints for managing projects")
public class ProjectController {

    private final ProjectFacade projectFacade;

    @Operation(
            summary = "Create a new project",
            description = "Creates a new project for the specified user.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Project successfully created",
                            content = @Content(schema = @Schema(implementation = ProjectDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Project with the same name already exists", content = @Content)
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createProject(
            @Parameter(description = "Owner user ID", required = true, example = "123")
            @RequestHeader("x-user-id") Long ownerId,
            @Valid @RequestBody ProjectCreateDto projectCreateDto) {
        log.info("Create project request: {}", ownerId);
        return projectFacade.create(projectCreateDto, ownerId);
    }

    @Operation(
            summary = "Update an existing project",
            description = "Updates project description or status by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project successfully updated",
                            content = @Content(schema = @Schema(implementation = ProjectDto.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid update data", content = @Content)
            }
    )
    @PatchMapping("/{id}")
    public ProjectDto updateProject(
            @Parameter(description = "Project ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        log.info("Update project id={}", id);
        return projectFacade.update(projectUpdateDto, id);
    }

    @Operation(
            summary = "Get projects by filter",
            description = "Retrieves projects filtered by name and/or status. Returns all visible projects for the user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of filtered projects",
                            content = @Content(schema = @Schema(implementation = ProjectDto.class)))
            }
    )
    @GetMapping("/filter")
    public List<ProjectDto> getProjectsByFilter(
            @Parameter(description = "Filter by project name", example = "MyProject")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by project status", example = "CREATED")
            @RequestParam(required = false) ProjectStatus status,
            @Parameter(description = "User ID making the request", required = true, example = "123")
            @RequestHeader("x-user-id") Long userId) {
        log.info("Filter projects request: name='{}', status='{}'", name, status);
        return projectFacade.getByFilter(name, status, userId);
    }

    @Operation(
            summary = "Get project by ID",
            description = "Retrieves a specific project if the user has access to it.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project found",
                            content = @Content(schema = @Schema(implementation = ProjectDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ProjectDto getProjectById(
            @Parameter(description = "Project ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "User ID making the request", required = true, example = "123")
            @RequestHeader("x-user-id") Long userId) {
        log.info("Get project id={} by user {}", id, userId);
        return projectFacade.getById(id, userId);
    }

    @Operation(
            summary = "Get all projects",
            description = "Retrieves a list of all existing projects.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of all projects",
                            content = @Content(schema = @Schema(implementation = ProjectDto.class)))
            }
    )
    @GetMapping
    public List<ProjectDto> getAllProjects() {
        log.info("Get all projects request");
        return projectFacade.getAll();
    }
}