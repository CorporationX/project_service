package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;
    @PostMapping("/subproject")
    public ProjectDto createSubProject(@RequestBody ProjectDto projectDto) {
        validateSubProject(projectDto);
        return projectService.createSubProject(projectDto);
    }
    private void validateSubProject(ProjectDto projectDto) {
        if (projectDto.getName().isBlank()) {
            throw new DataValidException("Enter project name, please");
        }
    }
}
