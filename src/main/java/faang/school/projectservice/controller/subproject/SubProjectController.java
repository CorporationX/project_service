package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.UpdateVisibilitySubprojectDto;
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

    @PutMapping("/update_visibility")
    public void updateStatusSubProject(@RequestBody UpdateVisibilitySubprojectDto updateStatusSubprojectDto) {
        subProjectValidator.validateUpdateVisibilitySubprojectDto(updateStatusSubprojectDto);
        subProjectService.updateVisibilitySubProject(updateStatusSubprojectDto);
    }
}
