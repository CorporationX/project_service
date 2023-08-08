package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.StatusSubprojectUpdateDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subproject")
public class SubProjectController {
    private final SubProjectService subProjectService;
    private final SubProjectValidator subProjectValidator;

    @PutMapping("/update/status")
    public ProjectDto updateStatusSubProject(@RequestBody StatusSubprojectUpdateDto statusSubprojectUpdateDto) {
        subProjectValidator.validateStatusSubprojectUpdateDto(statusSubprojectUpdateDto);
        return subProjectService.updateStatusSubProject(statusSubprojectUpdateDto);
    }
}
