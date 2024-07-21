package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.MessageError;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;

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

    @PutMapping("/project/{projectId")
    public CreateSubProjectDto updateProject(@PathVariable @Positive @Valid long projectId, @RequestBody CreateSubProjectDto dto) {
        return projectService.updateProject(projectId,dto);
    }

    private boolean validateProjectName(String name) {
        return name != null && !name.isEmpty();
    }

}
