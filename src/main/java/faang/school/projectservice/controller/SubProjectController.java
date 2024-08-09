package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subProjects")
@RequiredArgsConstructor
@Validated
public class SubProjectController {
    private final ProjectService projectService;
    private final UserContext userContext;

    @PostMapping("/project")
    @Operation(summary = "Create a new project in DB")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PostMapping("/project/{parentProjectId}")
    @Operation(summary = "Add a new project as subproject to parent project by id")
    public CreateSubProjectDto createSubProject(@PathVariable @Positive @NonNull Long parentProjectId, @RequestBody CreateSubProjectDto subProjectDto) {
        return projectService.createSubProject(parentProjectId, subProjectDto);
    }

    @PutMapping("/project/{projectId}")
    @Operation(summary = "Update project by id",
            parameters = @Parameter(
                    name = "x-user-id",
                    in = ParameterIn.HEADER, required = true,
                    description = "User ID")
    )
    public CreateSubProjectDto updateProject(@PathVariable @Positive long projectId, @RequestBody CreateSubProjectDto dto) {
        long userId = userContext.getUserId();
        return projectService.updateProject(projectId, dto, userId);
    }

    @PostMapping("/project/{projectId}/subprojects")
    @Operation(summary = "Show all child project to project with ID sorted by filters")
    public List<CreateSubProjectDto> getProjectsByFilters(@RequestBody FilterProjectDto filterDto, @PathVariable @Positive @NonNull Long projectId) {
        return projectService.getProjectByFilters(filterDto, projectId);
    }

    @GetMapping("/project/{projectId}")
    public ProjectDto getProject(@PathVariable @Positive long projectId) {
        return projectService.getProjectById(projectId);
    }

    @PostMapping("/projects")
    public List<ProjectDto> getProjectsByIds(@RequestBody @Positive List<Long> ids) {
        return projectService.getProjectsByIds(ids);
    }
}
