package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;
    private final SubProjectDtoValidator validator;

    public ProjectDto createSubProject(CreateSubProjectDto subProjectDto) {
        validator.validate(subProjectDto);
        return projectService.createSubProject(subProjectDto);
    }
}
