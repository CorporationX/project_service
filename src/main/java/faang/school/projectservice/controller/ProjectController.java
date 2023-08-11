package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto create(ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    public ProjectDto changeStatus(ProjectDto projectDto, Long id) {
        if (id == null) {
            throw new DataValidationException("Project id is null");
        }
        return projectService.updateStatusAndDescription(projectDto, id);
    }
}
