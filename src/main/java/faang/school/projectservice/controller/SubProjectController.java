package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.SubProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subprojects")
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createSubProject(@RequestBody ProjectDto projectDto) {
        validateProjectId(projectDto);
        return projectService.createSubProject(projectDto);
    }

    @PutMapping
    public ProjectDto updateSubProject(@RequestBody ProjectDto projectDto) {
        validateProjectId(projectDto);
        return projectService.updateSubProject(projectDto);
    }

    @PutMapping("filters")
    public List<ProjectDto> getFilteredSubProjects(@RequestBody SubProjectFilterDto filtersDto) {
        validateSubProjectFilterId(filtersDto);
        return projectService.getFilteredSubProjects(filtersDto);
    }

    private void validateProjectId(ProjectDto projectDto) {
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new DataValidationException("Enter project name, please");
        }
    }

    private void validateSubProjectFilterId(SubProjectFilterDto filtersDto) {
        if (filtersDto.getProjectId() == null) {
            throw new DataValidationException("Enter project id, please");
        }
    }
}
