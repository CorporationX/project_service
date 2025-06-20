package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectForUpdateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectForCreationDto;
import faang.school.projectservice.dto.project.ProjectOutputDto;
import faang.school.projectservice.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "ProjectController", description = "Main projects controller")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Creating new project", description = "Provides ability to create new project")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public ProjectOutputDto create(@Valid @RequestBody @Parameter(description = "Project to create", required = true) ProjectForCreationDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping
    @Operation(summary = "Updating project", description = "Provides ability to update existing project")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Project doesn't exist"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ProjectOutputDto update(@Valid @RequestBody @Parameter(description = "Updated project", required = true) ProjectForUpdateDto projectDto) {
        return projectService.update(projectDto);
    }

    @GetMapping("/filtered")
    @Operation(summary = "Getting filtered projects", description = "Provides ability to find projects with filters")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public List<ProjectOutputDto> getFilteredProjects(@Valid @RequestBody @Parameter(description = "Project filters", required = true) ProjectFilterDto projectDto) {
        return projectService.getFilteredProjects(projectDto);
    }

    @GetMapping("/all")
    @Operation(summary = "Getting all projects", description = "Provides ability to find all projects")
    public List<ProjectOutputDto> getAllProjects() {
        ProjectFilterDto emptyDto = ProjectFilterDto.builder().build();
        return projectService.getFilteredProjects(emptyDto);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Getting project", description = "Provides ability to find project by id")
    @ApiResponse(responseCode = "404", description = "Project doesn't exist")
    public ProjectOutputDto getProjectById(@PathVariable @Parameter(description = "Project id", required = true) long projectId) {
        return projectService.getProjectById(projectId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{projectId}/cover-image")
    @Operation(summary = "Adding project cover image", description = "Provides ability to add project's cover image")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Project doesn't exist"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ProjectOutputDto uploadCoverImage(@PathVariable @Parameter(description = "Project id", required = true) Long projectId,
                                             @RequestParam("file") @Parameter(description = "Cover image", required = true) MultipartFile file) {
        log.debug("Uploading cover image for project with id {} - Started", projectId);
        ProjectOutputDto projectWithCoverImage = projectService.uploadCoverImage(projectId, file);
        log.info("Uploading cover image for project with id {} - Finished", projectId);
        return projectWithCoverImage;
    }

    @DeleteMapping("/{projectId}/cover-image")
    @Operation(summary = "Deleting project cover image", description = "Provides possibility to delete project's cover image")
    @ApiResponse(responseCode = "404", description = "Project doesn't exist")
    public ProjectOutputDto deleteCoverImage(@PathVariable @Parameter(description = "Project id", required = true) Long projectId) {
        log.debug("Deleting cover image for project with id {} - Started", projectId);
        ProjectOutputDto projectWithoutCoverImage = projectService.deleteCoverImage(projectId);
        log.info("Deleting cover image for project with id {} - Finished", projectId);
        return projectWithoutCoverImage;
    }

    @GetMapping("/{projectId}/cover-image")
    @Operation(summary = "Downloading cover image", description = "Provides possibility to download cover image")
    @ApiResponse(responseCode = "404", description = "Project doesn't exist")
    public ResponseEntity<byte[]> downloadCoverImage(@PathVariable @Parameter(description = "Project id", required = true) Long projectId) {
        log.debug("Getting cover image for project with id {} - Started", projectId);
        byte[] coverImage = projectService.getCoverImage(projectId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        log.info("Getting cover image for project with id {} - Finished", projectId);
        return new ResponseEntity<>(coverImage, headers, HttpStatus.OK);
    }
}
