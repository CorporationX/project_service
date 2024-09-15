package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto create(ProjectDto projectDto) {
        if (!isValidProjectDto(projectDto)) {
            throw new DataValidationException("Project name and description must not be empty");
        }
        return projectService.create(projectDto);
    }

    public ProjectDto update(ProjectDto projectDto) {
        if (!isValidProjectDto(projectDto)) {
            throw new DataValidationException("Project description must not be empty");
        }
        return projectService.update(projectDto);
    }

    public List<ProjectDto> getFiltered(ProjectFilterDto filters) {
        return projectService.getFiltered(filters);
    }

    public List<ProjectDto> getAll() {
        return projectService.getAll();
    }

    public ProjectDto getById(Long id) {
        return projectService.getById(id);
    }

    private boolean isValidProjectDto(ProjectDto projectDto) {
        return projectDto != null
                && !projectDto.getName().trim().isBlank()
                && !projectDto.getDescription().trim().isBlank();
    }
}
