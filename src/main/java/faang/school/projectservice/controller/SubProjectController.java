package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.MessageError;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
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

    private boolean validateProjectName(String name) {
        return name != null && !name.isEmpty();
    }

}
