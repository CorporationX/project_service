package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;
    protected final ProjectValidator projectValidator;

    public void createSubProject(ProjectDto projectDto) {
        projectValidator.validateParentId(projectDto.getParentId());
        projectService.createSubProject(projectDto);
    }

    public void updateSubProject(long subprojectId) {

    }

    public void getAllSubprojectsByFilter() {

    }
}
