package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.service.subproject.ProjectService;
import faang.school.projectservice.validator.ValidatorSubProjectController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class SubProjectController {
    private final ValidatorSubProjectController validatorSubProjectController;
    private final ProjectService projectService;

    public ProjectDto createSubProject(ProjectDto projectDto) {
        validatorSubProjectController.isProjectDtoNull(projectDto);
        validatorSubProjectController.isProjectNameNull(projectDto);
        validatorSubProjectController.isProjectStatusNull(projectDto);
        validatorSubProjectController.isProjectVisibilityNull(projectDto);
        return projectService.create(projectDto);

    }
}
