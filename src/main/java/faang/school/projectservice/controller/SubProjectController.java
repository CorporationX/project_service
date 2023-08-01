package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping("/createSubProject")
    public ProjectDto createSubProject(@RequestBody ProjectDto projectDto) {
        validateSubProject(projectDto);
        return projectService.createSubProject(projectDto);
    }

    @PostMapping("/createSubProjects")
    public List<ProjectDto> createSubProjects(@RequestBody List<ProjectDto> projectsDtos) {
        validateProjectsList(projectsDtos);
        projectsDtos.forEach(this::validateSubProject);
        return projectService.createSubProjects(projectsDtos);
    }

    public Timestamp updateSubProject(ProjectDto projectDto) {
        validateSubProject(projectDto);
        return projectService.updateSubProject(projectDto);
    }

    private void validateSubProject(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new DataValidationException("ProjectDto cannot be null");
        }
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new DataValidationException("Project can't be created with empty name");
        }
        if (projectDto.getParentProjectId() == null) {
            throw new DataValidationException("SubProject must have parentProjectId");
        }
    }

    private void validateProjectsList(List<ProjectDto> projectDtos) {
        if (projectDtos == null) {
            throw new DataValidationException("List of projects is null");
        }
        if (projectDtos.isEmpty()) {
            throw new DataValidationException("List of project is empty");
        }
    }
}