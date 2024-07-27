package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.MessageError;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;
    private final UserContext userContext;

    @PostMapping("/project")
    @Operation(summary = "Create a new project in DB")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        if (validateProjectName(projectDto.getName())) {
            return projectService.createProject(projectDto);
        } else {
            throw new DataValidationException(MessageError.NAME_IS_EMPTY);
        }
    }

    @PostMapping("/project/{parentProjectId}")
    @Operation(summary = "Add a new project as subproject to parent project by id")
    public CreateSubProjectDto createSubProject(@PathVariable @Positive @NonNull @Valid Long parentProjectId, @RequestBody CreateSubProjectDto subProjectDto) {
        if (validateProjectName(subProjectDto.getName())) {
            return projectService.createSubProject(parentProjectId, subProjectDto);
        } else {
            throw new DataValidationException(MessageError.NAME_IS_EMPTY);
        }
    }

    @PutMapping("/project/{projectId}")
    @Operation(summary = "Update project by id")
    public CreateSubProjectDto updateProject(@PathVariable @Positive @Valid long projectId, @RequestBody CreateSubProjectDto dto) {
        long userId = userContext.getUserId();
        return projectService.updateProject(projectId,dto,userId);
    }

    @PostMapping("/project/{projectId}/subprojects")
    @Operation(summary = "Show all child project to project with ID sorted by filters")
    public List<CreateSubProjectDto> getProjectsByFilters(@RequestBody FilterDto filterDto, @PathVariable @Positive @NonNull @Valid Long projectId){
        return projectService.getProjectByFilters(filterDto, projectId);
    }

    private boolean validateProjectName(String name) {
        return name != null && !name.isEmpty();
    }

}
