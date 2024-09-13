package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.validator.subproject.ValidatorSubProjectController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subproject")
public class SubProjectController {
    private final ValidatorSubProjectController validatorSubProjectController;
    private final SubProjectService subProjectService;

    @PostMapping("/create-subproject")
    public ProjectDto createSubProject(@RequestBody ProjectDto projectDto) {
        validatorSubProjectController.isProjectDtoNull(projectDto);
        validatorSubProjectController.isProjectNameNull(projectDto);
        validatorSubProjectController.isProjectStatusNull(projectDto);
        validatorSubProjectController.isProjectVisibilityNull(projectDto);
        return subProjectService.create(projectDto);
    }

    @GetMapping("/get-subprojects")
    public List<ProjectDto> getSubProjects(@RequestBody ProjectDto projectDto) {
        validatorSubProjectController.isProjectDtoNull(projectDto);
        return subProjectService.getFilteredSubProjects(projectDto);
    }

    @PostMapping("/update-subproject")
    public ProjectDto updateSubProject(@RequestBody ProjectDto projectDto) {
        validatorSubProjectController.isProjectDtoNull(projectDto);
        return subProjectService.updateSubProject(projectDto);
    }
}
