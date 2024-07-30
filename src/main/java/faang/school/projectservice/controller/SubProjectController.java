package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.MessageError;
import faang.school.projectservice.service.project.ProjectService;
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
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        if (validateProjectName(projectDto.getName())) {
            return projectService.createProject(projectDto);
        } else {
            throw new DataValidationException(MessageError.NAME_IS_EMPTY);
        }
    }

    @PostMapping("/project/{parentProjectId}")
    public CreateSubProjectDto createSubProject(@PathVariable @Positive @NonNull @Valid Long parentProjectId, @RequestBody CreateSubProjectDto subProjectDto) {
        if (validateProjectName(subProjectDto.getName())) {
            return projectService.createSubProject(parentProjectId, subProjectDto);
        } else {
            throw new DataValidationException(MessageError.NAME_IS_EMPTY);
        }
    }

    @PutMapping("/project/{projectId}")
    public CreateSubProjectDto updateProject(@PathVariable @Positive @Valid long projectId, @RequestBody CreateSubProjectDto dto) {
        long userId = userContext.getUserId();
        return projectService.updateProject(projectId,dto,userId);
    }

    @PostMapping("/project/{projectId}/subprojects")
    public List<CreateSubProjectDto> getProjectsByFilters(@RequestBody FilterDto filterDto, @PathVariable @Positive @NonNull @Valid Long projectId){
        return projectService.getProjectByFilters(filterDto, projectId);
    }

    @GetMapping("/project/{projectId}")
    public ProjectDto getProject(@PathVariable @Positive long projectId){
        return projectService.getProjectById(projectId);
    }

    @PostMapping("/projects")
    public List<ProjectDto> getProjectsByIds(@RequestBody @Positive List<Long> ids){
        return projectService.getProjectsByIds(ids);
    }

    private boolean validateProjectName(String name) {
        return name != null && !name.isEmpty();
    }

}
