package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subproject")
public class SubProjectController {
    private final SubProjectService subProjectService;

    @PostMapping("/create")
    public ProjectDto createSubProject(@RequestBody ProjectDto projectDto) {
        return subProjectService.createSubProject(projectDto);
    }

    @PutMapping("/status")
    public ProjectDto updateStatusSubProject(@RequestBody StatusSubprojectDto statusSubprojectDto) {
        return subProjectService.updateStatusSubProject(statusSubprojectDto);
    }

    @PutMapping("/visibility")
    public void updateVisibilitySubProject(@RequestBody VisibilitySubprojectDto updateStatusSubprojectDto) {
        subProjectService.updateVisibilitySubProject(updateStatusSubprojectDto);
    }
}
