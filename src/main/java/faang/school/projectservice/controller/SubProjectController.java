package faang.school.projectservice.controller;

import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.service.SubProjectService;
import faang.school.projectservice.validator.ValidatorSubProjectController;
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
        validatorSubProjectController.validateProjectDtoNotNull(projectDto);
        validatorSubProjectController.validateProjectNameNotNull(projectDto);
        validatorSubProjectController.validateProjectStatusNotNull(projectDto);
        validatorSubProjectController.validateProjectVisibilityNotNull(projectDto);
        return subProjectService.create(projectDto);
    }

    @GetMapping("/get-subprojects")
    public List<ProjectDto> getSubProjects(@RequestBody ProjectDto projectDto) {
        validatorSubProjectController.validateProjectDtoNotNull(projectDto);
        return subProjectService.getFilteredSubProjects(projectDto);
    }

    @PostMapping("/update-subproject")
    public ProjectDto updateSubProject(@RequestBody ProjectDto projectDto) {
        validatorSubProjectController.validateProjectDtoNotNull(projectDto);
        return subProjectService.updatingSubProject(projectDto);
    }
}
