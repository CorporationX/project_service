package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectUpdateDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subproject")
public class SubProjectController {
    private final SubProjectService subProjectService;
    private final SubProjectValidator subProjectValidator;

    @PostMapping("/create")
    public SubProjectDto createSubProject(@RequestBody SubProjectDto subProjectDto) {
        subProjectValidator.validateCreateProjectDto(subProjectDto);
        return subProjectService.createProject(subProjectDto);
    }

    @PutMapping("/status")
    public ProjectDto updateStatusSubProject(@RequestBody StatusSubprojectDto statusSubprojectDto) {
        return subProjectService.updateStatusSubProject(statusSubprojectDto);
    }

    @PutMapping("/visibility")
    public void updateVisibilitySubProject(@RequestBody VisibilitySubprojectUpdateDto updateStatusSubprojectDto) {
        subProjectService.updateVisibilitySubProject(updateStatusSubprojectDto);
    }
}
