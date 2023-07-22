package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto create(ProjectDto projectDto) {
        validateData(projectDto == null, "Project doesn't exist");
        return projectService.create(projectDto);
    }

    public ProjectDto changeStatus(ProjectDto projectDto, Long id) {
        validateData(projectDto == null || id == null || projectDto.getStatus() == null || projectDto.getDescription().isEmpty(),
                "Status or id doesn't exist");
        return projectService.updateStatusAndDescription(projectDto, id);
    }

    public List<Project> getProjectsByName(ProjectFilterDto projectFilterDto) {
        validateData(projectFilterDto == null || projectFilterDto.getName() == null,
                "Name doesn't exist");
        return projectService.getProjectByName(projectFilterDto);
    }

    public List<Project> getProjectsByStatus(ProjectFilterDto projectFilterDto) {
        validateData(projectFilterDto == null || projectFilterDto.getStatus() == null,
                "Status doesn't exist");
        return projectService.getProjectByStatus(projectFilterDto);
    }

    private void validateData(boolean condition, String exception) {
        if (condition) {
            throw new DataValidationException(exception);
        }
    }
}
