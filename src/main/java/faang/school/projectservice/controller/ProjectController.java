package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final static String NOT_SAME_PROJECT_ID =
            "Id specified in the address does not match the project id from the request body. " +
                    "Id in the address: %d. Id in the request body: %d";
    private final ProjectService projectService;

    @PostMapping("/project")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PostMapping("/project/{projectId}")
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto,
                                    @PathVariable long projectId) {
        validateProjectIdSameAsInDto(projectId, projectDto.getId());
        return projectService.updateProject(projectDto);
    }

    private void validateProjectIdSameAsInDto(long projectIdFromAddress, long projectIdFromDto) {
        if (projectIdFromAddress != projectIdFromDto) {
            throw new DataValidationException(String.format(NOT_SAME_PROJECT_ID, projectIdFromAddress, projectIdFromDto));
        }
    }
}
